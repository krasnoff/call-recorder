package com.call.recorder;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class TermsActivity extends ActionBarActivity {

	public TextView mTextView;
	ActionBar ab;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terms_layout);
		ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        
		
		mTextView = (TextView) findViewById(R.id.txtTerms2);
		
		try
        {
			mTextView.setText(MainActivity.getDataFromRawFiles(R.raw.terms));
        }
        catch(IOException e)
        {
        	
        }
	}
	
	
	
}
