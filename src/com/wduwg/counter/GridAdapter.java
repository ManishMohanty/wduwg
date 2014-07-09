package com.wduwg.counter;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.mw.wduwg.model.Business;

public class GridAdapter extends BaseAdapter{
	private Context context;
	List<Business> businessList;
	
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

	
	static class ViewHolder {
		protected SmartImageView imageView;
		protected TextView businessName;
		protected TextView address;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Business tempPlace = (Business)businessList.get(position);
		final ViewHolder viewHolder;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View gridView;

			if (convertView == null) {

				gridView = new View(context);
				viewHolder = new ViewHolder();
				// get layout from mobile.xml
				convertView = inflater.inflate(R.layout.singlegrid,null);

				// set value into textview
				viewHolder.businessName = (TextView) convertView
						.findViewById(R.id.businessName);
				viewHolder.address = (TextView)convertView.findViewById(R.id.address);
				viewHolder.imageView = (SmartImageView) convertView
						.findViewById(R.id.image);
                convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.businessName.setText(businessList.get(position).getName());
			String completeAddress = businessList.get(position).getAddress();
			if(completeAddress.length() > 20)
			{
				completeAddress = completeAddress.substring(0, 20);
				completeAddress = completeAddress.substring(0,completeAddress.lastIndexOf(' ')) + "...";
				viewHolder.address.setText(completeAddress);
			}else
			{
				viewHolder.address.setText(businessList.get(position).getAddress());
			}
			
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
			
			viewHolder.imageView.setImageUrl(temp);

			return convertView;
	}
	

}
