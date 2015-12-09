package com.soloman.shuimian;

import java.io.IOException;
import java.util.Date;

import com.bluetooth.le.soloman.FileUtils;
import com.bluetooth.le.soloman.GlobalVar;
import com.bluetooth.le.soloman.R;
import com.bluetooth.le.soloman.gridUser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.TextView;

public class FragmentShuimian extends Fragment {

	private GlobalVar appState;
	
	private Button btn_shuimianselectuser, btn_sleep, btn_wakeup;
	private CheckBox checkB_record;
	private Chronometer chronometer1;
	private TextView tv_user1_shuimian, tv_user2_shuimian;
	private boolean recTag, isSleeping;//睡眠是否录音,正在睡眠
	private Cursor cursor = null;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		appState = (GlobalVar) getActivity().getApplicationContext(); // 获得全局变量
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//return inflater.inflate(R.layout.fragment_sleep, container, false);	
		
		View view = inflater.inflate(R.layout.fragment_shuimian, container, false);
		findView(view);	


				
        return view;       
	}
	
	@Override
	public void onStart(){
		super.onStart();
		if (isSleeping){
			btn_shuimianselectuser.setEnabled(false);
			btn_sleep.setEnabled(false);
	    	btn_wakeup.setEnabled(true);
		}else{
			btn_shuimianselectuser.setEnabled(true);
			btn_sleep.setEnabled(true);
	    	btn_wakeup.setEnabled(false);
		}
		
		appState.getDB();
		if (appState.userID == null || "".equals(appState.userID)){
			tv_user1_shuimian.setText("There is no user. If no user selected,");
			tv_user1_shuimian.setText("test result can not be saved.");			
			
			tv_user1_shuimian.setTextColor(Color.RED);
			tv_user1_shuimian.setTextColor(Color.RED);
		}else{
			tv_user1_shuimian.setTextColor(Color.WHITE);
			tv_user2_shuimian.setTextColor(Color.WHITE);

			tv_user1_shuimian.setText("ID:" + appState.userID + "    User Name:" + appState.userName);
			tv_user2_shuimian.setText("Note:" + appState.note);
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		if (isSleeping){
			chronometer1.stop();
	    	appState.recordingTime = SystemClock.elapsedRealtime() - chronometer1.getBase();// 保存这次记录了的时间

	    	if (recTag){
        		recStop();
        	}
	    	
	    	//强制变灰录音
    		checkB_record.setChecked(false);
    		checkB_record.setText("");
        	checkB_record.setTextColor(0xffa6a6a6);
        	recTag = false;
	    	
			//结束时间
			endTime = new Date(System.currentTimeMillis());
			String duration = timeDiff(startTime, endTime);//计算睡了多少小时，分钟
			
			//结束的时候更新这条记录
			appState.database.update_shuimian(startTime.toString(), endTime.toString(), duration);
		}
		
		appState.recordingTime = 0;
		appState.dbClose();
	}

	public void findView(View view){
		btn_shuimianselectuser = (Button) view.findViewById(R.id.btn_shuimianselectuser);
		btn_sleep = (Button) view.findViewById(R.id.btn_sleep);
		btn_wakeup = (Button) view.findViewById(R.id.btn_wakeup);
		chronometer1 = (Chronometer)view.findViewById(R.id.chronometer1);
		chronometer1.setFormat("%s");
		checkB_record = (CheckBox) view.findViewById(R.id.checkB_record);
		tv_user1_shuimian = (TextView) view.findViewById(R.id.tv_user1_shuimian);
		tv_user2_shuimian = (TextView) view.findViewById(R.id.tv_user2_shuimian);
		
		btn_shuimianselectuser.setOnClickListener(new Button.OnClickListener(){//创建监听    
            public void onClick(View v) {    
            	Intent it = new Intent(getActivity(), gridUser.class);
    			startActivityForResult(it, 1);	
            }
		});
		
		btn_sleep.setOnClickListener(new Button.OnClickListener(){//创建监听    
            public void onClick(View v) {  
            	// 开始时间
    			startTime = new Date(System.currentTimeMillis());
    			String year = String.valueOf(startTime.getYear() + 1900);
        		String mounth = String.valueOf(startTime.getMonth() + 1);
        		String day = String.valueOf(startTime.getDate());
        		String hour = String.valueOf(startTime.getHours());
        		String minite = String.valueOf(startTime.getMinutes());
        		String second = String.valueOf(startTime.getSeconds());
        		String filename = year + mounth + day + hour + minite + second;
        		
        		//强制录音
        		checkB_record.setChecked(true);
        		checkB_record.setText("Recording");
            	checkB_record.setTextColor(0xffffffff);
            	recTag = true;
    			if (recTag){
    				recfile = filename + ".amr";
    				// 录音				
    				recSound(recfile); //用开始时间做文件名
    			}
    			
    			if (!appState.database.isOpen()){
    				appState.getDB();
    			}
				//点开始的时候就写一条记录 ，避免丢失
				cursor = appState.database.getshuimian();
				
				if (appState.userID == null){
					appState.database.add_shuimian("NoUser", appState.sdcard + "/inurse/isleep/record/", recfile, startTime.toString(), "", "", "", "", "");
				}else {
					appState.database.add_shuimian(appState.userID, appState.sdcard + "/inurse/isleep/record/", recfile, startTime.toString(), "", "", "", "", "");
				}
				
				
//            	chronometer1.setBase(SystemClock.elapsedRealtime());  
            	chronometer1.setBase(SystemClock.elapsedRealtime() - appState.recordingTime);// 跳过已经记录了的时间，起到继续计时的作用
            	chronometer1.start(); 
            	isSleeping = true;
            	
            	btn_shuimianselectuser.setEnabled(false);
            	btn_sleep.setEnabled(false);
            	btn_wakeup.setEnabled(true);
            }
		});
		
		btn_wakeup.setOnClickListener(new Button.OnClickListener(){//创建监听    
            public void onClick(View v) {  
            	chronometer1.stop();
            	appState.recordingTime = SystemClock.elapsedRealtime() - chronometer1.getBase();// 保存这次记录了的时间

            	if (recTag){
            		recStop();
            	}
            	
            	//强制变灰录音
        		checkB_record.setChecked(false);
        		checkB_record.setText("");
            	checkB_record.setTextColor(0xffa6a6a6);
            	recTag = false;
            	
    			//结束时间
    			endTime = new Date(System.currentTimeMillis());
    			String duration = timeDiff(startTime, endTime);//计算睡了多少小时，分钟
    			
    			//结束的时候更新这条记录
        		appState.database.update_shuimian(startTime.toString(), endTime.toString(), duration);
    			
    			appState.recordingTime = 0;
    			isSleeping = false;
    			
    			btn_shuimianselectuser.setEnabled(true);
    			btn_sleep.setEnabled(true);
            	btn_wakeup.setEnabled(false);
            }
		});
				
		checkB_record.setEnabled(false);
		checkB_record.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){ 
            @Override
            public void onCheckedChanged(CompoundButton buttonView, 
                    boolean isChecked) { 
                // TODO Auto-generated method stub 
                recTag = isChecked;
//                if (isChecked){
//                	checkB_record.setText("Recording");
//                	checkB_record.setTextColor(0xffffffff);
//                }else{
//                	checkB_record.setText("");
//                	checkB_record.setTextColor(0xffa6a6a6);
//                }
            } 
        });
	}
	
	private int id = 0;
	private Date startTime, endTime;
	private String recfile = "";
	private MediaRecorder recorder;	
	public void recSound(String recfile){
		try {
			FileUtils file = new FileUtils();
			file.createSDDir("/inurse/isleep/record/");
			recorder = new MediaRecorder();
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			recorder.setOutputFile(appState.sdcard + "/inurse/isleep/record/" + recfile);
			recorder.prepare();
			recorder.start(); // 开始录音
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void recStop() {
		// TODO Auto-generated method stub
		if (recorder != null) {
			recorder.stop();
			recorder.reset();
			recorder.release();
			recorder = null;
        }
	}
	
	private int curSleepHour,curSleepMin;
	private String timeDiff(Date t1, Date t2) { // 时间差，分钟
		try {
			long diff = t2.getTime() - t1.getTime();// 这样得到的差值是微秒级别
			curSleepHour = (int) diff / (1000 * 60 * 60);
			curSleepMin = (int) (diff - (curSleepHour * (1000 * 60 * 60))) / (1000 * 60);
			System.out.println("" + curSleepHour + "小时" + curSleepMin + "分");
			
		} catch (Exception e) {
			curSleepHour = 0;
			curSleepMin = 0;
		}	
		return curSleepHour + "h" + curSleepMin + "m";
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
				
				
				tv_user1_shuimian.setTextColor(Color.WHITE);
				tv_user2_shuimian.setTextColor(Color.WHITE);

				tv_user1_shuimian.setText("ID:" + appState.userID + "    User Name:" + appState.userName);
				tv_user2_shuimian.setText("Note:" + appState.note);
			}
			
		}
	}
	
}



