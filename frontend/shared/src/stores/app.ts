import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    loading: false,
    error: null as string | null,
    theme: 'light'
  }),
  getters: {
    isLoading: (state) => state.loading,
    hasError: (state) => state.error !== null
  },
  actions: {
    setLoading(loading: boolean) {
      this.loading = loading
    },
    setError(error: string | null) {
      this.error = error
    },
    setTheme(theme: 'light' | 'dark') {
      this.theme = theme
    },
    clearError() {
      this.error = null
    },
    initializeApp() {
      // Initialize app state
      this.loading = false
      this.error = null
      this.theme = localStorage.getItem('theme') as 'light' | 'dark' || 'light'
    }
  }
})