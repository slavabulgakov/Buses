package ru.slavabulgakov.buses;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.util.Log;

public class SslJsoup {
	private String _url;
	private String _dataAsString;
	private Header[] _headers;
	private CookieStore _cookieStore;
	
	private SslJsoup(String url) {
		_url = url;
	}
	
	public static SslJsoup connect(String url) {
		return new SslJsoup(url);
	}
	
	private HttpClient getNewHttpClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	        
	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", (SocketFactory) sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}
	
	public Document parse() {
		return Jsoup.parse(_dataAsString);
	}
	
	public Document parseBodyFragment() {
		return Jsoup.parseBodyFragment(_dataAsString);
	}
	
	public SslJsoup eraseBefore(String before) {
		int index = _dataAsString.indexOf(before);
		if (index != -1) {
			_dataAsString = _dataAsString.substring(index, _dataAsString.length() - 1);
		}
		
		return this;
	}
	
	public SslJsoup eraseAfter(String after) {
		int index = _dataAsString.indexOf(after);
		if (index != -1) {
			_dataAsString = _dataAsString.substring(0, index);
		}
		return this;
	}
	
	public SslJsoup execute() {
		DefaultHttpClient client = (DefaultHttpClient) getNewHttpClient();
		
		HttpGet request = new HttpGet(_url);
		request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11");
		try {
			
			HttpResponse response = null;
			
			// add cookies
			if (_cookieStore != null) {
				HttpContext context = new BasicHttpContext();
			    context.setAttribute(ClientContext.COOKIE_STORE, _cookieStore);
			    response = client.execute(request, context);
			} else {
				response = client.execute(request);
			}
		      
		      _headers = response.getAllHeaders();

		      // Check if server response is valid
		      StatusLine status = response.getStatusLine();
		      if (status.getStatusCode() != 200) {
		          throw new IOException("Invalid response from server: " + status.toString());
		      }

		      // Pull content stream from response
		      HttpEntity entity = response.getEntity();
		      InputStream inputStream = entity.getContent();

		      ByteArrayOutputStream content = new ByteArrayOutputStream();

		      // Read response into a buffered stream
		      int readBytes = 0;
		      byte[] sBuffer = new byte[512];
		      while ((readBytes = inputStream.read(sBuffer)) != -1) {
		          content.write(sBuffer, 0, readBytes);
		      }

		      // Return result from buffered stream
		      _dataAsString = new String(content.toByteArray());
		  } catch (IOException e) {
		     Log.d("error", e.getLocalizedMessage());
		  }
		
		return this;
	}
	
	public String cookie(String key) {
		for (Header header : _headers) {
			if (header.getName().equals("Set-Cookie")) {
				String keyValue = header.getValue();
				String[] pair = keyValue.split("=");
				if (pair[0].equals(key)) {
					return pair[1].split(";")[0];
				}
			}
		}
		return null;
	}
	
	public SslJsoup cookie(String key, String value) {
		if (_cookieStore == null) {
			_cookieStore = new BasicCookieStore();
		}
		_cookieStore.addCookie(new BasicClientCookie(key, value));
		return this;
	}
}