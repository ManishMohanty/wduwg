package com.wduwg.preference;

import com.example.wduwg.tiles.LoginFacebookActivity;
import com.example.wduwg.tiles.R;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CustomPreference extends Preference {

    private LinearLayout mWidgetContainer;
    private View mRowView;

    public CustomPreference(Context context) {
        super(context);
    }

    public CustomPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        LayoutInflater viewInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mRowView = viewInflater.inflate(R.layout.custom_preferences_btn, parent, false);
        RelativeLayout textLayout = (RelativeLayout)mRowView.findViewById(R.id.textLayout);
        mWidgetContainer = (LinearLayout) mRowView.findViewById(android.R.id.widget_frame);
        TextView textView_title = (TextView) mRowView.findViewById(android.R.id.title);
	    textView_title.setTextSize(16);
	    TextView textView_summary = (TextView) mRowView.findViewById(android.R.id.summary);
	    Typeface myTypeface = Typeface.createFromAsset(textView_title.getContext().getAssets(),"Fonts/Exo2.0-Regular.otf");
	    if (textView_title != null) {
	    	textView_title.setTypeface(myTypeface);
	    	textView_summary.setTypeface(myTypeface);
	    }

        Button button = new Button(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        button.setLayoutParams(params);
        button.setBackgroundResource(R.drawable.images);
        button.setTextSize(16);
//        button.setPadding(10, 10, 10, 10);
        button.setPadding(5, 10, 10, 0);
        textLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getContext(), LoginFacebookActivity.class);
                getContext().startActivity(intent);
			}
		});
        
        button.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginFacebookActivity.class);
                getContext().startActivity(intent);
//            	Toast.makeText(getContext(), "hi", Toast.LENGTH_SHORT).show();
            }
        });
//        button.setTypeface(null, Typeface.BOLD);
//        button.setText("Buy now");
        mWidgetContainer.addView(button);

        return mRowView;
    }
    
    public void onPost(View V)
    {
    	Toast.makeText(getContext(), "hi", Toast.LENGTH_SHORT).show();
    }
}
