package com.dji.sample.manage.service.impl;

import com.dji.sample.manage.model.dto.*;
import com.dji.sample.manage.model.param.DeviceQueryParam;
import com.dji.sample.manage.service.*;
import com.dji.sdk.cloudapi.device.DeviceDomainEnum;
import com.dji.sdk.cloudapi.device.VideoId;
import com.dji.sdk.cloudapi.livestream.*;
import com.dji.sdk.cloudapi.livestream.api.AbstractLivestreamService;
import com.dji.sdk.common.HttpResultResponse;
import com.dji.sdk.common.SDKManager;
import com.dji.sdk.mqtt.services.ServicesReplyData;
import com.dji.sdk.mqtt.services.TopicServicesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author sean.zhou
 * @date 2021/11/22
 * @version 0.1
 */
@Service
@Transactional
public class LiveStreamServiceImpl implements ILiveStreamService {

    @Autowired
    private ICapacityCameraService capacityCameraService;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IWorkspaceService workspaceService;

    @Autowired
    private IDeviceRedisService deviceRedisService;

    @Autowired
    private AbstractLivestreamService abstractLivestreamService;

    @Autowired
    private M4TDLiveStreamOptimizer m4tdOptimizer;

    @Override
    public List<CapacityDeviceDTO> getLiveCapacity(String workspaceId) {

        // Query all devices in this workspace.
        List<DeviceDTO> devicesList = deviceService.getDevicesByParams(
                DeviceQueryParam.builder()
                        .workspaceId(workspaceId)
                        .domains(List.of(DeviceDomainEnum.DRONE.getDomain(), DeviceDomainEnum.DOCK.getDomain()))
                        .build());

        // Query the live capability of each drone.
        return devicesList.stream()
                .filter(device -> deviceRedisService.checkDeviceOnline(device.getDeviceSn()))
                .map(device -> {
                    List<CapacityCameraDTO> camerasList = capacityCameraService.getCapacityCameraByDeviceSn(device.getDeviceSn());
                    
                    // 为M4TD设备添加默认摄像头配置（如果没有摄像头数据）
                    if ((camerasList == null || camerasList.isEmpty()) && 
                        (device.getDeviceSn().contains("1581F8HGX") || device.getDeviceName().contains("M4TD"))) {
                        camerasList = createDefaultM4TDCameras();
                    }
                    
                    return CapacityDeviceDTO.builder()
                            .name(Objects.requireNonNullElse(device.getNickname(), device.getDeviceName()))
                            .sn(device.getDeviceSn())
                            .camerasList(camerasList)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 为M4TD设备创建默认摄像头配置
     */
    private List<CapacityCameraDTO> createDefaultM4TDCameras() {
        CapacityVideoDTO video = CapacityVideoDTO.builder()
                .id(java.util.UUID.randomUUID().toString())
                .index("normal-0")
                .type("normal")
                .switchVideoTypes(java.util.Arrays.asList("normal"))
                .build();
                
        CapacityCameraDTO camera = CapacityCameraDTO.builder()
                .id(java.util.UUID.randomUUID().toString())
                .name("M4TD Camera")
                .index("165-0-7")  // 修正M4TD的相机索引
                .videosList(java.util.Arrays.asList(video))
                .build();
                
        return java.util.Arrays.asList(camera);
    }

    @Override
    public HttpResultResponse liveStart(LiveTypeDTO liveParam) {
        // Check if this lens is available live.
        HttpResultResponse<DeviceDTO> responseResult = this.checkBeforeLive(liveParam.getVideoId());
        if (HttpResultResponse.CODE_SUCCESS != responseResult.getCode()) {
            return responseResult;
        }

        ILivestreamUrl url;
        // 如果客户端提供了自定义的 url 参数，使用它；否则使用配置文件中的默认值
        if (liveParam.getUrl() != null && !liveParam.getUrl().isEmpty()) {
            url = parseUrlFromString(liveParam.getUrlType(), liveParam.getUrl());
        } else {
            url = LiveStreamProperty.get(liveParam.getUrlType());
        }
        url = setExt(liveParam.getUrlType(), url, liveParam.getVideoId());
        url = setExt(liveParam.getUrlType(), url, liveParam.getVideoId());
        
        // 为M4TD设备优化URL配置以解决网络拥塞问题
        if (m4tdOptimizer.isM4TDDevice(responseResult.getData().getDeviceSn())) {
            url = m4tdOptimizer.optimizeM4TDUrl(liveParam.getUrlType(), url);
        }

        TopicServicesResponse<ServicesReplyData> response = abstractLivestreamService.liveStartPush(
                SDKManager.getDeviceSDK(responseResult.getData().getDeviceSn()),
                new LiveStartPushRequest()
                        .setUrl(url)
                        .setUrlType(liveParam.getUrlType())
                        .setVideoId(liveParam.getVideoId())
                        .setVideoQuality(liveParam.getVideoQuality()));

        if (!response.getData().getResult().isSuccess()) {
            return HttpResultResponse.error(response.getData().getResult());
        }

        LiveDTO live = new LiveDTO();

        switch (liveParam.getUrlType()) {
            case AGORA:
                break;
            case RTMP:
                // 直接使用RTMP URL
                live.setUrl(url.toString());
                break;
            case GB28181:
                LivestreamGb28181Url gb28181 = (LivestreamGb28181Url) url;
                // 生成RTMP URL格式
                live.setUrl(new StringBuilder()
                        .append("rtmp://")
                        .append(gb28181.getServerIP())
                        .append(":1935/rtp/")  // 使用RTMP端口1935
                        .append(gb28181.getAgentID())
                        .append("_")
                        .append(gb28181.getChannel())
                        .toString());
                break;
            case RTSP:
                live.setUrl(String.valueOf(response.getData().getOutput()));
                break;
            case WHIP:
                live.setUrl(url.toString().replace("whip", "whep"));
                break;
            default:
                return HttpResultResponse.error(LiveErrorCodeEnum.URL_TYPE_NOT_SUPPORTED);
        }

        return HttpResultResponse.success(live);
    }

    @Override
    public HttpResultResponse liveStop(VideoId videoId) {
        HttpResultResponse<DeviceDTO> responseResult = this.checkBeforeLive(videoId);
        if (HttpResultResponse.CODE_SUCCESS != responseResult.getCode()) {
            return responseResult;
        }

        TopicServicesResponse<ServicesReplyData> response = abstractLivestreamService.liveStopPush(
                SDKManager.getDeviceSDK(responseResult.getData().getDeviceSn()), new LiveStopPushRequest()
                        .setVideoId(videoId));
        if (!response.getData().getResult().isSuccess()) {
            return HttpResultResponse.error(response.getData().getResult());
        }

        return HttpResultResponse.success();
    }

    @Override
    public HttpResultResponse liveSetQuality(LiveTypeDTO liveParam) {
        HttpResultResponse<DeviceDTO> responseResult = this.checkBeforeLive(liveParam.getVideoId());
        if (responseResult.getCode() != 0) {
            return responseResult;
        }

        TopicServicesResponse<ServicesReplyData> response = abstractLivestreamService.liveSetQuality(
                SDKManager.getDeviceSDK(responseResult.getData().getDeviceSn()), new LiveSetQualityRequest()
                        .setVideoQuality(liveParam.getVideoQuality())
                        .setVideoId(liveParam.getVideoId()));
        if (!response.getData().getResult().isSuccess()) {
            return HttpResultResponse.error(response.getData().getResult());
        }

        return HttpResultResponse.success();
    }

    @Override
    public HttpResultResponse liveLensChange(LiveTypeDTO liveParam) {
        HttpResultResponse<DeviceDTO> responseResult = this.checkBeforeLive(liveParam.getVideoId());
        if (HttpResultResponse.CODE_SUCCESS != responseResult.getCode()) {
            return responseResult;
        }

        TopicServicesResponse<ServicesReplyData> response = abstractLivestreamService.liveLensChange(
                SDKManager.getDeviceSDK(responseResult.getData().getDeviceSn()), new LiveLensChangeRequest()
                        .setVideoType(liveParam.getVideoType())
                        .setVideoId(liveParam.getVideoId()));

        if (!response.getData().getResult().isSuccess()) {
            return HttpResultResponse.error(response.getData().getResult());
        }

        return HttpResultResponse.success();
    }

    /**
     * Check if this lens is available live.
     * @param videoId
     * @return
     */
    private HttpResultResponse<DeviceDTO> checkBeforeLive(VideoId videoId) {
        if (Objects.isNull(videoId)) {
            return HttpResultResponse.error(LiveErrorCodeEnum.ERROR_PARAMETERS);
        }

        Optional<DeviceDTO> deviceOpt = deviceService.getDeviceBySn(videoId.getDroneSn());
        // Check if the gateway device connected to this drone exists
        if (deviceOpt.isEmpty()) {
            return HttpResultResponse.error(LiveErrorCodeEnum.NO_AIRCRAFT);
        }

        if (DeviceDomainEnum.DOCK == deviceOpt.get().getDomain()) {
            return HttpResultResponse.success(deviceOpt.get());
        }
        List<DeviceDTO> gatewayList = deviceService.getDevicesByParams(
                DeviceQueryParam.builder()
                        .childSn(videoId.getDroneSn())
                        .build());
        if (gatewayList.isEmpty()) {
            return HttpResultResponse.error(LiveErrorCodeEnum.NO_FLIGHT_CONTROL);
        }

        return HttpResultResponse.success(gatewayList.get(0));
    }

    /**
     * This is business-customized logic and is only used for testing.
     * @param type
     * @param url
     * @param videoId
     */
    private ILivestreamUrl setExt(UrlTypeEnum type, ILivestreamUrl url, VideoId videoId) {
        switch (type) {
            case AGORA:
                LivestreamAgoraUrl agoraUrl = (LivestreamAgoraUrl) url.clone();
                return agoraUrl.setSn(videoId.getDroneSn());
            case RTMP:
                LivestreamRtmpUrl rtmpUrl = (LivestreamRtmpUrl) url.clone();
                return rtmpUrl.setUrl(rtmpUrl.getUrl() + videoId.getDroneSn() + "-" + videoId.getPayloadIndex().toString());
            case GB28181:
                String random = String.valueOf(Math.abs(videoId.getDroneSn().hashCode()) % 1000);
                LivestreamGb28181Url gbUrl = (LivestreamGb28181Url) url.clone();
                
                // 修复 agentID 的边界检查
                String agentID = gbUrl.getAgentID();
                int agentIDMaxLength = 20 - random.length();
                if (agentID.length() > agentIDMaxLength) {
                    agentID = agentID.substring(0, agentIDMaxLength);
                }
                gbUrl.setAgentID(agentID + random);
                
                // 修复 channel 的边界检查
                String deviceType = String.valueOf(videoId.getPayloadIndex().getType().getType());
                String channel = gbUrl.getChannel();
                int channelMaxLength = 20 - deviceType.length();
                if (channel.length() > channelMaxLength) {
                    channel = channel.substring(0, channelMaxLength);
                }
                return gbUrl.setChannel(channel + deviceType);
            case WHIP:
                LivestreamWhipUrl whipUrl = (LivestreamWhipUrl) url.clone();
                return whipUrl.setUrl(whipUrl.getUrl() + videoId.getDroneSn() + "-" + videoId.getPayloadIndex().toString());
        }
        return url;
    }

    /**
     * Parse URL string to ILivestreamUrl object based on URL type
     * @param urlType The type of URL
     * @param urlString The URL string to parse
     * @return ILivestreamUrl object
     */
    private ILivestreamUrl parseUrlFromString(UrlTypeEnum urlType, String urlString) {
        switch (urlType) {
            case GB28181:
                return parseGb28181Url(urlString);
            case RTMP:
                return new LivestreamRtmpUrl().setUrl(urlString);
            case RTSP:
                return parseRtspUrl(urlString);
            case AGORA:
                return parseAgoraUrl(urlString);
            case WHIP:
                return new LivestreamWhipUrl().setUrl(urlString);
            default:
                throw new IllegalArgumentException("Unsupported URL type: " + urlType);
        }
    }

    /**
     * Parse GB28181 URL string
     * @param urlString URL string in format: serverIP=xxx&serverPort=xxx&serverID=xxx&agentID=xxx&agentPassword=xxx&localPort=xxx&channel=xxx
     * @return LivestreamGb28181Url object
     */
    private LivestreamGb28181Url parseGb28181Url(String urlString) {
        LivestreamGb28181Url url = new LivestreamGb28181Url();
        String[] params = urlString.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                switch (key) {
                    case "serverIP":
                        url.setServerIP(value);
                        break;
                    case "serverPort":
                        url.setServerPort(Integer.parseInt(value));
                        break;
                    case "serverID":
                        url.setServerID(value);
                        break;
                    case "agentID":
                        url.setAgentID(value);
                        break;
                    case "agentPassword":
                        url.setAgentPassword(value);
                        break;
                    case "localPort":
                        url.setLocalPort(Integer.parseInt(value));
                        break;
                    case "channel":
                        url.setChannel(value);
                        break;
                }
            }
        }
        return url;
    }

    /**
     * Parse RTSP URL string
     * @param urlString URL string in format: userName=xxx&password=xxx&port=xxx
     * @return LivestreamRtspUrl object
     */
    private LivestreamRtspUrl parseRtspUrl(String urlString) {
        LivestreamRtspUrl url = new LivestreamRtspUrl();
        String[] params = urlString.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                switch (key) {
                    case "userName":
                        url.setUsername(value);
                        break;
                    case "password":
                        url.setPassword(value);
                        break;
                    case "port":
                        url.setPort(Integer.parseInt(value));
                        break;
                }
            }
        }
        return url;
    }

    /**
     * Parse Agora URL string
     * @param urlString URL string in format: channel=xxx&sn=xxx&token=xxx&uid=xxx
     * @return LivestreamAgoraUrl object
     */
    private LivestreamAgoraUrl parseAgoraUrl(String urlString) {
        LivestreamAgoraUrl url = new LivestreamAgoraUrl();
        String[] params = urlString.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                switch (key) {
                    case "channel":
                        url.setChannel(value);
                        break;
                    case "sn":
                        url.setSn(value);
                        break;
                    case "token":
                        url.setToken(value);
                        break;
                    case "uid":
                        url.setUid(Integer.parseInt(value));
                        break;
                }
            }
        }
        return url;
    }
}