package appManager;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import com.example.shakecoke.CokeGameView;

//������ ���ҽ��� ����.
public class AppManager {

	private CokeGameView m_gamev;
	private Resources m_res;
	private static AppManager s_instance;
	
	public Bitmap getBitmap(int r,Options op){ //��Ʈ�� ���� ������ �Լ�
		return BitmapFactory.decodeResource(m_res,r,op);
	}
	// ��� ���ҽ� ����
	public void setgameView(CokeGameView gv)
	{
		this.m_gamev = gv;
	}
	public void setResource(Resources rs)
	{
		this.m_res = rs;
	}
	public CokeGameView getgameView()
	{
		return this.m_gamev;
	}
	public Resources getResource()
	{
		return this.m_res;
	}
	
	public static AppManager getInstance(){ //��ü �̱��� 
		if(s_instance == null){
			s_instance = new AppManager();
		}
		return s_instance;
	}
}
