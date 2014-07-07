package com.mw.wduwg.adapter;

import java.util.List;

import com.apphance.android.Log;
import com.example.wduwg.tiles.R;
import com.loopj.android.image.SmartImageView;
import com.mw.wduwg.model.Business;
import com.mw.wduwg.model.Special;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SpecialApater extends BaseAdapter {

	Context context;
	List<Special> specialList;
	LayoutInflater inflater;
	Typeface typeface;

	public SpecialApater(Context context, List<Special> items) {
		super();
		this.context = context;
		this.specialList = items;
		Log.d("== special size:", "" + this.specialList.size());
		typeface = Typeface.createFromAsset(context.getAssets(),
				"Fonts/OpenSans-Bold.ttf");
	}

	@Override
	public int getCount() {
		return specialList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		System.out.println("=======================insise special adapter");
		Special special = specialList.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.griditem, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.businessName);
//			viewHolder.description = (TextView) convertView
//					.findViewById(R.id.address);
			viewHolder.image = (SmartImageView) convertView
					.findViewById(R.id.image);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.name.setTypeface(typeface);
		viewHolder.name.setText(special.getName());

//		viewHolder.description.setTypeface(Typeface.createFromAsset(
//				context.getAssets(), "Fonts/OpenSans-Light.ttf"));
//		viewHolder.description.setText(special.getDescription());
		viewHolder.image.setImageUrl(special.getImageUrl());

		return convertView;
	}

	private class ViewHolder {
		protected SmartImageView image;
		protected TextView name;
		/*protected TextView description;*/
	}

}
