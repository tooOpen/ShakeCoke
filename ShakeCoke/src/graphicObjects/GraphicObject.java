package graphicObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class GraphicObject {
	protected Bitmap b_bitmap; //비트맵 변수
	protected Rect dest; 
	protected Rect b_Rect;
	public GraphicObject(Bitmap bitmap)
	{
		this.b_bitmap = bitmap;
		this.dest = new Rect(0,0,0,0);
		this.b_Rect = new Rect(0, 0, 0, 0);
	}
	public void Draw(Canvas canvas) //렌더링 함수.
	{
		canvas.drawBitmap(b_bitmap, dest.left, dest.top, null);
	}
	public void bitmaprecycle() //비트맵 메모리 해제
	{
		b_bitmap.recycle();
		b_bitmap =null;
	}
	public boolean isnull() // nullpointexception을 막기위한 함수.
	{
		if(b_bitmap==null)
			return true;
		else
			return false;
	}
}
