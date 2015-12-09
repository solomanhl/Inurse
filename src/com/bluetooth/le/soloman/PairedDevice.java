package com.bluetooth.le.soloman;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class PairedDevice extends Activity{

	public GlobalVar appState;
	public TextView paired_ther, paired_bp, paired_scale,paired_oxi;
	public Button del_ther, del_bp, paired_rtn, del_scale,del_oxi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		appState = (GlobalVar) getApplicationContext(); // 获得全局变量
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置成竖屏		
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 	//去掉title
		setContentView(R.layout.paireddevice);
		
		findView();
		setOnClickListener();
		readDevice();
	}

	private void readDevice() {
		// TODO Auto-generated method stub
		//Thermometer温度计
        if ( appState.file.isFileExist("inurse/Thermometer.txt") ){
        	String lastDevice =  appState.file.readFile(appState.file.SDPATH + "inurse/Thermometer.txt");
        	paired_ther.setText(lastDevice);
        }
      //血压计
        if ( appState.file.isFileExist("inurse/Xueya.txt") ){
        	String lastDevice =  appState.file.readFile(appState.file.SDPATH + "inurse/Xueya.txt");
        	paired_bp.setText(lastDevice);
        }
      //电子秤
		if (appState.file.isFileExist("inurse/Dianzichen.txt")) {
			String lastDevice = appState.file.readFile(appState.file.SDPATH + "inurse/Dianzichen.txt");
			paired_scale.setText(lastDevice);
		}
		//血氧仪
        if ( appState.file.isFileExist("inurse/Xueyang.txt") ){
        	String lastDevice =  appState.file.readFile(appState.file.SDPATH + "inurse/Xueyang.txt");
        	paired_oxi.setText(lastDevice);
        }
	}

	private void setOnClickListener() {
		// TODO Auto-generated method stub
		//温度计
		del_ther.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				appState.file.deleteFile(appState.file.SDPATH + "inurse/Thermometer.txt");
				paired_ther.setText("");
			}
		});
		
		//血压计
		del_bp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				appState.file.deleteFile(appState.file.SDPATH + "inurse/Xueya.txt");
				paired_bp.setText("");
			}
		});
		
		//电子秤
		del_scale.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				appState.file.deleteFile(appState.file.SDPATH + "inurse/Dianzichen.txt");
				paired_scale.setText("");
			}
		});
		
		//血氧仪
		del_oxi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				appState.file.deleteFile(appState.file.SDPATH + "inurse/Xueyang.txt");
				paired_oxi.setText("");
			}
		});
		
		paired_rtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void findView() {
		// TODO Auto-generated method stub
		//温度计
		paired_ther = (TextView) findViewById(R.id.paired_ther);
		del_ther = (Button) findViewById(R.id.del_ther);
		//血压计
		paired_bp = (TextView) findViewById(R.id.paired_bp);
		del_bp = (Button) findViewById(R.id.del_bp);
		//电子秤
		paired_scale = (TextView) findViewById(R.id.paired_scale);
		del_scale = (Button) findViewById(R.id.del_scale);
		//血氧仪
		paired_oxi = (TextView) findViewById(R.id.paired_oxi);
		del_oxi = (Button) findViewById(R.id.del_oxi);
		
		paired_rtn = (Button) findViewById(R.id.paired_rtn);
	}
}
