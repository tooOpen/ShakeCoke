package com.solmi.m1app;

import com.solmi.m1app.main.MainActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class AppStartActivity extends Activity {

	//global variable
	Context g_ctx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_start);
		g_ctx = this;
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable(){

			public void run() {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(g_ctx,MainActivity.class);				
				g_ctx.startActivity(intent);
				((Activity)g_ctx).finish();
			}					
		}, 3000);
		
		
        ImageView image = (ImageView)findViewById(R.id.logoIv);
        Animation anima = AnimationUtils.loadAnimation(this, R.anim.alpha);
        image.startAnimation(anima);
        
		
	}

}
