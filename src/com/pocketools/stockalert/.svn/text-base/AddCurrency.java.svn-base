package com.pocketools.stockalert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class AddCurrency extends Activity {
	
	private int mLeftPosition;
	private int mRightPosition;
	private ArrayAdapter mLeftAdapter;
	private ArrayAdapter mRightAdapter;
	private String mCurrencyLeft;
	private String mCurrencyRight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
        requestWindowFeature(Window.FEATURE_NO_TITLE);   
        setContentView(R.layout.add_currency); 

        
		Spinner leftSpinner = (Spinner) findViewById(R.id.add_currency_spinner_left);
	    mLeftAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencyArray);
	    mLeftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    leftSpinner.setAdapter(mLeftAdapter);    
	 // This listener is used to set the selected timeframe from the Spinner.
	    leftSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		public void onItemSelected(AdapterView parent, View v, int position,
	          long id) {
				String longCurrency = (String)mLeftAdapter.getItem(position);
				mCurrencyLeft = longCurrency.substring(0,longCurrency.indexOf(" "));    	  	
	      }
	        public void onNothingSelected(AdapterView arg0) {
	          // NOP
	        }
	      });
	    
  
	    
	    Spinner rightSpinner = (Spinner) findViewById(R.id.add_currency_spinner_right);
	    mRightAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencyArray);
	    mRightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    rightSpinner.setAdapter(mRightAdapter);
	        
	 // This listener is used to set the selected timeframe from the Spinner.
	    rightSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		public void onItemSelected(AdapterView parent, View v, int position,
	          long id) {
				String longCurrency = (String)mRightAdapter.getItem(position);
				mCurrencyRight = longCurrency.substring(0,longCurrency.indexOf(" "));    	  	
	      }
	        public void onNothingSelected(AdapterView arg0) {
	          // NOP
	        }
	      });

	    
	    
	    Button addCurrency = (Button)findViewById(R.id.add_currency_button);
	    addCurrency.setOnClickListener(new Button.OnClickListener() {
	    	public void onClick(View v) {   
    			       
    			DBAdapter db = new DBAdapter(getApplicationContext());
                db.open();
                                   
                db.addStock(mCurrencyLeft + mCurrencyRight + "=X", "");	                    
                db.close();
                
                Toast.makeText(v.getContext(), mCurrencyLeft + " to " +  mCurrencyRight + " Added", Toast.LENGTH_SHORT).show();
                
                Intent intent = new Intent(v.getContext(),StockAlert.class);
                intent.putExtra("FORCE_REFRESH", true);
                startActivity(intent);
                
                //Let's finish the current activity
                finish();
	   		}            	
		});
    
    }

    
    private String[] currencyArray = {  
    		"EUR   -  Euro",
    		"GBP   -  United Kingdom Pounds",
    		"USD   -  United States Dollars",
    		"AFN   -  Afghanistan Afghanis",
    		"AED   -  United Arab Emirates Dirhams",
    		"ALL   -  Albania Leke",
    		"ARS   -  Argentina Pesos",
    		"AUD   -  Australia Dollars",
    		"BBD   -  Barbados Dollars",
    		"BDT   -  Bangladesh Taka",
    		"BGN   -  Bulgaria Leva",
    		"BHD   -  Bahrain Dinars",
    		"BMD   -  Bermuda Dollars",
    		"BRL   -  Brazil Reais",
    		"BSD   -  Bahamas Dollars",
    		"CAD   -  Canada Dollars",
    		"CHF   -  Switzerland Francs",
    		"CLP   -  Chile Pesos",
    		"COP   -  Colombia Pesos",
    		"CRC   -  Costa Rica Colones",
    		"CNY   -  China Yuan Renminbi",
    		"CZK   -  Czech Republic Koruny",
    		"DKK   -  Denmark Kroner",
    		"DOP   -  Dominican Republic Pesos",
    		"DZD   -  Algeria Dinars",
    		"EEK   -  Estonia Krooni",
    		"EGP   -  Egypt Pounds",
    		"EUR   -  Euro",
    		"FJD   -  Fiji Dollars",
    		"GBP   -  United Kingdom Pounds",
    		"HKD   -  Hong Kong Dollars",
    		"HRK   -  Croatia Kuna",
    		"HUF   -  Hungary Forint",
    		"IDR   -  Indonesia Rupiahs",
    		"ILS   -  Israel New Shekels",
    		"INR   -  India Rupees",
    		"IQD   -  Iraq Dinars",
    		"IRR   -  Iran Rials",
    		"ISK   -  Iceland Kronur",
    		"JMD   -  Jamaica Dollars",
    		"JOD   -  Jordan Dinars",
    		"KES   -  Kenya Shillings",
    		"KRW   -  South Korea Won",
    		"KWD   -  Kuwait Dinars",
    		"LBP   -  Lebanon Pounds",
    		"LKR   -  Sri Lanka Rupees",
    		"MAD   -  Morocco Dirhams",
    		"MUR   -  Mauritius Rupees",
    		"MXN   -  Mexico Pesos",
    		"MYR   -  Malaysia Ringgits",
    		"NOK   -  Norway Kroner",
    		"NZD   -  New Zealand Dollars",
    		"OMR   -  Oman Rials",
    		"PEN   -  Peru Nuevos Soles",
    		"PHP   -  Philippines Pesos",
    		"PKR   -  Pakistan Rupees",
    		"PLN   -  Poland Zlotych",
    		"QAR   -  Qatar Riyals",
    		"RON   -  Romania New Lei",
    		"RUB   -  Russia Rubles",
    		"SAR   -  Saudi Arabia Riyals",
    		"SDG   -  Sudan Pounds",
    		"SEK   -  Sweden Kronor",
    		"SGD   -  Singapore Dollars",
    		"THB   -  Thailand Baht",
    		"TND   -  Tunisia Dinars",
    		"JPY   -  Japan Yen",
    		"TRY   -  Turkey Lira",
    		"TTD   -  Trinidad and Tobago Dollars",
    		"TWD   -  Taiwan New Dollars",
    		"USD   -  United States Dollars",
    		"VEF   -  Venezuela Bolivares Fuertes",
    		"VND   -  Vietnam Dong",
    		"XAF   -  CFA BEAC Francs",
    		"XAG   -  Silver Ounces",
    		"XAU   -  Gold Ounces",
    		"XCD   -  Eastern Caribbean Dollars",
    		"XDR   -  IMF Special Drawing Right",
    		"XOF   -  CFA BCEAO Francs",
    		"XPD   -  Palladium Ounces",
    		"XPF   -  CFP Francs",
    		"XPT   -  Platinum Ounces",
    		"ZAR   -  South Africa Rand",
    		"ZMK   -  Zambia Kwacha",
    		"NGN   -  Nigerian Naira"};
    

}
