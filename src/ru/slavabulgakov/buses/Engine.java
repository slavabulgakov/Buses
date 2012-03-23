package ru.slavabulgakov.buses;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.slavabulgakov.buses.ParserWebPageTask.ParserType;

import android.content.Context;

public class Engine {
	
	public interface IRepresentation {
		public void onFormCheckDataError();
		public void onStartParsing();
		public void onFinishParsing(Object result, ParserType parserType);
		public void onFinishParsingEmpty();
		public void onFinishParsingConnectionError();
		public void onCancelParsing();
	}
	
	
	private IRepresentation _representation;
	private Context _context;
	private ParserWebPageTask _parserWebPageTask;
	
	
	public Boolean isLoading() {
		return _parserWebPageTask.isLoading();
	}
	
	
	public Engine(IRepresentation representation) {
		super();
		_representation = representation;
	}
	
	
	public String getStrDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		return formatter.format(date);
	}
	
	

	public void show(String from, String to, Date date) {
		Date currentDate = new Date();
		if (from == "" || to == "" || date.getYear() < currentDate.getYear() || date.getMonth() < currentDate.getMonth() || date.getDate() < currentDate.getDate()) {
			_representation.onFormCheckDataError();
			return;
		}
    	
		try {
			from = URLEncoder.encode(from, "UTF-8");
			to = URLEncoder.encode(to, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
		
		String url = "http://bashauto.ru/booking/?fromName=" + from + "&toName=" + to + "&when=" + getStrDate(date);
		
		_parserWebPageTask = new ParserWebPageTask(ParserType.BOOKING_PAGE, _context, _representation);
		_parserWebPageTask.execute(url);
	}
	
	public void cancel() {
		_parserWebPageTask.cancel(true);
	}
}
