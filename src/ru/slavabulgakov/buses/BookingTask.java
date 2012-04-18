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
	
	private String getSessId() {
		String bookLink = _app.getArrayListScheduleData().get(_app.getCurrentPosition()).bookLink;
		String sessId = bookLink.split("sessid=")[1];
		return sessId;
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
				    						.data(	"backurl", "/booking/", 
				    								"AUTH_FORM", "Y", 
				    								"TYPE", "AUTH", 
				    								"USER_LOGIN", login, 
				    								"USER_PASSWORD", password, 
				    								"Login", "Войти")
				    						.cookie("PHPSESSID", _app.getPhpSessId())
				    						.execute();
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
			Connection.Response res = Jsoup.connect("http://bashauto.ru/booking/")
				    						.method(Method.POST)
				    						.data(	"ps", "1", 
				    								"sessid", getSessId(), 
				    								"BACK", "", 
				    								"CurrentStep", "3")
				    						.cookie("PHPSESSID", phpSessId)
				    						.execute();
			doc = res.parse();
			String text = doc.text();
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
				    						.data(	"legacy", _app.getString(R.string.legacy), 
				    								"agree", "y",
				    								"ticket%5Bticket%5D%5B0%5D%5Btype%5D", "%D0%9F%D0%BE%D0%BB%D0%BD%D1%8B%D0%B",
				    								"sessid", getSessId(),
				    								"BACK", "",
				    								"CurrentStep", "4")
				    						.cookie("PHPSESSID", phpSessId)
				    						.execute();
			
			res = Jsoup.connect("http://bashauto.ru/booking/")
					.method(Method.POST)
					.data(	"sessid", getSessId(),
							"BACK", "",
							"CurrentStep", "5")
					.cookie("PHPSESSID", phpSessId)
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
