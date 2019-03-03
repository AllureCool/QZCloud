package com.chinanetcenter.wcs.android.entity;

public class ImageOption {

    /**
     * 模式选择
     *
     * @see com.chinanetcenter.wcs.android.entity.ImageMode
     */
    private ImageMode mode = ImageMode.MODE1;

    /**
     * 高度
     */
    private String height;

    /**
     * 宽度
     */
    private String width;

    /**
     * 质量, 取值范围1-100, 默认85
     */
    private String quality;

    /**
     * 格式, jpg，gif，png，webp, 默认值为原图格式
     */
    private String format;

    public ImageMode getMode() {
        return mode;
    }

    public void setMode(ImageMode mode) {
        this.mode = mode;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
