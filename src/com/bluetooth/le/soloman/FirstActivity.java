package com.bluetooth.le.soloman;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import com.bluetooth.le.soloman.FragmentThemometer;
import com.bluetooth.le.soloman.R;
import com.soloman.ble.dianzichen.DeviceScanActivityDianzichen;
import com.soloman.ble.dianzichen.DianzichenActivity;
import com.soloman.shuimian.ShuimianActivity;
import com.soloman.spp.xueyaji.XueyajiActivity;
import com.soloman.spp.xueyang.XueyangActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class FirstActivity extends FragmentActivity {

	public GlobalVar appState;
	
	public android.support.v4.app.FragmentTransaction ft;
	public FragmentHome fragmentHome;
	public FragmentSetting fragmentSetting;
	public FragmentHelp fragmentHelp;
	public FragmentUser fragmentUser;

	public MediaRecorder recorder;	
	public ViewPager viewPager;	
	public ImageView iv_home, iv_setting, iv_help, iv_user, iv_sound, iv_repair;	//首页图片  设置图片
	public Button btn_wendu, btn_shuimian, btn_dianzichen, btn_xueya, btn_xueyang;	//温度计,睡眠，电子秤，血压计,血氧仪

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		appState = (GlobalVar) getApplicationContext(); // 获得全局变量
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置成竖屏		
		appState.keepScreenAlive();
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 	//去掉title
		setContentView(R.layout.first_activity);

		appState.sdcard = Environment.getExternalStorageDirectory().toString();
		
		findView();
		setOnClickListener();
		initViewPager();
		
		
		android.support.v4.app.FragmentManager fm =  getSupportFragmentManager();
		ft = fm.beginTransaction();
		
		fragmentHome = (FragmentHome) fm.findFragmentById(R.layout.fragment_home);
		fragmentSetting = (FragmentSetting) fm.findFragmentById(R.layout.fragment_setting);
		fragmentHelp = (FragmentHelp) fm.findFragmentById(R.layout.fragment_help);
		fragmentUser = (FragmentUser) fm.findFragmentById(R.layout.fragment_user);
		
		ft.add(R.id.viewPager,new FragmentThemometer(), "home");	
		ft.add(R.id.viewPager,new FragmentThemometer(), "setting");
		ft.add(R.id.viewPager,new FragmentThemometer(), "help");
		ft.add(R.id.viewPager,new FragmentThemometer(), "user");
		
		//ft.replace(R.id.viewPager, fragmentSleep);
		ft.commit();	

		new Thread() {
			@Override
			public void run() {
				// 你要执行的方法
				uploadDevice();
				// 执行完毕后给handler发送一个空消息
				handler.sendEmptyMessage(0);
			}
		}.start();
		
	}

	//定义Handler对象
	private Handler handler =new Handler(){
	 @Override
	 //当有消息发送出来的时候就执行Handler的这个方法
	public void handleMessage(Message msg){
	 super.handleMessage(msg);
	//处理UI
	 
	 }
	 };
	 
	
	private void setOnClickListener() {
		// TODO Auto-generated method stub
		iv_home.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("info", "iv_home onClicked");
				viewPager.setCurrentItem(0);
			}
		});
		
		iv_setting.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("info", "iv_setting onClicked");
				viewPager.setCurrentItem(1);
			}
		});
		
		iv_help.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("info", "iv_help onClicked");
				viewPager.setCurrentItem(2);
			}
		});
		
		iv_user.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("info", "iv_user onClicked");
				viewPager.setCurrentItem(3);
			}
		});
		
		iv_repair.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("info", "iv_repair onClicked");
				Intent it = new Intent(FirstActivity.this, PairedDevice.class);				
				startActivity(it);
			}
		});
		
		iv_sound.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("info", "iv_sound onClicked");

			}
		});
		
	}
	
	public void btn_shuimian_onclick(View view){
		Log.i("info","点击睡眠按钮");
        
        Intent it = new Intent(FirstActivity.this, ShuimianActivity.class);
		startActivity(it);
				
	}
	
	public void btn_dianzichen_onclick(View view){
		Log.i("info","点击电子秤按钮");
		appState.devicetp = "dianzichen";
		BluetoothAdapter mBluetoothAdapter;
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
        }        
        
        if (mBluetoothAdapter.isEnabled()){ //如果蓝牙开了，进搜索蓝牙，然后进体温计
        	Intent it = new Intent(FirstActivity.this, DeviceScanActivityDianzichen.class);
    		startActivity(it);
        }else {//如果蓝牙没开，提示是否直接进体温计
        	new AlertDialog.Builder(this)
				.setTitle("Bluetooth")
				.setMessage("Your Bluetooth is closed now, do you want to open it ?")
				.setNegativeButton("No,Thanks",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//this.s = "Negative";
								Intent it = new Intent(FirstActivity.this, DianzichenActivity.class);
					    		startActivity(it);
					}
				})
				.setPositiveButton("Yes,Open it", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent it = new Intent(FirstActivity.this, DeviceScanActivityDianzichen.class);
			    		startActivity(it);
					}
					}).show();
					
        	
        }
				
