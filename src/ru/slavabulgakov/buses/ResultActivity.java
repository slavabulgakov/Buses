package ru.slavabulgakov.buses;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ResultActivity extends MyActivity {
	private ListView _listView;
	private TextView _title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		
		_title = (TextView)findViewById(R.id.resultTitle);
		_title.setText(_app.getFrom() + " - " + _app.getTo());
		
		
		_listView = (ListView)findViewById(R.id.resultListView);
		final ArrayList<ResultElement> al = _app.getArrayListScheduleData();
        ResultAdapter adapter = new ResultAdapter(ResultActivity.this, R.layout.list_result_item, al);
		_listView.setAdapter(adapter);
		
		_listView.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	_app.detail_show("https://bashauto.ru" + al.get(position).detailLink);
		    	_app.setCurrentPostion(position);
		    }
		  });
	}
}