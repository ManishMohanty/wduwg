package com.example.wduwg.tiles;


import java.util.Timer;

import com.example.wduwg.tiles.R;
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
    static SchedulerFBPosts scheduleTask;
	
    private SharedPreferences preferences ;
    GlobalVariable globalVariable;
    
    static Timer timer;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delinkbtn);
        int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
        globalVariable =  (GlobalVariable)getApplicationContext();
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
        
        System.out.println(">>>>>>> onresume mEntryCount:"+mEntryCount);
        for (int i = 0; i < mEntryCount; i++) {
        	if(mPreferenceEntries[i] instanceof EditTextPreference)
        	{
//        		EditTextPreference currentPreference = (EditTextPreference) mPreferenceEntries[i];
//                if(mAutoSummaryFields[i].equals("prefPhone"))
//                {
//                	if(currentPreference.getText() != null)
//                	    mPreferenceEntries[i].setSummary(currentPreference.getText()+"   Click to change");	
//                	else
//                		mPreferenceEntries[i].setSummary("click to enter phone number");
//                } commented on 10 september to remove sms
//                else
//                {
//                	if(currentPreference.getText() != null)
//                	    mPreferenceEntries[i].setSummary(currentPreference.getText()+" Years"+"   Click to change");	
//                	else
//                		mPreferenceEntries[i].setSummary("click to enter Age");	
//                }
        	}else if (mPreferenceEntries[i] instanceof ListPreference) {
                  ListPreference currentPreference = (ListPreference) mPreferenceEntries[i];
                if(currentPreference.getEntry()!=null)
                {
                mPreferenceEntries[i].setSummary(currentPreference.getEntry()+"   Click to change");
                System.out.println(">>>>>>> drop down value:"+currentPreference.getEntry());
                }
                else
                {
                	mPreferenceEntries[i].setSummary("Click to select value");
                	System.out.println(">>>>>>> else of drop down");
                }
            }else if(mPreferenceEntries[i] instanceof SwitchPreference)
            {
            	SwitchPreference currenPreference = (SwitchPreference)mPreferenceEntries[i];
            }
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
                     EditTextPreference currentPreference = (EditTextPreference) mPreferenceEntries[i];
//                    if(key.equals("prefPhone"))
//                    {
//                    	if(currentPreference.getText() != null)
//                    	    mPreferenceEntries[i].setSummary(currentPreference.getText()+"   Click to change");	
//                    	else
//                    		mPreferenceEntries[i].setSummary("click to enter phone number");
//                    } commented on 10th september to remove sms
//                    else
//                    {
//                    	if(currentPreference.getText() != null)
//                    	    mPreferenceEntries[i].setSummary(currentPreference.getText()+" Years"+"   Click to change");	
//                    	else
//                    		mPreferenceEntries[i].setSummary("click to enter Age");	
//                    }
                }
                else if (mPreferenceEntries[i] instanceof ListPreference) {
                    final ListPreference currentPreference = (ListPreference) mPreferenceEntries[i];
                    if(currentPreference.getEntry()!=null)
                    mPreferenceEntries[i].setSummary(currentPreference.getEntry()+"   Click to change");
                    else
                    	mPreferenceEntries[i].setSummary("Click to select value");	
                    
//                    if(key.equals("prefFb_frequency") && preferences.getBoolean("facebookSwitch", false) == true && currentPreference.getEntry() != null)
                    if(key.equals("prefFb_frequency")  && currentPreference.getEntry() != null)
                    {
                    	String  postcontent = preferences.getString("prefFb_frequency", "");
                    	if(postcontent.equalsIgnoreCase("Men and Women"))
                    	{
//                    		globalVariable.fbPostOn(true);
                    		globalVariable.setMenWomen(true);
                    	}
                    	else{
                    		globalVariable.setMenWomen(false);
//                    		globalVariable.fbPostOn(false);
                    	}
                    }
                    else 
                    {
//                    	if(key.equals("prefNotificationFrequency"))
//                    	{
//                    		globalVariable.setMessage_frequency(Integer.parseInt(preferences.getString("prefNotificationFrequency", "0")));
//                    	}
                    }
                }
                else if(mPreferenceEntries[i] instanceof SwitchPreference)
                {
                	System.out.println(">>>>>> key:"+key);
                	final SwitchPreference currentPrefrence = (SwitchPreference) mPreferenceEntries[i];
                	if(key.equals("prefMessageSwitch") )
                	{
                	 status = (boolean) mPreferenceEntries[i].getSharedPreferences().getBoolean("prefMessageSwitch", false);
                	System.out.println(">>>>>> status of key:"+ status);
                	}
                	 else
                		status = (boolean)mPreferenceEntries[i].getSharedPreferences().getBoolean("facebookSwitch", false);
                	    mPreferenceEntries[i].setSummary(status?"On":"Off");
                	if(key.equalsIgnoreCase("facebookSwitch") && preferences.getBoolean("facebookSwitch", false) == true && preferences.contains("prefFb_frequency"))
                    {
                		System.out.println(">>>>>> key:"+key);
                		boolean  flag = preferences.getBoolean("facebookSwitch", false);
                		System.out.println(">>>>>>> facebook status:"+flag);
                    	String postcontent = preferences.getString("prefFb_frequency", "0");
                    	System.out.println(">>>>>> facebook posting on");
                    	System.out.println(">>>>>> post content"+postcontent);
                    	globalVariable.fbPostOff();
                		if(postcontent.equalsIgnoreCase("Men and Women"))
                        	{
                			  globalVariable.fbPostOn(true);
                        	}
                		else
                		{
                			globalVariable.fbPostOn(false);
                		}
                    } // else for facebook off.
                	else if(key.equals("facebookSwitch") && preferences.getBoolean("facebookSwitch", false) == false && scheduleTask != null && timer != null )
                	{
                		globalVariable.fbPostOff();
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
		globalVariable.setSelectedBusiness(null);
		globalVariable.setSelectedEvent(null);
		globalVariable.setMenIn(0);
		globalVariable.setMenOut(0);
		globalVariable.setWomenIn(0);
		globalVariable.setWomenOut(0);
		globalVariable.saveSharedPreferences();
		globalVariable.fbPostOff();
		Intent intent = new Intent(this,MainActivity.class);
		startActivity(intent);
		this.finish();
	}


}
