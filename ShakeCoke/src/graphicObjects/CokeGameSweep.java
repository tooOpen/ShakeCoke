package graphicObjects;


import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Rect;
import appManager.AppManager;
import com.solmi.m1app.R;

public class CokeGameSweep extends CokeGameScroll {

	private int rankscene;//랭크화면을 그려줄지 정하는 변수
	private int botlimit; //화면 height값을 받아옴
	private int count; //콜라가 내려오는 속도 조절 변수.
	
	public CokeGameSweep(int w,int h,int bot,Rect desr,Options op)
	{
		super(AppManager.getInstance().getBitmap(R.drawable.sweep,op));//콜라 비트맵 받기
		this.initSprite(w, h, desr, bot/20,null);
		this.rankscene = 0;
		this.count = 0;
		this.botlimit = bot;
		this.b_Rect.right = spriteW;
		this.b_Rect.bottom = spriteH;
	}
	public void setrankScene(int rc)
	{
		this.rankscene = rc;
	}
	public int getrankScene()
	{
		return this.rankscene;
	}
	public void setRestart()//재시작 초기화 함수.
	{
		this.dir = botlimit/20;
		dest = new Rect(dest.left, -(botlimit+(botlimit-botlimit/3)), dest.right+dest.left, 0);
		this.rankscene = 0;
		this.count = 0;
	}
	@Override
	public void Update()
	{     
		dest.top += dir;
		dest.bottom +=dir;//콜라 데스티네이션 렉트를 dir만큼 내려준다.
	
		if(dir>botlimit/80) //dir이 빠를때는 3번마다 한번씩 dir을 감소시켜준다.
		{
			if(count==3)
			{
				dir -=botlimit/600;
				count = 0;
			}
			count++;
		}
		else if(dir<=botlimit/80)//일정이상 되면 속도유지.
		{
			dir = botlimit/80;
		}
		if(dest.bottom-spriteH/2>botlimit) //화면이 다 덮혔을때 랭크화면 그려주기 
		{
			rankscene = 1;
		}
		if(dest.top>botlimit)//화면에서 사라지면 업데이트와 렌더 안한기
		{
			rankscene = 2;
		}
	}
         @Override
     	public void Draw(Canvas canvas) {
     		canvas.drawBitmap(b_bitmap, b_Rect,dest,null);
     	}
		@Override
		public void bitmaprecycle() {
		b_bitmap.recycle();
		b_bitmap = null;
		}
}

