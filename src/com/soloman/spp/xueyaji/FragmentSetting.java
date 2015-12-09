package com.soloman.spp.xueyaji;

import com.bluetooth.le.soloman.GlobalVar;
import com.bluetooth.le.soloman.R;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class FragmentSetting extends Fragment {

	public GlobalVar appState;
	public Cursor cursor = null;
	public CheckBox cb_autoDeleteRecord, cb_autosendmail, cb_autoupload, cb_autosave, cb_fielduid, cb_fieldfname, cb_fieldlname, cb_fielddevtype, cb_fielddevmac, cb_fieldceliangtime, cb_fieldvalue, cb_fieldmode, cb_fieldunit, cb_fieldnote;
	public RadioGroup rg_autodel, rg_automail;
	public RadioButton autodel0, autodel1, autodel2,autodel3, autodel4, automail0, automail1, automail2;
	public EditText et_mail1, et_mail2, et_mailtime1, et_mailtime2, et_autoupload;
	public TextView tv_autosave;
	public Spinner sp_fileformat, sp_separate;
	

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
		
		View view = inflater.inflate(R.layout.fragment_setting, container, false);
	
		findView(view);
		
//		view.setOnTouchListener(new View.OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				Log.i("info","FagmentThemometer onTouch");
//				return false;
//			}
//
//		});
		
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
				if ("sportdata".equals((String) msg.obj)) {

				}

			}
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
	
	@Override
	public void onResume(){
		super.onResume();
		appState.getDB();
		updateUI();
		setOnClickListener();
	}

	@Override
	public void onPause(){
		super.onPause();
		appState.mail1 = et_mail1.getText().toString();
		appState.mail2 = et_mail2.getText().toString();
		appState.mailtime1 = et_mailtime1.getText().toString();
		appState.mailtime2 = et_mailtime2.getText().toString();
		appState.serverurl = et_autoupload.getText().toString();
		appState.path = tv_autosave.getText().toString();
		
		if (!appState.isDBOpen()){
			appState.getDB();
		}
		
		appState.updateSetting(appState.autodel, appState.whendel, 
				appState.automail, appState.mail1, appState.mail2, appState.whenmail, appState.mailtime1, appState.mailtime2, 
				appState.autoupload, appState.serverurl, 
				appState.autosave, appState.path, appState.ext, appState.separate,
				appState.fielduid, appState.fieldfname, appState.fieldlname, appState.fielddevicetype, appState.fielddeviceid, appState.fielddate, appState.fieldvalue, appState.fieldmode, appState.fieldunit, appState.fieldnote);
		
		appState.dbClose();		
	}
	
	public void findView(View view){
		cb_autoDeleteRecord = (CheckBox) view.findViewById(R.id.cb_autoDeleteRecord);
		cb_autosendmail = (CheckBox) view.findViewById(R.id.cb_autosendmail);
		cb_autoupload = (CheckBox) view.findViewById(R.id.cb_autoupload);
		cb_autosave = (CheckBox) view.findViewById(R.id.cb_autosave);
		cb_fielduid = (CheckBox) view.findViewById(R.id.cb_fielduid);
		cb_fieldfname = (CheckBox) view.findViewById(R.id.cb_fieldfname);
		cb_fieldlname = (CheckBox) view.findViewById(R.id.cb_fieldlname);
		cb_fielddevtype = (CheckBox) view.findViewById(R.id.cb_fielddevtype);
		cb_fielddevmac = (CheckBox) view.findViewById(R.id.cb_fielddevmac);
		cb_fieldceliangtime = (CheckBox) view.findViewById(R.id.cb_fieldceliangtime);
		cb_fieldvalue = (CheckBox) view.findViewById(R.id.cb_fieldvalue);
		cb_fieldmode = (CheckBox) view.findViewById(R.id.cb_fieldmode);
		cb_fieldunit = (CheckBox) view.findViewById(R.id.cb_fieldunit);
		cb_fieldnote = (CheckBox) view.findViewById(R.id.cb_fieldnote);
		
		rg_autodel = (RadioGroup) view.findViewById(R.id.rg_autodel);
		rg_automail = (RadioGroup) view.findViewById(R.id.rg_automail);
		
		autodel0 = (RadioButton) view.findViewById(R.id.autodel0);
		autodel1 = (RadioButton) view.findViewById(R.id.autodel1);
		autodel2 = (RadioButton) view.findViewById(R.id.autodel2);
		autodel3 = (RadioButton) view.findViewById(R.id.autodel3);
		autodel4 = (RadioButton) view.findViewById(R.id.autodel4);
		automail0 = (RadioButton) view.findViewById(R.id.automail0);
		automail1 = (RadioButton) view.findViewById(R.id.automail1);
		automail2 = (RadioButton) view.findViewById(R.id.automail2);
		
		et_mail1 = (EditText) view.findViewById(R.id.et_mail1);
		et_mail2 = (EditText) view.findViewById(R.id.et_mail2);
		et_mailtime1 = (EditText) view.findViewById(R.id.et_mailtime1);
		et_mailtime2 = (EditText) view.findViewById(R.id.et_mailtime2);
		et_autoupload = (EditText) view.findViewById(R.id.et_autoupload);
		
		tv_autosave = (TextView) view.findViewById(R.id.tv_autosave);
		
		sp_fileformat = (Spinner) view.findViewById(R.id.sp_fileformat);
		sp_separate = (Spinner) view.findViewById(R.id.sp_separate);
	}

	private void updateUI() {
		// TODO Auto-generated method stub		
		cursor = appState.getSetting();
		
		if (cursor == null || cursor.getCount() == 0){
			//没有记录，插一条默认记录
			appState.autodel = 1;
			appState.whendel = "aftersend";
			
			appState.automail = 1;
			appState.mail1 = "";
			appState.mail2 = "";
			appState.whenmail = "dailly";
			appState.mailtime1 = "18";
			appState.mailtime2 = "00";
			
			appState.autoupload = 1;
			appState.serverurl = "http://";
			
			appState.autosave = 1;
			appState.path = appState.sdcard + "/inurse/record/";
			appState.ext = ".txt";
			appState.separate = ",";
			
			appState.fielduid = 1;
			appState.fieldfname = 1;
			appState.fieldlname = 1;
			appState.fielddevicetype = 1;
			appState.fielddeviceid = 1;
			appState.fielddate = 1;
			appState.fieldvalue = 1;
			appState.fieldmode = 1;
			appState.fieldunit = 1;
			appState.fieldnote = 1;
			
			appState.add_setting(appState.autodel, appState.whendel, 
					appState.automail, appState.mail1, appState.mail2, appState.whenmail, appState.mailtime1, appState.mailtime2, 
					appState.autoupload, appState.serverurl, 
					appState.autosave, appState.path, appState.ext, appState.separate,
					appState.fielduid, appState.fieldfname, appState.fieldlname, appState.fielddevicetype, appState.fielddeviceid, appState.fielddate, appState.fieldvalue, appState.fieldmode, appState.fieldunit, appState.fieldnote);
			
			autoDelEnable();
			autoMailEnable();
			autoUploadEnable();
			autoSaveEnable();						
			
			cb_autoDeleteRecord.setChecked(true);
			autodel3.setChecked(true);//aftersend
			cb_autosendmail.setChecked(true);
			automail0.setChecked(true);//dailly
			et_mailtime1.setText("18");
			et_mailtime2.setText("00");
			cb_autoupload.setChecked(true);
			et_autoupload.setText("http://" );
			cb_autosave.setChecked(true);
			tv_autosave.setText(appState.sdcard + "/inurse/record/");
			sp_fileformat.setSelection(0);	//.txt
			sp_separate.setSelection(0);	//,
			cb_fieldfname.setChecked(true);
			cb_fieldlname.setChecked(true);
			cb_fielddevtype.setChecked(true);
			cb_fielddevmac.setChecked(true);
			cb_fieldceliangtime.setChecked(true);
			cb_fieldvalue.setChecked(true);
			cb_fieldmode.setChecked(true);
			cb_fieldunit.setChecked(true);
			cb_fieldnote.setChecked(true);
		}else{ //有记录就读记录
			cursor.moveToNext();
			
			appState.autodel = cursor.getInt(0);
			appState.whendel = cursor.getString(1);
			
			appState.automail = cursor.getInt(2);;
			appState.mail1 = cursor.getString(3);
			appState.mail2 = cursor.getString(4);
			appState.whenmail = cursor.getString(5);
			appState.mailtime1 = cursor.getString(6);
			appState.mailtime2 = cursor.getString(7);
			
			appState.autoupload = cursor.getInt(8);
			appState.serverurl = cursor.getString(9);
			
			appState.autosave = cursor.getInt(10);
			appState.path = cursor.getString(11);
			appState.ext = cursor.getString(12);
			appState.separate = cursor.getString(13);
			
			appState.fielduid = cursor.getInt(14);
			appState.fieldfname = cursor.getInt(15);
			appState.fieldlname = cursor.getInt(16);
			appState.fielddevicetype = cursor.getInt(17);
			appState.fielddeviceid = cursor.getInt(18);
			appState.fielddate = cursor.getInt(19);
			appState.fieldvalue = cursor.getInt(20);
			appState.fieldmode = cursor.getInt(21);
			appState.fieldunit = cursor.getInt(22);
			appState.fieldnote = cursor.getInt(23);
			
			if (appState.autodel == 1){
				autoDelEnable();
				cb_autoDeleteRecord.setChecked(true);
				if("dailly".equals(appState.whendel)){
					autodel0.setChecked(true);
				}else if("weekly".equals(appState.whendel)){
					autodel1.setChecked(true);
				}else if("monthly".equals(appState.whendel)){
					autodel2.setChecked(true);
				}else if("aftersend".equals(appState.whendel)){
					autodel3.setChecked(true);
				}else if("off".equals(appState.whendel)){
					autodel4.setChecked(true);
				}
			}else{
				cb_autoDeleteRecord.setChecked(false);
				autoDelDisable();
			}
			
			if (appState.automail == 1){
				autoMailEnable();
				cb_autosendmail.setChecked(true);
				et_mail1.setText(appState.mail1);
				et_mail2.setText(appState.mail2);
				if("dailly".equals(appState.whenmail)){
					automail0.setChecked(true);
				}else if("weekly".equals(appState.whenmail)){
					automail1.setChecked(true);
				}else if("monthly".equals(appState.whenmail)){
					automail2.setChecked(true);
				}
				et_mailtime1.setText(appState.mailtime1);
				et_mailtime2.setText(appState.mailtime2);
			}else{
				cb_autosendmail.setChecked(false);
				autoMailDisable();
			}
			
			if (appState.autoupload == 1){
				autoUploadEnable();
				cb_autoupload.setChecked(true);
				et_autoupload.setText(appState.serverurl);				
			}else{
				cb_autoupload.setChecked(false);
				autoUploadDisable();
			}
			
			if (appState.autosave == 1){
				autoSaveEnable();
				cb_autosave.setChecked(true);
				tv_autosave.setText(appState.path);	
				if(".txt".equals(appState.ext)){
					sp_fileformat.setSelection(0);
				}else if(".xls".equals(appState.ext)){
					sp_fileformat.setSelection(1);
				}		
				
				if(",".equals(appState.separate)){
					sp_separate.setSelection(0);
				}else if(";".equals(appState.separate)){
					sp_separate.setSelection(1);
				}
				else if(" ".equals(appState.separate)){
					sp_separate.setSelection(2);
				}else if("/".equals(appState.separate)){
					sp_separate.setSelection(3);
				}else if("|".equals(appState.separate)){
					sp_separate.setSelection(4);
				}else if("$".equals(appState.separate)){
					sp_separate.setSelection(5);
				}else if("#".equals(appState.separate)){
					sp_separate.setSelection(6);
				}else if("*".equals(appState.separate)){
					sp_separate.setSelection(7);
				}
			}else{
				cb_autosave.setChecked(false);
				autoSaveDisable();
			}
			
			if (appState.fielduid == 1){
				cb_fielduid.setChecked(true);
			}else{
				cb_fielduid.setChecked(false);
			}
			if (appState.fieldfname == 1){
				cb_fieldfname.setChecked(true);
			}else{
				cb_fieldfname.setChecked(false);
			}
			if (appState.fieldlname == 1){
				cb_fieldlname.setChecked(true);
			}else{
				cb_fieldlname.setChecked(false);
			}
			if (appState.fielddevicetype == 1){
				cb_fielddevtype.setChecked(true);
			}else{
				cb_fielddevtype.setChecked(false);
			}
			if (appState.fielddeviceid == 1){
				cb_fielddevmac.setChecked(true);
			}else{
				cb_fielddevmac.setChecked(false);
			}
			if (appState.fielddate == 1){
				cb_fieldceliangtime.setChecked(true);
			}else{
				cb_fieldceliangtime.setChecked(false);
			}
			if (appState.fieldvalue == 1){
				cb_fieldvalue.setChecked(true);
			}else{
				cb_fieldvalue.setChecked(false);
			}
			if (appState.fieldmode == 1){
				cb_fieldmode.setChecked(true);
			}else{
				cb_fieldmode.setChecked(false);
			}
			if (appState.fieldunit == 1){
				cb_fieldunit.setChecked(true);
			}else{
				cb_fieldunit.setChecked(false);
			}
			if (appState.fieldnote == 1){
				cb_fieldnote.setChecked(true);
			}else{
				cb_fieldnote.setChecked(false);
			}
			
			cursor.close();
		}
	}
	
	private void setOnClickListener() {
		// TODO Auto-generated method stub
		cb_autoDeleteRecord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					autoDelEnable();
					cb_autoDeleteRecord.setChecked(true);
					appState.autodel = 1; //true
				} else {
					autoDelDisable();
					cb_autoDeleteRecord.setChecked(false);
					appState.autodel = 0; //false			
					
				}
			}
		});	
		
		rg_autodel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == R.id.autodel0) {
					appState.whendel = "dailly"; 
				}else if (checkedId == R.id.autodel1) {
					appState.whendel = "weekly"; 
				}else if (checkedId == R.id.autodel2) {
					appState.whendel = "monthly"; 
				}else if (checkedId == R.id.autodel3) {
					appState.whendel = "aftersend"; 
				}else if (checkedId == R.id.autodel4) {
					appState.whendel = "off"; 
				}
			}
		});	
		
		cb_autosendmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					autoMailEnable();
					cb_autosendmail.setChecked(true);
					appState.automail = 1; //true
				} else {
					autoMailDisable();
					cb_autosendmail.setChecked(false);
					appState.automail = 0; //false			
					
				}
			}
		});	
		
		rg_automail.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == R.id.automail0) {
					appState.whenmail = "dailly"; 
				}else if (checkedId == R.id.automail1) {
					appState.whenmail = "weekly"; 
				}else if (checkedId == R.id.automail2) {
					appState.whenmail = "monthly"; 
				}
			}
		});	
		
		cb_autoupload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					autoUploadEnable();
					cb_autoupload.setChecked(true);
					appState.autoupload = 1; //true
				} else {
					autoUploadDisable();
					cb_autoupload.setChecked(false);
					appState.autoupload = 0; //false			
					
				}
			}
		});	
		
		cb_autosave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					autoSaveEnable();
					cb_autosave.setChecked(true);
					appState.autosave = 1; //true
				} else {
					autoSaveDisable();
					cb_autosave.setChecked(false);
					appState.autosave = 0; //false			
					
				}
			}
		});	
		
		sp_fileformat.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0){
					appState.ext = ".txt";
				}else if (position == 1){
					appState.ext = ".xls";
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		sp_separate.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0){
					appState.separate = ",";
				}else if (position == 1){
					appState.separate = ";";
				}else if (position == 2){
					appState.separate = " ";
				}else if (position == 3){
					appState.separate = "/";
				}else if (position == 4){
					appState.separate = "|";
				}else if (position == 5){
					appState.separate = "$";
				}else if (position == 6){
					appState.separate = "#";
				}else if (position == 7){
					appState.separate = "*";
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		cb_fielduid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					cb_fielduid.setChecked(true);
					appState.fielduid = 1; //true
				} else {
					cb_fielduid.setChecked(false);
					appState.fielduid = 0; //false					
				}
			}
		});
		
		cb_fieldfname.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					cb_fieldfname.setChecked(true);
					appState.fieldfname = 1; //true
				} else {
					cb_fieldfname.setChecked(false);
					appState.fieldfname = 0; //false					
				}
			}
		});
		
		cb_fieldlname.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					cb_fieldlname.setChecked(true);
					appState.fieldlname = 1; //true
				} else {
					cb_fieldlname.setChecked(false);
					appState.fieldlname = 0; //false					
				}
			}
		});
		
		cb_fielddevtype.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					cb_fielddevtype.setChecked(true);
					appState.fielddevicetype = 1; //true
				} else {
					cb_fielddevtype.setChecked(false);
					appState.fielddevicetype = 0; //false					
				}
			}
		});
		
		cb_fielddevmac.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					cb_fielddevmac.setChecked(true);
					appState.fielddeviceid = 1; //true
				} else {
					cb_fielddevmac.setChecked(false);
					appState.fielddeviceid = 0; //false					
				}
			}
		});
		
		cb_fieldceliangtime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					cb_fieldceliangtime.setChecked(true);
					appState.fielddate = 1; //true
				} else {
					cb_fieldceliangtime.setChecked(false);
					appState.fielddate = 0; //false					
				}
			}
		});
		
		cb_fieldvalue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					cb_fieldvalue.setChecked(true);
					appState.fieldvalue = 1; //true
				} else {
					cb_fieldvalue.setChecked(false);
					appState.fieldvalue = 0; //false					
				}
			}
		});
		
		cb_fieldmode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					cb_fieldmode.setChecked(true);
					appState.fieldmode = 1; //true
				} else {
					cb_fieldmode.setChecked(false);
					appState.fieldmode = 0; //false					
				}
			}
		});
		
		cb_fieldunit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					cb_fieldunit.setChecked(true);
					appState.fieldunit = 1; //true
				} else {
					cb_fieldunit.setChecked(false);
					appState.fieldunit = 0; //false					
				}
			}
		});
		
		cb_fieldnote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					cb_fieldnote.setChecked(true);
					appState.fieldnote = 1; //true
				} else {
					cb_fieldnote.setChecked(false);
					appState.fieldnote = 0; //false					
				}
			}
		});
	
	}
	
	public void autoDelEnable(){
		rg_autodel.setEnabled(true);	
		autodel0.setEnabled(true);
		autodel1.setEnabled(true);
		autodel2.setEnabled(true);
		autodel3.setEnabled(true);
		autodel4.setEnabled(true);
	}
	
	public void autoDelDisable(){
		rg_autodel.setEnabled(false);	
		autodel0.setEnabled(false);
		autodel1.setEnabled(false);
		autodel2.setEnabled(false);
		autodel3.setEnabled(false);
		autodel4.setEnabled(false);
	}
	
	public void autoMailEnable(){
		et_mail1.setEnabled(true);
		et_mail2.setEnabled(true);
		rg_automail.setEnabled(true);
		automail0.setEnabled(true);
		automail1.setEnabled(true);
		automail2.setEnabled(true);
		et_mailtime1.setEnabled(true);
		et_mailtime2.setEnabled(true);
	}
	
	public void autoMailDisable(){
		et_mail1.setEnabled(false);
		et_mail2.setEnabled(false);
		rg_automail.setEnabled(false);
		automail0.setEnabled(false);
		automail1.setEnabled(false);
		automail2.setEnabled(false);
		et_mailtime1.setEnabled(false);
		et_mailtime2.setEnabled(false);
	}
	
	public void autoUploadEnable(){
		et_autoupload.setEnabled(true);
	}
	
	public void autoUploadDisable(){
		et_autoupload.setEnabled(false);
	}
	
	public void autoSaveEnable(){
		tv_autosave.setEnabled(true);
		sp_fileformat.setEnabled(true);
		sp_separate.setEnabled(true);
	}
	
	public void autoSaveDisable(){
		tv_autosave.setEnabled(false);
		sp_fileformat.setEnabled(false);
		sp_separate.setEnabled(false);
	}
	
}



