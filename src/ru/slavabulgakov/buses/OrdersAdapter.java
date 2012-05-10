package ru.slavabulgakov.buses;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class OrdersElement {
    public String numberOrder;
}

public class OrdersAdapter extends ArrayAdapter<OrdersElement> {
	Context context; 
    int layoutResourceId;    
    ArrayList<OrdersElement> data = null;
    
    public OrdersAdapter(Context context, int layoutResourceId, ArrayList<OrdersElement> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	View row = convertView;
        Holder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new Holder();
            holder.txtTime = (TextView)row.findViewById(R.id.txtViewTitle);
            holder.txtPriceRubl = (TextView)row.findViewById(R.id.txtViewPriceRub);
            holder.txtPriceKop = (TextView)row.findViewById(R.id.txtViewPriceKop);
            holder.txtSeats = (TextView)row.findViewById(R.id.txtViewVacantSeats);
            
            row.setTag(holder);
        }
        else
        {
            holder = (Holder)row.getTag();
        }
        
        OrdersElement oe = data.get(position);
        holder.txtTime.setText(oe.numberOrder);
        holder.txtPriceRubl.setText(oe.numberOrder);
        holder.txtPriceKop.setText(oe.numberOrder);
        holder.txtSeats.setText(oe.numberOrder);
        
        return row;
    }
    
    static class Holder {
        TextView txtTime;
        TextView txtPriceRubl;
        TextView txtPriceKop;
        TextView txtSeats;
    }
}
