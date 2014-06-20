package com.example.wduwg;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mw.wduwg.adapter.EventAdapter2;
import com.mw.wduwg.adapter.SpecialApater;
import com.mw.wduwg.model.Event;
import com.mw.wduwg.model.Special;
import com.mw.wduwg.services.GlobalVariable;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;


public class BusinessDashboardActivity extends Activity {

	
	Typeface typefaceBold;
	GlobalVariable globalVariable;
	List<Event> eventList ;
	List<Special> specialList;
	ListView eventListView, SpecialListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business_dashboard);
		eventListView = (ListView) findViewById(R.id.evntlistView);
		SpecialListView = (ListView)findViewById(R.id.speciallistView);
		globalVariable = (GlobalVariable)getApplicationContext();
		typefaceBold = Typeface.createFromAsset(getAssets(), "Fonts/OpenSans-Bold.ttf");
		eventList = new ArrayList<Event>();
		specialList = new ArrayList<Special>();
		Event e1 = new Event();
		e1.setName("Dinner");
		e1.setDescription("starts From - 20-june-2014");
		Event e2 = new Event();
		e2.setName("Lunch");
		e2.setDescription("starts From - 20-june-2014");
		
//		eventList.add(globalVariable.getSelectedBusiness().getEventList().get(0));
//		eventList.add(globalVariable.getSelectedBusiness().getEventList().get(1));
		eventList.add(e1);
		eventList.add(e2);
		Special s1 = new Special();
		s1.setName("Bhindy Fry");
		s1.setDescription("23-06-2014");
		
		Special s2 = new Special();
		s2.setName("Dal Tadka");
		s2.setDescription("23-06-2014");
		
		specialList.add(s1);
		specialList.add(s2);
		EventAdapter2 eventAdapter = new EventAdapter2(
				BusinessDashboardActivity.this, eventList);
		SpecialApater specialAdApater = new SpecialApater(BusinessDashboardActivity.this, specialList);
		eventListView.setAdapter(eventAdapter);
		SpecialListView.setAdapter(specialAdApater);
	}

	
}
