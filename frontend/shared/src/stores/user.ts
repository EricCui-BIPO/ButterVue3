import { defineStore } from 'pinia'

export interface UserInfo {
  id: string
  username: string
  avatar?: string
}

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null as UserInfo | null,
    loading: false
  }),
  
  getters: {
    displayName: (state) => state.userInfo?.username,
    avatarUrl: (state) => state.userInfo?.avatar,
    // 生成头像占位符文字（用户名首字母）
    avatarPlaceholder: (state) => {
      if (!state.userInfo?.username) return ''
      return state.userInfo.username.charAt(0).toUpperCase()
    }
  },
  
  actions: {
    setUserInfo(userInfo: UserInfo) {
      this.userInfo = userInfo
    },
    
    setLoading(loading: boolean) {
      this.loading = loading
    },
    
    // 模拟获取用户信息（实际项目中应该从API获取）
    async fetchUserInfo() {
      this.setLoading(true)
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 500))
        
        // 模拟用户数据
        const mockUser: UserInfo = {
          id: '1',
          username: 'Admin User',
          avatar: '' // 暂时为空，使用占位符
        }
        
        this.setUserInfo(mockUser)
      } catch (error) {
        console.error('Failed to fetch user info:', error)
      } finally {
        this.setLoading(false)
      }
    }
  }
})