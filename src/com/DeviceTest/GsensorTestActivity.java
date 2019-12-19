package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.view.GsensorBall;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author LanBinYuan
 * @date 2011-06-11
 * 
 */

public class GsensorTestActivity extends Activity {
	/** Called when the activity is first created. */
	private final static int MAX_NUM = 8;
	private SensorManager sensorManager;
	private SensorEventListener lsn = null;
	boolean stop = false;

	private static enum TEST_AXIS {
		X, Y, Z, D
	};

	private TEST_AXIS testAxis;
	private GsensorBall mGsensorBall;
	private Button save_btn;
	private double x_offset=0;
	private double y_offset=0;
	private double z_offset=0;
	
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.gsensortest);
		stop = false;
		setTitle(getTitle() + "----("
				+ getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS) + ")");
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);

		ControlButtonUtil.initControlButtonView(this);
		sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		//mGsensorBall = (GsensorBall)findViewById(R.id.gsensorball);
		setTestAxis(TEST_AXIS.X);
//		findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);
		  save_btn = (Button) findViewById(R.id.save_calibration_button);
	}

	TextView X_textView, Y_textView, Z_textView;

	
	protected void onResume() {
		super.onResume();

		stop = false;
		final TextView subTitle = (TextView) findViewById(R.id.Accelerometer);
		subTitle.setTextColor(Color.rgb(255, 0, 0));

		X_textView = (TextView) findViewById(R.id.gsensorTestX);
		X_textView.setTextColor(android.graphics.Color.GREEN);

		Y_textView = (TextView) findViewById(R.id.gsensorTestY);
		Y_textView.setTextColor(android.graphics.Color.GREEN);

		Z_textView = (TextView) findViewById(R.id.gsensorTestZ);
		Z_textView.setTextColor(android.graphics.Color.GREEN);


		lsn = new SensorEventListener() {
			public void onAccuracyChanged(Sensor sensor, int accuracy) {

			}

			public void onSensorChanged(SensorEvent e) {
				if(stop) {
					return;
				}
				subTitle.setText("x:" + (double) e.values[0] + ", \ny:"
						+ (double) e.values[1] + ",\nz:" + (double) e.values[2]);
				x_offset=e.values[0]-0;
				y_offset=e.values[1]-0;
				z_offset=e.values[2]-9.8;
				doTest(e);
				//mGsensorBall.setXYZ(e.values[0], e.values[1], e.values[2]);
			}

		};

		Sensor sensors = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		sensorManager.registerListener(lsn, sensors,
				SensorManager.SENSOR_DELAY_NORMAL);

		save_btn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				File savefile = new File("/cache/Gsensor.txt");
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

	private void doTest(SensorEvent e) {
		switch (testAxis) {
		case X:
			if ((int) e.values[0] >= MAX_NUM && (int) e.values[1] == 0
					&& (int) e.values[2] == 0) {
				setTestAxis(TEST_AXIS.Y);
				X_textView.setText(X_textView.getText() + ":Pass");
			}
			break;
		case Y:
			if ((int) e.values[0] == 0 && (int) e.values[1] >= MAX_NUM
					&& (int) e.values[2] == 0) {
				setTestAxis(TEST_AXIS.Z);
				Y_textView.setText(Y_textView.getText() + ":Pass");
			}
			break;
		case Z:
			if ((int) e.values[0] == 0 && (int) e.values[1] == 0
					&& (int) e.values[2] >= MAX_NUM) {
				setTestAxis(TEST_AXIS.D);
				Z_textView.setText(Z_textView.getText() + ":Pass");
//				findViewById(R.id.btn_Pass).performClick();
			}
			break;
		default:
			break;
		}
	}

	private void setTestAxis(TEST_AXIS testAxis) {
		this.testAxis = testAxis;
		switch (testAxis) {
		case X:
			findViewById(R.id.gsensorTestX).setVisibility(View.VISIBLE);
			break;
		case Y:
			findViewById(R.id.gsensorTestY).setVisibility(View.VISIBLE);
			break;
		case Z:
			findViewById(R.id.gsensorTestZ).setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	//
	
	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(lsn);
		stop = true;
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}
}
