package com.bluetooth.le.soloman;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.bluetooth.le.soloman.R;
import com.soloman.spp.xueyaji.NoteXueya;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class FragmentThemometerData extends Fragment {

	public GlobalVar appState;
	public Cursor cursor = null;
	public Button btn_cloudselect, btn_cloudselectall, btn_clouddelete, btn_cloudmail, btn_cloudsave, btn_cloudupload, btn_cloudprint, btn_cloudshare;
	public TextView tv_clouduser;
	public CheckBox cb_themocloud_quanxuan;
	
	public StringBuilder sb = new StringBuilder(); 
	public StringBuilder mailcontent = new StringBuilder();
	public StringBuilder saveascontent = new StringBuilder();
	
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
		
		View view = inflater.inflate(R.layout.fragment_themometerdata, container, false);
	
		findView(view);
		
		btn_clouddelete.setEnabled(false);
		
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

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();		
		appState.getDB();
		updateUI();
	}
	
	public void onClose() {
		appState.dbClose();
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
	
	public void findView(View view){
		btn_cloudselect = (Button) view.findViewById(R.id.btn_cloudselect);
		btn_cloudselectall = (Button) view.findViewById(R.id.btn_cloudselectall);
		btn_clouddelete = (Button) view.findViewById(R.id.btn_clouddelete);
		btn_cloudmail = (Button) view.findViewById(R.id.btn_cloudmail);		
		btn_cloudsave = (Button) view.findViewById(R.id.btn_cloudsave);
		btn_cloudupload = (Button) view.findViewById(R.id.btn_cloudupload);
		btn_cloudprint = (Button) view.findViewById(R.id.btn_cloudprint);
		btn_cloudshare = (Button) view.findViewById(R.id.btn_cloudshare);
		tv_clouduser = (TextView) view.findViewById(R.id.tv_clouduser);
		lv_datarecord = (ListView) view.findViewById(R.id.lv_datarecord);
		cb_themocloud_quanxuan = (CheckBox) view.findViewById(R.id.cb_themocloud_quanxuan);
		
		btn_cloudselect.setOnClickListener(new Button.OnClickListener(){//创建监听    
            public void onClick(View v) {    
            	Intent it = new Intent(getActivity(), gridUser.class);
    			startActivityForResult(it, 1);	
            }
		});
		
		btn_cloudselectall.setOnClickListener(new Button.OnClickListener(){//创建监听    
            public void onClick(View v) {   
            	appState.userID = "";
            	appState.userName = "";
            	updateUI();
            }
		});
		
		btn_clouddelete.setOnClickListener(new Button.OnClickListener(){//创建监听    
            public void onClick(View v) {
            	// 弹框 提示是否提交评分
				new AlertDialog.Builder(getActivity())
						.setTitle("Warning")
						.setMessage("Are you sure to DELETE?")
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										//this.s = "Negative";
									}
								})
						.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
										if (sb.length() > 0){						                	
						                	sb.deleteCharAt(sb.length()- 1);
						                	String s1 = sb.toString();
						                	String [] sa1 = s1.split(",");
						                	
						                	for (int i=0; i<sa1.length; i++){
						                		appState.delRecord(appState.userID, sa1[i]);
						                	}
						                	sb.delete(0, sb.length());
						            	}          
						            	updateUI();
									}
						}).show();
            	
            }            
		});
		
		cb_themocloud_quanxuan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				HashMap<String, Object> m = new HashMap<String, Object>();
				if (isChecked) {
					for (int i = 0; i < lst.size(); i++) {
						m = lst.get(i);
						m.remove("sel");
						m.put("sel", true);
						lst.remove(i);
						lst.add(i, m);
						
						sb.append( m.get("date") + ",");
						
                		btn_clouddelete.setEnabled(true);
					}
				} else {
					for (int i = 0; i < lst.size(); i++) {
						m = lst.get(i);
						m.remove("sel");
						m.put("sel", false);
						lst.remove(i);
						lst.add(i, m);
					}
					sb.delete(0, sb.length());
					
            		btn_clouddelete.setEnabled(false);
				}
				saImageItems.notifyDataSetChanged();
			}
		});
		
		btn_cloudmail.setOnClickListener(new Button.OnClickListener(){//创建监听    
            public void onClick(View v) {  
            	if (!appState.isDBOpen()){
    				appState.getDB();
    			}
            	
            	if ("".equals(appState.userID) || appState.userID == null){
            		cursor = appState.getRecord("1");
            	}else{
            		cursor = appState.getRecord(appState.userID, "1");
            	} 
            	
            	mailcontent = new StringBuilder();
            	
//            	Cursor c1 = null;
//            	String name = "";
//				if (cursor != null && cursor.getCount() > 0){			
//					while (cursor.moveToNext()) {
//						c1 = appState.get_patient(cursor.getString(0));
//						if (c1!=null && c1.getCount()>0){
//							c1.moveToNext();
//							name = c1.getString(1) + c1.getString(2);
//							c1.close();
//						}
//						mailcontent.append("PatientID:" + cursor.getString(0) + appState.separate);
//						mailcontent.append("Name:" + name + appState.separate);
//						mailcontent.append("Device:Themometer" + appState.deviceAddress + appState.separate);
//						mailcontent.append("Mode:" + cursor.getString(1) + appState.separate);
//						mailcontent.append("Unit:" + cursor.getString(2) + appState.separate);
//						mailcontent.append("Value:" + cursor.getString(3) + appState.separate);
//						mailcontent.append("Date:" + cursor.getString(4)  + appState.separate);
//						mailcontent.append("Note:" + cursor.getString(6) + "\n");
//					}
//					cursor.close();
//				}
				
            	for (Iterator iterator = lst.iterator(); iterator.hasNext();) {
            		HashMap<String, Object> m = (HashMap) iterator.next();
            		if ((Boolean) m.get("sel")){
            			Cursor c1 = null;
                    	String name = "";
        				if (cursor != null && cursor.getCount() > 0){			
        					while (cursor.moveToNext()) {
        						c1 = appState.get_patient(cursor.getString(0));
        						if (c1!=null && c1.getCount()>0){
        							c1.moveToNext();
        							name = c1.getString(1) + c1.getString(2);
        							c1.close();
        						}
        					}
        					cursor.close();
        				}
            			mailcontent.append("PatientID:" + map.get("uid") + appState.separate);
						mailcontent.append("Name:" + name + appState.separate);
						mailcontent.append("Device:Themometer" + appState.deviceAddress + appState.separate);
						mailcontent.append("Mode:" + map.get("mode") + appState.separate);
						mailcontent.append("Unit:" + map.get("unit") + appState.separate);
						mailcontent.append("Value:" + map.get("value") + appState.separate);
						mailcontent.append("Date:" + map.get("date")  + appState.separate);
						mailcontent.append("Note:" + map.get("note") + "\n");
            		}
				}
            	
            	try{
            		Intent data=new Intent(Intent.ACTION_SENDTO); 
                	data.setData(Uri.parse("mailto:" + appState.mail1)); 	//收件人
                	data.putExtra(Intent.EXTRA_SUBJECT, "Inurse " + appState.deviceAddress); //标题
                	data.putExtra(Intent.EXTRA_TEXT, mailcontent.toString()); //内容
                	startActivity(data);
            	}catch (Exception e){
            		e.printStackTrace();
            	}
            	 
            }
		});
		
		btn_cloudsave.setOnClickListener(new Button.OnClickListener(){//创建监听    
            public void onClick(View v) {    
            	if (!appState.isDBOpen()){
    				appState.getDB();
    			}
            	
            	if ("".equals(appState.userID) || appState.userID == null){
            		cursor = appState.getRecord("1");
            	}else{
            		cursor = appState.getRecord(appState.userID, "1");
            	}  
            	
            	saveascontent = new StringBuilder();
//            	Cursor c1 = null;
//            	String name = "";
//				if (cursor != null && cursor.getCount() > 0){			
//					while (cursor.moveToNext()) {
//						c1 = appState.get_patient(cursor.getString(0));
//						if (c1!=null && c1.getCount()>0){
//							c1.moveToNext();
//							name = c1.getString(1) + c1.getString(2);
//							c1.close();
//						}
//						saveascontent.append("PatientID:" + cursor.getString(0) + appState.separate);
//						saveascontent.append("Name:" + name + appState.separate);
//						saveascontent.append("Device:Themometer" + appState.deviceAddress + appState.separate);
//						saveascontent.append("Mode:" + cursor.getString(1) + appState.separate);
//						saveascontent.append("Unit:" + cursor.getString(2) + appState.separate);
//						saveascontent.append("Value:" + cursor.getString(3) + appState.separate);
//						saveascontent.append("Date:" + cursor.getString(4)  + appState.separate);
//						saveascontent.append("Note:" + cursor.getString(6) + "\n");
//					}
//					cursor.close();
//				}
            	
            	for (Iterator iterator = lst.iterator(); iterator.hasNext();) {
            		HashMap<String, Object> m = (HashMap) iterator.next();
            		if ((Boolean) m.get("sel")){
            			Cursor c1 = null;
                    	String name = "";
        				if (cursor != null && cursor.getCount() > 0){			
        					while (cursor.moveToNext()) {
        						c1 = appState.get_patient(cursor.getString(0));
        						if (c1!=null && c1.getCount()>0){
        							c1.moveToNext();
        							name = c1.getString(1) + c1.getString(2);
        							c1.close();
        						}
        					}
        					cursor.close();
        				}
        				saveascontent.append("PatientID:" + map.get("uid") + appState.separate);
        				saveascontent.append("Name:" + name + appState.separate);
        				saveascontent.append("Device:Themometer" + appState.deviceAddress + appState.separate);
        				saveascontent.append("Mode:" + map.get("mode") + appState.separate);
        				saveascontent.append("Unit:" + map.get("unit") + appState.separate);
        				saveascontent.append("Value:" + map.get("value") + appState.separate);
        				saveascontent.append("Date:" + map.get("date")  + appState.separate);
        				saveascontent.append("Note:" + map.get("note") + "\n");
            		}
				}
            	
            	try{
            		Date dt = new Date(System.currentTimeMillis());
            		String year = String.valueOf(dt.getYear() + 1900);
            		String mounth = String.valueOf(dt.getMonth() + 1);
            		String day = String.valueOf(dt.getDate());
            		String hour = String.valueOf(dt.getHours());
            		String minite = String.valueOf(dt.getMinutes());
            		String second = String.valueOf(dt.getSeconds());
            		String filename = "inurse" + appState.userID + year + mounth + day + hour + minite + second;
            		if (".txt".equals(appState.ext)){
            			filename = filename + ".txt";
            		}else if (".xls".equals(appState.ext)){
            			filename = filename + ".xls";
            		}
            		FileUtils file = new FileUtils();
            		file.writeFromInput(appState.path, filename, saveascontent.toString());
            		
            		Toast.makeText(getActivity().getApplicationContext(), 
            				"Save to " + appState.path + filename + " successful!", 
            				Toast.LENGTH_LONG).show();
            	}catch (Exception e){
            		e.printStackTrace();
            		Toast.makeText(getActivity().getApplicationContext(), 
            				"Save file failed!", 
            				Toast.LENGTH_LONG).show();
            	}            	
            }
		});
	}
	
	public void clearSelected(){
		if (lst != null && lst.size()>0){
			for (int i = 0;i<lst.size(); i++){
				HashMap<String, Object> m = new HashMap<String, Object>();
				m = lst.get(i);
				m.remove("sel");
				m.put("sel", false);
				lst.remove(i);
				lst.add(i, m);
				sb.delete(0, sb.length());
				saImageItems.notifyDataSetChanged();
			}
		}		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode != -1 && resultCode !=0) {
			if ("none".equals(appState.userID)) {
				String uid = data.getStringExtra("uid");
				String name = data.getStringExtra("name");
				String note = data.getStringExtra("note");
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
				String uid = data.getStringExtra("uid");
				String name = data.getStringExtra("name");
				String note = data.getStringExtra("note");

				appState.userID = uid;
				appState.userName = name;
					updateUI();
			}
			
		}
	}

	//------------------------------------------------------------------
		public class ZuJian_themocloud {
			public LinearLayout list_themocloud;
			public TextView list_themocloud_xuanzhong;
			public TextView list_themocloud_id;
			public TextView list_themocloud_mode;
			public TextView list_themocloud_value;
			public TextView list_themocloud_time;
			public Button btn_datanote;
		}
		
		private ArrayList<HashMap<String, Object>> lst;
		// 生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
		private MyListAdapter saImageItems;
		private ListView lv_datarecord;
		private HashMap<String, Object> map = new HashMap<String, Object>();

		private void updateUI( ) {
			// TODO Auto-generated method stub
			lst = new ArrayList<HashMap<String, Object>>();
			saImageItems = new MyListAdapter(getActivity(), lst);// 没什么解释
			

			if (appState.userID != null && !"".equals(appState.userID)){
//				tv_clouduser.setText("User:" + appState.userID + "," + appState.userName);
				if (!appState.isDBOpen()){
    				appState.getDB();
    			}
				cursor = appState.getRecord(appState.userID, "1");
				if (cursor != null && cursor.getCount() > 0){			
					while (cursor.moveToNext()) {
						map = new HashMap<String, Object>();
						map.put("sel", false);
						map.put("uid", cursor.getString(0));
						map.put("name", appState.userName );
						map.put("mode", cursor.getString(1));
						map.put("unit", cursor.getString(2));
						map.put("value", cursor.getString(3));						
						map.put("date", cursor.getString(4));
						map.put("note", cursor.getString(6));
						lst.add(map);
					}
					cursor.close();
				}
			}else{
				if (!appState.isDBOpen()){
    				appState.getDB();
    			}
				cursor = appState.getRecord("1");
				if (cursor != null && cursor.getCount() > 0){			
					while (cursor.moveToNext()) {
						map = new HashMap<String, Object>();
						map.put("sel", false);
						map.put("uid", cursor.getString(0));
						map.put("name", appState.userName );	//列出全部用户数据时，这个地方是空值
						map.put("mode", cursor.getString(1));
						map.put("unit", cursor.getString(2));
						map.put("value", cursor.getString(3));						
						map.put("date", cursor.getString(4));
						map.put("note", cursor.getString(6));
						lst.add(map);
					}
					cursor.close();
				}
			}

			// 生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
			// MyListAdapter saImageItems = new MyListAdapter(this, lst);// 没什么解释

			// 绑定数据
			BinderListData(saImageItems);
		}

		// 绑定数据
		public void BinderListData(MyListAdapter saImageItems) {
			// ListView listView_cart = (ListView)
			// findViewById(R.id.listView_chakan);
			// 添加并且显示
			lv_datarecord.setAdapter(saImageItems);
			saImageItems.notifyDataSetChanged();			
			// 点击控件监听器
			lv_datarecord.setOnItemClickListener(new ItemClickListener());
		}
	    
		class ItemClickListener implements OnItemClickListener {
			public void onItemClick(AdapterView<?> arg0,// The AdapterView where the click happened
					View arg1,// The view within the AdapterView that was clicked
					int position,// The position of the view in the adapter
					long id// The row id of the item that was clicked
			) {
				Log.i("info", "click:" + String.valueOf(position));
				HashMap<String, Object> m = new HashMap<String, Object>();
				m = lst.get(position);
				if ((Boolean) m.get("sel")){ //已经选中，变没选中
					m.remove("sel");
					m.put("sel", false);						
					lst.remove(position);
					lst.add(position, m);
					

	            	
	            	int start = sb.indexOf(m.get("date") + ",");
	            	int end = start + (m.get("date") + ",").length();
	            	sb.delete(start, end);
	            	Log.i("info", sb.toString());
	            	
	            	if (sb.length() == 0){
	            		//删除禁用                	
	            		btn_clouddelete.setEnabled(false);
	            	}
				}else{ //没选中，变选中
					m.remove("sel");
					m.put("sel", true);						
					lst.remove(position);
					lst.add(position, m);
	            	
	            	sb.append( m.get("date") + ",");
	            	Log.i("info", sb.toString());
	            	
	            	//修改和删除使能
	            	btn_clouddelete.setEnabled(true);
				}
				saImageItems.notifyDataSetChanged();
			}
		}
		
		/*
		 * 以下是自定义的BaseAdapter类
		 */
		public class MyListAdapter extends BaseAdapter {
			private ArrayList<HashMap<String, Object>> data;
			private LayoutInflater layoutInflater;
			private Context context;

			public MyListAdapter(Context context,
					ArrayList<HashMap<String, Object>> data) {
				this.context = context;
				this.data = data;
				this.layoutInflater = LayoutInflater.from(context);
			}

			/**
			 * 获取列数
			 */
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return data.size();
			}

			/**
			 * 获取某一位置的数据
			 */
			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return data.get(position);
			}

			/**
			 * 获取唯一标识
			 */
			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			/**
			 * android绘制每一列的时候，都会调用这个方法
			 */
			ZuJian_themocloud zuJian = null;

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub

				if (convertView == null) {
					zuJian = new ZuJian_themocloud();
					// 获取组件布局
					convertView = layoutInflater.inflate(R.layout.lv_themometerdata_body, null);
					
					zuJian.list_themocloud = (LinearLayout) convertView.findViewById(R.id.list_themocloud);
					
					zuJian.list_themocloud_xuanzhong = (TextView) convertView.findViewById(R.id.list_themocloud_xuanzhong);
					zuJian.list_themocloud_id = (TextView) convertView.findViewById(R.id.list_themocloud_id);					
					zuJian.list_themocloud_mode = (TextView) convertView.findViewById(R.id.list_themocloud_mode);
					zuJian.list_themocloud_value = (TextView) convertView.findViewById(R.id.list_themocloud_value);
					zuJian.list_themocloud_time = (TextView) convertView.findViewById(R.id.list_themocloud_time);
					zuJian.btn_datanote = (Button) convertView.findViewById(R.id.btn_datanote);

					// 这里要注意，是使用的tag来存储数据的。
					convertView.setTag(zuJian);
				} else {
					zuJian = (ZuJian_themocloud) convertView.getTag();

				}

				// 绑定数据、以及事件触发
				if ( (Boolean) data.get(position).get("sel") ){
					zuJian.list_themocloud_xuanzhong.setText("√");
				}else{
					zuJian.list_themocloud_xuanzhong.setText("");
				}
				zuJian.list_themocloud_id.setText((String) data.get(position).get("uid"));
				zuJian.list_themocloud_mode.setText((String) data.get(position).get("mode"));
				zuJian.list_themocloud_value.setText((String) data.get(position).get("value") + (String) data.get(position).get("unit"));
				zuJian.list_themocloud_time.setText((String) data.get(position).get("date"));
				
				if (!"".equals((String) data.get(position).get("note")) && (String) data.get(position).get("note") !=null){
					zuJian.btn_datanote.setBackgroundResource(R.drawable.note_button2);
				}else {
					zuJian.btn_datanote.setBackgroundResource(R.drawable.note_button1);
				}
				
				
				zuJian.btn_datanote.setOnClickListener(new Button.OnClickListener(){//创建监听    
		            public void onClick(View v) {   
		            	//弹窗写note
		            	Intent intent = new Intent();
		        		intent.setClass(getActivity(), Note.class);
		        		
		        		Bundle bundle = new Bundle();
		        		bundle.putString("uid",(String) data.get(position).get("uid"));
		        		bundle.putString("date",(String) data.get(position).get("date"));
		        		bundle.putString("note",(String) data.get(position).get("note"));
		        		intent.putExtras(bundle);

		        		startActivityForResult(intent, 1);// 需要下一个Activity返回数据,在onActivityResult()中接收			
		            }
				});

				return convertView;
			}
		}
	
}



