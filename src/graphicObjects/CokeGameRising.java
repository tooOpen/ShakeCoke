package graphicObjects;

import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Rect;
import appManager.AppManager;

import com.solmi.m1app.R;


public class CokeGameRising extends SpriteAnimation {

	private boolean upndown;//�ݶ����
	private int limitbottom; //ȭ�� height
	public CokeGameRising(int w,int h,int downp,Rect desr,int fps,int iframe,Options op)
	{
		super(AppManager.getInstance().getBitmap(R.drawable.risingani,op));
		this.initSprite(w, h, desr, fps, iframe);
		this.b_Rect.bottom = h;
		this.limitbottom = downp;
		this.upndown = false;
	}
	public void setDir(boolean d)//���� ���ϱ�
	{
		this.upndown = d;
	}
	public boolean getDir()
	{
		return this.upndown;
	}
	public void setRestart()//���� ����� �ʱ�ȭ
	{
		this.upndown= false;
		dest.top = limitbottom/6;
		dest.bottom = limitbottom+10;
	}
	@Override
	public void Update(long GameTime)
	{
		if(upndown) //����� �ٳ����� ��������.
		{
			if(dest.top>=limitbottom+10)
			{
				upndown =! upndown;
			}
			dest.top += limitbottom/25;
			dest.bottom += limitbottom/25;
		}
		if(GameTime>b_frameTimer+b_fps) //��� ������ ���϶�
		{
			b_frameTimer = GameTime;//������ �ִϸ��̼�
			b_CurrentFrame += 1;
			if(b_CurrentFrame>=b_iFrames)
			{
				b_CurrentFrame = 0;
			}
			b_Rect.left = b_CurrentFrame*b_SpriteWidth;
			b_Rect.right = b_Rect.left+b_SpriteWidth;
		}
	}
	@Override
	public void Draw(Canvas canvas) {
		canvas.drawBitmap(b_bitmap,b_Rect, dest, null);
	}
}

