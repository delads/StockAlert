package com.pocketools.stockalert;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



/**
* Simple notes database access helper class. Defines the basic CRUD operations
* for the notepad example, and gives the ability to list all notes as well as
* retrieve or modify a specific note.
* 
* This has been improved from the first version of this tutorial through the
* addition of better error handling and also using returning a Cursor instead
* of using a collection of inner classes (which is less scalable and not
* recommended).
*/

public class DBAdapter {

	   public static final String KEY_CURRENCY_DESCRIPTION = "currency_description";
	   public static final String KEY_CURRENCY_X_RATE = "exchange_rate";
	   public static final String KEY_CURRENCY_IS_SELECTED = "exchange_rate_selected";
	   public static final String KEY_CURRENCY_X_RATE_REFRESH_DATE = "xrate_refresh_date";
	   public static final String KEY_DEFAULT_VALUE = "default_value";
	   public static final String KEY_DEFAULT_TYPE = "default_type";
	   
	   public static final String KEY_SYMBOL = "symbol";
	   public static final String KEY_LAST_TRADE_PRICE = "last_trade_price";
	   public static final String KEY_CHANGE_IN_PERCENTAGE = "change_in_percentage";
	   public static final String KEY_CHANGE_IN_PRICE = "change_in_price";
	   public static final String KEY_DAILY_HIGH = "daily_high";
	   public static final String KEY_DAILY_LOW = "daily_low";
	   public static final String KEY_52_WEEK_HIGH = "yearly_high"; 
	   public static final String KEY_52_WEEK_LOW = "yearly_low";
	   public static final String KEY_MARKET_CAP = "market_cap";
	   public static final String KEY_LAST_TRADE_TIME = "last_trade_time";
	   public static final String KEY_LAST_TRADE_DATE = "last_trade_date";
	   public static final String KEY_STOCK_EXCHANGE = "stock_exchange";
	   public static final String KEY_COMPANY_NAME = "company_name";
	   public static final String KEY_ASK_PRICE = "ask_price";
	   public static final String KEY_ASK_PRICE_REALTIME = "ask_price_realtime";
	   public static final String KEY_BID_PRICE = "bid_price";
	   public static final String KEY_BID_PRICE_REALTIME = "bid_price_realtime";
	   public static final String KEY_PREVIOUS_CLOSE_PRICE = "previous_close_price";
	   public static final String KEY_OPEN_PRICE = "open_price";
	   public static final String KEY_1_YEAR_TARGET_PRICE = "one_year_target_price";
	   public static final String KEY_VOLUME_AVERAGE_3M = "volume_average_3m";
	   public static final String KEY_VOLUME_DAY = "volume_day";
	   public static final String KEY_PRICE_EARNINGS_RATIO = "price_earnings_ratio";
	   public static final String KEY_EARNINGS_PER_SHARE = "earnings_per_share";
	   public static final String KEY_LOW_ALERT_PRICE = "low_alert_price";
	   public static final String KEY_HIGH_ALERT_PRICE = "high_alert_price";
	   public static final String KEY_LOW_ALERT_ACTIVE = "low_alert_active";
	   public static final String KEY_HIGH_ALERT_ACTIVE = "high_alert_active";
	   public static final String KEY_ALERT_MESSAGE = "stock_spare_1";
	   public static final String KEY_WIDGET_ID = "stock_spare_2";
	   public static final String KEY_IS_REALTIME = "stock_spare_3";
	   public static final String KEY_ID = "_id";

	  	
   private static final String TAG = "CurrencyDbAdapter";
   private DatabaseHelper mDbHelper;
   private SQLiteDatabase mDb;
   
   
   
   /**
    * Database creation sql statement
    */
   