//		Intent it = new Intent(FirstActivity.this, XueyajiActivity.class);
//		startActivity(it);
	}
	
	public void btn_xueyang_onclick(View view){
		Log.i("info","点击血氧仪按钮");
		appState.devicetp = "xueyangyi";
		Intent it = new Intent(FirstActivity.this, XueyangActivity.class);
		startActivity(it);
	}
	
	public void btn_xueya_onclick(View view){
		Log.i("info","点击血压计按钮");
		appState.devicetp = "xueyaji";
		Intent it = new Intent(FirstActivity.this, XueyajiActivity.class);
		startActivity(it);
	}

	public void btn_wendu_onclick(View view){
		Log.i("info","点击温度计按钮");
//		Intent it = new Intent(FirstActivity.this, CeliangActivity.class);
		BluetoothAdapter mBluetoothAdapter;
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
        }
        
        if (mBluetoothAdapter.isEnabled()){ //如果蓝牙开了，进搜索蓝牙，然后进体温计
        	Intent it = new Intent(FirstActivity.this, DeviceScanActivity.class);
    		startActivity(it);
        }else {//如果蓝牙没开，提示是否直接进体温计
        	new AlertDialog.Builder(this)
				.setTitle("Bluetooth")
				.setMessage("Your Bluetooth is closed now, do you want to open it ?")
				.setNegativeButton("No,Thanks",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//this.s = "Negative";
								Intent it = new Intent(FirstActivity.this, CeliangActivity.class);
					    		startActivity(it);
					}
				})
				.setPositiveButton("Yes,Open it", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent it = new Intent(FirstActivity.this, DeviceScanActivity.class);
			    		startActivity(it);
					}
					}).show();
					
        	
        }
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
	public void onResume() {
		super.onResume();
		if ( appState.file.isFileExist("inurse/Thermometer.txt") ){
			iv_repair.setBackgroundResource(R.drawable.bluetooth);
        }else{
        	iv_repair.setBackgroundResource(R.drawable.bluetooth1);
        }
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		appState.firstActivityRunning = false;
		appState.runThread = false;
		
		if (appState.BluetoothAdapter != null && appState.BluetoothAdapter.isEnabled() ){
			appState.BluetoothAdapter.disable();
		}
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
		viewPager = (ViewPager) findViewById(R.id.viewPager);		
	
		iv_home = (ImageView) findViewById(R.id.iv_home);		
		iv_setting = (ImageView) findViewById(R.id.iv_setting);
		iv_help = (ImageView) findViewById(R.id.iv_help);
		iv_user = (ImageView) findViewById(R.id.iv_user);
		iv_repair = (ImageView) findViewById(R.id.iv_repair);
		iv_sound = (ImageView) findViewById(R.id.iv_sound);
		
		btn_wendu =  (Button) findViewById(R.id.btn_wendu);
		btn_dianzichen =  (Button) findViewById(R.id.btn_dianzichen);
		btn_shuimian =  (Button) findViewById(R.id.btn_shuimian);
		btn_xueya = (Button) findViewById(R.id.btn_xueya);
		btn_xueyang = (Button) findViewById(R.id.btn_xueyang);
	}

	
	private ArrayList<Fragment> fragmentArryList;  
	public void initViewPager(){
		//viewPager = (ViewPager)findViewById(R.id.viewpager);  
		fragmentArryList = new ArrayList<Fragment>();  
 
		fragmentHome = new FragmentHome();
		fragmentSetting = new FragmentSetting();
		fragmentHelp = new FragmentHelp();
		fragmentUser = new FragmentUser();
        
		fragmentArryList.add(fragmentHome);  
		fragmentArryList.add(fragmentSetting);  
		fragmentArryList.add(fragmentHelp);
		fragmentArryList.add(fragmentUser);
          
        //给ViewPager设置适配器  
		viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentArryList));  
		viewPager.setCurrentItem(0);//设置当前显示标签页为第一页  
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器  		
	}
	
	
	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			switch (arg0) {
			case 0:
				iv_home.setBackgroundResource(R.drawable.home1);
				iv_setting.setBackgroundResource(R.drawable.setting);
				iv_help.setBackgroundResource(R.drawable.help);
				iv_user.setBackgroundResource(R.drawable.user);
				break;
			case 1:
				iv_setting.setBackgroundResource(R.drawable.setting1);
				iv_home.setBackgroundResource(R.drawable.home);
				iv_help.setBackgroundResource(R.drawable.help);
				iv_user.setBackgroundResource(R.drawable.user);
				break;
			case 2:
				iv_home.setBackgroundResource(R.drawable.home);
				iv_setting.setBackgroundResource(R.drawable.setting);
				iv_help.setBackgroundResource(R.drawable.help1);
				iv_user.setBackgroundResource(R.drawable.user);
				break;
			case 3:
				iv_home.setBackgroundResource(R.drawable.home);
				iv_setting.setBackgroundResource(R.drawable.setting);
				iv_help.setBackgroundResource(R.drawable.help);
				iv_user.setBackgroundResource(R.drawable.user1);
				break;
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		public int currentPageIndex;
		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
//			switch (arg0) {
//			case 0:
//				iv_home.setBackgroundResource(R.drawable.home1);
//				iv_setting.setBackgroundResource(R.drawable.setting);
//				break;
//			case 1:
//				iv_setting.setBackgroundResource(R.drawable.setting1);
//				iv_home.setBackgroundResource(R.drawable.home);
//				break;
//
//			}
//			
//			fragmentArryList.get(currentPageIndex).onPause(); // 调用切换前Fargment的onPause()
//			if (fragmentArryList.get(arg0).isAdded()) {
//				fragmentArryList.get(arg0).onResume(); // 调用切换后Fargment的onResume()
//			}
//			currentPageIndex = arg0;
		}
		
		
		
	}
	
	public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
		ArrayList<Fragment> list;

		public MyFragmentPagerAdapter(FragmentManager fm,
				ArrayList<Fragment> list) {
			super(fm);
			this.list = list;

		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Fragment getItem(int arg0) {
			return list.get(arg0);
		}			
	} 

	
	private void getDeviceInfo() {
		// TODO Auto-generated method stub
			DisplayMetrics metric = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metric);
			appState.screenWidth = metric.widthPixels; // 屏幕宽度（像素）
			appState.screenHeight = metric.heightPixels; // 屏幕高度（像素）
			appState.density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
			appState.densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
			appState.wh = appState.screenHeight / appState.screenWidth;

			appState.firm = android.os.Build.VERSION.RELEASE;
			appState.tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			/*
			 * 唯一的设备ID： GSM手机的 IMEI 和 CDMA手机的 MEID. Return null if device ID is not
			 * available.
			 */
			appState.IMEI = appState.tm.getDeviceId();// String
			appState.card1num = appState.tm.getLine1Number();// String
			appState.simserial = appState.tm. getSimSerialNumber();// String
			
			String androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
			appState.deviceUuid = new UUID(androidId.hashCode(), 
					((long)("" + appState.IMEI).hashCode() << 32) | ("" + appState.simserial).hashCode()); 

	}
	
	private void uploadDevice() {
		// TODO Auto-generated method stub
		 getDeviceInfo() ;
		 String servletUrl = "http://solomanhl.3322.org:8888/WebLoginToandroid/servlet/inurse";
//		 String servletUrl = "http://192.168.1.9:8080/WebLoginToandroid/servlet/inurse";
		 String send;
		 Date dt = new Date(System.currentTimeMillis());
		 String devicetime = dt.toString();
			
			// 将参数传给服务器
			String resultData = "";
			send = "device=" + appState.deviceUuid
					+ "&devicetime=" + devicetime
					+ "&note=Firm:" + appState.firm + ",Pixels:" + appState.screenWidth + "/" + appState.screenHeight + "/" + appState.wh
								+ ",Density:" + appState.density + ",densityDpi:" + appState.densityDpi
								+ ",Tel:" + appState.card1num + ",Sim:" + appState.simserial
					+ "&logintype=0000";//预留参数
			URL url = null;
			try {
				url = new URL(servletUrl);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (url != null) {
				try {
					// 使用HttpURLConnection打开连接
					HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
					urlConn.setConnectTimeout(appState.REQUEST_TIMEOUT);
					urlConn.setReadTimeout(appState.SO_TIMEOUT);
					// 因为要求使用Post方式提交数据，需要设置为true
					urlConn.setDoOutput(true);
					urlConn.setDoInput(true);
					// 设置以Post方式，注意此处的“POST”必须大写
					urlConn.setRequestMethod("POST");
					// Post 请求不能使用缓存
					urlConn.setUseCaches(false);
					urlConn.setInstanceFollowRedirects(true);
					// 配置本次连接的Content-Type，配置为application/x-www-form-urlencoded
					urlConn.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					// 连接，从postUrl.openConnection()至此的配置必须在connect之前完成
					// 要注意的事connection.getOutputStream会隐含地进行connect。
					urlConn.connect();
					// DataOutputStream流上传数据
					DataOutputStream out = new DataOutputStream(
							urlConn.getOutputStream());					
					// 将要上传的内容写入流中
					out.writeBytes(send);
					// 刷新，关闭
					out.flush();
					out.close();
					// 得到读取的数据
					InputStreamReader in = new InputStreamReader(
							urlConn.getInputStream());
					BufferedReader buffer = new BufferedReader(in);
					String str = null;
					while ((str = buffer.readLine()) != null) {
						resultData += str;
					}
					in.close();
					urlConn.disconnect();
					
					if ("shutdown".equals(resultData)){
						//退出app
						finish();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}// if(url!=null)
			else {

			}
	}
	
}
