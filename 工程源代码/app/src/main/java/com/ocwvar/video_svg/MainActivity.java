package com.ocwvar.video_svg;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import de.greenrobot.event.EventBus;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.VideoEvents;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    JCVideoPlayer videoController1;
    EditText editText,videoText;
    Button btnToFullscreen, btnJiexi, btnPlay, btnDownLoad;
    String url="";  //视频播放链接
    String title="";    //标题
    String cover="";    //封面图片链接
    ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        Retrofit retrofit = new Retrofit.Builder()  //用于调用抖音视频解析链接
                .baseUrl("http://api.xcrobot.top/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
//http://api.xcrobot.top/dyjx/dzy.mp4
        apiService = retrofit.create(ApiService.class);
        videoController1 = (JCVideoPlayer) findViewById(R.id.videocontroller1);
        videoController1.setUp(url, cover, title);//初始化视频播放器设置
        videoText= (EditText)findViewById(R.id.videoUrl);
        editText = (EditText)findViewById(R.id.edit);
        btnToFullscreen = (Button) findViewById(R.id.to_fullscreen);

        btnJiexi=(Button) findViewById(R.id.search);
        btnPlay=(Button) findViewById(R.id.play);
        btnDownLoad=(Button) findViewById(R.id.downLoad);
        btnToFullscreen.setOnClickListener(this);
        btnJiexi.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnDownLoad.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    public void downFile(String name, String urlString) {   //下载视频文件到本地 name为文件名，urlString为下载视频地址
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download";
        DownloadUtil.get().download(urlString,path, name + ".mp4", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                Log.v("Down", "下载成功,位置:"+path+name+".mp4");
                Looper.prepare();
                Toast.makeText(MainActivity.this, "下载成功,位置:"+path+name+".mp4", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onDownloading(int progress) {
                Log.v("Down", "下载进度" + progress);
            }

            @Override
            public void onDownloadFailed(Exception e) {
                Looper.prepare();
                Toast.makeText(MainActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //https://v.douyin.com/YMnovA9/
            case R.id.to_fullscreen://进入全屏播放
                JCVideoPlayer.toFullscreenActivity(this, url, cover,title);
                break;
            case R.id.search://解析抖音视频无水印版本
                Log.d("text", "onClick: "+editText.getText());
                //发送解析请求 editText中的内容是用户输入的抖音分享链接
                apiService.getVideo(editText.getText().toString()).enqueue(new Callback<urlResponse>() {
                    @Override
                    public void onResponse(Call<urlResponse> call, Response<urlResponse> response) {
                        if (response.body() != null) {
                            if(response.body().code==200){//成功的情况
                                url=response.body().url;//视频链接
                                videoText.setText(url);//设置播放编辑框内容
                                cover=response.body().cover;//封面
                                title=response.body().title;//标题
                                videoController1.setUp(url, cover, title);//修改视频播放器参数

                                Toast.makeText(MainActivity.this, "解析成功", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MainActivity.this, "Error code:"+response.body().code+response.body().msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<urlResponse> call, Throwable t) {
                        Log.d("retrofit", t.getMessage());
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.downLoad:
                downFile(title,videoText.getText().toString());//下载到本地
                Toast.makeText(MainActivity.this, "开始下载", Toast.LENGTH_SHORT).show();

                break;
            case R.id.play:
                url=videoText.getText().toString();//播放编辑框的地址中的视频
                videoController1.setUp(url, cover, title);
                break;

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    public void onEventMainThread(VideoEvents event) {

    }
}