   private static final String STOCK_TABLE_CREATE =
       "CREATE TABLE stock (" + 
       						   "_id integer primary key autoincrement," +
       						   "symbol TEXT, " +
       						   "currency_description TEXT, " +
	       					   "last_trade_price TEXT, " + 
	       					   "change_in_percentage TEXT, " + 
	       					   "change_in_price TEXT, " + 
	       					   "daily_high TEXT, " + 
	       					   "daily_low TEXT, " + 
	       					   "yearly_high TEXT, " + 
	       					   "yearly_low TEXT, " + 
	       					   "market_cap TEXT, " + 
	       					   "last_trade_time TEXT, " + 
	       					   "last_trade_date TEXT, " + 
	       					   "stock_exchange TEXT, " + 
	       					   "company_name TEXT, " + 
	       					   "ask_price TEXT, " + 
	       					   "ask_price_realtime TEXT, " + 
	       					   "bid_price TEXT, " + 
	       					   "bid_price_realtime TEXT, " + 
	       					   "previous_close_price TEXT, " +
	       					   "open_price TEXT, " + 
	       					   "one_year_target_price TEXT, " + 
	       					   "volume_average_3m TEXT, " + 
	       					   "volume_day TEXT, " + 
	       					   "price_earnings_ratio TEXT, " + 
	       					   "earnings_per_share TEXT, " + 
	       					   "stock_spare_1 TEXT, " + 
	       					   "stock_spare_2 TEXT, " + 
	       					   "stock_spare_3 TEXT, " + 
	       					   "stock_spare_4 TEXT);";
   
   private static final String DEFAULTS_TABLE_CREATE =
       "CREATE TABLE defaults (" + 
       						   "default_type TEXT, " +
       						   "default_value TEXT);";
   
   
   private static final String ALERTS_TABLE_CREATE =
       "CREATE TABLE alerts (" + 
       						   "symbol TEXT, " +
       						   "high_alert_price TEXT, " +
       						   "low_alert_price TEXT, " +
       						   "high_alert_active INT, " +
       						   "low_alert_active INT, " + 
       						   "alert_spare_1 INT, " +
       						   "alert_spare_2 INT);";
   
   
 
   private static final String DATABASE_NAME = "stock_value";
   private static final String STOCK_TABLE = "stock";
   private static final String DEFAULTS_TABLE = "defaults";
   private static final String ALERTS_TABLE = "alerts";
   
   private static final int DATABASE_VERSION = 33;

   private Context mCtx = null;

   private static class DatabaseHelper extends SQLiteOpenHelper {

       DatabaseHelper(Context context) {
           super(context, DATABASE_NAME, null, DATABASE_VERSION);
       }

       @Override
       public void onCreate(SQLiteDatabase db) {

    	   Log.w(TAG, "Creating new Database");
           db.execSQL(STOCK_TABLE_CREATE);     
           db.execSQL(DEFAULTS_TABLE_CREATE);
           db.execSQL(ALERTS_TABLE_CREATE);
 
           db.execSQL("INSERT INTO stock VALUES (0,\"GOOG\",\"Google\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\")");
           db.execSQL("INSERT INTO stock VALUES (1,\"YHOO\",\"Yahoo\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\")");
           db.execSQL("INSERT INTO stock VALUES (2,\"MSFT\",\"Microsoft\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\")");
           db.execSQL("INSERT INTO stock VALUES (3,\"USDEUR=X\",\"USD to EUR\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\")");
                     
           
           //db.execSQL("INSERT INTO defaults VALUES (\"default_base_currency\",\"EUR\")");
           //db.execSQL("INSERT INTO defaults VALUES (\"default_result_currency\",\"USD\")");
           db.execSQL("INSERT INTO defaults VALUES (\"refresh_date\",\"0\")");
           db.execSQL("INSERT INTO defaults VALUES (\"user_agreement_accepted\",\"false\")");
           db.execSQL("INSERT INTO defaults VALUES (\"default_base_currency_list\",\"GOOG\")");
           db.execSQL("INSERT INTO defaults VALUES (\"default_base_currency_amount\",\"1\")");
           db.execSQL("INSERT INTO defaults VALUES (\"graph_section_expanded\",\"on\")");
           db.execSQL("INSERT INTO defaults VALUES (\"stock_alerts_section_expanded\",\"off\")");
           db.execSQL("INSERT INTO defaults VALUES (\"portfolio_value_section_expanded\",\"off\")");
           db.execSQL("INSERT INTO defaults VALUES (\"key_statistics_section_expanded\",\"off\")");
           db.execSQL("INSERT INTO defaults VALUES (\"company_news_section_expanded\",\"on\")");
           db.execSQL("INSERT INTO defaults VALUES (\"youtube_section_expanded\",\"off\")");
           
           db.execSQL("INSERT INTO alerts VALUES (\"GOOG\",\"\",\"\",0,0,\"\",\"\")"); 
           db.execSQL("INSERT INTO alerts VALUES (\"YHOO\",\"\",\"\",0,0,\"\",\"\")"); 
           db.execSQL("INSERT INTO alerts VALUES (\"MSFT\",\"\",\"\",0,0,\"\",\"\")"); 
           db.execSQL("INSERT INTO alerts VALUES (\"USDEUR=X\",\"\",\"\",0,0,\"\",\"\")"); 
           
           
       }

