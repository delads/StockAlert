package com.pocketools.stockalert;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;

public class StockAlertDialog extends Activity {
	
	private String mSymbol = "";
	private static final int SHOW_MOVE_UP_DOWN = 1000;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.stock_alert_dialog);
        
        mSymbol = this.getIntent().getStringExtra("SYMBOL");
        
        TextView delete = (TextView)findViewById(R.id.dialog_delete);
        delete.setOnClickListener(new TextView.OnClickListener() {
        	public void onClick(View v) {	            		
        		DBAdapter db = new DBAdapter(v.getContext());
        		db.open();	            		
        		db.deleteStock(mSymbol);
        		db.close();
        		Toast.makeText(v.getContext(), mSymbol + " Deleted !", Toast.LENGTH_SHORT).show();
        		
        		finish();
       		}            	
		});

        TextView moveUp = (TextView)findViewById(R.id.dialog_move_up);
        moveUp.setOnClickListener(new TextView.OnClickListener() {
        	public void onClick(View v) {	            		
        		setResult(StockAlert.SHOW_MOVE_UP_DOWN);
        		finish();
       		}            	
		});
        
        TextView moveDown = (TextView)findViewById(R.id.dialog_move_down);
        moveDown.setOnClickListener(new TextView.OnClickListener() {
        	public void onClick(View v) {	            		
        		setResult(StockAlert.SHOW_MOVE_UP_DOWN);
        		finish();
       		}            	
		});
    }
 
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {      
        Log.d(this.getLocalClassName(), "onSaveInstanceState()");
        
        super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onPause() {
        Log.d(this.getLocalClassName(), "onPause()");

        super.onPause();
    }
    
    @Override
    protected void onStop() {      
        Log.d(this.getLocalClassName(), "onStop()");
        
  
        super.onStop();
    }
    
    @Override
    protected void onDestroy() {
        Log.d(this.getLocalClassName(), "onDestroy()");
        
        super.onDestroy();
        

    }
    
    @Override
    protected void onResume() {
    	Log.d(this.getLocalClassName(), "onResume()");

    	
    	super.onResume();
    }
    
    @Override
    protected void onRestart() {        
    	Log.d(this.getLocalClassName(), "onRestart()");

      
        super.onRestart();
    }
    
    
    protected void onStart() {
    	Log.d(this.getLocalClassName(), "onStart()");
	
        super.onStart();  
}

}
