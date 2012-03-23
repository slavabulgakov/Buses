package ru.slavabulgakov.buses;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ru.slavabulgakov.buses.TwApp;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

public class MyApplication extends Application {
	private ArrayList<Trip> _arrayListScheduleData;
	private DetailTrip _currentDetailTrip;
	private Date _date;
	private MyCallback _callback;
	private Boolean _progressDialogShowed = false;
	private String _from;
	private String _to;
	private ParserWebPageTask _parserWebPageTask;
	private TwApp _twApp;
	
	public Boolean enterPin;
	
	public TwApp getTwApp() {
		if (_twApp == null) {
			_twApp = new TwApp();
		}
		return _twApp;
	}
    
	public String getFrom() {
		return _from;
	}
	
	public void setFrom(String from) {
		_from = from;
	}
	
	public String getTo() {
		return _to;
	}
	
	public void setTo(String to) {
		_to = to;
	}
	
	
	public Boolean getProgressDialogShowed() {
		return _progressDialogShowed;
	}
	
	public enum RequestResult {
		SUCCESS,
	    EMPTY_RESPONSE, 
	    CONNECTION_ERROR,
	    CANCELED
	}
	
	interface ITrip {
		void put(String key, String value);
	}
	
	public ArrayList<Trip> parserBooking(Document doc) {
		Elements elements = doc.select("table.timesheet");
        if (elements.isEmpty()) { // ÌÂÚ ‰‡ÌÌ˚ı ÔÓ ‰‡ÌÌÓÏÛ Á‡ÔÓÒÛ
			return null;
		}
        Element el = elements.get(0);
        elements = el.getElementsByTag("tr");
        ArrayList<Trip> arrayList = new ArrayList<Trip>();
        for (Element element : elements) {
        	try {
        		String startTime = element.select("div.time").get(0).text();
            	String endTime = element.select("div.desttime").get(0).text();
            	String[] price = element.select("div.price").get(0).text().split(",");
            	String[] seat = element.select("div.seat").get(0).text().split(getString(R.string.split_str));
            	String detailLink = element.select("div.name").get(0).select("a").attr("href");
            	
            	Trip w = new Trip();
            	w.put("priceRub", price[0]);
            	w.put("priceKop", price[1]);
            	w.put("timeStart", startTime);
            	w.put("timeEnd", endTime);
            	w.put("allSeats", seat[1]);
            	w.put("freeSeats", seat[0]);
            	w.put("detailLink", detailLink);
            	arrayList.add(w);
			} catch (Exception e) {
				continue;
			}
		}
        return arrayList;
	}
	
	public DetailTrip parserDetail(Document doc) {
		DetailTrip dt = getCurrentDetailTrip();
		dt.arrayList = new ArrayList<DetailTripItem>();
		dt.tripName  = doc.select("h1.l-indent-bottom").get(0).text();
		dt.number = doc.select("td.additional").get(0).select("p").get(0).text();
		dt.busModel = doc.select("td.additional").get(0).select("p").get(1).text();
		dt.carrier = doc.select("td.additional").get(0).select("p").get(2).text();
		dt.periodicity = doc.select("td.additional").get(0).select("p").get(3).text();
		
		Elements elements = doc.select("table.common");
        if (elements.isEmpty()) {
			return null;
		}
        Element el = elements.get(0);
        elements = el.getElementsByTag("tr");
        for (Element element : elements) {
        	try {
        		DetailTripItem dti = new DetailTripItem();
        		dti.route = element.select("td").get(0).text();
            	dti.arrival = element.select("td").get(1).text();
            	dti.camp = element.select("td").get(2).text();
            	dti.departure = element.select("td").get(3).text();
            	dti.distance = element.select("td").get(4).text();
            	if (dti.arrival.contains("-") || dti.departure.contains("-")) {
					dti.arrival = null;
					dti.departure = null;
				}
            	if (dti.distance.contains("-")) {
					dti.distance = null;
				}
            	dt.arrayList.add(dti);
			} catch (Exception e) {
				continue;
			}
		}
        return dt;
	}
	
