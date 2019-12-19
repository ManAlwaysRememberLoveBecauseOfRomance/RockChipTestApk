package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.DeviceTest.helper.ControlButtonUtil;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.DeviceTest.view.*;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
/**
 * @author LanBinYuan
 * @date 2011-06-11
 * 
 */

public class GyroscopeTestActivity extends Activity {
	/** Called when the activity is first created. */
	private SensorManager sensorManager;
	private SensorEventListener lsn = null;
	private TouchSurfaceView mGLSurfaceView;
    private Button save_btn;
	private double x_offset=0;
	private double y_offset=0;
	private double z_offset=0;

	private float x;
	  private float y;
	  private float z = 0.0F;
	  int i = 0;
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		

		setTitle(getTitle() + "----("
				+ getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS) + ")");
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);
		
		setContentView(R.layout.gyroscopetest);
		ControlButtonUtil.initControlButtonView(this);
		sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		this.mGLSurfaceView = new TouchSurfaceView(this);
		//setContentView(this.mGLSurfaceView);
	    
	   // AdView localAdView = new AdView(this);
	    //localAdView.setVerticalGravity(80);
	   // localRelativeLayout.addView(localAdView, localLayoutParams);
//	    addContentView(localRelativeLayout, new WindowManager.LayoutParams(-1, -2));
	    RelativeLayout layout2 = (RelativeLayout)findViewById(R.id.relativeLayout2);
	    RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
	    params2.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    layout2.setGravity(Gravity.BOTTOM);
	    layout2.addView(mGLSurfaceView, params2);
	   // LayoutInflater.from(this).inflate(R.xml.control_buttons, get);
	    this.mGLSurfaceView.requestFocus();
	    this.mGLSurfaceView.setFocusableInTouchMode(true);
	    save_btn = (Button) findViewById(R.id.save_calibration_button);
	}
	

    @Override
    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        mGLSurfaceView.onPause();
        this.sensorManager.unregisterListener(lsn);
    }

	
	protected void onResume() {
		super.onResume();

		lsn = new SensorEventListener() {
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub

			}

			public void onSensorChanged(SensorEvent e) {
				TextView subTitle = (TextView) findViewById(R.id.Gyroscope);
				subTitle.setTextColor(Color.rgb(255, 0, 0));

				String info = " 	name:" + String.valueOf(e.sensor.getName());
				info += "\n";

				info += " 	vendor:" + String.valueOf(e.sensor.getVendor());
				info += "\n";
				info += " 	version:" + String.valueOf(e.sensor.getVersion());
				info += "\n";
				info += " 	maxRange:"
						+ String.valueOf(e.sensor.getMaximumRange());
				info += "\n";
				info += " 	resolution:"
						+ String.valueOf(e.sensor.getResolution());
				info += "\n";
				info += " 	power:" + String.valueOf(e.sensor.getPower());

				TextView infoView = (TextView) findViewById(R.id.magnetic_info);
				infoView.setText(info);
				// --------------

				TextView text = (TextView) findViewById(R.id.magnetic_x);
				text.setText(" 	x:"
						+ String.valueOf(e.values[SensorManager.DATA_X]));
				text.setTextColor(android.graphics.Color.GREEN);

				TextView text2 = (TextView) findViewById(R.id.magnetic_y);
				text2.setText(" 	y:"
						+ String.valueOf(e.values[SensorManager.DATA_Y]));
				text2.setTextColor(android.graphics.Color.GREEN);

				TextView text3 = (TextView) findViewById(R.id.magnetic_z);
				text3.setText(" 	z:"
						+ String.valueOf(e.values[SensorManager.DATA_Z]));
				text3.setTextColor(android.graphics.Color.GREEN);
				
			    x += e.values[0];
			    y += e.values[1];
			    z += e.values[2];
			    mGLSurfaceView.updateGyro(x, y, z);
			   
			    int j = i;
			    i = (j + 1);
			    if (j % 50 != 0)
			      return;
			    Log.i("gyro", "x=" + e.values[0] + " y=" +e.values[1] + " z=" + e.values[2]);
			    x_offset=e.values[0]-0;
				y_offset=e.values[1]-0;
				z_offset=e.values[2]-0;
			}

		};

		Sensor sensors = sensorManager
				.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

		sensorManager.registerListener(lsn, sensors,
				SensorManager.SENSOR_DELAY_NORMAL);
		this.mGLSurfaceView.onResume();
save_btn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				File savefile = new File("/cache/Gyroscope.txt");
				if (savefile.exists()) {
					savefile.delete();
				}
				if (!savefile.exists())
				{
					try
					{
						savefile.createNewFile();
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				StringBuilder sb = new StringBuilder();
				sb.append("x_offset:" + x_offset).append("\n").append("y_offset:" + y_offset).append("\n").append("z_offset:" + x_offset);
				String sbStirng = sb.toString();
				FileOutputStream fos = null;
				try
				{
					fos = new FileOutputStream(savefile, false);
					fos.write(sbStirng.getBytes());
					fos.close();
				} catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally
				{
					if (fos != null)
					{
						try
						{
							fos.close();
						} catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
				Toast.makeText(getApplicationContext(), getString(R.string.save_isok), Toast.LENGTH_SHORT).show();
			}
		});

	}

	//
	
	protected void onStop() {
		super.onStop();
		sensorManager.unregisterListener(lsn);
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}
}
