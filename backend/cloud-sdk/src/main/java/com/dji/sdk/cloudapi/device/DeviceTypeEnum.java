package com.dji.sdk.cloudapi.device;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * @author sean
 * @version 1.7
 * @date 2023/5/26
 */
@Schema(description = "device type", enumAsRef = true)
public enum DeviceTypeEnum {

    M350_RTK(89),
    M300_RTK(60),
    M30_SERIES(67),
    RC_PLUS(119),
    DOCK(1),
    DOCK2(2),
    DOCK3(3),
    H20(42),
    H20T(43),
    Z30(20),
    XT2(26),
    XT_S(90742),
    L1(90742),
    P1(50),
    M30_CAMERA(52),
    M30T_CAMERA(53),
    H20N(61),
    DOCK_CAMERA(165),
    M3E(77),

    M4T(99),
    M4D(100),
    M4D_CAMERA(98),
    M4TD_CAMERA(99),
    M3E_CAMERA(66),
    M3M_CAMERA(68),
    RC(56),
    RC_PRO(144),
    M3D(91),
    M3D_CAMERA(80),
    M3TD_CAMERA(81),
    M4T_CAMERA(89),
    XTS(41),
    FPV(39),
    UNKNOWN(-1),
    M400(400), // Matrice 4
    RC_PLUS_2(174), // RC Plus 2
    ;

    private final int type;

    DeviceTypeEnum(int type) {
        this.type = type;
    }

    @JsonValue
    public int getType() {
        return type;
    }

    @JsonCreator
    public static DeviceTypeEnum find(int type) {
        return Arrays.stream(values()).filter(typeEnum -> typeEnum.type == type).findAny()
                .orElse(UNKNOWN);
    }
}
