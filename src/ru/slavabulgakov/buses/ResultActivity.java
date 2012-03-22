package ru.slavabulgakov.buses;

import java.util.ArrayList;

import ru.slavabulgakov.buses.MyApplication.MyCallback;
import ru.slavabulgakov.buses.MyApplication.ParserType;

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

public class ResultActivity extends MyActivity {
	private ListView _listView;
	private Button _backBtn;
	
	

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
		    	
		    	((MyApplication)getApplicationContext()).loadData(new MyCallback() {
					
		    		public void callbackSuccessResult() {
						startActivity(new Intent(ResultActivity.this, DetailTripActivity.class));
//		    			Toast.makeText(getApplicationContext(), "parsed", Toast.LENGTH_SHORT).show();
					}
					
					public void callbackShowProgressDialog() {
						showProgressDialog();
					}
					
					public void callbackShowAlertDialog(int titleId, int messageId, int iconId) {
						showAlertDialog(titleId, messageId, iconId);
					}
					
					public void callbackHideProgressDialog() {
						hideProgressDialog();
					}
				}, "http://bashauto.ru" + al.get(position).detailLink, ParserType.DETAIL_PAGE);
		    	
		      
		    }
		  });
	}
	
}
