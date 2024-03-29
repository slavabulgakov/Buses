package ru.slavabulgakov.buses;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.slavabulgakov.buses.ParserWebPageTask.ParserType;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

@SuppressLint("SimpleDateFormat")
public class MyApplication extends Application {
	private Share _share;
	
	public static final String PREF_NAME = "BUSES_PREF";
	public static final String ORDERS_NAME = "ORDERS";
	public static final String LOGIN = "LOGIN";
	public static final String PASSWORD = "PASWWORD";
	
	
	public MyApplication() {
		super();
		_representation = new Representation(this);
	}
	
	
	public Share getShare() {
		if (_share == null) {
			_share = new Share(this);
		}
		return _share;
	}
	
	
	public interface IRepresentation {
		public void onStartParsing();
		public void onFinishParsingBookingPage();
		public void onFinishParsingDetailPage();
		public void onFinishParsingOrdersPage();
		public void onFinishParsingEmpty();
		public void onFinishParsingConnectionError();
		public void onCancelParsing();
		
		public void onStartBooking();
		public void onFinishAuthSuccess();
		public void onFinishAuthDeny();
		public void onFinishBookingSuccess();
		public void onFinishBookingDeny();
		public void onCancelBooking();
		
		public void setCurrentActivity(Activity activity);
		public Activity getCurrentActivity();
		
		public void setWithoutProgress(Boolean withoutProgress);
	}
	
	
	/////////////////////
	// Representation ===
	private IRepresentation _representation;
	public IRepresentation getRepresentation(){
		return _representation;
	}
	//===================
	/////////////////////
	
	
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
		
		String url = "https://bashauto.ru/booking/?fromName=" + from + "&toName=" + to + "&when=" + getStrDate(date);
		
		_parserWebPageTask = new ParserWebPageTask(ParserType.BOOKING_PAGE, this, _representation);
		_parserWebPageTask.execute(url);
	}
	
	public void detail_show(String url) {
		_parserWebPageTask = new ParserWebPageTask(ParserType.DETAIL_PAGE, this, _representation);
		_parserWebPageTask.execute(url);
	}
	
	public void cancel() {
		_parserWebPageTask.cancel(true);
	}
	
	public Boolean enterPin;
	
	
	
	
	
	
	////////////////////////////
	// arrayListScheduleData ===
	private ArrayList<ResultElement> _arrayListScheduleData;
	public ArrayList<ResultElement> getArrayListScheduleData() {
		return _arrayListScheduleData;
	}
	public void setArrayListScheduleData(ArrayList<ResultElement> array) {
		_arrayListScheduleData = array;
	}
	//==========================
	////////////////////////////
	
	
	
	//////////////////////
	// arrayListOrders ===
	private ArrayList<OrdersElement> _arrayListOrders;
	public ArrayList<OrdersElement> getArrayListOrders() {
		return _arrayListOrders;
	}
	public void setArrayListOrders(ArrayList<OrdersElement> array) {
		_arrayListOrders = array;
	}
	//====================
	//////////////////////
	
	
	
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
	public String getTicketType() {
		if (_ticketType == TicketType.Child) {
			return "%D0%94%D0%B5%D1%82%D1%81%D0%BA%D0%B8%D0%B9";
		}
		return "%D0%9F%D0%BE%D0%BB%D0%BD%D1%8B%D0%B9";
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
			_phpSessId = SslJsoup.connect("https://bashauto.ru/booking/")
									.execute()
									.cookie(PHPSESSID);
		}
		
		return _phpSessId;
	}
	//================
	//////////////////
	
	
	private String _orderNumber;
	public void setOrderNumber(String orderNumber){
		_orderNumber = orderNumber;
	}
	public String getOrderNumber() {
		return _orderNumber;
	}
	
	private Boolean _bookingIsGoing = false;
	public void setBookingIsGoing(Boolean bookingIsGoing){
		_bookingIsGoing = bookingIsGoing;
	}
	public Boolean getBookingIsGoing(){
		return _bookingIsGoing;
	}
	//=======================================================
	/////////////////////////////////////////////////////////
	
}
