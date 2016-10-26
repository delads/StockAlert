package com.pocketools.stockalert;


public class CommodityUtil {
	
	private String mType = "";
	private String mSymbol = "";
	private String mName = "";
	private String mPrice = "";
	private String mLastTradeDate = "";
	private String mChangeInPrice = "";
	private String mChangeInPercentage = "";

	public String getType(){return mType;}
	public String getSymbol(){return mSymbol;}
	public String getName(){return mName;}
	public String getPrice(){return mPrice;}
	public String getLastTradeDate(){return mLastTradeDate;}
	public String getChangeInPrice(){return mChangeInPrice;}
	public String getChangeInPercentage(){return mChangeInPercentage;}
	
	public void setType(String type){ mType = type;}
	public void setSymbol(String symbol){ mSymbol = symbol;}
	public void setName(String name){ mName = name;}
	public void setPrice(String price){ mPrice = price;}
	public void setLastTradeDate(String lastTradeDate){ mLastTradeDate = lastTradeDate;}
	public void setChangeInPrice(String changeInPrice){ mChangeInPrice = changeInPrice;}
	public void setChangeInPercentage(String changeInPercentage){ mChangeInPercentage = changeInPercentage;}
	

}
