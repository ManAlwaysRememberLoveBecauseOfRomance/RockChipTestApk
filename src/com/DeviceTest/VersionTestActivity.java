package com.DeviceTest;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemProperties;
import android.view.KeyEvent;
import android.widget.TextView;
import android.provider.Settings;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import android.view.View;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.DeviceTest.helper.ControlButtonUtil;



import android.graphics.Color;



public class VersionTestActivity extends Activity
{
  private String getFormattedKernelVersion() {
      String procVersionStr;

      try {
          BufferedReader reader = new BufferedReader(new FileReader("/proc/version"), 256);
          try {
              procVersionStr = reader.readLine();
          } finally {
              reader.close();
          }

          final String PROC_VERSION_REGEX =
              "\\w+\\s+" + /* ignore: Linux */
              "\\w+\\s+" + /* ignore: version */
              "([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
              "\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /* group 2: (xxxxxx@xxxxx.constant) */
              "\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+" + /* ignore: (gcc ..) */
              "([^\\s]+)\\s+" + /* group 3: #26 */
              "(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
              "(.+)"; /* group 4: date */

          Pattern p = Pattern.compile(PROC_VERSION_REGEX);
          Matcher m = p.matcher(procVersionStr);

          if (!m.matches()) {

              return "Unavailable";
          } else if (m.groupCount() < 4) {

              return "Unavailable";
          } else {
              return (new StringBuilder(m.group(1)).append("\n").append(
                      m.group(2)).append(" ").append(m.group(3)).append("\n")
                      .append(m.group(4))).toString();
          }
      } catch (IOException e) {
 

          return "Unavailable";
          }
  }
  
  private String ssssss;
  
  @Override
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);

    
    setContentView(R.layout.versiontest);
    ssssss= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    String serialNumber = android.os.Build.SERIAL; 
    
    TextView firmwareTextView = (TextView)findViewById(R.id.TextFirmwareversion);
    firmwareTextView.setText(Build.VERSION.RELEASE);
    
    TextView kernelTextView = (TextView)findViewById(R.id.TextKernelversion);
    kernelTextView.setText(getFormattedKernelVersion());
    

    
    TextView buildTextView = (TextView)findViewById(R.id.TextBuildversion);
    buildTextView.setText(Build.DISPLAY);
    
    TextView localTextView5 = (TextView)findViewById(R.id.TextBasebandversion);
    String str4 = SystemProperties.get("gsm.version.baseband", "Unavailable");
    localTextView5.setText(str4);



    TextView test1=(TextView)findViewById(R.id.xu1);
    test1.setText(serialNumber);
    TextView test2=(TextView)findViewById(R.id.xu2);
    test2.setText(ssssss);

    TextView test3 = (TextView) findViewById(R.id.Deviceversion);
    test3.setText(Build.DEVICE);
    
//    checkFlashSize();
    
    ControlButtonUtil.initControlButtonView(this);
    /*if (ssssss!=null) {

      findViewById(R.id.btn_Pass).setEnabled(true);
      findViewById(R.id.btn_Pass).setBackgroundColor(Color.parseColor("#FFFF6F00"));
      
    }*/
    
  }
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}
  
  
/* Flash test Used for mid
 * 
  public void checkFlashSize() {
      TextView flashsizeView = (TextView)findViewById(R.id.FlashSize);
      
      String path = getDirectory("FLASH_STORAGE", "/flash").getPath();
      StatFs stat = new StatFs(path);
      long blockSize = stat.getBlockSize();        
      long availableBlocks = stat.getAvailableBlocks();
      long availableSize = (availableBlocks * blockSize)/(1024 * 1024); //MBtye
      long freeBlocks = stat.getFreeBlocks();
      long freeSize = (freeBlocks * blockSize)/(1024 * 1024); //MBtye
      
      flashsizeView.setText(String.valueOf(availableSize)+" MB");
  }
  
  static File getDirectory(String variableName, String defaultPath) {
      String path = System.getenv(variableName);
      return path == null ? new File(defaultPath) : new File(path);
  }
  
  */
  
}
