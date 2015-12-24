package com.example.shakecoke;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
//게임 스레드 클래스
public class CokeGameThread extends Thread {

	private SurfaceHolder m_surfaceHolder;
	private CokeGameView m_gameview;
	private boolean m_run = false; //스레드를 통제하는 변수
	private Canvas _canvas;
	private  static Object mPauseLock; 
    private static boolean  bPausedT; //게임 일시정지시 사용
    private  boolean  bPaused; //puase가 일시정지로 호출되는지 게임종료로 호출되는지 구분하는 변수
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

		while (m_run) { //m_run이 true일때만 스레드가 돌아간다.
			synchronized (mPauseLock) {
				while(bPausedT) //일시정지 상태가 되면 계속 wait를 시킨다.
				{
					  try {
	                        mPauseLock.wait();
	                    } catch (InterruptedException e) {
	                    }
				}
			}
			try {
				//스레드에서 해주는 일, 업데이트, 렌더링
				_canvas = m_surfaceHolder.lockCanvas(null); 
				synchronized (m_surfaceHolder) {
					m_gameview.Update();
					m_gameview.bitdraw(_canvas);
				}
			} finally {
				if (_canvas != null)
					m_surfaceHolder.unlockCanvasAndPost(_canvas);
				//화면에 그려주기
			}
		}
	}
	
    public static void onPause() {//일시정지일때 호출됨
        synchronized (mPauseLock) { 
        	bPausedT = true;
        }
    }

    public static void onResume() { //다시 액티비티가 활성화 되었을 때
        synchronized (mPauseLock) {
        	bPausedT = false;
            mPauseLock.notifyAll();
        }
    }

}
