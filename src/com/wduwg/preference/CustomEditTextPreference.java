package com.wduwg.preference;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class CustomEditTextPreference  extends EditTextPreference{

	public CustomEditTextPreference(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public CustomEditTextPreference(Context context, AttributeSet attrs)
	{
		super(context , attrs);
	}
	
	public CustomEditTextPreference(Context context,AttributeSet attrs , int defStyle)
	{
		super(context,attrs,defStyle);
	}
	
	@Override
	 protected void onBindView(View view) {
	    super.onBindView(view);

	    TextView textView_title = (TextView) view.findViewById(android.R.id.title);
	    textView_title.setTextSize(16);
	    TextView textView_summary = (TextView) view.findViewById(android.R.id.summary);
	    Typeface myTypeface = Typeface.createFromAsset(textView_title.getContext().getAssets(),"Fonts/Exo2.0-Regular.otf");
	    if (textView_title != null) {
	    	textView_title.setTypeface(myTypeface);
	    	textView_summary.setTypeface(myTypeface);
	    }
	  }

}
