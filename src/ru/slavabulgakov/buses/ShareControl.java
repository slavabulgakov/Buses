package ru.slavabulgakov.buses;

import java.io.IOException;
import java.net.MalformedURLException;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

import ru.slavabulgakov.buses.TwApp;
import ru.slavabulgakov.buses.VkApp;
import ru.slavabulgakov.buses.VkApp.VkDialogListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ShareControl extends LinearLayout {
	private ImageButton _vkImgBtn;
	private ImageButton _twImgBtn;
	private ImageButton _emailImgBtn;
	private ImageButton _fbImgBtn;
	private LinearLayout _layout;
	private Context _context;
	public Facebook facebook;
	
	private void init(Context context, AttributeSet attrs) {
		_context = context;
		facebook = new Facebook("407111749305322");
		((Activity)getContext()).getLayoutInflater().inflate(R.layout.share, this, true);

		setUpViews();
		setAttrs(attrs);
	}

	public ShareControl(Context context) {
		super(context,null);
		init(context, null);
	}

	public ShareControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public ShareControl(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
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
	
	private OnClickListener _vkOnClickListener = new OnClickListener() {
		
		public void onClick(View arg0) {
			final VkApp vkApp = new VkApp(_context);
			vkApp.setListener(new VkDialogListener() {
				
				public void onError(String description) {
					showAlertDialog(R.string.share_error_title, R.string.share_error_message, android.R.drawable.ic_dialog_alert);
				}
				
				public void onComplete(String url) {
					String[] params = vkApp.getAccessToken(url);
					vkApp.saveAccessToken(params[0], params[1], params[2]);
					vkApp.postToWall(_context.getString(R.string.share_message));
					Toast.makeText(_context, R.string.share_success, 400).show();
				}
			});
			vkApp.showLoginDialog();
		}
	};
	
	public void updateStatus(String accessToken) {
    	try {
            Bundle bundle = new Bundle();
            bundle.putString("message", _context.getString(R.string.share_message));
            bundle.putString(Facebook.TOKEN,accessToken);
            String response = facebook.request("me/feed",bundle,"POST");
            if (response.contains("error")) {
            	showAlertDialog(R.string.share_error_title, R.string.share_error_message_duplicate, android.R.drawable.ic_dialog_alert);
			} else {
				Toast.makeText(_context, R.string.share_success, 400).show();
			}
        } catch (MalformedURLException e) {
        	showAlertDialog(R.string.share_error_title, R.string.share_error_message, android.R.drawable.ic_dialog_alert);
        } catch (IOException e) {
        	showAlertDialog(R.string.share_error_title, R.string.share_error_message, android.R.drawable.ic_dialog_alert);
        }
    }
	
	private OnClickListener _fbOnClickListener = new OnClickListener() {
		
		public void onClick(View arg0) {
			facebook.authorize((Activity)_context, new String[] {"publish_stream", "read_stream", "offline_access"}, new DialogListener() {

				@Override
				public void onComplete(Bundle values) {
					
					updateStatus(Facebook.TOKEN);
					
				}

				@Override
				public void onFacebookError(FacebookError e) {
					showAlertDialog(R.string.app_name, R.string.app_name, android.R.drawable.ic_dialog_alert);
				}

				@Override
				public void onError(DialogError e) {
					showAlertDialog(R.string.app_name, R.string.app_name, android.R.drawable.ic_dialog_alert);
				}

				@Override
				public void onCancel() {
					showAlertDialog(R.string.app_name, R.string.app_name, android.R.drawable.ic_dialog_alert);
				}
	            
	        });
		}
	};
	
	private OnClickListener _twOnClickListener = new OnClickListener() {
		
		public void onClick(View v) {
			MyApplication app = (MyApplication)_context.getApplicationContext();
			TwApp twApp = app.getTwApp();
			twApp.updateStatus(_context);
		}
	};
	
	private OnClickListener _emailOnClickListener = new OnClickListener() {
		
		public void onClick(View v) {
			final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Автобусы");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, R.string.share_message);
			_context.startActivity(Intent.createChooser(emailIntent, _context.getString(R.string.send_with)));
		}
	};
	
	private void setUpViews() {
		_emailImgBtn = (ImageButton)findViewById(R.id.shareEmailImageButton);
		_emailImgBtn.setOnClickListener(_emailOnClickListener);
		
		_vkImgBtn = (ImageButton)findViewById(R.id.shareVKImageButton);
		_vkImgBtn.setOnClickListener(_vkOnClickListener);
		
		_fbImgBtn = (ImageButton)findViewById(R.id.shareFBImageButton);
		_fbImgBtn.setOnClickListener(_fbOnClickListener);
		
		_twImgBtn = (ImageButton)findViewById(R.id.shareTwitterImageButton);
		_twImgBtn.setOnClickListener(_twOnClickListener);
		
		_layout = (LinearLayout)findViewById(R.id.shareLayout);
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
