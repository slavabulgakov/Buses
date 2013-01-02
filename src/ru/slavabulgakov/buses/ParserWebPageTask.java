package ru.slavabulgakov.buses;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ru.slavabulgakov.buses.MyApplication.IRepresentation;


import android.os.AsyncTask;
import android.util.Log;



enum RequestResult {
	SUCCESS,
    EMPTY_RESPONSE, 
    CONNECTION_ERROR,
    CANCELED
}


public class ParserWebPageTask extends AsyncTask<String, Void, RequestResult> {
	
	public enum ParserType {
		BOOKING_PAGE,
		DETAIL_PAGE,
		ORDERS_PAGE
	};

	
	
	private ParserType _parserType;
	private Boolean _canceled = false;
	private Boolean _isLoading;
	private MyApplication _app;
	private IRepresentation _parserCallback;
	
	public Boolean isLoading() {
		return _isLoading;
	}
	
	public ParserWebPageTask(ParserType parserType, MyApplication app, IRepresentation parserCallback) {
		super();
		_parserType = parserType;
		_app = app;
		_parserCallback = parserCallback;
	}
	
	
	
	public ArrayList<ResultElement> parserBooking(Document doc) {
		Elements elements = doc.select("table.timesheet");
	    if (elements.isEmpty()) { // ÌÂÚ ‰‡ÌÌ˚ı ÔÓ ‰‡ÌÌÓÏÛ Á‡ÔÓÒÛ
			return null;
		}
	    Element el = elements.get(0);
	    elements = el.getElementsByTag("tr");
	    ArrayList<ResultElement> arrayList = new ArrayList<ResultElement>();
	    for (Element element : elements) {
	    	try {
	    		String startTime = element.select("div.time").get(0).text();
	        	String endTime = element.select("div.desttime").get(0).text();
	        	String[] price = element.select("div.price").get(0).text().split(",");
	        	String[] seat = element.select("div.seat").get(0).text().split(_app.getString(R.string.split_str));
	        	String detailLink = element.select("div.name").get(0).select("a").attr("href");
	        	String bookLink = element.select("td").get(5).select("a").get(1).attr("href");
	        	
	        	ResultElement re = new ResultElement();
	        	re.priceRub = price[0];
	        	re.priceKop = price[1];
	        	re.timeStart = startTime;
	        	re.timeEnd = endTime;
	        	re.allSeats = seat[1];
	        	re.freeSeats = seat[0];
	        	re.detailLink = detailLink;
	        	re.bookLink = bookLink;
	        	arrayList.add(re);
			} catch (Exception e) {
				continue;
			}
		}
	    return arrayList;
	}
	
	public ArrayList<OrdersElement> parserOrders(Document doc) {
		Elements elements = doc.select("table.timesheet");
	    if (elements.isEmpty()) { // ÌÂÚ ‰‡ÌÌ˚ı ÔÓ ‰‡ÌÌÓÏÛ Á‡ÔÓÒÛ
			return null;
		}
	    Element el = elements.get(0);
	    elements = el.getElementsByTag("tr");
	    ArrayList<OrdersElement> arrayList = new ArrayList<OrdersElement>();
	    for (Element element : elements) {
	    	try {
//	    		String startTime = element.select("div.time").get(0).text();
//	        	String endTime = element.select("div.desttime").get(0).text();
//	        	String[] price = element.select("div.price").get(0).text().split(",");
//	        	String[] seat = element.select("div.seat").get(0).text().split(_app.getString(R.string.split_str));
	        	String numberOrder = element.select("div.name").get(0).text();
//	        	String bookLink = element.select("td").get(5).select("a").get(1).attr("href");
	        	
	        	OrdersElement re = new OrdersElement();
	        	re.numberOrder = numberOrder;
	        	arrayList.add(re);
			} catch (Exception e) {
				continue;
			}
		}
	    return arrayList;
	}

	public DetailTrip parserDetail(Document doc) {
		DetailTrip dt = new DetailTrip();
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
	
	

	@Override
	protected void onPreExecute(){
	   super.onPreExecute();
		_parserCallback.onStartParsing();
	   _canceled = false;
	   _isLoading = true;
	} 
	
	@Override
	protected RequestResult doInBackground(String... url) {
		try {
			Log.i("info", "start download");
			String phpSessId = _app.getPhpSessId();

	        Document doc = SslJsoup.connect(url[0])
	        		.cookie(MyApplication.PHPSESSID, phpSessId)
	        		.execute()
	        		.parse();
	        
	        if (_canceled) {
				return RequestResult.CANCELED;
	        }
	        
	        Log.i("info", "end download");
	        
	        Log.i("info", "start parsing");
	        switch (_parserType) {
			case BOOKING_PAGE:
				_app.setArrayListScheduleData(parserBooking(doc));
				break;
				
			case DETAIL_PAGE:
				_app.setCurrentDetailTrip(parserDetail(doc));
				break;
				
			case ORDERS_PAGE:
				if (doc.select("div.bx-auth-title").size() > 0) {
					if (!BookingTask.auth(_app, _canceled)) {
						break;
					}
				}
				
				_app.setArrayListOrders(parserOrders(doc));
				break;

			default:
				break;
			}
	        Log.i("info", "end parsing");
	        
	        if (_parserType == ParserType.BOOKING_PAGE && _app.getArrayListScheduleData() == null) {
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
		_parserCallback.onCancelParsing();
		_isLoading = false;
	}

	@Override
	protected void onPostExecute(RequestResult result) {
		
		switch (result) {
		case SUCCESS:
			switch (_parserType) {
			case BOOKING_PAGE:
				_parserCallback.onFinishParsingBookingPage();
				break;
				
			case DETAIL_PAGE:
				_parserCallback.onFinishParsingDetailPage();
				break;
				
			case ORDERS_PAGE:
				_parserCallback.onFinishParsingOrdersPage();
				break;

			default:
				break;
			}
			break;
			
		case EMPTY_RESPONSE:
			_parserCallback.onFinishParsingEmpty();
			break;
			
		case CONNECTION_ERROR:
			_parserCallback.onFinishParsingConnectionError();
			break;

		default:
			break;
		}
		
		_isLoading = false;
	}
}
