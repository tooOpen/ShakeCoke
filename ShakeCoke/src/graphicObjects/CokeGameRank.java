package graphicObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class CokeGameRank extends GraphicObject {

	private Bitmap rebtn;//재시작 버튼 비트맵
	private Bitmap finibtn;//끝내기 버튼 비트맵
	private int whatButon;//어느 버튼이 눌렸는가 판별하는 변수

	private Rect finsrcRect;
	private Rect resrcRect;
	
	private Rect findesRedct;
	private Rect redesRect;
	public CokeGameRank(Bitmap rank,Bitmap fin,Bitmap regi)
	{
		super(rank);
		this.finibtn = fin;
		this.rebtn = regi;

		this.whatButon = 0;
		this.finsrcRect = new Rect(0,0,330,110);
		this.resrcRect = new Rect(0,0,330,110);

		this.findesRedct = new Rect(0,0,0,0);
		this.redesRect = new Rect(0,0,0,0);

	}
	public void setRestart()
	{
		this.whatButon = 0;
	}
	public void initSprite(int w,int h,Rect desr)
	{
		this.dest = desr;
		this.b_Rect.right = w;
		this.b_Rect.bottom = h;
		
		this.findesRedct.left =dest.right/15;
		this.findesRedct.right = dest.right/2;
		this.findesRedct.top = dest.bottom-dest.bottom/5;
		this.findesRedct.bottom = dest.bottom-dest.bottom/10;
		
		this.redesRect.left = dest.right-dest.right/2;
		this.redesRect.right = dest.right-dest.right/15;
		this.redesRect.top = findesRedct.top;
		this.redesRect.bottom = findesRedct.bottom;
		
	}
	public Rect getfinbtnRect()
	{
		return this.findesRedct;
	}
	public Rect getrebtnRect()
	{
		return this.redesRect;
	}

	public void setwhatbtnon(int wb)
	{
		this.whatButon = wb;
	}
	@Override
	public void Draw(Canvas canvas)
	{
		if(whatButon==1) //재시작 버튼일 경우
		{
			resrcRect.left = 330;
			resrcRect.right = 660;
		}
		else if(whatButon==2) //끝내기 버튼일 경우
		{
			finsrcRect.left = 330;
			finsrcRect.right = 660;
		}
		else if(whatButon == 0) //아무것도 눌리지 않았을 경우
		{
			resrcRect.left = 0;
			resrcRect.right = 330;
			finsrcRect.left = 0;
			finsrcRect.right = 330;
	
		}
		canvas.drawBitmap(b_bitmap, b_Rect, dest, null);
		canvas.drawBitmap(finibtn, finsrcRect, findesRedct, null);
		canvas.drawBitmap(rebtn, resrcRect, redesRect, null);

	}
	@Override
	public void bitmaprecycle() {
		finibtn.recycle();
		finibtn = null;
		rebtn.recycle();
		rebtn = null;
		super.bitmaprecycle();
	}
	
}
