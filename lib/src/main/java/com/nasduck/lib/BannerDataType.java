package com.nasduck.lib;

public enum  BannerDataType {
    TYPE_SOURCE(0), TYPE_INTERENET(1);

    private int mType;

    BannerDataType(int type) {
        this.mType = type;
    }

    public int getType() {
        return mType;
    }
}
