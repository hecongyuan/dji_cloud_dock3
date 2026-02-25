<template>
  <div class="flex-column flex-justify-start flex-align-center">
    <video
      :style="{ width: '720px', height: '480px' }"
      id="video-webrtc"
      ref="videowebrtc"
      controls
      autoplay
      class="mt20"
    ></video>
    <p class="fz24">Live streaming source selection</p>

    <div class="flex-row flex-justify-center flex-align-center mt10">
      <template v-if="liveState && isDockLive">
        <span class="mr10">Lens:</span>
        <a-radio-group v-model:value="lensSelected" button-style="solid">
          <a-radio-button v-for="lens in lensList" :key="lens" :value="lens">{{lens}}</a-radio-button>
        </a-radio-group>
      </template>
      <template v-else>
      <a-select
        style="width: 150px"
        placeholder="Select Live Type"
        @select="onLiveTypeSelect"
        v-model:value="livetypeSelected"
      >
        <a-select-option
          v-for="item in liveTypeList"
          :key="item.label"
          :value="item.value"
        >
          {{ item.label }}
        </a-select-option>
      </a-select>
      <a-select
        class="ml10"
        style="width:150px"
        placeholder="Select Drone"
        v-model:value="droneSelected"
      >
        <a-select-option
          v-for="item in droneList"
          :key="item.value"
          :value="item.value"
          @click="onDroneSelect(item)"
          >{{ item.label }}</a-select-option
        >
      </a-select>
      <a-select
        class="ml10"
        style="width:150px"
        placeholder="Select Camera"
        v-model:value="cameraSelected"
      >
        <a-select-option
          v-for="item in cameraList"
          :key="item.value"
          :value="item.value"
          @click="onCameraSelect(item)"
          >{{ item.label }}</a-select-option
        >
      </a-select>
      <!-- <a-select
        class="ml10"
        style="width:150px"
        placeholder="Select Lens"
        v-model:value="videoSelected"
      >
        <a-select-option
          v-for="item in videoList"
          :key="item.value"
          :value="item.value"
          @click="onVideoSelect(item)"
          >{{ item.label }}</a-select-option
        >
      </a-select> -->
      </template>
      <a-select
        class="ml10"
        style="width:150px"
        placeholder="Select Clarity"
        @select="onClaritySelect"
        v-model:value="claritySelected"
      >
        <a-select-option
          v-for="item in clarityList"
          :key="item.value"
          :value="item.value"
          >{{ item.label }}</a-select-option
        >
      </a-select>
    </div>
    <div class="mt20">
      <p class="fz10" v-if="livetypeSelected == 2">
        Please use VLC media player to play the RTSP livestream !!!
      </p>
      <p class="fz10" v-if="livetypeSelected == 2">
        RTSP Parameter:{{ rtspData }}
      </p>
    </div>
    <div class="mt10 flex-row flex-justify-center flex-align-center">
      <a-button v-if="liveState && isDockLive" type="primary" large @click="onSwitch">Switch Lens</a-button>
      <a-button v-else type="primary" large @click="onStart">Play</a-button>
      <a-button class="ml20" type="primary" large @click="onStop"
        >Stop</a-button
      >
      <a-button class="ml20" type="primary" large @click="onUpdateQuality"
        >Update Clarity</a-button
      >
      <a-button v-if="!liveState || !isDockLive" class="ml20" type="primary" large @click="onRefresh"
        >Refresh Live Capacity</a-button
      >
    </div>
  </div>
</template>

<script lang="ts" setup>
import { message } from 'ant-design-vue'
import flvjs from 'flv.js'
import { onMounted, ref, onUnmounted } from 'vue'
import { CURRENT_CONFIG as config } from '/@/api/http/config'
import { getLiveCapacity, setLivestreamQuality, startLivestream, stopLivestream } from '/@/api/manage'
import EventBus from '/@/event-bus'
// @ts-ignore

// 声明全局的ZLMRTCClient
declare global {
  interface Window {
    ZLMRTCClient: any
  }
}

interface SelectOption {
  value: any,
  label: string,
  more?: any
}

const liveTypeList: SelectOption[] = [
  {
    value: 1,
    label: 'RTMP'
  },
  {
    value: 2,
    label: 'RTSP'
  },
  {
    value: 3,
    label: 'GB28181'
  },
  {
    value: 4,
    label: 'WEBRTC'
  }
]
const clarityList: SelectOption[] = [
  {
    value: 0,
    label: 'Adaptive'
  },
  {
    value: 1,
    label: 'Smooth'
  },
  {
    value: 2,
    label: 'Standard'
  },
  {
    value: 3,
    label: 'HD'
  },
  {
    value: 4,
    label: 'Super Clear'
  }
]

