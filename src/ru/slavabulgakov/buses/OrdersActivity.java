package ru.slavabulgakov.buses;

import java.util.ArrayList;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class OrdersActivity extends MyActivity {
	private ListView _listView;
	private TextView _title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		
		_title = (TextView)findViewById(R.id.resultTitle);
		_title.setText(R.string.orders_list);
		
		
		_listView = (ListView)findViewById(R.id.resultListView);
		final ArrayList<OrdersElement> al = _app.getArrayListOrders();
        OrdersAdapter adapter = new OrdersAdapter(OrdersActivity.this, R.layout.list_orders_item, al);
		_listView.setAdapter(adapter);
	}
}