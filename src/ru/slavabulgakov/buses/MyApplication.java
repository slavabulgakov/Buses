package ru.slavabulgakov.buses;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.slavabulgakov.buses.ParserWebPageTask.ParserType;

import android.app.Activity;
import android.app.Application;

public class MyApplication extends Application {
	private Share _share;
	
	
	public MyApplication() {
		super();
	}
	
	
	public Share getShare() {
		if (_share == null) {
			_share = new Share(this);
		}
		return _share;
	}
	
	
	public interface IRepresentation {
		public void onStartParsing();
		public void onFinishParsing();
		public void onFinishParsingEmpty();
		public void onFinishParsingConnectionError();
		public void onCancelParsing();
	}
	
	
	
	//////////////////////
	// currentActivity ===
	private Activity _currentActivity;
	public Activity getCurrentActivity() {
		return _currentActivity;
	}
	public void setCurrentActivity(Activity activity) {
		_currentActivity = activity;
	}
	//====================
	//////////////////////
	
	
	private ParserWebPageTask _parserWebPageTask;
	
	
	public Boolean isLoading() {
		if (_parserWebPageTask != null) {
			return _parserWebPageTask.isLoading();
		}
		return false;
	}
		
	
	public String getStrDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		return formatter.format(date);
	}
	
	

	public void show(String from, String to, Date date) {
		try {
			from = URLEncoder.encode(from, "UTF-8");
			to = URLEncoder.encode(to, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String url = "http://bashauto.ru/booking/?fromName=" + from + "&toName=" + to + "&when=" + getStrDate(date);
		
		_parserWebPageTask = new ParserWebPageTask(ParserType.BOOKING_PAGE, this, (IRepresentation)_currentActivity);
		_parserWebPageTask.execute(url);
	}
	
	public void detail_show(String url) {
		_parserWebPageTask = new ParserWebPageTask(ParserType.DETAIL_PAGE, this, (IRepresentation)_currentActivity);
		_parserWebPageTask.execute(url);
	}
	
	public void cancel() {
		_parserWebPageTask.cancel(true);
	}
	
	public Boolean enterPin;
	
	
	interface ITrip {
		void put(String key, String value);
	}
	
	
	
	
	
	////////////////////////////
	// arrayListScheduleData ===
	private ArrayList<Trip> _arrayListScheduleData;
	public ArrayList<Trip> getArrayListScheduleData() {
		return _arrayListScheduleData;
	}
	public void setArrayListScheduleData(ArrayList<Trip> array) {
		_arrayListScheduleData = array;
	}
	//==========================
	////////////////////////////
	
	
	
	////////////////////////
	// currentDetailTrip ===
	private DetailTrip _currentDetailTrip;
	public DetailTrip getCurrentDetailTrip() {
		return _currentDetailTrip;
	}
	public void setCurrentDetailTrip(DetailTrip detailTrip) {
		_currentDetailTrip = detailTrip;
	}
	//======================
	////////////////////////
	
	
	
	
	///////////
	// from ===
	private String _from;
	public void setFrom(String from) {
		_from = from;
	}
	public String getFrom() {
		return _from;
	}
	//=========
	///////////
	
	
	
	
	/////////
	// to ===
	private String _to;
	public void setTo(String to) {
		_to = to;
	}
	public String getTo() {
		return _to;
	}
	//=======
	/////////
	
	
	
	
	///////////
	// date ===
	private Date _date;
	public void setDate(Date date) {
		_date = date;
	}
	public Date getDate() {
		if (_date == null) {
			_date = new Date();
		}
		return _date;
	}
	//=========
	///////////
	
}