const videowebrtc = ref(null)
const livestreamSource = ref()
const droneList = ref()
const cameraList = ref()
const videoList = ref()
const droneSelected = ref()
const cameraSelected = ref()
const videoSelected = ref()
const claritySelected = ref()
const videoId = ref()
const liveState = ref<boolean>(false)
const livetypeSelected = ref()
const rtspData = ref()
const lensList = ref<string[]>([])
const lensSelected = ref<String>()
const isDockLive = ref(false)
const nonSwitchable = 'normal'
let webrtc: any = null
let flvPlayer: any = null

const onRefresh = async () => {
  droneList.value = []
  cameraList.value = []
  videoList.value = []
  droneSelected.value = null
  cameraSelected.value = null
  videoSelected.value = null
  await getLiveCapacity({})
    .then(res => {
      console.log(res)
      if (res.code === 0) {
        if (res.data === null) {
          console.warn('warning: get live capacity is null!!!')
          return
        }
        const resData: Array<[]> = res.data
        console.log('live_capacity:', resData)
        livestreamSource.value = resData

        const temp: Array<SelectOption> = []
        if (livestreamSource.value) {
          livestreamSource.value.forEach((ele: any) => {
            // 为M4TD设备添加特殊标识
            const deviceLabel = ele.name + '-' + ele.sn + (ele.sn.startsWith('1581F') ? ' (M4TD)' : '')
            temp.push({ label: deviceLabel, value: ele.sn, more: ele.cameras_list })
          })
          droneList.value = temp
        }
      }
    })
    .catch(error => {
      message.error(error)
      console.error(error)
    })
}

// 处理机场直播状态更新
function handleDockLiveStatus (payload: any) {
  if (!payload || !payload.data) {
    return
  }

  console.log('直播组件收到机场直播状态更新:', payload.data)

  // 如果当前正在直播，可能需要根据状态更新UI或重新获取直播能力
  if (liveState.value) {
    // 根据payload.data中的信息更新直播状态
    // 例如，如果机场直播状态发生变化，可能需要刷新直播源列表
    onRefresh()
  }
}

onMounted(() => {
  onRefresh()

  // 监听机场直播状态更新事件
  EventBus.on('dockLiveStatus', handleDockLiveStatus)
})

