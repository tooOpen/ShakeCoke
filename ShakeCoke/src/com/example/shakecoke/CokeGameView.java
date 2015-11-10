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
	

	public static CokeGameThread b_thread; //스레드 변수
	private CokeGameReady b_ready;//준비화면 객체
	private CokeGameShake b_hand;//흔들리는 손 객체
	private CokeGameTimer b_time;//게임 시간 객체
	private CokeGameGauge b_gauge;//게이지 객체
	private CokeGamePop b_pop;//콜라 폭파화면 객체
	private Bitmap[] poparray;//폭파 하면 비트맵 배열
	private CokeGameScroll b_scroll;//구름 상승 스크롤 객체
	private CokeGameRising b_rising;//콜라 상승 객체
	private CokeGameHeight b_height;//높이 숫자 객체
	private CokeGameSweep b_swipe;//내려오는 콜라 객체
	private CokeGameRank b_rank;//랭크 화면 객체
	
	private CokeGameSQL b_helper; //데이터베이스 헬퍼
	private Bitmap back;//게임 배경화면 비트맵

	
	private Bitmap bar;//해상도로 인한 블랙바처리 비트맵
	private int resolutionStartSpot; //해상도가 16:9가 아닐때 생기는 오차
	private int deivcecounter;//기기로 부터 받는 값 속도가 너무 빨라 카운터를 속도 조절
	
	private int gaugefill; //게이지를 계산하기 위한 값으로 화면의 해상도마다 게이지 크기가 다르므로 해상도를 바탕으로 계산된 값
	private boolean swipeStart;//내려오는 콜라 사운드풀 재생 여부 결정
	private boolean regame;//게임이 재시작 인지 아닌지 구분하는 변수
	private boolean dontmove;//유저가 기기를 흔들지 않으면 true가 된다.
	private SimpleDateFormat dateFormat;//날짜를 얻어오기 위한 변수
	private Date date;
	private int whereYouAre; //자기 등수를 저장하는 변수
	private String degree; //등수 뒤의 st,nd,rd,th
	
	private int width;//화면 가로
	private int height;//화면 세로
	private int line;//데이터 베이스 한줄의 줄간격
	private int streamid;
	private int variance;//가속도 값의 변량
	private int prevalue;//이전의 가속도 값
	private static final int risingM = 1; //사운드 풀 해쉬맵 인덱스들
	private static final int swipeM = 2;
	private static final int countM = 3; 
	public enum gState { //상태를 enum형으로 하여 알기 쉽게함
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
	
	Handler bhandler = new Handler() { //기기 가속도 값을 처리하는 핸들러
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case M1DATA.MSG_ACC:
				M1DATA inData = new M1DATA();
				inData = (M1DATA) msg.obj; //가속도 값받기
				int value = (inData.ACC_X + inData.ACC_Y + inData.ACC_Z) / 3;//x,y,z각 축의 가속도값을 더한다음 나눠 평균을 얻는다.
				variance += Math.abs(value - prevalue);//이전값과 비교하여 나온 변량을 사용한다.
				if (deivcecounter > 10) {//10번마다 한번씩 처리
					int speedvalue = 15 - value / 100 * 3;//최대 15도에서 -15도까지 갈수 있기 때문에 15- 값/100을 하는데
					if (speedvalue >= -15 && speedvalue <= -3) { //각도가 너무 세밀하면 떨리는것처럼 보이므로 5단계로 조정
						speedvalue = -15;
					} else if (speedvalue > -3 && speedvalue <= 0) {
						speedvalue = -6;
					} else if (speedvalue > 0 && speedvalue <= 6) {
						speedvalue = 0;
					} else if (speedvalue > 6 && speedvalue <= 9) {
						speedvalue = 6;
					} else if (speedvalue > 9 && speedvalue <= 15) {
						speedvalue = 15;}
					if(variance<=10) //움직이지 않을때
						dontmove = true;
					else 
						dontmove = false;
					b_hand.setDif(speedvalue);//얻은 각도로 로테이션 시켜준다.
					
					if (variance <= 0) { //0일경우 나누면 예외가 뜸으로 아무것도 해주지 않는다.
					} else {
						b_gauge.setgauge(b_gauge.getgauge() + variance//변량을 gaugefill로 나누어 적용한다.
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
		
		setGameState(gState.PRE_GAME.ordinal());//시작시 준비 모드로 설정
		swipeStart = false;
		dateFormat = new SimpleDateFormat("MM-dd HH:mm"); 
		
		poparray = new Bitmap[6];
		b_helper = new CokeGameSQL(context, "cokerank.db", null, 1);
		
		Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		if(display.getWidth()<=720||display.getWidth()*16 == display.getHeight()*9)//해상도가 16대 9이면 그대로 사용하지만
		{
        width = display.getWidth();
        resolutionStartSpot = 0;
		}
		else {//그렇지 않다면 width는 720으로 하고 resolutionStartSpot에 화면 갭을 준다.
		width = 720;
		resolutionStartSpot = (display.getWidth()-720)/2;
		}
       height = display.getHeight();
       
       gaugefill = 250000/width;//화면 해상도로 250000을 나눈다 250000은 여러번해서 적절해서 선택
       
       line = height/19;
       
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.RGB_565;
		options.inSampleSize = 2; //리사이즈를 해야하거나 크기가 큰 비트맵은 1/2사이즈로 로드해주어 오버메모리를 방지한다.
	
		for(int i =0;i<6;i++) //폭파 비트맵 배열 할당하기
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
	  	
	  	
	  		
	  		if(resolutionStartSpot!=0)//16대 9가 아닐때 왼쪽블랙바를 그려주는데 그 이유는 팔 이미지가 화면보다 더 왼쪽에 있기 때문이다.
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
			
		 options.inSampleSize = 1;//다시 원래크기로 돌아오기
		 
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
	
	public void pauseS()//각 상태별로 일시정지가 일어났을때 음악 일시정지 하기
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
	public void resumeS()//각 상태별로 resume가 일어났을때 음악 다시재생 하기
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
	
	
	public void regame()//게임 재시작 버튼을 눌렀을 때.
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
	public void getmessage(Message ms) //액티비티에서 호출되는데 그곳에서 받은 메시지를 다시 뷰안의 핸들러로 보낸다.
	{
		Message bms = bhandler.obtainMessage();
		bms.obj = ms.obj;
		bms.what = ms.what;
		bhandler.sendMessage(bms);
	}
	
	
	
	@Override
	public  boolean onTouchEvent(MotionEvent event) {//터치이벤트
		
		if (event.getAction() == MotionEvent.ACTION_DOWN||event.getAction() == MotionEvent.ACTION_MOVE) {//눌러만 졌을때 띄지는 않고
			int x = (int) event.getX();
			int y = (int) event.getY();
			switch (gameState.ordinal()) {//상태별로 터치시 하는일이 다르다.
			case 0:                    //준비상태일경우
				if(CokeGameActivity.g_BluetoothService.getState()==104&&!b_ready.gethelp())
				{
				if (b_ready.getstratDest().contains(x, y)) {//스타트버튼 하버
					b_ready.setonorOff(true);
				}else
					b_ready.setonorOff(false);
				}
				break;
			case 6:                      //랭크모드일경우
				if (b_rank.getfinbtnRect().contains(x, y)) { //끝내기 버튼 하버
					b_rank.setwhatbtnon(2);
				} else if (b_rank.getrebtnRect().contains(x, y)) {//재시작 버튼 하버
					b_rank.setwhatbtnon(1);
				}else {
					b_rank.setwhatbtnon(0);
				}
				break;
			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {//띄었을때 그 버튼이 선택되었을때
			int x = (int) event.getX();
			int y = (int) event.getY();
			switch(gameState.ordinal())
			{
			case 0:              //준비모드
				if(b_ready.gethelp()&&b_ready.gethSceneDest().contains(x, y))
				{
					b_ready.sethelp(false); //도움말 끄기
				}
				else if (!b_ready.gethelp()&&b_ready.getstratDest().contains(x, y)) //스타트 버튼 눌렀을 경우
				{
					if (!regame) {
						if (CokeGameActivity.g_BluetoothService.getState() == 104) { //블루투스가 연결되었는지 확인
							b_ready.setGamestart(1);
							SoundManager.getInstance().play(countM);
							CokeGameActivity.getACC();
						} else
							CokeGameActivity.connedevice();//연결되지 않았다면 연결
					}else                            //재시작일 경우 따로 연결하지 않음
					{
						b_ready.setGamestart(1); 
						SoundManager.getInstance().play(countM);
					}
				}else if(b_ready.gethelpDest().contains(x, y))//도움말 창띄우기
				{
					b_ready.sethelp(true);
				}
					break;
			case 6:
				if(b_rank.getrebtnRect().contains(x,y)) //재시작 버튼
					{
						regame();
					}else if(b_rank.getfinbtnRect().contains(x, y))//끄기
					{
						System.exit(0);
					}
					b_rank.setwhatbtnon(0);
				break;
			}
		}
		return true;
	}



	public void bitdraw(Canvas canvas) {//렌더링 함수
		if (canvas != null) {
			canvas.drawColor(Color.BLACK); //배경 검은색으로 하여 해상도가 다를 시 자연스레 블랙바 형성
			switch (gameState.ordinal()) {
			case 0:
			case 1:                      //인게임일 경우
				canvas.drawBitmap(back, resolutionStartSpot, 0, null);//배경그리기
				
				b_time.Draw(canvas);//시간그리기
				b_gauge.Draw(canvas);//게이지그리기
				b_hand.Draw(canvas);//손그리기
				if(resolutionStartSpot!=0)//해상도가 16대 9가 아닐때
				{
					canvas.drawBitmap(bar, 0, 0, null);//블랙바
				}
				if (b_ready.getGamestart()==0||b_ready.getGamestart()==1) {//게임 시작전일때
					b_ready.Draw(canvas);//준비화면 그리기
				}
				if(dontmove&&gameState.ordinal()==1)//기기가 흔들리지 않을때
				{
					Paint paint2 = new Paint();
					paint2.setTextSize((width + height) / 20);
					paint2.setTypeface(Typeface.create(Typeface.SANS_SERIF,
							Typeface.NORMAL));
					paint2.setTextAlign(Paint.Align.CENTER);
					paint2.setColor(Color.RED);
					canvas.drawText("흔들어 주세요! ", width / 2+resolutionStartSpot, (height-height / 4)
							+ paint2.getTextSize(), paint2);
				}
				break;
			case 2:
				b_pop.Draw(canvas);//폭파장면그리기
				break;
			case 3:
				b_scroll.Draw(canvas);//구름그리기
				b_rising.Draw(canvas);//콜라 상승그리기
				b_height.Draw(canvas);//높이 숫자그리기
				break;
			case 4:
				b_scroll.Draw(canvas);
				b_rising.Draw(canvas);
				b_height.Draw(canvas);
				if (b_rising.getDir() == false)//높이만큼 갔다면
					b_swipe.Draw(canvas);//내려오는 콜라그리기
				break;
			case 5:
			case 6:
				b_rank.Draw(canvas);//랭크화면그리기
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
						
						if (i % 2 == 0)//순위 번갈아가며 색 바꾸기
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
				if(b_swipe.getrankScene()!=2)//내려오는 콜라그리기
				b_swipe.Draw(canvas);
				break;
			}
		}
	}
	
	public void Update() { //업데이트 함수
		long gamet = System.currentTimeMillis();

		switch (gameState.ordinal()) {
		case 0:  //준비상태
			if (b_ready.getGamestart()==1) {
			b_ready.Update(gamet);
			}
			else if (b_ready.getGamestart()==2) {//게임이 시작하면
				SoundManager.getInstance().playmedia();//음악재생
				setGameState(gState.IN_GAME.ordinal());
				SoundManager.getInstance().stop(countM);//카운트 숫자 끄기
			}
			break;
		case 1:

			b_time.Update(gamet);//시간 업데이트
			b_gauge.Update();//게이지 업데이트

			if (b_time.getValue() <= 0) {//시간이 다되면
				setGameState(gState.POP_ANI.ordinal());
				SoundManager.getInstance().stopmedia();//배경음악끄기
			}
			break;
		case 2:

			b_pop.Update(gamet);
			b_height.setLimit(b_gauge.getgauge() + 300);//흔든 값만큼 높이 설정하기
			//높이만큼 점수평가하기
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
			if (b_pop.getcurframe() >= 6) {//폭파장면이 끝나면
				streamid = SoundManager.getInstance().playLoop(risingM);
				setGameState(gState.RISING_COKE.ordinal());
			}
			break;
		case 3:

			b_scroll.Update();
			b_rising.Update(gamet);
		
			if (b_height.getValue() >= b_height.getLimit()) { //높이숫자가 끝까지 갔으면
				if(!regame)
				SoundManager.getInstance().stop(risingM);
				else
				SoundManager.getInstance().strstop(streamid); //상승음악 멈추기
				setGameState(gState.DROP_COKE.ordinal());
			}
			else
				b_height.Update(gamet);
			break;
		case 4:
			b_rising.setDir(true);//콜라 방향바꿔 내려오게 하기
			b_rising.Update(gamet);
			if (b_rising.getDir() == false) {//상승하는 콜라가 다내려왔으면
				b_swipe.Update();
				if (!swipeStart) {
					SoundManager.getInstance().play(swipeM);//내려오는 콜라소리 켜기
					swipeStart = !swipeStart;
				}
			}
			if (b_swipe.getrankScene() == 1)//콜라가 화면을 다가렸으면
				setGameState(gState.RANK_SCENE.ordinal());
			break;
		case 5:
		case 6:

			if (b_helper.getonlyOneR() == 0) { //한번만 기록할 수 있게한다.
				SQLiteDatabase db = b_helper.getWritableDatabase();
				date = new Date();
				ContentValues row = new ContentValues();
				row.put("dat", dateFormat.format(date));
				row.put("score", b_gauge.getgauge() + 300);
				db.insertOrThrow("CokeRankBoard", null, row);//값넣기
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
		 if (!b_thread.isAlive()) {//스레드가 아직 시작되지 않았다면
			 b_thread.setRunning(true);//스레드 돌리기
			 b_thread.start();
		 }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		if(!b_thread.getPause())//일시정지가 아니라면 
		{
		boolean retry = true;
		b_thread.setRunning(false);
		while (retry) {
			try {
				b_thread.join();//스레드 종료
				retry = false;
			} catch (Exception e) {

			}
		}
		//-----------비트맵 메모리 해제
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
