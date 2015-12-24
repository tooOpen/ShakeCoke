package graphicObjects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Rect;
import appManager.AppManager;

import com.solmi.m1app.R;

public class CokeGameTimer extends SpriteAnimation {

	private int singledigit;//���ڸ��� 
	private int doubledigit;//���ڸ���
	private int value;//���� ��
	private int inordecrese;//�������� �������� �����ϴ� ���º���
	private int nGap;//���ڻ��� �Ÿ�
	private boolean whattime;//����ī��Ʈ �ٿ����� �غ�ȭ�� �������� �����ϴ� ���º���
	private Rect singleRect;
	private Bitmap urgentNum;//�ð��� �󸶳��� �ʾ����� �Ӱ� ���ϴ� ���� ����
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
		if(value<10) //���� 10���� ������ 
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
		if(whattime) //��������� �ð����� true: ���ӽð�. false:�غ� �ð�
		{
			if(value>5)//5���� ���� ������ ��  
			{
			canvas.drawBitmap(b_bitmap,b_Rect, dest, null);
			dest.left += nGap;
			dest.right += nGap; 
			canvas.drawBitmap(b_bitmap, singleRect, dest, null);
			dest.left -= nGap;
			dest.right -= nGap;
			}else//5���� ���� �������� �������ڷ� �ٲ� 
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
		else{//�غ� �ð��϶�
			canvas.drawBitmap(b_bitmap,b_Rect, dest, null);
			dest.left += nGap;
			dest.right += nGap; 
			canvas.drawBitmap(b_bitmap, singleRect, dest, null);
			dest.left -= nGap;
			dest.right -= nGap;
		}
		
	}
}
