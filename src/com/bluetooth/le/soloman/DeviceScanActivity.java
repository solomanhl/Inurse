/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bluetooth.le.soloman;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;
import java.util.List;
import java.util.UUID;

import com.bluetooth.le.soloman.BluetoothLeClass.OnDataAvailableListener;
import com.bluetooth.le.soloman.BluetoothLeClass.OnServiceDiscoverListener;
import com.bluetooth.le.soloman.R;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends ListActivity {
	private final static String TAG = DeviceScanActivity.class.getSimpleName();
	//体温计的
	private final static String UUID_KEY_SERVICE = "0000fe18-0000-1000-8000-00805f9b34fb";
	private final static String UUID_KEY_DATA_SEND = "0000fe11-0000-1000-8000-00805f9b34fb"; //write
	private final static String UUID_KEY_DATA_RECIV = "0000fe10-0000-1000-8000-00805f9b34fb"; //notify
	private final static String CCC = "00002902-0000-1000-8000-00805f9b34fb"; //CCC

	public BluetoothGatt mBluetoothGatt = null;	//全局bluetoothgatt
	
    private LeDeviceListAdapter mLeDeviceListAdapter;
    /**搜索BLE终端*/
    private BluetoothAdapter mBluetoothAdapter;
    /**读写BLE终端*/
    private BluetoothLeClass mBLE_send, mBLE_reciv;
    private boolean mScanning;
    private Handler mHandler = new Handler();

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    public GlobalVar appState; // 获得全局变量;; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	appState = (GlobalVar) getApplicationContext(); // 获得全局变量
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置成竖屏
    	
    	
        super.onCreate(savedInstanceState);
        //getActionBar().setTitle(R.string.title_devices);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 	//去掉title
        //mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        appState.BluetoothAdapter = mBluetoothAdapter;
        
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        //开启蓝牙
        mBluetoothAdapter.enable();
        
        
        
        mBLE_reciv = new BluetoothLeClass(this);
        appState.mBLE_reciv = mBLE_reciv;
        //appState.init_BluetoothLeClass();
        if (! mBLE_reciv.initialize()) {
            Log.e(TAG, "Unable to initialize Bluetooth");
            finish();
        }
        //发现BLE终端的Service时回调
        mBLE_reciv.setOnServiceDiscoverListener(mOnServiceDiscover);
        //收到BLE终端数据交互的事件
        mBLE_reciv.setOnDataAvailableListener(mOnDataAvailable);     
        
        
        mBLE_send = new BluetoothLeClass(this);
        appState.mBLE_send = mBLE_send;
        if (! appState.mBLE_send.initialize()) {
            Log.e(TAG, "Unable to initialize Bluetooth");
            finish();
        }
        //发现BLE终端的Service时回调
        mBLE_send.setOnServiceDiscoverListener(mOnServiceDiscover);
        //收到BLE终端数据交互的事件
        mBLE_send.setOnDataAvailableListener(mOnDataAvailable);               
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Initializes list view adapter.    
        mLeDeviceListAdapter = new LeDeviceListAdapter(this);        	
        setListAdapter(mLeDeviceListAdapter);
        if (mBluetoothAdapter.isEnabled()){
        	scanLeDevice(true);
        }else{
        	finish();
        	return;
        }
                	 
    }

    @Override
    protected void onPause() {
        super.onPause();
//        scanLeDevice(false);
//        mLeDeviceListAdapter.clear();
//        mBLE_send.disconnect();
//        mBLE_reciv.disconnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mBLE_send.close();
//        mBLE_reciv.close();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	scanLeDevice(false);
        mLeDeviceListAdapter.clear();
        mBLE_send.disconnect();
        mBLE_reciv.disconnect();
        mBLE_send.close();
        mBLE_reciv.close();
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        
        appState.deviceAddress = device.getAddress();
        
        mBLE_reciv.connect(device.getAddress());
        mBLE_send.connect(device.getAddress());
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                    //Log.i("info", "正在搜索");
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    /**
     * 搜索到BLE终端服务的事件
     */
    private BluetoothLeClass.OnServiceDiscoverListener mOnServiceDiscover = new OnServiceDiscoverListener(){

		@Override
		public void onServiceDiscover(BluetoothGatt gatt) {
			mBluetoothGatt = gatt;  //搜索到服务后，给全局bluetoothgatt赋值
			displayGattServices( mBLE_send.getSupportedGattServices());
		}
    	
    };
    
    /**
     * 收到BLE终端数据交互的事件
     */
    private BluetoothLeClass.OnDataAvailableListener mOnDataAvailable = new OnDataAvailableListener(){

    	/**
    	 * BLE终端数据被读的事件
    	 */
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) 
				Log.e(TAG,"onCharRead "+gatt.getDevice().getName()
						+" read "
						+characteristic.getUuid().toString()
						+" -> "
						+Utils.bytesToHexString(characteristic.getValue()));
		}
		
	    /**
	     * 收到BLE终端写入数据回调
	     */
		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			Log.e(TAG,"onCharWrite "+gatt.getDevice().getName()
					+" write "
					+characteristic.getUuid().toString()
					+" -> "
					+Utils.bytesToHexString(characteristic.getValue()));
			
			switch (characteristic.getValue()[0]) {
			case (byte) 0xFA:	//实时温度数据
				appState.calcTemp(characteristic.getValue()); //解析温度
				appState.dataArrive = true;
				break;

			default:
				break;
			}
		}
    };

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                    
                  //自动连接功能-------------------------------------------
                    String lastDevice = "";
                    appState.autoConnect = true;
                    appState.deviceAddress = device.getAddress();
                    //读上次的设备
                    //Thermometer温度计
                    if ( appState.file.isFileExist("inurse/Thermometer.txt") ){
                    	lastDevice =  appState.file.readFile(appState.file.SDPATH + "inurse/Thermometer.txt");
                    }
                    
                    //如果和上次的相等，就直接连接
                    if (lastDevice.equals(device.getAddress())){
                    	appState.file.write2SDFromInput("inurse/", "Thermometer.txt", device.getAddress());                    	
                    	
                    	if (mScanning) {
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                            mScanning = false;
                        }
                        
                        appState.mBLE_reciv.connect(device.getAddress());
                        appState.mBLE_send.connect(device.getAddress());             
                    }else{
                    	appState.autoConnect = false;
                    }
                    //---------------------------------------------------------------------
                }
            });
        }
    };

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        for (BluetoothGattService gattService : gattServices) {
        	//-----Service的字段信息-----//
        	int type = gattService.getType();
            Log.e(TAG,"-->service type:"+Utils.getServiceType(type));
            Log.e(TAG,"-->includedServices size:"+gattService.getIncludedServices().size());
            Log.e(TAG,"-->service uuid:"+gattService.getUuid());
            
            //-----Characteristics的字段信息-----//
            List<BluetoothGattCharacteristic> gattCharacteristics =gattService.getCharacteristics();
            for (final BluetoothGattCharacteristic  gattCharacteristic: gattCharacteristics) {
                Log.e(TAG,"---->char uuid:"+gattCharacteristic.getUuid());
                
                int permission = gattCharacteristic.getPermissions();
                Log.e(TAG,"---->char permission:"+Utils.getCharPermission(permission));
                
                int property = gattCharacteristic.getProperties();
                Log.e(TAG,"---->char property:"+Utils.getCharPropertie(property));

                byte[] data = gattCharacteristic.getValue();
        		if (data != null && data.length > 0) {
        			Log.e(TAG,"---->char value:"+ Utils.bytesToHexString(data) );
        		}

        		if ( gattService.getUuid().toString().equals(UUID_KEY_SERVICE) ) { //如果是温度计的service
        			//UUID_KEY_DATA是可以跟蓝牙模块串口通信的Characteristic
            		if(gattCharacteristic.getUuid().toString().equals(UUID_KEY_DATA_RECIV)) {    
            			//测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
            			mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                            	 mBLE_reciv.readCharacteristic(gattCharacteristic);
                            }
                        }, 500);
            			
            			//接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
//            			 mBLE_reciv.setCharacteristicNotification(gattCharacteristic, true);
//            			 appState.gattCharacteristic_reciv = gattCharacteristic;
//            			 
            			//接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
            			 mBLE_reciv.setCharacteristicNotification(gattCharacteristic, true);
            			 BluetoothGattDescriptor localBluetoothGattDescriptor = gattCharacteristic.getDescriptor(UUID.fromString(CCC));
            			 localBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            			 mBluetoothGatt.writeDescriptor(localBluetoothGattDescriptor);
            			 appState.gattCharacteristic_reciv_dianzichen = gattCharacteristic;
            			//设置数据内容
//            			gattCharacteristic.setValue("0");
            			//往蓝牙模块写入数据
//            			appState.mBLE_reciv.writeCharacteristic(gattCharacteristic);
            		}
            		
            		//UUID_KEY_DATA是可以跟蓝牙模块串口通信的Characteristic
            		if(gattCharacteristic.getUuid().toString().equals(UUID_KEY_DATA_SEND)) {        			
            			//测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
            			mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                            	 mBLE_send.readCharacteristic(gattCharacteristic);
                            	 //Log.i("info", "appState.mBLE_send 尝试读数据");
                            }
                        }, 500);
            			
            			//接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
            			 mBLE_send.setCharacteristicNotification(gattCharacteristic, true);
            			//设置数据内容
            			//gattCharacteristic.setValue("send data->");
            			//往蓝牙模块写入数据
            			//mBLE_send.writeCharacteristic(gattCharacteristic);
            			
            			if ( !appState.firstActivityRunning ){
            				appState.gattCharacteristic_send = gattCharacteristic;
            				
            				appState.file.write2SDFromInput("inurse/", "Thermometer.txt", appState.deviceAddress);
                			
            				Intent it = new Intent(this, CeliangActivity.class);
                			startActivityForResult(it, 0);	//配合onActivityResult，当FirstActivity退出时获得一个0值，然后自己也退出
//            				moveTaskToBack(true); //这一句是整个程序返回桌面
                			appState.firstActivityRunning = true;
            			}
            			
            		}
        		} 	//结束 if ( gattService.getUuid().toString().equals(UUID_KEY_SERVICE) )
        		
        		
        		//-----Descriptors的字段信息-----//
				List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic.getDescriptors();
				for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) {
					Log.e(TAG, "-------->desc uuid:" + gattDescriptor.getUuid());
					int descPermission = gattDescriptor.getPermissions();
					Log.e(TAG,"-------->desc permission:"+ Utils.getDescPermission(descPermission));
					
					byte[] desData = gattDescriptor.getValue();
					if (desData != null && desData.length > 0) {
						Log.e(TAG, "-------->desc value:"+ Utils.bytesToHexString(data) );
					}
					
					
					//soloman 设置CCC通知enable，并回写到BluetoothGatt
//					if (gattDescriptor.getUuid().toString().equals(CCC)){
//						gattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//						mBluetoothGatt.writeDescriptor(gattDescriptor);
//					}
        		 }
            }
        }//

    }
    
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == 0 && resultCode == RESULT_OK) {
			finish();
		}
	}
    
    
    
    
}