package graphicObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class CokeGameScroll extends GraphicObject {

	private Bitmap cloud; //구름
	protected int b_y2;//스크롤되는 좌표
	protected int b_cy2;//그림중심좌표
	protected int spriteW;
	protected int spriteH;
	protected int dir;//방향
	protected Rect skyR; //구름 소스 렉트 조금씩 올려주며 스크롤한다.

	public CokeGameScroll(Bitmap p_sky )
	{
		super(p_sky);
	}
	public void initSprite(int w,int h,Rect desr,int d,Bitmap p_cloud)
	{
		this.dest = desr;
		this.spriteW = w;
		this.spriteH = h;
		this.skyR = new Rect(0, 0, spriteW, spriteH);
		this.b_cy2 = h/2;
		this.b_y2 = b_cy2;
		this.dir = d;
		this.cloud = p_cloud;
		b_Rect.right = spriteW;
	}
	public void Update()
	{                              
		 b_y2 += dir; //구름 위로 스크롤시키기 비트맵 소스 좌표 바꾸기
			
			if(b_y2<0) //0보다 작아지면
			{
				b_y2 =b_cy2; //중심으로 이동하여 계속 증가.
			}
			//증가한 범위부터 그림 반만큼 화면에 그려줌.
			b_Rect.top = b_y2; 
			b_Rect.bottom = b_Rect.top+b_cy2;
	}
         @Override
     	public void Draw(Canvas canvas) {
    		canvas.drawBitmap(b_bitmap,skyR, dest,null);
     		canvas.drawBitmap(cloud, b_Rect, dest,null);	
     	}
		@Override
		public void bitmaprecycle() {
			cloud.recycle();
			cloud = null;
			super.bitmaprecycle();
		}
}
