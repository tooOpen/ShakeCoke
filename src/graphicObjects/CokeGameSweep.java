package graphicObjects;


import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Rect;
import appManager.AppManager;
import com.solmi.m1app.R;

public class CokeGameSweep extends CokeGameScroll {

	private int rankscene;//��ũȭ���� �׷����� ���ϴ� ����
	private int botlimit; //ȭ�� height���� �޾ƿ�
	private int count; //�ݶ� �������� �ӵ� ���� ����.
	
	public CokeGameSweep(int w,int h,int bot,Rect desr,Options op)
	{
		super(AppManager.getInstance().getBitmap(R.drawable.sweep,op));//�ݶ� ��Ʈ�� �ޱ�
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
	public void setRestart()//����� �ʱ�ȭ �Լ�.
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
		dest.bottom +=dir;//�ݶ� ����Ƽ���̼� ��Ʈ�� dir��ŭ �����ش�.
	
		if(dir>botlimit/80) //dir�� �������� 3������ �ѹ��� dir�� ���ҽ����ش�.
		{
			if(count==3)
			{
				dir -=botlimit/600;
				count = 0;
			}
			count++;
		}
		else if(dir<=botlimit/80)//�����̻� �Ǹ� �ӵ�����.
		{
			dir = botlimit/80;
		}
		if(dest.bottom-spriteH/2>botlimit) //ȭ���� �� �������� ��ũȭ�� �׷��ֱ� 
		{
			rankscene = 1;
		}
		if(dest.top>botlimit)//ȭ�鿡�� ������� ������Ʈ�� ���� ���ѱ�
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

