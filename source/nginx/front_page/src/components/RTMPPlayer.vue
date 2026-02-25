<template>
  <div class="rtmp-player">
    <video ref="videoElement" autoplay muted style="width: 100%; height: 100%;"></video>
    <div v-if="error" class="error-message">
      {{ error }}
    </div>
    <div v-if="loading" class="loading">
      正在连接RTMP流...
    </div>
  </div>
</template>

<script lang="ts" setup>
import { message } from 'ant-design-vue'
import { onMounted, onUnmounted, ref } from 'vue'
// @ts-ignore
import flvjs from 'flv.js'

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
let flvPlayer: any = null

const playRTMP = async () => {
  if (!videoElement.value) {
    error.value = '视频元素未找到'
    return
  }

  try {
    loading.value = true
    error.value = ''

    // 关闭之前的连接
    if (flvPlayer) {
      flvPlayer.destroy()
      flvPlayer = null
    }

    // 检查浏览器是否支持flv.js
    if (!flvjs.isSupported()) {
      error.value = '浏览器不支持FLV播放'
      loading.value = false
      message.error('浏览器不支持FLV播放')
      return
    }

    // 将RTMP URL转换为HTTP-FLV URL
    const httpFlvUrl = convertRtmpToHttpFlv(props.streamUrl)
    console.log('使用HTTP-FLV URL:', httpFlvUrl)

    // 创建flv.js播放器
    flvPlayer = flvjs.createPlayer({
      type: 'flv',
      url: httpFlvUrl,
      isLive: true,
      hasAudio: false, // 通常监控视频没有音频
      hasVideo: true
    })

    // 绑定视频元素
    flvPlayer.attachMediaElement(videoElement.value)
    flvPlayer.load()

    // 监听事件
    flvPlayer.on(flvjs.Events.LOADING_COMPLETE, () => {
      console.log('RTMP流加载完成')
      loading.value = false
      message.success('RTMP流播放成功')
    })

    flvPlayer.on(flvjs.Events.ERROR, (errorType: string, errorDetail: any) => {
      console.error('RTMP播放错误:', errorType, errorDetail)
      error.value = `RTMP播放错误: ${errorType}`
      loading.value = false
      message.error('RTMP流播放失败')
    })

    flvPlayer.on(flvjs.Events.STATISTICS_INFO, (stats: any) => {
      console.log('RTMP播放统计:', stats)
    })

  } catch (err: any) {
    console.error('RTMP播放错误:', err)
    error.value = `播放错误: ${err.message || err}`
    loading.value = false
    message.error('RTMP播放出错')
  }
}

const convertRtmpToHttpFlv = (rtmpUrl: string): string => {
  // 将 rtmp://124.71.163.191:1935/rtp/stream 转换为 http://124.71.163.191:8088/rtp/stream.live.flv
  return rtmpUrl
    .replace('rtmp://', 'http://')
    .replace(':1935/', ':8088/')
    .replace('/rtp/', '/rtp/')
    + '.live.flv'
}

const stopRTMP = () => {
  if (flvPlayer) {
    flvPlayer.destroy()
    flvPlayer = null
  }
  if (videoElement.value) {
    videoElement.value.srcObject = null
  }
  loading.value = false
  error.value = ''
}

onMounted(() => {
  if (props.autoPlay) {
    playRTMP()
  }
})

onUnmounted(() => {
  stopRTMP()
})

// 暴露方法给父组件
defineExpose({
  play: playRTMP,
  stop: stopRTMP
})
</script>

<style lang="scss" scoped>
.rtmp-player {
  position: relative;
  width: 100%;
  height: 100%;
  background: #000;

  .error-message {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    color: #ff4d4f;
    background: rgba(0, 0, 0, 0.8);
    padding: 10px;
    border-radius: 4px;
    text-align: center;
    max-width: 80%;
  }

  .loading {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    color: #fff;
    background: rgba(0, 0, 0, 0.8);
    padding: 10px;
    border-radius: 4px;
  }
}
</style> 