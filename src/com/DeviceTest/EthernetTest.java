package com.DeviceTest;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.DeviceTest.helper.ControlButtonUtil;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.InetAddress;
import java.util.Enumeration;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import android.widget.Button;
import android.view.View;
import java.util.Calendar;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.Context;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.content.DialogInterface;
import android.view.KeyEvent;
import com.DeviceTest.helper.TestCase.RESULT;
import android.graphics.Color;

public class EthernetTest extends Activity {
    String  result=null;
    public Activity mActivity;
    private static final int MIN_CLICK_DELAY_TIME=5000;
    private long LastClickTime=0;
    private String TAG="GOD";
    public int progress=0;
    public int counts=0;
    private android.net.NetworkInfo.State state;
    private int type;
    private AppUpdateProgressDialog dialog=null;
    public static Intent resultIntent = new Intent();
    private Button passButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ethernettest);
         IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); //网络连接消息
        this.registerReceiver(receiver, filter);

       // ControlButtonUtil.initControlButtonView(this);
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream("/sys/class/net/eth0/address")));
            String ethernetMacAddress = input.readLine();
            TextView textView=(TextView)findViewById(R.id.test2);
            textView.setText(ethernetMacAddress);
            Log.d("Ethernet MAC Address: " ,ethernetMacAddress);
        } catch (IOException ex) {
            Log.e("ex: " , String.valueOf(ex));
        }
        Button button=(Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); //网络连接消息
        registerReceiver(receiver, filter);
        long currentTime=Calendar.getInstance().getTimeInMillis();
        if (MIN_CLICK_DELAY_TIME<currentTime-LastClickTime) {
            LastClickTime=currentTime;
            if (type == ConnectivityManager.TYPE_ETHERNET&&state== android.net.NetworkInfo.State.CONNECTED) {
                pinging();
            }else{
                Toast.makeText(EthernetTest.this,"The Net Cable Is Out",Toast.LENGTH_SHORT).show();
            }
        }
    }
});

        Button returnButton = (Button) findViewById(R.id.btn_return);
        returnButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
               EthernetTest.this.setResult(mActivity.RESULT_OK,resultIntent);
               finish();
           }
       });
        Button failedButton = (Button) findViewById(R.id.btn_Fail);
        failedButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
               EthernetTest.this.setResult(RESULT.NG.ordinal(),
                resultIntent);
               finish();
           }
       });
        Button skipButton = (Button)findViewById(R.id.btn_Skip);
        skipButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
               EthernetTest.this.setResult(mActivity.RESULT_OK,
                resultIntent);
               finish();
           }
       });
        passButton= (Button)findViewById(R.id.btn_Pass);
        passButton.setEnabled(false);
        passButton.setBackgroundColor(Color.parseColor("#ff888888"));  
        passButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            finish();
        }
    });  


    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    private String getLocalIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
            for (Enumeration<InetAddress> enumIpAddr = intf
                .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                InetAddress inetAddress = enumIpAddr.nextElement();
            if (!inetAddress.isLoopbackAddress()&& inetAddress instanceof Inet4Address) {
                return inetAddress.getHostAddress();
            }
        }
    }
}
catch (SocketException ex) {

}
return "0.0.0.0";
}
public void pinging(){
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
                String  address="www.baidu.com";
                Process process = null;
                process = Runtime.getRuntime().exec("ping -c 2 -w 100 " + address);
                InputStreamReader r = new InputStreamReader(process.getInputStream());
                LineNumberReader returnData = new LineNumberReader(r);
                String  returnMsg ="";
                String line = "";

                while ((line = returnData.readLine()) != null) {
                    returnMsg += line;
                    counts+=1;
                    progress=counts;
                    Message msg =myHandler.obtainMessage();
                    msg.what = 100;
                    msg.obj = progress;
                    myHandler.sendMessage(msg);
                    
                }
                Log.d("return", String.valueOf(returnMsg));
                


                if (returnMsg.indexOf("100% packet loss") != -1||returnMsg.equals("")) {
                    result="ERROR";

                } else {
                    result="SUCCESS";
                }


            } catch (IOException e) {
                result = "failed~ IOException";
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
                progress=0;
                counts=0;
                TextView textView1=(TextView)findViewById(R.id.te1);
                textView1.setText(result);
                if (result=="SUCCESS") {
                    passButton.setEnabled(true);
                    passButton.setBackgroundColor(Color.parseColor("#FFFF6F00"));
                }

            }
            break;
        }
    }
};







private BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction(); //得到广播意图
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) { //检查网络状态
                Log.i(TAG, "ConnectivityManager.CONNECTIVITY_ACTION ");
                NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                type = info.getType();
                NetworkInfo.State st;
                state = info.getState(); //得到此时的连接状态

                if(type == ConnectivityManager.TYPE_ETHERNET){
                    Log.i(TAG, "TYPE_ETHERNET ");
                    if(state == android.net.NetworkInfo.State.CONNECTED){   //判断网络状态
                        Log.e(TAG, "android.net.NetworkInfo.State.CONNECTED");
                        TextView textView5=(TextView)findViewById(R.id.test5);
                        textView5.setText("CONNECTED SUCCESS");
                        textView5.setTextColor(context.getResources().getColor(R.color.orages));
                        TextView textView4= (TextView) findViewById(R.id.test4);
                        textView4.setText(getLocalIp());
                    } else {
                        Log.e(TAG, "android.net.NetworkInfo.State.DISCONNECTED");
                    }
                }else{
                 TextView textView5=(TextView)findViewById(R.id.test5);
                 textView5.setText("CONNECTED ERROR");
             }
         }else{
             TextView textView5=(TextView)findViewById(R.id.test5);
             textView5.setText("CONNECTED ERROR");
         }
          //注册中加注销不可，无休眠，退出什么的。

     }
 };








}
