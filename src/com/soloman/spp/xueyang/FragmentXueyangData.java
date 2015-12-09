package com.soloman.spp.xueyang;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import com.bluetooth.le.soloman.FileUtils;
import com.bluetooth.le.soloman.GlobalVar;
import com.bluetooth.le.soloman.Note;
import com.bluetooth.le.soloman.R;
import com.bluetooth.le.soloman.gridUser;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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

public class FragmentXueyangData extends Fragment {

	public GlobalVar appState;
	public Cursor cursor = null;
	public Button btn_cloudselect, btn_cloudselectall, btn_clouddelete, btn_cloudmail, btn_cloudsave, btn_cloudupload, btn_cloudprint, btn_cloudshare;
	public TextView tv_clouduser;
	public CheckBox cb_xueyangcloud_quanxuan;
	
	public StringBuilder sb = new StringBuilder(); 
	public StringBuilder mailcontent = new StringBuilder();
	public StringBuilder saveascontent = new StringBuilder();
	
	public FileUtils file = new FileUtils();
	
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
		
		View view = inflater.inflate(R.layout.frag_xueyangdata, container, false);
	
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
				if ("xueyangdata".equals((String) msg.obj)) {

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
		lv_xueyangrecord = (ListView) view.findViewById(R.id.lv_xueyangrecord);
		cb_xueyangcloud_quanxuan = (CheckBox) view.findViewById(R.id.cb_xueyangcloud_quanxuan);
		
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
            	// 弹框 提示是否删除
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
						                		appState.database.delXueyang(sa1[i]);	//根据time删除数据库记录
						                	}
						                	sb.delete(0, sb.length());
						                	
						            	}          
						            	updateUI();
									}
						}).show();
            	
            }            
		});
		
		cb_xueyangcloud_quanxuan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
//            	if (!appState.isDBOpen()){
//    				appState.getDB();
//    			}
            	
//            	if ("".equals(appState.userID) || appState.userID == null){
//            		cursor = appState.database.getshuimian();
//            	}else{
//            		cursor = appState.database.getshuimian(appState.userID); 
//            	} 
            	
            	saveascontent = new StringBuilder();
