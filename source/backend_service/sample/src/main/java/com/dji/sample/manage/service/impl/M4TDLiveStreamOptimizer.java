package com.dji.sample.manage.service.impl;

import com.dji.sdk.cloudapi.livestream.*;
import org.springframework.stereotype.Component;

/**
 * M4TD直播流优化器
 * 专门处理M4TD设备的直播流配置和网络优化
 * 
 * @author CodeBuddy
 * @version 1.0
 */
@Component
public class M4TDLiveStreamOptimizer {
    
    /**
     * 检查是否为M4TD设备
     */
    public boolean isM4TDDevice(String deviceSn) {
        return deviceSn != null && deviceSn.startsWith("1581F");
    }
    
    /**
     * 为M4TD设备优化直播流URL配置
     */
    public ILivestreamUrl optimizeM4TDUrl(UrlTypeEnum urlType, ILivestreamUrl originalUrl) {
        switch (urlType) {
            case RTMP:
                return optimizeRtmpForM4TD((LivestreamRtmpUrl) originalUrl);
            case GB28181:
                return optimizeGb28181ForM4TD((LivestreamGb28181Url) originalUrl);
            case WHIP:
                return optimizeWhipForM4TD((LivestreamWhipUrl) originalUrl);
            case RTSP:
                return optimizeRtspForM4TD((LivestreamRtspUrl) originalUrl);
            default:
                return originalUrl;
        }
    }
    
    /**
     * 优化RTMP配置以减少网络拥塞
     */
    private LivestreamRtmpUrl optimizeRtmpForM4TD(LivestreamRtmpUrl rtmpUrl) {
        LivestreamRtmpUrl optimized = (LivestreamRtmpUrl) rtmpUrl.clone();
        // 为M4TD添加低延迟参数
        String originalUrl = optimized.getUrl();
        if (!originalUrl.contains("?")) {
            optimized.setUrl(originalUrl + "?live=1&buffer=0");
        } else {
            optimized.setUrl(originalUrl + "&live=1&buffer=0");
        }
        return optimized;
    }
    
    /**
     * 优化GB28181配置
     */
    private LivestreamGb28181Url optimizeGb28181ForM4TD(LivestreamGb28181Url gb28181Url) {
        LivestreamGb28181Url optimized = (LivestreamGb28181Url) gb28181Url.clone();
        // 为M4TD设备使用更稳定的端口配置
        if (optimized.getLocalPort() == null || optimized.getLocalPort() == 0) {
            optimized.setLocalPort(8116);
        }
        return optimized;
    }
    
    /**
     * 优化WHIP/WebRTC配置
     */
    private LivestreamWhipUrl optimizeWhipForM4TD(LivestreamWhipUrl whipUrl) {
        LivestreamWhipUrl optimized = (LivestreamWhipUrl) whipUrl.clone();
        // 为M4TD添加WebRTC优化参数
        String originalUrl = optimized.getUrl();
        if (!originalUrl.contains("?")) {
            optimized.setUrl(originalUrl + "?transport=tcp&timeout=5000");
        } else {
            optimized.setUrl(originalUrl + "&transport=tcp&timeout=5000");
        }
        return optimized;
    }
    
    /**
     * 优化RTSP配置
     */
    private LivestreamRtspUrl optimizeRtspForM4TD(LivestreamRtspUrl rtspUrl) {
        LivestreamRtspUrl optimized = (LivestreamRtspUrl) rtspUrl.clone();
        // 为M4TD设备设置合适的端口
        if (optimized.getPort() == null || optimized.getPort() == 0) {
            optimized.setPort(8554);
        }
        return optimized;
    }
    
    /**
     * 获取M4TD推荐的视频质量设置
     */
    public VideoQualityEnum getRecommendedQuality() {
        // M4TD建议使用自动质量以适应网络状况
        return VideoQualityEnum.AUTO;
    }
    
    /**
     * 检查M4TD设备是否支持指定的URL类型
     */
    public boolean isSupportedUrlType(UrlTypeEnum urlType) {
        switch (urlType) {
            case RTMP:
            case GB28181:
            case WHIP:
            case RTSP:
                return true;
            case AGORA:
                // M4TD可能对Agora支持有限
                return false;
            default:
                return false;
        }
    }
}