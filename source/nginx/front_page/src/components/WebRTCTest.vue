<template>
  <div class="webrtc-test">
    <h3>WebRTC 测试</h3>
    
    <div class="input-group">
      <label>WebRTC URL:</label>
      <input 
        v-model="testUrl" 
        placeholder="webrtc://124.71.163.191/live/41010500002000000761@34020000001320000089"
        style="width: 400px; margin: 10px 0;"
      />
    </div>
    
    <div class="button-group">
      <button @click="testUrlConversion">测试URL转换</button>
      <button @click="testPlay">测试播放</button>
      <button @click="stopPlay">停止播放</button>
    </div>
    
    <div v-if="convertedUrl" class="result">
      <strong>转换后的URL:</strong> {{ convertedUrl }}
    </div>
    
    <div v-if="error" class="error">
      <strong>错误:</strong> {{ error }}
    </div>
    
    <div class="video-container">
      <video ref="videoElement" autoplay muted style="width: 100%; max-width: 640px; height: 360px; background: #000;"></video>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { message } from 'ant-design-vue'
import { ref } from 'vue'

// 声明全局的ZLMRTCClient
declare global {
  interface Window {
    ZLMRTCClient: any
  }
}

const testUrl = ref('webrtc://124.71.163.191/live/41010500002000000761@34020000001320000089')
const convertedUrl = ref('')
const error = ref('')
const videoElement = ref<HTMLVideoElement>()
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
      const webrtcUrl = `http://${urlObj.hostname}:8088/index/api/webrtc?app=${app}&stream=${stream}&type=play`
      return webrtcUrl
    } catch (err) {
      console.error('URL转换失败:', err)
      throw new Error('URL格式错误')
    }
  }
  return url
}

const testUrlConversion = () => {
  try {
    error.value = ''
    convertedUrl.value = convertWebRTCUrl(testUrl.value)
    message.success('URL转换成功')
  } catch (err: any) {
    error.value = err.message
    message.error('URL转换失败: ' + err.message)
  }
}

const testPlay = async () => {
  if (!videoElement.value) {
    message.error('视频元素未找到')
    return
  }

  // 检查ZLMRTCClient是否可用
  if (!window.ZLMRTCClient) {
    message.error('ZLMRTCClient 未加载，请确保已引入 ZLMRTCClient.js')
    return
  }

  try {
    error.value = ''
    
    // 停止之前的播放
    if (webrtc) {
      webrtc.close()
    }

    // 转换URL
    const url = convertWebRTCUrl(testUrl.value)
    convertedUrl.value = url
    
    console.log('开始播放WebRTC流:', url)

    // 创建ZLM WebRTC连接
    webrtc = new window.ZLMRTCClient.Endpoint({
      element: videoElement.value,
      debug: true,
      zlmsdpUrl: url,
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
      message.error('WebRTC ICE 协商失败')
    })

    webrtc.on(window.ZLMRTCClient.Events.WEBRTC_ON_REMOTE_STREAMS, (e: any) => {
      console.log('WebRTC连接成功:', e)
      message.success('WebRTC流播放成功')
    })

    webrtc.on(window.ZLMRTCClient.Events.WEBRTC_OFFER_ANWSER_EXCHANGE_FAILED, (e: any) => {
      console.error('Offer/Answer 交换失败:', e)
      error.value = `Offer/Answer 交换失败: ${e.msg || e}`
      message.error('WebRTC 连接失败')
    })

    webrtc.on(window.ZLMRTCClient.Events.WEBRTC_ON_LOCAL_STREAM, (s: any) => {
      console.log('获取到本地流:', s)
    })
    
  } catch (err: any) {
    console.error('WebRTC播放错误:', err)
    error.value = err.message || '播放失败'
    message.error('WebRTC播放失败: ' + error.value)
  }
}

const stopPlay = () => {
  if (webrtc) {
    webrtc.close()
    webrtc = null
  }
  if (videoElement.value) {
    videoElement.value.srcObject = null
  }
  message.info('播放已停止')
}
</script>

<style scoped>
.webrtc-test {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.input-group {
  margin: 20px 0;
}

.button-group {
  margin: 20px 0;
}

.button-group button {
  margin-right: 10px;
  padding: 8px 16px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.button-group button:hover {
  background: #40a9ff;
}

.result {
  margin: 20px 0;
  padding: 10px;
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 4px;
}

.error {
  margin: 20px 0;
  padding: 10px;
  background: #fff2f0;
  border: 1px solid #ffccc7;
  border-radius: 4px;
  color: #ff4d4f;
}

.video-container {
  margin: 20px 0;
  text-align: center;
}
</style> 