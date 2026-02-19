package com.dji.sample.manage.service.impl;

import com.dji.sample.manage.model.receiver.CapacityDeviceReceiver;
import com.dji.sample.manage.model.receiver.CapacityCameraReceiver;
import com.dji.sample.manage.model.receiver.CapacityVideoReceiver;
import com.dji.sample.manage.service.ICapacityCameraService;
import com.dji.sdk.cloudapi.livestream.DockLivestreamAbilityUpdate;
import com.dji.sdk.cloudapi.livestream.RcLivestreamAbilityUpdate;
import com.dji.sdk.cloudapi.livestream.VideoTypeEnum;
import com.dji.sdk.cloudapi.livestream.api.AbstractLivestreamService;
import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.dji.sdk.mqtt.state.TopicStateRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author sean
 * @version 1.7
 * @date 2023/7/6
 */
@Service
public class SDKLivestreamService extends AbstractLivestreamService {

    @Autowired
    private ICapacityCameraService capacityCameraService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void dockLivestreamAbilityUpdate(TopicStateRequest<DockLivestreamAbilityUpdate> request, MessageHeaders headers) {
        saveLiveCapacity(request.getData().getLiveCapacity().getDeviceList());
    }

    @Override
    public void rcLivestreamAbilityUpdate(TopicStateRequest<RcLivestreamAbilityUpdate> request, MessageHeaders headers) {
        saveLiveCapacity(request.getData().getLiveCapacity().getDeviceList());
    }

    private void saveLiveCapacity(Object data) {
        List<CapacityDeviceReceiver> devices = objectMapper.convertValue(
                data, new TypeReference<List<CapacityDeviceReceiver>>() {});
        for (CapacityDeviceReceiver capacityDeviceReceiver : devices) {
            // 检查设备是否有有效的相机列表
            if (capacityDeviceReceiver.getCameraList() != null && !capacityDeviceReceiver.getCameraList().isEmpty()) {
                capacityCameraService.saveCapacityCameraReceiverList(
                        capacityDeviceReceiver.getCameraList(), capacityDeviceReceiver.getSn());
            } else {
                // 对于M4TD等没有相机列表的设备，创建默认的相机配置
                createDefaultCameraCapacity(capacityDeviceReceiver.getSn());
            }
        }
    }
    
    /**
     * 为没有相机列表的设备（如M4TD）创建默认的相机配置
     * @param deviceSn 设备序列号
     */
    private void createDefaultCameraCapacity(String deviceSn) {
        // 检查设备型号，为M4TD创建默认配置
        if (deviceSn != null && (deviceSn.contains("1581F8HGX") || deviceSn.contains("M4TD"))) {
            // 创建M4TD的默认相机配置
            List<CapacityCameraReceiver> defaultCameras = createM4TDDefaultCameras();
            capacityCameraService.saveCapacityCameraReceiverList(defaultCameras, deviceSn);
        }
    }
    
    /**
     * 创建M4TD的默认相机配置
     * @return 默认相机配置列表
     */
    private List<CapacityCameraReceiver> createM4TDDefaultCameras() {
        CapacityCameraReceiver camera = new CapacityCameraReceiver();
        camera.setAvailableVideoNumber(1);
        camera.setCoexistVideoNumberMax(1);
        
        // 为M4TD设置正确的PayloadIndex (相机类型)
        try {
            camera.setCameraIndex(new PayloadIndex("165-0-7")); // M4TD主相机的正确索引
        } catch (Exception e) {
            // 如果PayloadIndex创建失败，跳过
            return new ArrayList<>();
        }
        
        // 创建默认视频配置
        CapacityVideoReceiver video = new CapacityVideoReceiver();
        video.setVideoIndex("normal-0");
        video.setVideoType(VideoTypeEnum.NORMAL);
        video.setSwitchableVideoTypes(Arrays.asList(VideoTypeEnum.NORMAL));
        
        camera.setVideoList(Arrays.asList(video));
        
        return Arrays.asList(camera);
    }
}
