package com.bluetooth.le.soloman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class gridUser extends Activity {
	
	public GlobalVar appState;
	public GridView grid_listUser;
	private Cursor cursor = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		appState = (GlobalVar) getApplicationContext(); // 获得全局变量
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置成竖屏		
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 	//去掉title
		setContentView(R.layout.grid_user);
		
		grid_listUser = (GridView) findViewById(R.id.grid_listUser);		
		
	}
	
	private void updateGridView(List<String> idArray, List<String> nameArray, List<String> noteArray, List<String> heightArray) {
		// TODO Auto-generated method stub
		//生成动态数组，并且转入数据  
	      ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();  
		for (int i = 0; i < idArray.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("userid", idArray.get(i));// 
			map.put("username", nameArray.get(i));
			map.put("usernote", noteArray.get(i));
			map.put("userheight", heightArray.get(i));
			lstImageItem.add(map);
		} 
	      //生成适配器的ImageItem <====> 动态数组的元素，两者一一对应  
	      SimpleAdapter saImageItems = new SimpleAdapter(this, //没什么解释  
	                                                lstImageItem,//数据来源   
	                                                R.layout.grid_head,//night_item的XML实现  
	                                                  
	                                                //动态数组与ImageItem对应的子项          
	                                                new String[] {"userid","username","usernote"},   
	                                                  
	                                                //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
	                                                new int[] {R.id.grid_id, R.id.grid_name, R.id.grid_note});  
	      //添加并且显示  
	      grid_listUser.setAdapter(saImageItems);  
	      //添加消息处理  
	      grid_listUser.setOnItemClickListener(new ItemClickListener());  
	}
	
	// 当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件
	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
													// click happened
				View arg1,// The view within the AdapterView that was clicked
				int arg2,// The position of the view in the adapter
				long arg3// The row id of the item that was clicked
		) {
			// 在本例中arg2=arg3
			HashMap<String, Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
			// 显示所选Item的ItemText
			String uid = (String) item.get("userid");
			String name = (String) item.get("username");
			String note = (String) item.get("usernote");
			String height = (String) item.get("userheight");
			
			Intent rtnIntent = new Intent();
			rtnIntent.putExtra("uid", uid);
			rtnIntent.putExtra("name", name);
			rtnIntent.putExtra("note", note);
			rtnIntent.putExtra("height", height);
			setResult(1,rtnIntent);
			finish();
		}

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();	
		appState.getDB();
		
		List<String> idArray = new ArrayList<String>();
		List<String> nameArray = new ArrayList<String>();
		List<String> noteArray = new ArrayList<String>();
		List<String> heightArray = new ArrayList<String>();
		
		cursor = appState.get_patient();
		if (cursor != null && cursor.getCount() > 0){			
			while (cursor.moveToNext()) {
				idArray.add(cursor.getString(0));
				nameArray.add(cursor.getString(1) + " " + cursor.getString(2));
				noteArray.add(cursor.getString(5));
				
				if ( "".equals(cursor.getString(cursor.getColumnIndex("age"))) ||
						cursor.getString(cursor.getColumnIndex("age")) == null ){
					appState.userAge = "30";
				}else{
					heightArray.add(cursor.getString(cursor.getColumnIndex("age")));
					appState.userAge = cursor.getString(cursor.getColumnIndex("age"));
				}
				
				if ( "".equals(cursor.getString(cursor.getColumnIndex("shengao"))) ||
						cursor.getString(cursor.getColumnIndex("shengao")) == null ){
					heightArray.add("1.74");
					appState.userHeight = "1.74";
				}else{
					heightArray.add(cursor.getString(cursor.getColumnIndex("shengao")));
					appState.userHeight = cursor.getString(cursor.getColumnIndex("shengao"));
				}
			}
			cursor.close();
		}else{
			//返回none
			String uid ="none";
			String name = "";
			String note = "";
			String sex = "M";
			String age = "30";
			String height = "1.74";
			appState.userSex = sex;
			appState.userAge = age;
			appState.userHeight = height;
			
			Intent rtnIntent = new Intent();
			rtnIntent.putExtra("uid", uid);
			rtnIntent.putExtra("name", name);
			rtnIntent.putExtra("note", note);
			rtnIntent.putExtra("sex", sex);
			rtnIntent.putExtra("age", age);
			rtnIntent.putExtra("height", height);
			setResult(1,rtnIntent);
			finish();
		}
			
		updateGridView(idArray, nameArray, noteArray, heightArray);
	}
	
	public void onClose() {
		appState.dbClose();
	}
}
