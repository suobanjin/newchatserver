package zzuli.zw.domain;

import java.io.Serializable;

/**
 * @author 索半斤
 * @description 文字风格
 * @date 2022/1/16
 * @className TextStyle
 */
public class TextStyle implements Serializable {
    private int id;
    private double fontSize;
    private String fontColor;
    private String fontType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getFontSize() {
        return fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getFontType() {
        return fontType;
    }

    public void setFontType(String fontType) {
        this.fontType = fontType;
    }
}
