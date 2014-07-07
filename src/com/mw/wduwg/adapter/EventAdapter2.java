package com.mw.wduwg.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wduwg.tiles.R;
import com.loopj.android.image.SmartImageView;
import com.mw.wduwg.model.Event;
import com.mw.wduwg.services.GlobalVariable;
import com.parse.ParseObject;

public class EventAdapter2 extends BaseAdapter {
	Context context;
	List<Event> listEvents;

	LayoutInflater inflater;
    Typeface typeface;
	public EventAdapter2(Context context, List<Event> listEvents) {
		super();
		this.context = context;
		this.listEvents = listEvents;
		typeface = Typeface.createFromAsset(context.getAssets(), "Fonts/OpenSans-Bold.ttf");
	}

	static class ViewHolder {
		protected TextView nameTV;
//		protected TextView descTV;
		protected SmartImageView iconIV;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("getView" + position);
		ViewHolder viewHolder;
		if (convertView == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.griditem,null);
			viewHolder.nameTV = (TextView) convertView.findViewById(R.id.businessName);
//			viewHolder.descTV = (TextView) convertView.findViewById(R.id.address);
			viewHolder.iconIV = (SmartImageView) convertView.findViewById(R.id.image);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Event tempEvent = listEvents.get(position);
		viewHolder.nameTV.setTypeface(typeface);
		viewHolder.nameTV.setText(tempEvent.getName());
//		viewHolder.descTV.setTypeface(Typeface.createFromAsset(context.getAssets(), "Fonts/OpenSans-Light.ttf"));
//		viewHolder.descTV.setText(tempEvent.getDescription());
		viewHolder.iconIV.setImageUrl(tempEvent.getImageUrl());

		return convertView;
	}

	@Override
	public int getCount() {
		return listEvents.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
}
