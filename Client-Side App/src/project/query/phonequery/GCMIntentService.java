package project.query.phonequery;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gcm.GCMBaseIntentService;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

public class GCMIntentService extends GCMBaseIntentService {

	final static private String URLSTRING = "http://coit-servlet02.uncc.edu:8080/ppedduri/ServiceMessage";
	HttpPost request = new HttpPost(URLSTRING);
	HttpClient httpclient = new DefaultHttpClient();
	List<NameValuePair> params1 = new ArrayList<NameValuePair>();
	String query1 = "";
	private Context _context;

	public GCMIntentService() {
		super(MainActivity.SENDER_ID);
	}

	@Override
	protected void onError(Context arg0, String arg1) {

		Log.d("GCM", "RECIEVED A ERROR MESSAGE");

	}

	@Override
	protected void onRegistered(Context arg0, String registrationId) {
		Toast.makeText(this, "Constructor Called....  :  ", Toast.LENGTH_LONG)
				.show();
		new AsyncXML().execute(registrationId);

	}

	public class AsyncXML extends AsyncTask<String, Void, Void> {

		protected void onPreExecute() {

			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				String registrationId = params[0];
				HttpPost request = new HttpPost(URLSTRING);
				HttpClient httpclient = new DefaultHttpClient();
				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				params1.add(new BasicNameValuePair("registrationid",
						registrationId));
				try {
					request.setEntity(new UrlEncodedFormEntity(params1));
					httpclient.execute(request);

				} catch (UnsupportedEncodingException e) {

					e.printStackTrace();
				}
				// Send the registration id to my app server to store the reg id
				// list
				// MainActivity.sendRegIdtoApplicationServer(registrationId);
				catch (ClientProtocolException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}
			} catch (Exception e) {

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

		}

	}

	@Override
	protected void onUnregistered(Context arg0, String registrationId) {

	}

