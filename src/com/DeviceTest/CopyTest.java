package com.DeviceTest;
import java.io.File;
import java.io.FileInputStream;
import android.util.Log;
import java.io.IOException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.app.Activity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;
import com.DeviceTest.DeviceTest;
import com.DeviceTest.R;
import com.DeviceTest.R.id;
import com.DeviceTest.helper.TestCase.RESULT;
import com.DeviceTest.helper.ControlButtonUtil;
import android.text.TextWatcher;
import android.widget.Button;
import android.view.View;
import android.app.NotificationManager;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.view.KeyEvent;
import android.os.Handler;
import android.os.Message;
import com.DeviceTest.AppUpdateProgressDialog;
import java.util.Calendar;
import android.widget.Toast;
import android.graphics.Color;
public class CopyTest extends Activity {
   public Activity mActivity;
   public static ControlButtonUtil mControlButtonView;
   public static NotificationManager mNotificationManager;
   public static Intent resultIntent = new Intent();
   private ProgressBar progressBar;
   public int progress=0;
   long copySizes = 0;
   long mum=314368736L;
   public Button button;
   BigInteger bi = null;
   private AlertDialog.Builder builder;
   private AlertDialog.Builder builder2;
   private AppUpdateProgressDialog dialog=null;
   private static final int MIN_CLICK_DELAY_TIME=5000;
   private long LastClickTime=0;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
   // getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_SHOW_FULLSCREEN);
    setContentView(R.layout.cooo);
    button=(Button)findViewById(R.id.button);
    Button button1=(Button)findViewById(R.id.button1);    
    TextView textView1= (TextView) findViewById(R.id.text1);
    textView1.setText("314,368,736B");
    ControlButtonUtil.initControlButtonView(this);
    button1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            long currentTime=Calendar.getInstance().getTimeInMillis();
            if (MIN_CLICK_DELAY_TIME<currentTime-LastClickTime) {
                LastClickTime=currentTime;
                getFolderSize(new File("/sdcard/encrypted"));
            }
            
        }
    });
    button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(progress==0){
                String newPath="/sdcard/encrypted";
                String oldPath="/mnt/media_rw/D4F1-3571/encrypted";
                File oldFile = new File(oldPath);
                File newFile = new File(newPath);
                if (newFile.exists()) {
                    deleteFile(new File(newPath));
                }else if (oldFile.exists()) {
                    showDialog();
                }else{
                    Toast.makeText(CopyTest.this,"The USB Is Out",Toast.LENGTH_SHORT).show();
                } 
            }
            else{
                dialog.show();
            }
        }   
    });
}

@Override
protected void onDestroy() {
    super.onDestroy();
    myHandler.removeCallbacksAndMessages(null);
}






public long getFolderSize(java.io.File file) {
    long size = 0;
    try {
        java.io.File[] fileList = file.listFiles();
        for (File fss : fileList) {
            if (fss.isDirectory()) {
                size = size + getFolderSize(fss);

            } else {
                size = size + fss.length();
                Log.d("SIZEEEEEEE",String.valueOf(size));
            }
        }
        TextView textView2= (TextView) findViewById(R.id.text2);
        textView2.setText(String.valueOf(size)+"B");
        Button passButton=(Button)findViewById(R.id.btn_Pass);
        if(size!=mum){
        }else{
            findViewById(R.id.btn_Pass).setEnabled(true);
            findViewById(R.id.btn_Pass).setBackgroundColor(Color.parseColor("#FFFF6F00"));
        }  
    } catch (Exception e) {
            // TODO Auto-generated catch block
        e.printStackTrace();
    }
    return size;
}



