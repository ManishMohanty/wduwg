package com.mw.wduwg.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.apphance.android.Log;

import com.mw.wduwg.fragments.ReportFragment1;
import com.mw.wduwg.fragments.ReportFragment2;
import com.mw.wduwg.fragments.ReportFragment3;
import com.mw.wduwg.fragments.ReportFragment4;
import com.wduwg.counter.app.ReportActualActvivity;
 
public class TabsPagerAdapter extends FragmentPagerAdapter {
	public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
        	Log.d("case 0","index: "+index);
        	return new ReportFragment1();
            
        case 1:
        	Log.d("case 1","index: "+index);
//            return new ReportFragment2(ReportActualActvivity.tabs[index]);
        	return new ReportFragment2();
        case 2:
        	Log.d("case 2","index: "+index);
//        	return new ReportFragment2(ReportActualActvivity.tabs[index]);
        	return new ReportFragment3();
        	
        case 3:
        	Log.d("case 3","index: "+index);
//        	return new ReportFragment2(ReportActualActvivity.tabs[index]);
        	return new ReportFragment4();
        }
    	
    	
        return null;
    }
    	
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return ReportActualActvivity.tabs.length;
    }

	
}
