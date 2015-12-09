package com.bluetooth.le.soloman;

import com.bluetooth.le.soloman.FragmentThemometer;
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

public class CeliangActivity extends FragmentActivity {

	public GlobalVar appState;
	
	public android.support.v4.app.FragmentTransaction ft;
	public FragmentThemometer fragmentThemometer;
	public FragmentThemometerGraph fragmentThemometerGraph;
	public FragmentThemometerData fragmentThemometerData;
	public FragmentThemometerHelp fragmentThemometerHelp;
//	public FragmentThemometerSetting fragmentThemometerSetting;
	public FragmentSetting fragmentSetting;


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
		
		fragmentThemometer = (FragmentThemometer) fm.findFragmentById(R.layout.fragment_themometer);
		
		ft.add(R.id.frag_container,new FragmentThemometer(), "home");
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
				replaceFrag(new FragmentThemometer() );
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
				replaceFrag(new FragmentThemometerGraph() );
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
				replaceFrag(new FragmentThemometerData() );
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
				replaceFrag(new FragmentThemometerHelp() );
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

	
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
	
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

	
//	private ArrayList<Fragment> fragmentArryList;  
//	public void initViewPager(){
//		//viewPager = (ViewPager)findViewById(R.id.viewpager);  
//		fragmentArryList = new ArrayList<Fragment>();  
// 
//		fragmentThemometer = new FragmentThemometer();
//		fragmentThemometerGraph = new FragmentThemometerGraph();
//		fragmentThemometerData = new FragmentThemometerData();
//		fragmentThemometerHelp = new FragmentThemometerHelp();
////		fragmentThemometerSetting = new FragmentThemometerSetting();
//		fragmentThemometerSetting = new FragmentSetting();
//        
//		fragmentArryList.add(fragmentThemometer);  
//		fragmentArryList.add(fragmentThemometerGraph);
//		fragmentArryList.add(fragmentThemometerData);
//		fragmentArryList.add(fragmentThemometerHelp);
//		fragmentArryList.add(fragmentThemometerSetting);
//          
//        //给ViewPager设置适配器  
////		viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentArryList));  
////		viewPager.setCurrentItem(0);//设置当前显示标签页为第一页  
////		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器  		
//	}
//	
//	
//	public class MyOnPageChangeListener implements OnPageChangeListener {
//
//		@Override
//		public void onPageScrolled(int arg0, float arg1, int arg2) {
//			// TODO Auto-generated method stub
//			switch (arg0) {
//			case 0:
//				iv_celianghome.setBackgroundResource(R.drawable.home1);
//				iv_graph.setBackgroundResource(R.drawable.graph);
//				iv_data.setBackgroundResource(R.drawable.data);
//				iv_celianghelp.setBackgroundResource(R.drawable.help);
//				iv_celiangsetting.setBackgroundResource(R.drawable.setting);
//				break;
//			case 1:
//				iv_graph.setBackgroundResource(R.drawable.graph1);
//				iv_celianghome.setBackgroundResource(R.drawable.home);
//				iv_data.setBackgroundResource(R.drawable.data);
//				iv_celianghelp.setBackgroundResource(R.drawable.help);
//				iv_celiangsetting.setBackgroundResource(R.drawable.setting);
//				break;
//			case 2:
//				iv_graph.setBackgroundResource(R.drawable.graph);
//				iv_celianghome.setBackgroundResource(R.drawable.home);
//				iv_data.setBackgroundResource(R.drawable.data1);
//				iv_celianghelp.setBackgroundResource(R.drawable.help);
//				iv_celiangsetting.setBackgroundResource(R.drawable.setting);
//				break;
//			case 3:
//				iv_graph.setBackgroundResource(R.drawable.graph);
//				iv_celianghome.setBackgroundResource(R.drawable.home);
//				iv_data.setBackgroundResource(R.drawable.data);
//				iv_celianghelp.setBackgroundResource(R.drawable.help1);
//				iv_celiangsetting.setBackgroundResource(R.drawable.setting);
//				break;
//			case 4:
//				iv_graph.setBackgroundResource(R.drawable.graph);
//				iv_celianghome.setBackgroundResource(R.drawable.home);
//				iv_data.setBackgroundResource(R.drawable.data);
//				iv_celianghelp.setBackgroundResource(R.drawable.help);
//				iv_celiangsetting.setBackgroundResource(R.drawable.setting1);
//				break;
//			}
//		}
//
//		@Override
//		public void onPageScrollStateChanged(int arg0) {
//			// TODO Auto-generated method stub
//
//		}
//
//		public int currentPageIndex;
//		@Override
//		public void onPageSelected(int arg0) {
//			// TODO Auto-generated method stub
//			switch (arg0) {
//			case 0:
//				break;
//			case 1:
//				break;
//
//			}
//			
//			fragmentArryList.get(currentPageIndex).onPause(); // 调用切换前Fargment的onPause()
//			if (fragmentArryList.get(arg0).isAdded()) {
//				fragmentArryList.get(arg0).onResume(); // 调用切换后Fargment的onResume()
//			}
//			currentPageIndex = arg0;
//		}
//		
//		
//		
//	}
//	
//	public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
//		ArrayList<Fragment> list;
//
//		public MyFragmentPagerAdapter(FragmentManager fm,
//				ArrayList<Fragment> list) {
//			super(fm);
//			this.list = list;
//
//		}
//
//		@Override
//		public int getCount() {
//			return list.size();
//		}
//
//		@Override
//		public Fragment getItem(int arg0) {
//			return list.get(arg0);
//		}			
//	} 

}
