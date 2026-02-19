package com.dji.sample.manage.model.receiver;

import com.dji.sample.manage.model.deserializer.PayloadIndexDeserializer;
import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.List;

/**
 * @author sean.zhou
 * @date 2021/11/18
 * @version 0.1
 */
@Data
public class CapacityCameraReceiver {

    private Integer availableVideoNumber;

    private Integer coexistVideoNumberMax;

    @JsonDeserialize(using = PayloadIndexDeserializer.class)
    private PayloadIndex cameraIndex;

    private List<CapacityVideoReceiver> videoList;

}
