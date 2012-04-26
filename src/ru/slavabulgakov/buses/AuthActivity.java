package ru.slavabulgakov.buses;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AuthActivity extends MyActivity {
	MyApplication _app;
	EditText _loginEditText;
	EditText _passwordEditText;
	
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
}