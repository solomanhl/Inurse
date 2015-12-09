package com.bluetooth.le.soloman;

import java.util.Date;

import com.bluetooth.le.soloman.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FragmentThemometer extends Fragment {

	public GlobalVar appState;
	public Button btn_getTemp, btn_selectuser, btn_swichcewenmode, btn_swichcewenunit;
	public TextView tv_user1_cewen, tv_user2_cewen, tv_device_cewen, tv_tempre, tv_cewenwendu, tv_cewenunit, tv_cewennum;
	public byte mode, unit;
	public int cnttotal, cntbody, cntsurface, cntroom;
	public int ti, tj;
	public Cursor cursor = null;
	
	public listenWenduThread t;
	
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
		
		View view = inflater.inflate(R.layout.fragment_themometer, container, false);
		
		ti = 0;
		tj = 0;
		findView(view);
		
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i("info","FagmentThemometer onTouch");
				if (appState.BluetoothAdapter!=null && appState.BluetoothAdapter.isEnabled()){	
					//如果蓝牙是开着的，启用测温、选择模式、单位等功能
					cewen();
				}
				
				return false;
			}

		});
		
		// 得到当前线程的Looper实例，由于当前线程是UI线程也可以通过Looper.getMainLooper()得到
		Looper looper = Looper.myLooper();
		// 此处甚至可以不需要设置Looper，因为 Handler默认就使用当前线程的Looper
		messageHandler = new MessageHandler(looper);
		
		cntbody = 0;
		cntsurface = 0;
		cntroom = 0;
		cnttotal = cntbody + cntsurface + cntroom;			
		
        return view;       
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
					if (appState.dataArrive){
						updateWendu();
					}					
				}

			}
		}
	}
	
	@Override
	public void onStart(){
		super.onStart();
		appState.getDB();
		appState.runThread = true;
		appState.dataArrive = false;
		t = new listenWenduThread();
		t.start();
		
		if (appState.userID == null || "".equals(appState.userID)){
			tv_user1_cewen.setText("There is no user. If no user selected,");
			tv_user2_cewen.setText("test result can not be saved.");			
			
			tv_user1_cewen.setTextColor(Color.RED);
			tv_user2_cewen.setTextColor(Color.RED);
		}else{
			updateUI();	
		}
		tv_device_cewen.setText("Device ID:" + appState.deviceAddress);
	}
	
	@Override
	public void onStop(){
		super.onStop();
		appState.dbClose();
		appState.runThread = false;
		try {
			if (t!=null){
				t.sleep(1);
				t.interrupt();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/*
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		if (mAudioCapture != null) {
            mAudioCapture.stop();
            mAudioCapture.release();
            mAudioCapture = null;
        }
	}
	
	public void onClose() {
		if (mAudioCapture != null) {
            mAudioCapture.stop();
            mAudioCapture.release();
            mAudioCapture = null;
        }
	}
	*/
	
	public void findView(View view){
		btn_getTemp = (Button) view.findViewById(R.id.btn_getTemp);
		btn_selectuser  = (Button) view.findViewById(R.id.btn_selectuser);
		btn_swichcewenmode  = (Button) view.findViewById(R.id.btn_swichcewenmode);
		btn_swichcewenunit = (Button) view.findViewById(R.id.btn_swichcewenunit);
		tv_tempre = (TextView) view.findViewById(R.id.tv_tempre);
		tv_user1_cewen = (TextView) view.findViewById(R.id.tv_user1_cewen);
		tv_user2_cewen = (TextView) view.findViewById(R.id.tv_user2_cewen);
		tv_device_cewen = (TextView) view.findViewById(R.id.tv_device_cewen);
		tv_cewenwendu = (TextView) view.findViewById(R.id.tv_cewenwendu);
		tv_cewenunit = (TextView) view.findViewById(R.id.tv_cewenunit);
		tv_cewennum = (TextView) view.findViewById(R.id.tv_cewennum);
		

		if (appState.BluetoothAdapter!=null && appState.BluetoothAdapter.isEnabled()){	
			//如果蓝牙是开着的，启用测温、选择模式、单位等功能
			btn_getTemp.setOnClickListener(new Button.OnClickListener(){//创建监听    
	            public void onClick(View v) {    
	            	cewen();
	            }  
	        });  
			
			//切换模式0body 1surface 2room
			btn_swichcewenmode.setOnClickListener(new Button.OnClickListener(){//创建监听    
	            public void onClick(View v) {    
	            	ti ++;
	            	appState.setMode(appState.gattCharacteristic_send, (byte) (ti % 3 & 0xff), (byte) (tj % 2 & 0xff) );
	            }
			});
			
			btn_swichcewenunit.setOnClickListener(new Button.OnClickListener(){//创建监听    
	            public void onClick(View v) {    
	            	tj ++;
	            	if (tj %2 ==0){
	            		tv_cewenunit.setText("℃");
	            	}else if (tj % 2 ==1){
	            		tv_cewenunit.setText("℉");
	            	}
	            	appState.setMode(appState.gattCharacteristic_send, (byte) (ti % 3 & 0xff), (byte) (tj % 2 & 0xff) );
	            	tv_cewenwendu.setText("- -");
	            }
			});
		}		  
		
		btn_selectuser.setOnClickListener(new Button.OnClickListener(){//创建监听    
            public void onClick(View v) {    
            	Intent it = new Intent(getActivity(), gridUser.class);
    			startActivityForResult(it, 1);	
            }
		});
				
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
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
				tv_user1_cewen.setTextColor(Color.BLACK);
				tv_user2_cewen.setTextColor(Color.BLACK);
				
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
			
			tv_user1_cewen.setText("ID:" + appState.userID + "    User Name:" + appState.userName);
			tv_user2_cewen.setText("Note:" + appState.note);
			
			cntbody = cntsurface = cntroom = 0;
			//读测量次数
			cursor = appState.getRecord(appState.userID, "1", "body");
			if (cursor != null && cursor.getCount() > 0){
				cntbody = cursor.getCount();
				cursor.close();
			}
			cursor = appState.getRecord(appState.userID, "1", "surface");
			if (cursor != null && cursor.getCount() > 0){
				cntsurface = cursor.getCount();
				cursor.close();
			}
			cursor = appState.getRecord(appState.userID, "1", "room");
			if (cursor != null && cursor.getCount() > 0){
				cntroom = cursor.getCount();
				cursor.close();
			}
			cnttotal = cntbody + cntsurface + cntroom;
			tv_cewennum.setText("Record Total:" + String.valueOf(cnttotal)
					+ "  body:" + String.valueOf(cntbody) 
					+ "  surface:" + String.valueOf(cntsurface) 
					+ "  room:" + String.valueOf(cntroom));
		}	
	}
	
	public void updateWendu(){
		appState.dataArrive = false;
		tv_tempre.setText("表温：" + String.valueOf(appState.surface) +
    			"，体温：" + String.valueOf(appState.body) +
    			"，环温："+ String.valueOf(appState.room) +
    			"，模式："+ appState.mode +
    			"，单位："+ appState.unit );
    	
				if ("body".equals(appState.mode)) {
					tv_cewenwendu.setText(String.valueOf(appState.body));
					ti = 0;
					cntbody++;
				} else if ("surface".equals(appState.mode)) {
					tv_cewenwendu.setText(String.valueOf(appState.surface));
					ti = 1;
					cntsurface++;
				} else if ("room".equals(appState.mode)) {
					tv_cewenwendu.setText(String.valueOf(appState.room));
					ti = 2;
					cntroom++;
				}
				cnttotal = cntbody + cntsurface + cntroom;

				if (appState.mode != null) {
					if ("℃".equals(appState.unit)) {
						tj = 0;
					}else if ("℉".equals(appState.unit)) {
						tj = 1;
					}
					tv_cewenunit.setText(appState.unit);
					tv_cewennum.setText("Record Total:" + String.valueOf(cnttotal)
							+ "  body:" + String.valueOf(cntbody) 
							+ "  surface:" + String.valueOf(cntsurface) 
							+ "  room:" + String.valueOf(cntroom));
				}
				
				
				//如果选择了病人（user），写数据库
				if (appState.userID != null && !"".equals(appState.userID) && appState.mode != null){
					Date dt = new Date(System.currentTimeMillis());
					Time tm = new Time ();
					appState.add_Record(appState.userID, "1", appState.mode, appState.unit, tv_cewenwendu.getText().toString(), dt.toString(), tm.toString());
					
					tv_user1_cewen.setTextColor(Color.BLACK);
					tv_user2_cewen.setTextColor(Color.BLACK);
				};
	}
	
	public void cewen(){
		Log.i("info", "点击侧温度");
        appState.getTemp(appState.gattCharacteristic_send);
        Log.i("info", "已调用测温度方法");
//        messageHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//            	updateWendu();
//            }
//        }, 2000);
	}
	
	
	// 更新数据进程----------------------------------------
		public class listenWenduThread extends Thread {
			public listenWenduThread() {
				if (!appState.runThread){
					appState.runThread = true;	
				}
				
			}

			
			@Override
			public void run() {
				while (appState.runThread && !this.isInterrupted()) {
//					System.out.println("sportDataThread run again");
					updateHandler("wendudata");
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}
		//==================end thread
		
}



