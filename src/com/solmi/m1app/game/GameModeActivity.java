package com.solmi.m1app.game;

import com.solmi.m1app.R;
import com.solmi.m1app.R.layout;
import com.solmi.m1app.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class GameModeActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_mode);
		initButtons();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game_mode, menu);
		return true;
	}
	
	private void initButtons(){
		LinearLayout lBtn = (LinearLayout)findViewById(R.id.btn_layout_game_sample);
		lBtn.setOnClickListener(this);
		lBtn = (LinearLayout)findViewById(R.id.btn_layout_game_bingo);
		lBtn.setOnClickListener(this);
		lBtn = (LinearLayout)findViewById(R.id.btn_layout_game_tower);
		lBtn.setOnClickListener(this);
		lBtn = (LinearLayout)findViewById(R.id.btn_layout_game_color);
		lBtn.setOnClickListener(this);
		lBtn = (LinearLayout)findViewById(R.id.btn_layout_game_coke);
		lBtn.setOnClickListener(this);
		lBtn = (LinearLayout)findViewById(R.id.btn_layout_game_runner);
		lBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(v.getId()){
		case R.id.btn_layout_game_sample:
			intent = new Intent(this, GameActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_layout_game_bingo:
			intent = new Intent(this, BingoGameActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_layout_game_tower:
			intent = new Intent(this, TowerGameActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_layout_game_color:
			intent = new Intent(this, ColorGameActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_layout_game_coke:
			intent = new Intent(this, CokeGameActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_layout_game_runner:
			intent = new Intent(this, RunnerGameActivity.class);
			startActivity(intent);
			break;
		}
		
	}
	
	

}
