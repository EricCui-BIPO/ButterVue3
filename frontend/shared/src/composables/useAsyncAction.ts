import { Ref, ref } from 'vue'

/**
 * 克隆函数类型，返回 Promise<void>
 */
type FnClone<T> = T extends (...args: infer P) => any
  ? (...args: P) => Promise<void>
  : T

/**
 * useAsyncAction 配置选项
 */
interface UseAsyncActionOptions {
  /** 成功回调函数（可选） */
  onSuccess?: () => void
  /** 错误回调函数（可选，传入 null 则不处理错误） */
  onError?: null | ((error: any) => void)
}

/**
 * 异步操作 Composable
 * 
 * 用于处理异步操作的加载状态、成功和错误回调
 * 
 * @param fn - 要执行的异步函数
 * @param options - 配置选项
 * @param options.onSuccess - 成功回调函数（可选）
 * @param options.onError - 错误回调函数（可选）
 * @returns [action, loading] - 返回包装后的异步函数和加载状态
 * 
 * @example
 * ```typescript
 * const [deleteAction, isDeleting] = useAsyncAction(async (id: string) => {
 *     await api.deleteItem(id)
 *     ElMessage.success('删除成功')
 * })
 * 
 * // 在模板中使用
 * <el-button :loading="isDeleting" @click="deleteAction(id)">
 *   删除
 * </el-button>
 * ```
 */
export function useAsyncAction<T extends Function>(
  fn: T,
  options?: UseAsyncActionOptions
): [FnClone<T>, Ref<boolean>] {
  const loading = ref(false)

  const action: any = async (...args: any[]) => {
    // 防止重复执行
    if (loading.value) return

    try {
      loading.value = true
      console.log('🔄 useAsyncAction 开始执行:', fn.name || 'anonymous function')
      await fn(...args)
      console.log('✅ useAsyncAction 执行成功')
      options?.onSuccess && options.onSuccess()
    } catch (error) {
      console.group('❌ useAsyncAction 捕获到错误')
      console.log('错误详情:', error)
      console.log('错误类型:', error?.type)
      console.log('错误消息:', error?.details || error?.message)
      console.groupEnd()

      // 如果没有提供自定义错误处理器，默认重新抛出错误
      if (options?.onError) {
        options.onError(error)
      } else {
        console.log('🚫 useAsyncAction 重新抛出错误（没有自定义错误处理器）')
        throw error
      }
    } finally {
      loading.value = false
    }
  }

  return [action, loading]
}