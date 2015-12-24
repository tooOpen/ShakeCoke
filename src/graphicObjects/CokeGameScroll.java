package graphicObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class CokeGameScroll extends GraphicObject {

	private Bitmap cloud; //����
	protected int b_y2;//��ũ�ѵǴ� ��ǥ
	protected int b_cy2;//�׸��߽���ǥ
	protected int spriteW;
	protected int spriteH;
	protected int dir;//����
	protected Rect skyR; //���� �ҽ� ��Ʈ ���ݾ� �÷��ָ� ��ũ���Ѵ�.

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
		 b_y2 += dir; //���� ���� ��ũ�ѽ�Ű�� ��Ʈ�� �ҽ� ��ǥ �ٲٱ�
			
			if(b_y2<0) //0���� �۾�����
			{
				b_y2 =b_cy2; //�߽����� �̵��Ͽ� ��� ����.
			}
			//������ �������� �׸� �ݸ�ŭ ȭ�鿡 �׷���.
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
