package ru.slavabulgakov.buses;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class ResultElement {
    public String priceRub;
    public String priceKop;
    public String timeStart;
    public String timeEnd;
    public String allSeats;
    public String freeSeats;
    public String detailLink;
    public String bookLink;
}

public class ResultAdapter extends ArrayAdapter<ResultElement> {
	Context context; 
    int layoutResourceId;    
    ArrayList<ResultElement> data = null;
    
    public ResultAdapter(Context context, int layoutResourceId, ArrayList<ResultElement> data) {
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
        
        ResultElement weather = data.get(position);
        holder.txtTime.setText(weather.timeStart + " - " + weather.timeEnd);
        holder.txtPriceRubl.setText(weather.priceRub);
        holder.txtPriceKop.setText(weather.priceKop);
        holder.txtSeats.setText(weather.freeSeats + " " + getContext().getString(R.string.of) + " " + weather.allSeats + " " + getContext().getString(R.string.free_seats));
        
        return row;
    }
    
    static class TripHolder {
        TextView txtTime;
        TextView txtPriceRubl;
        TextView txtPriceKop;
        TextView txtSeats;
    }
}
