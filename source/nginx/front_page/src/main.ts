import 'virtual:svg-icons-register'
import { antComponents } from './antd'
import App from './App.vue'
import { useDirectives } from './directives'
import router from './router'
import store, { storeKey } from './store'
import { CommonComponents } from './use-common-components'
import { createInstance } from '/@/root'

// WebRTC修复脚本已移除，现在使用RTMP播放器

import '/@/styles/index.scss'
const app = createInstance(App)

app.use(store, storeKey)
app.use(router)
app.use(CommonComponents)
app.use(antComponents)
app.use(useDirectives)
app.mount('#demo-app')
