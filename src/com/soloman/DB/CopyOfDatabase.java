/**
 * ���ݿ�
 * 
 * @author ����
 * 
 */
package com.soloman.DB;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CopyOfDatabase {

//	dbversion	
//	1:�¶ȼơ����˺ͻ�������	
//	2:����˯��
//	
	
	private int dbversion = 3;
	private String db_name = "inurse.db";
	private String table_patient = "binren";// ���˱�
	private String table_value = "celiangjieguo";// �¶Ȳ��������
	private String table_setting = "setting";// ���ñ�
	private String table_shuimian = "shuimian";// ˯�߼�¼
	private String table_xueya = "xueya";// Ѫѹ������¼
	private Context mCtx = null;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase SQLdb;

	public class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
			System.out.println("DB databasehelper(context,name,factory,version)");
		}

		public DatabaseHelper(Context context) {
			super(context, db_name, null, dbversion);
			// TODO Auto-generated constructor stub
			System.out.println("DB databasehelper(context)");
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			System.out.println("DB onCreate");
			// ֻ�ڵ�һ�δ�����ʱ�����
			db.execSQL("create table IF NOT EXISTS " + table_patient + 
					"(id varchar(10) ," + //����id
					"firstname varchar(20), " + 
					"lastname varchar(20), " + 
					"tel varchar(20), " + 
					"mail varchar(50), " +					
					"note varchar(100) )");	//��ע		
			
			SQLdb = db;
			System.out.println("DB onCreate --- table_patient");
			
			db.execSQL("create table IF NOT EXISTS " + table_value + 
					"(id varchar(10) ," + //����id
					"devicetype varchar(10), " + // �豸���ͣ�1�¶ȼ�
					"mode varchar(10), " + // ģʽsurface body room
					"unit varchar(10), " + // ��λ��,�H
					"value varchar(10), " + // ����ֵ
					"date varchar(50), " + // ��������
					"time varchar(50), " + //����ʱ��
					"note varchar(100) )"); //ÿ����¼��note��ҽ��
			SQLdb = db;
			System.out.println("DB onCreate --- table_value");			
			
			db.execSQL("create table IF NOT EXISTS " + table_setting + 
					"(id varchar(1), " + //��ţ���Զֻ��һ����¼"1"					
					"autodel int ," + //�Զ�ɾ��0 1
					"whendel varchar(10), " + // ��ʱ�Զ�ɾ��dailly,weekly,monthly,aftersend,off
					
					"automail int, " + // �Զ�mail 0 1
					"mail1 varchar(50), " + // 
					"mail2 varchar(50), " + // 
					"whenmail varchar(10), " + // ��ʱ�Զ����ʼ�dailly,weekly,monthly
					"mailtime1 varchar(2), " + // �Զ����ʼ�ʱ��Сʱ 00-23
					"mailtime2 varchar(2), " + // �Զ����ʼ�ʱ����� 00-59
					
					"autoupload int, " + // �Զ�upload 0 1
					"serverurl varchar(100), " + // �ϴ�����
					
					"autosave int, " + // �Զ�save 0 1
					"path varchar(50), " + // ����·��
					"ext varchar(5), " + // ��׺ .txt .xls
					"separate varchar(1), " + // �ָ���,; ?|$#* ��8��
					
					"userid int, " + // ��ʾ�ֶ� userid 0 1
					"fname int, " + // ��ʾ�ֶ� firstname 0 1
					"lname int, " + // ��ʾ�ֶ� lastname 0 1
					"devicetype int, " + // ��ʾ�ֶ�devicetype 0 1
					"deviceid int, " + // ��ʾ�ֶ�deviceid 0 1
					"date int, " + // ��ʾ�ֶ�date 0 1
					"value int, " + // ��ʾ�ֶ�value 0 1
					"mode int, " + // ��ʾ�ֶ�mode 0 1
					"unit int, " + // ��ʾ�ֶ�unit 0 1
					"note int )");//��ʾ�ֶ�note 0 1
			SQLdb = db;
			System.out.println("DB onCreate --- table_setting");			
			
			//˯��
			db.execSQL("create table IF NOT EXISTS " + table_shuimian + 
					"(id varchar(10) ," + //����id
					"filepath varchar(100), " + // ¼���ļ�·��
					"filename varchar(50), " + // ¼���ļ���xxx.amr
					"starttime varchar(50), " + // ˯�߿�ʼʱ��
					"endtime varchar(50), " + //˯�߽���ʱ��
					"note varchar(100), " + //ÿ����¼��note��ҽ��
					"ext1 varchar(50), " + //ext1 ��ʱ����duration
					"ext2 varchar(50), " +
					"ext3 varchar(50) )"); 
			SQLdb = db;
			System.out.println("DB onCreate --- table_shuimian");		
			
			//Ѫѹ��
			db.execSQL("create table IF NOT EXISTS " + table_xueya + 
					"(id varchar(10) ," + //����id
					"devicetype varchar(10), " + // �豸���ͣ�1Ѫѹ��
					"unit varchar(10), " + // ��λmmHg
					"sys varchar(10), " + // ��ѹ������ѹ��
					"dia varchar(10), " + // ��ѹ������ѹ��
					"pul varchar(10), " + // ����  pulse/min
					"date varchar(50), " + // ��������
					"time varchar(50), " + //����ʱ��
					"note varchar(100) )"); //ÿ����¼��note��ҽ��
			SQLdb = db;
			System.out.println("DB onCreate --- table_xueya");			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			switch (newVersion) {
			case 2:
				upgrade1to2(db);
				break;
			case 3:
				upgrade1to2(db);
				upgrade2to3(db);
				break;
			}
		}

		private void upgrade1to2(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			try  
		    {  
				db.beginTransaction();  		  
		        // 1, Rename table.  
		        String tempTableName = table_patient + "_temp";  
		        String sql = "ALTER TABLE " + table_patient +" RENAME TO " + tempTableName;  
		        db .execSQL( sql);  
		        
		        String tempTableName1 = table_value + "_temp";  
		        sql = "ALTER TABLE " + table_value +" RENAME TO " + tempTableName1;  
		        db .execSQL( sql);  
		        
		        String tempTableName2 = table_setting + "_temp";  
		        sql = "ALTER TABLE " + table_setting +" RENAME TO " + tempTableName2;  
		        db .execSQL( sql);  
		  
		        // 2, Create table.  
		        onCreate(db);  
		  
		        // 3, Load data  
		        sql =   "INSERT INTO " + table_patient +  
		                " (id, firstname, lastname, tel, mail,  note ) " +  
		                " SELECT " + "id, firstname, lastname, tel, mail,  note " + " FROM " + tempTableName;  		  
		        db.execSQL( sql);  
		        
		        sql =   "INSERT INTO " + table_value +  
		                " (id, devicetype, mode, unit, value,  date, time, note ) " +  
		                " SELECT " + "id, devicetype, mode, unit, value,  date, time, note " + " FROM " + tempTableName1;  		  
		        db.execSQL( sql);  
		        
		        sql =   "INSERT INTO " + table_setting +  
		                " (id, autodel, whendel, automail, mail1,  mail2, whenmail, mailtime1, mailtime2, autoupload, serverurl, autosave, path, ext, separate, userid, fname, lname, devicetype, deviceid, date, value, mode, unit, note ) " +  
		                " SELECT " + "id, autodel, whendel, automail, mail1,  mail2, whenmail, mailtime1, mailtime2, autoupload, serverurl, autosave, path, ext, separate, userid, fname, lname, devicetype, deviceid, date, value, mode, unit, note " + " FROM " + tempTableName2;  		  
		        db.execSQL( sql);  
		  
		        // 4, Drop the temporary table.  
		        db.execSQL( "DROP TABLE IF EXISTS " + tempTableName);  	
		        db.execSQL( "DROP TABLE IF EXISTS " + tempTableName1);
		        db.execSQL( "DROP TABLE IF EXISTS " + tempTableName2);
		        db.setTransactionSuccessful();  
		    }  
		    catch (SQLException e)  
		    {  
		        e.printStackTrace();  
		    }  
		    catch (Exception e)  
		    {  
		        e.printStackTrace();  
		    }  
		    finally  
		    {  
		    	db.endTransaction();  
		    }  
		}
		
		private void upgrade2to3(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			try  
		    {  
				db.beginTransaction();  		  
		        // 1, Rename table.  
		        String tempTableName = table_patient + "_temp";  
		        String sql = "ALTER TABLE " + table_patient +" RENAME TO " + tempTableName;  
		        db .execSQL( sql);  
		        
		        String tempTableName1 = table_value + "_temp";  
		        sql = "ALTER TABLE " + table_value +" RENAME TO " + tempTableName1;  
		        db .execSQL( sql);  
		        
		        String tempTableName2 = table_setting + "_temp";  
		        sql = "ALTER TABLE " + table_setting +" RENAME TO " + tempTableName2;  
		        db .execSQL( sql);  
		        
		        String tempTableName3 = table_xueya + "_temp";  
		        sql = "ALTER TABLE " + table_xueya +" RENAME TO " + tempTableName3;  
		        db .execSQL( sql);  
		  
		        // 2, Create table.  
		        onCreate(db);  
		  
		        // 3, Load data  
		        sql =   "INSERT INTO " + table_patient +  
		                " (id, firstname, lastname, tel, mail,  note ) " +  
		                " SELECT " + "id, firstname, lastname, tel, mail,  note " + " FROM " + tempTableName;  		  
		        db.execSQL( sql);  
		        
		        sql =   "INSERT INTO " + table_value +  
		                " (id, devicetype, mode, unit, value,  date, time, note ) " +  
		                " SELECT " + "id, devicetype, mode, unit, value,  date, time, note " + " FROM " + tempTableName1;  		  
		        db.execSQL( sql);  
		        
		        sql =   "INSERT INTO " + table_setting +  
		                " (id, autodel, whendel, automail, mail1,  mail2, whenmail, mailtime1, mailtime2, autoupload, serverurl, autosave, path, ext, separate, userid, fname, lname, devicetype, deviceid, date, value, mode, unit, note ) " +  
		                " SELECT " + "id, autodel, whendel, automail, mail1,  mail2, whenmail, mailtime1, mailtime2, autoupload, serverurl, autosave, path, ext, separate, userid, fname, lname, devicetype, deviceid, date, value, mode, unit, note " + " FROM " + tempTableName2;  		  
		        db.execSQL( sql);  
		        
		        sql =   "INSERT INTO " + table_xueya +  
		                " (id, devicetype, unit, sys, dia, pul,  date, time, note ) " +  
		                " SELECT " + "id, devicetype, unit, sys, dia, pul,  date, time, note " + " FROM " + tempTableName3;  		  
		        db.execSQL( sql);  
		  
		        // 4, Drop the temporary table.  
		        db.execSQL( "DROP TABLE IF EXISTS " + tempTableName);  	
		        db.execSQL( "DROP TABLE IF EXISTS " + tempTableName1);
		        db.execSQL( "DROP TABLE IF EXISTS " + tempTableName2);
		        db.execSQL( "DROP TABLE IF EXISTS " + tempTableName3);
		        db.setTransactionSuccessful();  
		    }  
		    catch (SQLException e)  
		    {  
		        e.printStackTrace();  
		    }  
		    catch (Exception e)  
		    {  
		        e.printStackTrace();  
		    }  
		    finally  
		    {  
		    	db.endTransaction();  
		    }  
		}
		
	}

	
	
	/** ���캯�� */
	public CopyOfDatabase(Context ctx) {
		this.mCtx = ctx;
		System.out.println("DB���캯��");
	}

	public CopyOfDatabase open() throws SQLException {
		System.out.println("DB Open");
		dbHelper = new DatabaseHelper(mCtx);
		// ֻ�е���getReadableDatabase����getWriteableDatabase�������Żᴴ�����ݿ����
		SQLdb = dbHelper.getWritableDatabase();
		return this;
	}
	
	public Boolean isOpen(){
		return SQLdb.isOpen();
	}

	public void close() {
		dbHelper.close();
	}

	public long add_patient(String id, String firstname, String lastname, String tel, String mail, String note ) {
		ContentValues cv = new ContentValues();

		cv.put("id", id);
		cv.put("firstname", firstname);
		cv.put("lastname", lastname);
		cv.put("tel", tel);
		cv.put("mail", mail);
		cv.put("note", note);

		System.out.println("DB.add_patient " + table_patient);
		return SQLdb.insert(table_patient, null, cv);
	}	

	
	public Cursor get_patient(){
		Cursor cursor = null;
		try {
			cursor = SQLdb.query(table_patient, // table��
					new String[] { "id", "firstname", "lastname", "tel", "mail","note"}, // �ֶ�
					null, // ����
					null, 
					null, //group by
					null, //having
					"id desc");//order by
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
		return cursor;
	}
	
	public Cursor get_patient(String id){
		Cursor cursor = null;
		try {
			cursor = SQLdb.query(table_patient, // table��
					new String[] { "id", "firstname", "lastname", "tel", "mail","note"}, // �ֶ�
					"id = '" + id + "'", // ����
					null, 
					null, //group by
					null, //having
					"id desc");//order by
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
		return cursor;
	}
	
	
	// �޸���Ϣ
	public int Update_patient(String id, String firstname, String lastname, String tel, String mail, String note ) {
		ContentValues cv = new ContentValues();
		cv.put("firstname", firstname);
		cv.put("lastname", lastname);
		cv.put("tel", tel);
		cv.put("mail", mail);
		cv.put("note", note);
		String[] args = { String.valueOf(id) };
		return SQLdb.update(table_patient, cv, "id=?", args);
	}
	
	public long del_patient(String id) {
		return SQLdb.delete(table_patient, "id = '" + id + "'", null);
	}

	//-------------------------------------------------------------------------------------
	
	
	public long add_Record(String id, String devicetype, String mode, String unit, String value, String date, String time ) {
		ContentValues cv = new ContentValues();

		cv.put("id", id);
		cv.put("devicetype", devicetype);
		cv.put("mode", mode);
		cv.put("unit", unit);
		cv.put("value", value);
		cv.put("date", date);
		cv.put("time", time);

		System.out.println("DB.add_patient " + table_patient);
		return SQLdb.insert(table_value, null, cv);
	}
	
	public Cursor getRecord(String devicetype ) {
		Cursor cursor = null;
		try {
			cursor = SQLdb.query(table_value, // table��
					new String[] { "id", "mode", "unit", "value", "date","time", "note" }, // �ֶ�
					"devicetype = '" + devicetype + "'", // ����
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
		return cursor;
	}	
	
	public Cursor getRecord(String id, String devicetype ) {
		Cursor cursor = null;
		try {
			cursor = SQLdb.query(table_value, // table��
					new String[] { "id", "mode", "unit", "value", "date","time", "note" }, // �ֶ�
					"id = '" + id + "' and " + "devicetype = '" + devicetype + "'", // ����
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
		return cursor;
	}		
	
	public Cursor getRecord(String id, String devicetype, String mode ) {
		Cursor cursor = null;
		try {
			cursor = SQLdb.query(table_value, // table��
					new String[] { "id", "mode", "unit", "value", "date","time", "note" }, // �ֶ�
					"id = '" + id + "' and " + "devicetype = '" + devicetype + "' and " + "mode = '" + mode + "'", // ����
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
		return cursor;
	}
	
	// �޸���Ϣ
		public int updateRecord (String id, String date, String note ) {
			ContentValues cv = new ContentValues();
			cv.put("note", note);
			String[] args = { id, date };
			return SQLdb.update(table_value, cv, "id=? and date=?", args);
		}
	
	public long delRecord(String id, String date) {
		return SQLdb.delete(table_value, "id = '" + id + "' and date = '" + date + "'",
				null);
	}
	
	//--------------------------------------------------------------------------------------------
	public long add_setting(int autodel, String whendel, 
			int automail, String mail1, String mail2, String whenmail, String mailtime1, String mailtime2,
			int autoupload, String serverurl, 
			int autosave, String path, String ext, String separate,
			int userid, int fname, int lname, int devicetype, int deviceid, int date, int value, int mode, int unit, int note) {
		ContentValues cv = new ContentValues();

		cv.put("id", "1");
		
		cv.put("autodel", autodel);
		cv.put("whendel", whendel);
		
		cv.put("automail", automail);
		cv.put("mail1", mail1);
		cv.put("mail2", mail2);
		cv.put("whenmail", whenmail);
		cv.put("mailtime1", mailtime1);
		cv.put("mailtime2", mailtime2);
		
		cv.put("autoupload", autoupload);
		cv.put("serverurl", serverurl);
		
		cv.put("autosave", autosave);
		cv.put("path", path);
		cv.put("ext", ext);
		cv.put("separate", separate);
		
		cv.put("userid", userid);
		cv.put("fname", fname);
		cv.put("lname", lname);
		cv.put("devicetype", devicetype);
		cv.put("deviceid", deviceid);
		cv.put("date", date);
		cv.put("value", value);
		cv.put("mode", mode);
		cv.put("unit", unit);
		cv.put("note", note);		

		System.out.println("DB.add_setting " + table_setting);
		return SQLdb.insert(table_setting, null, cv);
	}
	
	public Cursor getSetting ( ) {
		Cursor cursor = null;
		try {
			cursor = SQLdb.query(table_setting, // table��
					new String[] { "autodel", "whendel", 
						"automail", "mail1", "mail2","whenmail","mailtime1","mailtime2",
						"autoupload", "serverurl", 
						"autosave","path","ext", "separate",
						"userid","fname","lname","devicetype","deviceid","date","value","mode","unit","note"}, // �ֶ�
					null, // ����
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
		return cursor;
	}	
	
	public int updateSetting(int autodel, String whendel, 
			int automail, String mail1, String mail2, String whenmail, String mailtime1, String mailtime2,
			int autoupload, String serverurl, 
			int autosave, String path, String ext, String separate,
			int userid, int fname, int lname, int devicetype, int deviceid, int date, int value, int mode, int unit, int note ) {
		ContentValues cv = new ContentValues();
		
		cv.put("autodel", autodel);
		cv.put("whendel", whendel);
		
		cv.put("automail", automail);
		cv.put("mail1", mail1);
		cv.put("mail2", mail2);
		cv.put("whenmail", whenmail);
		cv.put("mailtime1", mailtime1);
		cv.put("mailtime2", mailtime2);
		
		cv.put("autoupload", autoupload);
		cv.put("serverurl", serverurl);
		
		cv.put("autosave", autosave);
		cv.put("path", path);
		cv.put("ext", ext);
		cv.put("separate", separate);
		
		cv.put("userid", userid);
		cv.put("fname", fname);
		cv.put("lname", lname);
		cv.put("devicetype", devicetype);
		cv.put("deviceid", deviceid);
		cv.put("date", date);
		cv.put("value", value);
		cv.put("mode", mode);
		cv.put("unit", unit);
		cv.put("note", note);
		
		String[] args = { "1" };
		return SQLdb.update(table_setting, cv, "id=?", args);
	}
	//-------------------------------------------------------------------------
	
	public Cursor getshuimian ( ) {
		Cursor cursor = null;
		try {
			cursor = SQLdb.query(table_shuimian, // table��
					new String[] { "id", "filepath",  "filename", "starttime", "endtime","note","ext1","ext2", "ext3"}, // �ֶ�
					//ext1 ��ʱ����duration
					null, // ����
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
		return cursor;
	}	
	
	public Cursor getshuimian (String id ) {
		Cursor cursor = null;
		try {
			cursor = SQLdb.query(table_shuimian, // table��
					new String[] { "id", "filepath",  "filename", "starttime", "endtime","note","ext1","ext2", "ext3"}, // �ֶ�
					//ext1 ��ʱ����duration
					"id = '" + id + "'", // ����
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
		return cursor;
	}	
	
	public Cursor getshuimian (String id, String starttime ) {
		Cursor cursor = null;
		try {
			cursor = SQLdb.query(table_shuimian, // table��
					new String[] { "id", "filepath",  "filename", "starttime", "endtime","note","ext1","ext2", "ext3"}, // �ֶ�
					//ext1 ��ʱ����duration
					"id = '" + id + "' and starttime=' " + starttime + "'" , // ����
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
		return cursor;
	}	
	
	public long add_shuimian(String id, String filepath, String filename, String starttime, String endtime, String note, String ext1, String ext2, String ext3) {
		ContentValues cv = new ContentValues();

		cv.put("id", id);
		cv.put("filepath", filepath);
		cv.put("filename", filename);
		cv.put("starttime", starttime);
		cv.put("endtime", endtime);
		cv.put("note", note);
		cv.put("ext1", ext1);//ext1 ��ʱ����duration
		cv.put("ext2", ext2);
		cv.put("ext3", ext3);

		System.out.println("DB.add_shuimian " + table_shuimian);
		return SQLdb.insert(table_shuimian, null, cv);
	}
	
	public int update_shuimian (String starttime, String endtime, String duration) {
		ContentValues cv = new ContentValues();
		cv.put("endtime", endtime);
		cv.put("ext1", duration);//ext1 ��ʱ����duration
		String[] args = { starttime };
		return SQLdb.update(table_shuimian, cv, "starttime=? ", args);
	}
	
	public long del_shuimian(String starttime) {
		return SQLdb.delete(table_shuimian, "starttime = '" + starttime + "'",
				null);
	}
	//-------------------------------------------------------------------------------------
	
	public long addXueya(String id, String devicetype, String unit, String sys, String dia, String pul, String date, String time ) {
		ContentValues cv = new ContentValues();

		cv.put("id", id);
		cv.put("devicetype", devicetype);
		cv.put("unit", unit);
		cv.put("sys", sys);
		cv.put("dia", dia);
		cv.put("pul", pul);
		cv.put("date", date);
		cv.put("time", time);

		System.out.println("DB.add_xueya " + table_xueya);
		return SQLdb.insert(table_xueya, null, cv);
	}
	
	public Cursor getXueya(String id ) {
		Cursor cursor = null;
		try {
			cursor = SQLdb.query(table_xueya, // table��
					new String[] { "id", "unit", "sys", "dia", "pul", "date","time", "note" }, // �ֶ�
					"id = '" + id + "'", // ����
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
		return cursor;
	}	
	
	public Cursor getXueya( ) {
		Cursor cursor = null;
		try {
			cursor = SQLdb.query(table_xueya, // table��
					new String[] { "id", "unit", "sys", "dia", "pul", "date","time", "note" }, // �ֶ�
					null, // ����
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
		return cursor;
	}
	
	// �޸���Ϣ
	public int updateXueya(String id, String date, String note) {
		ContentValues cv = new ContentValues();
		cv.put("note", note);
		String[] args = { id, date };
		return SQLdb.update(table_xueya, cv, "id=? and date=?", args);
	}
	
	public long delXueya(String date) {
		return SQLdb.delete(table_xueya, "date = '" + date + "'",
				null);
	}
	
	//-------------------------------------------------------------------------------------
	public void clearThis(String tableid) {
		SQLdb.delete(tableid, null, null);
	}
	
}
