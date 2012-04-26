package ru.slavabulgakov.buses;

import ru.slavabulgakov.buses.MyApplication.TicketType;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class BookingActivity extends MyActivity {
	private MyApplication _app;
	private Spinner _spinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.booking);
		
		_app = (MyApplication)getApplicationContext();
		
		_spinner = (Spinner) findViewById(R.id.bookingTicketTypeSpinner);
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.booking_ticket_type, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    _spinner.setAdapter(adapter);
	    
	    Button doneBtn = (Button)findViewById(R.id.bookingDoneBtn);
		doneBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int position = _spinner.getSelectedItemPosition();
				if (position == 0) {
					_app.setTicketType(TicketType.Fully);
				} else {
					_app.setTicketType(TicketType.Child);
				}
				_app.booking();
			}
		});
        
		Button backBtn = (Button)findViewById(R.id.bookingBackBtn);
		backBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
	}
}