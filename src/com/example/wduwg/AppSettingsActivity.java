package com.example.wduwg;


import java.util.Timer;

import com.mw.wduwg.services.GlobalVariable;
import com.mw.wduwg.services.SchedulerCount;
import com.mw.wduwg.services.SchedulerFBPosts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppSettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
	private final String[] mAutoSummaryFields = { "prefMinage", "facebookSwitch", "prefFb_frequency","prefMessageSwitch","prefNotificationFrequency","prefPhone","businessSwitch" }; // change here
    private final int mEntryCount = mAutoSummaryFields.length;
    private Preference[] mPreferenceEntries;
    SchedulerFBPosts scheduleTask;
	
    private SharedPreferences preferences ;
    
    private Timer timer=null;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        setContentView(R.layout.delinkbtn);
        int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		TextView yourTextView = (TextView) findViewById(titleId);
		yourTextView.setTextColor(Color.parseColor("#016AB2"));
		yourTextView.setTextSize(19);
		yourTextView.setTypeface(Typeface
				.createFromAsset(getAssets(), "Fonts/OpenSans-Light.ttf"));
        addPreferencesFromResource(R.xml.settings);
        mPreferenceEntries = new Preference[mEntryCount];
        for (int i = 0; i < mEntryCount; i++) {
            mPreferenceEntries[i] = getPreferenceScreen().findPreference(mAutoSummaryFields[i]);
        }
    }
	
	
	@SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < mEntryCount; i++) {
            updateSummary(mAutoSummaryFields[i]); // initialization
        }
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this); // register change listener
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this); // unregister change listener
    }

    private void updateSummary(String key) {
    	boolean status;
    	preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        for (int i = 0; i < mEntryCount; i++) {
            if (key.equals(mAutoSummaryFields[i])) {
                if (mPreferenceEntries[i] instanceof EditTextPreference) {
                    final EditTextPreference currentPreference = (EditTextPreference) mPreferenceEntries[i];
                    if(key.equals("prefPhone"))
                    {
                    	if(currentPreference.getText() != null)
                    	    mPreferenceEntries[i].setSummary(currentPreference.getText()+"   Click to change");	
                    	else
                    		mPreferenceEntries[i].setSummary("click to enter phone number");
                    }
                    else
                    {
                    	if(currentPreference.getText() != null)
                    	    mPreferenceEntries[i].setSummary(currentPreference.getText()+" Years"+"   Click to change");	
                    	else
                    		mPreferenceEntries[i].setSummary("click to enter Age");	
                    }
                }
                else if (mPreferenceEntries[i] instanceof ListPreference) {
                    final ListPreference currentPreference = (ListPreference) mPreferenceEntries[i];
                    if(currentPreference.getEntry()!=null)
                    mPreferenceEntries[i].setSummary(currentPreference.getEntry()+"   Click to change");
                    else
                    	mPreferenceEntries[i].setSummary("Click to select value");	
                    
                    if(key.equals("prefFb_frequency") && preferences.getBoolean("facebookSwitch", false) == true && currentPreference.getEntry() != null)
                    {
                    	System.out.println(">>>>>>> AppSettings");
                    	if(timer != null)
                    	{
                    		timer.cancel();
                   		 scheduleTask = null;
                   		 timer = null;
                    	}
                    	timer = new Timer();
                    	int minutes = Integer.parseInt(preferences.getString("prefFb_frequency", ""));
                    	scheduleTask = new SchedulerFBPosts(AppSettingsActivity.this);
                    	timer.scheduleAtFixedRate(scheduleTask, 1000, minutes*60*1000);
                    }
                }
                else if(mPreferenceEntries[i] instanceof SwitchPreference)
                {
                	final SwitchPreference currentPrefrence = (SwitchPreference) mPreferenceEntries[i];
                	if(key.equals("prefMessageSwitch") )
                	 status = (boolean) mPreferenceEntries[i].getSharedPreferences().getBoolean("prefMessageSwitch", false);
                	else
                		status = (boolean)mPreferenceEntries[i].getSharedPreferences().getBoolean("facebookSwitch", false);
//                	mPreferenceEntries[i].setSummary(status?"On":"Off");
                	if(key.equals("facebookSwitch") && preferences.getBoolean("facebookSwitch", false) == true && preferences.contains("prefFb_frequency"))
                    {
                		if(timer != null)
                		{
                		 timer.cancel();
                		 scheduleTask = null;
                		 timer = null;
                		 
                		}
                		timer = new Timer();
                    	int minutes = Integer.parseInt(preferences.getString("prefFb_frequency", "0"));
                    	scheduleTask = new SchedulerFBPosts(AppSettingsActivity.this);
                    	timer.scheduleAtFixedRate(scheduleTask, 1000, minutes*60*1000);
                    }
                }
                break;
            }
        }
    }



	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		updateSummary(key);
	}
	
	
	@Override
	public void onBackPressed() {
		this.setResult(RESULT_OK);
		finish();
	}
	
	public void delink(View v)
	{
		GlobalVariable globalVariable = (GlobalVariable) getApplicationContext();
		SchedulerCount scheduledTask = new SchedulerCount(this);
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(scheduledTask, 1000, 10000);
		scheduledTask.run();
		SchedulerCount.event = globalVariable.getSelectedEvent();
		timer.cancel();
		globalVariable.setSelectedBusiness(null);
		globalVariable.setSelectedEvent(null);
		globalVariable.setMenIn(0);
		globalVariable.setMenOut(0);
		globalVariable.setWomenIn(0);
		globalVariable.setWomenOut(0);
		globalVariable.saveSharedPreferences();
		Intent intent = new Intent(this,MainActivity.class);
		startActivity(intent);
		this.finish();
	}


}
