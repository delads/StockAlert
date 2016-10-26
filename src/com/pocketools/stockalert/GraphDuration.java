package com.pocketools.stockalert;

import com.pocketools.stockalert.R;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class GraphDuration extends ListActivity {
	
	Intent mIntent;
    public static final int CHOOSE_TIMEFRAME = 1000;
    public static final int CHOOSE_TIMEFRAME_SUCCESS = 1001;
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.chart_timeframe);
       
        mIntent = this.getIntent();       
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mStrings));
        
        this.getListView().setDivider(this.getResources().getDrawable(android.R.drawable.divider_horizontal_textfield));
        
        
        
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {         
	     mIntent.putExtra("TIMEFRAME", mStrings[position]);		            	
			setResult(GraphDuration.CHOOSE_TIMEFRAME_SUCCESS, mIntent);
			
			finish();
    }
    
    private String[] mStrings = {
            "1 day", "5 day", "3 month", "1 year", "2 year", "5 year"};


}