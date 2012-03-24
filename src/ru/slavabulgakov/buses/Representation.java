package ru.slavabulgakov.buses;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;

import ru.slavabulgakov.buses.Engine.IRepresentation;
import ru.slavabulgakov.buses.ParserWebPageTask.ParserType;

public class Representation implements IRepresentation {
	
	private String _from;
	private String _to;
	private Date _date;
	private MyActivity _currentActivity;
	private ArrayList<Trip> _arrayListScheduleData;
	private DetailTrip _currentDetailTrip;
	
	public void setCurrentActivity(MyActivity activity) {
		_currentActivity = activity;
	}
	
	public ArrayList<Trip> getArrayListScheduleData() {
		return _arrayListScheduleData;
	}
	
	public DetailTrip getCurrentDetailTrip() {
		return _currentDetailTrip;
	}
	
	public void setFrom(String from) {
		_from = from;
	}
	public String getFrom() {
		return _from;
	}
	
	public void setTo(String to) {
		_to = to;
	}
	public String getTo() {
		return _to;
	}
	
	public void setDate(Date date) {
		_date = date;
	}
	public Date getDate() {
		if (_date == null) {
			_date = new Date();
		}
		return _date;
	}


	@Override
	public void onFormCheckDataError() {
		_currentActivity.showAlertDialog(R.string.form_error_title, R.string.form_error_message, android.R.drawable.ic_dialog_alert);
	}



	@Override
	public void onStartParsing() {
		_currentActivity.showProgressDialog();
	}


	@Override
	public void onFinishParsing(Object result, ParserType parserType) {
		_currentActivity.hideProgressDialog();
		if (parserType == ParserType.BOOKING_PAGE) {
			_arrayListScheduleData = (ArrayList<Trip>)result;
			_currentActivity.startActivity(new Intent(_currentActivity, ResultActivity.class));
		} else if (parserType == ParserType.DETAIL_PAGE) {
			_currentDetailTrip = (DetailTrip)result;
			_currentActivity.startActivity(new Intent(_currentActivity, DetailTripActivity.class));
		}
	}


	@Override
	public void onFinishParsingEmpty() {
		_currentActivity.showAlertDialog(R.string.empty_response_title, R.string.empty_response_message, android.R.drawable.ic_dialog_alert);
	}


	@Override
	public void onFinishParsingConnectionError() {
		_currentActivity.showAlertDialog(R.string.connection_error_title, R.string.connection_error_message, android.R.drawable.ic_dialog_alert);
	}


	@Override
	public void onCancelParsing() {
		// TODO Auto-generated method stub
		
	}
	
}
