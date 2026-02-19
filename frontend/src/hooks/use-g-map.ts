import AMapLoader from '@amap/amap-jsapi-loader'
import { App, reactive } from 'vue'
import { AMapConfig } from '/@/constants/index'

export function useGMapManage () {
  const state = reactive({
    aMap: null, // Map类
    map: null, // 地图对象
    mouseTool: null,
  })

  async function initMap (container: string, app: App, center?: [number, number]) {
    AMapLoader.load({
      ...AMapConfig
    }).then((AMap) => {
      state.aMap = AMap
      state.map = new AMap.Map(container, {
        center: center || [113.943225499, 22.577673716], // 使用传入的中心点或默认值
        zoom: 15 // 调整默认缩放级别
      })
      state.mouseTool = new AMap.MouseTool(state.map)

      // 挂在到全局
      app.config.globalProperties.$aMap = state.aMap
      app.config.globalProperties.$map = state.map
      app.config.globalProperties.$mouseTool = state.mouseTool
    }).catch(e => {
      console.log(e)
    })
  }

  function globalPropertiesConfig (app: App, center?: [number, number]) {
    initMap('g-container', app, center)
  }

  function getMap () {
    return state.map
  }

  return {
    globalPropertiesConfig,
    getMap,
  }
}
