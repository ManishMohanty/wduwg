package com.example.wduwg.tiles;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Typeface;
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
	LayoutInflater inflater;
	Typeface typefaceBold,typefaceLight;
	boolean isCustomerBusiness ;
	
	public Integer[] mThumbIds = {
            R.drawable.lights_color, R.drawable.lights_colors,
            R.drawable.splash_image, R.drawable.city_lights,
            R.drawable.city_lights, R.drawable.splash_image
           
    };
	public GridAdapter(Context context, List<Business> businessList,boolean value)
	{
		this.context = context;
		this.businessList = businessList;
		this.isCustomerBusiness = value;
		typefaceBold = Typeface.createFromAsset(this.context.getAssets(), "Fonts/OpenSans-Bold.ttf");
		typefaceLight = Typeface.createFromAsset(this.context.getAssets(), "Fonts/OpenSans-Light.ttf");
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
		protected TextView status;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		System.out.println(">>>>>>> position :"+position);
		Business tempPlace = (Business)businessList.get(position);
			if (convertView == null) {
				 
				inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				viewHolder = new ViewHolder();
				
				
				convertView = inflater.inflate(R.layout.singlegrid, null);
	 
				viewHolder.businessName = (TextView) convertView
						.findViewById(R.id.businessName);
				
				viewHolder.address = (TextView)convertView.findViewById(R.id.address);
				viewHolder.imageView = (SmartImageView) convertView
						.findViewById(R.id.image);
				if(!tempPlace.isActivated() && !tempPlace.getName().equalsIgnoreCase("Add Business") && isCustomerBusiness)
				{
					viewHolder.status = (TextView)convertView.findViewById(R.id.status);
					viewHolder.status.setVisibility(View.VISIBLE);
				}
				viewHolder.businessName.setTypeface(typefaceBold);
				viewHolder.address.setTypeface(typefaceLight);
				
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
			if(businessList.get(position).getName().equalsIgnoreCase("Add Business"))
			{
				viewHolder.imageView.setImageUrl("http://us.123rf.com/400wm/400/400/nicemonkey/nicemonkey0703/nicemonkey070300014/782266-8-silhouette-business-people-in-line-in-black-and-white.jpg");
			}else
			{
				try{
					viewHolder.imageView.setImageUrl(businessList.get(position).getImageUrl());
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				viewHolder.imageView.setImageUrl(businessList.get(position).getImageUrl());
			}
			return convertView;
	}

}
