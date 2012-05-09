package ru.slavabulgakov.buses;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class ResultActivity extends MyActivity {
	private ListView _listView;
	private Button _backBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		
//		String str = (String) getIntent().getSerializableExtra("slava");
		_backBtn = (Button)findViewById(R.id.resultBackBtn);
		_backBtn.setText(_app.getFrom() + " - " + _app.getTo());
		_backBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
		
		_listView = (ListView)findViewById(R.id.resultListView);
		final ArrayList<Trip> al = _app.getArrayListScheduleData();
        TripAdapter adapter = new TripAdapter(ResultActivity.this, R.layout.two_item, al);
		_listView.setAdapter(adapter);
		
		_listView.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	_app.detail_show("http://bashauto.ru" + al.get(position).detailLink);
		    	_app.setCurrentPostion(position);
		    }
		  });
	}
}