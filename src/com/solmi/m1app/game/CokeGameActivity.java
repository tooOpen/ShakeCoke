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
	public static String g_DevName = "00:18:9A:21:7B:08"; //����̽� ���� ����
	private Timer btime; //����� ���â�� �߰� �ϱ� ���� Ÿ�̸�
	private boolean bflag;//����� ���â�� �߰� �ϱ� ���� ���º���
	
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
					SystemClock.sleep(1500); //�߰��� ���� ���� ������ �浹�� �߻��� 3�Լ� ��� ������� �ʱ� ������ �־���
					g_BluetoothService.clearData();
					g_BluetoothService.getAccelerometer();
				} catch (RemoteException e) {
		}
	}
	
	public static void connedevice() { 
		try {
			g_BluetoothService.BTconnect(g_DevName); //��� �����ϴ� �Լ� �信�� ȣ�� ��
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
				bms.obj = m1Data;//���ӵ��� �ֱ�
				bms.what = m1Data.TYPE;//Ÿ�� �ֱ�
				mHandler.sendMessage(bms);//�ڵ鷯�� ����
				}
				break;
				
				default:
					break;				
			}
		}
	};
	
	
	@Override
	protected void onResume() {

		if(cokegame.b_thread.getPause()) //���� �Ͻ������� ���¿��ٸ� resumeȣ��
		{
		cokegame.resumeS(); //�������� �ٽ� ���
		CokeGameThread.onResume();//������ �ٽý���
		cokegame.b_thread.setPause(false);//puase�� �Ͻ������� ȣ��Ǵ��� ��������� ȣ��Ǵ��� �����ϴ� �Լ�
		}
		super.onResume();
	}

	@Override
	protected void onPause() { //�Ͻ������� ȣ�� �Ǵ� �Լ�.
		
		cokegame.pauseS(); //�������� �Ͻ������� ���� �Լ�.
		CokeGameThread.onPause(); //������ ����
		cokegame.b_thread.setPause(true);//puase�� �Ͻ������� ȣ��Ǵ��� ��������� ȣ��Ǵ��� �����ϴ� �Լ�
	
		super.onPause();
	}

	private Handler mHandler = new Handler(){ //���ӵ� ���� �ް� ��� �޼����� ������ �ڵ鷯
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg); 
			cokegame.getmessage(msg); //���� �ڵ鷯�� �޼��� ����
		}
	};
	@Override
	public void onBackPressed() { //�ڷ� ���� ��ưŬ���� ���â �ٿ��
		if (!bflag) {
			Toast.makeText(CokeGameActivity.this, "really?", Toast.LENGTH_SHORT) //����佺Ʈ
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
			btime.schedule(second, 2000); //���� �ð��� ��
		} else {
			super.onBackPressed(); //�ð��ȿ� �ι� ������ �� �����ϱ�.
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
