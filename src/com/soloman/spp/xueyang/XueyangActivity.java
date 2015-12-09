package com.soloman.spp.xueyang;

import com.bluetooth.le.soloman.GlobalVar;
import com.bluetooth.le.soloman.R;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.content.pm.ActivityInfo;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class XueyangActivity extends FragmentActivity {

	public GlobalVar appState;
	
	public android.support.v4.app.FragmentTransaction ft;
	public FragmentXueyang fragmentXueyang;
//	public FragmentXueyangGraph fragmentXueyangGraph;
//	public FragmentXueyangData fragmentXueyangData;
//	public FragmentXueyangHelp fragmentXueyangHelp;
//	public FragmentSetting fragmentSetting;


	public MediaRecorder recorder;	
//	public ViewPager viewPager;	
	public FrameLayout frag_container;
	public ImageView iv_fanhui, iv_celianghome, iv_graph, iv_data, iv_celianghelp, iv_celiangsetting;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		appState = (GlobalVar) getApplicationContext(); // 获得全局变量
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置成竖屏		
		appState.keepScreenAlive();
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 	//去掉title
		setContentView(R.layout.celiang_activity);

		
		findView();
		setOnClickListener();
//		initViewPager();
		
		
		android.support.v4.app.FragmentManager fm =  getSupportFragmentManager();
		ft = fm.beginTransaction();
		
		fragmentXueyang = (FragmentXueyang) fm.findFragmentById(R.layout.frag_xueyang);
		
		ft.add(R.id.frag_container,new FragmentXueyang(), "home");
//		ft.add(R.id.viewPager,new FragmentThemometerGraph(), "graph");
		
		//ft.replace(R.id.viewPager, fragmentSleep);
		ft.commit();	

		iv_celianghome.setBackgroundResource(R.drawable.home1);
		iv_graph.setBackgroundResource(R.drawable.graph);
		iv_data.setBackgroundResource(R.drawable.data);
		iv_celianghelp.setBackgroundResource(R.drawable.help);
		iv_celiangsetting.setBackgroundResource(R.drawable.setting);

	}

	public void replaceFrag(Fragment f){
		android.support.v4.app.FragmentManager fm =  getSupportFragmentManager();
		ft = fm.beginTransaction();
		ft.replace(R.id.frag_container,f);
		ft.commit();
	}
	
	private void setOnClickListener() {
		// TODO Auto-generated method stub
		iv_fanhui.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("info", "iv_fanhui onClicked");
				setResult(RESULT_OK);
				finish();
			}
		});
		
		
		iv_celianghome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("info", "iv_celianghome onClicked");
//				viewPager.setCurrentItem(0);
				replaceFrag(new FragmentXueyang() );
				iv_celianghome.setBackgroundResource(R.drawable.home1);
				iv_graph.setBackgroundResource(R.drawable.graph);
				iv_data.setBackgroundResource(R.drawable.data);
				iv_celianghelp.setBackgroundResource(R.drawable.help);
				iv_celiangsetting.setBackgroundResource(R.drawable.setting);
			}
		});
		
		iv_graph.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("info", "iv_graph onClicked");
//				viewPager.setCurrentItem(1);
				replaceFrag(new FragmentXueyangGraph() );
				iv_graph.setBackgroundResource(R.drawable.graph1);
				iv_celianghome.setBackgroundResource(R.drawable.home);
				iv_data.setBackgroundResource(R.drawable.data);
				iv_celianghelp.setBackgroundResource(R.drawable.help);
				iv_celiangsetting.setBackgroundResource(R.drawable.setting);
			}
		});
		
		iv_data.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("info", "iv_data onClicked");
//				viewPager.setCurrentItem(2);
				replaceFrag(new FragmentXueyangData() );
				iv_graph.setBackgroundResource(R.drawable.graph);
				iv_celianghome.setBackgroundResource(R.drawable.home);
				iv_data.setBackgroundResource(R.drawable.data1);
				iv_celianghelp.setBackgroundResource(R.drawable.help);
				iv_celiangsetting.setBackgroundResource(R.drawable.setting);
			}
		});
		
		iv_celianghelp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("info", "iv_celianghelp onClicked");
//				viewPager.setCurrentItem(3);
				replaceFrag(new FragmentXueyangHelp() );
				iv_graph.setBackgroundResource(R.drawable.graph);
				iv_celianghome.setBackgroundResource(R.drawable.home);
				iv_data.setBackgroundResource(R.drawable.data);
				iv_celianghelp.setBackgroundResource(R.drawable.help1);
				iv_celiangsetting.setBackgroundResource(R.drawable.setting);
			}
		});
		
		iv_celiangsetting.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("info", "iv_celiangsetting onClicked");
//				viewPager.setCurrentItem(4);
				replaceFrag(new FragmentSetting() );
				iv_graph.setBackgroundResource(R.drawable.graph);
				iv_celianghome.setBackgroundResource(R.drawable.home);
				iv_data.setBackgroundResource(R.drawable.data);
				iv_celianghelp.setBackgroundResource(R.drawable.help);
				iv_celiangsetting.setBackgroundResource(R.drawable.setting1);
			}
		});
	}

	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		appState.firstActivityRunning = false;
		appState.runThread = false;
	}
	
	// 检测按键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 按下键盘上返回按钮
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			System.out.println("返回按钮");
			setResult(RESULT_OK);
			finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	
	public void findView(){
//		viewPager = (ViewPager) findViewById(R.id.viewPager);
		frag_container = (FrameLayout) findViewById(R.id.frag_container);
	
		iv_fanhui = (ImageView) findViewById(R.id.iv_fanhui);				
		iv_celianghome = (ImageView) findViewById(R.id.iv_celianghome);
		iv_graph = (ImageView) findViewById(R.id.iv_graph);
		iv_data = (ImageView) findViewById(R.id.iv_data);
		iv_celianghelp = (ImageView) findViewById(R.id.iv_celianghelp);
		iv_celiangsetting = (ImageView) findViewById(R.id.iv_celiangsetting);
	}

}
