package com.DeviceTest;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.content.ComponentName;

public class ThomasromActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);            
        ComponentName cn = new ComponentName("com.android.gl2jni", "com.android.gl2jni.Main");            
        intent.setComponent(cn);
        startActivity(intent);
    }

}
