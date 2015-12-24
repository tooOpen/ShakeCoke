package com.example.shakecoke;

import graphicObjects.CokeGameGauge;
import graphicObjects.CokeGameHeight;
import graphicObjects.CokeGamePop;
import graphicObjects.CokeGameRank;
import graphicObjects.CokeGameReady;
import graphicObjects.CokeGameRising;
import graphicObjects.CokeGameScroll;
import graphicObjects.CokeGameShake;
import graphicObjects.CokeGameSweep;
import graphicObjects.CokeGameTimer;

import java.text.SimpleDateFormat;
import java.util.Date;

import soundManager.SoundManager;
import sqlite.CokeGameSQL;
import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import appManager.AppManager;

import com.solmi.bluetoothservice.M1DATA;
import com.solmi.m1app.R;
import com.solmi.m1app.game.CokeGameActivity;

public class CokeGameView extends SurfaceView implements SurfaceHolder.Callback{
	

	public static CokeGameThread b_thread; //������ ����
	private CokeGameReady b_ready;//�غ�ȭ�� ��ü
	private CokeGameShake b_hand;//��鸮�� �� ��ü
	private CokeGameTimer b_time;//���� �ð� ��ü
	private CokeGameGauge b_gauge;//������ ��ü
	private CokeGamePop b_pop;//�ݶ� ����ȭ�� ��ü
	private Bitmap[] poparray;//���� �ϸ� ��Ʈ�� �迭
	private CokeGameScroll b_scroll;//���� ��� ��ũ�� ��ü
	private CokeGameRising b_rising;//�ݶ� ��� ��ü
	private CokeGameHeight b_height;//���� ���� ��ü
	private CokeGameSweep b_swipe;//�������� �ݶ� ��ü
	private CokeGameRank b_rank;//��ũ ȭ�� ��ü
	
	private CokeGameSQL b_helper; //�����ͺ��̽� ����
	private Bitmap back;//���� ���ȭ�� ��Ʈ��

	
	private Bitmap bar;//�ػ󵵷� ���� ����ó�� ��Ʈ��
	private int resolutionStartSpot; //�ػ󵵰� 16:9�� �ƴҶ� ����� ����
	private int deivcecounter;//���� ���� �޴� �� �ӵ��� �ʹ� ���� ī���͸� �ӵ� ����
	
	private int gaugefill; //�������� ����ϱ� ���� ������ ȭ���� �ػ󵵸��� ������ ũ�Ⱑ �ٸ��Ƿ� �ػ󵵸� �������� ���� ��
	private boolean swipeStart;//�������� �ݶ� ����Ǯ ��� ���� ����
	private boolean regame;//������ ����� ���� �ƴ��� �����ϴ� ����
	private boolean dontmove;//������ ��⸦ ����� ������ true�� �ȴ�.
	private SimpleDateFormat dateFormat;//��¥�� ������ ���� ����
	private Date date;
	private int whereYouAre; //�ڱ� ����� �����ϴ� ����
	private String degree; //��� ���� st,nd,rd,th
	
	private int width;//ȭ�� ����
	private int height;//ȭ�� ����
	private int line;//������ ���̽� ������ �ٰ���
	private int streamid;
	private int variance;//���ӵ� ���� ����
	private int prevalue;//������ ���ӵ� ��
	private static final int risingM = 1; //���� Ǯ �ؽ��� �ε�����
	private static final int swipeM = 2;
	private static final int countM = 3; 
	public enum gState { //���¸� enum������ �Ͽ� �˱� ������
		PRE_GAME("ready_to_play"),
		IN_GAME("in_game"),
		POP_ANI("coke_pop"),
		RISING_COKE("coke_rise"),
		DROP_COKE("reach_the_limit"),
		SWIPE_SCENE("coke_swipe_scene"),
		RANK_SCENE("display_rank");
		private String descripState;

		gState(String arg) {

			this.descripState = arg;
		}

		String get_state() {
			return descripState;
		}
	}
	private gState gameState;
	
