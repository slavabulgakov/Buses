package ru.slavabulgakov.buses;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class OrdersActivity extends MyActivity {
	private ListView _listView;
	private Button _backBtn;
	private TextView _title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		
		_title = (TextView)findViewById(R.id.resultTitle);
		_title.setText(R.string.orders_list);
		
		_backBtn = (Button)findViewById(R.id.resultBackBtn);
		_backBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
		
		
		_listView = (ListView)findViewById(R.id.resultListView);
		final ArrayList<OrdersElement> al = _app.getArrayListOrders();
        OrdersAdapter adapter = new OrdersAdapter(OrdersActivity.this, R.layout.list_orders_item, al);
		_listView.setAdapter(adapter);
		
//		_listView.setOnItemClickListener(new OnItemClickListener() {
//		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		    	_app.detail_show("http://bashauto.ru" + al.get(position).detailLink);
//		    	_app.setCurrentPostion(position);
//		    }
//		  });
	}
}