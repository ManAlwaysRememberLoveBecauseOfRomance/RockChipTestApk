package com.DeviceTest;
import android.view.Window;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.widget.TextView;
import com.DeviceTest.helper.ControlButtonUtil;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import android.view.View;
import android.app.PendingIntent;
import android.content.IntentFilter;
import android.graphics.Color;

public class NfcTestActivity extends Activity {
    NfcAdapter nfcAdapter;
    TextView textView;
    private String metaInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);
       // getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_SHOW_FULLSCREEN);
        setContentView(R.layout.nfctest);
        ControlButtonUtil.initControlButtonView(this);
        //findViewById(R.id.btn_Pass).setBackgroundDrawable(getResources().getDrawable(R.drawable.gray));
        textView = (TextView)findViewById(R.id.promt);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            textView.setText("UnSupport");
            Log.d("jessica","UnSupporrt");

            return;
        }
        if (!nfcAdapter.isEnabled()) {
            textView.setText("Please Open The Nfc");
            Log.d("jessica","Not Open");

            return;
        }
    }
 /*   @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null&&nfcAdapter.isEnabled()) {
           PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
            getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null); 
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
            || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag iTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        textView.setText(TagReader.readTag(iTag, intent));
        if (iTag!=null) {
            findViewById(R.id.btn_Pass).setEnabled(true);
            findViewById(R.id.btn_Pass).setBackgroundColor(Color.parseColor("#FFFF6F00"));
        }
    }
}



}