	Handler bhandler = new Handler() { //��� ���ӵ� ���� ó���ϴ� �ڵ鷯
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case M1DATA.MSG_ACC:
				M1DATA inData = new M1DATA();
				inData = (M1DATA) msg.obj; //���ӵ� ���ޱ�
				int value = (inData.ACC_X + inData.ACC_Y + inData.ACC_Z) / 3;//x,y,z�� ���� ���ӵ����� ���Ѵ��� ���� ����� ��´�.
				variance += Math.abs(value - prevalue);//�������� ���Ͽ� ���� ������ ����Ѵ�.
				if (deivcecounter > 10) {//10������ �ѹ��� ó��
					int speedvalue = 15 - value / 100 * 3;//�ִ� 15������ -15������ ���� �ֱ� ������ 15- ��/100�� �ϴµ�
					if (speedvalue >= -15 && speedvalue <= -3) { //������ �ʹ� �����ϸ� �����°�ó�� ���̹Ƿ� 5�ܰ�� ����
						speedvalue = -15;
					} else if (speedvalue > -3 && speedvalue <= 0) {
						speedvalue = -6;
					} else if (speedvalue > 0 && speedvalue <= 6) {
						speedvalue = 0;
					} else if (speedvalue > 6 && speedvalue <= 9) {
						speedvalue = 6;
					} else if (speedvalue > 9 && speedvalue <= 15) {
						speedvalue = 15;}
					if(variance<=10) //�������� ������
						dontmove = true;
					else 
						dontmove = false;
					b_hand.setDif(speedvalue);//���� ������ �����̼� �����ش�.
					
					if (variance <= 0) { //0�ϰ�� ������ ���ܰ� ������ �ƹ��͵� ������ �ʴ´�.
					} else {
						b_gauge.setgauge(b_gauge.getgauge() + variance//������ gaugefill�� ������ �����Ѵ�.
								/ gaugefill);
					}
					deivcecounter = 0;
					variance = 0;
				}
				prevalue = value;
				deivcecounter++;
				break;
			default:
				break;
			}
		}
	};
	
	public CokeGameView(Context context) {
		super(context); 
		AppManager.getInstance().setgameView(this);		
		AppManager.getInstance().setResource(getResources());
		SoundManager.getInstance().Init(context);
		
		setGameState(gState.PRE_GAME.ordinal());//���۽� �غ� ���� ����
		swipeStart = false;
		dateFormat = new SimpleDateFormat("MM-dd HH:mm"); 
		
		poparray = new Bitmap[6];
		b_helper = new CokeGameSQL(context, "cokerank.db", null, 1);
		
		Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		if(display.getWidth()<=720||display.getWidth()*16 == display.getHeight()*9)//�ػ󵵰� 16�� 9�̸� �״�� ���������
		{
        width = display.getWidth();
        resolutionStartSpot = 0;
		}
		else {//�׷��� �ʴٸ� width�� 720���� �ϰ� resolutionStartSpot�� ȭ�� ���� �ش�.
		width = 720;
		resolutionStartSpot = (display.getWidth()-720)/2;
		}
       height = display.getHeight();
       
       gaugefill = 250000/width;//ȭ�� �ػ󵵷� 250000�� ������ 250000�� �������ؼ� �����ؼ� ����
       
       line = height/19;
       
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.RGB_565;
		options.inSampleSize = 2; //������� �ؾ��ϰų� ũ�Ⱑ ū ��Ʈ���� 1/2������� �ε����־� �����޸𸮸� �����Ѵ�.
	
		for(int i =0;i<6;i++) //���� ��Ʈ�� �迭 �Ҵ��ϱ�
		{
			poparray[i] = BitmapFactory.decodeResource(getResources(), R.drawable.popani0+i,options);
		}
		b_rising = new CokeGameRising(318/2, 878/2,height, new Rect(width/2-width/3+resolutionStartSpot, height/6 ,width/2+width/3+resolutionStartSpot, height+10), 50, 6,options);
	  		b_scroll = new CokeGameScroll(AppManager.getInstance().getBitmap(R.drawable.bulesky2,options));
	  		b_scroll.initSprite(720/2, 1280/2, new Rect(resolutionStartSpot,0,width+resolutionStartSpot,height),-width/50,
	 				AppManager.getInstance().getBitmap(R.drawable.clouds,options));
	  		b_swipe = new CokeGameSweep(720/2, 2000/2,height, new Rect(resolutionStartSpot, -(height+(height-height/3)), width+resolutionStartSpot, 0),options);
	  		
	  		back = BitmapFactory.decodeResource(getResources(), R.drawable.background,options);
	  		back = Bitmap.createScaledBitmap(back, width, height, false);
	  	
	  	
	  		
	  		if(resolutionStartSpot!=0)//16�� 9�� �ƴҶ� ���ʺ��ٸ� �׷��ִµ� �� ������ �� �̹����� ȭ�麸�� �� ���ʿ� �ֱ� �����̴�.
	  		{
		  		bar = BitmapFactory.decodeResource(getResources(), R.drawable.bar,options);
				bar = Bitmap.createScaledBitmap(bar, resolutionStartSpot,  height, false);
	  		}
	  		Bitmap htemp = BitmapFactory.decodeResource(getResources(), R.drawable.shake,options);
			htemp = Bitmap.createScaledBitmap(htemp, width,  height-height/3, false);
			Bitmap etemp = BitmapFactory.decodeResource(getResources(), R.drawable.egauge,options);
			etemp = Bitmap.createScaledBitmap(etemp, width-width/4, height/6, false);
			Bitmap ftemp = BitmapFactory.decodeResource(getResources(), R.drawable.fillgauge,options);
			ftemp = Bitmap.createScaledBitmap(ftemp, width-width/4, height/6, false);
			Bitmap etempb = BitmapFactory.decodeResource(getResources(), R.drawable.egaugebot,options);
			etempb = Bitmap.createScaledBitmap(etempb, width-width/4, height/6, false);
			Bitmap btemp = BitmapFactory.decodeResource(getResources(), R.drawable.bottle,options);
			btemp = Bitmap.createScaledBitmap(btemp, width/5, height/12, false);
			
		 options.inSampleSize = 1;//�ٽ� ����ũ��� ���ƿ���
		 
    	getHolder().addCallback(this);
		b_thread = new CokeGameThread(getHolder(),this);
		
         SoundManager.getInstance().addmedia(R.raw.background);
         SoundManager.getInstance().addSound(swipeM, R.raw.swipe);
         SoundManager.getInstance().addSound(risingM, R.raw.rise);
         SoundManager.getInstance().addSound(countM, R.raw.countdown);
         
        b_hand = new CokeGameShake(htemp, height/5);
		b_ready = new CokeGameReady(AppManager.getInstance().getBitmap(
				R.drawable.ready, options), AppManager.getInstance().getBitmap(
				R.drawable.readyscene, options),AppManager.getInstance().getBitmap(
						R.drawable.help, options),AppManager.getInstance().getBitmap(
								R.drawable.helpscene, options),AppManager.getInstance().getBitmap(
										R.drawable.go, options),AppManager.getInstance().getBitmap(R.drawable.number_icon, options),
										AppManager.getInstance().getBitmap(R.drawable.readytext, options),
										AppManager.getInstance().getBitmap(R.drawable.exit, options));
		
		b_ready.initSprite(350, 350, options,new Rect((width / 2 - width / 4)+resolutionStartSpot, height / 2
				- height / 7, (width / 2 + width / 4)+resolutionStartSpot, height / 2 + height / 7),
				new Rect(resolutionStartSpot, 0, width+resolutionStartSpot, height));
		b_time = new CokeGameTimer(100, 100, new Rect(width / 2 - width/5+resolutionStartSpot, 0,
				width / 2+resolutionStartSpot, width/5), 20, -1,true,width/5, options);
		b_height = new CokeGameHeight(AppManager.getInstance().getBitmap(
				R.drawable.m, options), 150, 150, new Rect((width / 2+resolutionStartSpot) - 275,
				height / 2, (width / 2+resolutionStartSpot) - 125, height / 2 + 150), options);
		b_gauge = new CokeGameGauge(etemp, btemp, ftemp, etempb);
		b_gauge.initSprite(width - width / 4, height / 6, new Rect(resolutionStartSpot, 0, width+resolutionStartSpot,
				height));

		b_pop = new CokeGamePop(poparray,AppManager.getInstance().getBitmap(R.drawable.good, options),714 / 2, 974 / 2,575,400, height, new Rect(resolutionStartSpot,
				height / 7, width+resolutionStartSpot, height - height / 7), 10, 6);

		b_rank = new CokeGameRank(AppManager.getInstance().getBitmap(
				R.drawable.rankscene, options), AppManager.getInstance()
				.getBitmap(R.drawable.exit, options), AppManager
				.getInstance().getBitmap(R.drawable.again, options));
		b_rank.initSprite(720, 1280, new Rect(resolutionStartSpot, 0, width+resolutionStartSpot, height));
	}
	
	public int whatstate() 
	{
		return gameState.ordinal();
	}
	
	public void setGameState(int state)
	{
		this.gameState = gState.values()[state];	
	}
	
	public void pauseS()//�� ���º��� �Ͻ������� �Ͼ���� ���� �Ͻ����� �ϱ�
	{
		switch(gameState.ordinal())
		{
		case 0 :
			SoundManager.getInstance().poolPause();
			break;
		case 1:
			SoundManager.getInstance().pauseM();
			break;
		case 5:
			SoundManager.getInstance().poolPause();
			break;
		}
	}
	public void resumeS()//�� ���º��� resume�� �Ͼ���� ���� �ٽ���� �ϱ�
	{
		switch(gameState.ordinal())
		{
		case 0 :
			SoundManager.getInstance().poolResume();
			break;
		case 1:
			SoundManager.getInstance().playmedia();
			break;
		case 5:
			SoundManager.getInstance().poolResume();
			break;
		}
	}
	
	
	public void regame()//���� ����� ��ư�� ������ ��.
	{
		CokeGameActivity.getACC();
		SoundManager.getInstance().mReset();
		SoundManager.getInstance().addmedia(R.raw.background);
		SoundManager.getInstance().mPrepare();
		
		swipeStart = false;
		setGameState(gState.PRE_GAME.ordinal());
		b_ready.setRestart();
		b_time.setRestart();
		b_gauge.setRestart();
		b_pop.setRestart();
		b_swipe.setRestart();
		b_rising.setRestart();
		b_height.setRestart();
		b_rank.setRestart();
		b_helper.setRestart();
		regame = true;
	}
	public void getmessage(Message ms) //��Ƽ��Ƽ���� ȣ��Ǵµ� �װ����� ���� �޽����� �ٽ� ����� �ڵ鷯�� ������.
	{
		Message bms = bhandler.obtainMessage();
		bms.obj = ms.obj;
		bms.what = ms.what;
		bhandler.sendMessage(bms);
	}
	
	
	
	@Override
	public  boolean onTouchEvent(MotionEvent event) {//��ġ�̺�Ʈ
		
		if (event.getAction() == MotionEvent.ACTION_DOWN||event.getAction() == MotionEvent.ACTION_MOVE) {//������ ������ ������ �ʰ�
			int x = (int) event.getX();
			int y = (int) event.getY();
			switch (gameState.ordinal()) {//���º��� ��ġ�� �ϴ����� �ٸ���.
			case 0:                    //�غ�����ϰ��
				if(CokeGameActivity.g_BluetoothService.getState()==104&&!b_ready.gethelp())
				{
				if (b_ready.getstratDest().contains(x, y)) {//��ŸƮ��ư �Ϲ�
					b_ready.setonorOff(true);
				}else
					b_ready.setonorOff(false);
				}
				break;
			case 6:                      //��ũ����ϰ��
				if (b_rank.getfinbtnRect().contains(x, y)) { //������ ��ư �Ϲ�
					b_rank.setwhatbtnon(2);
				} else if (b_rank.getrebtnRect().contains(x, y)) {//����� ��ư �Ϲ�
					b_rank.setwhatbtnon(1);
				}else {
					b_rank.setwhatbtnon(0);
				}
				break;
			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {//������� �� ��ư�� ���õǾ�����
			int x = (int) event.getX();
			int y = (int) event.getY();
			switch(gameState.ordinal())
			{
			case 0:              //�غ���
				if(b_ready.gethelp()&&b_ready.gethSceneDest().contains(x, y))
				{
					b_ready.sethelp(false); //���� ����
				}
				else if (!b_ready.gethelp()&&b_ready.getstratDest().contains(x, y)) //��ŸƮ ��ư ������ ���
				{
					if (!regame) {
						if (CokeGameActivity.g_BluetoothService.getState() == 104) { //��������� ����Ǿ����� Ȯ��
							b_ready.setGamestart(1);
							SoundManager.getInstance().play(countM);
							CokeGameActivity.getACC();
						} else
							CokeGameActivity.connedevice();//������� �ʾҴٸ� ����
					}else                            //������� ��� ���� �������� ����
					{
						b_ready.setGamestart(1); 
						SoundManager.getInstance().play(countM);
					}
				}else if(b_ready.gethelpDest().contains(x, y))//���� â����
				{
					b_ready.sethelp(true);
				}
					break;
			case 6:
				if(b_rank.getrebtnRect().contains(x,y)) //����� ��ư
					{
						regame();
					}else if(b_rank.getfinbtnRect().contains(x, y))//����
					{
						System.exit(0);
					}
					b_rank.setwhatbtnon(0);
				break;
			}
		}
		return true;
	}



	public void bitdraw(Canvas canvas) {//������ �Լ�
		if (canvas != null) {
			canvas.drawColor(Color.BLACK); //��� ���������� �Ͽ� �ػ󵵰� �ٸ� �� �ڿ����� ���� ����
			switch (gameState.ordinal()) {
			case 0:
			case 1:                      //�ΰ����� ���
				canvas.drawBitmap(back, resolutionStartSpot, 0, null);//���׸���
				
				b_time.Draw(canvas);//�ð��׸���
				b_gauge.Draw(canvas);//�������׸���
				b_hand.Draw(canvas);//�ձ׸���
				if(resolutionStartSpot!=0)//�ػ󵵰� 16�� 9�� �ƴҶ�
				{
					canvas.drawBitmap(bar, 0, 0, null);//����
				}
				if (b_ready.getGamestart()==0||b_ready.getGamestart()==1) {//���� �������϶�
					b_ready.Draw(canvas);//�غ�ȭ�� �׸���
				}
				if(dontmove&&gameState.ordinal()==1)//��Ⱑ ��鸮�� ������
				{
					Paint paint2 = new Paint();
					paint2.setTextSize((width + height) / 20);
					paint2.setTypeface(Typeface.create(Typeface.SANS_SERIF,
							Typeface.NORMAL));
					paint2.setTextAlign(Paint.Align.CENTER);
					paint2.setColor(Color.RED);
					canvas.drawText("���� �ּ���! ", width / 2+resolutionStartSpot, (height-height / 4)
							+ paint2.getTextSize(), paint2);
				}
				break;
			case 2:
				b_pop.Draw(canvas);//�������׸���
				break;
			case 3:
				b_scroll.Draw(canvas);//�����׸���
				b_rising.Draw(canvas);//�ݶ� ��±׸���
				b_height.Draw(canvas);//���� ���ڱ׸���
				break;
			case 4:
				b_scroll.Draw(canvas);
				b_rising.Draw(canvas);
				b_height.Draw(canvas);
				if (b_rising.getDir() == false)//���̸�ŭ ���ٸ�
					b_swipe.Draw(canvas);//�������� �ݶ�׸���
				break;
			case 5:
			case 6:
				b_rank.Draw(canvas);//��ũȭ��׸���
				Paint paint = new Paint();

				paint.setTextSize((width + height) / 33);
				paint.setTypeface(Typeface.create(Typeface.SANS_SERIF,
						Typeface.NORMAL));
				synchronized (this) {
					SQLiteDatabase db = b_helper.getReadableDatabase();
					Cursor cursor = db.query("CokeRankBoard", new String[] {
							"score", "dat" }, null, null, null, null,
							"score desc");
					
					
					Cursor cursor2 = db.query("CokeRankBoard", new String[] {
							"score", "dat" }, null, null, null, null,
							"score desc");
					if (b_helper.getonlyOneR() == 1) {
						for (int i = 0; i < cursor2.getCount(); i++) {
							if (cursor2.moveToNext() == false) {
								break;
							}
							if (cursor2.getString(0).equals(((Integer) (b_gauge.getgauge() + 300)).toString())
									&& cursor2.getString(1).equals(dateFormat.format(date))) {
								whereYouAre = i + 1;
								if(whereYouAre==1)
									degree = "st";
								else if(whereYouAre==2)
									degree = "nd";
								else if(whereYouAre==3)
									degree = "rd";
								else
									degree = "th";
								break;
							}
						}
					}
					for (int i = 0; i < 6; i++) {
						if (cursor.moveToNext()==false) 
							break;
						
						if (i % 2 == 0)//���� �����ư��� �� �ٲٱ�
							paint.setColor(Color.argb(255, 0, 255, 255));
						else
							paint.setColor(Color.argb(0255, 255, 255, 0));

						paint.setTextAlign(Paint.Align.LEFT);
						canvas.drawText(i + 1 + ".", width / 15+resolutionStartSpot, height / 6
								+ (line * i) + paint.getTextSize(), paint);
						canvas.drawText(cursor.getString(0) + "m.", width / 5+resolutionStartSpot,
								height / 6 + (line * i) + paint.getTextSize(),
								paint);
						paint.setTextAlign(Paint.Align.RIGHT);
						canvas.drawText(cursor.getString(1),
								(width - width / 20)+resolutionStartSpot, height / 6 + (line * i)
										+ paint.getTextSize(), paint);
					}
					if (date != null) {
						paint.setTextAlign(Paint.Align.CENTER);
						paint.setColor(Color.RED);
						canvas.drawText("your score is ", width / 2+resolutionStartSpot, height / 2
								+ paint.getTextSize(), paint);
						paint.setTextAlign(Paint.Align.LEFT);
						canvas.drawText(whereYouAre + degree, width / 20+resolutionStartSpot, height
								/ 2 + height / 11 + paint.getTextSize(), paint);
						canvas.drawText(
								((Integer) (b_gauge.getgauge() + 300))
										.toString() + "m.", width / 4+resolutionStartSpot,
								height / 2 + height / 11 + paint.getTextSize(),
								paint);
						paint.setTextAlign(Paint.Align.RIGHT);
						canvas.drawText(dateFormat.format(date).toString(),
								(width - width / 20)+resolutionStartSpot, height / 2 + height / 11
										+ paint.getTextSize(), paint);
					}
					cursor.close();
					cursor2.close();
				}
				if(b_swipe.getrankScene()!=2)//�������� �ݶ�׸���
				b_swipe.Draw(canvas);
				break;
			}
		}
	}
	
	public void Update() { //������Ʈ �Լ�
		long gamet = System.currentTimeMillis();

		switch (gameState.ordinal()) {
		case 0:  //�غ����
			if (b_ready.getGamestart()==1) {
			b_ready.Update(gamet);
			}
			else if (b_ready.getGamestart()==2) {//������ �����ϸ�
				SoundManager.getInstance().playmedia();//�������
				setGameState(gState.IN_GAME.ordinal());
				SoundManager.getInstance().stop(countM);//ī��Ʈ ���� ����
			}
			break;
		case 1:

			b_time.Update(gamet);//�ð� ������Ʈ
			b_gauge.Update();//������ ������Ʈ

			if (b_time.getValue() <= 0) {//�ð��� �ٵǸ�
				setGameState(gState.POP_ANI.ordinal());
				SoundManager.getInstance().stopmedia();//������ǲ���
			}
			break;
		case 2:

			b_pop.Update(gamet);
			b_height.setLimit(b_gauge.getgauge() + 300);//��� ����ŭ ���� �����ϱ�
			//���̸�ŭ �������ϱ�
			if(b_height.getLimit()>300+width/2)
			{
				b_pop.sethowgood(2);//great
			}else if(b_height.getLimit()>300+width/3)
			{
				b_pop.sethowgood(3);//nice
			}else if(b_height.getLimit()>300+width/4)
			{
				b_pop.sethowgood(0);//good
			}else
				b_pop.sethowgood(1);//bad
			if (b_pop.getcurframe() >= 6) {//��������� ������
				streamid = SoundManager.getInstance().playLoop(risingM);
				setGameState(gState.RISING_COKE.ordinal());
			}
			break;
		case 3:

			b_scroll.Update();
			b_rising.Update(gamet);
		
			if (b_height.getValue() >= b_height.getLimit()) { //���̼��ڰ� ������ ������
				if(!regame)
				SoundManager.getInstance().stop(risingM);
				else
				SoundManager.getInstance().strstop(streamid); //������� ���߱�
				setGameState(gState.DROP_COKE.ordinal());
			}
			else
				b_height.Update(gamet);
			break;
		case 4:
			b_rising.setDir(true);//�ݶ� ����ٲ� �������� �ϱ�
			b_rising.Update(gamet);
			if (b_rising.getDir() == false) {//����ϴ� �ݶ� �ٳ���������
				b_swipe.Update();
				if (!swipeStart) {
					SoundManager.getInstance().play(swipeM);//�������� �ݶ�Ҹ� �ѱ�
					swipeStart = !swipeStart;
				}
			}
			if (b_swipe.getrankScene() == 1)//�ݶ� ȭ���� �ٰ�������
				setGameState(gState.RANK_SCENE.ordinal());
			break;
		case 5:
		case 6:

			if (b_helper.getonlyOneR() == 0) { //�ѹ��� ����� �� �ְ��Ѵ�.
				SQLiteDatabase db = b_helper.getWritableDatabase();
				date = new Date();
				ContentValues row = new ContentValues();
				row.put("dat", dateFormat.format(date));
				row.put("score", b_gauge.getgauge() + 300);
				db.insertOrThrow("CokeRankBoard", null, row);//���ֱ�
				b_helper.setonlyOneR(1);
				}
			if (b_swipe.getrankScene() == 1) {
				b_swipe.Update();
			} else
				SoundManager.getInstance().stop(swipeM);
			break;
		}

	}
//---------------------------------------------surface view
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		 if (!b_thread.isAlive()) {//�����尡 ���� ���۵��� �ʾҴٸ�
			 b_thread.setRunning(true);//������ ������
			 b_thread.start();
		 }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		if(!b_thread.getPause())//�Ͻ������� �ƴ϶�� 
		{
		boolean retry = true;
		b_thread.setRunning(false);
		while (retry) {
			try {
				b_thread.join();//������ ����
				retry = false;
			} catch (Exception e) {

			}
		}
		//-----------��Ʈ�� �޸� ����
		if (!b_ready.isnull()) {
			b_ready.bitmaprecycle();
			b_hand.bitmaprecycle();
			b_time.bitmaprecycle();
			b_gauge.bitmaprecycle();
			back.recycle();
			back = null;
			
			b_pop.bitmaprecycle();
			b_scroll.bitmaprecycle();
			b_rising.bitmaprecycle();
			b_height.bitmaprecycle();
			b_swipe.bitmaprecycle();
			b_rank.bitmaprecycle();
		} else if (!b_hand.isnull()) {
			b_hand.bitmaprecycle();
			b_time.bitmaprecycle();
			b_gauge.bitmaprecycle();
			back.recycle();
			back = null;
		
			b_pop.bitmaprecycle();
			b_scroll.bitmaprecycle();
			b_rising.bitmaprecycle();
			b_height.bitmaprecycle();
			b_swipe.bitmaprecycle();
			b_rank.bitmaprecycle();
		} else if (!b_pop.isnull()) {
			b_pop.bitmaprecycle();
			b_scroll.bitmaprecycle();
			b_rising.bitmaprecycle();
			b_height.bitmaprecycle();
			b_swipe.bitmaprecycle();
			b_rank.bitmaprecycle();
		} else if (!b_scroll.isnull()) {
			b_scroll.bitmaprecycle();
			b_rising.bitmaprecycle();
			b_height.bitmaprecycle();
			b_swipe.bitmaprecycle();
			b_rank.bitmaprecycle();
		} else if (!b_swipe.isnull())
		{
			b_swipe.bitmaprecycle();
			b_rank.bitmaprecycle();
		}
		else if(!b_rank.isnull())
		{
			b_rank.bitmaprecycle();
		}
		SoundManager.getInstance().releaseMusic();
		}
	}
}
