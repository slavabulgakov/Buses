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
	
	@Override
	public void onStartParsing() {
		((MyActivity)_currentActivity).showProgressDialog();
	}
	
	@Override
	public void onFinishParsingBookingPage() {
		_currentActivity.startActivity(new Intent(_currentActivity, ResultActivity.class));
		((MyActivity)_currentActivity).hideProgressDialog();
	}
	
	@Override
	public void onFinishParsingDetailPage() {
		_currentActivity.startActivity(new Intent(_currentActivity, DetailTripActivity.class));
		((MyActivity)_currentActivity).hideProgressDialog();
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
		Toast.makeText(_currentActivity, R.string.cancel_loading, 400).show();
	}

	@Override
	public void onStartBooking() {
		((MyActivity)_currentActivity).showProgressDialog();
	}

	@Override
	public void onFinishAuthSuccess() {
		((MyActivity)_currentActivity).hideProgressDialog();
		_currentActivity.finish();
		Toast.makeText(_currentActivity, R.string.auth_success, 400).show();
		if (_app.getBookingIsGoing()) {
			_app.booking();
		}
	}

	@Override
	public void onFinishAuthDeny() {
		((MyActivity)_currentActivity).hideProgressDialog();
		((MyActivity)_currentActivity).showAlertDialog(R.string.auth_deny_title, R.string.auth_error_message, android.R.drawable.ic_dialog_alert);
	}

	@Override
	public void onFinishBookingSuccess() {
		((MyActivity)_currentActivity).hideProgressDialog();
		_currentActivity.startActivity(new Intent(_currentActivity, OrderActivity.class));
	}

	@Override
	public void onFinishBookingDeny() {
		((MyActivity)_currentActivity).hideProgressDialog();
		_currentActivity.startActivity(new Intent(_currentActivity, AuthActivity.class));
	}

	@Override
	public void onCancelBooking() {
		((MyActivity)_currentActivity).hideProgressDialog();
	}
	
}
