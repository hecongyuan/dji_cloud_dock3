<template>
  <div class="smart-camera-switch">
    <a-card title="智能摄像头切换" :bordered="false">
      <a-space direction="vertical" style="width: 100%">
        <!-- 当前状态显示 -->
        <a-alert
          v-if="currentStatus"
          :message="currentStatus.message"
          :type="currentStatus.type"
          show-icon
        />
        
        <!-- 摄像头选择 -->
        <a-form layout="vertical">
          <a-form-item label="选择摄像头">
            <a-select
              v-model:value="selectedCamera"
              placeholder="请选择要切换的摄像头"
              style="width: 100%"
              @change="onCameraChange"
            >
              <a-select-option
                v-for="camera in availableCameras"
                :key="camera.value"
                :value="camera.value"
              >
                {{ camera.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
          
          <a-form-item label="视频质量">
            <a-select
              v-model:value="selectedQuality"
              placeholder="请选择视频质量"
              style="width: 100%"
            >
              <a-select-option
                v-for="quality in qualityOptions"
                :key="quality.value"
                :value="quality.value"
              >
                {{ quality.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-form>
        
        <!-- 操作按钮 -->
        <a-space>
          <a-button
            type="primary"
            :loading="isSwitching"
            @click="performSmartSwitch"
            :disabled="!selectedCamera"
          >
            智能切换摄像头
          </a-button>
          
          <a-button
            @click="stopCurrentStream"
            :loading="isStopping"
            :disabled="!isStreaming"
          >
            停止当前流
          </a-button>
          
          <a-button
            @click="refreshStatus"
            :loading="isRefreshing"
          >
            刷新状态
          </a-button>
        </a-space>
        
        <!-- 操作日志 -->
        <a-collapse v-if="operationLogs.length > 0">
          <a-collapse-panel key="1" header="操作日志">
            <a-timeline>
              <a-timeline-item
                v-for="(log, index) in operationLogs"
                :key="index"
                :color="log.type === 'success' ? 'green' : log.type === 'error' ? 'red' : 'blue'"
              >
                {{ log.message }}
                <template #dot>
                  <ClockCircleOutlined />
                </template>
              </a-timeline-item>
            </a-timeline>
          </a-collapse-panel>
        </a-collapse>
      </a-space>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
import { ClockCircleOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { onMounted, ref } from 'vue'
import { getLiveCapacity, smartCameraSwitch, stopLivestream } from '/@/api/manage'

interface CameraOption {
  value: string
  label: string
  deviceSn: string
  payloadIndex: number
  videoIndex: number
}

interface QualityOption {
  value: number
  label: string
}

interface OperationLog {
  type: 'info' | 'success' | 'error'
  message: string
  timestamp: Date
}

// 响应式数据
const selectedCamera = ref<string>()
const selectedQuality = ref<number>(2) // 默认标准质量
const isSwitching = ref(false)
const isStopping = ref(false)
const isRefreshing = ref(false)
const isStreaming = ref(false)
const currentStatus = ref<{ type: 'success' | 'error' | 'info', message: string } | null>(null)
const operationLogs = ref<OperationLog[]>([])

// 选项数据
const availableCameras = ref<CameraOption[]>([])
const qualityOptions: QualityOption[] = [
  { value: 0, label: '自适应' },
  { value: 1, label: '流畅' },
  { value: 2, label: '标准' },
  { value: 3, label: '高清' },
  { value: 4, label: '超清' }
]

// 当前流信息
const currentStreamInfo = ref<{
  deviceSn: string
  payloadIndex: number
  videoIndex: number
} | null>(null)

// 添加操作日志
const addLog = (type: 'info' | 'success' | 'error', message: string) => {
  operationLogs.value.unshift({
    type,
    message,
    timestamp: new Date()
  })
  
  // 只保留最近10条日志
  if (operationLogs.value.length > 10) {
    operationLogs.value = operationLogs.value.slice(0, 10)
  }
}

// 刷新摄像头列表
const refreshCameras = async () => {
  try {
    isRefreshing.value = true
    addLog('info', '正在获取可用摄像头列表...')
    
    const response = await getLiveCapacity({})
    
    if (response.code === 0 && response.data) {
      const cameras: CameraOption[] = []
      
      response.data.forEach((device: any) => {
        if (device.camerasList) {
          device.camerasList.forEach((camera: any) => {
            if (camera.videos_list) {
              camera.videos_list.forEach((video: any) => {
                cameras.push({
                  value: `${device.sn}_${camera.index}_${video.index}`,
                  label: `${device.name || device.sn} - ${camera.name} - ${video.type}`,
                  deviceSn: device.sn,
                  payloadIndex: camera.index,
                  videoIndex: video.index
                })
              })
            }
          })
        }
      })
      
      availableCameras.value = cameras
      addLog('success', `成功获取 ${cameras.length} 个可用摄像头`)
      
      // 更新状态
      updateCurrentStatus()
    } else {
      addLog('error', '获取摄像头列表失败: ' + (response.message || '未知错误'))
    }
  } catch (error) {
    addLog('error', '获取摄像头列表异常: ' + (error as Error).message)
  } finally {
    isRefreshing.value = false
  }
}

// 更新当前状态
const updateCurrentStatus = () => {
  if (currentStreamInfo.value) {
    currentStatus.value = {
      type: 'success',
      message: `当前正在使用摄像头: ${currentStreamInfo.value.deviceSn}`
    }
    isStreaming.value = true
  } else {
    currentStatus.value = {
      type: 'info',
      message: '当前没有活跃的直播流'
    }
    isStreaming.value = false
  }
}

// 摄像头选择变化
const onCameraChange = (value: string) => {
  selectedCamera.value = value
  addLog('info', `选择了摄像头: ${value}`)
}

// 执行智能切换
const performSmartSwitch = async () => {
  if (!selectedCamera.value) {
    message.warning('请先选择要切换的摄像头')
    return
  }
  
  try {
    isSwitching.value = true
    addLog('info', '开始智能摄像头切换...')
    
    const camera = availableCameras.value.find(c => c.value === selectedCamera.value)
    if (!camera) {
      throw new Error('选中的摄像头不存在')
    }
    
    const switchParams = {
      video_id: {
        drone_sn: camera.deviceSn,
        payload_index: camera.payloadIndex,
        video_index: camera.videoIndex
      },
      url_type: 1, // RTMP
      video_quality: selectedQuality.value
    }
    
    const response = await smartCameraSwitch(switchParams)
    
    if (response.code === 0) {
      addLog('success', '智能摄像头切换成功')
      message.success('摄像头切换成功')
      
      // 更新当前流信息
      currentStreamInfo.value = {
        deviceSn: camera.deviceSn,
        payloadIndex: camera.payloadIndex,
        videoIndex: camera.videoIndex
      }
      
      updateCurrentStatus()
    } else {
      addLog('error', '智能摄像头切换失败: ' + (response.message || '未知错误'))
      message.error('摄像头切换失败: ' + (response.message || '未知错误'))
    }
  } catch (error) {
    addLog('error', '智能摄像头切换异常: ' + (error as Error).message)
    message.error('摄像头切换异常: ' + (error as Error).message)
  } finally {
    isSwitching.value = false
  }
}

// 停止当前流
const stopCurrentStream = async () => {
  if (!currentStreamInfo.value) {
    message.warning('当前没有活跃的直播流')
    return
  }
  
  try {
    isStopping.value = true
    addLog('info', '正在停止当前直播流...')
    
    const stopParams = {
      video_id: {
        drone_sn: currentStreamInfo.value.deviceSn,
        payload_index: currentStreamInfo.value.payloadIndex,
        video_index: currentStreamInfo.value.videoIndex
      }
    }
    
    const response = await stopLivestream(stopParams)
    
    if (response.code === 0) {
      addLog('success', '直播流停止成功')
      message.success('直播流已停止')
      
      currentStreamInfo.value = null
      updateCurrentStatus()
    } else {
      addLog('error', '停止直播流失败: ' + (response.message || '未知错误'))
      message.error('停止直播流失败: ' + (response.message || '未知错误'))
    }
  } catch (error) {
    addLog('error', '停止直播流异常: ' + (error as Error).message)
    message.error('停止直播流异常: ' + (error as Error).message)
  } finally {
    isStopping.value = false
  }
}

// 刷新状态
const refreshStatus = async () => {
  await refreshCameras()
}

// 组件挂载时初始化
onMounted(() => {
  refreshCameras()
})
</script>

<style lang="scss" scoped>
.smart-camera-switch {
  padding: 16px;
  
  .ant-card {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  .ant-timeline {
    max-height: 200px;
    overflow-y: auto;
  }
}
</style> 