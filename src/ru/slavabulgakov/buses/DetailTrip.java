package ru.slavabulgakov.buses;

import java.util.ArrayList;

class DetailTripItem {
	public String route;
	public String arrival;
	public String camp;
	public String departure;
	public String distance;
}

public class DetailTrip {
	public String tripName;
	public String number;
	public String busModel;
	public String carrier;
	public String periodicity;
	public ArrayList<DetailTripItem> arrayList;
	public DetailTrip() {
		super();
		arrayList = new ArrayList<DetailTripItem>();
	}
	
}