       @Override
       public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
           Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                   + newVersion + ", which will destroy all old data");
           db.execSQL("DROP TABLE IF EXISTS " + STOCK_TABLE);
           db.execSQL("DROP TABLE IF EXISTS " + DEFAULTS_TABLE);
           db.execSQL("DROP TABLE IF EXISTS " + ALERTS_TABLE);
           onCreate(db);
       }
   }

   /**
    * Constructor - takes the context to allow the database to be
    * opened/created
    * 
    * @param ctx the Context within which to work
    */
   public DBAdapter(Context ctx) {
       this.mCtx = ctx;
   }
   
   public Context getContext(){
	   return mCtx;
   }

   /**
    * Open the notes database. If it cannot be opened, try to create a new
    * instance of the database. If it cannot be created, throw an exception to
    * signal the failure
    * 
    * @return this (self reference, allowing this to be chained in an
    *         initialization call)
    * @throws SQLException if the database could be neither opened or created
    */
   public DBAdapter open() throws SQLException {
       mDbHelper = new DatabaseHelper(mCtx);
       mDb = mDbHelper.getWritableDatabase();
       return this;
   }
   
   public void close() {
       mDbHelper.close();
   }
   
   public SQLiteDatabase getDatabase(){
	   return mDb;
   }
   
   public int getSplashScreenVersion(){
	   
	   int screenVersion = 0;
	   
	   String sql = "SELECT * FROM defaults WHERE default_type = \"splash_screen_version\"";
	   Cursor c = mDb.rawQuery(sql, null);
	   
	   if(c.getCount() == 0){
		   mDb.execSQL("INSERT INTO defaults VALUES (\"splash_screen_version\",\"0\")");
	   }
	   else{
		   c.moveToFirst();
		   String versionString = c.getString(c.getColumnIndex("default_value"));
		   try{
			   screenVersion = Integer.parseInt(versionString);
		   }
		   catch(Exception e){}
	   }
	   
	   c.close();
	   
	   return screenVersion;
   }
 
   
   
   public void updateSplashScreenVersion(int version){
	   
	   //Let's add the screen version JUST IN CASE it's not already there
	   String sql = "SELECT * FROM defaults WHERE default_type = \"splash_screen_version\"";
	   Cursor c = mDb.rawQuery(sql, null);
	   
	   if(c.getCount() == 0){
		   mDb.execSQL("INSERT INTO defaults VALUES (\"splash_screen_version\",\"0\")");
	   }
	   
	   c.close();
   
	   sql = "UPDATE defaults SET default_value = \"" + Integer.toString(version) + "\" WHERE default_type = \"splash_screen_version\"";
	   mDb.execSQL(sql);
	   
   }
   
   
   public void setDefaultBaseCurrencyList(String currency_type){
	   
	   String sql = "UPDATE defaults SET default_value = \"" + currency_type + "\" WHERE default_type = \"default_base_currency_list\"";
	   mDb.execSQL(sql);
	   
   }
   
   public void setDefaultBaseCurrencyListAmount(String currency_amount){
	   
	   String sql = "UPDATE defaults SET default_value = \"" + currency_amount + "\" WHERE default_type = \"default_base_currency_amount\"";
	   mDb.execSQL(sql);
	   
   }
   
   
   public void addDefaultPage(String default_page){	   
	   mDb.execSQL("INSERT INTO defaults VALUES (\"default_page\",\"" + default_page + "\")");
   }
   
   public void updateDefaultPage(String default_page){
	   String sql = "UPDATE defaults SET default_value = \"" + default_page + "\" WHERE default_type = \"default_page\"";
	   mDb.execSQL(sql);
   }
   
   
   public void addDefaultTimeframe(String default_timeframe){	   
	   mDb.execSQL("INSERT INTO defaults VALUES (\"default_timeframe\",\"" + default_timeframe + "\")");
   }
   
   public void addDefaultLocation(String default_location){	   
	   mDb.execSQL("INSERT INTO defaults VALUES (\"default_location\",\"" + default_location + "\")");
   }
   
   public void addAdPreferences(String ad_preferences){	   
	   mDb.execSQL("INSERT INTO defaults VALUES (\"ad_preferences\",\"" + ad_preferences + "\")");
   }
   
   
   
   public void updateDefaultTimeframe(String default_timeframe){
	   String sql = "UPDATE defaults SET default_value = \"" + default_timeframe + "\" WHERE default_type = \"default_timeframe\"";
	   mDb.execSQL(sql);
   }
   
   public void updateDefaultLocation(String default_location){
	   String sql = "UPDATE defaults SET default_value = \"" + default_location + "\" WHERE default_type = \"default_location\"";
	   mDb.execSQL(sql);
   }
   
   public void updateAdPreferences(String ad_preferences){
	   String sql = "UPDATE defaults SET default_value = \"" + ad_preferences + "\" WHERE default_type = \"ad_preferences\"";
	   mDb.execSQL(sql);
   }
   
   
   public void addGoogleKeywords(String google_keywords){	   
	   mDb.execSQL("INSERT INTO defaults VALUES (\"google_keywords\",\"" + google_keywords + "\")");
   }
   
   public void updateGoogleKeywords(String google_keywords){
	   String sql = "UPDATE defaults SET default_value = \"" + google_keywords + "\" WHERE default_type = \"google_keywords\"";
	   mDb.execSQL(sql);
   }
   
   
   public void setUserAgreement(String accpeted_rejected){
	   String sql = "UPDATE defaults SET default_value = \"" + accpeted_rejected + "\" WHERE default_type = \"user_agreement_accepted\"";
	   mDb.execSQL(sql);
   }
   
   
   public void setWidgetStock(String symbol){
	   
	   
	   String sql = "SELECT * FROM defaults WHERE default_type = \"widget_stock\"";
	   Cursor c = mDb.rawQuery(sql, null);
	   
	   if(c.getCount() == 0)
		   mDb.execSQL("INSERT INTO defaults VALUES (\"widget_stock\",\"GOOG\")");
	   
	   c.close();

	   sql = "UPDATE defaults SET default_value = \"" + symbol + "\" WHERE default_type = \"widget_stock\"";
	   mDb.execSQL(sql);
   }
   
   /*
   public void deleteWidget(int widgetId){
	   String widgetString = String.valueOf(widgetId);
	   
	   String sql = "UPDATE stock SET " + KEY_WIDGET_ID + " = \"\" WHERE " + KEY_WIDGET_ID + " = \"" + widgetString + "\"";
	   mDb.execSQL(sql);
   }
   
   public void addWidget(String symbol, String widgetId){
	   String sql = "UPDATE stock SET " + KEY_WIDGET_ID + " = \"" + widgetId + "\" WHERE symbol = \"" + symbol + "\"";
	   mDb.execSQL(sql);
   }
   
   public Cursor getWidgetId(String symbol){
	   String sql = "SELECT " + KEY_WIDGET_ID + " from stock WHERE symbol = \"" + symbol + "\"";
	   return mDb.rawQuery(sql, null);
   }
   */

   
   public Cursor getDefaultValues(){
	   String sql = "SELECT * from defaults";
	   return mDb.rawQuery(sql, null);
   }
   
   public Cursor getDefaultValue(String type){
	   String sql = "SELECT * from defaults WHERE default_type = \"" + type + "\"";
	   return mDb.rawQuery(sql, null);
	   
   }
   
   public void addDefaultPair(String key, String value){
	   mDb.execSQL("INSERT INTO defaults VALUES (\"" + key + "\",\"" + value + "\")");
   }
   
   public void updateDefaultValue(String key, String value){
	   String sql = "UPDATE defaults SET default_value = \"" + value + "\" WHERE default_type = \"" + key + "\"";
	   mDb.execSQL(sql);
   }

   
   public void updateRateDate(String xrate_refresh_date){
	   
	   String sql = "UPDATE defaults SET default_value = \"" + xrate_refresh_date + "\" WHERE default_type = \"refresh_date\"";
	   mDb.execSQL(sql);
	   
   }

   
   public Cursor getStockDetail(String symbol){
	   String sql = "SELECT * from stock WHERE symbol = '" + symbol + "'";
	   return mDb.rawQuery(sql, null);
   }
 
   public Cursor getStocks(){
	   //String sql = "SELECT * FROM stock ORDER BY symbol ASC";
	   String sql = "SELECT * FROM stock";
	   return mDb.rawQuery(sql, null);
   }
   
   
   public long addStock(String symbol, String company_name){
	   
	   
	   mDb.execSQL("INSERT INTO alerts VALUES (\"" + symbol + "\",\"\",\"\",0,0,\"\",\"\")");
	   
	   ContentValues initialValues = new ContentValues();
       initialValues.put(KEY_SYMBOL, symbol);
       initialValues.put(KEY_COMPANY_NAME, company_name);

       return mDb.insert(STOCK_TABLE, null, initialValues);
  
   }
   
   public long addStock(String symbol, String company_name, String price){
	   
	   
	   mDb.execSQL("INSERT INTO alerts VALUES (\"" + symbol + "\",\"\",\"\",0,0,\"\",\"\")");
	   
	   ContentValues initialValues = new ContentValues();
       initialValues.put(KEY_SYMBOL, symbol);
       initialValues.put(KEY_COMPANY_NAME, company_name);
       initialValues.put(KEY_LAST_TRADE_PRICE,price);

       return mDb.insert(STOCK_TABLE, null, initialValues);
  
   }
   
   public void deleteStock(String symbol){
	   
	   mDb.delete(STOCK_TABLE, KEY_SYMBOL + " = \"" + symbol + "\"", null);
	   mDb.delete(ALERTS_TABLE, KEY_SYMBOL + " = \"" + symbol + "\"", null);
	   
   }
   
   public void setStockId(int newId, int oldId){   
	   String sql = "UPDATE stock SET _id = " + newId + " WHERE _id = " + oldId;
	   mDb.execSQL(sql);
   }
   
   public void addAlertMessage(String symbol, String message){   
	   
	   Cursor c = getAlertMessage(symbol);
	   c.moveToFirst();
	   
	   String currentMessage = c.getString(c.getColumnIndex(DBAdapter.KEY_ALERT_MESSAGE));
	   
	   c.close();
	   
	   if(currentMessage == null)
	  	   currentMessage = "";
	   
	   else
		   currentMessage = currentMessage.replace("N/A", "");
	   
	   String newMessage = currentMessage + message;
	   
	   String sql = "UPDATE stock SET stock_spare_1 = \"" + newMessage + ".  \" WHERE symbol = '" + symbol + "'";
	   //Log.d("DBAdapter.addAlertMessage()",sql);
	   
	   
	   mDb.execSQL(sql);
   }
   
   public void removeAlertMessage(String symbol){   
	   String sql = "UPDATE stock SET stock_spare_1 = 'N/A' WHERE symbol = '" + symbol + "'";
	   mDb.execSQL(sql);
   }
   
   public Cursor getAlertMessage(String symbol){
	   String sql = "SELECT stock_spare_1 FROM stock WHERE symbol = '" + symbol + "'";	   
	   return mDb.rawQuery(sql, null);
   }
   
   
   public void updateRealtimePrice(String symbol, String lastTradePrice, String change_in_percentage, String change_in_price, String last_trade_time, String last_trade_date){
	   
	   
	   //Quick fix for the Date
	   int comma = last_trade_date.indexOf(",");
	   String short_date = last_trade_date.substring(0, comma);
	   
	   String sql = "UPDATE stock SET " +

	   "last_trade_price ='" + lastTradePrice + "', " + 
	   "last_trade_date ='" + short_date + "', " + 
	   "change_in_percentage ='" + change_in_percentage + "', " + 
	   "last_trade_time ='" + last_trade_time + "', " + 
	   KEY_IS_REALTIME + "='true', " + 
	   "change_in_price ='" + change_in_price + "' " + 
	   "WHERE symbol = '" + symbol + "' ";
		
		mDb.execSQL(sql);
   }
   
   
   public void updateStocks(String symbol, Map<String, String> stockMap){
	   
	   String sql = "UPDATE stock SET " +

			   "last_trade_price ='" + stockMap.get("last_trade_price") + "', " + 
			   "change_in_percentage ='" + stockMap.get("change_in_percentage") + "', " + 
			   "change_in_price ='" +	stockMap.get("change_in_price") + "', " + 
			   "daily_high ='" + stockMap.get("daily_high") + "', " + 
			   "daily_low ='" + stockMap.get("daily_low") + "', " + 
			   "yearly_high ='" +	stockMap.get("yearly_high") + "', " + 
			   "yearly_low ='" +	stockMap.get("yearly_low") + "', " + 
			   "market_cap ='" + stockMap.get("market_cap") + "', " + 
			   "last_trade_time ='" + stockMap.get("last_trade_time") + "', " + 
			   "last_trade_date ='" + stockMap.get("last_trade_date") + "', " + 
			   "stock_exchange ='" + stockMap.get("stock_exchange") + "', " + 
			   "ask_price ='" +	stockMap.get("ask_price") + "', " + 
			   "ask_price_realtime ='" + stockMap.get("ask_price_realtime") + "', " + 
			   "bid_price ='" +	stockMap.get("bid_price") + "', " + 
			   "bid_price_realtime ='" + stockMap.get("bid_price_realtime") + "', " + 
			   "previous_close_price ='" + stockMap.get("previous_close_price") + "', " + 
			   "open_price ='" + stockMap.get("open_price") + "', " + 
			   "one_year_target_price ='" + stockMap.get("one_year_target_price") + "', " + 
			   "volume_average_3m ='" +	stockMap.get("volume_average_3m") + "', " + 
			   "volume_day ='" + stockMap.get("volume_average_daily") + "', " + 
			   "price_earnings_ratio ='" +	stockMap.get("price_earnings_ratio") + "', " + 
			   "earnings_per_share ='" + stockMap.get("earnings_per_share") + "', " +
			   "company_name =\"" + stockMap.get("company_name") + "\" " +
			   
			   "WHERE symbol = '" + symbol + "' ";
	   
	   //Log.d("DBAdapter.updateStocks", sql);
	   
   
	   mDb.execSQL(sql);
  
	   
   }
   
   public void activateHighAlert(String symbol, boolean isActive){
	   
	  // Log.d("DBAdapter.activateHighAlert()","Symbol = " + symbol + ", isActive = " + isActive);
	   
	   int active;
	   
	   if(isActive)
		   active = 1;
	   else
		   active = 0;
	   
	   String sql = "UPDATE alerts SET high_alert_active = " + active + " WHERE symbol = '" + symbol + "'";
	   
	   mDb.execSQL(sql);

   }
   
   public void activateHighAlert(String symbol, boolean isActive, String price){
	   
	   int active;
	   
	   if(isActive)
		   active = 1;
	   else
		   active = 0;
	   
	   String sql = "UPDATE alerts SET high_alert_active = " + active + " , high_alert_price = '" + price + "' WHERE symbol = '" + symbol + "'";
	   //Log.d("DBAdapter.activateHighAlert", "SQL = " + sql);
	   mDb.execSQL(sql);

	   
   }
   
   public void activateLowAlert(String symbol, boolean isActive){
	   int active;
	   
	   if(isActive)
		   active = 1;
	   else
		   active = 0;
	   
	   String sql = "UPDATE alerts SET low_alert_active = " + active + " WHERE symbol = '" + symbol + "'";
	   mDb.execSQL(sql);

   }
   
   public void activateLowAlert(String symbol, boolean isActive, String price){
	   
	   int active;
	   
	   if(isActive)
		   active = 1;
	   else
		   active = 0;
	   
	   String sql = "UPDATE alerts SET low_alert_active = " + active + " , low_alert_price = '" + price + "' WHERE symbol = '" + symbol + "'";
	   mDb.execSQL(sql);

	   
   }
   
   public Cursor getActiveAlerts(){
	   String sql = "SELECT * FROM alerts WHERE high_alert_active = 1 OR low_alert_active = 1";
	   
	   return mDb.rawQuery(sql, null);
   }
   
   public Cursor getAlerts(String symbol){
	   String sql = "SELECT * FROM alerts WHERE symbol = '" + symbol + "'";

	   return mDb.rawQuery(sql, null);
   }
   

   
}