//            	Cursor c1 = null;
//            	String name = "";
            	ArrayList<Uri> amrUris = new ArrayList<Uri>(); 
            	for (Iterator iterator = lst.iterator(); iterator.hasNext();) {
            		HashMap<String, Object> m = (HashMap) iterator.next();
            		if ((Boolean) m.get("sel")){
            			mailcontent.append("PatientID:" + m.get("uid") + appState.separate);
    					mailcontent.append("Device:Oximeter" + appState.separate);
    					mailcontent.append("O2:" + m.get("o2") + "%" + appState.separate);
    					mailcontent.append("PUL:" + m.get("pul") + "/min" + appState.separate);
    					mailcontent.append("Note:" + m.get("mnote") + "\n");
    					
    				
            		}
				}
            	
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
//						mailcontent.append("Starttime:" + cursor.getString(3) + appState.separate);
//						mailcontent.append("Endtime:" + cursor.getString(4) + appState.separate);
//						mailcontent.append("Duration:" + cursor.getString(6) + appState.separate);
//						mailcontent.append("Recordname:" + cursor.getString(2)  + appState.separate);
//						mailcontent.append("Note:" + cursor.getString(5) + "\n");
//						
//						//附件,有录音文件才加入
//						if (!"".equals(cursor.getString(cursor.getColumnIndex("filename")))){
//							amrUris.add(Uri.parse("file://" 
//									+ cursor.getString(cursor.getColumnIndex("filepath"))
//									 + cursor.getString(cursor.getColumnIndex("filename")) )); 
//								}
//						}
//					cursor.close();
//				}
				
            	try{
            		Intent data=new Intent(Intent.ACTION_SEND_MULTIPLE); 
                	data.setData(Uri.parse("mailto:" + appState.mail1)); 	//收件人
                	data.putExtra(Intent.EXTRA_SUBJECT, "Inurse - Oximeter" ); //标题
                	data.putExtra(Intent.EXTRA_TEXT, mailcontent.toString()); //内容
//                	data.putParcelableArrayListExtra(Intent.EXTRA_STREAM, amrUris); //附件
//                	data.setType("audio/*"); 
                	data.setType("message/rfc882"); 
//                	Intent.createChooser(data, "Choose Email Client"); 
                	startActivity(data);
            	}catch (Exception e){
            		e.printStackTrace();
            	}
            	 
            }
		});
		
		btn_cloudsave.setOnClickListener(new Button.OnClickListener(){//创建监听    
            public void onClick(View v) {    
//            	if (!appState.isDBOpen()){
//    				appState.getDB();
//    			}
            	
//            	if ("".equals(appState.userID) || appState.userID == null){
//            		cursor = appState.database.getshuimian();
//            	}else{
//            		cursor = appState.database.getshuimian(appState.userID); 
//            	}  
            	
            	saveascontent = new StringBuilder();
//            	Cursor c1 = null;
//            	String name = "";
            	
            	for (Iterator iterator = lst.iterator(); iterator.hasNext();) {
            		HashMap<String, Object> m = (HashMap) iterator.next();
            		if ((Boolean) m.get("sel")){
            			saveascontent.append("PatientID:" + m.get("uid") + appState.separate);
            			saveascontent.append("Device:Oximeter" + appState.separate);
            			saveascontent.append("O2:" + m.get("o2") + "%" + appState.separate);
            			saveascontent.append("PUL:" + m.get("pul") + "/min" + appState.separate);
            			saveascontent.append("Note:" + m.get("mnote") + "\n");
            		}
				}
            	
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
//						saveascontent.append("Starttime:" + cursor.getString(3) + appState.separate);
//						saveascontent.append("Endtime:" + cursor.getString(4) + appState.separate);
//						saveascontent.append("Duration:" + cursor.getString(6) + appState.separate);
//						saveascontent.append("Recordname:" + cursor.getString(2)  + appState.separate);
//						saveascontent.append("Note:" + cursor.getString(5) + "\n");
//					}
//					cursor.close();
//				}
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
            		
            		file.writeFromInput(appState.sdcard + "/inurse/Oximeter/saveas/", filename, saveascontent.toString());
            		
            		Toast.makeText(getActivity().getApplicationContext(), 
            				"Save to " + appState.sdcard + "/inurse/Oximeter/saveas/" + filename + " successful!", 
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
		public class ZuJian_xueyangcloud {
			public LinearLayout list_xueyangcloud;
			public TextView list_xueyangcloud_xuanzhong;
			public TextView list_xueyangcloud_id;
			public TextView list_xueyangcloud_time;
			public TextView list_xueyangcloud_o2;
			public TextView list_xueyangcloud_pul;
			public Button btn_xueyangdatanote;
		}
		
		private ArrayList<HashMap<String, Object>> lst;
		// 生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
		private MyListAdapter saImageItems;
		private ListView lv_xueyangrecord;
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
				cursor = appState.database.getXueyang(appState.userID);
				if (cursor != null && cursor.getCount() > 0){			
					while (cursor.moveToNext()) {
						map = new HashMap<String, Object>();
						map.put("sel", false);
						map.put("uid", cursor.getString(0));
						map.put("o2", cursor.getString(1)); //血氧
						map.put("pul", cursor.getString(2));	//脉搏率
						map.put("date", cursor.getString(4));
						map.put("time", cursor.getString(5) );	
						map.put("mnote", cursor.getString(6) );	
							
						lst.add(map);
					}
					cursor.close();
				}
			}else{
				if (!appState.isDBOpen()){
    				appState.getDB();
    			}
				cursor = appState.database.getXueyang();
				if (cursor != null && cursor.getCount() > 0){			
					while (cursor.moveToNext()) {
						map = new HashMap<String, Object>();
						map.put("sel", false);
						map.put("uid", cursor.getString(0));
						map.put("o2", cursor.getString(1)); //血氧
						map.put("pul", cursor.getString(2));	//脉搏率
						map.put("date", cursor.getString(4));
						map.put("time", cursor.getString(5) );	
						map.put("mnote", cursor.getString(6) );	

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
			lv_xueyangrecord.setAdapter(saImageItems);
			saImageItems.notifyDataSetChanged();			
			// 点击控件监听器
			lv_xueyangrecord.setOnItemClickListener(new ItemClickListener());
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
			ZuJian_xueyangcloud zuJian = null;

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub

				if (convertView == null) {
					zuJian = new ZuJian_xueyangcloud();
					// 获取组件布局
					convertView = layoutInflater.inflate(R.layout.lv_xueyangdata_body, null);
					
					zuJian.list_xueyangcloud = (LinearLayout) convertView.findViewById(R.id.list_xueyangcloud);
					
					zuJian.list_xueyangcloud_xuanzhong = (TextView) convertView.findViewById(R.id.list_xueyangcloud_xuanzhong);
					zuJian.list_xueyangcloud_id = (TextView) convertView.findViewById(R.id.list_xueyangcloud_id);					
					zuJian.list_xueyangcloud_time = (TextView) convertView.findViewById(R.id.list_xueyangcloud_time);
					zuJian.list_xueyangcloud_o2 = (TextView) convertView.findViewById(R.id.list_xueyangcloud_o2);
					zuJian.list_xueyangcloud_pul = (TextView) convertView.findViewById(R.id.list_xueyangcloud_pul);
					zuJian.btn_xueyangdatanote = (Button) convertView.findViewById(R.id.btn_xueyangdatanote);
					
					// 这里要注意，是使用的tag来存储数据的。
					convertView.setTag(zuJian);
				} else {
					zuJian = (ZuJian_xueyangcloud) convertView.getTag();

				}

				// 绑定数据、以及事件触发
				if ( (Boolean) data.get(position).get("sel") ){
					zuJian.list_xueyangcloud_xuanzhong.setText("√");
				}else{
					zuJian.list_xueyangcloud_xuanzhong.setText("");
				}
				zuJian.list_xueyangcloud_id.setText((String) data.get(position).get("uid"));
				zuJian.list_xueyangcloud_time.setText((String) data.get(position).get("date"));
				zuJian.list_xueyangcloud_o2.setText((String) data.get(position).get("o2") );
				zuJian.list_xueyangcloud_pul.setText((String) data.get(position).get("pul"));
				
				if (!"".equals((String) data.get(position).get("mnote")) && (String) data.get(position).get("mnote") !=null){
					zuJian.btn_xueyangdatanote.setBackgroundResource(R.drawable.note_button2);
				}else {
					zuJian.btn_xueyangdatanote.setBackgroundResource(R.drawable.note_button1);
				}
				
				
				zuJian.btn_xueyangdatanote.setOnClickListener(new Button.OnClickListener(){//创建监听    
		            public void onClick(View v) {   
		            	//弹窗写note
		            	Intent intent = new Intent();
		        		intent.setClass(getActivity(), NoteXueyang.class);
		        		
		        		Bundle bundle = new Bundle();
		        		bundle.putString("uid",(String) data.get(position).get("uid"));
		        		bundle.putString("date",(String) data.get(position).get("date"));
		        		bundle.putString("note",(String) data.get(position).get("mnote"));
		        		intent.putExtras(bundle);

		        		startActivityForResult(intent, 1);// 需要下一个Activity返回数据,在onActivityResult()中接收			
		            }
				});
				
				return convertView;
			}
		}
	
}



