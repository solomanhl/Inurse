package com.soloman.usb.wenduji;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Iterator;

import com.bluetooth.le.soloman.GlobalVar;
import com.bluetooth.le.soloman.R;
import com.bluetooth.le.soloman.gridUser;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentUSBThemometer extends Fragment {

	public GlobalVar appState;
	private static final String TAG = "I Nurse USB";
	public Button btn_usbgetTemp, btn_usbselectuser;
	public TextView tv_user1_usbcewen, tv_user2_usbcewen, tv_device_usbcewen, tv_usbtempre, tv_usbcewenwendu, tv_usbcewenunit, tv_usbcewennum;
	public byte mode, unit;
	public int cnttotal, cntbody, cntsurface, cntroom;
	public int ti, tj;
	public Cursor cursor = null;
	private float temper_value;
	
	 private UsbManager mUsbManager;
	    private UsbDevice mDevice;
	    private UsbDeviceConnection mConnection;
	    private UsbEndpoint mEndpointIntr;
	    private SensorManager mSensorManager;
	    private Sensor mGravitySensor;
	
	public UpdateUIThread t;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		appState = (GlobalVar) getActivity().getApplicationContext(); // 获得全局变量
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		appState.keepScreenAlive();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//return inflater.inflate(R.layout.fragment_sleep, container, false);	
		
		View view = inflater.inflate(R.layout.fragment_usbthemometer, container, false);
		
		ti = 0;
		tj = 0;
		cnttotal = 0;
		
		findView(view);
		
		 mUsbManager = (UsbManager)getActivity().getSystemService(Context.USB_SERVICE);

	        mSensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
	        mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
				 
        return view;       
	}

	
	
	
	
	
	@Override
	public void onStart(){
		super.onStart();
		appState.getDB();
		
		if (appState.userID == null || "".equals(appState.userID)){
			tv_user1_usbcewen.setText("There is no user. If no user selected,");
			tv_user2_usbcewen.setText("test result can not be saved.");			
			
			tv_user1_usbcewen.setTextColor(Color.RED);
			tv_user2_usbcewen.setTextColor(Color.RED);
		}else{
			updateUI();	
		}
		tv_device_usbcewen.setText("Device ID:" + appState.deviceAddress);
	}
	
	@Override
    public void onResume() {
        super.onResume();
        startDeviceandThread();
    }
	
	
	private UsbDevice device = null;
	private void startDeviceandThread() {
		// TODO Auto-generated method stub
		mSensorManager.registerListener(mGravityListener, mGravitySensor, SensorManager.SENSOR_DELAY_NORMAL);

        Intent intent = getActivity().getIntent();
        Log.d(TAG, "intent: " + intent);
        String action = intent.getAction();
        
        
       

//        UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (device == null){
//        	Toast.makeText(getActivity(), "device null", Toast.LENGTH_SHORT).show();
        	device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        }else{
//        	Toast.makeText(getActivity(), "device online", Toast.LENGTH_SHORT).show();
        	setDevice(device);
        }
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            setDevice(device);
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            if (mDevice != null && mDevice.equals(device)) {
                setDevice(null);
            }
        }
        
     // 得到当前线程的Looper实例，由于当前线程是UI线程也可以通过Looper.getMainLooper()得到
		Looper looper = Looper.myLooper();
		// 此处甚至可以不需要设置Looper，因为 Handler默认就使用当前线程的Looper
		messageHandler = new MessageHandler(looper);
		
		t= new UpdateUIThread();
		t.start();
	}

	@Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mGravityListener);
        mConnection.close();	//这一句很关键，否则ui pause以后，再进来获取不到数据
        t.interrupt();
     
    }
	
	@Override
	public void onStop(){
		super.onStop();
		appState.dbClose();
	}
	
	private void setDevice(UsbDevice device) {
        Log.d(TAG, "setDevice " + device);
        if (device.getInterfaceCount() != 1) {
            Log.e(TAG, "could not find interface");
            return;
        }
        UsbInterface intf = device.getInterface(0);
        // device should have one endpoint
        if (intf.getEndpointCount() != 1) {
            Log.e(TAG, "could not find endpoint");
//            Toast.makeText(getActivity(), "could not find endpoint", Toast.LENGTH_SHORT).show();
            return;
        }
        // endpoint should be of type interrupt
        UsbEndpoint ep = intf.getEndpoint(0);	//读是0，写是1
        if (ep.getType() != UsbConstants.USB_ENDPOINT_XFER_INT) {
//        	Toast.makeText(getActivity(), "endpoint is not interrupt type", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "endpoint is not interrupt type");
            return;
        }
        mDevice = device;
        mEndpointIntr = ep;
        
        if (device != null) {
            UsbDeviceConnection connection = mUsbManager.openDevice(device);
            if (connection != null && connection.claimInterface(intf, true)) {
                Log.d(TAG, "open SUCCESS");
//                Toast.makeText(getActivity(), "open SUCCESS", Toast.LENGTH_SHORT).show();
                mConnection = connection;
                new UsbDataRead().execute(new String[0]);

            } else {
                Log.d(TAG, "open FAIL");
//                Toast.makeText(getActivity(), "open FAIL", Toast.LENGTH_SHORT).show();
                mConnection = null;
            }
         }
    }
	
	public class UsbDataRead extends AsyncTask<String, Integer, String> {
		public UsbDataRead() {
		}

		protected String doInBackground(String... paramVarArgs) {
			ByteBuffer localByteBuffer = ByteBuffer.allocate(8);
			UsbRequest localUsbRequest = new UsbRequest();
			localUsbRequest.initialize(mConnection, mEndpointIntr);
			
			while(true){
				if (true) {
					localUsbRequest.queue(localByteBuffer, 8);
					sendCommandBytes();
					if (mConnection.requestWait() != localUsbRequest) {
						break;
					}
					// Log.d("MyService", localByteBuffer.get(0) + " " + localByteBuffer.get(1) + " " + localByteBuffer.get(2) + " " + localByteBuffer.get(3));
					// Log.d("MyService", (float)((localByteBuffer.get(0) + 256 * localByteBuffer.get(1) + localByteBuffer.get(3)) / 10.0D));
					temper_value = (float) (((0xFF & localByteBuffer.get(0))
							+ 256 * localByteBuffer.get(1) + localByteBuffer
							.get(3)) / 10.0D);
					 
					try {
						Thread.sleep(200L);
					} catch (InterruptedException localInterruptedException) {
					}
				}
			}
			Log.e("MyService", "requestWait failed, exiting");
			return null;
		}
	}
	
	 private void sendCommandBytes() {
	        synchronized (this) {
	            if (mConnection != null) {
	                byte[] message = new byte[8];
	                message[0] = (byte) 0xf1;
	                message[2] = (byte) 0xcc;
	                message[3] = (byte) 0xcc;
	                message[4] = (byte) 0xcc;
	                message[5] = (byte) 0xcc;
	                message[6] = (byte) 0xcc;
	                message[7] = (byte) 0xcc;
	                // Send command via a control request on endpoint zero
	                mConnection.controlTransfer(0x21, 0x9, 0x200, 0, message, message.length, 0);
	            }
	        }
	    }
	 
	public void findView(View view){
		btn_usbgetTemp = (Button) view.findViewById(R.id.btn_usbgetTemp);
		btn_usbselectuser  = (Button) view.findViewById(R.id.btn_usbselectuser);
		tv_usbtempre = (TextView) view.findViewById(R.id.tv_usbtempre);
		tv_user1_usbcewen = (TextView) view.findViewById(R.id.tv_user1_usbcewen);
		tv_user2_usbcewen = (TextView) view.findViewById(R.id.tv_user2_usbcewen);
		tv_device_usbcewen = (TextView) view.findViewById(R.id.tv_device_usbcewen);
		tv_usbcewenwendu = (TextView) view.findViewById(R.id.tv_usbcewenwendu);
		tv_usbcewenunit = (TextView) view.findViewById(R.id.tv_usbcewenunit);
		tv_usbcewennum = (TextView) view.findViewById(R.id.tv_usbcewennum);
		
		btn_usbselectuser.setOnClickListener(new Button.OnClickListener(){//创建监听    
            public void onClick(View v) {    
            	Intent it = new Intent(getActivity(), gridUser.class);
    			startActivityForResult(it, 1);	
            }
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		startDeviceandThread();
		
		if (resultCode != -1 && resultCode !=0) {
			appState.userID = data.getStringExtra("uid");
			appState.userName = data.getStringExtra("name");
			appState.note = data.getStringExtra("note");			
			if ("none".equals(appState.userID)) {
				new AlertDialog.Builder(getActivity())
				.setTitle("No user found")
				.setMessage("There's no user found in your device, please ADD one in User page.")
//				.setNegativeButton("Cancel",
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								//this.s = "Negative";
//							}
//						})
				.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
							}
				}).show();
			} else {
				tv_user1_usbcewen.setTextColor(Color.BLACK);
				tv_user2_usbcewen.setTextColor(Color.BLACK);
				
				updateUI();	
			}
			
		}
	}
	
	public void updateUI(){
		if (appState.userID != null){
			if (appState.userName == null){
				appState.userName = "";
			}
			if (appState.note ==null){
				appState.note = "";
			}
			
			tv_user1_usbcewen.setText("ID:" + appState.userID + "    User Name:" + appState.userName);
			tv_user2_usbcewen.setText("Note:" + appState.note);
			
		}	
	}
	
	public void updateWendu(){
				
				
				try{
					tv_usbcewenunit.setText("℃");
					tv_usbcewenwendu.setText( String.valueOf(temper_value) );		
					tv_usbcewennum.setText( "Record Total: " + String.valueOf(cnttotal) );		
				}catch(Exception e){
					e.printStackTrace();
				}
				
				//如果选择了病人（user）
				if (appState.userID != null && !"".equals(appState.userID)){
					tv_user1_usbcewen.setTextColor(Color.BLACK);
					tv_user2_usbcewen.setTextColor(Color.BLACK);
				}
	}
	
	
	
	private int mLastValue = 0;

    SensorEventListener mGravityListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {

            // compute current tilt
            int value = 0;
//            if (event.values[0] < -THRESHOLD) {
//                value += TILT_LEFT;
//            }
//            ed1.append("onSensorChanged:" + String.valueOf(event.values[0]) + "\n");
            
            if (value != mLastValue) {
                mLastValue = value;
                
                // send motion command if the tilt changed
                switch (value) {
//                case TILT_LEFT:
                    case 1:
//                        sendCommand(COMMAND_LEFT);
                        break;                   
                    default:
//                        sendCommand(COMMAND_STOP);
                        break;
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // ignore
        }
    };
    
    public void run() {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        UsbRequest request = new UsbRequest();
        request.initialize(mConnection, mEndpointIntr);
        byte status = -1;
        while (true) {
            // queue a request on the interrupt endpoint
            request.queue(buffer, 1);
            // send poll status command
//            sendCommand(COMMAND_STATUS);
            // wait for status event
            if (mConnection.requestWait() == request) {
                byte newStatus = buffer.get(0);
                if (newStatus != status) {
                    Log.d(TAG, "got status " + newStatus);
                    status = newStatus;
//                    if ((status & COMMAND_FIRE) != 0) {
                        // stop firing
//                        sendCommand(COMMAND_STOP);
//                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            } else {
                Log.e(TAG, "requestWait failed, exiting");
                break;
            }
        }
    }
    

    private Handler messageHandler;
	private void updateHandler(Object obj) {
		// 创建一个Message对象，并把得到的网络信息赋值给Message对象
		Message message = Message.obtain();// 第一步
		message = Message.obtain();// 第一步
		message.obj = obj; // 第二步
		messageHandler.sendMessage(message);// 第三步
	}
	
	// 子类化一个Handler
	class MessageHandler extends Handler {
		public MessageHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			// 更新UI
			if (!((String) msg.obj == null)) {
				if ("wendudata".equals((String) msg.obj)) {
						updateWendu();
//						Toast.makeText(getActivity(), String.valueOf(temper_value), Toast.LENGTH_SHORT).show();
				}

			}
		}
	}
 // 更新数据进程----------------------------------------
 	public class UpdateUIThread extends Thread {
 		public UpdateUIThread() {

 		}

 		private int cnt = 0;
 		@Override
 		public void run() {
 			while (!this.isInterrupted()) {
 				// System.out.println("sportDataThread run again");
 				updateHandler("wendudata");
 				
 				cnt++;
 				if (cnt == 15){
 					cnt = 0;
 					//如果选择了病人（user），写数据库
 					try{
					if (appState.userID != null && !"".equals(appState.userID) && temper_value>32){
						Date dt = new Date(System.currentTimeMillis());
						Time tm = new Time ();
						appState.add_Record(appState.userID, "2", "usb", "℃",String.valueOf(temper_value), dt.toString(), tm.toString());
						cnttotal++;
					}
 					}catch (Exception e) {
	 					// TODO Auto-generated catch block
	 					e.printStackTrace();
	 				}
 				}
 				

 				try {
 					Thread.sleep(1000*3);
 				} catch (InterruptedException e) {
 					// TODO Auto-generated catch block
 					e.printStackTrace();
 				}
 			}
 		}

 	}
 	// ==================end thread
}



