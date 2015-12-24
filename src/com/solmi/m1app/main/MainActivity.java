package com.solmi.m1app.main;


import com.solmi.bluetoothservice.FindDeviceActivity;
import com.solmi.m1app.R;
import com.solmi.m1app.game.GameModeActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Context g_ctx;
	RelativeLayout g_connectingLayout;
	boolean g_connetingFlag = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		g_ctx = this;
		
		loadTypeface();		
		setContentView(R.layout.activity_main);
		
		//initHandler();
		//initBluetoothDevice();
		
		g_connectingLayout = (RelativeLayout)findViewById(R.id.ConnectingLayout);
		
		Button btn = (Button)findViewById(R.id.mainStartBtn);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),GameModeActivity.class);
				startActivity(intent);

			}
		});
	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		g_connetingFlag = false;
		g_connectingLayout.setVisibility(View.INVISIBLE);
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();        
	}

	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(item.getItemId()){
		case R.id.item_goGame:
			intent = new Intent(g_ctx,GameModeActivity.class);				
			g_ctx.startActivity(intent);
			break;
			default:
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	
	/**
	 * @brief 커스텀 폰트를 적용하기 위한 코드
	 */
	private static final String TYPEFACE_NAME = "NANUMGOTHICBOLD.TTF";
	//private static final String TYPEFACE_NAME = "YBLO05.TTF";
	private Typeface typeface = null;
	private void loadTypeface(){
		if(typeface == null){
			typeface = Typeface.createFromAsset(getAssets(), TYPEFACE_NAME);
		}
		
	}

	@Override
	public void setContentView(int viewId) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(g_ctx).inflate(viewId, null);
		ViewGroup group = (ViewGroup)view;
		int childCnt = group.getChildCount();
		for(int i=0; i<childCnt;i++){
			View v = group.getChildAt(i);
			if(v instanceof TextView){
				((TextView)v).setTypeface(typeface);
			}
		}
		super.setContentView(view);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(event.getKeyCode() == event.KEYCODE_BACK){
			if(g_connetingFlag){				
				return false;
			}
			else
				return super.onKeyDown(keyCode, event);
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	///////////////////////////////////////////////////////////////////////////////end 커스텀 폰트
	protected void showToast(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	

}
