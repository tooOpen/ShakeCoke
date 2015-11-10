package graphicObjects;

import java.util.Random;

import android.graphics.BitmapFactory.Options;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import appManager.AppManager;

import com.solmi.m1app.R;




public class CokeGameHeight extends SpriteAnimation {

	private int singledigit;//���ڸ���
	private int doubledigit;//���ڸ���
	private int tripledigit;//���ڸ���
	private int value;//��
	private int limit;//�Ѱ�
	private Bitmap meter;//���� ��Ʈ��
	private Rect doubleRect;
	private Rect tripleRect;
	private Random rand;
	public CokeGameHeight(Bitmap m,int w,int h,Rect desr,Options op)
	{
		super(AppManager.getInstance().getBitmap(R.drawable.height,op));
		this.initSprite(w, h, desr, 100, 10);
		this.singledigit = 0;
		this.doubledigit = 0;
		this.tripledigit = 0;
		this.value = 0;
		this.meter = m;
		this.rand = new Random();
		this.doubleRect = new Rect(0, 0, 0, 0);
		this.tripleRect = new Rect(0, 0, 0, 0);
		this.b_Rect.bottom = h;
		this.doubleRect.bottom = h;
		this.tripleRect.bottom = h;
	}
	public int getValue()
	{
		return this.value;
	}
	public void setRestart()
	{
		this.singledigit = 0;
		this.doubledigit = 0;
		this.tripledigit = 0;
		this.value = 0;
	}
	public void setLimit(int l)//���̰� ������ ��ŭ �����ϰ� ��
	{
		this.limit = l;
	}
	public int getLimit()
	{
		return this.limit;
	}
	@Override
	public void Update(long GameTime) {
		if (GameTime > b_frameTimer+b_fps) {
			b_frameTimer = GameTime;
			if(value<300) //300������ 1~10�� ���������� �����ϴ�
				value += rand.nextInt(10)+1;
			else
			value +=rand.nextInt(5)+1;//�� �̻� ���ʹ� õõ�� ������
		}
		if (value < 100) { 
			if (value < 10) {//���ڸ��϶�
				singledigit = value;
				doubledigit = 0;
				tripledigit = 0;
			} else {        //���ڸ��϶�
				singledigit = value % 10;
				doubledigit = value / 10;
				tripledigit = 0;
			}
		} else {
			singledigit = value % 10;
			tripledigit = value / 100;
			doubledigit = (value-tripledigit*100)/ 10;
		
		}
		b_Rect.left =b_SpriteWidth*singledigit;//���ڸ��϶�
		b_Rect.right =b_SpriteWidth*(singledigit+1);
		doubleRect.left = b_SpriteWidth*doubledigit;
		doubleRect.right =b_SpriteWidth*(doubledigit+1);
		tripleRect.left = b_SpriteWidth*tripledigit;
		tripleRect.right =b_SpriteWidth*(tripledigit+1);
	}
	@Override
	public void Draw(Canvas canvas) {
		canvas.drawBitmap(b_bitmap, tripleRect, dest, null);
		dest.left += b_SpriteWidth;
		dest.right += b_SpriteWidth;
		canvas.drawBitmap(b_bitmap, doubleRect, dest, null);
		dest.left += b_SpriteWidth;
		dest.right += b_SpriteWidth;
		canvas.drawBitmap(b_bitmap,b_Rect, dest, null);
		dest.left += 150;
		dest.right += 100;
		canvas.drawBitmap(meter,dest.left,dest.bottom-88, null);
		dest.left -= 2*b_SpriteWidth+150;
		dest.right -= 2*b_SpriteWidth+100; 
	
		
	}
}

