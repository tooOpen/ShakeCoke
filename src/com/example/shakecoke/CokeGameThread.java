package com.example.shakecoke;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
//���� ������ Ŭ����
public class CokeGameThread extends Thread {

	private SurfaceHolder m_surfaceHolder;
	private CokeGameView m_gameview;
	private boolean m_run = false; //�����带 �����ϴ� ����
	private Canvas _canvas;
	private  static Object mPauseLock; 
    private static boolean  bPausedT; //���� �Ͻ������� ���
    private  boolean  bPaused; //puase�� �Ͻ������� ȣ��Ǵ��� ��������� ȣ��Ǵ��� �����ϴ� ����
	public void setRunning(boolean run) {
		m_run = run;
	}
	public Boolean getRunning() {
		return m_run ;
	}
	public void setPause(boolean pause) {
		bPaused = pause;
	}
	public Boolean getPause() {
		return bPaused ;
	}

	public CokeGameThread(SurfaceHolder surfaceHolder, CokeGameView gameview) {

		mPauseLock = new Object();
	    bPaused = false;
	    bPausedT = false;
		m_surfaceHolder = surfaceHolder;
		m_gameview = gameview;
		_canvas = null;
	}

	@Override
	public void run() {

		while (m_run) { //m_run�� true�϶��� �����尡 ���ư���.
			synchronized (mPauseLock) {
				while(bPausedT) //�Ͻ����� ���°� �Ǹ� ��� wait�� ��Ų��.
				{
					  try {
	                        mPauseLock.wait();
	                    } catch (InterruptedException e) {
	                    }
				}
			}
			try {
				//�����忡�� ���ִ� ��, ������Ʈ, ������
				_canvas = m_surfaceHolder.lockCanvas(null); 
				synchronized (m_surfaceHolder) {
					m_gameview.Update();
					m_gameview.bitdraw(_canvas);
				}
			} finally {
				if (_canvas != null)
					m_surfaceHolder.unlockCanvasAndPost(_canvas);
				//ȭ�鿡 �׷��ֱ�
			}
		}
	}
	
    public static void onPause() {//�Ͻ������϶� ȣ���
        synchronized (mPauseLock) { 
        	bPausedT = true;
        }
    }

    public static void onResume() { //�ٽ� ��Ƽ��Ƽ�� Ȱ��ȭ �Ǿ��� ��
        synchronized (mPauseLock) {
        	bPausedT = false;
            mPauseLock.notifyAll();
        }
    }

}
