package com.capitalism.thenewboston;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Menu extends ListActivity{

	String classes[] = { "MainActivity", "TextPlay", 
			"ChessClock", "example3" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(Menu.this, android.R.layout.simple_list_item_1, classes)) ;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		String clicked_element = classes[position];
		try {
			Class ourClass = Class.forName("com.capitalism.thenewboston." + clicked_element);
			Intent ourIntent = new Intent(Menu.this, ourClass);
			startActivity(ourIntent);
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		};
	}

	
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater blowUp = getMenuInflater();
		blowUp.inflate(R.menu.clock_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		//case R.id.aboutUs:
		//	Intent i = new Intent("com.capitalism.thenewboston.ABOUT");
		//	startActivity(i);
		//	break;
		case R.id.preferences:
			Intent p = new Intent("com.capitalism.thenewboston.PREFERENCES");
			startActivity(p);
			break;
		//case R.id.exit:
		//	finish();
		//	break;
		}
		return false;
	}
}
