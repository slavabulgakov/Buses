package ru.slavabulgakov.buses;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.Toast;
import ru.slavabulgakov.buses.R;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

public class TwApp {
	private RequestToken _requestToken;
	private Twitter _twitter;
	private String _url;
	private State _state = State.NOTHING;
	
	private enum State {
		NOTHING,
		GET_PIN,
		ENTER_PIN
	};
	
	public void updateAlerts(Context context) {
		switch (_state) {
		case GET_PIN:
			showGetPinDialog(context);
			break;
			
		case ENTER_PIN:
			showEnterPinDialog(context);
			break;

		default:
			break;
		}
	}
	
	private void showGetPinDialog(final Context context) {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(context);                 
		alert.setTitle(R.string.twitter_get_pin_title);
		alert.setMessage(R.string.twitter_get_pin_message);

		alert.setPositiveButton(context.getString(R.string.twitter_get_pin_continue_btn), new DialogInterface.OnClickListener() {  
		    public void onClick(DialogInterface dialog, int whichButton) {
		    	_state = State.ENTER_PIN;
		    	showEnterPinDialog(context);
			    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(_url)).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
			    context.startActivity(browserIntent);
		        return;                  
		    }  
		});
		
		alert.setNegativeButton(R.string.cancel, new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(context, R.string.share_cancel_title, 400).show();
			}
		});
	    alert.show();
		
    }
	
	private void showEnterPinDialog(final Context context) {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(context);                 
		alert.setTitle(R.string.twitter_enter_pin_title);
		alert.setMessage(R.string.twitter_enter_pin_message);

		final EditText input = new EditText(context); 
		alert.setView(input);
		
		alert.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {  
		    public void onClick(DialogInterface dialog, int whichButton) {
		        String pin = input.getText().toString();
		        _state = State.NOTHING;
		        try {
					_twitter.getOAuthAccessToken(_requestToken, pin);
					Status status = _twitter.updateStatus(context.getString(R.string.share_message));
					Toast.makeText(context, status.getText(), 400).show();
				} catch (TwitterException e) {
					e.printStackTrace();
				}
		        return;                  
		    }  
		});
		
		alert.setNegativeButton(R.string.cancel, new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(context, R.string.share_cancel_title, 400).show();
				_state = State.NOTHING;
			}
		});
	    alert.show();
		
    }
	
	
	public void updateStatus(Context context) {
		_twitter = new TwitterFactory().getInstance();

		_twitter.setOAuthConsumer("vRDIaZxcogXoBnAupH1nyQ", "JscEmBVLpwW55XejFKI65qmeJwmU7x7zpJH0i0w14o");
	    _requestToken = null;
		try {
			_requestToken = _twitter.getOAuthRequestToken();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		_url = _requestToken.getAuthorizationURL();
		
		_state = State.GET_PIN;
		showGetPinDialog(context);
		
//	    MyApplication app = (MyApplication)_context.getApplicationContext();
//	    app.enterPin = true;
	    
	    
	    
	    
//	    TwDialog dialog = new TwDialog(context, url, new TwDialogListener() {
//			
//			public void onError(String description) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			public void onComplete(String url, String pin) {
//				// TODO Auto-generated method stub
//			    try {
//					_twitter.getOAuthAccessToken(_requestToken, pin);
//					Status status = _twitter.updateStatus("testtesttest");
//				} catch (TwitterException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		});
//	    dialog.show();

	}
	
}
