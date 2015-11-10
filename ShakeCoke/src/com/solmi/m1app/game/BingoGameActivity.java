package com.solmi.m1app.game;

import com.solmi.m1app.R;
import com.solmi.m1app.R.layout;
import com.solmi.m1app.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class BingoGameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bingo_game);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bingo_game, menu);
		return true;
	}

}
