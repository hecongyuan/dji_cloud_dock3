package com.dji.sdk.config.version;

import com.dji.sdk.exception.CloudSDKVersionException;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * @author sean
 * @version 1.7
 * @date 2023/9/7
 */
public enum Dock3ThingVersionEnum implements IThingVersion {

    V1_1_2("1.1.2", CloudSDKVersionEnum.V1_0_1),

    V1_2_0("1.2.0", CloudSDKVersionEnum.V1_0_3),
    V1_2_3("1.2.3", CloudSDKVersionEnum.V1_0_3),
    V1_2_5("1.2.5", CloudSDKVersionEnum.V1_0_3),

    ;

    private final String thingVersion;

    private final CloudSDKVersionEnum cloudSDKVersion;

    Dock3ThingVersionEnum(String thingVersion, CloudSDKVersionEnum cloudSDKVersion) {
        this.thingVersion = thingVersion;
        this.cloudSDKVersion = cloudSDKVersion;
    }

    @JsonValue
    public String getThingVersion() {
        return thingVersion;
    }

    public CloudSDKVersionEnum getCloudSDKVersion() {
        return cloudSDKVersion;
    }

    public static Dock3ThingVersionEnum find(String thingVersion) {
        return Arrays.stream(values()).filter(thingVersionEnum -> thingVersionEnum.thingVersion.equals(thingVersion))
                .findAny().orElseThrow(() -> new CloudSDKVersionException(thingVersion));
    }
}
