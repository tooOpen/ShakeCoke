package com.solmi.m1app.game;

import com.solmi.bluetoothservice.BluetoothService;
import com.solmi.bluetoothservice.FindDeviceActivity;
import com.solmi.bluetoothservice.M1DATA;
import com.solmi.m1app.R;
import com.solmi.m1app.custom.MultiGraphDashBoard;
import com.solmi.m1app.custom.ResultGraphDashBoard;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity implements OnClickListener{
	
	
	String g_DevName = "00:00:00:00:00:00";
	ResultGraphDashBoard gGraph0;
	MultiGraphDashBoard gGraph1;
	String gMsg = "";
	SoundPool gSoundpool = null;
	int sp1,sp2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		bindService();
		
		Button btn = (Button)findViewById(R.id.btnBTCtest);
		btn.setOnClickListener(this);
		btn = (Button)findViewById(R.id.btnRSteptest);
		btn.setOnClickListener(this);
		btn = (Button)findViewById(R.id.btnHRSTtest);
		btn.setOnClickListener(this);
		btn = (Button)findViewById(R.id.btnAccTest);
		btn.setOnClickListener(this);
		btn = (Button)findViewById(R.id.btnStop);
		btn.setOnClickListener(this);
		btn = (Button)findViewById(R.id.btnSetRun20);
		btn.setOnClickListener(this);
		btn = (Button)findViewById(R.id.btnSetRunContinue);
		btn.setOnClickListener(this);

		gGraph0 = (ResultGraphDashBoard)findViewById(R.id.rgdb_0);
		gGraph0.setRangeMax(800, 1024);
		gGraph1 = (MultiGraphDashBoard)findViewById(R.id.rgdb_1);
		
		gGraph1.addSeries(getResources().getColor(R.color.opaque_shadow));
		gGraph1.addSeries(getResources().getColor(R.color.orange));
		gGraph1.addSeries(getResources().getColor(R.color.mint));
		gGraph1.setRangeMax(512, 1024);
		
		
		gSoundpool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		sp1 = gSoundpool.load(this, R.raw.wood1,1);
		sp2 = gSoundpool.load(this, R.raw.wood2,1);	
		
	}
	
	@Override
	public void onClick(View v) {
		try{
			switch(v.getId()){
			case R.id.btnBTCtest:
				gSoundpool.play(sp2, 1, 1, 0, 0, 1);
				connectBT();
				break;
			case R.id.btnStop:
				gSoundpool.play(sp1, 1, 1, 0, 0, 1);
				g_BluetoothService.stopDeviceMode();
				break;
			case R.id.btnRSteptest:
				gSoundpool.play(sp2, 1, 1, 0, 0, 1);
				gMsg = "-";
				updateText();
				g_BluetoothService.clearData();
				g_BluetoothService.getStep();
				break;
			case R.id.btnHRSTtest:
				gSoundpool.play(sp1, 1, 1, 0, 0, 1);
				gMsg = "-";
				updateText();

				g_BluetoothService.clearData();
				g_BluetoothService.getHrStress();
				
				gGraph0.resetData();
				
				gGraph0.setVisibility(View.VISIBLE);
				gGraph1.setVisibility(View.INVISIBLE);
				gGraph0.drawStart();
				break;				
			case R.id.btnAccTest:
				gSoundpool.play(sp1, 1, 1, 0, 0, 1);
				gMsg = "-";
				updateText();
				g_BluetoothService.clearData();
				g_BluetoothService.getAccelerometer();
				
				gGraph1.resetData();
				
				gGraph0.setVisibility(View.INVISIBLE);
				gGraph1.setVisibility(View.VISIBLE);
				
				gGraph1.drawStart();			
				break;		
			case R.id.btnSetRun20:
				gSoundpool.play(sp2, 1, 1, 0, 0, 1);
				gMsg = "-";
				updateText();
				g_BluetoothService.setRunningTime(BluetoothService.RT_20SEC);
				break;
			case R.id.btnSetRunContinue:
				gSoundpool.play(sp1, 1, 1, 0, 0, 1);
				gMsg = "-";
				updateText();
				g_BluetoothService.setRunningTime(BluetoothService.RT_CONTINUE);
				break;
			}
		}catch(RemoteException e){
			
		}		
	}

	/***************************** 블루투스 장치와 연결 ******************************************/
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
					g_BluetoothService.BTconnect(g_DevName);//블루투스연결
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
			}	
		}
	}
	
	/***************************** 블루투스 서비스를 등록 *****************************************/
	protected void bindService()
	{
		Intent intent = new Intent(this, BluetoothService.class);		
		bindService(intent,g_BluetoothServiceConn,BIND_AUTO_CREATE+BIND_DEBUG_UNBIND);
	}

	/***************** SHC-M1 연동을 위한 서비스 및 컨트롤러 등록 ********************************/
	protected BluetoothService g_BluetoothService;
	protected ServiceConnection g_BluetoothServiceConn = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			g_BluetoothService = ((BluetoothService.BluetoothServiceBinder)service).getService();			
