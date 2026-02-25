<template>
  <div class="rtmp-example">
    <h3>RTMP播放器示例</h3>
    
    <div class="control-panel">
      <a-input 
        v-model:value="streamUrl" 
        placeholder="输入RTMP流地址"
        style="width: 400px; margin-right: 10px;"
      />
      <a-button type="primary" @click="startPlay" :loading="isPlaying">
        {{ isPlaying ? '播放中' : '开始播放' }}
      </a-button>
      <a-button @click="stopPlay" :disabled="!isPlaying">
        停止播放
      </a-button>
    </div>

    <div class="player-container">
      <RTMPPlayer 
        ref="rtmpPlayer"
        :stream-url="streamUrl"
        :auto-play="false"
      />
    </div>

    <div class="info-panel">
      <h4>使用说明：</h4>
      <ul>
        <li>输入RTMP流地址，例如：<code>rtmp://124.71.163.191:1935/rtp/41010500002000000761_34020000001320000089</code></li>
        <li>点击"开始播放"按钮开始播放</li>
        <li>系统会自动将RTMP转换为HTTP-FLV格式播放</li>
        <li>如果遇到问题，请检查网络连接和流地址</li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { message } from 'ant-design-vue'
import { ref } from 'vue'
import RTMPPlayer from './RTMPPlayer.vue'

const streamUrl = ref('rtmp://124.71.163.191:1935/rtp/41010500002000000761_34020000001320000089')
const rtmpPlayer = ref()
const isPlaying = ref(false)

const startPlay = async () => {
  if (!streamUrl.value) {
    message.warning('请输入RTMP流地址')
    return
  }

  try {
    isPlaying.value = true
    await rtmpPlayer.value?.play()
  } catch (error) {
    console.error('播放失败:', error)
    message.error('播放失败')
    isPlaying.value = false
  }
}

const stopPlay = () => {
  rtmpPlayer.value?.stop()
  isPlaying.value = false
  message.info('已停止播放')
}
</script>

<style lang="scss" scoped>
.rtmp-example {
  padding: 20px;

  .control-panel {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
  }

  .player-container {
    width: 720px;
    height: 420px;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
    overflow: hidden;
    margin-bottom: 20px;
  }

  .info-panel {
    background: #f5f5f5;
    padding: 15px;
    border-radius: 4px;

    ul {
      margin: 10px 0;
      padding-left: 20px;
    }

    code {
      background: #e6f7ff;
      padding: 2px 4px;
      border-radius: 2px;
      font-family: monospace;
    }
  }
}
</style> 