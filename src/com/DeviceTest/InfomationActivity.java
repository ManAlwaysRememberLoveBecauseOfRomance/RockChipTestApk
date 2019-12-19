package com.DeviceTest;
import java.io.File;

import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.helper.SystemInfoUtil;
import android.content.DialogInterface.OnCancelListener;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.SystemProperties;
import android.os.storage.StorageEventListener;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import java.lang.reflect.Array;
import android.content.res.Resources;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public class InfomationActivity extends Activity implements OnCancelListener{
	private final static String TAG = "InfomationActivity";
	
	private TextView mCPUInfoTV;
	private TextView mMemoryInfoTV;
	private TextView mInternalAvailTV;
	private TextView mInternalAvailSizeTV;
	private TextView mNandSize;
    private TextView mNandAvail;
    private TextView mDataAvail;
	private Resources mRes;
	public  String flash_path = null;
	public  String sdcard_path = null;
	public  String usb_path = null;
	private int flash_pit = 0; 
	private int sdcard_pit = 1; 
	private int usb_pit = 2; 
	private StorageVolume[] storageVolumes = null;
    private StorageManager mStorageManager = null;
    private final static int MSG_OK=0;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.infomation);
		InitStorage();
		initRes();
		mNandSize = (TextView)findViewById(R.id.nand_total_space);
        mNandAvail = (TextView)findViewById(R.id.nand_available_space);
        mDataAvail = (TextView)findViewById(R.id.data_available_space);

		ControlButtonUtil.initControlButtonView(this);
		mRes = getResources();
		//Cancel automatically passing test.
		//handler.sendEmptyMessageDelayed(MSG_OK, 2500);
	}
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MSG_OK:
				((Button) findViewById(R.id.btn_Pass)).performClick();
			}
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		updateView();
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentFilter.addDataScheme("file");
        registerReceiver(mReceiver, intentFilter);
        if (getStoragePath(this,false)==null) {
        finish();
        }
        updateMemoryStatus(getStoragePath(this,false));
	}
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateMemoryStatus(getStoragePath(getApplication(),false));
        }
    };

	private void initRes() {
		mCPUInfoTV = (TextView) findViewById(R.id.cpuinfo_tv);
		mMemoryInfoTV = (TextView) findViewById(R.id.meminfo_tv);
		//mInternalAvailTV  = (TextView) findViewById(R.id.internal_avail_tv);
		//mInternalAvailSizeTV    = (TextView) findViewById(R.id.internal_avail_size_tv);
	}
	
	private String getCpuInfoString() {
		StringBuilder cpuInfoSB = new StringBuilder(); 
		/*cpuInfoSB.append("processor  :  ");*/
		/*cpuInfoSB.append(SystemInfoUtil.getCpuName()).append("  ").append("\n");*/
		cpuInfoSB.append(SystemInfoUtil.getNumCores()+"").append("cores").append(" * ").append(""+SystemInfoUtil.getMaxCpuFreq()+" Hz");
		return cpuInfoSB.toString();
	}
	
	private void updateView() {
		mCPUInfoTV.setText(getCpuInfoString());
		mMemoryInfoTV.setText(SystemInfoUtil.GetMemInfo1(this));
		//updateMemoryStatus(flash_path);
		   File dataPath = Environment.getDataDirectory();
        StatFs stat = new StatFs(dataPath.getPath());
        long blockSize = stat.getBlockSize();
		long blockCount = stat.getBlockCount();
		
       // long availableBlocks = stat.getAvailableBlocks();
        //mInternalAvailSizeTV.setText(formatSize(blockCount * blockSize));
		//Log.d(TAG,"formatSize(availableBlocks * blockSize)="+formatSize(availableBlocks * blockSize));
	}
	
	
	private void updateMemoryStatus(String path) {
        String status = SystemProperties.get("EXTERNAL_STORAGE_STATE","unmounted");
        if (flash_path!=null&&path.equals(flash_path)) {
            status = mStorageManager.getVolumeState(path);
        }
        String readOnly = "";
        if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            status = Environment.MEDIA_MOUNTED;
            readOnly = getString(R.string.read_only);
        }
 
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                //File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path);
                long blockSize = stat.getBlockSize();
                long totalBlocks = stat.getBlockCount();
                long availableBlocks = stat.getAvailableBlocks();

                if (path.equals(getStoragePath(this,false))){
                    mNandSize.setText(formatSize(totalBlocks * blockSize));
                    mNandAvail.setText(formatSize(availableBlocks * blockSize) + readOnly);
                }
            } catch (IllegalArgumentException e) {
                // this can occur if the SD card is removed, but we haven't received the 
                // ACTION_MEDIA_REMOVED Intent yet.
                status = Environment.MEDIA_REMOVED;
            }
            
        } else {
            if (flash_path!=null&&path.equals(flash_path)){
            	mNandSize.setText(mRes.getString(R.string.nand_unavailable));
                mNandAvail.setText(mRes.getString(R.string.nand_unavailable));
                if (status.equals(Environment.MEDIA_UNMOUNTED) ||
                    status.equals(Environment.MEDIA_NOFS) ||
                    status.equals(Environment.MEDIA_UNMOUNTABLE) ) {
                }
            }
        }

        File dataPath = Environment.getDataDirectory();
        StatFs stat = new StatFs(dataPath.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
       // mInternalAvailTV.setText(getString(R.string.available_space)+":"+formatSize(availableBlocks * blockSize));
		Log.d(TAG,"formatSize(availableBlocks * blockSize)="+formatSize(availableBlocks * blockSize));
    }
    
    private String formatSize(long size) {
        return Formatter.formatFileSize(this, size);
    }

    public void onCancel(DialogInterface dialog) {
        finish();
    }
    
	private void InitStorage(){
		if (mStorageManager == null) {
            mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
            mStorageManager.registerListener(mStorageListener);
            storageVolumes = mStorageManager.getVolumeList();
            Log.e(TAG,"storageVolumes.length:"+storageVolumes.length);
            if(storageVolumes.length >= 3){
            	flash_path = storageVolumes[flash_pit].getPath();
            	sdcard_path = storageVolumes[sdcard_pit].getPath();
            	usb_path = storageVolumes[usb_pit].getPath();
            	Log.d(TAG, " _____ " + flash_path + "   " + sdcard_path + "   " + usb_path);
            }
        }
	}
	
    StorageEventListener mStorageListener = new StorageEventListener() {

        @Override
        public void onStorageStateChanged(String path, String oldState, String newState) {
            Log.d(TAG, "Received storage state changed notification that " +
                    path + " changed state from " + oldState +
                    " to " + newState);
            if (path.equals(sdcard_path) && !newState.equals(Environment.MEDIA_MOUNTED)) {
            } else {
                updateMemoryStatus(flash_path);
                updateMemoryStatus(getStoragePath(getApplication(),false));
            }
        }
    };
    private static String getStoragePath(Context mContext, boolean is_removale) {  
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}