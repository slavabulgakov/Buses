package ru.slavabulgakov.buses;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TripAdapter extends ArrayAdapter<Trip> {
	Context context; 
    int layoutResourceId;    
    ArrayList<Trip> data = null;
    
    public TripAdapter(Context context, int layoutResourceId, ArrayList<Trip> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	View row = convertView;
        TripHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new TripHolder();
            holder.txtTime = (TextView)row.findViewById(R.id.txtViewTitle);
            holder.txtPriceRubl = (TextView)row.findViewById(R.id.txtViewPriceRub);
            holder.txtPriceKop = (TextView)row.findViewById(R.id.txtViewPriceKop);
            holder.txtSeats = (TextView)row.findViewById(R.id.txtViewVacantSeats);
            
            row.setTag(holder);
        }
        else
        {
            holder = (TripHolder)row.getTag();
        }
        
        Trip weather = data.get(position);
        holder.txtTime.setText(weather.timeStart + " - " + weather.timeEnd);
        holder.txtPriceRubl.setText(weather.priceRub);
        holder.txtPriceKop.setText(weather.priceKop);
        holder.txtSeats.setText(weather.freeSeats + getContext().getString(R.string.of) + weather.allSeats + getContext().getString(R.string.free_seats));
        
        return row;
    }
    
    static class TripHolder {
        TextView txtTime;
        TextView txtPriceRubl;
        TextView txtPriceKop;
        TextView txtSeats;
    }
}
