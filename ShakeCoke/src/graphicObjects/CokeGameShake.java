package graphicObjects;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

public class CokeGameShake extends GraphicObject {
	private float mX, mY; //Ʈ��������Ʈ ��Ű�� �̹��� ����
	private float mDir; //���� ����
	private int dif; //���� ����
	public CokeGameShake(Bitmap bitmap,int top)
	{
		super(bitmap);
		this.mX = -100; 
		this.mY = top;
	}
	public void setDif(int dif)
	{
		this.dif = dif;
	}
	public int getDif()
	{
		return this.dif;
	}
	public void Draw(Canvas canvas) {
		Matrix m = new Matrix();
		m.setTranslate(mX, mY);
		m.postRotate(dif, mX , mY + b_bitmap.getHeight()/2); //ȸ����Ű��
		canvas.drawBitmap(b_bitmap, m,null);
	}
}
