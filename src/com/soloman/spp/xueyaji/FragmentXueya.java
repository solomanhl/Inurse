package com.soloman.spp.xueyaji;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import com.bluetooth.le.soloman.GlobalVar;
import com.bluetooth.le.soloman.R;
import com.bluetooth.le.soloman.Utils;
import com.bluetooth.le.soloman.gridUser;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentXueya extends Fragment {

public GlobalVar appState;
	
    // Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private Button btn_xueyaselectuser, btn_startxueya;
    private TextView tv_xueyanum, tv_user1_xueya, tv_user2_xueya, tv_device_xueya;
    public ProgressBar progressBar_xueya;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
//    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatServiceXueya mChatService = null;

	public int high, low, rate;//手机计算的高压和低压、心率
	public int stat;//状态，上升stat=0，到达00 3f最高压时stat=1，下降stat-2
    
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
		
		View view = inflater.inflate(R.layout.frag_xueyaji, container, false);
		findView(view);	

		// Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
//            finish();
//            return;
        }
				
        return view;       
	}
	
	@Override
	public void onStart(){
		super.onStart();
		if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
        
        //界面信息的初始化
        if (appState.userID == null || "".equals(appState.userID)){
        	tv_user1_xueya.setText("There is no user. If no user selected,");
        	tv_user2_xueya.setText("test result can not be saved.");			
			
        	tv_user1_xueya.setTextColor(Color.RED);
        	tv_user2_xueya.setTextColor(Color.RED);
		}else{
			updateUI();	
		}
        tv_device_xueya.setText("Device ID:" + appState.deviceAddress);
        
        appState.getDB();
	}

	public void findView(View view){
		tv_user1_xueya = (TextView) view.findViewById(R.id.tv_user1_xueya);
		tv_user2_xueya = (TextView) view.findViewById(R.id.tv_user2_xueya);
		tv_device_xueya = (TextView) view.findViewById(R.id.tv_device_xueya);
		progressBar_xueya = (ProgressBar) view.findViewById(R.id.progressBar_xueya);
		
		btn_xueyaselectuser = (Button) view.findViewById(R.id.btn_xueyaselectuser);
		btn_xueyaselectuser.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent it = new Intent(getActivity(), gridUser.class);
    			startActivityForResult(it, 1);	
            }
        });
	}
	
	
	@Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatServiceXueya.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");
        
        tv_xueyanum = (TextView) getActivity().findViewById(R.id.tv_xueyanum);

        btn_startxueya= (Button) getActivity().findViewById(R.id.btn_startxueya);
        btn_startxueya.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	int i = mChatService.getState(); //1:未连接，3已连接
            	if (i == 1){
            		// Send a message using content of the edit text widget
                	Intent serverIntent = null;
                	// Launch the DeviceListActivity to see devices and do scan
                    serverIntent = new Intent(getActivity(), DeviceListActivityXueya.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            	}else if (i == 3){
            		//发送开始测量
            		sendStartBloodPressure();
					tv_xueyanum.setText("Blood Pressure Monitor is online...");
					lastdt = new Date(System.currentTimeMillis());
					Log.i("info","starttime=" + String.valueOf(lastdt.getTime()) );
            	}
            	
                
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatServiceXueya(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
        
        appState.dbClose();
    }

    private void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatServiceXueya.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
        }
    }
    
    private void sendStartBloodPressure(){
    	// Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatServiceXueya.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
            // Get the message bytes and tell the BluetoothChatService to write
        	byte[] send = {(byte) 0xfd, (byte) 0xfd, (byte) 0xfa, 0x05, 0x0d, 0x0a};
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
    }
    
    private void sendClose() {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatServiceXueya.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
            // Get the message bytes and tell the BluetoothChatService to write
        	byte[] send = {(byte) 0xfd, 0x35, 0x00, 0x00, 0x00, 0x00, 0x00, 0x35};
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
//            mOutEditText.setText(mOutStringBuffer);
    }
    


   

    private final void setStatus(int resId) {
//        final ActionBar actionBar = getActivity().getActionBar();
//        actionBar.setSubtitle(resId);
    }

    private final void setStatus(CharSequence subTitle) {
//        final ActionBar actionBar = getActivity().getActionBar();
//        actionBar.setSubtitle(subTitle);
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	String str;
            switch (msg.what) {            
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatServiceXueya.STATE_CONNECTED:
                    setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
//                    mConversationArrayAdapter.clear();
                    
                    //发送取数据指令
                    if ("dianzichen".equals(appState.devicetp)){
                    	
                    }else if("xueyaji".equals(appState.devicetp)){
                    	
                    }
                    
                    break;
                case BluetoothChatServiceXueya.STATE_CONNECTING:
                    setStatus(R.string.title_connecting);
                    break;
                case BluetoothChatServiceXueya.STATE_LISTEN:
                case BluetoothChatServiceXueya.STATE_NONE:
                    setStatus(R.string.title_not_connected);
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                
                str = Utils.bytesToHexString(writeBuf);
                
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);               
                
                byte[] arrayOfByte2 = new byte[msg.arg1];
                
                try {
					arrayOfByte2=readMessage.getBytes(  "ISO-8859-1" );
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                str = Utils.bytesToHexString(arrayOfByte2);
                
                Log.i("info", "=================" + str);
                str = "";
                
                if ("dianzichen".equals(appState.devicetp)){
                	if (arrayOfByte2[0] == 0xcf){
                    	
                    }
                	
                	 mHandler.postDelayed(new Runnable() {
                         @Override
                         public void run() {
                         	//回发关闭指令
                             sendClose();
                             Log.i("info", "=================回发");
                         }
                     }, 500);
                }else if("xueyaji".equals(appState.devicetp)){
					if (arrayOfByte2[0] == 0x3f && arrayOfByte2.length == 1) {	//血压仪发回上线消息
						//发开始测量
						stat=0;
						high=0;
						low=0;
						rate=0;
						sendStartBloodPressure();
						tv_xueyanum.setText("Blood Pressure Monitor is online...");
						lastdt = new Date(System.currentTimeMillis());
						Log.i("info","starttime=" + String.valueOf(lastdt.getTime()) );
						//更新汞柱
						progressBar_xueya.setProgress( 0 ); //第1进度代表实时血压和低压dia 
						progressBar_xueya.setSecondaryProgress( 0 ); //第2进度代表高压sys
					}else if ("3f3f060d0a".equals(str)){
						//血压计正在工作
						tv_xueyanum.setText("Blood Pressure Monitor is starting...");
						
					}else if(arrayOfByte2.length == 7 &&
							arrayOfByte2[0] == 0x3f &&
							arrayOfByte2[1] == 0x3f &&
							arrayOfByte2[2] == 0x3f &&
							arrayOfByte2[5] == 0x0d &&
							arrayOfByte2[6] == 0x0a){//[0xFD,0xFD,0xFB,PressureH, PressureL,0X0D, 0x0A]
						//收到回传的实时血压数据
						if (arrayOfByte2[3] == 0x00 && arrayOfByte2[4] == 0x3f){ //00 3f 好像是到最高点保压的数据
							stat = 1;
						}else{
							if (stat==1){
								stat = 2;
							}
							int pr = ( (int) (arrayOfByte2[3] & 0xff) * 256 + (int) (arrayOfByte2[4] & 0xff) ); 
							if (pr<300){
								tv_xueyanum.setText("Blood Pressure:" + String.valueOf(pr));
								//更新汞柱
								progressBar_xueya.setProgress( pr/3 ); //第1进度代表实时血压和低压dia 
								if (stat==0){
									//加压阶段
									if (pr > high){
										high=pr;
										progressBar_xueya.setSecondaryProgress( pr/3 ); //第2进度代表高压sys
										Log.i("info","high=" + String.valueOf(pr));
									}
								}else if(stat==2){
									//下降阶段
									low = pr;
									Log.i("info","low=" + String.valueOf(pr));
								}
							}else{//大于300是无效数据,经测试，此时高位是低压，低位是心率
//								tv_xueyanum.setText("Blood Pressure:Invalid Data,please start again!");
								low =  (int) (arrayOfByte2[3] & 0xff) ; 
								rate =  (int) (arrayOfByte2[4] & 0xff) ; 
								tv_xueyanum.setText(String.valueOf(high) + " SYS_mmHg,"
										+ String.valueOf(low) + "DIA_mmHg,\n"
										+ String.valueOf(rate) + "PUL/min");
								dt = new Date(System.currentTimeMillis());
								if (dt.getTime() - lastdt.getTime() > 10000){//大于10S，则是2次不同的记录
									lastdt = dt;
									Time tm = new Time ();
									if ( !appState.isDBOpen() ){
										appState.getDB();
									}
									appState.database.addXueya(appState.userID, "xueyaji", "mmHg", String.valueOf(high), String.valueOf(low), String.valueOf(rate), dt.toString(), tm.toString() );
									//更新汞柱
									progressBar_xueya.setProgress( low/3 ); //第1进度代表实时血压和低压dia 
									progressBar_xueya.setSecondaryProgress( high/3 ); //第2进度代表高压sys
								}
							}
							
						}
						
					}else if(arrayOfByte2.length == 8 &&
							arrayOfByte2[0] == 0x3f &&
							arrayOfByte2[1] == 0x3f &&
							arrayOfByte2[2] == 0x3f &&
							arrayOfByte2[6] == 0x0d &&
							arrayOfByte2[7] == 0x0a){
						//测量完成[0xFD,0xFD,0xFC, SYS,DIA,PUL, 0X0D, 0x0A]	;Test result 
						//收到回传的最终测量数据
						int sys = (int) (arrayOfByte2[3] & 0xff) ; 
						int dia = (int) (arrayOfByte2[4] & 0xff) ; 
						int pul = (int) (arrayOfByte2[5] & 0xff) ; 
						tv_xueyanum.setText(String.valueOf(sys) + " SYS_mmHg,"
								+ String.valueOf(dia) + "DIA_mmHg,\n"
								+ String.valueOf(pul) + "PUL/min");
						dt = new Date(System.currentTimeMillis());
						if (dt.getTime() - lastdt.getTime() > 10000){//大于10S，则是2次不同的记录
							lastdt = dt;
							Time tm = new Time ();
							if ( !appState.isDBOpen() ){
								appState.getDB();
							}
							appState.database.addXueya(appState.userID, "xueyaji", "mmHg", String.valueOf(sys), String.valueOf(dia), String.valueOf(pul), dt.toString(), tm.toString() );
							//更新汞柱
							progressBar_xueya.setProgress( dia/3 ); //第1进度代表实时血压和低压dia 
							progressBar_xueya.setSecondaryProgress( sys/3 ); //第2进度代表高压sys
						}
					}
				}
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getActivity().getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
//                Toast.makeText(getActivity().getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    
    Date dt, lastdt;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, true);
            }
            break;
        case REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, false);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
//                getActivity().finish();
            }
        }
        
        
        //以下是处理选择用户功能
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
				tv_user1_xueya.setTextColor(Color.BLACK);
				tv_user2_xueya.setTextColor(Color.BLACK);
				
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
			
			tv_user1_xueya.setText("ID:" + appState.userID + "    User Name:" + appState.userName);
			tv_user2_xueya.setText("Note:" + appState.note);
			tv_xueyanum.setText("");
			tv_device_xueya.setText("Device ID:" + appState.deviceAddress);
		}
	}
    
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListActivityXueya.EXTRA_DEVICE_ADDRESS);
        appState.deviceAddress = address;
      //by 贺亮，保存连接的mac地址
		appState.file.write2SDFromInput("inurse/", "Xueya.txt", address );
		
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }


}



