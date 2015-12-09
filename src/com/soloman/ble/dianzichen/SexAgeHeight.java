package com.soloman.ble.dianzichen;

import com.bluetooth.le.soloman.GlobalVar;
import com.bluetooth.le.soloman.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SexAgeHeight extends Activity {
	public GlobalVar appState;
	public RadioGroup radioGroup1;
	public RadioButton radio_male, radio_female;
	public EditText et_age, et_hei;
	public Button ageok;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		appState = (GlobalVar) getApplicationContext(); // 获得全局变量
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sexageheight);

		setTitle("Sex  Age  Height");
		findviews(); // 注册控件
				
	}
	
	private void findviews() {
		// TODO Auto-generated method stub
		radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
		radio_male = (RadioButton) findViewById(R.id.radio_male);
		radio_female = (RadioButton) findViewById(R.id.radio_female);
		et_age = (EditText) findViewById(R.id.et_age);
		et_hei = (EditText) findViewById(R.id.et_hei);
		ageok = (Button) findViewById(R.id.ageok);
	}
	
	// Male  radiobutton点击事件
	public void radio_male_onclick(View target) {
		appState.userSex = "M";// 男
	}
	
	// Male  radiobutton点击事件
	public void radio_female_onclick(View target) {
		appState.userSex = "F";// nv
	}
		
	// 返回按钮事件
	public void ageok_onclick(View target) {
		appState.userAge = et_age.getText().toString();
		appState.userHeight = et_hei.getText().toString();
		if ( !"".equals(appState.userAge)  &&  !"".equals(appState.userHeight)  &&  !"".equals(appState.userID) ){
			// 更新数据库
			appState.database.Update_patient_ext(appState.userID, appState.userSex, appState.userAge, appState.userHeight);
		}
		// 返回主界面,回传参数
		int resultCode = -1;
		
		resultCode = 101;

		Intent rtnIntent = new Intent();
		rtnIntent.putExtra("uid", appState.userID);
		rtnIntent.putExtra("age", appState.userAge);
		rtnIntent.putExtra("height", appState.userHeight);
		this.setResult(resultCode, rtnIntent);
		this.finish();
	}
}
