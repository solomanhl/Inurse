package com.bluetooth.le.soloman;


import java.util.ArrayList;
import java.util.HashMap;
import com.bluetooth.le.soloman.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentUser extends Fragment {

	public GlobalVar appState;
	public EditText et_uid, et_note, et_fname, et_lname, et_tel, et_mail;
	public Button btn_add, btn_modify, btn_delete;
	public CheckBox cb_user_quanxuan;
	public String selectPatient = "";
	StringBuilder sb = new StringBuilder();  
	
	//public sportDataThread st = null;
	
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
		
		View view = inflater.inflate(R.layout.fragment_user, container, false);
	
		
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

	public void disableAll(){
		et_uid.setEnabled(false);
		et_note.setEnabled(false);
		et_fname.setEnabled(false);
		et_lname.setEnabled(false);
		et_tel.setEnabled(false);
		et_mail.setEnabled(false);
	}
	
	public void enableAll(){
		et_uid.setEnabled(true);
		et_note.setEnabled(true);
		et_fname.setEnabled(true);
		et_lname.setEnabled(true);
		et_tel.setEnabled(true);
		et_mail.setEnabled(true);
	}
	
	public void clearAll(){
		et_uid.setText("");
		et_note.setText("");
		et_fname.setText("");
		et_lname.setText("");
		et_tel.setText("");
		et_mail.setText("");
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
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();		
		
		appState.getDB();
		updateUI();
		setOnclickListener();
		disableAll();//禁用编辑区域
		btn_modify.setEnabled(false);
		btn_delete.setEnabled(false);
	}
	
//	@Override
//	protected void finalize() throws Throwable {
//		// TODO Auto-generated method stub
//		super.finalize();
//	}
	
	@Override
	public void onPause() {
		super.onPause();
		appState.dbClose();
	}

	
	public void findView(View view){
		et_uid = (EditText) view.findViewById(R.id.et_uid);
		et_note = (EditText) view.findViewById(R.id.et_note);
		et_fname = (EditText) view.findViewById(R.id.et_fname);
		et_lname = (EditText) view.findViewById(R.id.et_lname);
		et_tel = (EditText) view.findViewById(R.id.et_tel);
		et_mail = (EditText) view.findViewById(R.id.et_mail);
		
		btn_add = (Button) view.findViewById(R.id.btn_add);
		btn_modify = (Button) view.findViewById(R.id.btn_modify);
		btn_delete = (Button) view.findViewById(R.id.btn_delete);
		
		cb_user_quanxuan = (CheckBox) view.findViewById(R.id.cb_user_quanxuan);
		
		listView_user = (ListView) view.findViewById(R.id.lv_user);
	}
	
	private String addState = "add";
	private String modifyState = "modi";
	private void setOnclickListener() {
		// TODO Auto-generated method stub				
		
		btn_add.setOnClickListener(new Button.OnClickListener(){//创建监听    
            public void onClick(View v) {
            	if ("add".equals(addState)){
            		enableAll();//使能编辑区域
            		//id获得焦点，弹出输入法
//            		et_uid.setFocusable(true);
//            		et_uid.setFocusableInTouchMode(true);
//            		et_uid.setSelected(true);
            		et_uid.requestFocus();
            		InputMethodManager imm = (InputMethodManager) et_uid.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        			imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED); 
            		
            		listView_user.setEnabled(false);	//禁用listview
            		addState = "save";
            		btn_add.setBackgroundResource(R.drawable.save_selector);
            		btn_modify.setEnabled(false);
            		btn_delete.setEnabled(false);
            	}else if ("save".equals(addState)){
            		if (!"".equals(et_uid.getText().toString())){
            			if (!appState.isDBOpen()){
            				appState.getDB();
            			}
                		cursor = appState.get_patient(et_uid.getText().toString());
                		if (cursor == null || cursor.getCount() == 0 ){//如果没有就添加
                			appState.add_patient(et_uid.getText().toString(), 
                    				et_fname.getText().toString(), 
                    				et_lname.getText().toString(), 
                    				et_tel.getText().toString(), 
                    				et_mail.getText().toString(), 
                    				et_note.getText().toString() 
                    				);
                    		clearAll();  
//                    		clearSelected();
//                    		btn_modify.setEnabled(false);
//                    		btn_delete.setEnabled(false);
                		}else {
                			cursor.close();
                		}
                	}
                	updateUI();
                	listView_user.setEnabled(true);	//使能listview
                	disableAll();//禁用编辑区域
                	addState = "add";
                	btn_add.setBackgroundResource(R.drawable.add_selector);
                	clearSelected();
                	btn_modify.setEnabled(false);
            		btn_delete.setEnabled(false);
            	}            	
            }            
		});
		
		btn_modify.setOnClickListener(new Button.OnClickListener(){//创建监听    
            public void onClick(View v) {
            	enableAll();//激活编辑区域
            	if ("modi".equals(modifyState)){
            		listView_user.setEnabled(false);	//禁用listview
            		enableAll();//使能编辑区域
            		et_uid.setEnabled(false);	//用户名不能编辑
            		modifyState = "save";
            		btn_modify.setBackgroundResource(R.drawable.save_selector);
            		btn_add.setEnabled(false);
            		btn_delete.setEnabled(false);
            	}else if ("save".equals(modifyState)){
            		if (!"".equals(et_uid.getText().toString())){
            			if (!appState.isDBOpen()){
            				appState.getDB();
            			}
                		cursor = appState.get_patient(et_uid.getText().toString());
                		if (cursor != null && cursor.getCount() > 0){//如果有就修改
                			appState.Update_patient(et_uid.getText().toString(), 
                    				et_fname.getText().toString(), 
                    				et_lname.getText().toString(), 
                    				et_tel.getText().toString(), 
                    				et_mail.getText().toString(), 
                    				et_note.getText().toString() 
                    				);
                    		clearAll();
                    		cursor.close();
                		}            		
                	}
                	updateUI();
                	modifyState = "modi";
                	btn_modify.setBackgroundResource(R.drawable.modify_selector);
                	listView_user.setEnabled(true);	//使能listview
                	disableAll();
                	clearSelected();
                	btn_add.setEnabled(true);
                	btn_modify.setEnabled(false);
            		btn_delete.setEnabled(false);
            	}            	
            }            
		});
		
		btn_delete.setOnClickListener(new Button.OnClickListener(){//创建监听    
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
						                	String s = sb.toString();
						                	String [] sa = s.split(",");
						                	
						                	if (!appState.isDBOpen()){
						            			appState.getDB();
						            		}
						                	
						                	for (int i=0; i<sa.length; i++){
						                		appState.del_patient(sa[i]);
						                	}
						                	sb.delete(0, sb.length());
						            	}          
						            	clearAll();
						            	updateUI();
									}
						}).show();
            	
            }            
		});
		
		cb_user_quanxuan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
						
						sb.append( m.get("uid") + ",");
						
