// WebRTC SDK端口修复和SDP格式修复
// 在WebRTC SDK加载后修改默认端口和SDP格式

(function () {
  // 等待页面加载完成
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', fixWebRtcPort)
  } else {
    fixWebRtcPort()
  }

  function fixWebRtcPort () {
    // 延迟执行，确保WebRTC SDK已加载
    setTimeout(() => {
      // 修改XMLHttpRequest以拦截WebRTC请求
      const originalOpen = XMLHttpRequest.prototype.open
      const originalSend = XMLHttpRequest.prototype.send

      XMLHttpRequest.prototype.open = function (method, url, async, user, password) {
        let processedUrl = url

        // Step 1: 修复错误的URL格式 - 将 /rtc/v1/play/&app=... 转换为 /index/api/webrtc?app=...
        if (typeof processedUrl === 'string' && processedUrl.includes('124.71.163.191:8088/rtc/v1/play/&app=')) {
          processedUrl = processedUrl.replace('/rtc/v1/play/&app=', '/index/api/webrtc?app=')
          console.log('WebRTC API路径修复 (错误格式):', {
            original: url,
            corrected: processedUrl
          })
        }

        // Step 2: 修复其他可能的错误格式 - 将 /rtc/v1/play/?app=... 转换为 /index/api/webrtc?app=...
        if (typeof processedUrl === 'string' && processedUrl.includes('124.71.163.191:8088/rtc/v1/play/?app=')) {
          processedUrl = processedUrl.replace('/rtc/v1/play/?app=', '/index/api/webrtc?app=')
          console.log('WebRTC API路径修复 (错误路径):', {
            original: url,
            corrected: processedUrl
          })
        }

        // Step 3: 将直接访问的ZLM WebRTC API替换为代理URL
        if (typeof processedUrl === 'string' && processedUrl.includes('124.71.163.191:8088/index/api/webrtc')) {
          const newUrl = processedUrl.replace('http://124.71.163.191:8088/index/api/webrtc', 'http://192.168.8.140:6789/proxy/index/api/webrtc')
          console.log('ZLM WebRTC API代理修复:', {
            original: url,
            fixed: newUrl
          })
          processedUrl = newUrl
        } else if (typeof processedUrl === 'string' && processedUrl.includes('124.71.163.191:8088/rtc')) {
          // Step 4: 将直接访问的WebRTC URL替换为代理URL（兼容旧格式）
          const newUrl = processedUrl.replace('http://124.71.163.191:8088/rtc', 'http://192.168.8.140:6789/proxy/rtc')
          console.log('WebRTC代理修复:', {
            original: url,
            fixed: newUrl
          })
          processedUrl = newUrl
        } else if (typeof processedUrl === 'string' && processedUrl.includes(':1985/rtc')) {
          // Step 5: 修复1985端口到8088（WVP ZLM服务器端口）
          const newUrl = processedUrl.replace(':1985/rtc', ':8088/rtc')
          console.log('WebRTC端口修复 (1985->8088):', {
            original: url,
            fixed: newUrl
          })
          processedUrl = newUrl
        } else if (typeof processedUrl === 'string' && processedUrl.includes(':8000/rtc')) {
          // Step 6: 修复8000端口到8088
          const newUrl = processedUrl.replace(':8000/rtc', ':8088/rtc')
          console.log('WebRTC端口修复 (8000->8088):', {
            original: url,
            fixed: newUrl
          })
          processedUrl = newUrl
        }

        return originalOpen.call(this, method, processedUrl, async, user, password)
      }

      // 修复SDP格式和URL格式
      XMLHttpRequest.prototype.send = function (data) {
        if (data && typeof data === 'string') {
          try {
            const jsonData = JSON.parse(data)
            let hasChanges = false

            // 修复SDP中的origin行，将 o=- 替换为 o=webrtc
            if (jsonData.sdp && typeof jsonData.sdp === 'string') {
              const fixedSdp = jsonData.sdp.replace(/^o=-\s+(\d+)\s+(\d+)\s+IN\s+IP4\s+(.+)$/gm, 'o=webrtc $1 $2 IN IP4 $3')
              if (fixedSdp !== jsonData.sdp) {
                jsonData.sdp = fixedSdp
                hasChanges = true
                console.log('SDP格式修复 (origin字段):', {
                  original: data.substring(0, 100) + '...',
                  fixed: fixedSdp.substring(0, 100) + '...'
                })
              }
            }

            // 修复api字段的URL格式
            if (jsonData.api && typeof jsonData.api === 'string') {
              if (jsonData.api.includes('/rtc/v1/play/&app=')) {
                const fixedApi = jsonData.api.replace('/rtc/v1/play/&app=', '/index/api/webrtc?app=')
                jsonData.api = fixedApi
                hasChanges = true
                console.log('API URL格式修复:', {
                  original: jsonData.api,
                  fixed: fixedApi
                })
              }
            }

            // 修复streamurl字段的URL格式
            if (jsonData.streamurl && typeof jsonData.streamurl === 'string') {
              if (jsonData.streamurl.startsWith('webrtc://')) {
                const fixedStreamUrl = jsonData.streamurl.replace('webrtc://', 'http://')
                jsonData.streamurl = fixedStreamUrl
                hasChanges = true
                console.log('Stream URL格式修复:', {
                  original: jsonData.streamurl,
                  fixed: fixedStreamUrl
                })
              }
            }

            if (hasChanges) {
              const fixedData = JSON.stringify(jsonData)
              return originalSend.call(this, fixedData)
            }
          } catch (e) {
            // 如果不是JSON格式，忽略错误
          }
        }
        return originalSend.call(this, data)
      }

      // 修复fetch请求
      const originalFetch = window.fetch
      window.fetch = function (input, init) {
        if (init && init.body && typeof init.body === 'string') {
          try {
            const jsonData = JSON.parse(init.body)
            let hasChanges = false

            // 修复SDP中的origin行，将 o=- 替换为 o=webrtc
            if (jsonData.sdp && typeof jsonData.sdp === 'string') {
              const fixedSdp = jsonData.sdp.replace(/^o=-\s+(\d+)\s+(\d+)\s+IN\s+IP4\s+(.+)$/gm, 'o=webrtc $1 $2 IN IP4 $3')
              if (fixedSdp !== jsonData.sdp) {
                jsonData.sdp = fixedSdp
                hasChanges = true
                console.log('SDP格式修复 (origin字段, fetch):', {
                  original: init.body.substring(0, 100) + '...',
                  fixed: fixedSdp.substring(0, 100) + '...'
                })
              }
            }

            // 修复api字段的URL格式
            if (jsonData.api && typeof jsonData.api === 'string') {
              if (jsonData.api.includes('/rtc/v1/play/&app=')) {
                const fixedApi = jsonData.api.replace('/rtc/v1/play/&app=', '/index/api/webrtc?app=')
                jsonData.api = fixedApi
                hasChanges = true
                console.log('API URL格式修复 (fetch):', {
                  original: jsonData.api,
                  fixed: fixedApi
                })
              }
            }

            // 修复streamurl字段的URL格式
            if (jsonData.streamurl && typeof jsonData.streamurl === 'string') {
              if (jsonData.streamurl.startsWith('webrtc://')) {
                const fixedStreamUrl = jsonData.streamurl.replace('webrtc://', 'http://')
                jsonData.streamurl = fixedStreamUrl
                hasChanges = true
                console.log('Stream URL格式修复 (fetch):', {
                  original: jsonData.streamurl,
                  fixed: fixedStreamUrl
                })
              }
            }

            if (hasChanges) {
              init.body = JSON.stringify(jsonData)
            }
          } catch (e) {
            // 如果不是JSON格式，忽略错误
          }
        }
        return originalFetch.call(this, input, init)
      }

      console.log('WebRTC端口修复、SDP格式修复和fetch拦截已应用')
    }, 2000)
  }
})()
