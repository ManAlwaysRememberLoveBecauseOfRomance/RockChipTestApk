package com.DeviceTest;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;
import android.net.Uri;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import com.DeviceTest.helper.ControlButtonUtil;
import android.view.KeyEvent;


public class VideoageActivity extends Activity {
    public static ControlButtonUtil mControlButtonView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AssetFileDescriptor afd = null;
        //String uri = "android.resource://" + getPackageName() + "/" + R.raw.video;
        super.onCreate(savedInstanceState);
        getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.videoage);
        ControlButtonUtil.initControlButtonView(this);
        MediaPlayer mediaPlayer = new MediaPlayer();
        VideoView videoViewi =(VideoView) findViewById(R.id.videoView);
        //videoViewi.setVideoPath(uri);
        videoViewi.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + "/raw/video"));
        videoViewi.start();
        //videoViewi.setVideoPath(path); 
        //videoViewi.start();
        /*try {
            afd = getResources().getAssets().openFd("Davichi_T-ara_We_were_in_love.mp4");
            mediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }               
        */

    }
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

}