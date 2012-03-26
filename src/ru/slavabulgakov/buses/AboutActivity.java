package ru.slavabulgakov.buses;

import com.facebook.android.Facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AboutActivity extends Activity {
	private Facebook _facebook;
	
	@Override
	protected void onStart() {
		MyApplication app = (MyApplication)getApplicationContext();
		app.getShare().updateAlerts();
		app.setCurrentActivity(this);
		super.onStart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		MyApplication app = (MyApplication)getApplicationContext();
		ShareView shareView = (ShareView)findViewById(R.id.aboutShareControl);
        app.getShare().setShareView(shareView);
        _facebook = app.getShare().getFacebook();
		
		Button send_btn = (Button)findViewById(R.id.aboutSendBtn);
		send_btn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.setType("plain/text");
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ getString(R.string.author_email2) });
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " for Android " + Build.VERSION.RELEASE + " feedback");
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "\n\n" + "Info: " + Build.MODEL);
				startActivity(Intent.createChooser(emailIntent, null));
			}
		});
		
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        _facebook.authorizeCallback(requestCode, resultCode, data);
    }
}
