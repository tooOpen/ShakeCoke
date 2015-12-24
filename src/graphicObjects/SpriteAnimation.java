package graphicObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class SpriteAnimation extends GraphicObject {
	
	protected int b_fps; //프레임 애니 fps
	protected int b_iFrames;//총 프레임 수 
	protected int b_CurrentFrame;//현재 프레임
	protected int b_SpriteWidth;//이미지 가로
	protected int b_SpriteHeight;//이미지 세로
	
	protected long b_frameTimer; //현재 시간
	
	
	public SpriteAnimation(Bitmap bitmap)
	{
		super(bitmap);	
	}
	public void initSprite(int w,int h,Rect desr ,int fps,int iframe)
	{
		this.b_SpriteWidth = w;
		this.b_SpriteHeight = h;
		this.b_fps = 1000/fps;
		this.b_iFrames = iframe;
		this.b_Rect.right = b_SpriteWidth;
		this.b_Rect.bottom = b_SpriteHeight;
		this.dest = desr;
	}
	
	@Override
	public void Draw(Canvas canvas) {
		canvas.drawBitmap(b_bitmap, b_Rect, dest,null);
	}
	
	public void Update(long GameTime)
	{
	}
}
