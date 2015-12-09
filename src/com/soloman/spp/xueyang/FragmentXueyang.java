package com.soloman.spp.xueyang;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
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
import android.hardware.usb.UsbRequest;
import android.media.MediaPlayer;
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
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentXueyang extends Fragment {

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
    private Button btn_xueyangselectuser, btn_startxueyang, btn_volume;
    private TextView tv_xueyangnum, tv_user1_xueyang, tv_user2_xueyang, tv_device_xueyang, tv_recentrecord_o2,tv_o2,tv_bpm;
    private ScrollView sv_o2;
    private ImageView iv_heart;
    
    private boolean volumeon = true;
    public Thread play;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
//    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatServiceXueyang mChatService = null;

	
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
		
		View view = inflater.inflate(R.layout.frag_xueyang, container, false);
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
        	tv_user1_xueyang.setText("There is no user. If no user selected,");
        	tv_user2_xueyang.setText("test result can not be saved.");			
			
        	tv_user1_xueyang.setTextColor(Color.RED);
        	tv_user2_xueyang.setTextColor(Color.RED);
		}else{
			updateUI();	
		}
        tv_device_xueyang.setText("Device ID:" + appState.deviceAddress);
        
        appState.getDB();
	}

	public void findView(View view){
		tv_user1_xueyang = (TextView) view.findViewById(R.id.tv_user1_xueyang);
		tv_user2_xueyang = (TextView) view.findViewById(R.id.tv_user2_xueyang);
		tv_device_xueyang = (TextView) view.findViewById(R.id.tv_device_xueyang);
		tv_recentrecord_o2  = (TextView) view.findViewById(R.id.tv_recentrecord_o2);
		tv_o2 = (TextView) view.findViewById(R.id.tv_o2);
		tv_bpm = (TextView) view.findViewById(R.id.tv_bpm);
		sv_o2 = (ScrollView) view.findViewById(R.id.sv_o2);
		iv_heart = (ImageView) view.findViewById(R.id.iv_heart);
		
		btn_xueyangselectuser = (Button) view.findViewById(R.id.btn_xueyangselectuser);
		btn_xueyangselectuser.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent it = new Intent(getActivity(), gridUser.class);
    			startActivityForResult(it, 1);	
            }
        });
		
		btn_volume = (Button) view.findViewById(R.id.btn_volume);
		btn_volume.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if (volumeon){
            		volumeon = false;
            		btn_volume.setBackgroundResource(R.drawable.volumeoff);
            	}else{
            		volumeon = true;
            		btn_volume.setBackgroundResource(R.drawable.volumeon);
            	}
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
            if (mChatService.getState() == BluetoothChatServiceXueyang.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");
        
        tv_xueyangnum = (TextView) getActivity().findViewById(R.id.tv_xueyangnum);

        btn_startxueyang= (Button) getActivity().findViewById(R.id.btn_startxueyang);
        btn_startxueyang.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	int i = mChatService.getState(); //1:未连接，3已连接
            	if (i == 1){
            		// Send a message using content of the edit text widget
                	Intent serverIntent = null;
                	// Launch the DeviceListActivity to see devices and do scan
                    serverIntent = new Intent(getActivity(), DeviceListActivityXueyang.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            	}else if (i == 3){
            	}
            	
                
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatServiceXueyang(getActivity(), mHandler);

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
        if (mChatService.getState() != BluetoothChatServiceXueyang.STATE_CONNECTED) {
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
    

    private final void setStatus(int resId) {
//        final ActionBar actionBar = getActivity().getActionBar();
//        actionBar.setSubtitle(resId);
    }

    private final void setStatus(CharSequence subTitle) {
//        final ActionBar actionBar = getActivity().getActionBar();
//        actionBar.setSubtitle(subTitle);
    }

    
    
    private String xueyangrecive,  xueyang09, xueyang18;
    private int step = 0;
    
    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	String str;
            switch (msg.what) {            
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatServiceXueyang.STATE_CONNECTED:
                    setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
//                    mConversationArrayAdapter.clear();
                    break;
                case BluetoothChatServiceXueyang.STATE_CONNECTING:
                    setStatus(R.string.title_connecting);
                    break;
                case BluetoothChatServiceXueyang.STATE_LISTEN:
                case BluetoothChatServiceXueyang.STATE_NONE:
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
               
                if("xueyangyi".equals(appState.devicetp)){
					if ( str. startsWith("3f3f09")){	//收到09指令
						step = 1;
						xueyangrecive = str;
						xueyang09 = str;
						
						//声音
						if (volumeon){
//							playRec(getResources().getResourceName(R.raw.test));	//(包名+文件名)
//							playRec();	
							//要另开线程
							play  = new play();
							play.start();
							
						}
						iv_heart.setVisibility(View.VISIBLE);
						
					}else if ( str.startsWith("3f3f18")){	//收到18指令
						if (step == 1){
							step = 2;
						}						
						xueyangrecive = str;
						xueyang18 = str;
					}else{
						if (step == 1){
							xueyangrecive += str;
							xueyang09 += str;
						}else if (step == 2){
							
							
							xueyangrecive += str;
							xueyang18 += str;
							
							//如果18数据满了，提取09 和 18的数据，并计算
							if (xueyang18.length() == 52){
								try{
								//提取09的数据，每次都是重复的
								Log.i("info", "血氧09数据包>>>>>>>>>>>>>>>>>" + xueyang09);
								String pulse = xueyang09.substring(14,16);								
								Log.i("info", "血氧09脉搏>>>>>>>>>>>>>>>>>" + pulse);
								String o2 = xueyang09.substring(16,18);
								Log.i("info", "血氧浓度>>>>>>>>>>>>>>>>>" + o2);
								String wenduH = xueyang09.substring(18,20);	
								Log.i("info", "温度H>>>>>>>>>>>>>>>>>" + wenduH);
								String wenduL = xueyang09.substring(20,22);	
								Log.i("info", "温度L>>>>>>>>>>>>>>>>>" + wenduL);
								
								//提取18的数据
								Log.i("info", "血氧18数据包>>>>>>>>>>>>>>>>>" + xueyang18);
								String pulseH = xueyang18.substring(50,52);
								Log.i("info", "血氧18脉搏高位>>>>>>>>>>>>>>>>>" + pulseH);
								String wave = xueyang18.substring(24,26);
								Log.i("info", "血氧18波形>>>>>>>>>>>>>>>>>" + wave);
								String bt3 = xueyang18.substring(34,36);
								Log.i("info", "血氧18棒图3>>>>>>>>>>>>>>>>>" + bt3);
								xueyang18 = "";
								
								//计算真实值
								Byte p09 = Byte.valueOf(pulse, 16);
								Byte p18 =  Byte.valueOf(pulseH, 16);
								Byte p = (byte) (((p18 & 0x40) << 1) | p09);
								Log.i("info", "血氧真实脉搏}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}" + String.valueOf(p));
								
								Byte o = Byte.valueOf(o2, 16);
								Log.i("info", "血氧真实浓度}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}" + String.valueOf(o));
								
								Byte wdH = (byte) (Byte.valueOf(wenduH, 16) & 0x7f);
								Byte wdL = (byte) (Byte.valueOf(wenduL, 16) & 0x07);
								Log.i("info", "真实温度}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}" + String.valueOf(wenduH)+"." + String.valueOf(wenduL));
								
								Byte w = Byte.valueOf(wave, 16);
								Log.i("info", "血氧真实波形}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}" + String.valueOf(w));
								
								Byte b3 = Byte.valueOf(bt3, 16);
								Log.i("info", "血氧真实棒图3}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}" + String.valueOf(b3));
								
								//更新实时数据UI
//								tv_xueyangnum.setText(String.valueOf(o) + " %," + String.valueOf(p) + "PUL/min");
								tv_o2.setText(String.valueOf(o) + " %");
								tv_bpm.setText(String.valueOf(p) + " bpm");
								iv_heart.setVisibility(View.INVISIBLE);
								
								//保存数据库
								dt = new Date(System.currentTimeMillis());
								if (lastdt == null || o>100 || p<0){//o==127 p == -1时，血氧仪是处于未测量到正确数据，或者从手上拿下的状态
									lastdt = dt;
								}
								
								if (dt.getTime() - lastdt.getTime() > 1000 * 30){//大于30S，则保存一次数据库
									lastdt = dt;
									Time tm = new Time ();
									if ( !"".equals(appState.userID) && appState.userID != null && o<=100 & p>=0){
										appState.database.addXueyang(appState.userID, "1",  String.valueOf(o), String.valueOf(p), "0.0", dt.toString(), tm.toString() );
										//更新最近记录
										tv_recentrecord_o2.append(dt.toString() + "\n" + String.valueOf(o) + " %\t" + String.valueOf(p) + "PUL/min\n\n");
										sv_o2.scrollTo(0, tv_recentrecord_o2.getHeight());	//滚动
									}
								}
								}catch(Exception e){
									e.printStackTrace();
									xueyangrecive  = "";
									xueyang09 = "";
									xueyang18  = "";
								}
							}
							
							
							
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
				tv_user1_xueyang.setTextColor(Color.BLACK);
				tv_user2_xueyang.setTextColor(Color.BLACK);
				
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
			
			tv_user1_xueyang.setText("ID:" + appState.userID + "    User Name:" + appState.userName);
			tv_user2_xueyang.setText("Note:" + appState.note);
			tv_xueyangnum.setText("");
			tv_device_xueyang.setText("Device ID:" + appState.deviceAddress);
		}
	}
    
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListActivityXueyang.EXTRA_DEVICE_ADDRESS);
        appState.deviceAddress = address;
      //by 贺亮，保存连接的mac地址
		appState.file.write2SDFromInput("inurse/", "Xueyang.txt", address );
		
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    
    public class play extends Thread {

		public play() {

		}
		
		@Override
		public void run() {
			playRec() ;
			
		}

	}
    
    public MediaPlayer mPlayer;
	public void playRec() {
		// TODO Auto-generated method stub		
		
		try {
			stopPlayRec();	//先停止上一次的
//			mPlayer = new MediaPlayer();//4.0以上不要new了 直接create
			
//			mPlayer.setDataSource(recfile);
			mPlayer = MediaPlayer.create(getActivity(), R.raw.pulse);
			//mPlayer.setLooping(true);//设置循环播放
//			mPlayer.prepare();	//缓冲
			mPlayer.start();//播放声音    
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	} 
	
	public void stopPlayRec(){
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.reset();
			mPlayer.release();
			mPlayer = null;
		}
	}

}



