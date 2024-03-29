package ru.slavabulgakov.buses;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import ru.slavabulgakov.buses.MyApplication.IRepresentation;

public class Representation implements IRepresentation {
	MyApplication _app;
	
	public Representation(MyApplication app) {
		super();
		_app = app;
	}
	
	//////////////////////
	//currentActivity ===
	private Activity _currentActivity;
	@Override
	public void setCurrentActivity(Activity activity) {
		_currentActivity = activity;
	}

	@Override
	public Activity getCurrentActivity() {
		return _currentActivity;
	}
	//====================
	//////////////////////
	
	private Boolean _withoutProgress = false;
	@Override
	public void setWithoutProgress(Boolean withoutProgress) {
		_withoutProgress = withoutProgress;
	}
	
	@Override
	public void onStartParsing() {
		if (_withoutProgress) {
			_withoutProgress = false;
		} else {
			((MyActivity)_currentActivity).showProgressDialog();
		}
	}
	
	@Override
	public void onFinishParsingBookingPage() {
		_currentActivity.startActivity(new Intent(_currentActivity, ResultActivity.class));
		((MyActivity)_currentActivity).hideProgressDialog();
	}
	
	@Override
	public void onFinishParsingDetailPage() {
		_currentActivity.startActivity(new Intent(_app.getApplicationContext(), DetailTripActivity.class));
		((MyActivity)_currentActivity).hideProgressDialog();
	}
	
	@Override
	public void onFinishParsingOrdersPage() {
		((MainActivity)_currentActivity).showLastOrderNumber();
	}

	@Override
	public void onFinishParsingEmpty() {
		((MyActivity)_currentActivity).showAlertDialog(R.string.empty_response_title, R.string.empty_response_message, android.R.drawable.ic_dialog_alert);
		((MyActivity)_currentActivity).hideProgressDialog();
	}

	@Override
	public void onFinishParsingConnectionError() {
		((MyActivity)_currentActivity).showAlertDialog(R.string.connection_error_title, R.string.connection_error_message, android.R.drawable.ic_dialog_alert);
		((MyActivity)_currentActivity).hideProgressDialog();
	}

	@Override
	public void onCancelParsing() {
		Toast.makeText(_currentActivity, R.string.cancel_loading, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStartBooking() {
		((MyActivity)_currentActivity).showProgressDialog();
	}

	@Override
	public void onFinishAuthSuccess() {
	}

	@Override
	public void onFinishAuthDeny() {
		((MyActivity)_currentActivity).hideProgressDialog();
		((MyActivity)_currentActivity).showAlertDialog(R.string.auth_deny_title, R.string.auth_error_message, android.R.drawable.ic_dialog_alert);
	}

	@Override
	public void onFinishBookingSuccess() {
	}

	@Override
	public void onFinishBookingDeny() {
	}

	@Override
	public void onCancelBooking() {
		((MyActivity)_currentActivity).hideProgressDialog();
	}

}
