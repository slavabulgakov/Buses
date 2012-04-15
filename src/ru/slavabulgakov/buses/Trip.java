package ru.slavabulgakov.buses;

import ru.slavabulgakov.buses.MyApplication.ITrip;

public class Trip implements ITrip{
    public String priceRub;
    public String priceKop;
    public String timeStart;
    public String timeEnd;
    public String allSeats;
    public String freeSeats;
    public String detailLink;
    public String bookLink;
    
    public Trip(){
        super();
    }
    

	public void put(String key, String value) {
		// TODO Auto-generated method stub
		
		if (key.compareTo("priceRub") == 0) {
			this.priceRub = value;
		}
		
		if (key.compareTo("priceKop") == 0) {
			this.priceKop = value;
		}
		
		if (key.compareTo("timeStart") == 0) {
			this.timeStart = value;
		}
		
		if (key.compareTo("timeEnd") == 0) {
			this.timeEnd = value;
		}
		
		if (key.compareTo("allSeats") == 0) {
			this.allSeats = value;
		}
		
		if (key.compareTo("freeSeats") == 0) {
			this.freeSeats = value;
		}
		
		if (key.compareTo("detailLink") == 0) {
			this.detailLink = value;
		}
		
		if (key.compareTo("bookLink") == 0) {
			this.bookLink = value;
		}
	}
}
