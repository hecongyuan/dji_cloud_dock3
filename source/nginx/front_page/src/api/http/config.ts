export const CURRENT_CONFIG = {

  // license
  appId: '166655', // You need to go to the development website to apply.
  appKey: 'a0c883fadacccb2053db1903eebe541', // You need to go to the development website to apply.
  appLicense: 'TsaTKLi6UDFg+EMJ4qP/Nhk0ZoO16WUqOo3HvbySNOE9RCledlt23u6yzQUY0EnwjAwc5NUcyKI810m1KUkGE+DPpm3KZlVds+//AovUXs3liS3SgTV0gz8wfyNRXwCYGT5fsL+PRT/ehy8zBVO88UfrS7P7D5rxHFqbLt6lgBk=', // You need to go to the development website to apply.

  baseURL: 'http://124.71.163.191:6789/', // This url must end with "/". Example: 'http://192.168.1.1:6789/'
  websocketURL: 'ws://124.71.163.191:6789/api/v1/ws', // Example: 'ws://192.168.1.1:6789/api/v1/ws'


  // livestreaming
  // RTMP  Note: This IP is the address of the streaming server. If you want to see livestream on web page, you need to convert the RTMP stream to WebRTC stream.
  // rtmpURL: 'rtmp://124.71.163.191:1935/rtp/', // Example: 'rtmp://192.168.1.1/live/'
  // GB28181 Note:If you don't know what these parameters mean, you can go to Pilot2 and select the GB28181 page in the cloud platform. Where the parameters same as these parameters.
  gbServerIp: '124.71.163.191',
  gbServerPort: '8116',
  gbServerId: '41010500002000000001',
  gbAgentId: '41010500002000000001',
  gbPassword: 'bajiuwulian1006',
  gbAgentPort: '8116',
  gbAgentChannel: '34020000001320000006',

  // WebRTC配置 - 根据WVP配置，使用ZLM WebRTC API
  webrtcProxyUrl: 'http://124.71.163.191:6789/proxy/index/api/webrtc',
  webrtcDirectUrl: 'http://124.71.163.191:8088/index/api/webrtc',
  // RTSP
  // rtspUserName: 'Please enter the username.',
  // rtspPassword: 'Please enter the password.',
  rtspPort: '554',
  // Agora
  agoraAPPID: 'Please enter the agora app id.',
  agoraToken: 'Please enter the agora temporary token.',
  agoraChannel: 'Please enter the agora channel.',

  // map
  // You can apply on the AMap website.
  amapKey: '6f1f84a5768fd92733d91756d4a8e2ca',

}
