package ru.slavabulgakov.buses;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ru.slavabulgakov.buses.Engine.IRepresentation;

import android.content.Context;
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
		DETAIL_PAGE
	};

	
	
	private ParserType _parser;
	private Boolean _canceled = false;
	private Context _context;
	private IRepresentation _parserCallback;
	private ArrayList<Trip> _arrayListScheduleData = null;
	private DetailTrip _currentDetailTrip;
	private Boolean _isLoading;
	
	public Boolean isLoading() {
		return _isLoading;
	}
	
	public ParserWebPageTask(ParserType parser, Context context, IRepresentation parserCallback) {
		super();
		_parser = parser;
		_context = context;
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
	        	String[] seat = element.select("div.seat").get(0).text().split(_context.getString(R.string.split_str));
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
	        
	        if (_parser == ParserType.BOOKING_PAGE && _arrayListScheduleData == null) {
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
			if (_parser == ParserType.BOOKING_PAGE) {
				_parserCallback.onFinishParsing(_arrayListScheduleData, _parser);
			} else if (_parser == ParserType.DETAIL_PAGE) {
				_parserCallback.onFinishParsing(_currentDetailTrip, _parser);
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
