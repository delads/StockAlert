package com.pocketools.stockalert;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TableRow;




public class Info extends Activity {
    /** Called when the activity is first created. */
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);       
        setContentView(R.layout.info); 
     
        
        
        /*
        TableRow moreInfo = (TableRow)findViewById(R.id.info_more_info);
        moreInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.pocketools.com/release-notes/")));          		
            }
        });
        
        */
        
        TableRow followMe = (TableRow)findViewById(R.id.info_follow_me);
        followMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/pocketools")));          		
            }
        });
        
        TableRow feedback = (TableRow)findViewById(R.id.info_send_feedbck);
        feedback.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	Intent emailIntent = new Intent(Intent.ACTION_SEND);     
        		emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{"don@pocketools.com"});     
        		emailIntent.putExtra(Intent.EXTRA_TEXT, "");     
        		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback on Stock Alert");
        		emailIntent.setType("message/rfc822");  
        		startActivity(Intent.createChooser(emailIntent, "Title:")); 

            	
            }
        });
        
        TableRow otherTools = (TableRow)findViewById(R.id.info_other_tools);
        otherTools.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	//mTracker.trackEvent(, arg1, arg2, arg3)
            	
            	Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://market.android.com/search?q=pub:\"Pocketools.com\"")); 
    	    	startActivity(marketIntent);    		
            }
        });
        
        TableRow faq = (TableRow)findViewById(R.id.info_faq);
        faq.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(v.getContext(),FAQ.class);
                startActivity(intent);
            }
        });
        
    }
 
}