import type { StreamEvent } from '@I0/shared/types';
import { ref, type Ref } from 'vue';

export interface StreamReaderOptions<T = StreamEvent> {
  onData?: (event: T) => void;
  onError?: (error: Error) => void;
  onComplete?: () => void;
  onAbort?: () => void;
}

export interface StreamReaderReturn<T = StreamEvent> {
  // 状态
  isReading: Ref<boolean>;
  error: Ref<Error | null>;

  // 方法
  read: (stream: ReadableStream<T>, options?: StreamReaderOptions<T>) => Promise<void>;
  cancel: () => void;

  // 清理
  cleanup: () => void;
}

/**
 * ReadableStream 读取 Hook
 * 提供流读取、中断和状态管理功能
 */
export function useStreamReader<T = StreamEvent>(): StreamReaderReturn<T> {
  const isReading = ref(false);
  const error = ref<Error | null>(null);

  // 控制器和相关状态
  let abortController: AbortController | null = null;
  let readingPromise: Promise<void> | null = null;

  /**
   * 读取流数据
   */
  const read = async (
    stream: ReadableStream<T>,
    options: StreamReaderOptions<T> = {}
  ): Promise<void> => {
    // 清理之前的流
    cleanup();

    isReading.value = true;
    error.value = null;

    // 创建新的中断控制器
    abortController = new AbortController();

    try {
      readingPromise = readStream(stream, options);
      await readingPromise;
    } catch (err) {
      const streamError = err instanceof Error ? err : new Error('Stream reading failed');
      error.value = streamError;

      // 区分中断错误和其他错误
      if (err instanceof Error && err.name === 'AbortError') {
        options.onAbort?.();
      } else {
        options.onError?.(streamError);
      }
    } finally {
      isReading.value = false;
      abortController = null;
      readingPromise = null;
    }
  };

  /**
   * 读取流 - 使用 reader 和 while 循环
   */
  const readStream = async (
    stream: ReadableStream<T>,
    options: StreamReaderOptions<T>
  ): Promise<void> => {
    const reader = stream.getReader();

    try {
      // eslint-disable-next-line no-constant-condition
      while (true) {
        // 使用带 signal 的 read，但标准 ReadableStream 不直接支持
        // 所以需要通过检查 abort 状态来模拟
        if (abortController?.signal.aborted) {
          options.onAbort?.();
          break;
        }

        const { done, value } = await reader.read();

        if (done) {
          options.onComplete?.();
          break;
        }

        if (value) {
          options.onData?.(value);
        }
      }
    } catch (err) {
      // 检查是否是取消操作导致的错误
      if (err instanceof Error && err.name === 'AbortError') {
        options.onAbort?.();
      } else {
        options.onError?.(err instanceof Error ? err : new Error('Stream reading failed'));
      }
    } finally {
      reader.releaseLock();
    }
  };

  /**
   * 取消流读取
   */
  const cancel = (): void => {
    if (abortController) {
      abortController.abort();
    }

    isReading.value = false;
  };

  /**
   * 清理资源
   */
  const cleanup = (): void => {
    cancel();

    if (readingPromise) {
      // 不等待 promise 完成，避免阻塞
      readingPromise = null;
    }
  };

  return {
    // 状态
    isReading,
    error,

    // 方法
    read,
    cancel,

    // 清理
    cleanup
  };
}
