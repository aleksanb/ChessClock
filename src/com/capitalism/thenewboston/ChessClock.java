package com.capitalism.thenewboston;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChessClock extends Activity {
	
	RelativeLayout rl_body;
	TextView tv_white;
	TextView tv_black;
	TextView tv_status;
	boolean white_turn;
	boolean started;
	boolean paused;
	CountDownTimer timer_white;
	CountDownTimer timer_black;
	ProgressBar pb_white;
	ProgressBar pb_black;
	int time_white;
	int time_black;
	long white_time_left;
	long black_time_left;
	int bonus_seconds;
	int pb;
	int pw;
	MediaPlayer start_sound;
	MediaPlayer click_sound;
	MediaPlayer win_sound;
	//MediaPlayer pause_sound;
	
	//Preferences
	SharedPreferences getPreferences;
	//Bools from preferences
	boolean usingBonusSeconds;
	boolean addBonusSecondsAfterTurn;
	
	private void importValuesFromPreferences() {
		getPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		boolean sameSettings = getPreferences.getBoolean("sameSettings", true);
		int hours_white = Integer.valueOf(getPreferences.getString("hours_white", "0"));
		int minutes_white = Integer.valueOf(getPreferences.getString("minutes_white", "5"));
		int seconds_white = Integer.valueOf(getPreferences.getString("seconds_white", "0"));
		int hours_black = Integer.valueOf(getPreferences.getString("hours_black", "0"));
		int minutes_black = Integer.valueOf(getPreferences.getString("minutes_black", "5"));
		int seconds_black = Integer.valueOf(getPreferences.getString("seconds_black", "0"));
		
		time_white = (hours_white*60*60+minutes_white*60+seconds_white)*1000;
		if (sameSettings) {
			time_black = time_white;
		} else {
			time_black = (hours_black*60*60+minutes_black*60+seconds_black)*1000;
		}
		
		usingBonusSeconds = getPreferences.getBoolean("usingBonusSeconds", false);
		if (usingBonusSeconds) {
			bonus_seconds = Integer.valueOf(getPreferences.getString("bonusSeconds", "3"))*1000;
		} else {
			bonus_seconds = 0;
		}
		addBonusSecondsAfterTurn = getPreferences.getBoolean("addBonusSecondsAfterTurn", true);
		
		// Set initial clock values
		tv_white.setText(generate_timestring(time_white));
        tv_black.setText(generate_timestring(time_black));
        pb_white.setProgress(100);
        pb_black.setProgress(100);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);     
		setContentView(R.layout.clock);
		
		//connect the xml to code
		tv_white = (TextView) findViewById(R.id.tvWhite);
		tv_black = (TextView) findViewById(R.id.tvBlack);
		tv_status = (TextView) findViewById(R.id.tvStatus);
		pb_white = (ProgressBar) findViewById(R.id.pbWhite);
		pb_black = (ProgressBar) findViewById(R.id.pbBlack);
		rl_body = (RelativeLayout) findViewById(R.id.rlBody);
		
		//import times from settings goes here at some point
		importValuesFromPreferences();
		
		//game logic values
		white_turn = true; // true means white is counting down
		started = false;
		paused = false;
		white_time_left = time_white;
		black_time_left = time_black;
		
		// Set initial clock values
        tv_status.setText("Ready");
        
        //set sounds
        start_sound = MediaPlayer.create(ChessClock.this, R.raw.announcer_capture_intel);
        click_sound = MediaPlayer.create(ChessClock.this, R.raw.ping);
        win_sound = MediaPlayer.create(ChessClock.this,  R.raw.announcer_am_flawlessvictory02);
        //pause_sound = MediaPlayer.create(ChessClock.this, R.raw.turretlaunched05);
        
        //longclick listener for pause, stop, menu
        rl_body.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				pauseStopMenu();
				return true;
			}
		});
	}
	
	private void pauseStopMenu() {
		if (paused) { // Reset the program
			tv_status.setText("Ready");
			//stop remaining timers
			try {
				timer_black.cancel();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				timer_white.cancel();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//reset values
			black_time_left = time_black;
			white_time_left = time_white;
			started = false;
			white_turn = true;
			paused = false;
			// Set initial clock values
			tv_white.setText(generate_timestring(time_white));
	        tv_black.setText(generate_timestring(time_black));
	        update_white_bar();
	        update_black_bar();
		} else if (!paused && started) {
			//pause game
			//pause_sound.start();
			paused = true;
			tv_status.setText("Paused");
			//stop remaining timers
			try {
				timer_black.cancel();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				timer_white.cancel();
			} catch (Exception e) {
				e.printStackTrace();
			}	
		} else {
			Intent p = new Intent("com.capitalism.thenewboston.PREFERENCES");
			startActivity(p);
		}
	}
	
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		importValuesFromPreferences();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onStop();
		if (!paused && started) {
			//pause game
			//pause_sound.start();
			paused = true;
			tv_status.setText("Paused");
			//stop remaining timers
			try {
				timer_black.cancel();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				timer_white.cancel();
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
	}	
	private String generate_timestring(int millis) {
		int s = (int) (millis / 1000) % 60 ;
	   	int m = (int) ((millis / (1000*60)) % 60);
	   	int h = (int) ((millis / (1000*60*60)) % 24);
	   	
	   	String s_s = (s < 10)? "0"+s:""+s;
	   	String s_m = (m < 10)? "0"+m:""+m;
	   	String s_h = (h < 10)? "0"+h:""+h;
	   	
	   	if (h == 0 && m == 0) {
	   		return Integer.toString(s);
	   	} else if (h == 0) {
	   		return (m < 10)? m+":"+s_s: s_m+":"+s_s;
	   	} else {
	   		return (h < 10)? h+":"+s_m+":"+s_s:s_h+":"+s_m+":"+s_s;
	   	}
	}
	private void update_white_bar() {
		pw = (white_time_left >= time_white)? 100:(int)(white_time_left * 100 / time_white);
		pb_white.setProgress(pw);
	}	
	private void update_black_bar() {
		pb = (black_time_left >= time_black)? 100:(int)(black_time_left * 100 / time_black);
		pb_black.setProgress(pb);
	}
	public void screenClicked(View v) {
		if(tv_white == null || tv_black == null) return;
		if (paused) {
			click_sound.start();
			tv_status.setText("Playing");
			if (white_turn) {
				create_white_timer((int) white_time_left);
				timer_white.start();
			} else {
				create_black_timer((int) black_time_left);
				timer_black.start();
			}
			//resume from paused state
			paused = false;
		} else if (started) {
			tv_status.setText("Playing");
			toggle();
		} else {
			start_sound.start();
			tv_status.setText("Playing");
			//Reset game, initialize values
			//Set clock values
		    tv_white.setText(generate_timestring(time_white));
		    tv_black.setText(generate_timestring(time_black));
			white_time_left = time_white;
			black_time_left = time_black;
	        update_white_bar();
	        update_black_bar();
			
			//game logic values
			white_time_left = time_white;
			black_time_left = time_black;
			started = true;
			white_turn = true;
			
		    //Create white timer
			create_white_timer(time_white);
			timer_white.start();
		}
	}
	private void toggle() {
		click_sound.start();
		if (white_turn) {
			timer_white.cancel();
			if (addBonusSecondsAfterTurn) {
				white_time_left += bonus_seconds;
			} else {
				black_time_left += bonus_seconds;
			}
			tv_white.setText(generate_timestring((int)white_time_left));
			
			create_black_timer((int) black_time_left);
			timer_black.start();
			
			white_turn = false;
			update_white_bar();
		} else {
			timer_black.cancel();
			if (addBonusSecondsAfterTurn) {
				black_time_left += bonus_seconds;				
			} else {
				white_time_left += bonus_seconds;
			}
			tv_black.setText(generate_timestring((int)black_time_left));
			
			create_white_timer((int) white_time_left);
			timer_white.start();
			
			white_turn = true;
			update_black_bar();
		}
	}
	private void create_white_timer(int time_white) {
		timer_white = new CountDownTimer(time_white, 100)  {
		     public void onTick(long millisUntilFinished) {
		    	 white_time_left = millisUntilFinished;
		         tv_white.setText(generate_timestring((int) millisUntilFinished));
		         update_white_bar();
		     }
		     public void onFinish() {
		         handle_countdown_finish(false);
		     }
		     
		};
	}
	private void create_black_timer(int time_black) {
		timer_black = new CountDownTimer(time_black, 100)  {
		     public void onTick(long millisUntilFinished) {
		    	 black_time_left = millisUntilFinished;
		         tv_black.setText(generate_timestring((int) millisUntilFinished));
		         update_black_bar();
		     }
		     public void onFinish() {
		    	 handle_countdown_finish(true);
		     }
		};
	}
	private void handle_countdown_finish(boolean white_won) {
		//stop remaining timers
		try {
			timer_black.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			timer_white.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//display victory/loss
		if (white_won) {
			tv_status.setText("White wins!");
		} else {
			tv_status.setText("Black wins!");
		}
		//play victory tune
		win_sound.start();
		//reset values
		black_time_left = time_black;
		white_time_left = time_white;
		started = false;
		white_turn = true;
		paused = false;
	}
	
}
