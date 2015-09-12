package com.yunji.activity;

import com.example.yunji.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ImageTextButton bt1 = (ImageTextButton)findViewById(R.id.bt1);
		bt1.setText("icon");
		bt1.setTextColor(Color.rgb(0, 0, 0));
		bt1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "bt1±»µã»÷ÁË", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
