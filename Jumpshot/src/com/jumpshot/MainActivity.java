package com.jumpshot;

import java.util.ArrayList;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.drm.DrmStore.Action;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity{
	
	protected void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setCustomView(R.layout.action_bar_custom);
		actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        
        ArrayList<String> items = new ArrayList<String>();
        items.add("test");
        items.add("test2");
        
        ListView list=(ListView)findViewById(R.id.listView1);
        
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        list.setAdapter(listAdapter);
        list.setOnItemClickListener(new OnItemClickListener() {
       	 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            	// Expand			
            }
        });
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        return super.onCreateOptionsMenu(menu);
	}
	
	
	Context c = MyApplication.getAppContext();
	
	OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(c);
	    public void onSwipeRight() {
	        Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
	    }
	    public void onSwipeLeft() {
	        Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
	    };
	
}

