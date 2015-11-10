package graphicObjects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Rect;
import appManager.AppManager;

import com.solmi.m1app.R;

public class CokeGameTimer extends SpriteAnimation {

	private int singledigit;//한자리수 
	private int doubledigit;//두자리수
	private int value;//시작 값
	private int inordecrese;//증가인지 감소인지 구분하는 상태변수
	private int nGap;//숫자사이 거리
	private boolean whattime;//게임카운트 다운인지 준비화면 숫자인지 구분하는 상태변수
	private Rect singleRect;
	private Bitmap urgentNum;//시간이 얼마남지 않았을때 붉게 변하는 강조 숫자
	public CokeGameTimer(int w,int h,Rect desr,int v,int dir,boolean what,int gap,Options op)
	{
		super(AppManager.getInstance().getBitmap(R.drawable.time,op));
		this.initSprite(w, h, desr, 1, 0);
		this.urgentNum = AppManager.getInstance().getBitmap(R.drawable.urgent, op);
		this.value =v;
		this.nGap = gap;
		this.whattime = what;
		this.inordecrese = dir;
		this.singledigit = 0 ;
		this.doubledigit = 0;
		this.singleRect = new Rect(0, 0, 0, 0);
		this.b_Rect.bottom = h;
		this.singleRect.bottom = h;
		
		
		this.singledigit = value%10;
		this.doubledigit = value/10;
		this.b_Rect.left =b_SpriteWidth*doubledigit;
		this.b_Rect.right =b_SpriteWidth*(doubledigit+1);
		this.singleRect.left = b_SpriteWidth*singledigit;
		this.singleRect.right =b_SpriteWidth*(singledigit+1);
	}
	public void setValue(int v)
	{
		this.value = v;
	}
	public int getValue()
	{
		return this.value;
	}
	public void setRestart()
	{
		value = 20;
		dest.bottom = nGap;
		this.singledigit = value%10;
		this.doubledigit = value/10;
		this.b_Rect.left =b_SpriteWidth*doubledigit;
		this.b_Rect.right =b_SpriteWidth*(doubledigit+1);
		this.singleRect.left = b_SpriteWidth*singledigit;
		this.singleRect.right =b_SpriteWidth*(singledigit+1);
	}
	@Override
	public void Update(long GameTime)
	{
		if(GameTime>b_frameTimer+b_fps)
		{
			b_frameTimer = GameTime;
			value+=inordecrese;
		}
		if(value<10) //값이 10보다 작을때 
		{
			singledigit = value;
			doubledigit = 0;		
		}
		else
		{
			singledigit = value%10;
			doubledigit = value/10;
		}
		b_Rect.left =b_SpriteWidth*doubledigit;
		b_Rect.right =b_SpriteWidth*(doubledigit+1);
		singleRect.left = b_SpriteWidth*singledigit;
		singleRect.right =b_SpriteWidth*(singledigit+1);
	}
	@Override
	public void Draw(Canvas canvas) {
		if(whattime) //어느종류의 시간인지 true: 게임시간. false:준비 시간
		{
			if(value>5)//5보다 많이 남았을 때  
			{
			canvas.drawBitmap(b_bitmap,b_Rect, dest, null);
			dest.left += nGap;
			dest.right += nGap; 
			canvas.drawBitmap(b_bitmap, singleRect, dest, null);
			dest.left -= nGap;
			dest.right -= nGap;
			}else//5보다 적게 남았을때 강조숫자로 바뀜 
			{
				dest.bottom = nGap+nGap/2;
				dest.left -=nGap/2;
				canvas.drawBitmap(urgentNum,b_Rect, dest, null);
				dest.left += nGap+nGap/2;
				dest.right += nGap+nGap/2; 
				canvas.drawBitmap(urgentNum, singleRect, dest, null);
				dest.left -= nGap;
				dest.right -= nGap+nGap/2;
			}
		}
		else{//준비 시간일때
			canvas.drawBitmap(b_bitmap,b_Rect, dest, null);
			dest.left += nGap;
			dest.right += nGap; 
			canvas.drawBitmap(b_bitmap, singleRect, dest, null);
			dest.left -= nGap;
			dest.right -= nGap;
		}
		
	}
}