//						btn_modify.setEnabled(false);
                		btn_delete.setEnabled(true);
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
					
//					btn_modify.setEnabled(false);
            		btn_delete.setEnabled(false);
				}
				saImageItems.notifyDataSetChanged();
			}
		});
		
	}

	
	//------------------------------------------------------------------
	public class ZuJian_user {
		public LinearLayout list_user;
		public TextView list_user_xuanzhong;
		public TextView list_uid;
		public TextView list_username;
		public TextView list_tel;
		public TextView list_mail;
		public TextView list_note;	
	}
	
	private ArrayList<HashMap<String, Object>> lst;
	// 生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
	private MyListAdapter saImageItems = null;
	private ListView listView_user;
	private HashMap<String, Object> map = new HashMap<String, Object>();
	private Cursor cursor = null;

	private void updateUI() {
		// TODO Auto-generated method stub
		lst = new ArrayList<HashMap<String, Object>>();
		saImageItems = new MyListAdapter(getActivity(), lst);// 没什么解释		
		
		cursor = appState.get_patient();
		if (cursor != null && cursor.getCount() > 0){			
			while (cursor.moveToNext()) {
				map = new HashMap<String, Object>();
				map.put("sel", false);
				map.put("uid", cursor.getString(0));
				map.put("fname", cursor.getString(1));
				map.put("lname", cursor.getString(2));
				map.put("tel", cursor.getString(3));
				map.put("mail", cursor.getString(4));
				map.put("note", cursor.getString(5));
				lst.add(map);
			}
			cursor.close();
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
		listView_user.setAdapter(saImageItems);
		saImageItems.notifyDataSetChanged();
		// 点击控件监听器
		listView_user.setOnItemClickListener(new ItemClickListener());
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
				
				int start = sb.indexOf(m.get("uid") + ",");
            	int end = start + (m.get("uid") + ",").length();
//            	Log.i("info", sb.toString() + "/" + String.valueOf(start) + "/" + String.valueOf(end));
            	sb.delete(start, end);
            	Log.i("info", sb.toString());
            	
            	if (sb.length() == 0){
            		//删除禁用                	
                	btn_delete.setEnabled(false);
            	}
			}else{ //没选中，变选中
				m.remove("sel");
				m.put("sel", true);						
				lst.remove(position);
				lst.add(position, m);
				
				sb.append( m.get("uid") + ",");
            	Log.i("info", sb.toString());
            	
            	//修改和删除使能
            	btn_modify.setEnabled(true);
            	btn_delete.setEnabled(true);
			}
			saImageItems.notifyDataSetChanged();
			
			et_uid.setText(m.get("uid").toString());
			et_fname.setText(m.get("fname").toString());
			et_lname.setText(m.get("lname").toString());
			et_tel.setText(m.get("tel").toString());
			et_mail.setText(m.get("mail").toString());
			et_note.setText(m.get("note").toString());
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
		

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ZuJian_user zuJian = null;
			if (convertView == null) {
				zuJian = new ZuJian_user();
				// 获取组件布局
				convertView = layoutInflater.inflate(R.layout.lv_user_body, null);
				
				zuJian.list_user = (LinearLayout) convertView.findViewById(R.id.list_user);
				
				zuJian.list_user_xuanzhong = (TextView) convertView.findViewById(R.id.list_user_xuanzhong);
				zuJian.list_uid = (TextView) convertView.findViewById(R.id.list_uid);
				zuJian.list_username = (TextView) convertView.findViewById(R.id.list_username);
				zuJian.list_tel = (TextView) convertView.findViewById(R.id.list_tel);
				zuJian.list_mail = (TextView) convertView.findViewById(R.id.list_mail);
				zuJian.list_note = (TextView) convertView.findViewById(R.id.list_note);

				// 这里要注意，是使用的tag来存储数据的。
				convertView.setTag(zuJian);
			} else {
				zuJian = (ZuJian_user) convertView.getTag();

			}

			// 绑定数据、以及事件触发		
			if ( (Boolean) data.get(position).get("sel") ){
				zuJian.list_user_xuanzhong.setText("√");
			}else{
				zuJian.list_user_xuanzhong.setText("");
			}
			zuJian.list_uid.setText((String) data.get(position).get("uid"));
			zuJian.list_username.setText((String) data.get(position).get("fname") + " " + (String) data.get(position).get("lname"));
			zuJian.list_tel.setText((String) data.get(position).get("tel"));
			zuJian.list_mail.setText((String) data.get(position).get("mail"));
			zuJian.list_note.setText((String) data.get(position).get("note"));		

			return convertView;
		}
		
		
	}
}