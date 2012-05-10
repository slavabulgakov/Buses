package ru.slavabulgakov.buses;

import ru.slavabulgakov.buses.TextViewAdapter.Direction;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends MyActivity {
	private AutoCompleteTextView _textViewFrom;
	private AutoCompleteTextView _textViewTo;
	private DatePickerDialog _datePickerDialog;
	private Button _dateBtn;
	private ShareView _shareView;
	private RelativeLayout _ordersRelativeLayout;
	private LinearLayout _loadingLinearLayout;
	private LinearLayout _ordersLinearLayout;
	private TextView _lastOrderNumberTextView;
	
	
	private void updateDate() {
		MyApplication app = (MyApplication)getApplicationContext();
		SimpleDateFormat dayFormatter = new SimpleDateFormat("dd");
		SimpleDateFormat monthFormatter = new SimpleDateFormat("MMM");
		SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy");
        String day = String.valueOf(dayFormatter.format(app.getDate()));
        String month = String.valueOf(monthFormatter.format(app.getDate()));
        String year = String.valueOf(yearFormatter.format(app.getDate()));
        _dateBtn.setText(Html.fromHtml(day + "<br />" + month + "<br />" + year));
	}
	
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        	((MyApplication)getApplicationContext()).setDate(new Date(year - 1900, monthOfYear, dayOfMonth));
        	updateDate();
        	view.updateDate(year, monthOfYear, dayOfMonth);
        }
    };
    
    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyApplication app = (MyApplication)getApplicationContext();
        app.getShare().getFacebook().authorizeCallback(requestCode, resultCode, data);
    }
    

	@Override
	protected void onStart() {
		_app.getShare().updateAlerts();
		super.onStart();
	}
	
	
	public void showLastOrderNumber() {
		if (_app.getArrayListOrders() == null) {
			_ordersRelativeLayout.setVisibility(View.INVISIBLE);
		} else {
			if (_app.getArrayListOrders().size() > 0) {
	        	_lastOrderNumberTextView.setText(_app.getArrayListOrders().get(0).numberOrder);
	        	_ordersRelativeLayout.setVisibility(View.VISIBLE);
	    		_loadingLinearLayout.setVisibility(View.INVISIBLE);
	            _ordersLinearLayout.setVisibility(View.VISIBLE);
			} else {
				_ordersRelativeLayout.setVisibility(View.INVISIBLE);
			}
		}
	}


	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        _shareView = (ShareView)findViewById(R.id.mainShareControl);
        _app.getShare().setShareView(_shareView);
                
        ImageButton logo = (ImageButton)findViewById(R.id.mainLogoImageButton);
        logo.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, AboutActivity.class));
			}
		});
        
        _textViewFrom = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextViewFrom);
        TextViewAdapter myAdapter = new TextViewAdapter(this, android.R.layout.simple_dropdown_item_1line, Direction.FROM);
        _textViewFrom.setAdapter(myAdapter);
        _textViewFrom.setText(_app.getFrom());
        _textViewFrom.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				_app.setFrom(_textViewFrom.getText().toString());
			}
		});
        
        _textViewTo = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextViewTo);
        TextViewAdapter myAdapter2 = new TextViewAdapter(this, android.R.layout.simple_dropdown_item_1line, Direction.TO);
        _textViewTo.setAdapter(myAdapter2);
        _textViewTo.setText(_app.getTo());
        _textViewTo.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				_app.setTo(_textViewTo.getText().toString());
			}
		});
        
        
        Date date = _app.getDate();
        _datePickerDialog = new DatePickerDialog(this, mDateSetListener, date.getYear() + 1900, date.getMonth(), date.getDate());
        _dateBtn = (Button)findViewById(R.id.buttonDate);
        updateDate();
        _dateBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				_datePickerDialog.show();
			}
		});
        
        
        _loadingLinearLayout = (LinearLayout)findViewById(R.id.mainLoadingOrdersLinearLayout);
        _ordersLinearLayout = (LinearLayout)findViewById(R.id.mainOrdesLinearLayout);
        _lastOrderNumberTextView = (TextView)findViewById(R.id.mainNumberLastOrderTextView);
        _ordersRelativeLayout = (RelativeLayout)findViewById(R.id.mainOrdesRelativeLayout);
        
        _ordersRelativeLayout.setVisibility(View.VISIBLE);
		_loadingLinearLayout.setVisibility(View.VISIBLE);
        _ordersLinearLayout.setVisibility(View.INVISIBLE);
        if (_app.getArrayListOrders() != null) {
			showLastOrderNumber();
		} else if (!_app.isLoading()) {
			_app.getRepresentation().setWithoutProgress(true);
			_app.loadOrdersList();
		}
        
        Button ordersBtn = (Button)findViewById(R.id.mainOrdersBtn);
        ordersBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (_app.getArrayListOrders().size() > 0) {
					startActivity(new Intent(MainActivity.this, OrdersActivity.class));
				}
			}
		});
        
        Button refreshBtn = (Button)findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {
                // Perform action on click
            	String from = _textViewFrom.getText().toString();
            	String to = _textViewTo.getText().toString();
            	
            	_app.setFrom(from);
            	_app.setTo(to);
				
				Date currentDate = new Date(new Date().getYear(), new Date().getMonth(), new Date().getDate());
				Date date = _app.getDate();
				
				if (	from.isEmpty() || 
						to.isEmpty() || 
						date.compareTo(currentDate) < 0) {
					showAlertDialog(R.string.form_error_title, R.string.form_error_message, android.R.drawable.ic_dialog_alert);
					return;
				}
            	
        		_app.show(from, to, _app.getDate());
            }
        });
    }
}