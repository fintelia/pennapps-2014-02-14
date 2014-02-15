package com.jumpshot;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class Main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button submit=(Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent myIntent=new Intent(view.getContext(), CameraActivity.class );
                startActivityForResult(myIntent,0);

            }
        });
        Button acc = (Button)findViewById(R.id.acc);
        acc.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		Intent myIntent=new Intent(view.getContext(), AccActivity.class );
                startActivityForResult(myIntent,0);
        	}
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
