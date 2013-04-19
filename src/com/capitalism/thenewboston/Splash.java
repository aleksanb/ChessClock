package com.capitalism.thenewboston;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;

public class Splash extends Activity {
	MediaPlayer ourSong;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		ourSong = MediaPlayer.create(Splash.this, R.raw.turretstuckintube01);
		
		SharedPreferences getPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		boolean music = getPreferences.getBoolean("checkbox", true);
		if (music) {
			ourSong.start();
		}
			
		Thread timer = new Thread(){
			public void run(){
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					Intent openStartingPoint = new Intent("com.capitalism.thenewboston.Menu");
					startActivity(openStartingPoint);
				}
			}
		};
		timer.start();
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		ourSong.release();
		finish();
	}

}
