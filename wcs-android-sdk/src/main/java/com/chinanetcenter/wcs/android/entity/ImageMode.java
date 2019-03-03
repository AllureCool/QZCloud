package com.chinanetcenter.wcs.android.entity;

public enum ImageMode {

    /**
     * 限定缩略图的宽最少为<width>，高最少为<height>，进行等比缩放，居中裁剪。 转后的缩略图通常恰好是 <width>x<height>
     * 的大小（有一个边缩放的时候会因为超出矩形框而被裁剪掉多余部分）。 如果只指定 width 参数或只指定 height
     * 参数，代表限定为长宽相等的正方图。
     */
    MODE1("1"),
    /**
     * 限定缩略图的宽度最多为<width>，高度最多为< height>，进行等比缩放，不裁剪。
     * 如果只指定width参数则表示限定宽度（高度自适应），只指定 height 参数则表示限定高度（宽度自适应）。
     */
    MODE2("2"),
    /**
     * 限定缩略图的宽最少为<width>，高最少为<height>，进行等比缩放，不裁剪。
     */
    MODE3("3");

    ImageMode(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
