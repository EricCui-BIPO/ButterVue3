/**
 * 国旗工具类
 * 提供获取国旗图片URL的功能
 */

/**
 * 获取国旗图片URL
 * @param countryCode - 国家代码 (如: 'US', 'CN', 'GB')
 * @returns 国旗图片URL
 */
export function getFlagUrl(countryCode: string): string {
  if (!countryCode) return ''
  return `https://flagcdn.com/w160/${countryCode.toLowerCase()}.png`
}

// 处理国旗图片加载错误
export const handleFlagError = (event: Event) => {
  const img = event.target as HTMLImageElement;
  img.style.display = 'none';
};