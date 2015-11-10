package com.solmi.m1app;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * ���� ������ ���� ���ø����̼� Ŭ����
 * @author sb hwang
 *
 */

public class M1APP extends Application{

	//define
	private final String APPNAME = "M1APPv1";  
	//global variable
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}
	
	
	/**
	 * ���������� ����Ǿ��� ������� �ּҸ� ��ȯ�Ѵ�.
	 * @return
	 */
	public String getLastConnection() {		
		String devname = "";
    	SharedPreferences state = getSharedPreferences(APPNAME, MODE_PRIVATE);    	
    	devname = state.getString("bt_dev_addr", "00:00:00:00:00:00");
    	
		return devname;
	}
	
	/**
	 * ���������� ����� ������� �ּҸ� �����Ѵ�.
	 * @param devName
	 * @return
	 */
	public boolean setLastConnection(String devName){
		boolean bRtn = false;
		SharedPreferences state = getSharedPreferences(APPNAME, MODE_PRIVATE);
	    SharedPreferences.Editor editor = state.edit();
	    editor.putString("bt_dev_addr", devName);
	    bRtn = editor.commit();	    
	    return bRtn;
	}
	
	
	
	
	

	

}