onUnmounted(() => {
  // 组件卸载时移除事件监听
  EventBus.off('dockLiveStatus', handleDockLiveStatus)
})
const onStart = async () => {
  console.log(
    'Param:',
    livetypeSelected.value,
    droneSelected.value,
    cameraSelected.value,
    videoSelected.value,
    claritySelected.value
  )
  const timestamp = new Date().getTime().toString()
  if (
    livetypeSelected.value == null ||
    droneSelected.value == null ||
    cameraSelected.value == null ||
    claritySelected.value == null
  ) {
    message.warn('waring: not select live para!!!')
    return
  }
  videoId.value =
    droneSelected.value + '/' + cameraSelected.value + '/' + (videoSelected.value || nonSwitchable + '-0')

  let liveURL = ''
  switch (livetypeSelected.value) {
    case 1: {
      // RTMP
      liveURL = config.rtmpURL + timestamp
      break
    }
    case 2: {
      // RTSP
      liveURL = `userName=${config.rtspUserName}&password=${config.rtspPassword}&port=${config.rtspPort}`
      break
    }
    case 3: {
      liveURL = `serverIP=${config.gbServerIp}&serverPort=${config.gbServerPort}&serverID=${config.gbServerId}&agentID=${config.gbAgentId}&agentPassword=${config.gbPassword}&localPort=${config.gbAgentPort}&channel=${config.gbAgentChannel}`
      break
    }
    case 4: {
      break
    }
    default:
      console.warn('warning: live type is not correct!!!')
      break
  }
  await startLivestream({
    url: liveURL,
    video_id: videoId.value,
    url_type: livetypeSelected.value,
    video_quality: claritySelected.value
  })
    .then(res => {
      if (res.code !== 0) {
        return
      }
      if (livetypeSelected.value === 3) {
        console.log('gb28181 url:88888888888888', res.data.url)
        const url = res.data.url
        const videoElement = videowebrtc.value

        // 检查是否是webrtc://协议
        if (url.startsWith('webrtc://')) {
          // 使用WebRTC播放器播放
          console.log('使用WebRTC播放器播放GB28181流:', url)
          playWebrtc(videoElement, url)
        } else if (url.startsWith('rtmp://')) {
          // 使用flv.js播放RTMP流
          if (flvPlayer) {
            flvPlayer.destroy()
            flvPlayer = null
          }

          // 将RTMP URL转换为HTTP-FLV URL
          const httpFlvUrl = url.replace('rtmp://', 'http://').replace(':1935/', ':8088/') + '.live.flv'
          console.log('HTTP-FLV URL:', httpFlvUrl)

          if (flvjs.isSupported()) {
            flvPlayer = flvjs.createPlayer({
              type: 'flv',
              url: httpFlvUrl,
              isLive: true,
              hasAudio: false,
              hasVideo: true
            })

            flvPlayer.attachMediaElement(videoElement)
            flvPlayer.load()

            flvPlayer.on(flvjs.Events.LOADING_COMPLETE, () => {
              console.log('GB28181流加载完成')
              message.success('GB28181流播放成功')
            })

            flvPlayer.on(flvjs.Events.ERROR, (errorType: string, errorDetail: any) => {
              console.error('GB28181播放错误:', errorType, errorDetail)
              message.error('GB28181流播放失败')
            })
          } else {
            message.error('浏览器不支持FLV播放')
          }
        } else {
          message.error('不支持的流协议: ' + url)
        }
      } else if (livetypeSelected.value === 2) {
        console.log(999999, res)
        rtspData.value = 'url:' + res.data.url
      } else if (livetypeSelected.value === 1) {
        console.log('rtmp url:666666', res)
        const url = res.data.url
        const videoElement = videowebrtc.value
        console.log('start live:', url)
        console.log(videoElement)

        // 使用flv.js播放RTMP流
        if (flvPlayer) {
          flvPlayer.destroy()
          flvPlayer = null
        }

        // 将RTMP URL转换为HTTP-FLV URL
        const httpFlvUrl = url.replace('rtmp://', 'http://').replace(':1935/', ':8088/') + '.live.flv'
        console.log('HTTP-FLV URL:', httpFlvUrl)

        if (flvjs.isSupported()) {
          flvPlayer = flvjs.createPlayer({
            type: 'flv',
            url: httpFlvUrl,
            isLive: true,
            hasAudio: false,
            hasVideo: true
          })

          flvPlayer.attachMediaElement(videoElement)
          flvPlayer.load()

          flvPlayer.on(flvjs.Events.LOADING_COMPLETE, () => {
            console.log('RTMP流加载完成')
            message.success('RTMP流播放成功')
          })

          flvPlayer.on(flvjs.Events.ERROR, (errorType: string, errorDetail: any) => {
            console.error('RTMP播放错误:', errorType, errorDetail)
            message.error('RTMP流播放失败')
          })
        } else {
          message.error('浏览器不支持FLV播放')
        }
      } else if (livetypeSelected.value === 4) {
        const videoElement = videowebrtc.value as unknown as HTMLMediaElement
        videoElement.muted = true
        playWebrtc(videoElement, res.data.url)
      }
      liveState.value = true
    })
    .catch(err => {
      console.error(err)
    })
}
const onStop = () => {
  videoId.value =
    droneSelected.value + '/' + cameraSelected.value + '/' + (videoSelected.value || nonSwitchable + '-0')

  // 停止flv播放器
  if (flvPlayer) {
    flvPlayer.destroy()
    flvPlayer = null
  }

  stopLivestream({
    video_id: videoId.value
  }).then(res => {
    if (res.code === 0) {
      message.success(res.message)
      liveState.value = false
      lensSelected.value = undefined
      console.log('stop play livestream')
    }
  })
}

const onUpdateQuality = () => {
  if (!liveState.value) {
    message.info('Please turn on the livestream first.')
    return
  }
  setLivestreamQuality({
    video_id: videoId.value,
    video_quality: claritySelected.value
  })
    .then(res => {
      if (res.code === 0) {
        message.success('Set the clarity to ' + clarityList[claritySelected.value].label)
      }
    })
    .catch(err => {
      console.error(err)
    })
}

