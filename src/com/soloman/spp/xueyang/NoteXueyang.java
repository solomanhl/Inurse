package com.soloman.spp.xueyang;

import com.bluetooth.le.soloman.GlobalVar;
import com.bluetooth.le.soloman.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NoteXueyang extends Activity {
	public GlobalVar appState;
	
	private Button btn_ok;
	private TextView info;
	private EditText ed_listnote;
	private Bundle bundle;
	private String uid, date, note;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		appState = (GlobalVar) getApplicationContext(); // 获得全局变量
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note);

		setTitle("Note");
		findviews(); // 注册控件
				
	}

	private void findviews() {
		// TODO Auto-generated method stub
		btn_ok = (Button) findViewById(R.id.btn_ok);
		info = (TextView) findViewById(R.id.info);
		ed_listnote = (EditText) findViewById(R.id.ed_listnote);
		
		bundle = this.getIntent().getExtras();
		uid = bundle.getString("uid");
		date = bundle.getString("date");
		note = bundle.getString("note");
		info.setText(uid + "  " +  date );		
		ed_listnote.setText(note);
	}


	// 返回按钮事件
	public void btn_ok_onclick(View target) {
		// 返回主界面,回传参数
		int resultCode = -1;
		//更新数据库
		appState.database.updateXueyang(uid, date, ed_listnote.getText().toString());
		resultCode = 100;
		
		Intent rtnIntent = new Intent();
		NoteXueyang.this.setResult( resultCode, rtnIntent);
		NoteXueyang.this.finish();
	}
}
