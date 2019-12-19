package com.DeviceTest;
import android.app.Dialog;
import android.content.Context;
import com.DeviceTest.R;
import android.widget.Button;
import android.view.View;
import com.DeviceTest.CopyTest;
import java.io.File;



/**
 * Created by zhh on 2017/4/10.
 */
public class AppUpdateProgressDialog extends Dialog {
    private CopyTest coop;
    private NumberProgressBar numberProgressBar;
    public AppUpdateProgressDialog(Context context) {
        super(context, R.style.Custom_Progress);
        initLayout();
    }
    public AppUpdateProgressDialog(Context context, int theme) {
        super(context, R.style.Custom_Progress);
        initLayout();
    }
    private void initLayout() {
        this.setContentView(R.layout.update_progress_layout);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        numberProgressBar = (NumberProgressBar) findViewById(R.id.number_progress);
        this.setCanceledOnTouchOutside(false);//点击dialog背景部分不消失
//        this.setCancelable(false);//dialog出现时，点击back键不消失
        Button button=(Button)findViewById(R.id.busss);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               
                dismiss();
            }
        });



    }
    public void setProgress(int progress) {
        numberProgressBar.setProgress(progress);
    }
}