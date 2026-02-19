<template>
  <div class="webrtc-player">
    <video ref="videoElement" autoplay muted style="width: 100%; height: 100%;"></video>
    <div v-if="error" class="error-message">
      {{ error }}
    </div>
    <div v-if="loading" class="loading">
      正在连接WebRTC流...
    </div>
  </div>
</template>

<script lang="ts" setup>
import { message } from 'ant-design-vue'
import { onMounted, onUnmounted, ref } from 'vue'

// 声明全局的ZLMRTCClient
declare global {
  interface Window {
    ZLMRTCClient: any
  }
}

interface Props {
  streamUrl: string
  autoPlay?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  autoPlay: true
})

const videoElement = ref<HTMLVideoElement>()
const error = ref<string>('')
const loading = ref<boolean>(false)
let webrtc: any = null

// 将webrtc://URL转换为正确的WebRTC API端点
const convertWebRTCUrl = (url: string): string => {
  if (url.startsWith('webrtc://')) {
    try {
      // 解析webrtc://URL格式: webrtc://host/live/stream
      const urlObj = new URL(url.replace('webrtc://', 'http://'))
      const pathParts = urlObj.pathname.split('/').filter(Boolean)
      
      let app = 'live'  // 默认为live
      let stream = 'stream'  // 默认为stream
      
      if (pathParts.length >= 1) {
        app = pathParts[0]
      }
      if (pathParts.length >= 2) {
        stream = pathParts[1]
      }
      
      // 构建正确的ZLM WebRTC API URL (WVP格式)
      // 根据WVP配置，使用ZLM的/index/api/webrtc端点
      const webrtcUrl = `http://${urlObj.hostname}:8088/index/api/webrtc?app=${app}&stream=${stream}&type=play`
      console.log('转换后的ZLM WebRTC URL:', webrtcUrl)
      return webrtcUrl
    } catch (err) {
      console.error('URL转换失败:', err)
      return url
    }
  }
  return url
}

const playWebRTC = async () => {
  if (!videoElement.value) {
    error.value = '视频元素未找到'
    return
  }

  // 检查ZLMRTCClient是否可用
  if (!window.ZLMRTCClient) {
    error.value = 'ZLMRTCClient 未加载，请确保已引入 ZLMRTCClient.js'
    message.error('ZLMRTCClient 未加载')
    return
  }

  try {
    loading.value = true
    error.value = ''

    // 关闭之前的连接
    if (webrtc) {
      webrtc.close()
    }

    // 转换URL格式
    const convertedUrl = convertWebRTCUrl(props.streamUrl)
    console.log('原始URL:', props.streamUrl)
    console.log('转换后URL:', convertedUrl)

    // 创建ZLM WebRTC连接
    webrtc = new window.ZLMRTCClient.Endpoint({
      element: videoElement.value,
      debug: true,
      zlmsdpUrl: convertedUrl,
      simulcast: false,
      useCamera: false,
      audioEnable: true,
      videoEnable: true,
      recvOnly: true,
      usedatachannel: false
    })

    // 监听事件
    webrtc.on(window.ZLMRTCClient.Events.WEBRTC_ICE_CANDIDATE_ERROR, (e: any) => {
      console.error('ICE 协商出错:', e)
      error.value = 'ICE 协商出错'
      loading.value = false
      message.error('WebRTC ICE 协商失败')
    })

    webrtc.on(window.ZLMRTCClient.Events.WEBRTC_ON_REMOTE_STREAMS, (e: any) => {
      console.log('WebRTC连接成功:', e)
      loading.value = false
      message.success('WebRTC流播放成功')
    })

    webrtc.on(window.ZLMRTCClient.Events.WEBRTC_OFFER_ANWSER_EXCHANGE_FAILED, (e: any) => {
      console.error('Offer/Answer 交换失败:', e)
      error.value = `Offer/Answer 交换失败: ${e.msg || e}`
      loading.value = false
      message.error('WebRTC 连接失败')
    })

    webrtc.on(window.ZLMRTCClient.Events.WEBRTC_ON_LOCAL_STREAM, (s: any) => {
      console.log('获取到本地流:', s)
    })

  } catch (err: any) {
    console.error('WebRTC播放错误:', err)
    error.value = `播放错误: ${err.message || err}`
    loading.value = false
    message.error('WebRTC播放出错')
  }
}

const stopWebRTC = () => {
  if (webrtc) {
    webrtc.close()
    webrtc = null
  }
  if (videoElement.value) {
    videoElement.value.srcObject = null
  }
  error.value = ''
  loading.value = false
}

onMounted(() => {
  if (props.autoPlay) {
    playWebRTC()
  }
})

onUnmounted(() => {
  stopWebRTC()
})

// 暴露方法给父组件
defineExpose({
  play: playWebRTC,
  stop: stopWebRTC
})
</script>

<style scoped>
.webrtc-player {
  position: relative;
  width: 100%;
  height: 100%;
}

.error-message {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(255, 0, 0, 0.8);
  color: white;
  padding: 10px;
  border-radius: 4px;
  z-index: 10;
}

.loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 10px;
  border-radius: 4px;
  z-index: 10;
}
</style> 