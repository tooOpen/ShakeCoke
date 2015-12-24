package com.solmi.m1app;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * 전역 설정을 위한 어플리케이션 클래스
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
	 * 마지막으로 연결되었던 블루투스 주소를 반환한다.
	 * @return
	 */
	public String getLastConnection() {		
		String devname = "";
    	SharedPreferences state = getSharedPreferences(APPNAME, MODE_PRIVATE);    	
    	devname = state.getString("bt_dev_addr", "00:00:00:00:00:00");
    	
		return devname;
	}
	
	/**
	 * 마지막으로 연결된 블루투스 주소를 저장한다.
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
