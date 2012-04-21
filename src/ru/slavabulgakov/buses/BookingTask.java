package ru.slavabulgakov.buses;

import java.io.IOException;
import java.net.URLEncoder;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import ru.slavabulgakov.buses.MyApplication.IRepresentation;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

enum RequestType {
	AUTH,
	STEP2,
	STEP3
}

enum BookingRequestResult {
	STEP2_YES,
	STEP2_REQ_AUTH,
	AUTH_YES,
	AUTH_NO,
	REQ_DATA,
	DONE
}

public class BookingTask extends AsyncTask<String, Void, BookingRequestResult> {
	public static final String PHPSESSID = "PHPSESSID";
	private Boolean _cancelled;
	private IRepresentation _callback;
	private Boolean _isLoading;
	private MyApplication _app;
	private RequestType _requestType;
	
	public BookingTask(MyApplication app, IRepresentation callback, RequestType requestType) {
		super();
		_app = app;
		_callback = callback;
		_requestType = requestType;
	}
	
	

	@Override
	protected void onCancelled() {
		super.onCancelled();
		_cancelled = true;
	}



	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		_cancelled = false;
		_callback.onStartBooking();
		_isLoading = true;
	}
	
	
	private String getPhpSessId() {
//		SharedPreferences settings = _app.getSharedPreferences(MyApplication.PREF_NAME, 0);
	    String phpSessId = null;//settings.getString(PHPSESSID, null);
	    Document doc = null;
	    if (phpSessId == null) {
	    	try {
	    		Connection.Response res = Jsoup.connect("http://bashauto.ru/booking/")
	    										.method(Method.GET)
	    										.execute();
	    		doc = res.parse();
	    		phpSessId = res.cookie("PHPSESSID");
//	    		SharedPreferences.Editor editor = settings.edit();
//	    		editor.putString(PHPSESSID, phpSessId);
//	    		editor.commit();
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    }
	    return phpSessId;
	}
	
	private String getBookLink() {
		String bookLink = _app.getArrayListScheduleData().get(_app.getCurrentPosition()).bookLink;
		return "http://bashauto.ru" + bookLink;
	}
	
	private String getSessId() {
		String bookLink = getBookLink();
		String sessId = bookLink.split("sessid=")[1];
		return sessId;
	}
	
	private Connection getStandartConnection(Connection conn) {
		return conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
					.header("Accept-Charset", "windows-1251,utf-8;q=0.7,*;q=0.3")
					.header("Accept-Encoding", "gzip,deflate,sdch")
					.header("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4")
					.header("Connection", "keep-alive")
					.cookie("PHPSESSID", _app.getPhpSessId())
					.cookie("BITRIX_SM_SOUND_LOGIN_PLAYED", "Y")
					.cookie("BITRIX_SM_LOGIN", _app.getLogin())
					.cookie("__utma", "141051809.1702567638.1333279036.1335008606.1335012401.26")
					.cookie("__utmb", "141051809.30.10.1335012401")
					.cookie("__utmc", "141051809")
					.cookie("__utmz", "141051809.1333279036.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)")
					.cookie("_ym_visorc", "w")
					.header("Host", "bashauto.ru")
					.userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
	}
	
	private Boolean auth() {
		String login = _app.getLogin();
		String password = _app.getPassword();
		if (login == null || password == null) {
			return false;
		}
		try {
			Connection.Response res = Jsoup.connect("http://bashauto.ru/booking/?login=yes")
									.method(Method.POST)
									.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
									.header("Accept-Charset", "windows-1251,utf-8;q=0.7,*;q=0.3")
									.header("Accept-Encoding", "gzip,deflate,sdch")
									.header("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4")
									.header("Cache-Control", "max-age=0")
									.header("Connection", "keep-alive")
//									.header("Content-Length", "116")
									.header("Content-Type", "application/x-www-form-urlencoded")
									.cookie("PHPSESSID", _app.getPhpSessId())
									.cookie("BITRIX_SM_SOUND_LOGIN_PLAYED", "Y")
									.cookie("BITRIX_SM_LOGIN", _app.getLogin())
									.cookie("__utma", "141051809.1702567638.1333279036.1335008606.1335012401.26")
									.cookie("__utmb", "141051809.30.10.1335012401")
									.cookie("__utmc", "141051809")
									.cookie("__utmz", "141051809.1333279036.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)")
									.cookie("_ym_visorc", "w")
									.header("Host", "bashauto.ru")
									.header("Origin", "http://bashauto.ru")
									.referrer("http://bashauto.ru/")
									.userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19")
									.data(	"backurl", "%2F", 
											"AUTH_FORM", "Y", 
											"TYPE", "AUTH", 
											"USER_LOGIN", login, 
											"USER_PASSWORD", password, 
											"Login", "%D0%92%D0%BE%D0%B9%D1%82%D0%B8")
									.execute();
    		
    		String retCookie = res.cookie("PHPSESSID");
    		Document doc = res.parse();
    		int size = doc.select("input.field[name=USER_LOGIN]").size();
    		if (size > 0) {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private Boolean step2() {
		Document doc = null;
		try {
			String phpSessId = _app.getPhpSessId();
			Connection.Response res = Jsoup.connect(getBookLink())
				    						.method(Method.GET)
				    						.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
				    						.header("Accept-Charset", "windows-1251,utf-8;q=0.7,*;q=0.3")
				    						.header("Accept-Encoding", "gzip,deflate,sdch")
				    						.header("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4")
				    						.header("Connection", "keep-alive")
				    						.cookie("PHPSESSID", phpSessId)
				    						.cookie("BITRIX_SM_SOUND_LOGIN_PLAYED", "Y")
				    						.cookie("BITRIX_SM_LOGIN", _app.getLogin())
				    						.cookie("__utma", "141051809.1702567638.1333279036.1335008606.1335012401.26")
				    						.cookie("__utmb", "141051809.40.10.1335012401")
				    						.cookie("__utmc", "141051809")
				    						.cookie("__utmz", "141051809.1333279036.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)")
				    						.cookie("_ym_visorc", "w")
				    						.header("Host", "bashauto.ru")
				    						.referrer("http://bashauto.ru/booking/?fromName=%D0%A3%D1%84%D0%B0+%D0%AE%D0%B6%D0%BD%D1%8B%D0%B9+%D0%90%D0%92&toName=%D0%9D%D0%B5%D1%84%D1%82%D0%B5%D0%BA%D0%B0%D0%BC%D1%81%D0%BA&when=22.04.2012")
				    						.userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19")
				    						.execute();
			String retCookie = res.cookie("PHPSESSID");
			
			retCookie = res.cookie("PHPSESSID");
			doc = res.parse();
			int size = doc.select("table.sale_order_full_table").size();
			if (size > 0) { // пользователь не авторизован
				if (auth()) {
					return step2();
				} else {
					return false; 
				}
			}
	    } catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private Boolean step3() {
		Document doc = null;
		try {
			String phpSessId = _app.getPhpSessId();
			
			Connection.Response res = Jsoup.connect("http://bashauto.ru/booking/")
					.method(Method.POST)
					.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
					.header("Accept-Charset", "windows-1251,utf-8;q=0.7,*;q=0.3")
					.header("Accept-Encoding", "gzip,deflate,sdch")
					.header("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4")
					.header("Cache-Control", "max-age=0")
					.header("Connection", "keep-alive")
//					.header("Content-Length", "64")
					.header("Content-Type", "application/x-www-form-urlencoded")
					.cookie("PHPSESSID", phpSessId)
					.cookie("BITRIX_SM_SOUND_LOGIN_PLAYED", "Y")
				    .cookie("BITRIX_SM_LOGIN", _app.getLogin())
				    .cookie("__utma", "141051809.1702567638.1333279036.1335008606.1335012401.26")
				    .cookie("__utmb", "141051809.40.10.1335012401")
				    .cookie("__utmc", "141051809")
				    .cookie("__utmz", "141051809.1333279036.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)")
				    .cookie("_ym_visorc", "w")
				    .header("Host", "bashauto.ru")
				    .header("Origin", "http://bashauto.ru")
					.referrer(getBookLink())
					.userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19")
					.data(	"ps", "1", 
							"sessid", getSessId(), 
							"BACK", "", 
							"CurrentStep", "3")
					.execute();
			
			res = Jsoup.connect("http://bashauto.ru/booking/")
				    						.method(Method.POST)
				    						.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
				    						.header("Accept-Charset", "windows-1251,utf-8;q=0.7,*;q=0.3")
				    						.header("Accept-Encoding", "gzip,deflate,sdch")
				    						.header("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4")
				    						.header("Cache-Control", "max-age=0")
				    						.header("Connection", "keep-alive")
//				    						.header("Content-Length", "45643")
				    						.header("Content-Type", "application/x-www-form-urlencoded")
				    						.cookie("PHPSESSID", phpSessId)
				    						.cookie("BITRIX_SM_SOUND_LOGIN_PLAYED", "Y")
				    						.cookie("BITRIX_SM_LOGIN", _app.getLogin())
				    						.cookie("__utma", "141051809.1702567638.1333279036.1335008606.1335012401.26")
				    						.cookie("__utmb", "141051809.40.10.1335012401")
				    						.cookie("__utmc", "141051809")
				    						.cookie("__utmz", "141051809.1333279036.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)")
				    						.cookie("_ym_visorc", "w")
				    						.header("Host", "bashauto.ru")
				    						.header("Origin", "http://bashauto.ru")
				    						.referrer("http://bashauto.ru/booking/")
				    						.userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19")
				    						.data(	"legacy", _app.getString(R.string.legacy), 
				    								"agree", "y",
				    								"ticket%5Bticket%5D%5B0%5D%5Btype%5D", "%D0%9F%D0%BE%D0%BB%D0%BD%D1%8B%D0%B",
				    								"sessid", getSessId(),
				    								"BACK", "",
				    								"CurrentStep", "4")
				    						.execute();
			
			res = Jsoup.connect("http://bashauto.ru/booking/")
					.method(Method.POST)
					.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
					.header("Accept-Charset", "windows-1251,utf-8;q=0.7,*;q=0.3")
					.header("Accept-Encoding", "gzip,deflate,sdch")
					.header("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4")
					.header("Cache-Control", "max-age=0")
					.header("Connection", "keep-alive")
//					.header("Content-Length", "59")
					.header("Content-Type", "application/x-www-form-urlencoded")
				    .cookie("PHPSESSID", phpSessId)
				    .cookie("BITRIX_SM_SOUND_LOGIN_PLAYED", "Y")
				    .cookie("BITRIX_SM_LOGIN", _app.getLogin())
				    .cookie("__utma", "141051809.1702567638.1333279036.1335008606.1335012401.26")
				    .cookie("__utmb", "141051809.40.10.1335012401")
				    .cookie("__utmc", "141051809")
				    .cookie("__utmz", "141051809.1333279036.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)")
				    .cookie("_ym_visorc", "w")
				    .header("Host", "bashauto.ru")
				    .header("Origin", "http://bashauto.ru")
				    .referrer("http://bashauto.ru/booking/")
				    .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19")
					.data(	"sessid", getSessId(),
							"BACK", "",
							"CurrentStep", "5")
					.followRedirects(false)
					.execute();
			
			String url = res.header("Location");
			int statusCode = res.statusCode();
			
			url = res.url().toString();
			String method = res.method().toString();
			
			
			Elements els = doc.select("span.cd60 font11 tc");
			int size = els.size();
			
			if (size > 0) {
				String orderNumber = els.get(0).text();
				int i = 0;
			}
	    } catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	protected BookingRequestResult doInBackground(String... url) {
		switch (_requestType) {
		case STEP2:
			if (step2()) {
				return BookingRequestResult.REQ_DATA;
			}
			return BookingRequestResult.STEP2_REQ_AUTH;
			
		case STEP3:
			if (step3()) {
				return BookingRequestResult.DONE;
			}
			break;
			
		case AUTH:
			if (auth()) {
				return BookingRequestResult.AUTH_YES;
			} else {
				return BookingRequestResult.AUTH_NO;
			}
			

		default:
			break;
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(BookingRequestResult result) {
		super.onPostExecute(result);
		
		switch (result) {
		case STEP2_REQ_AUTH:
			_callback.onFinishBookingRequestAuth();
			break;
			
		case AUTH_YES:
			_callback.onFinishBookingAuthSuccess();
			break;
			
		case REQ_DATA:
			_app.increaseBookingStep();
			_callback.onFinishBookingReqData();
			break;

		default:
			_callback.onFinishBooking();
			break;
		}
	}

}
