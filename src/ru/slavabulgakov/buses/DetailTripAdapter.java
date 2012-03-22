package ru.slavabulgakov.buses;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DetailTripAdapter extends ArrayAdapter<DetailTripItem> {
	Context context; 
    int layoutResourceId;    
    ArrayList<DetailTripItem> data = null;
    
    public DetailTripAdapter(Context context, int layoutResourceId, ArrayList<DetailTripItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	View row = convertView;
    	DetailTripHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new DetailTripHolder();
            holder.txtTrip = (TextView)row.findViewById(R.id.txtDetailTripItemName);
            holder.txtTime = (TextView)row.findViewById(R.id.txtDetailTripItemTime);
            holder.txtDistance = (TextView)row.findViewById(R.id.txtDetailTripItemDistance);
            
            row.setTag(holder);
        }
        else
        {
            holder = (DetailTripHolder)row.getTag();
        }
        
        DetailTripItem dt = data.get(position);
        
        holder.txtTime.setText("");
        if (dt.arrival != null && dt.departure != null) {
        	holder.txtTime.setText(dt.arrival + " - " + dt.departure);
		}
        
        holder.txtTrip.setText(dt.route);
        
        holder.txtDistance.setText("");
        if (dt.distance != null) {
        	holder.txtDistance.setText(getContext().getString(R.string.distance) + dt.distance + getContext().getString(R.string.km));
		}
        
        return row;
    }
    
    static class DetailTripHolder {
    	TextView txtTrip;
        TextView txtTime;
        TextView txtDistance;
    }
}
