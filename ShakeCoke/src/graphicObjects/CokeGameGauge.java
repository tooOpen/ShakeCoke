package graphicObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class CokeGameGauge extends GraphicObject {

	private int gaugefill; //게이지 차는 정도
	private Bitmap bottle; //병 이미지
	private Bitmap fgauge; //비어있는 게이지
	private Bitmap egaugeB;//비어있는게이지 배경
	private int b_GaugeWidth;
	private int b_GaugeHeight;
	private Rect fgsrcRect; //차는 게이지 소스렉트
	private Rect fgdesRect;//차는 게이지 데스티네이션 렉트
	private Rect egdesRect;//비어있는 게이지 소스렉트
	private Rect egbdesRect;//비어있는 게이지 데스티네이션 렉트
	
	public CokeGameGauge(Bitmap p_eg, Bitmap p_bot, Bitmap p_fg,Bitmap p_egbot )
	{
		super(p_eg);//차있는 게이지 
		this.bottle = p_bot;
		this.fgauge = p_fg;
		this.egaugeB = p_egbot;
		this.gaugefill = 0;
	}
	public void setRestart() //게임 재시작시에 호출되는 함수
	{
		//게이지 초기화.
		this.gaugefill = 0;
		this.fgsrcRect = new Rect(0,0,0,b_GaugeHeight);
		this.fgdesRect = new Rect(dest.right/6+dest.left, dest.bottom-dest.bottom/6, dest.right/6+dest.left, dest.bottom-dest.bottom/6+b_GaugeHeight);
	}
	public void initSprite(int w,int h,Rect desr)
	{
		this.b_GaugeWidth = w;//그림크기
		this.b_GaugeHeight =h;//그림크기
		this.b_Rect.right = b_GaugeWidth;
		this.b_Rect.bottom = b_GaugeHeight;
		this.dest = desr;
		this.fgsrcRect = new Rect(0,0,0,b_GaugeHeight);
		this.fgdesRect = new Rect(dest.right/6+dest.left, dest.bottom-dest.bottom/6, dest.right/6+dest.left, dest.bottom-dest.bottom/6+b_GaugeHeight);
		this.egdesRect = new Rect(dest.right/6+dest.left, dest.bottom-dest.bottom/6, (dest.right/6+dest.left)+b_GaugeWidth, dest.bottom-dest.bottom/6+b_GaugeHeight);
		this.egbdesRect = new Rect(dest.right/6+dest.left, dest.bottom-dest.bottom/6, (dest.right/6+dest.left)+b_GaugeWidth, dest.bottom-dest.bottom/6+b_GaugeHeight);
	}
	public void setgauge(int p_g) //게이지 값을 받는 함수
	{
		this.gaugefill = p_g;
	}
	public int getgauge()
	{
		return this.gaugefill;
	}
	public void Update()
	{
		this.fgdesRect.right = (dest.right/6+dest.left) +gaugefill; //gaugefill 만큼 게이지 이미지를 그린다.
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
	public void bitmaprecycle() { //비트맵 메모리 해제
		bottle.recycle();
		bottle = null;
		fgauge.recycle();
		fgauge = null;
		super.bitmaprecycle();
	}
	
}
