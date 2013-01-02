package ru.slavabulgakov.buses;

import ru.slavabulgakov.buses.Share.IShareView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ShareView extends LinearLayout implements IShareView {
	private ImageButton _vkImgBtn;
	private ImageButton _twImgBtn;
	private ImageButton _emailImgBtn;
	private ImageButton _fbImgBtn;
//	private LinearLayout _layout;
	private Context _context;
	private HorizontalScrollView _scrollView;
	
	private void init(Context context, AttributeSet attrs) {
		_context = context;
		((Activity)getContext()).getLayoutInflater().inflate(R.layout.share, this, true);

		setUpViews();
		setAttrs(attrs);
	}

	public ShareView(Context context) {
		super(context,null);
		init(context, null);
	}

	public ShareView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public ShareView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	
	
	
	public void scroll() {
		_scrollView.scrollBy(1000, 0);
	}
	
	


	public void showAlertDialog(int titleId, int messageId, int iconId) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(_context);
		builder.setMessage(messageId)
			.setTitle(titleId)
			.setCancelable(false)
			.setIcon(iconId)
			.setPositiveButton(R.string.ok, null);
		AlertDialog alert = builder.create();
		alert.show();
    }
	
	public void showAlertDialog(String title, String message, int iconId) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(_context);
		builder.setMessage(message)
			.setTitle(title)
			.setCancelable(false)
			.setIcon(iconId)
			.setPositiveButton(R.string.ok, null);
		AlertDialog alert = builder.create();
		alert.show();
    }
	
	
	
	
	/////////
	// VK ===
	@Override
	public void onVKError() {
		showAlertDialog(R.string.share_error_title, R.string.share_error_message, android.R.drawable.ic_dialog_alert);
	}
	
	@Override
	public void onVKSendSuccess() {
		Toast.makeText(_context, R.string.share_success, Toast.LENGTH_LONG).show();
	}
	
	private OnClickListener _vkOnClickListener = new OnClickListener() {
		
		public void onClick(View arg0) {
			MyApplication app = (MyApplication)_context.getApplicationContext();
			app.getShare().sendMess2VK();
		}
	};
	//=======
	/////////
	
	
	
	///////////////
	// Facebook ===
	public void onFBError() {
		showAlertDialog(R.string.share_error_title, R.string.share_error_message, android.R.drawable.ic_dialog_alert);
	};
	
	@Override
	public void onFBSendSuccess() {
		Toast.makeText(_context, R.string.share_success, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onFBErrorDuplicate() {
		showAlertDialog(R.string.share_error_title, R.string.share_error_message_duplicate, android.R.drawable.ic_dialog_alert);
	}
	
	@Override
	public void onFBCanceled() {
		Toast.makeText(_context, R.string.share_cancel_title, Toast.LENGTH_LONG).show();
	}
	
	private OnClickListener _fbOnClickListener = new OnClickListener() {
		
		public void onClick(View arg0) {
			
			MyApplication app = (MyApplication)_context.getApplicationContext();
			app.getShare().sendMess2FB((Activity)_context);
			
		}
	};
	
	public void onFBInvalidKey(String mess) {
		showAlertDialog("Invalid key", mess, android.R.drawable.ic_dialog_alert);
	};
	//=============
	///////////////
	
	
	
	//////////////
	// Twitter ===
	private Boolean _enterPinDialogIsShowed = false;
	private void showEnterPinDialog(final Share share) {
		if (_enterPinDialogIsShowed) {
			return;
		}
		AlertDialog.Builder alert = new AlertDialog.Builder(_context);                 
		alert.setTitle(R.string.twitter_enter_pin_title);
		alert.setMessage(R.string.twitter_enter_pin_message);

		final EditText input = new EditText(_context); 
		alert.setView(input);
		
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {  
		    public void onClick(DialogInterface dialog, int whichButton) {
		    	_enterPinDialogIsShowed = false;
		        String pin = input.getText().toString();
		        share.setPin(pin);
		        share.setNothingState();
		    }  
		});
		
		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				_enterPinDialogIsShowed = false;
				Toast.makeText(_context, R.string.share_cancel_title, Toast.LENGTH_LONG).show();
				share.setNothingState();
			}
		});
		
		_enterPinDialogIsShowed = true;
	    alert.show();
		
    }
	
	private Boolean _getPinDialogIsShowed = false;
	private void showGetPinDialog(final Share share, final String url) {
		if (_getPinDialogIsShowed) {
			return;
		}
		AlertDialog.Builder alert = new AlertDialog.Builder(_context);                 
		alert.setTitle(R.string.twitter_get_pin_title);
		alert.setMessage(R.string.twitter_get_pin_message);

		alert.setPositiveButton(R.string.twitter_get_pin_continue_btn, new DialogInterface.OnClickListener() {  
		    public void onClick(DialogInterface dialog, int whichButton) {
		    	_getPinDialogIsShowed = false;
		    	share.setEnterPinState();
		    	showEnterPinDialog(share);
			    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
			    _context.startActivity(browserIntent);
		        return;                  
		    }  
		});
		
		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				_getPinDialogIsShowed = false;
				Toast.makeText(_context, R.string.share_cancel_title, Toast.LENGTH_LONG).show();
				share.setNothingState();
			}
		});
		
		_getPinDialogIsShowed = true;
	    alert.show();
		
    }
	
	@Override
	public void onTwitterEnterPin(Share share) {
    	showEnterPinDialog(share);
	}
	
	@Override
	public void onTwitterErrorDuplicateUpdating() {
		showAlertDialog(R.string.share_error_title, R.string.share_error_message_duplicate, android.R.drawable.ic_dialog_alert);
	}
	
	@Override
	public void onTwitterErrorUpdating() {
		showAlertDialog(R.string.connection_error_title, R.string.connection_error_message, android.R.drawable.ic_dialog_alert);
	}
	
	@Override
	public void onTwitterSuccesUpdating() {
		Toast.makeText(_context, R.string.share_success, Toast.LENGTH_LONG).show();
	}
	
		
	@Override
	public void onTwitterGetPin(Share share, String url) {
		showGetPinDialog(share, url);
	}
	
	private OnClickListener _twOnClickListener = new OnClickListener() {
		
		public void onClick(View v) {
			MyApplication app = (MyApplication)_context.getApplicationContext();
			app.getShare().sendMess2TW();
		}
	};
	//============
	//////////////
	
	
	
	
	
	/////////////
	// e-mail ===
	private OnClickListener _emailOnClickListener = new OnClickListener() {
		
		public void onClick(View v) {
			final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, _context.getString(R.string.share_message_title));
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, _context.getString(R.string.share_message));
			_context.startActivity(Intent.createChooser(emailIntent, _context.getString(R.string.send_with)));
		}
	};
	//===========
	/////////////
	
	
	
	
	
	
	private void setUpViews() {
		_scrollView = (HorizontalScrollView)findViewById(R.id.shareScrollView);
		
		_emailImgBtn = (ImageButton)findViewById(R.id.shareEmailImageButton);
		_emailImgBtn.setOnClickListener(_emailOnClickListener);
		
		_vkImgBtn = (ImageButton)findViewById(R.id.shareVKImageButton);
		_vkImgBtn.setOnClickListener(_vkOnClickListener);
		
		_fbImgBtn = (ImageButton)findViewById(R.id.shareFBImageButton);
		_fbImgBtn.setOnClickListener(_fbOnClickListener);
		
		_twImgBtn = (ImageButton)findViewById(R.id.shareTwitterImageButton);
		_twImgBtn.setOnClickListener(_twOnClickListener);
		
//		_layout = (LinearLayout)findViewById(R.id.shareLayout);
	}

	private void setAttrs(AttributeSet attrs) {
		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ShareControl, 0,0);
//			setColor(a.getColor(R.styleable.TitledImageView_backColor, 0xFFFFFFFF));
//			setTitle(a.getString(R.styleable.TitledImageView_title));
//			setImage(a.getDrawable(R.styleable.TitledImageView_image));

			a.recycle();
		}
	}
	
}
