package sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class CokeGameSQL extends SQLiteOpenHelper {

	private int onlyonerecord;//한게임당 한번만 입력하게 하기위해
	
	public CokeGameSQL(Context context,String name,CursorFactory factory,int version)
	{
		super(context, name, factory, version);
		this.onlyonerecord = 0;	
	}
	public void setonlyOneR(int o)
	{
		this.onlyonerecord  = o;
	}
	public int getonlyOneR()
	{
		return this.onlyonerecord;
	}
	public void setRestart()
	{
		this.onlyonerecord = 0;
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE CokeRankBoard(score INTEGER, dat DATE);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}


}

