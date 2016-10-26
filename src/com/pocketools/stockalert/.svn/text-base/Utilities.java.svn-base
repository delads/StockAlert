package com.pocketools.stockalert;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;


public class Utilities {
	


   // This method will search for a string in a String buffer and return the data between the end
   // of the search string and the endString.
   public static String searchForString(String searchString, String endString, StringBuffer theData) {
       BufferedReader br;
       String singleLine;
       String returnString = null;
       int foundIndex;
       boolean returnVal = false;
       
       
       br = new BufferedReader(new StringReader(theData.toString()));
        try {
            while ((singleLine = br.readLine()) != null) {
                foundIndex = singleLine.indexOf(searchString);
                if(foundIndex > -1) {
                    String tempString = singleLine.substring(foundIndex+searchString.length());
                    
                    if(tempString.indexOf(endString) != -1){
                    	returnString = tempString.substring(0,tempString.indexOf(endString));  // Stop
	                    if(returnString != null) {
	                        break;
	                    }
                	}
                }
            }
           
        }
        catch (IOException e) {
			//TODO
        }

        
        return returnString;

       
   }

}