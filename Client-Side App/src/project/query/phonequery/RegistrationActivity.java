package project.query.phonequery;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import project.query.phonequery.MainActivity.AsyncXML;

import com.google.android.gcm.GCMRegistrar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationActivity extends Activity {

	TextView username, password, confirmpassword;
	public final static String SENDER_ID = "963681245550";
	public StringBuffer sb = new StringBuffer("");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		Button submitbutton = (Button) findViewById(R.id.submit1);
		username = (TextView) findViewById(R.id.regusername);
		password = (TextView) findViewById(R.id.regconfirmpassword);
		confirmpassword = (TextView) findViewById(R.id.regconfirmpassword);

		submitbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				new AsyncXML().execute();
			}

		});

	}

	public class AsyncXML extends AsyncTask<String, Void, Void> {

		protected void onPreExecute() {

			super.onPreExecute();

		}

		private void ParseJSONData(StringBuffer sb) throws IOException,
				JSONException {
			try {

				InputStream in = new ByteArrayInputStream(sb.toString()
						.getBytes());

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder strbuilder = new StringBuilder();
				String line = reader.readLine();
				while (line != null) {
					strbuilder.append(line);
					line = reader.readLine();
				}
				reader.close();

				JSONObject initobj = new JSONObject(strbuilder.toString());
					if (!initobj.getString("okay").equals(null)
							&& initobj.getString("okay").equals("true")) {
						Intent historyintent = new Intent(RegistrationActivity.this,
								NewUserActivity.class);
						//historyintent.putExtra("history", reponsetext);
						startActivity(historyintent);
					}

				
				// Intent historyintent = new Intent(MainActivity.this,
				// HistoryActivity.class);
				// historyintent.putExtra("history",reponsetext);
				// startActivity(historyintent);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				String username1, password1, confirmpassword1;
				HttpPost request = new HttpPost(
						"http://coit-servlet02.uncc.edu:8080/ppedduri/AddUser");
				HttpClient httpclient = new DefaultHttpClient();
				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				username1 = username.getText().toString().trim();
				password1 = password.getText().toString().trim();
				confirmpassword1 = confirmpassword.getText().toString().trim();
				if (username1 == null || username1 == "" || password1 == null
						|| password1 == "" || confirmpassword1 == null
						|| confirmpassword1 == "") {
					Toast.makeText(getBaseContext(),
							"Please Fill All the Fields", Toast.LENGTH_LONG)
							.show();
				}

				else if (!password1.equals(confirmpassword1)) {
					Toast.makeText(getBaseContext(),
							"Password and ConfirmPassword should match",
							Toast.LENGTH_LONG).show();
				} else {
					params1.add(new BasicNameValuePair("username", username1));
					params1.add(new BasicNameValuePair("password", password1));
					params1.add(new BasicNameValuePair("registration_id",
							MainActivity.regId));
					params1.add(new BasicNameValuePair(Res.KEY, Res.API_KEY));
					try {
						request.setEntity(new UrlEncodedFormEntity(params1));
						HttpResponse response = httpclient.execute(request);
						if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							BufferedReader in = new BufferedReader(
									new InputStreamReader(response.getEntity()
											.getContent()));
							String line = "";

							while ((line = in.readLine()) != null) {

								sb.append(line + "\n");
							}
							ParseJSONData(sb);
							in.close();

						} else {

						}

						Log.d("data11", "Inserted a row");

					} catch (UnsupportedEncodingException e) {

						e.printStackTrace();
					}

					catch (ClientProtocolException e) {

						e.printStackTrace();
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

		}
	}
}