const onLiveTypeSelect = (val: any) => {
  livetypeSelected.value = val
}
const onDroneSelect = (val: SelectOption) => {
  droneSelected.value = val.value
  const temp: Array<SelectOption> = []
  cameraList.value = []
  cameraSelected.value = undefined
  videoSelected.value = undefined
  videoList.value = []
  lensList.value = []
  if (!val.more) {
    return
  }
  val.more.forEach((ele: any) => {
    temp.push({ label: ele.name, value: ele.index, more: ele.videos_list })
  })

  console.info('temp--------', temp)
  cameraList.value = temp
}
const onCameraSelect = (val: SelectOption) => {
  cameraSelected.value = val.value
  const result: Array<SelectOption> = []
  videoSelected.value = undefined
  videoList.value = []
  lensList.value = []
  if (!val.more) {
    return
  }

  val.more.forEach((ele: any) => {
    result.push({ label: ele.type, value: ele.index, more: ele.switch_video_types })
  })
  videoList.value = result
  if (videoList.value.length === 0) {
    return
  }
  const firstVideo: SelectOption = videoList.value[0]
  videoSelected.value = firstVideo.value
  lensList.value = firstVideo.more
  lensSelected.value = firstVideo.label
  isDockLive.value = lensList.value?.length > 0
}
const onVideoSelect = (val: SelectOption) => {
  videoSelected.value = val.value
  lensList.value = val.more
  lensSelected.value = val.label
}
const onClaritySelect = (val: any) => {
  claritySelected.value = val
}
const onSwitch = async () => {
  if (lensSelected.value === undefined || lensSelected.value === nonSwitchable) {
    message.info('The ' + nonSwitchable + ' lens cannot be switched, please select the lens to be switched.', 8)
    return
  }

  try {
    // 检查当前是否有直播流在运行
    if (liveState.value) {
      // 如果有直播流在运行，先停止当前流
      message.loading('Stopping current stream...', 0)

      await stopLivestream({
        video_id: videoId.value
      })

      // 等待一秒确保流完全停止
      await new Promise(resolve => setTimeout(resolve, 1000))

      message.destroy()
      message.success('Current stream stopped successfully')
    }

    // 更新videoId以反映新的摄像头选择
    const newVideoId = {
      drone_sn: droneSelected.value,
      payload_index: cameraSelected.value,
      video_index: videoSelected.value
    }

    // 启动新的直播流
    message.loading('Starting new stream with selected camera...', 0)

    const startResult = await startLivestream({
      video_id: newVideoId,
      url_type: livetypeSelected.value,
      video_quality: claritySelected.value
    })

    if (startResult.code === 0) {
      message.destroy()

      // 根据流类型显示不同的成功消息
      if (livetypeSelected.value === 3) { // GB28181
        message.success('Successfully switched to new GB28181 camera and started stream')
        console.log('📺 GB28181 WebRTC URL:', startResult.data?.url)
      } else {
        message.success('Successfully switched to new camera and started stream')
      }

      // 更新当前状态
      liveState.value = true
      videoId.value = newVideoId

      // 更新直播流URL显示
      if (startResult.data && startResult.data.url) {
        livestreamSource.value = startResult.data.url

        // 如果是GB28181流，显示特殊提示
        if (livetypeSelected.value === 3) {
          console.log('🔗 GB28181流已启动，可使用WebRTC播放器播放:', startResult.data.url)
        }
      }
    } else {
      message.destroy()
      message.error('Failed to start new stream: ' + (startResult.message || 'Unknown error'))
    }
  } catch (error) {
    message.destroy()
    console.error('Camera switch error:', error)
    message.error('Failed to switch camera: ' + (error.message || 'Unknown error'))
  }
}
const playWebrtc = (videoElement: HTMLMediaElement, url: string) => {
  if (webrtc) {
    webrtc.close()
  }

  // 检查ZLMRTCClient是否可用
  if (!window.ZLMRTCClient) {
    message.error('ZLMRTCClient 未加载，请确保已引入 ZLMRTCClient.js')
    return
  }

  // 将webrtc://URL转换为正确的WebRTC API端点
  let webrtcUrl = url
  if (url.startsWith('webrtc://')) {
    try {
      // 解析webrtc://URL格式: webrtc://host/live/stream
      const urlObj = new URL(url.replace('webrtc://', 'http://'))
      const pathParts = urlObj.pathname.split('/').filter(Boolean)

      let app = 'live' // 默认为live
      let stream = 'stream' // 默认为stream

      if (pathParts.length >= 1) {
        app = pathParts[0]
      }
      if (pathParts.length >= 2) {
        stream = pathParts[1]
      }

      // 构建正确的ZLM WebRTC API URL (WVP格式)
      webrtcUrl = `http://${urlObj.hostname}:8088/index/api/webrtc?app=${app}&stream=${stream}&type=play`
      console.log('转换后的ZLM WebRTC URL:', webrtcUrl)
    } catch (err) {
      console.error('URL转换失败:', err)
      message.error('WebRTC URL格式错误')
      return
    }
  }

  // 创建ZLM WebRTC连接
  webrtc = new window.ZLMRTCClient.Endpoint({
    element: videoElement,
    debug: true,
    zlmsdpUrl: webrtcUrl,
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
    message.error('WebRTC ICE 协商失败')
  })

  webrtc.on(window.ZLMRTCClient.Events.WEBRTC_ON_REMOTE_STREAMS, (e: any) => {
    console.info('WebRTC连接成功:', e)
    message.success('WebRTC流播放成功')
  })

  webrtc.on(window.ZLMRTCClient.Events.WEBRTC_OFFER_ANWSER_EXCHANGE_FAILED, (e: any) => {
    console.error('Offer/Answer 交换失败:', e)
    message.error('WebRTC 连接失败: ' + (e.msg || e))
  })

  webrtc.on(window.ZLMRTCClient.Events.WEBRTC_ON_LOCAL_STREAM, (s: any) => {
    console.log('获取到本地流:', s)
  })
}
</script>

<style lang="scss" scoped>
@import '/@/styles/index.scss';
</style>
