package com.example.wduwg.tiles;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wduwg.tiles.R;
import com.loopj.android.image.SmartImageView;
import com.mw.wduwg.model.Business;

public class GridAdapter extends BaseAdapter {

	private Context context;
	List<Business> businessList;
	public Integer[] mThumbIds = {
            R.drawable.lights_color, R.drawable.lights_colors,
            R.drawable.splash_image, R.drawable.city_lights,
            R.drawable.city_lights, R.drawable.splash_image
           
    };
	public GridAdapter(Context context, List<Business> businessList)
	{
		this.context = context;
		this.businessList = businessList;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return businessList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		Business tempPlace = (Business)businessList.get(position);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 
			View gridView;
			
			if (convertView == null) {
				 
				gridView = new View(context);
	 
				// get layout from mobile.xml
				gridView = inflater.inflate(R.layout.singlegrid, null);
	 
				// set value into textview
				TextView businessName = (TextView) gridView
						.findViewById(R.id.businessName);
				businessName.setText(businessList.get(position).getName());
				
				TextView address = (TextView)gridView.findViewById(R.id.address);
				String completeAddress = businessList.get(position).getAddress();
				if(completeAddress.length() > 20)
				{
					completeAddress = completeAddress.substring(0, 20);
					completeAddress = completeAddress.substring(0,completeAddress.lastIndexOf(' ')) + "...";
					address.setText(completeAddress);
				}else
				{
				address.setText(businessList.get(position).getAddress());
				}
	 
//				// set image based on selected text
				SmartImageView imageView = (SmartImageView) gridView
						.findViewById(R.id.image);
//	            imageView.setImageResource(mThumbIds[4]);
	            
	            try{
	        		String googleApiResults = tempPlace.getGoogleAPIResult();
	        		JSONObject json = new JSONObject(googleApiResults);

	        		JSONObject photoJsonObject  = new JSONObject(json.getString("photos").substring(1, json.getString("photos").length()-1).toString());
	        		tempPlace.setImageUrl(photoJsonObject.getString("photo_reference"));
	        		System.out.println(">>>>>>> ImageUrl->"+tempPlace.getImageUrl());
	        		}catch(Exception e)
	        		{
	        			e.printStackTrace();
	        		}

	        		String temp = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=60&photoreference="
	        				+ tempPlace.getImageUrl()
	        				+ "&sensor=true&key=AIzaSyBG7NMHOOu50N3C96ZRmR2hgwAmx0KhddI";

	        		System.out.println(">>>>>>> ImageUrl->"+temp);

	        		imageView.setImageUrl(temp);
	 
			} else {
				gridView = (View) convertView;
			}
	 
			return gridView;
	}

}
