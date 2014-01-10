package com.call.recoder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends ActionBarActivity {

    ActionBar ab;
    
    public static final String FILE_DIRECTORY = "recordedCalls";
	public ListView listView;
	//public ScrollView mScrollView;
	public ScrollView mScrollView2;
	public TextView mTextView;
	public static final String LISTEN_ENABLED = "ListenEnabled";
	private static final int CATEGORY_DETAIL = 1;
	private static final int NO_MEMORY_CARD = 2;
	private static final int TERMS = 3;
	
    public RadioButton radEnable;
    public RadioButton radDisable;
        
    public static final int MEDIA_MOUNTED = 0;
    public static final int MEDIA_MOUNTED_READ_ONLY = 1;
    public static final int NO_MEDIA = 2;
    
    private static Resources res;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ab = getSupportActionBar();
        
        
        res = getResources();
        
        listView = (ListView) findViewById(R.id.mylist);
        //mScrollView = (ScrollView) findViewById(R.id.ScrollView01);
        mScrollView2 = (ScrollView) findViewById(R.id.ScrollView02);
        mTextView = (TextView) findViewById(R.id.txtNoRecords);
        
        SharedPreferences settings = this.getSharedPreferences(LISTEN_ENABLED, 0);
        boolean silent = settings.getBoolean("silentMode", false);
        
        if (!silent)
        	showDialog(CATEGORY_DETAIL);
        
        context = this.getBaseContext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
	protected void onResume() {
		if (updateExternalStorageState() == MEDIA_MOUNTED) {
	    	String filepath = Environment.getExternalStorageDirectory().getPath();
	    	final File file = new File(filepath, FILE_DIRECTORY);
					
			if (!file.exists()) {
				file.mkdirs();
			}
			
			final List<Model> listDir = ListDir2(file);
			
			if (listDir.isEmpty())
			{
				mScrollView2.setVisibility(TextView.VISIBLE);
				listView.setVisibility(ScrollView.GONE);
			}
			else
			{
				mScrollView2.setVisibility(TextView.GONE);
				listView.setVisibility(ScrollView.VISIBLE);
			}
			
			final MyCallsAdapter adapter = new MyCallsAdapter(this, listDir);
	    	
			listView.setOnItemClickListener(new OnItemClickListener() {
	
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					adapter.showPromotionPieceDialog(listDir.get(position)
							.getCallName(), position);
				}
			});
			
			adapter.sort(new Comparator<Model>() {
	
				public int compare(Model arg0, Model arg1) {
					Long date1 = Long.valueOf(arg0.getCallName().substring(1, 15));
					Long date2 = Long.valueOf(arg1.getCallName().substring(1, 15));
					return (date1 > date2 ? -1 : (date1 == date2 ? 0 : 1));
				}
	
			});
	    	
			listView.setAdapter(adapter);
			
    	}
    	else if (updateExternalStorageState() == MEDIA_MOUNTED_READ_ONLY) {
    		mScrollView2.setVisibility(TextView.VISIBLE);
    		listView.setVisibility(ScrollView.GONE);
    		showDialog(NO_MEMORY_CARD);
        } else {
        	mScrollView2.setVisibility(TextView.VISIBLE);
        	listView.setVisibility(ScrollView.GONE);
        	showDialog(NO_MEMORY_CARD);
        }
		
		super.onResume();
	}

	/**
	 * checks if an external memory card is available
	 * 
	 * @return
	 */
	public static int updateExternalStorageState() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return MEDIA_MOUNTED;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return MEDIA_MOUNTED_READ_ONLY;
		} else {
			return NO_MEDIA;
		}

	}
	
	/**
	 * Fetches list of previous recordings
	 * 
	 * @param f
	 * @return
	 */
	private List<Model> ListDir2(File f) {
		File[] files = f.listFiles();
		List<Model> fileList = new ArrayList<Model>();
		for (File file : files) {
			Model mModel = new Model(file.getName());
			String phonenum = mModel.getCallName().substring(16,
					mModel.getCallName().length() - 4);
			mModel.setUserNameFromContact(getContactName(phonenum));
			fileList.add(mModel);
		}

		Collections.sort(fileList);
		Collections.sort(fileList, Collections.reverseOrder());

		return fileList;
	}
	
	/**
	 * Obtains the contact list for the currently selected account.
	 * 
	 * @return A cursor for for accessing the contact list.
	 */
	private String getContactName(String phoneNum) {
		String res = phoneNum;
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER };
		String selection = null;// =
								// ContactsContract.CommonDataKinds.Phone.NUMBER
								// + " = ?";
		String[] selectionArgs = null;// = new String[] { "1111111" };
		Cursor names = getContentResolver().query(uri, projection, selection,
				selectionArgs, null);

		int indexName = names
				.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
		int indexNumber = names
				.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
		if (names.getCount() > 0) {
			names.moveToFirst();
			do {
				String name = names.getString(indexName);
				String number = names.getString(indexNumber)
						.replaceAll("-", "");

				if (number.compareTo(phoneNum) == 0) {
					res = name;
					break;
				}

			} while (names.moveToNext());
		}

		return res;
	}
	
	public static String getDataFromRawFiles(int id) throws IOException 
    {
    	InputStream in_s = res.openRawResource(id);

        byte[] b = new byte[in_s.available()];
        in_s.read(b);
    	String value = new String(b);
    	
    	return value;
    }
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
    	SharedPreferences settings = this.getSharedPreferences(LISTEN_ENABLED, 0);
		boolean silent = settings.getBoolean("silentMode", true);
		
		MenuItem menuDisableRecord = menu.findItem(R.id.menu_Disable_record);
		MenuItem menuEnableRecord = menu.findItem(R.id.menu_Enable_record);
		if (silent)
		{
			menuDisableRecord.setEnabled(true);
			menuEnableRecord.setEnabled(false);
		}
		else
		{
			menuDisableRecord.setEnabled(false);
			menuEnableRecord.setEnabled(true);
		}
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		Toast toast;
		switch (item.getItemId()) {
            case R.id.menu_about:
            	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            	builder.setTitle(R.string.about_title)
            	.setMessage(R.string.about_content)
            	.setPositiveButton(R.string.about_close_button, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        				dialog.cancel();
        			}
        		})
        		.show();
            	break;
            case R.id.menu_Disable_record:
            	setSharedPreferences(false);
            	toast = Toast.makeText(this, this.getString(R.string.menu_record_is_now_disabled), Toast.LENGTH_SHORT);
		    	toast.show();
            	break;
            case R.id.menu_Enable_record:
            	setSharedPreferences(true);
            	//activateNotification();
            	toast = Toast.makeText(this, this.getString(R.string.menu_record_is_now_enabled), Toast.LENGTH_SHORT);
		    	toast.show();
            	break;
            case R.id.menu_see_terms:
            	Intent i = new Intent(this.getBaseContext(), TermsActivity.class);
        		startActivity(i);
            	break;
            case R.id.menu_privacy_policy:
            	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.privacychoice.org/policy/mobile?policy=306ef01761f300e3c30ccfc534babf6b"));
            	startActivity(browserIntent);
            	break;
            case R.id.menu_delete_all:
            	AlertDialog.Builder builderDelete = new AlertDialog.Builder(MainActivity.this);
            	builderDelete.setTitle(R.string.dialog_delete_all_title)
            	.setMessage(R.string.dialog_delete_all_content)
            	.setPositiveButton(R.string.dialog_delete_all_yes, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        				deleteAllRecords();
        				dialog.cancel();
        			}
        		})
            	.setNegativeButton(R.string.dialog_delete_all_no, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        				dialog.cancel();
        			}
        		})
        		.show();
            	break;
            default:
            	break;
        }
        return super.onOptionsItemSelected(item);
    }
	
	private void deleteAllRecords()
	{
		String filepath = Environment.getExternalStorageDirectory().getPath() + "/" + FILE_DIRECTORY;
		File file = new File(filepath);
		
		String listOfFileNames[] = file.list();
		
		for (int i = 0; i<listOfFileNames.length; i++)
		{
			File file2 = new File(filepath, listOfFileNames[i]);
			if (file2.exists()) {
				file2.delete();
			}
		}
		onResume();
	}
	
	private void setSharedPreferences(boolean settingsValue)
	{
		SharedPreferences settings = this.getSharedPreferences(LISTEN_ENABLED, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("silentMode", settingsValue);
		editor.commit();
	}
}