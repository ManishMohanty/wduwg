package com.example.wduwg;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apphance.android.Log;
import com.loopj.android.image.SmartImageView;
import com.mw.wduwg.model.Business;
import com.mw.wduwg.services.GlobalVariable;

public class CustomAdapter extends BaseAdapter {
	Context context;
	List<Business> placeList;
	LayoutInflater inflater;
	Typeface typeface,typefaceBold;

	public CustomAdapter(Context context, List<Business> items) {
		super();
		this.context = context;
		this.placeList = items;
		Log.d("== place size:", "" + this.placeList.size());
		typeface = Typeface.createFromAsset(context.getAssets(),
				"Fonts/OpenSans-Light.ttf");
		typefaceBold = typeface.createFromAsset(context.getAssets(), "Fonts/OpenSans-Bold.ttf");
	}

	private class ViewHolder {
		protected TextView sno;
		protected SmartImageView image;
		protected TextView name;
		protected TextView address;
	}
	
	public View getView(int pos, View convertView, ViewGroup parent) {
		Business tempPlace = placeList.get(pos);
		ViewHolder viewHolder;
		if (convertView == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.place_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.sno = (TextView) convertView.findViewById(R.id.sno);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.address = (TextView) convertView.findViewById(R.id.desc);
			viewHolder.image = (SmartImageView) convertView
					.findViewById(R.id.icon);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.sno.setTypeface(typefaceBold);
		viewHolder.sno.setText("" + (pos + 1) + ".");

		viewHolder.name.setTypeface(typefaceBold);
		viewHolder.name.setText(tempPlace.getName());

		viewHolder.address.setTypeface(typeface);
		viewHolder.address.setText(tempPlace.getAddress());
		try{
			String googleApiResults = tempPlace.getGoogleAPIResult();
			JSONObject json = new JSONObject(googleApiResults);

			JSONObject photoJsonObject  = new JSONObject(json.getString("photos").substring(1, json.getString("photos").length()-1).toString());
			tempPlace.setImageUrl(photoJsonObject.getString("photo_reference"));
			}catch(Exception e)
			{
				e.printStackTrace();
			}


		String temp = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=60&photoreference="
				+ tempPlace.getImageUrl()
				+ "&sensor=true&key=AIzaSyB7yyHP-E00gXJOH6erfU6Acg8yMpSoZV4";

		System.out.println(temp);

		viewHolder.image.setImageUrl(temp);
		
		return convertView;
	}



	@Override
	public int getCount() {
		return placeList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	// AsyncTask
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference imageViewReference;

		public DownloadImageTask(ImageView imageView, Business item) {
			imageViewReference = new WeakReference(imageView);
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=60&photoreference="
					+ urls[0]
					+ "&sensor=true&key=AIzaSyB7yyHP-E00gXJOH6erfU6Acg8yMpSoZV4";
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			if (isCancelled()) {
				result = null;
			}
			if (imageViewReference != null) {
				ImageView imageView = (ImageView) imageViewReference.get();
				if (imageView != null) {
					if (result != null) {

						imageView.setImageBitmap(GlobalVariable
								.getRoundedShape(result));

					} else {
						imageView.setImageDrawable(imageView.getContext()
								.getResources().getDrawable(R.drawable.earth));
					}
				}
			}
		}// onPost
	}// Async

}