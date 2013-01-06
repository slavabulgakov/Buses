package ru.slavabulgakov.buses;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailTripActivity extends MyActivity {
	
	void loadListData(ArrayList<DetailTripItem> arrayList) {
		LinearLayout linearLayout = (LinearLayout)findViewById(R.id.detailTripLinearLayout);
		LayoutInflater inflater = getLayoutInflater();
		
		for (DetailTripItem dt : arrayList) {
			
			View row = inflater.inflate(R.layout.detial_trip_item, linearLayout, false);
	        TextView txtTrip = (TextView)row.findViewById(R.id.txtDetailTripItemName);
	        TextView txtTime = (TextView)row.findViewById(R.id.txtDetailTripItemTime);
	        TextView txtDistance = (TextView)row.findViewById(R.id.txtDetailTripItemDistance);
	        ImageView img = (ImageView)row.findViewById(R.id.imgDetailTripItemImageView);

	        txtTime.setText("");
	        if (dt.arrival != null && dt.departure != null) {
	        	txtTime.setText(dt.arrival + " - " + dt.departure);
			}
	        
	        txtTrip.setText(dt.route);
	        
	        txtDistance.setText("");
	        if (dt.distance != null) {
	        	txtDistance.setText(getString(R.string.distance) + dt.distance + getString(R.string.km));
			}
	        
	        int i = arrayList.indexOf(dt); 
	        if (i == 0) {
	        	img.setImageResource(R.drawable.path_start);
			} else if (i == arrayList.size() - 1) {
				img.setImageResource(R.drawable.path_end);
			} else {
				img.setImageResource(R.drawable.path);
			}
	        
	        
	        linearLayout.addView(row);
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_trip);
		
		MyApplication app = (MyApplication)getApplicationContext();
		DetailTrip detailTrip = app.getCurrentDetailTrip();
		
		loadListData(detailTrip.arrayList);
		
		
        
//		ListView listView = (ListView)findViewById(R.id.detailTripListView);
//		DetailTripAdapter adapter = new DetailTripAdapter(this, R.layout.detial_trip_item, detailTrip.arrayList);
//		listView.setAdapter(adapter);
		
		
		TextView numberTxt = (TextView)findViewById(R.id.detailTripNumberTxt);
		numberTxt.setText(detailTrip.number);
		
		TextView modelTxt = (TextView)findViewById(R.id.detailTripModelTxt);
		modelTxt.setText(detailTrip.busModel);
		
		TextView carrierTxt = (TextView)findViewById(R.id.detailTripCarrierTxt);
		carrierTxt.setText(detailTrip.carrier);
		
		TextView periodicityTxt = (TextView)findViewById(R.id.detailTripPeriodicityTxt);
		periodicityTxt.setText(detailTrip.periodicity);
		
	}
}