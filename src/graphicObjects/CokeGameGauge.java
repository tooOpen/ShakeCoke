package graphicObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class CokeGameGauge extends GraphicObject {

	private int gaugefill; //������ ���� ����
	private Bitmap bottle; //�� �̹���
	private Bitmap fgauge; //����ִ� ������
	private Bitmap egaugeB;//����ִ°����� ���
	private int b_GaugeWidth;
	private int b_GaugeHeight;
	private Rect fgsrcRect; //���� ������ �ҽ���Ʈ
	private Rect fgdesRect;//���� ������ ����Ƽ���̼� ��Ʈ
	private Rect egdesRect;//����ִ� ������ �ҽ���Ʈ
	private Rect egbdesRect;//����ִ� ������ ����Ƽ���̼� ��Ʈ
	
	public CokeGameGauge(Bitmap p_eg, Bitmap p_bot, Bitmap p_fg,Bitmap p_egbot )
	{
		super(p_eg);//���ִ� ������ 
		this.bottle = p_bot;
		this.fgauge = p_fg;
		this.egaugeB = p_egbot;
		this.gaugefill = 0;
	}
	public void setRestart() //���� ����۽ÿ� ȣ��Ǵ� �Լ�
	{
		//������ �ʱ�ȭ.
		this.gaugefill = 0;
		this.fgsrcRect = new Rect(0,0,0,b_GaugeHeight);
		this.fgdesRect = new Rect(dest.right/6+dest.left, dest.bottom-dest.bottom/6, dest.right/6+dest.left, dest.bottom-dest.bottom/6+b_GaugeHeight);
	}
	public void initSprite(int w,int h,Rect desr)
	{
		this.b_GaugeWidth = w;//�׸�ũ��
		this.b_GaugeHeight =h;//�׸�ũ��
		this.b_Rect.right = b_GaugeWidth;
		this.b_Rect.bottom = b_GaugeHeight;
		this.dest = desr;
		this.fgsrcRect = new Rect(0,0,0,b_GaugeHeight);
		this.fgdesRect = new Rect(dest.right/6+dest.left, dest.bottom-dest.bottom/6, dest.right/6+dest.left, dest.bottom-dest.bottom/6+b_GaugeHeight);
		this.egdesRect = new Rect(dest.right/6+dest.left, dest.bottom-dest.bottom/6, (dest.right/6+dest.left)+b_GaugeWidth, dest.bottom-dest.bottom/6+b_GaugeHeight);
		this.egbdesRect = new Rect(dest.right/6+dest.left, dest.bottom-dest.bottom/6, (dest.right/6+dest.left)+b_GaugeWidth, dest.bottom-dest.bottom/6+b_GaugeHeight);
	}
	public void setgauge(int p_g) //������ ���� �޴� �Լ�
	{
		this.gaugefill = p_g;
	}
	public int getgauge()
	{
		return this.gaugefill;
	}
	public void Update()
	{
		this.fgdesRect.right = (dest.right/6+dest.left) +gaugefill; //gaugefill ��ŭ ������ �̹����� �׸���.
		this.fgsrcRect.right = gaugefill;
	}
	@Override
	public void Draw(Canvas canvas) {
		canvas.drawBitmap(egaugeB, b_Rect, egbdesRect, null);
		canvas.drawBitmap(fgauge, fgsrcRect,fgdesRect, null);
		canvas.drawBitmap(b_bitmap, b_Rect,egdesRect, null);
		
		canvas.drawBitmap(bottle,dest.left, dest.bottom-dest.bottom/8,null);
	}
	@Override
	public void bitmaprecycle() { //��Ʈ�� �޸� ����
		bottle.recycle();
		bottle = null;
		fgauge.recycle();
		fgauge = null;
		super.bitmaprecycle();
	}
	
}
