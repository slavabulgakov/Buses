package ru.slavabulgakov.buses;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.slavabulgakov.buses.ParserWebPageTask.ParserType;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

public class MyApplication extends Application {
	private Share _share;
	
	public static final String PREF_NAME = "BUSES_PREF";
	public static final String LOGIN = "LOGIN";
	public static final String PASSWORD = "PASWWORD";
	
	
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
		
		public void onStartBooking();
		public void onFinishBookingRequestAuth();
		public void onFinishBookingAuthSuccess();
		public void onFinishBookingReqData();
		public void onFinishBookingAuthDeny();
		public void onFinishBooking();
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
	
	
	
	//////////////////////
	// currentPosition ===
	private int _position;
	public int getCurrentPosition() {
		return _position;
	}
	public void setCurrentPostion(int position) {
		_position = position;
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
			_date = new Date(new Date().getYear(), new Date().getMonth(), new Date().getDate());
		}
		return _date;
	}
	//=========
	///////////
	
	
	
	/////////////////////////////////////////////////////////
	// booking ==============================================
	
	//////////////////////
	//ticket type ===
	enum TicketType {
		Fully,
		Child
	}
	private TicketType _ticketType;
	public TicketType getTicketType() {
		return _ticketType;
	}
	public void setTicketType(TicketType ticketType) {
		_ticketType = ticketType;
	}
	//====================
	//////////////////////


	//////////////////
	//currentLogin ===
	private String _login;
	public String getLogin() {
		if (_login == null) {
			SharedPreferences settings = getSharedPreferences(PREF_NAME, 0);
			_login = settings.getString(LOGIN, null);
		}
		return _login;
	}
	public void setLogin(String login) {
		_login = login;
		SharedPreferences settings = getSharedPreferences(PREF_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(LOGIN, _login);
		editor.commit();
	}
	//================
	//////////////////



	/////////////////////
	//currentPassword ===
	private String _password;
	public String getPassword() {
		if (_password == null) {
			SharedPreferences settings = getSharedPreferences(PREF_NAME, 0);
			_password = settings.getString(PASSWORD, null);
		}
		return _password;
	}
	public void setPassword(String password) {
		_password = password;
		SharedPreferences settings = getSharedPreferences(PREF_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PASSWORD, _password);
		editor.commit();
	}
	//================
	//////////////////
	
	
	/////////////////////
	//php session id===
	private String _phpSessId;
	public static final String PHPSESSID = "PHPSESSID";
	public String getPhpSessId() {
		if (_phpSessId == null) {
			try {
	    		Connection.Response res = Jsoup.connect("http://bashauto.ru")
	    										.method(Method.GET)
	    										.timeout(3000000)
	    										.execute();
	    		_phpSessId = res.cookie("PHPSESSID");
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
		}
		
		return _phpSessId;
	}
	//================
	//////////////////
	
	
	
	private int _bookingStep = 2;
	public void increaseBookingStep() {
		_bookingStep++;
	}
	public String booking() {
		BookingTask bookingTask = null;
		switch (_bookingStep) {
		case 2:
			bookingTask = new BookingTask(this, (IRepresentation)_currentActivity, RequestType.STEP2);
			break;
			
		case 3:
			bookingTask = new BookingTask(this, (IRepresentation)_currentActivity, RequestType.STEP3);
			break;

		default:
			break;
		}
		
		bookingTask.execute("");
		return "";
	}
	
	public String auth() {
		BookingTask bookingTask = new BookingTask(this, (IRepresentation)_currentActivity, RequestType.AUTH);
		bookingTask.execute("");
		return "";
	}
	
	//=======================================================
	/////////////////////////////////////////////////////////
	
}
