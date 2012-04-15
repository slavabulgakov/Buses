package ru.slavabulgakov.buses;

import java.util.ArrayList;

import ru.slavabulgakov.buses.MyApplication.IRepresentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ResultActivity extends MyActivity implements IRepresentation {
	private ListView _listView;
	private Button _backBtn;
	
	
	@Override
	protected void onStart() {
		MyApplication app = (MyApplication)getApplicationContext();
		app.setCurrentActivity(this);
		super.onStart();
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		
//		String str = (String) getIntent().getSerializableExtra("slava");
		MyApplication app = (MyApplication)getApplicationContext();
		
		_backBtn = (Button)findViewById(R.id.resultBackBtn);
		_backBtn.setText(app.getFrom() + " - " + app.getTo());
		_backBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
		
		_listView = (ListView)findViewById(R.id.resultListView);
		final ArrayList<Trip> al = app.getArrayListScheduleData();
        TripAdapter adapter = new TripAdapter(ResultActivity.this, R.layout.two_item, al);
		_listView.setAdapter(adapter);
		
		_listView.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	MyApplication app = (MyApplication)getApplicationContext();
		    	app.detail_show("http://bashauto.ru" + al.get(position).detailLink);
		    	app.setCurrentPostion(position);
		    }
		  });
	}



	@Override
	public void onStartParsing() {
		showProgressDialog();
	}


	@Override
	public void onFinishParsing() {
		startActivity(new Intent(this, DetailTripActivity.class));
		hideProgressDialog();
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
	public void onFinishBooking() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onFinishBookingAuthSuccess() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onFinishBookingAuthDeny() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onFinishBookingReqData() {
		// TODO Auto-generated method stub
		
	}
	
}
