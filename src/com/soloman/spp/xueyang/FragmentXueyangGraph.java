package com.soloman.spp.xueyang;


import java.math.BigDecimal;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;

import com.bluetooth.le.soloman.GlobalVar;
import com.bluetooth.le.soloman.R;
import com.bluetooth.le.soloman.gridUser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentXueyangGraph extends Fragment {

	public GlobalVar appState;
	public static final String TYPE = "type";  
	public Button btn_refresh ,btn_graphseluser, btn_graphswitch;
	  
	  
	    
	  private int index = 0;  
	  
	  private TextView tv_graphdata;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		appState = (GlobalVar) getActivity().getApplicationContext(); // 获得全局变量
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	public String [] value,value2, value3;	//   value
	public String [] unit, unit2, unit3;	//   unit 
	public String [] date, date2, date3;	//  date
	
	public void addPoint(String mode ){
		double x = 0;
		String y = "";
		Cursor cursor = null;
		
		if (!appState.isDBOpen()){
			appState.getDB();
		}
		
		if (!"".equals(appState.userID)){
			cursor = appState.database.getXueyang(appState.userID);
			if (cursor!=null && cursor.getCount() > 0){
				cursor.moveToFirst();
				if ("o2".equals(mode)){
					value = new String [cursor.getCount()];
					date = new String [cursor.getCount()];
					while (!cursor.isAfterLast()){					
						x++;
						y = cursor.getString(1);
						mCurrentSeries.add(x,  Integer.valueOf(y));
						
						value[(int) (x -1)] = y;//血氧
						date[(int) (x -1)] = cursor.getString(4);
						
						cursor.moveToNext();
					}
				}else if ("pul".equals(mode)){
					value2 = new String [cursor.getCount()];
					unit2 = new String [cursor.getCount()];
					date2 = new String [cursor.getCount()];
					while (!cursor.isAfterLast()){					
						x++;
						y = cursor.getString(2);
						mCurrentSeries.add(x,  Integer.valueOf(y));
						
						value2[(int) (x -1)] = cursor.getString(2);//脉搏率
						date2[(int) (x -1)] = cursor.getString(4);
						
						cursor.moveToNext();
					}
				}
				
				cursor.close();
			}

		}
          
 
        if (mChartView != null) {  
          mChartView.repaint();//重画图表  
        }  
        //生成图片保存,注释掉下面的代码不影响图表的生成.  
        //-->start  
//        Bitmap bitmap = mChartView.toBitmap();  
//        try {  
//          File file = new File(Environment.getExternalStorageDirectory(), "test" + index++ + ".png");  
//          FileOutputStream output = new FileOutputStream(file);  
//          bitmap.compress(CompressFormat.PNG, 100, output);  
//        } catch (Exception e) {  
//          e.printStackTrace();  
//        }  
        //-->end  
	}
	
//	 @Override  
//	 public void onRestoreInstanceState(Bundle savedState) {  
//	    super.onRestoreInstanceState(savedState);  
//	    mDataset = (XYMultipleSeriesDataset) savedState.getSerializable("dataset");  
//	    mRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable("renderer");  
//	    mCurrentSeries = (XYSeries) savedState.getSerializable("current_series");  
//	    mCurrentRenderer = (XYSeriesRenderer) savedState.getSerializable("current_renderer");  
//	    mDateFormat = savedState.getString("date_format");  
//	  }  
	  
//	  @Override
//	public void onSaveInstanceState(Bundle outState) {  
//	    super.onSaveInstanceState(outState);  
//	    outState.putSerializable("dataset", mDataset);  
//	    outState.putSerializable("renderer", mRenderer);  
//	    outState.putSerializable("current_series", mCurrentSeries);  
//	    outState.putSerializable("current_renderer", mCurrentRenderer);  
//	    outState.putString("date_format", mDateFormat);  
//	  }  
	  
	@Override
	public void onResume() {  
	    super.onResume();  
	    if (!"none".equals(appState.userID)) {
	    	AChart(); 
	    }
	}
	
	@Override
	public void onPause(){
		super.onPause();
		appState.dbClose();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//return inflater.inflate(R.layout.fragment_sleep, container, false);	
		
		View view = inflater.inflate(R.layout.fragment_themometergraph, container, false);
	
		findView(view);
		
		
		// 得到当前线程的Looper实例，由于当前线程是UI线程也可以通过Looper.getMainLooper()得到
		Looper looper = Looper.myLooper();
		// 此处甚至可以不需要设置Looper，因为 Handler默认就使用当前线程的Looper
		messageHandler = new MessageHandler(looper);

				
        return view;       
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
	private String mode = "o2";
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	  private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	  private XYSeries mCurrentSeries, mCurrentSeriesSurface;  	  
	  private XYSeriesRenderer mCurrentRenderer;  	  
	  private String mDateFormat;  
	  private GraphicalView mChartView;  
	  private LinearLayout layout = null;
	  
	public void AChart(){
		mDataset = new XYMultipleSeriesDataset();  	  
		mRenderer = new XYMultipleSeriesRenderer();
//		mCurrentRenderer = new XYSeriesRenderer();
		
		  
	    mRenderer.setApplyBackgroundColor(true);//设置是否显示背景色  
	    mRenderer.setBackgroundColor(Color.argb(50, 214, 241, 255));//设置背景色  
	    
	    mRenderer.setAxisTitleTextSize(32); //设置轴标题文字的大小  
	    mRenderer.setAxesColor(Color.BLACK);//坐标轴颜色
	    
	    mRenderer.setChartTitleTextSize(32);//?设置整个图表标题文字大小  
	    mRenderer.setChartTitle(appState.userID);
	    
	    mRenderer.setXLabelsColor(Color.BLACK);//设置X轴刻度颜色
	    mRenderer.setYLabelsColor(0, Color.BLACK);//设置Y轴刻度颜色	    
	    mRenderer.setLabelsTextSize(32);//设置刻度显示文字的大小(XY轴都会被设置)  
	    
	    mRenderer.setLegendTextSize(32);//图例文字大小  
	    
	    mRenderer.setMargins(new int[] { 30, 30, 50, 10 });//设置图表的外边框(上/左/下/右)  
	    mRenderer.setMarginsColor(Color.argb(50, 214, 241, 255));//边框颜色
	    mRenderer.setZoomButtonsVisible(false);//是否显示放大缩小按钮  
	    mRenderer.setShowGrid(true); //显示网格
	    mRenderer.setPointSize(10);//设置点的大小(图上显示的点的大小和图例中点的大小都会被设置)  

	    //1
	    String seriesTitle = "O2";
//	    if ("body".equals(mode)){
//	    	seriesTitle = "Body Series ";// + (mDataset.getSeriesCount() + 1);//图例  
//	    }else if("surface".equals(mode)){
//	    	seriesTitle = "Surface Series ";// + (mDataset.getSeriesCount() + 1);//图例  
//	    }
	     
	    if (mDataset.getSeriesCount() > 0){	    	 
	    	mDataset.removeSeries(0); 
	    	mRenderer.removeAllRenderers();
	    }
	    
	    XYSeries series = new XYSeries(seriesTitle);//定义XYSeries
	    mDataset.addSeries(series);//在XYMultipleSeriesDataset中添加XYSeries  
	    mCurrentSeries = series;//设置当前需要操作的XYSeries  \	    
        
        addPoint("o2");//血氧
        
        XYSeriesRenderer renderer = new XYSeriesRenderer();//定义XYSeriesRenderer  
        mRenderer.addSeriesRenderer(renderer);//将单个XYSeriesRenderer增加到XYMultipleSeriesRenderer
        
        renderer.setPointStyle(PointStyle.CIRCLE);//点的类型是圆形  
        renderer.setFillPoints(true);//设置点是否实心  
        if ("o2".equals(mode)){
        	renderer.setColor(Color.BLUE); //折线颜色
	    }else if("pul".equals(mode)){
	    	renderer.setColor(Color.RED); //折线颜色
	    }
        
        mCurrentRenderer = renderer;  
        
        //2
        String seriesTitle2 = "";

	    seriesTitle2 = "Pulse";// + (mDataset.getSeriesCount() + 1);//图例  
	     
	    XYSeries series2 = new XYSeries(seriesTitle2);//定义XYSeries
	    mDataset.addSeries(series2);//在XYMultipleSeriesDataset中添加XYSeries  
	    mCurrentSeries = series2;//设置当前需要操作的XYSeries  \	    
        
        addPoint("pul");
        
        XYSeriesRenderer renderer2 = new XYSeriesRenderer();//定义XYSeriesRenderer  
        mRenderer.addSeriesRenderer(renderer2);//将单个XYSeriesRenderer增加到XYMultipleSeriesRenderer
        
        renderer2.setPointStyle(PointStyle.CIRCLE);//点的类型是圆形  
        renderer2.setFillPoints(true);//设置点是否实心  
	    renderer2.setColor(Color.RED); //折线颜色
        
        mCurrentRenderer = renderer2;  
	    
//	    if (mChartView == null) {  
        if (layout != null){
        	layout.removeAllViews();
        }
	        layout = (LinearLayout) getActivity().findViewById(R.id.chart);  
	        mChartView = ChartFactory.getLineChartView(getActivity().getApplicationContext(), mDataset, mRenderer);  
	        mRenderer.setClickEnabled(true);//设置图表是否允许点击  
	        mRenderer.setSelectableBuffer(100);//设置点的缓冲半径值(在某点附件点击时,多大范围内都算点击这个点)  
	        mChartView.setOnClickListener(new View.OnClickListener() {  
	          @Override  
	          public void onClick(View v) {  
	              //这段代码处理点击一个点后,获得所点击的点在哪个序列中以及点的坐标.  
	              //-->start  
	            SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();  
	            double[] xy = mChartView.toRealPoint(0);  
	            if (seriesSelection == null) {  
//	              Toast.makeText(getActivity().getApplicationContext(), "No chart element was clicked", Toast.LENGTH_SHORT).show();  
	            } else {  
//	              Toast.makeText(getActivity().getApplicationContext(),  
//	                  "Chart element in series index " + seriesSelection.getSeriesIndex()  
//	                      + " data point index " + seriesSelection.getPointIndex() + " was clicked"  
//	                      + " closest point value X=" + seriesSelection.getXValue() + ", Y=" + seriesSelection.getValue()  
//	                      + " clicked point value X=" + (float) xy[0] + ", Y=" + (float) xy[1], Toast.LENGTH_SHORT).show();  
	              
//	              tv_graphdata.setText("X = " + String.valueOf(seriesSelection.getXValue()) +
//	            		  " Y = " + String.valueOf(seriesSelection.getValue()));
	            	
	            	tv_graphdata.setTextSize(16);
	            	if (seriesSelection.getSeriesIndex() == 0){//sys series
	            		tv_graphdata.setText(appState.userID + "  " 
	  	            		  + appState.userName + "\n"
	  	            		  + "O2:" + value[(int) seriesSelection.getXValue() - 1] +  "%,"
	  	            		  + "PUL:" + value2[(int) seriesSelection.getXValue() - 1] + "/min\n"
	  	            		  + date[(int) seriesSelection.getXValue() - 1]);
	            	}else if (seriesSelection.getSeriesIndex() == 1){//dia series
	            		tv_graphdata.setText(appState.userID + "  " 
		  	            		+ appState.userName + "\n"
		  	            		+ "O2:" + value[(int) seriesSelection.getXValue() - 1] +  "%,"
		  	  	            	+ "PUL:" + value2[(int) seriesSelection.getXValue() - 1] + "/min\n"
		  	            		+ date2[(int) seriesSelection.getXValue() - 1]);
		            }else if (seriesSelection.getSeriesIndex() == 2){//pul series
	            		tv_graphdata.setText(appState.userID + "  " 
		  	            		+ appState.userName + "\n"
		  	            		+ "O2:" + value[(int) seriesSelection.getXValue() - 1] + "%,"
		  	  	            	+ "PUL:" + value2[(int) seriesSelection.getXValue() - 1] + "/min\n"
		  	            		+ date[(int) seriesSelection.getXValue() - 1]);
		            }
	              
	              
	            }  
	            //-->end  
	          }  
	        });  
	        mChartView.setOnLongClickListener(new View.OnLongClickListener() {  
	          @Override  
	          public boolean onLongClick(View v) {  
	            SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();  
	            if (seriesSelection == null) {  
//	              Toast.makeText(XYChartBuilder.this, "No chart element was long pressed",Toast.LENGTH_SHORT);  
	              return false; // no chart element was long pressed, so let something  
	              // else handle the event  
	            } else {  
//	              Toast.makeText(XYChartBuilder.this, "Chart element in series index "  
//	                  + seriesSelection.getSeriesIndex() + " data point index "  
//	                  + seriesSelection.getPointIndex() + " was long pressed", Toast.LENGTH_SHORT);  
	              return true; // the element was long pressed - the event has been  
	              // handled  
	            }  
	          }  
	        });  
	        //这段代码处理放大缩小  
	        //-->start  
	        mChartView.addZoomListener(new ZoomListener() {  
	          public void zoomApplied(ZoomEvent e) {  
	            String type = "out";  
	            if (e.isZoomIn()) {  
	              type = "in";  
	            }  
	            System.out.println("Zoom " + type + " rate " + e.getZoomRate());  
	          }  
	            
	          public void zoomReset() {  
	            System.out.println("Reset");  
	          }  
	        }, true, true);  
	        //-->end  
	        //设置拖动图表时后台打印出图表坐标的最大最小值.  
	        mChartView.addPanListener(new PanListener() {  
	          public void panApplied() {  
	            System.out.println("New X range=[" + mRenderer.getXAxisMin() + ", " + mRenderer.getXAxisMax()  
	                + "], Y range=[" + mRenderer.getYAxisMax() + ", " + mRenderer.getYAxisMax() + "]");  
	          }  
	        });  
	        layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));  
	        boolean enabled = mDataset.getSeriesCount() > 0;  
//	      } else {  
//	        mChartView.repaint();  
//	      } 
	}
	
	public void findView(View view){
		tv_graphdata = (TextView) view.findViewById(R.id.tv_graphdata);
		btn_refresh =  (Button) view.findViewById(R.id.btn_refresh);
		btn_graphseluser =  (Button) view.findViewById(R.id.btn_graphseluser);
		btn_graphswitch =  (Button) view.findViewById(R.id.btn_graphswitch);
		
		btn_refresh.setOnClickListener(new Button.OnClickListener(){//创建监听    
            public void onClick(View v) {    
            	 AChart();
            }
		});
		
		btn_graphseluser.setOnClickListener(new Button.OnClickListener(){//创建监听    
            public void onClick(View v) {    
            	Intent it = new Intent(getActivity(), gridUser.class);
    			startActivityForResult(it, 1);	
            }
		});
		
		btn_graphswitch.setOnClickListener(new Button.OnClickListener(){//创建监听    
            public void onClick(View v) {    
            	if ("body".equals(mode)){
            		mode = "surface";
            	}else if ("surface".equals(mode)){
            		mode = "body";
            	}
            	 AChart();
            }
		});
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
				
				if (uid != null){
					if (name == null){
						name = "";
					}
					if (note ==null){
						note = "";
					}
					
					appState.userID = uid;
					appState.userName = name;			
					
					AChart();

				}
			}
			
		}
	}
	
}



