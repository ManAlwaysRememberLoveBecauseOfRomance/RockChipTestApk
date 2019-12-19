package com.DeviceTest;

import android.app.Activity;
import android.os.Bundle;
import com.DeviceTest.helper.ControlButtonUtil;
import android.view.View;
import android.widget.Button;
import android.graphics.Color;
import android.view.KeyEvent;
import android.util.Log;

public class TelecontrolActivity extends Activity implements View.OnKeyListener{
    private Button button1,button2,button3,button4,button5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telecontroltest);
        ControlButtonUtil.initControlButtonView(this);
        button1 =(Button) findViewById(R.id.button1);
        button2 =(Button) findViewById(R.id.button2);
        button3 =(Button) findViewById(R.id.button3);
        button4 =(Button) findViewById(R.id.button4);
        button5 =(Button) findViewById(R.id.button5);
        button1.setOnKeyListener(this);
        button2.setOnKeyListener(this);
        button3.setOnKeyListener(this);
        button4.setOnKeyListener(this);
        button5.setOnKeyListener(this);
    }


    public boolean dispatchKeyEvent(KeyEvent event) {   //back建返回无效
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	@Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (keyCode){
            case 66:
                button1.setBackgroundColor(Color.parseColor("#FFFF6F00"));
                break;
            case 21:
                button2.setBackgroundColor(Color.parseColor("#FFFF6F00"));
                break;
            case 22:
                button3.setBackgroundColor(Color.parseColor("#FFFF6F00"));
                break;
            case 19:
                button4.setBackgroundColor(Color.parseColor("#FFFF6F00"));
                break;
            case 20:
                button5.setBackgroundColor(Color.parseColor("#FFFF6F00"));
                break;
        }
        Log.d("Jessica", String.valueOf(keyCode));
        Log.d("Jessica2", String.valueOf(v.getId()));
        Log.d("Jessica3", String.valueOf(event.getAction()));
        return true;
    }
}
