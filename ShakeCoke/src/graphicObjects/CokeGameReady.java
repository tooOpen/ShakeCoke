package graphicObjects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


public class CokeGameReady extends SpriteAnimation {

	private CokeGameTimer readytime; //게임 준비시간
	private int  gameStart; //게임 준비상태 구분변수 0.연결전,도움말보기,1.카운트다운2.시작
	
	private boolean ontouch; // 스타트버튼 하버변수 터치된 상태인가
	private boolean needhelp;//도움말 화면 상태변수
	
	private Bitmap readyscene; //준비화면
	private Bitmap hBtn;//도움버튼
	private Bitmap hScene;//도움말 화면
	private Bitmap go;//go텍스트
	private Bitmap timeback;//시간뒤 배경
	private Bitmap ready;//ready텍스트
	private Bitmap helpExit;//도움말 화면 나가기 버튼
	
	private Paint b_paint;//화면 반투명 처리를 위한 페인트
	
	private Rect b_brect;
	private Rect srcR;
	private Rect help_brect;
	private Rect srcHelp;
	private Rect srcG;
	private Rect destB;
	private Rect srcB;
	private Rect go_brect;
	private Rect destRT;
	private Rect srcRT;
	private Rect finsrcRect;
	private Rect findesRedct;
	
	public CokeGameReady(Bitmap bitmap,Bitmap rscene,Bitmap helpbtn,Bitmap helpScene,Bitmap go,Bitmap timeback,Bitmap ready,Bitmap helpExit)
	{
		super(bitmap);
		this.readyscene = rscene;
		this.hBtn = helpbtn;
		this.hScene = helpScene;
		this.timeback = timeback;
		this.go = go;
		this.helpExit = helpExit;
		this.ready = ready;
		this.gameStart = 0;
		this.ontouch = false;
		this.needhelp = false;
		this.b_paint = new Paint();
		this.b_paint.setAlpha(150);
		this.srcR = new Rect(0, 0, 720, 1280);
		this.srcHelp = new Rect(0, 0, 150, 150);
	}
	public void initSprite(int w,int h,Options o,Rect desr,Rect brect)
	{
		this.b_Rect.right = w;
		this.b_Rect.bottom = h;
		this.dest = desr;
		this.b_brect = brect;
		this.finsrcRect = new Rect(0,0,330,110);
		this.findesRedct = new Rect(b_brect.right/2-b_brect.right/5,b_brect.bottom-b_brect.bottom/9,b_brect.right/2+b_brect.right/5,b_brect.bottom-b_brect.bottom/25);
		this.go_brect = new Rect((b_brect.right+b_brect.left)/ 2 - 200, b_brect.bottom/2-250,
				(b_brect.right+b_brect.left )/ 2+200, b_brect.bottom/2+150);
		this.srcG = new Rect(0, 0, 330, 330);
		this.help_brect = new Rect((b_brect.right+b_brect.left)/2-(b_brect.right+b_brect.left)/10, b_brect.bottom-b_brect.bottom/5,
				(b_brect.right+b_brect.left)/2+(b_brect.right+b_brect.left)/10, b_brect.bottom-b_brect.bottom/12);
		this.destB = new Rect((b_brect.right+b_brect.left)/ 2 - 200, b_brect.bottom/2-250,
				(b_brect.right+b_brect.left )/ 2+200, b_brect.bottom/2+150);
		this.srcB = new Rect(0, 0, 330, 330);
		int temp = 0;
		if(b_brect.left>=40)
			temp = b_brect.right/5;
		else
			temp = b_brect.right/4;
		this.readytime = new CokeGameTimer(100, 100, new Rect(b_brect.right/ 2 - 100, b_brect.bottom/2-100,
				b_brect.right/ 2+b_brect.right/24, b_brect.bottom/2), 4, -1, false,b_brect.right/6,o);
		
		
		this.srcRT = new Rect(0, 0, 330, 120);
		this.destRT = new Rect(b_brect.right/2-b_brect.right/5, b_brect.bottom-b_brect.bottom/5, b_brect.right/2+b_brect.right/5, b_brect.bottom-b_brect.bottom/10);
	}
	public void setRestart()//재시작 초기화 함수
	{
		readytime.setValue(4);
		this.ontouch = false;
		this.needhelp = false;
		this.gameStart = 0;
	}
	public void setGamestart(int gs)
	{
		this.gameStart = gs;
	}
	public int getGamestart()
	{
		return this.gameStart;
	}
	public void setonorOff(boolean of)
	{
		this.ontouch = of;
	}
	public void sethelp(boolean of)
	{
		this.needhelp = of;
	}
	public boolean gethelp()
	{
		return this.needhelp;
	}
	public Rect getstratDest()
	{
		return dest;
	}
	public Rect gethelpDest()
	{
		return help_brect;
	}
	public Rect gethSceneDest()
	{
		return findesRedct;
	}
	
	@Override
	public void Update(long GameTime) {
			readytime.Update(GameTime); //준비시간 업데이트
			if(readytime.getValue()<=-2)//0이 아니라 -2인 이유는 go텍스트 출력시간을 위해서 
				gameStart=2;
	}
	
	@Override
	public void Draw(Canvas canvas) {
		if(gameStart==0) //접속전 상황일때
		{
			if (ontouch) {
				b_Rect.left = 350;
				b_Rect.right = 700;
			} else {
				b_Rect.left = 0;
				b_Rect.right = 350;
			}
			canvas.drawBitmap(readyscene, srcR, b_brect, b_paint);
			canvas.drawBitmap(b_bitmap, b_Rect, dest, null);
			canvas.drawBitmap(hBtn, srcHelp, help_brect, null);
			if(needhelp) //도움말 버튼을 눌렀을때
			{
				canvas.drawBitmap(hScene, srcR, b_brect, null);
				canvas.drawBitmap(helpExit, finsrcRect, findesRedct, null);
			}
		}else if(gameStart==1)//카운트다운
		{
			canvas.drawBitmap(readyscene, srcR, b_brect, b_paint);
			if(readytime.getValue()<=0) //시간이 다되었을때
			{
				canvas.drawBitmap(go, srcG, go_brect, null);
			}else//준비시간 업데이트
			{
			canvas.drawBitmap(timeback, srcB, destB, null);
			readytime.Draw(canvas);
			canvas.drawBitmap(ready, srcRT, destRT, null);
			}
		}
	}
	@Override
	public void bitmaprecycle() {
		readyscene.recycle();
		readyscene = null;
		hBtn.recycle();
		hBtn = null;
	    hScene.recycle();
	    hScene = null;
		go.recycle();
		go = null;
		timeback.recycle();
		timeback = null;
		super.bitmaprecycle();
	}
}
