package ru.slavabulgakov.buses;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

public class TextViewAdapter extends ArrayAdapter<String> {
	private Filter mFilter;
    
    public enum Direction {
    	 FROM, TO;
    }

    private List<String> mSubData = new ArrayList<String>();
    static int counter=0;
    

    public TextViewAdapter(Context context, int textViewResourceId, final Direction direction) {
    	super(context, textViewResourceId);
    	setNotifyOnChange(false);

    	mFilter = new Filter() {
    		private List<String> mData = new ArrayList<String>();

    		@Override
    		protected FilterResults performFiltering(CharSequence constraint) {
    			// This method is called in a worker thread
    			mData.clear();

    			FilterResults filterResults = new FilterResults();
    			if(constraint != null && constraint.length() > 2) {
    				try {
    					// Here is the method (synchronous) that fetches the data
    					// from the server
    					String strDir = "";
    					switch (direction) {
    					case FROM:
    						strDir = "fromName";
    						break;
					
    					case TO:
    						strDir = "toName";
    						break;

    					default:
    						break;
    					}
    					String encConstr = URLEncoder.encode(constraint.toString(), "UTF-8");
    					URL url = new URL("http://bashauto.ru/bitrix/components/unistation/order2/templates/.default/ajax.php?name=3&q=" + encConstr + "&n=" + strDir);
    					URLConnection conn = url.openConnection();
    					BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    					String line = "";
              
    					String json = "";
    					while ((line = rd.readLine()) != null) {
    						json += line;
    					}
              
    					JSONArray ja = new JSONArray(json);
    					for (int i = 0; i < ja.length(); i++) {
    						String label = ja.getJSONObject(i).getString("label");
    						mData.add(label);
    					}
    				}
    				catch(Exception e) {
    				}

    				filterResults.values = mData;
    				filterResults.count = mData.size();
    			}
    			return filterResults;
    		}

    		@SuppressWarnings("unchecked")
    		@Override
    		protected void publishResults(CharSequence contraint, FilterResults results) {
    			if(true) { //c == counter) {
    				mSubData.clear();
    				if(results != null && results.count > 0) {
    					ArrayList<String> obejcts = (ArrayList<String>)results.values;
    					for (String v : obejcts) {
    						mSubData.add(v);
    					}

    					notifyDataSetChanged();
    				}
    				else {
    					notifyDataSetInvalidated();
    				}
    			}
    		}
    	};
    }

    @Override
    public int getCount() {
    	return mSubData.size();
    }

    @Override
    public String getItem(int index) {
    	return mSubData.get(index);
    }

    @Override
    public Filter getFilter() {
    	return mFilter;
    }
}