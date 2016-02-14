package com.example.todo;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class ServiceRequest {

	ProgressDialog progressDialog;
	public static final int CONN_TIME = 1000 * 15;
	public static final String SERVER_ADDRESS = "http://pratikbsp.comoj.com/";

	ServiceRequest(Context context) {
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Processing");
		progressDialog.setMessage("Please wait...");
	}

	public void storeUserDataInBackground(User user, GetUserCallBack callBack) {
		progressDialog.show();
		new StoreUserDataAsyncTask(user, callBack).execute();
	}

	public void fetchUserDataInBackground(User user, GetUserCallBack callBack) {
		progressDialog.show();
		new FetchUserDataAsyncTask(user, callBack).execute();
	}

	public class FetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {

		User user;
		GetUserCallBack callBack;

		FetchUserDataAsyncTask(User user, GetUserCallBack callBack) {
			this.user = user;
			this.callBack = callBack;
		}

		@Override
		protected User doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
			// dataToSend.add(new BasicNameValuePair("name",user.name));
			dataToSend.add(new BasicNameValuePair("email", user.email));
			dataToSend.add(new BasicNameValuePair("password", user.password));
			// dataToSend.add(new BasicNameValuePair("mobNo",user.mobNo));

			HttpParams httpRequestParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpRequestParams,
					CONN_TIME);
			HttpConnectionParams.setSoTimeout(httpRequestParams, CONN_TIME);

			HttpClient client = new DefaultHttpClient(httpRequestParams);
			HttpPost post = new HttpPost(
					"http://pratikbsp.comoj.com/FetchUserData.php");

			User returnedUser = null;
			try {
				post.setEntity(new UrlEncodedFormEntity(dataToSend));
				HttpResponse httpResponse = client.execute(post);

				HttpEntity entity = httpResponse.getEntity();
				String result = EntityUtils.toString(entity);
				JSONObject jObject = new JSONObject(result);
				if (jObject.length() == 0) {
					returnedUser = null;
				} else {
					String name = jObject.getString("name");
					String mobNo = jObject.getString("mobNo");
					// String = jObject .getString("name");
					returnedUser = new User(name, user.email, user.password,
							mobNo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return returnedUser;

		}

		@Override
		protected void onPostExecute(User user) {
			progressDialog.dismiss();
			callBack.done(user);
			super.onPostExecute(user);
		}

	}

	public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {

		User user;
		GetUserCallBack callBack;

		StoreUserDataAsyncTask(User user, GetUserCallBack callBack) {
			this.user = user;
			this.callBack = callBack;
		}

		@Override
		protected Void doInBackground(Void... params) {
			ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
			dataToSend.add(new BasicNameValuePair("name", user.name));
			dataToSend.add(new BasicNameValuePair("email", user.email));
			dataToSend.add(new BasicNameValuePair("password", user.password));
			dataToSend.add(new BasicNameValuePair("mobNo", user.mobNo));
			HttpParams httpRequestParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpRequestParams,
					CONN_TIME);
			HttpConnectionParams.setSoTimeout(httpRequestParams, CONN_TIME);

			HttpClient client = new DefaultHttpClient(httpRequestParams);
			HttpPost post = new HttpPost(SERVER_ADDRESS + "register.php");
			try {
				post.setEntity(new UrlEncodedFormEntity(dataToSend));
				client.execute(post);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			progressDialog.dismiss();
			callBack.done(null);
			super.onPostExecute(aVoid);
		}
	}

	public class CheckMobUserDataAsyncTask extends AsyncTask<Void, Void, User> {

		String mobNo;
		GetUserCallBack callBack;

		CheckMobUserDataAsyncTask(String mobNo, GetUserCallBack callBack) {
			this.mobNo = mobNo;
			this.callBack = callBack;
		}

		@Override
		protected User doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
			// dataToSend.add(new BasicNameValuePair("name",user.name));
			dataToSend.add(new BasicNameValuePair("mobNo", this.mobNo));
			// dataToSend.add(new BasicNameValuePair("password",
			// user.password));
			// dataToSend.add(new BasicNameValuePair("mobNo",user.mobNo));

			HttpParams httpRequestParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpRequestParams,
					CONN_TIME);
			HttpConnectionParams.setSoTimeout(httpRequestParams, CONN_TIME);

			HttpClient client = new DefaultHttpClient(httpRequestParams);
			HttpPost post = new HttpPost(
					"http://pratikbsp.comoj.com/check_mob.php");

			User returnedUser = null;
			try {
				post.setEntity(new UrlEncodedFormEntity(dataToSend));
				HttpResponse httpResponse = client.execute(post);

				HttpEntity entity = httpResponse.getEntity();
				String result = EntityUtils.toString(entity);
				JSONObject jObject = new JSONObject(result);
				if (jObject.length() == 0) {
					returnedUser = null;
				} else {
					String name = jObject.getString("name");
					String emailId = jObject.getString("email");
					String password = jObject.getString("password");
					String mobNo = jObject.getString("mobNo");
					// String = jObject .getString("name");
					returnedUser = new User(name, emailId, password, mobNo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return returnedUser;

		}

		@Override
		protected void onPostExecute(User user) {
			progressDialog.dismiss();
			callBack.done(user);
			super.onPostExecute(user);
		}

	}

	public void CheckUserDataInBackground(String email, GetUserCallBack callBack) {
		progressDialog.show();
		new CheckUserDataAsyncTask(email, callBack).execute();
	}

	public void CheckMobUserDataInBackground(String mobNo,
			GetUserCallBack callBack) {
		progressDialog.show();
		new CheckMobUserDataAsyncTask(mobNo, callBack).execute();
	}

	public class CheckUserDataAsyncTask extends AsyncTask<Void, Void, User> {

		String email;
		GetUserCallBack callBack;

		CheckUserDataAsyncTask(String email, GetUserCallBack callBack) {
			this.email = email;
			this.callBack = callBack;
		}

		@Override
		protected User doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ArrayList<NameValuePair> dataToSend = new ArrayList<NameValuePair>();
			// dataToSend.add(new BasicNameValuePair("name",user.name));
			dataToSend.add(new BasicNameValuePair("email", this.email));
			// dataToSend.add(new BasicNameValuePair("password",
			// user.password));
			// dataToSend.add(new BasicNameValuePair("mobNo",user.mobNo));

			HttpParams httpRequestParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpRequestParams,
					CONN_TIME);
			HttpConnectionParams.setSoTimeout(httpRequestParams, CONN_TIME);

			HttpClient client = new DefaultHttpClient(httpRequestParams);
			HttpPost post = new HttpPost(
					"http://pratikbsp.comoj.com/check_email.php");

			User returnedUser = null;
			try {
				post.setEntity(new UrlEncodedFormEntity(dataToSend));
				HttpResponse httpResponse = client.execute(post);

				HttpEntity entity = httpResponse.getEntity();
				String result = EntityUtils.toString(entity);
				JSONObject jObject = new JSONObject(result);
				if (jObject.length() == 0) {
					returnedUser = null;
				} else {
					String name = jObject.getString("name");
					String emailId = jObject.getString("email");
					String password = jObject.getString("password");
					String mobNo = jObject.getString("mobNo");
					// String = jObject .getString("name");
					returnedUser = new User(name, emailId, password, mobNo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return returnedUser;

		}

		@Override
		protected void onPostExecute(User user) {
			progressDialog.dismiss();
			callBack.done(user);
			super.onPostExecute(user);
		}

	}

}
