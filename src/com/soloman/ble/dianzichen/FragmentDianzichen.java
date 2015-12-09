package com.soloman.ble.dianzichen;

import java.util.Date;

import com.bluetooth.le.soloman.GlobalVar;
import com.bluetooth.le.soloman.R;
import com.bluetooth.le.soloman.gridUser;

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
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentDianzichen extends Fragment {

	public GlobalVar appState;
	public Button btn_selectuser_dianzichen;
	public TextView tv_user1_dianzichen, tv_user2_dianzichen, tv_device_dianzichen, tv_data_dianzichen;
	public TextView tv_sex, tv_age, tv_height, textView_tizohng, textView_bmi, textView_zhifang, textView_jirou, textView_shuifen, textView_gutou, textView_kaluli;
	public ImageView imageView_infoedt;
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
		
		View view = inflater.inflate(R.layout.fragment_dianzichen, container, false);
		
		findView(view);
		
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i("info","FagmentDianzichen onTouch");
				if (appState.BluetoothAdapter!=null && appState.BluetoothAdapter.isEnabled()){	
					//如果蓝牙是开着的，启用测量体重功能
					sendPeople();
				}
				
				return false;
			}

		});
		
		// 得到当前线程的Looper实例，由于当前线程是UI线程也可以通过Looper.getMainLooper()得到
		Looper looper = Looper.myLooper();
		// 此处甚至可以不需要设置Looper，因为 Handler默认就使用当前线程的Looper
		messageHandler = new MessageHandler(looper);
		
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
				if ("tizhongdata".equals((String) msg.obj)) {
					if (appState.dataArrive){
						updateData();
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
			tv_user1_dianzichen.setText("There is no user. If no user selected,");
			tv_user2_dianzichen.setText("test result can not be saved.");			
			
			tv_user1_dianzichen.setTextColor(Color.RED);
			tv_user2_dianzichen.setTextColor(Color.RED);
		}else{
			updateUI();	
		}
		tv_device_dianzichen.setText("Device ID:" + appState.deviceAddress);
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
		btn_selectuser_dianzichen  = (Button) view.findViewById(R.id.btn_selectuser_dianzichen);
		tv_user1_dianzichen = (TextView) view.findViewById(R.id.tv_user1_dianzichen);
		tv_user2_dianzichen = (TextView) view.findViewById(R.id.tv_user2_dianzichen);
		tv_device_dianzichen = (TextView) view.findViewById(R.id.tv_device_dianzichen);
		tv_sex = (TextView) view.findViewById(R.id.tv_sex);
		tv_age = (TextView) view.findViewById(R.id.tv_age);
		tv_height = (TextView) view.findViewById(R.id.tv_height);
		textView_tizohng = (TextView) view.findViewById(R.id.textView_tizohng);
		textView_bmi = (TextView) view.findViewById(R.id.textView_bmi);
		textView_zhifang = (TextView) view.findViewById(R.id.textView_zhifang);
		textView_jirou = (TextView) view.findViewById(R.id.textView_jirou);
		textView_shuifen = (TextView) view.findViewById(R.id.textView_shuifen);
		textView_gutou = (TextView) view.findViewById(R.id.textView_gutou);
		textView_kaluli = (TextView) view.findViewById(R.id.textView_kaluli);
		tv_data_dianzichen = (TextView) view.findViewById(R.id.tv_data_dianzichen);
		imageView_infoedt = (ImageView) view.findViewById(R.id.imageView_infoedt);

		if (appState.BluetoothAdapter!=null && appState.BluetoothAdapter.isEnabled()){	
			//如果蓝牙是开着的，启用测温、选择模式、单位等功能
			
		}		  
		
		btn_selectuser_dianzichen.setOnClickListener(new Button.OnClickListener(){//创建监听    
            public void onClick(View v) {    
            	Intent it = new Intent(getActivity(), gridUser.class);
    			startActivityForResult(it, 1);	
            }
		});
			
		//编辑性别 年龄 身高
		imageView_infoedt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent(getActivity(), SexAgeHeight.class);
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
				tv_user1_dianzichen.setTextColor(Color.BLACK);
				tv_user2_dianzichen.setTextColor(Color.BLACK);
				
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
			
			tv_user1_dianzichen.setText("ID:" + appState.userID + "    User Name:" + appState.userName);
			tv_user2_dianzichen.setText("Note:" + appState.note);
			tv_sex.setText(appState.userSex);
			tv_age.setText(appState.userAge);
			tv_height.setText(appState.userHeight);
		}	
	}
	
	public void updateData(){
		appState.dataArrive = false;
				
		textView_tizohng.setText(String.valueOf(appState.weight));
		textView_bmi.setText(String.valueOf(appState.bmi));
		textView_zhifang.setText(String.valueOf(appState.fat));
		textView_jirou.setText(String.valueOf(appState.muscle));
		textView_shuifen.setText(String.valueOf(appState.water));
		textView_gutou.setText(String.valueOf(appState.bon));
		textView_kaluli.setText(String.valueOf(appState.kcal));
		
		tv_data_dianzichen.setText("Weight:" + String.valueOf(appState.weight)
				+"Fat:" + String.valueOf(appState.fat)
				+"Water:" + String.valueOf(appState.water)
				+"Muscle:" + String.valueOf(appState.muscle)
				+"Bone:" + String.valueOf(appState.bon)
				+"Calorie:" + String.valueOf(appState.kcal)
				+"BMI:" + String.valueOf(appState.bmi));
		// 如果选择了病人（user）且是最终的详细数据，写数据库
		if (appState.userID != null && !"".equals(appState.userID) && appState.finalweightflag ) {
			textView_tizohng.setText(String.valueOf(appState.finalweight));
			Date dt = new Date(System.currentTimeMillis());
			Time tm = new Time();
			appState.database.addDianzichen(appState.userID, "1", 
					String.valueOf(appState.weight), "kg", String.valueOf(appState.fat), 
					String.valueOf(appState.water), String.valueOf(appState.muscle), 
					String.valueOf(appState.bon), String.valueOf(appState.kcal), 
					String.valueOf(appState.bmi), dt.toString(), tm.toString());

			tv_user1_dianzichen.setTextColor(Color.BLACK);
			tv_user2_dianzichen.setTextColor(Color.BLACK);
			
			appState.finalweightflag = false;
		}
	}
	
	public void sendPeople(){
		Log.i("info", "点击测体重");
        appState.getWeight(appState.gattCharacteristic_send_dianzichen);
        Log.i("info", "已调用测体重方法");
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
					updateHandler("tizhongdata");
					
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



