package com.pocketools.stockalert;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class URLFetcher {
	
	public static Bitmap getBitmap(String url){
		
		try {
		    
            /* Open a new URL and get the InputStream to load data from it. */
		    
		        URL img = new URL(url);
		        
		        try{
		        	Thread.sleep(2000);
		        }catch(Exception e){}
		        
		        return BitmapFactory.decodeStream(img.openStream()); 
	        }catch(MalformedURLException e){
	        	Log.d("URLFetcher", e.getMessage());
	        	return null;
	        }
	        catch(IOException e){
	        	Log.d("URLFetcher", e.getMessage());
	        	return null;
	        	
	        } 
	}
	
public static String getString(String url){
		
		try {
			
			Log.d("URLFetcher", "FETCH " + url);
		    
			URL http_url = new URL(url);
			http_url.openConnection();
			BufferedInputStream bis = new BufferedInputStream(http_url.openStream());
		    ByteArrayOutputStream buf = new ByteArrayOutputStream();
		    int result = bis.read();
		    
		    while(result != -1) {
		      byte b = (byte)result;
		      buf.write(b);
		      result = bis.read();
		    } 
		    
		    return buf.toString();
	    
	        }catch(MalformedURLException e){
	        	Log.d("URLFetcher", e.getMessage());
	        	return null;
	        }
	        catch(IOException e){
	        	Log.d("URLFetcher", e.getMessage());
	        	return null;
	        	
	        } 
	}
	
	
	

}
