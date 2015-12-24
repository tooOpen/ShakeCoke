package com.solmi.m1app.game;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.impl.conn.tsccm.WaitingThread;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.view.Menu;
import android.widget.Toast;

import com.example.shakecoke.CokeGameThread;
import com.example.shakecoke.CokeGameView;
import com.solmi.bluetoothservice.BluetoothService;
import com.solmi.bluetoothservice.FindDeviceActivity;
import com.solmi.bluetoothservice.M1DATA;
import com.solmi.m1app.R;


public class CokeGameActivity extends Activity {
	public static String g_DevName = "00:18:9A:21:7B:08"; //디바이스 정보 변수
	private Timer btime; //종료시 경고창이 뜨게 하기 위한 타이머
	private boolean bflag;//종료시 경고창이 뜨게 하기 위한 상태변수
	
	private  CokeGameView cokegame;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cokegame = new CokeGameView(this);
		
		setContentView(cokegame);
		bindService(); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.coke_game, menu);
		return true;
	}
	
	public static void getACC() { 
				try {
					g_BluetoothService.setRunningTime(BluetoothService.RT_CONTINUE);
					SystemClock.sleep(1500); //중간에 텀을 주지 않으면 충돌이 발생해 3함수 모두 실행되지 않기 때문에 넣었음
					g_BluetoothService.clearData();
					g_BluetoothService.getAccelerometer();
				} catch (RemoteException e) {
		}
	}
	
	public static void connedevice() { 
		try {
			g_BluetoothService.BTconnect(g_DevName); //기기 연결하는 함수 뷰에서 호출 함
		} catch (RemoteException e) {
			e.printStackTrace();
		}
}
	
	protected void connectBT() {		
		Intent intent = new Intent(getApplicationContext(),FindDeviceActivity.class);
		startActivityForResult(intent,FindDeviceActivity.SET_DEVICE_ADDRESS);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode ==  FindDeviceActivity.SET_DEVICE_ADDRESS){
			if (resultCode == FindDeviceActivity.RESULT_OK) {
				
				bindService();				
				g_DevName = data.getExtras().getString(FindDeviceActivity.EXTRA_DEVICE_ADDRESS);
				
				try {
					g_BluetoothService.BTconnect(g_DevName);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}	
		}
	}
	
	public static BluetoothService g_BluetoothService;
	protected  ServiceConnection g_BluetoothServiceConn = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			
			g_BluetoothService = ((BluetoothService.BluetoothServiceBinder)service).getService();
			g_BluetoothService.registerCallback(mBTCallback); 
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			g_BluetoothService = null;
		}
	};
	
	
	protected void bindService()
	{
		Intent intent = new Intent(this, BluetoothService.class);		
		bindService(intent,g_BluetoothServiceConn,BIND_AUTO_CREATE+BIND_DEBUG_UNBIND);
	}
	
	protected BluetoothService.BTCallback mBTCallback = new BluetoothService.BTCallback(){
		@Override
		public void BTConnected() {
			// TODO Auto-generated method stub
		
		}

		@Override
		public void ConnectionFail() {
			// TODO Auto-generated method stub
			
			
		}

		@Override
		public void rcvedData(M1DATA m1Data) {
		
			// TODO Auto-generated method stub
			switch(m1Data.TYPE){
			case M1DATA.MSG_ACC:
				if(cokegame.whatstate()==1)
				{
				Message bms = mHandler.obtainMessage();
				bms.obj = m1Data;//가속도값 넣기
				bms.what = m1Data.TYPE;//타입 넣기
				mHandler.sendMessage(bms);//핸들러로 전송
				}
				break;
				
				default:
					break;				
			}
		}
	};
	
	
	@Override
	protected void onResume() {

		if(cokegame.b_thread.getPause()) //앱이 일시정지된 상태였다면 resume호출
		{
		cokegame.resumeS(); //음악파일 다시 재생
		CokeGameThread.onResume();//스레드 다시시작
		cokegame.b_thread.setPause(false);//puase가 일시정지로 호출되는지 게임종료로 호출되는지 구분하는 함수
		}
		super.onResume();
	}

	@Override
	protected void onPause() { //일시정지시 호출 되는 함수.
		
		cokegame.pauseS(); //음악파일 일시정지를 위한 함수.
		CokeGameThread.onPause(); //스레드 정지
		cokegame.b_thread.setPause(true);//puase가 일시정지로 호출되는지 게임종료로 호출되는지 구분하는 함수
	
		super.onPause();
	}

	private Handler mHandler = new Handler(){ //가속도 값을 받고 뷰로 메세지를 보내는 핸들러
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg); 
			cokegame.getmessage(msg); //뷰의 핸들러로 메세지 전송
		}
	};
	@Override
	public void onBackPressed() { //뒤로 가기 버튼클릭시 경고창 뛰우기
		if (!bflag) {
			Toast.makeText(CokeGameActivity.this, "really?", Toast.LENGTH_SHORT) //경고토스트
					.show();
			bflag = true;
			TimerTask second = new TimerTask() {
				@Override
				public void run() {
					btime.cancel();
					btime = null;
					bflag = false;
				}
			};
			if (btime != null) {
				btime.cancel();
				btime = null;
			}
			btime = new Timer();
			btime.schedule(second, 2000); //초의 시간을 줌
		} else {
			super.onBackPressed(); //시간안에 두번 눌렀을 때 종료하기.
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(g_BluetoothServiceConn != null){		       
			unbindService(g_BluetoothServiceConn);
			g_BluetoothServiceConn = null;
		}
	}
}
