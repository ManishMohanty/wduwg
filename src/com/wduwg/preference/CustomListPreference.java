package com.wduwg.preference;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CustomListPreference extends ListPreference{
	
	public CustomListPreference(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public CustomListPreference(Context context, AttributeSet attrs)
	{
		super(context , attrs);
	}

	 @Override
	 protected View onCreateView(ViewGroup parent) {
	     View view = super.onCreateView(parent);
	     Typeface font2 = Typeface.createFromAsset(view.getContext().getAssets(), "Fonts/Exo2.0-Regular.otf");
	     ((TextView) view.findViewById(android.R.id.title)).setTypeface(font2);
	     ((TextView) view.findViewById(android.R.id.title)).setTextSize(16);
	     ((TextView) view.findViewById(android.R.id.summary)).setTypeface(font2);
	     return view;
	 }
	
}
