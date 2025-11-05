import { createI18n } from "vue-i18n"
import zhCN from "./locales/zh-CN.json"
import enUS from "./locales/en-US.json"

const messages = {
  "zh-CN": zhCN,
  "en-US": enUS
}

const i18n = createI18n({
  legacy: false,
  locale: localStorage.getItem("language") || "zh-CN",
  fallbackLocale: "zh-CN",
  messages
})

export default i18n