	interface MyCallback {
		void callbackSuccessResult();
	    void callbackShowProgressDialog();
	    void callbackHideProgressDialog();
	    void callbackShowAlertDialog(int titleId, int messageId, int iconId);
	}
	
	public enum ParserType {
		BOOKING_PAGE,
		DETAIL_PAGE
	};
	
	public class ParserWebPageTask extends AsyncTask<String, Void, RequestResult> {
		private ParserType _parser;
		private Boolean _canceled = false;
		
    	public ParserWebPageTask(ParserType parser) {
			super();
			_parser = parser;
		}

		@Override
    	protected void onPreExecute(){
    	   super.onPreExecute();
    	   _callback.callbackShowProgressDialog();
    	   _progressDialogShowed = true;
    	   _canceled = false;
    	} 
    	
    	@Override
    	protected RequestResult doInBackground(String... url) {
    		try {
    			Log.i("info", "start download");
        		Connection conn = Jsoup.connect(url[0]).timeout(300000);
    	        Document doc = conn.get();
    	        
    	        if (_canceled) {
					return RequestResult.CANCELED;
    	        }
    	        
    	        Log.i("info", "end download");
    	        
    	        Log.i("info", "start parsing");
    	        switch (_parser) {
				case BOOKING_PAGE:
					_arrayListScheduleData = parserBooking(doc);
					break;
					
				case DETAIL_PAGE:
					_currentDetailTrip = parserDetail(doc);
					break;

				default:
					break;
				}
    	        Log.i("info", "end parsing");
    	        
    	        if (_arrayListScheduleData == null) {
					return RequestResult.EMPTY_RESPONSE;
				}
    			
    			
    		} catch (IOException e) {
    			e.printStackTrace();
    			return RequestResult.CONNECTION_ERROR;
    		}
    		
            return RequestResult.SUCCESS;
    	}
    	
    	

    	@Override
		protected void onCancelled() {
			super.onCancelled();
			_canceled = true;
			_progressDialogShowed = false;
		}

		@Override
    	protected void onPostExecute(RequestResult result) {
    		_callback.callbackHideProgressDialog();
    		_progressDialogShowed = false;
    		
    		switch (result) {
			case SUCCESS:
//				Bundle b = new Bundle();
//            	b.putString("from", _textViewFrom.getText().toString());
//            	b.putString("to", _textViewTo.getText().toString());
//            	startActivity(new Intent(Test2Activity.this, ResultActivity.class).putExtras(b));
    			_callback.callbackSuccessResult();
				break;
				
			case EMPTY_RESPONSE:
				_callback.callbackShowAlertDialog(R.string.empty_response_title, R.string.empty_response_message, android.R.drawable.ic_dialog_alert);
				break;
				
			case CONNECTION_ERROR:
				_callback.callbackShowAlertDialog(R.string.connection_error_title, R.string.connection_error_message, android.R.drawable.ic_dialog_alert);
				break;

			default:
				break;
			}
    	}
    }
	
	public void loadData(MyCallback callback, String url, ParserType parser) {
		_callback = callback;
		_parserWebPageTask = new ParserWebPageTask(parser);
		_parserWebPageTask.execute(url);
	}
	
	public void cancelLoading() {
		_parserWebPageTask.cancel(true);
	}
	
	
	public ArrayList<Trip> getArrayListScheduleData() {
		if (_arrayListScheduleData == null) {
			_arrayListScheduleData = new ArrayList<Trip>();
		}
		return _arrayListScheduleData;
	}
	
	public DetailTrip getCurrentDetailTrip() {
		if (_currentDetailTrip == null) {
			_currentDetailTrip = new DetailTrip();
		}
		return _currentDetailTrip;
	}
	
	public String getStrDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		return formatter.format(getDate());
	}
	
	public Date getDate() {
		if (_date == null) {
			_date = new Date();
		}
		return _date;
	}
	
	public void setDate(Date date) {
		_date = date;
	}
}
