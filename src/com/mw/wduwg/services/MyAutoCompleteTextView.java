package com.mw.wduwg.services;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import com.parse.ParseObject;

public class MyAutoCompleteTextView extends AutoCompleteTextView {

	public MyAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/** Returns the country name corresponding to the selected item */
	@Override
	protected CharSequence convertSelectionToString(Object selectedItem) {
		ParseObject temp = (ParseObject) selectedItem;
		return temp.getString("name");
	}

	
	
	
}
