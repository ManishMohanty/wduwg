package com.mw.wduwg.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.wduwg.tiles.R;
import com.parse.ParseObject;

public class AutoCompleteAdapter extends BaseAdapter implements Filterable {

	private Context context;
	List<ParseObject> mainList;
	List<ParseObject> origList;
	LayoutInflater inflater;
	private TempFilter filter;

	// Constructor
	public AutoCompleteAdapter(Context context, List<ParseObject> tempList) {
		this.context = context;
		this.mainList = tempList;
		origList = this.mainList;
		filter = new TempFilter();
	}

	static class ViewHolder {
		protected TextView textView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.element_auto_complete,
					parent, false);
			viewHolder.textView = (TextView) convertView
					.findViewById(R.id.event_name_TV);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.textView.setText(mainList.get(position).getString("name"));
		return convertView;
	}

	@Override
	public int getCount() {
		if (mainList != null)
			return mainList.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		return mainList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// return tempList.get(arg0).getID();
		return 0;
	}

	@Override
	public Filter getFilter() {
		return filter;
	}

	private class TempFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence charSequence) {
			System.out.println("performFiltering");
			FilterResults oReturn = new FilterResults();
			ArrayList<ParseObject> results = new ArrayList<ParseObject>();

			// next 2 lines are probably not important
			if (origList == null)
				origList = mainList;

			if (charSequence != null) {
				if (origList != null && origList.size() > 0) {
					for (ParseObject g : origList) {
						if (g.getString("name").contains(charSequence))
							results.add(g);
					}
				}
				oReturn.values = results;
			}
			// the return is sent to next function as argument
			return oReturn;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			System.out.println("publishResults");
			mainList = (ArrayList<ParseObject>) results.values;
			notifyDataSetChanged();
		}
	}

}
