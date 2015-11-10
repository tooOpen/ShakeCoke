package graphicObjects;

import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Rect;
import appManager.AppManager;

import com.solmi.m1app.R;


public class CokeGameRising extends SpriteAnimation {

	private boolean upndown;//콜라방향
	private int limitbottom; //화면 height
	public CokeGameRising(int w,int h,int downp,Rect desr,int fps,int iframe,Options op)
	{
		super(AppManager.getInstance().getBitmap(R.drawable.risingani,op));
		this.initSprite(w, h, desr, fps, iframe);
		this.b_Rect.bottom = h;
		this.limitbottom = downp;
		this.upndown = false;
	}
	public void setDir(boolean d)//방향 정하기
	{
		this.upndown = d;
	}
	public boolean getDir()
	{
		return this.upndown;
	}
	public void setRestart()//게임 재시작 초기화
	{
		this.upndown= false;
		dest.top = limitbottom/6;
		dest.bottom = limitbottom+10;
	}
	@Override
	public void Update(long GameTime)
	{
		if(upndown) //상승이 다끝나고 내려갈때.
		{
			if(dest.top>=limitbottom+10)
			{
				upndown =! upndown;
			}
			dest.top += limitbottom/25;
			dest.bottom += limitbottom/25;
		}
		if(GameTime>b_frameTimer+b_fps) //계속 오르는 중일때
		{
			b_frameTimer = GameTime;//프레임 애니메이션
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

