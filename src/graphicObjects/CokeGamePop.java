package graphicObjects;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class CokeGamePop extends SpriteAnimation {

	private Bitmap[] popani; //���ĺ�Ʈ�� �迭
	private Bitmap howgood; //���� ǥ�� ��Ʈ��
	private int howgoodi; //����
	private int hwidth;
	private int hheight;
	private Rect howsrcRect;
	private Rect howdesRect;
	public CokeGamePop(Bitmap[] bitmap,Bitmap how,int w,int h,int hw,int hh,int botlimit,Rect desr,int fps,int iframe)
	{
		super(bitmap[0]);
		Random rand = new Random();
		int gap = rand.nextInt(5)+2;
		this.initSprite(w, h, desr, fps, iframe);
		this.howsrcRect = new Rect(0, 0, 0, 400);
		this.howdesRect = new Rect(dest.right/2-dest.right/gap, botlimit-botlimit/3, //���� ��Ʈ�� ����Ʈ���̼� ��Ʈ
				(dest.right/2-dest.right/gap)+dest.right-dest.right/5, botlimit);
		this.hwidth = hw;
		this.hheight = hh;
		this.popani = bitmap;
		this.howgood = how;
		this.b_Rect.right = w;
		this.b_Rect.bottom = h;
	}
	public void setRestart()
	{
		this.b_CurrentFrame = 0;
	}
	public void setcurframe(int cf) {
		this.b_CurrentFrame = cf;
	}
	public int getcurframe() {
		return this.b_CurrentFrame;
	}
	public void sethowgood(int h)
	{
		this.howgoodi = h;
		howsrcRect.left = hwidth*howgoodi;
		howsrcRect.right = howsrcRect.left+hwidth;
	}
	public int gethowgood()
	{
		return howgoodi;
	}
	@Override
	public void Draw(Canvas canvas) {
		canvas.drawBitmap(popani[b_CurrentFrame], b_Rect, dest,null);
		if(b_CurrentFrame>1)//�ִϸ��̼��� ������
		canvas.drawBitmap(howgood, howsrcRect, howdesRect,null);//�����׸���
	}
	@Override
	public void Update(long GameTime)
	{
		if(GameTime>b_frameTimer+b_fps)
		{
			b_frameTimer = GameTime;
			b_CurrentFrame += 1;
		}
	}
	@Override
	public void bitmaprecycle() {
		for(int i=0;i<b_iFrames;i++)
		{
			popani[i].recycle();
			popani[i] = null;
		}
		super.bitmaprecycle();
	}
}
