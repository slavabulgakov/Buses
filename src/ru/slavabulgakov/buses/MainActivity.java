package ru.slavabulgakov.buses;

import ru.slavabulgakov.buses.TextViewAdapter.Direction;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.facebook.android.Facebook;

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


public class MainActivity extends MyActivity {
	private AutoCompleteTextView _textViewFrom;
	private AutoCompleteTextView _textViewTo;
	private DatePickerDialog _datePickerDialog;
	private Button _dateBtn;
	private ShareControl _shareControl;
	private Facebook _facebook;
	
	
	
	private void updateDate() {
		MyApplication app = (MyApplication)getApplicationContext();
		SimpleDateFormat dayFormatter = new SimpleDateFormat("dd");
		SimpleDateFormat monthFormatter = new SimpleDateFormat("MMM");
		SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy");
        String day = String.valueOf(dayFormatter.format(app.getRepresentation().getDate()));
        String month = String.valueOf(monthFormatter.format(app.getRepresentation().getDate()));
        String year = String.valueOf(yearFormatter.format(app.getRepresentation().getDate()));
        _dateBtn.setText(Html.fromHtml(day + "<br />" + month + "<br />" + year));
	}
	
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        	((MyApplication)getApplicationContext()).getRepresentation().setDate(new Date(year - 1900, monthOfYear, dayOfMonth));
        	updateDate();
        	view.updateDate(year, monthOfYear, dayOfMonth);
        }
    };
    
    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        _facebook.authorizeCallback(requestCode, resultCode, data);
    }
    
            

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final MyApplication app = (MyApplication)getApplicationContext();
        
        app.getTwApp().updateAlerts(this);
        
        _shareControl = (ShareControl)findViewById(R.id.shareControl1);
        _facebook = _shareControl.facebook;
                
        ImageButton logo = (ImageButton)findViewById(R.id.mainLogoImageButton);
        logo.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, AboutActivity.class));
			}
		});
        
        _textViewFrom = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextViewFrom);
        TextViewAdapter myAdapter = new TextViewAdapter(this, android.R.layout.simple_dropdown_item_1line, Direction.FROM);
        _textViewFrom.setAdapter(myAdapter);
        _textViewFrom.setText(app.getRepresentation().getFrom());
        _textViewFrom.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				app.getRepresentation().setFrom(_textViewFrom.getText().toString());
			}
		});
        
        _textViewTo = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextViewTo);
        TextViewAdapter myAdapter2 = new TextViewAdapter(this, android.R.layout.simple_dropdown_item_1line, Direction.TO);
        _textViewTo.setAdapter(myAdapter2);
        _textViewTo.setText(app.getRepresentation().getTo());
        _textViewTo.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				app.getRepresentation().setTo(_textViewTo.getText().toString());
			}
		});
        
        
        Date date = app.getRepresentation().getDate();
        _datePickerDialog = new DatePickerDialog(this, mDateSetListener, date.getYear() + 1900, date.getMonth(), date.getDate());
        _dateBtn = (Button)findViewById(R.id.buttonDate);
        updateDate();
        _dateBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				_datePickerDialog.show();
			}
		});
        
        Button refreshBtn = (Button)findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {
                // Perform action on click
            	String from = _textViewFrom.getText().toString();
            	String to = _textViewTo.getText().toString();
            	
            	MyApplication app = (MyApplication)getApplicationContext();
            	app.getRepresentation().setFrom(from);
            	app.getRepresentation().setTo(to);
				
				Date currentDate = new Date();
				if (from == "" || to == "" || app.getRepresentation().getDate().getYear() < currentDate.getYear() || app.getRepresentation().getDate().getMonth() < currentDate.getMonth() || app.getRepresentation().getDate().getDate() < currentDate.getDate()) {
					showAlertDialog(R.string.form_error_title, R.string.form_error_message, android.R.drawable.ic_dialog_alert);
					return;
				}
            	
        		app.getEngine().show(from, to, app.getRepresentation().getDate());
            }
        });
    }
    
    
}