package com.call.recoder;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

    ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ab = getSupportActionBar();
        ab.setTitle("Test");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        return super.onCreateOptionsMenu(menu);
    }
}