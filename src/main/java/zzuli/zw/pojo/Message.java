package zzuli.zw.pojo;

import java.io.Serializable;

/**
 * @ClassName Message
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 03日 19:53
 * @Version: 1.0
 */
public class Message implements Serializable {
    private int id;
    private String content;
    private double fontSize;
    private String fontType;
    private String fontStyle;
    private String fontColor;

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", fontSize=" + fontSize +
                ", fontType='" + fontType + '\'' +
                ", fontStyle='" + fontStyle + '\'' +
                ", fontColor='" + fontColor + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getFontSize() {
        return fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontType() {
        return fontType;
    }

    public void setFontType(String fontType) {
        this.fontType = fontType;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }
}
