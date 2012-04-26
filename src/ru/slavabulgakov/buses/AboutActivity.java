package ru.slavabulgakov.buses;

import com.facebook.android.Facebook;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AboutActivity extends MyActivity {
	private Facebook _facebook;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	@Override
	protected void onStart() {
		_app.getShare().updateAlerts();
		super.onStart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		ShareView shareView = (ShareView)findViewById(R.id.aboutShareControl);
        _app.getShare().setShareView(shareView);
        _facebook = _app.getShare().getFacebook();
		
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
