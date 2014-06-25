package com.mw.wduwg.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mw.wduwg.model.ContextMenuItem;
import com.wduwg.counter.app.R;

public class ContextMenuAdapter extends BaseAdapter {
	Context context;
	List<ContextMenuItem> listContextMenuItems;

	LayoutInflater inflater;
	boolean isLogoutVisisble;
	boolean isFlashCompatible;
	Typeface typeface;

	public ContextMenuAdapter(Context context,
			List<ContextMenuItem> listContextMenuItems,
			boolean isLogoutVisisble, boolean isFlashCompatible) {
		super();
		this.context = context;
		this.listContextMenuItems = listContextMenuItems;
		this.isLogoutVisisble = isLogoutVisisble;
		this.isFlashCompatible = isFlashCompatible;
//		typeface = Typeface.createFromAsset(context.getAssets(),
//				"Fonts/ufonts.com_segoe_ui_semibold.ttf");
		typeface = Typeface.createFromAsset(context.getAssets(),
				"Fonts/OpenSans-Light.ttf");

	}

	static class ViewHolder {
		protected ImageView imageView;
		protected TextView textView;
		protected TextView textView2;
		protected ImageButton imageButton;
		protected Switch toggleButton;
	}

	public void swapData(List<ContextMenuItem> listContextMenuItems, boolean isLogoutVisisble) {
		this.listContextMenuItems = listContextMenuItems;
		
		this.isLogoutVisisble = isLogoutVisisble;

	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.context_menu_item, parent,
					false);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.imageView_menu);
			viewHolder.textView = (TextView) convertView
					.findViewById(R.id.textView_menu);
			viewHolder.textView2 = (TextView) convertView
					.findViewById(R.id.textView_menu2);
			viewHolder.imageButton = (ImageButton) convertView
					.findViewById(R.id.logout_IB);
			viewHolder.toggleButton = (Switch) convertView
					.findViewById(R.id.switchB);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.imageView.setImageDrawable(listContextMenuItems
				.get(position).getDrawable());
		viewHolder.textView.setText(listContextMenuItems.get(position)
				.getText());
		viewHolder.textView.setTypeface(typeface);
		if (position == 0 && isLogoutVisisble)
			viewHolder.imageButton.setVisibility(View.VISIBLE);
		if (position == 2) {
			if (!isFlashCompatible)
				viewHolder.textView2.setVisibility(View.VISIBLE);
			else
				viewHolder.toggleButton.setVisibility(View.VISIBLE);
		}
		else
		{
			viewHolder.textView2.setVisibility(View.GONE);
			viewHolder.toggleButton.setVisibility(View.GONE);
		}
		return convertView;

	}

	@Override
	public int getCount() {
		return listContextMenuItems.size();
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