public void showDialog(){

    dialog = new AppUpdateProgressDialog(this);
        //正在下载时，不可关闭dialog
    dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                return true;
            } else {
                return false;
            }
        }
    });
    dialog.show();
    new Thread(new Runnable(){
     @Override
     public void run() {
        while(progress<100){
            try {

                String newPath="/sdcard/encrypted";
                String oldPath="/mnt/media_rw/D4F1-3571/encrypted";
                File newFile = new File(newPath);
                File oldFile = new File(oldPath);
                if (!newFile.exists()) {
                    if (!newFile.mkdirs()) {
                        Log.e("--Method--", "copyFolder: cannot create directory.");
                    }else{
                        Log.e("CREAT","create success");
                    }
                }
                String[] files = oldFile.list();
                File temp = null;
                assert files != null;
                long allcount = oldFile.length();
                for (String file : files) {                //下过程中拔出U盘，出现NullPoint
                    if (oldFile.exists()) {
                        if (oldPath.endsWith(File.separator)) {
                            temp = new File(oldPath + file);
                        } else {
                            temp = new File(oldPath + File.separator + file);
                        }
                if (temp.isDirectory()) {   //如果是子文件夹
                    copyFolder(oldPath + "/" + file, newPath + "/" + file);
                } else if (!temp.exists()) {

                    Log.e("--Method--", "copyFolder:  oldFile not exist.");
                } else if (!temp.isFile()) {
                    Log.e("--Method--", "copyFolder:  oldFile not file.");
                } else if (!temp.canRead()) {
                    Log.e("--Method--", "copyFolder:  oldFile cannot read.");
                } else {
                    FileInputStream fileInputStream = new FileInputStream(temp);
                    FileOutputStream fileOutputStream = new FileOutputStream(newPath + "/" + temp.getName());
                    byte[] buffer = new byte[4096];
                    int byteRead=0;
                    while ((byteRead = fileInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, byteRead);
                        copySizes += byteRead;
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    fileInputStream.close();
                }
                Log.d("SIZEZZZZZZ",String.valueOf(copySizes));
                progress=(int) (copySizes*100.0/mum);
                Message msg =myHandler.obtainMessage();
                msg.what = 100;
                msg.obj = progress;
                myHandler.sendMessage(msg);
            }else{
                CopyTest.this.setResult(RESULT.NG.ordinal(),resultIntent);
                finish();
            }
            
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}

}
}).start();
    
}
Handler myHandler = new Handler() {
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 100:
            int progre =(int) msg.obj;
            dialog.setProgress(progre);
            if (100 == progre) {
                dialog.dismiss();
                progre=0;
                progress=0;
                copySizes=0;


            }
            break;
        }
    }
};
public void copyFolder(String oldPath,String newPath){
    try{
        File newFile = new File(newPath);
        if (!newFile.exists()) {
            if (!newFile.mkdirs()) {
                Log.e("--Method--", "copyFolder: cannot create directory.");
            }
        }
        File oldFile = new File(oldPath);
        String[] files = oldFile.list();
        File temp = null;
        assert files != null;
        long allcount = oldFile.length();
        for (String file : files) {
            if (oldPath.endsWith(File.separator)) {
                temp = new File(oldPath + file);
            } else {
                temp = new File(oldPath + File.separator + file);
            }
                if (temp.isDirectory()) {   //如果是子文件夹
                    copyFolder(oldPath + "/" + file, newPath + "/" + file);
                } else if (!temp.exists()) {
                    Log.e("--Method--", "copyFolder:  oldFile not exist.");
                } else if (!temp.isFile()) {
                    Log.e("--Method--", "copyFolder:  oldFile not file.");
                } else if (!temp.canRead()) {
                    Log.e("--Method--", "copyFolder:  oldFile cannot read.");
                } else {
                    FileInputStream fileInputStream = new FileInputStream(temp);
                    FileOutputStream fileOutputStream = new FileOutputStream(newPath + "/" + temp.getName());
                    byte[] buffer = new byte[2048];
                    int byteRead;
                    while ((byteRead = fileInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, byteRead);
                        copySizes += byteRead;
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    fileInputStream.close();
                }
                Log.d("SIZEZZZZZZ",String.valueOf(copySizes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public  void deleteFile(File file) {
        if (file.isDirectory()) {        
            File[] files = file.listFiles();        
            for (int i = 0; i < files.length; i++) {            
                File f = files[i];            
                deleteFile(f);        
            }        
            file.delete();
        }
        else if (file.exists()) {        
            file.delete();  
        }
        

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}