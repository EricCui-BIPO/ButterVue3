# 🤖 BigModel Chat Function Calling 系统开发设计文档（V3.0，Vue版本）

**版本号**：v3.0  
**最后更新**：2025-10-17  
**作者**：Sky Wang  
**目标系统**：BigModel + Java 后端 + Vue 前端 + 流式通信（SSE）  

---

## 1️⃣ 系统目标

### 🎯 核心目标
构建一个 **基于自然语言对话** 的智能系统，  
通过调用 **BigModel（如 ChatGLM / 智谱AI）API** 来自动执行企业内部业务操作（如创建员工）。

系统要求：
- 支持 **流式生成（SSE）**；
- 支持 **函数调用（Function Calling）**；
- 后端使用 **Java（Spring Boot）**；
- 前端使用 **Vue 3 + Vite + TailwindCSS**；
- 可扩展更多业务功能。

---

## 2️⃣ 系统总体架构

```
┌────────────────────────────────────────────┐
│                 前端应用（Vue）             │
│  - 聊天界面（Chat UI）                     │
│  - 事件流接收（EventSource / SSE）         │
└────────────────────────────────────────────┘
                    │  HTTP(S)
                    ▼
┌────────────────────────────────────────────┐
│          Java 后端（Spring Boot）          │
│  - ChatController（SSE输出）                │
│  - BigModelStreamService（流式调用模型）   │
│  - FunctionRouter（业务调度）               │
│  - EmployeeService（标准业务API调用）      │
└────────────────────────────────────────────┘
                    │
                    ▼
┌────────────────────────────────────────────┐
│               BigModel API                 │
│  - Function Calling + Stream 模式           │
└────────────────────────────────────────────┘
                    │
                    ▼
┌────────────────────────────────────────────┐
│          企业业务系统（标准API）           │
│  - /api/employees                         │
│  - /api/departments                       │
└────────────────────────────────────────────┘
```

---

## 3️⃣ 核心功能设计

| 模块 | 职责 |
|------|------|
| ChatController | 接收前端消息，建立 SSE 通道 |
| BigModelStreamService | 向 BigModel API 发起流式请求 |
| FunctionRouter | 根据模型返回的 function_call 路由到对应服务 |
| EmployeeService | 调用内部 `/api/employees` 接口创建员工 |
| Vue Chat UI | 展示对话、发送消息、实时渲染模型输出 |

---

## 4️⃣ 通信协议（SSE 流式）

- 使用 **Server-Sent Events (SSE)** 协议；
- 单向实时推流（服务端 → 前端）；
- 前端通过 `EventSource` 接收事件流；
- 支持自动重连与断线恢复。

数据示例：

```
data: {"choices":[{"delta":{"content":"好的"}}]}
data: {"choices":[{"delta":{"content":"，正在创建员工"}}]}
data: {"choices":[{"delta":{"content":"李雷"}}]}
data: [DONE]
```

---

## 5️⃣ BigModel Function Schema 示例

```json
{
  "name": "create_employee",
  "description": "创建一个新的员工记录",
  "parameters": {
    "type": "object",
    "properties": {
      "name": {"type": "string", "description": "员工姓名"},
      "title": {"type": "string", "description": "职位"},
      "department": {"type": "string", "description": "部门"}
    },
    "required": ["name", "title"]
  }
}
```

---

## 6️⃣ Java 后端设计

### ✅ ChatController（流式通信入口）

```java
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final BigModelStreamService bigModelService;

    public ChatController(BigModelStreamService bigModelService) {
        this.bigModelService = bigModelService;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestParam String message) {
        SseEmitter emitter = new SseEmitter(0L);
        bigModelService.streamResponse(message, emitter);
        return emitter;
    }
}
```

---

### ✅ BigModelStreamService（流式请求 BigModel）

```java
@Service
public class BigModelStreamService {
    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private static final String API_KEY = "c6000b5eb2b24c7abfa27c3053197b2e.O69l79m7xtlBDGUg";
    private final OkHttpClient client = new OkHttpClient();

    public void streamResponse(String message, SseEmitter emitter) {
        String body = String.format(
            "{\n" +
            "  \"model\": \"glm-4\",\n" +
            "  \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}],\n" +
            "  \"functions\": [{\"name\": \"create_employee\",\"description\": \"创建员工\",\"parameters\": {\"type\": \"object\",\"properties\": {\"name\": {\"type\": \"string\"},\"title\": {\"type\": \"string\"},\"department\": {\"type\": \"string\"}},\"required\": [\"name\",\"title\"]}}],\n" +
            "  \"stream\": true\n" +
            "}", message);

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(RequestBody.create(body, MediaType.get("application/json")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                try { emitter.send("出错：" + e.getMessage()); } catch (IOException ignored) {}
                emitter.completeWithError(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try (var source = response.body().source()) {
                    while (!source.exhausted()) {
                        String line = source.readUtf8Line();
                        if (line != null && line.startsWith("data: ")) {
                            emitter.send(line.substring(6));
                        }
                    }
                    emitter.send("[DONE]");
                } catch (Exception e) {
                    emitter.completeWithError(e);
                } finally {
                    emitter.complete();
                }
            }
        });
    }
}
```

---

## 7️⃣ Vue 前端设计

### 🧰 技术栈

| 功能 | 技术 |
|------|------|
| 框架 | Vue 3（Composition API） |
| 构建工具 | Vite |
| UI | TailwindCSS |
| 通信 | EventSource（SSE） |
| 状态管理 | Pinia / 组件内部状态 |

---

### 💬 ChatWindow.vue 示例

```vue
<template>
  <div class="flex flex-col h-screen bg-gray-50">
    <div class="flex-1 overflow-y-auto p-4">
      <div v-for="(msg, i) in messages" :key="i" :class="msg.role === 'user' ? 'text-right' : 'text-left'">
        <div :class="msg.role === 'user' ? 'bg-blue-500 text-white' : 'bg-gray-200'"
             class="inline-block px-4 py-2 rounded-2xl my-1 max-w-[80%]">
          {{ msg.content }}
        </div>
      </div>
    </div>

    <div class="p-4 border-t flex">
      <input
        v-model="input"
        @keyup.enter="sendMessage"
        class="flex-1 border rounded-lg p-2"
        placeholder="请输入内容..."
      />
      <button @click="sendMessage" class="ml-2 bg-blue-600 text-white px-4 py-2 rounded-lg">
        发送
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

const messages = ref([]);
const input = ref("");

const sendMessage = () => {
  if (!input.value.trim()) return;

  messages.value.push({ role: "user", content: input.value });

  const eventSource = new EventSource(`/chat/stream?message=${encodeURIComponent(input.value)}`);
  input.value = "";
  let currentMsg = "";

  eventSource.onmessage = (e) => {
    if (e.data === "[DONE]") {
      messages.value.push({ role: "assistant", content: currentMsg });
      eventSource.close();
    } else {
      try {
        const delta = JSON.parse(e.data);
        const token = delta?.choices?.[0]?.delta?.content || "";
        currentMsg += token;
      } catch {
        currentMsg += e.data;
      }
    }
  };

  eventSource.onerror = () => {
    messages.value.push({ role: "assistant", content: "⚠️ 网络连接中断" });
    eventSource.close();
  };
};
</script>
```

---

## ✅ 总结

本系统具备以下特征：
- 对话式操作（Chat 模式）  
- 支持 Function Calling 调用企业 API  
- 实时流式响应（SSE）  
- Vue + Java 双端架构，前后端分离  
- 高度可扩展，可直接用于 AI 业务助手系统  
