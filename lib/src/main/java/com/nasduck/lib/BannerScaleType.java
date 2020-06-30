package com.nasduck.lib;

public enum BannerScaleType {

    CENTER(0),
    CENTER_CROP(1),
    CENTER_INSIDE(2),
    FIT_CENTER(3),
    FIT_END(4),
    FIT_START(5),
    FIT_XY(5),
    MATRIX(6);

    private int value;

    BannerScaleType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static BannerScaleType getType(int value) {
        for (BannerScaleType e : BannerScaleType.values()) {
            if (value == e.getValue()) {
                return e;
            }
        }
        return null;
    }
}
