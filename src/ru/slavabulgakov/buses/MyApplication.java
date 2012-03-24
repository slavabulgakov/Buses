package ru.slavabulgakov.buses;

import ru.slavabulgakov.buses.TwApp;

import android.app.Application;

public class MyApplication extends Application {
	private Representation _representation;
	private Engine _engine;
	private TwApp _twApp;
	
	
	
	public MyApplication() {
		super();
		_representation = new Representation();
		_engine = new Engine(_representation, this);
	}
	
	
	public Representation getRepresentation() {
		return _representation;
	}

	public Engine getEngine() {
		return _engine;
	}


	public Boolean enterPin;
	
	public TwApp getTwApp() {
		if (_twApp == null) {
			_twApp = new TwApp();
		}
		return _twApp;
	}
    
	
	
	interface ITrip {
		void put(String key, String value);
	}
	
}
