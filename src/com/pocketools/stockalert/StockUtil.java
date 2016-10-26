package com.pocketools.stockalert;

import android.widget.CheckBox;
import android.widget.TextView;

public class StockUtil {
	
	private String mSymbol = null;
	private String mCompanyName = null;
	private CheckBox mCheckBox = null;
	private TextView mTextView = null;
	
	public StockUtil(String symbol, String companyName){
		mSymbol = symbol;
		mCompanyName = companyName;
		
	}
	
	public StockUtil(CheckBox checkbox, TextView textView, String symbol){
		mCheckBox = checkbox;
		mTextView = textView;
		mSymbol = symbol;
	}
	
	public String getSymbol(){return mSymbol;}
	public String getCompanyName(){return mCompanyName;}
	public CheckBox getCheckBox(){return mCheckBox;}
	public TextView getTextView(){return mTextView;}
	

}
