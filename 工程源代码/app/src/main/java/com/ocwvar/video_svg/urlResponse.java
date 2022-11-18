package com.ocwvar.video_svg;
import com.google.gson.annotations.SerializedName;
public class urlResponse {
    @SerializedName("code")//返回的code 正常为200
    public int code;
    @SerializedName("msg")//返回的msg
    public String msg;
    @SerializedName("url")//解析后的无水印视频链接地址
    public String url;
    @SerializedName("cover")//视频封面
    public String cover;
    @SerializedName("title")//视频标题
    public String title;
}