//			g_BluetoothService.setTargetHandler(mHandler);   //이 코드는 동작안함.			
			g_BluetoothService.registerCallback(mBTCallback); //  핸들러가 아닌 콜백 객체를 등록하고

		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			g_BluetoothService = null;
		}
	};

	/********************** 수신 데이터 처리 ***********************************************/	
	protected BluetoothService.BTCallback mBTCallback = new BluetoothService.BTCallback(){
	/** 이 안에서는 UI 스레드에 접근할수 없음**/
		@Override
		public void BTConnected() {
			// TODO Auto-generated method stub
			// 블루투스가 연결되었을때 호출됨
		}

		@Override
		public void ConnectionFail() {
			// TODO Auto-generated method stub
			// 블루투스 연결이 실패했을때 호출됨
			
		}
		@Override
		public void rcvedData(M1DATA m1Data) {
			//데이터가 수신될때마다 호출됨
			// TODO Auto-generated method stub
			switch(m1Data.TYPE){
			case M1DATA.MSG_HRSTRESS:
				gMsg = String.format("HR = %d, STRESS = %.1f", m1Data.HR, m1Data.STRESS);
				mHandler.sendEmptyMessage(m1Data.TYPE);
				break;
			case M1DATA.MSG_STEP:
				gMsg = String.format("STEP = %d", m1Data.STEP);
				mHandler.sendEmptyMessage(m1Data.TYPE);
				break;
			case M1DATA.MSG_ACC:
				
				
				gECG = (m1Data.ECG);				
				gGraph0.addValue((double)gECG);	
				
				gAccX = m1Data.ACC_X;
				gAccY = m1Data.ACC_Y;
				gAccZ = m1Data.ACC_Z;
				gGraph1.addValue(0,gAccX);
				gGraph1.addValue(1,gAccY);
				gGraph1.addValue(2,gAccZ);
				break;
				
				default:
					break;				
			}
		}
		
	};
	
	int gECG, gAccX, gAccY, gAccZ;
	
	/********************** UI스레드 업데이트 핸들러 정의 ************************************/
	protected Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			switch(msg.what){
			case M1DATA.MSG_HRSTRESS:
				updateText();
				break;
			case M1DATA.MSG_STEP:
				updateText();
				break;
			case M1DATA.MSG_ACC:
				
				gGraph0.addValue(gECG);				
				gGraph1.addValue(gAccX);
				gGraph1.setCurrentValue(gAccX);
				break;
				
				default:
					break;
				
			}
		}
	};
	
	/******************************** ETC *******************************************************/
	protected void showToast(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
	public void updateText()
	{
		TextView tv = (TextView)findViewById(R.id.tvTestLogger);
		tv.setText(gMsg);
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