	public String getnamefornumber(String phnumber) {
		String name = null;
		String[] projections = new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME };
		Uri contactUri = Uri.withAppendedPath(
				ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phnumber));
		Cursor cursor = _context.getContentResolver().query(contactUri,
				projections, null, null, null);
		if (cursor.moveToFirst()) {
			name = cursor.getString(cursor
					.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
		}
		return name;

	}

	@SuppressWarnings("unchecked")
	public void postresponse(String responsetext, String messageid,
			String sessionid) {

		params1.add(new BasicNameValuePair(Res.MESSAGE_ID, messageid));
		params1.add(new BasicNameValuePair(Res.RESULT, responsetext));
		params1.add(new BasicNameValuePair(Res.REGISTRATION_ID,
				MainActivity.regId));
		params1.add(new BasicNameValuePair(Res.KEY, Res.API_KEY));
		if (sessionid != null && sessionid != "")
			params1.add(new BasicNameValuePair(Res.SESSION, sessionid));
		try {
			request.setEntity(new UrlEncodedFormEntity(params1));
			try {
				HttpResponse response = httpclient.execute(request);

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Log.d("data11", "Inserted a row");

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}

	}

	@Override
	protected void onMessage(Context context, Intent intent) {

		_context = context;
	Log.d("GCM", "RECIEVED A MESSAGE");

		// Get the data from intent and send to notificaion bar

		Bundle data = intent.getExtras();
		String messageid = data.getString(Res.MESSAGE_ID);
		String query1 = data.getString(Res.MESSAGE);
		String sessionid = data.getString(Res.SESSION);
		String[] parts = query1.split(":");
		if (parts.length == 2) {

			if (parts[0].equalsIgnoreCase("GetName")) {
				String message = "Contact Details";
				String name = null;
				String[] projections = new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME };
				Uri contactUri = Uri.withAppendedPath(
						ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
						Uri.encode(parts[1]));
				Cursor cursor = _context.getContentResolver().query(contactUri,
						projections, null, null, null);
				if (cursor.moveToFirst()) {
					name = cursor
							.getString(cursor
									.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
					message = message + "\nName = " + name + "-- Number = "
							+ parts[1] + "\n";
				}

				postresponse(message, messageid, sessionid);

			} else if (parts[0].equalsIgnoreCase("GetNumber")) {
				Log.d("GCM", "Entered the loop");
				String number = null;
				String name = null;
				String message = "";
				String[] projections = new String[] {
						ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
						ContactsContract.CommonDataKinds.Phone.NUMBER };

				Cursor cursor = _context.getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						projections, "display_name like '%" + parts[1] + "%'",
						null, null);
				if (cursor.moveToFirst()) {
					do {
						number = cursor
								.getString(cursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						name = cursor
								.getString(cursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
						message = message + "Name = " + name + "-- Number = "
								+ number + "\n";
						System.out.println("Name = " + name + "\n" + "Number ="
								+ number);
						break;
					} while (cursor.moveToNext());
				}
				postresponse(message, messageid, sessionid);
				// sendSMS(messages[0].getOriginatingAddress(), message);

			}

		} else if(parts[0].equalsIgnoreCase("getMessages")) 
		{
			String data2="These are your messages: \n";
			String data1="";
			 Uri uri = Uri.parse("content://sms/inbox");
		       Cursor c= getContentResolver().query(uri, null, "read = 0" ,null," date desc");
		      // startManagingCursor(c);
		        
		       // Read the sms data and store it in the list
		       
		       if(c.moveToFirst()) {
		           for(int i=0; i < c.getCount(); i++) {
		               SMSData sms = new SMSData();
		               sms.setBody(c.getString(c.getColumnIndexOrThrow("body")).toString());
		               sms.setNumber(c.getString(c.getColumnIndexOrThrow("address")).toString());
		               sms.setName(getnamefornumber(c.getString(c.getColumnIndexOrThrow("address")).toString()));
		               data1= sms.getdatainStringFormat();
		               
		               data2=data2+"\n"+data1;
		                
		               c.moveToNext();
		           }
		           postresponse(data2, messageid, sessionid);
		}
		}
		
		
		
		else if (parts[0].equalsIgnoreCase("getgeo")) {
			String location1="";
			LocationManager locationManager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);

			// Define a listener that responds to location updates
			LocationListener locationListener = new LocationListener() {
				public void onLocationChanged(Location location) {
					// Called when a new location is found by the network location
					// provider.
					// makeUseOfNewLocation(location);
					Location l  = location;
					Log.d("demo","DONE>>...."+l);
				}

				public void onStatusChanged(String provider, int status,
						Bundle extras) {
				}

				public void onProviderEnabled(String provider) {
				}

				public void onProviderDisabled(String provider) {
				}
			};

			// Register the listener with the Location Manager to receive location
			// updates
			if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
			Location location = locationManager
	                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	        if (location != null) {
	            double latitude = location.getLatitude();
	            double longitude = location.getLongitude();
	            Log.d("demo",latitude+">>"+longitude);
	            location1="This is the current location of the mobile device. latitude -->"+latitude+", longitude -->"+longitude;
	        }
			}
		  

			postresponse(location1, messageid, sessionid);
		}

		else if (parts[0].equalsIgnoreCase("getmissedcalls")) {
			String missedcontacts = "This is your Missed calls List \n";
			int MISSED_CALL_TYPE = android.provider.CallLog.Calls.MISSED_TYPE;
			final String[] projection = null;
			final String selection = null;
			final String[] selectionArgs = null;
			final String sortOrder = android.provider.CallLog.Calls.DATE
					+ " DESC";
			Cursor cursor = null;
			try {
				cursor = context.getContentResolver().query(
						Uri.parse("content://call_log/calls"), projection,
						selection, selectionArgs, sortOrder);
				while (cursor.moveToNext()) {
					String callLogID = cursor
							.getString(cursor
									.getColumnIndex(android.provider.CallLog.Calls._ID));
					String callNumber = cursor
							.getString(cursor
									.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
					String callDate = cursor
							.getString(cursor
									.getColumnIndex(android.provider.CallLog.Calls.DATE));
					String callType = cursor
							.getString(cursor
									.getColumnIndex(android.provider.CallLog.Calls.TYPE));
					String isCallNew = cursor
							.getString(cursor
									.getColumnIndex(android.provider.CallLog.Calls.NEW));
					if (Integer.parseInt(callType) == MISSED_CALL_TYPE
							&& Integer.parseInt(isCallNew) > 0) {

						String s = getnamefornumber(callNumber);

						missedcontacts = missedcontacts + "\nName is: " + s
								+ " and Number is: " + callNumber;
					}

					

				}
				postresponse(missedcontacts, messageid, sessionid);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				cursor.close();
			}

		}
	}
	
		}



