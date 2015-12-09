/*
 * Copyright (C) 2009 The Android Open Source Project
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

package com.soloman.spp.xueyang;

import java.io.UnsupportedEncodingException;

import com.bluetooth.le.soloman.GlobalVar;
import com.bluetooth.le.soloman.R;
import com.bluetooth.le.soloman.Utils;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothChatXueyang extends Activity {
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
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatServiceXueyang mChatService = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
    	appState = (GlobalVar) getApplicationContext(); // 获得全局变量
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout
        setContentView(R.layout.main);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    public void onStart() {
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

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mConversationView = (ListView) findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message);
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatServiceXueyang(this, mHandler);

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
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }
    
    //not use
    private void sendStartBloodPressure(){
    	// Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatServiceXueyang.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
            // Get the message bytes and tell the BluetoothChatService to write
        	byte[] send = {(byte) 0xfd, (byte) 0xfd, (byte) 0xfa, 0x05, 0x0d, 0x0a};
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
    }
    
    private void sendClose() {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatServiceXueyang.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
            // Get the message bytes and tell the BluetoothChatService to write
        	byte[] send = {(byte) 0xfd, 0x35, 0x00, 0x00, 0x00, 0x00, 0x00, 0x35};
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
    }
    
    //not use
    private void sendGetData() {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatServiceXueyang.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

     // Check that there's actually something to send
        // Get the message bytes and tell the BluetoothChatService to write
        String s4 = Integer.toHexString(174);
    	String s5 = Integer.toHexString(30);
        //                           group  sex   level  height      age   danwei
    	byte[] send0 = {(byte) 0xfe, 0x01, 0x01, 0x01, (byte) 0xae, 0x1e, 0x01, (byte) 0xb0};
    	send0[send0.length-1] = send0[1];//7
    	for (int i = 2; i<(send0.length -1); i++){//2<=i<7
    		send0[send0.length-1] ^= send0[i];
    	}
        mChatService.write(send0);

        // Reset out string buffer to zero and clear the edit text field        
        mOutStringBuffer.setLength(0);
        mOutEditText.setText(mOutStringBuffer);
        
        // Check that there's actually something to send
            // Get the message bytes and tell the BluetoothChatService to write
        	final byte[] send = {(byte) 0xfe, 0x01, 0x01, 0x01, (byte) 0xa8, 0x1e, 0x01, (byte) 0xb6};
        	s4 = Integer.toHexString(168);
        	s5 = Integer.toHexString(30);
        	send[send.length-1] = send[1];//7
        	for (int i = 2; i<(send.length -1); i++){//2<=i<7
        		send[send.length-1] ^= send[i];
        	}
        	
        	mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                	mChatService.write(send);
                }
            }, 300);
            

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
    }

    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            if(D) Log.i(TAG, "END onEditorAction");
            return true;
        }
    };

    private final void setStatus(int resId) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(resId);
    }

    private final void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(subTitle);
    }

//    private String xueyangrecive, xueyang09, xueyang18;
//    private int step = 0;
    
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
                    mConversationArrayAdapter.clear();
                    
                    //发送取数据指令
//                    if ("dianzichen".equals(appState.devicetp)){
//                    	sendGetData();
//                    }else if("xueyaji".equals(appState.devicetp)){
//                    	
//                    }else if ("xueyangyi".equals(appState.devicetp)){
//                    	
//                    }
                    
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
                mConversationArrayAdapter.add("Me:  " + writeMessage);
                
                str = Utils.bytesToHexString(writeBuf);
                mConversationArrayAdapter.add("Me:  " + str);
                
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);               
                mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                
                byte[] arrayOfByte2 = new byte[msg.arg1];
                
                try {
					arrayOfByte2=readMessage.getBytes(  "ISO-8859-1" );
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
				str = Utils.bytesToHexString(arrayOfByte2);
				mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + str);

				Log.i("info", "=================" + str);
//                
//                if ("dianzichen".equals(appState.devicetp)){
//                	if (arrayOfByte2[0] == 0xcf){
//                    	
//                    }
//                	
//                	 mHandler.postDelayed(new Runnable() {
//                         @Override
//                         public void run() {
//                         	//回发关闭指令
//                             sendClose();
//                             Log.i("info", "=================回发");
//                         }
//                     }, 500);
//                }else if("xueyaji".equals(appState.devicetp)){
//					if (arrayOfByte2[0] == 0x3f && arrayOfByte2.length == 1) {	//血压仪发回上线消息
//						//发开始测量
//						sendStartBloodPressure();
//					}else if ("fdfd060d0a".equals(str)){
//						//血压计正在工作
//					}else if(arrayOfByte2[0] == 0xfd &&
//							arrayOfByte2[1] == 0xfd &&
//							arrayOfByte2[2] == 0xfb &&
//							arrayOfByte2[5] == 0x0d &&
//							arrayOfByte2[6] == 0x0a){//[0xFD,0xFD,0xFB,PressureH, PressureL,0X0D, 0x0A]
//						//收到回传的实时血压数据
//					}else if(arrayOfByte2[0] == 0xfd &&
//							arrayOfByte2[1] == 0xfd &&
//							arrayOfByte2[2] == 0xfc &&
//							arrayOfByte2[6] == 0x0d &&
//							arrayOfByte2[7] == 0x0a){
//						//测量完成[0xFD,0xFD,0xFC, SYS,DIA,PUL, 0X0D, 0x0A]	;Test result 
//						//收到回传的最终测量数据
//					}
//				}else if("xueyangyi".equals(appState.devicetp)){
//					if ( str. startsWith("3f3f093f233f")){	//收到09指令
//						step = 1;
//						xueyangrecive = str;
//						xueyang09 = str;
//					}else if ( str.startsWith("3f3f183f233f")){	//收到18指令
//						if (step == 1){
//							step = 2;
//						}						
//						xueyangrecive = str;
//						xueyang18 = str;
//					}else{
//						if (step == 1){
//							xueyangrecive += str;
//							xueyang09 += str;
//						}else if (step == 2){
//							//提取09的数据
//							Log.i("info", "血氧09数据包>>>>>>>>>>>>>>>>>" + xueyang09);
//							
//							xueyangrecive += str;
//							xueyang18 += str;
//							
//							//如果18数据满了，提取18的数据，并计算
//							if (xueyang18.length() == 52){
//								Log.i("info", "血氧18数据包>>>>>>>>>>>>>>>>>" + xueyang18);
//								xueyang18 = "";
//							}
//						}
//					}
//				}
                
                
               
              
                
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

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
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivityXueyang.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
        case R.id.secure_connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            serverIntent = new Intent(this, DeviceListActivityXueyang.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            return true;
        case R.id.insecure_connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            serverIntent = new Intent(this, DeviceListActivityXueyang.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
            return true;
        case R.id.discoverable:
            // Ensure this device is discoverable by others
            ensureDiscoverable();
            return true;
        }
        return false;
    }

}
