package ru.slavabulgakov.buses;

import ru.slavabulgakov.buses.MyApplication.IRepresentation;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AuthActivity extends MyActivity implements IRepresentation {
	MyApplication _app;
	EditText _loginEditText;
	EditText _passwordEditText;
	
	@Override
	protected void onStart() {
		MyApplication app = (MyApplication)getApplicationContext();
		app.setCurrentActivity(AuthActivity.this);
		super.onStart();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth);
		
		_app = (MyApplication)getApplicationContext();
		
		_loginEditText = (EditText)findViewById(R.id.authEditTextLogin);
		_loginEditText.setText(_app.getLogin());
		_passwordEditText = (EditText)findViewById(R.id.authEditTextPassword);
		
		Button doneBtn = (Button)findViewById(R.id.authDoneBtn);
		doneBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				_app.setLogin(_loginEditText.getText().toString());
				_app.setPassword(_passwordEditText.getText().toString());
				_app.auth();
			}
		});
        
		Button backBtn = (Button)findViewById(R.id.authBackBtn);
		backBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public void onStartParsing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinishParsing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinishParsingEmpty() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinishParsingConnectionError() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCancelParsing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartBooking() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinishBookingRequestAuth() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinishBookingAuthSuccess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinishBooking() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinishBookingAuthDeny() {
		showAlertDialog(R.string.auth_error_title, R.string.auth_error_message, android.R.drawable.ic_dialog_alert);
	}

	@Override
	public void onFinishBookingReqData() {
		startActivity(new Intent(this, BookingActivity.class));
	}
}
