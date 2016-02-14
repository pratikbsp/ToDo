package com.example.todo;

import java.util.ArrayList;
import java.util.Calendar;

//import com.freakypulse.writenote.MainActivity.MyLAdapter;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener {

	ListView listView;
	ArrayList<info> data;
	ArrayList<String> toDo, locationList, timeList;
	ArrayList<Long> miliTimeList;
	ImageView add;
	ArrayAdapter lAdapter;
	static int i = 0;
	public static final String toDoPREFERENCES = "MyPrefs1";
	public static final String locationPREFERENCES = "MyPrefs2";
	public static final String timePREFERENCES = "MyPrefs3";
	public static final String miliTimePREFERENCES = "MyPrefs4";
	public static SharedPreferences toDoSharedPreferences,
			locationSharedPreferences, timeSharedPreferences,miliTimeSharedPreferences;

	LinearLayout ll, ll2, chooser;

	double longitude;
	double lattitude;

	Button save, discard, delete, pick;
	EditText et1, et3;
	TextView tv1, tv2, tv3, et2;
	TimePicker timePicker;
	DatePicker datePicker;

	int z = 0;

	String info, address = "Add a address";

	int savedDay, savedMonth, savedYear, savedHour, savedMin;
	protected LocationManager locationManager;

	private AddressResultReceiver mResultReceiver;

	protected GoogleApiClient mGoogleApiClient;
	protected Location mLastLocation;

	protected boolean mAddressRequested;

	/**
	 * The formatted location address.
	 */
	protected String mAddressOutput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mResultReceiver = new AddressResultReceiver(new Handler());

		mAddressRequested = false;
		mAddressOutput = "";

		buildGoogleApiClient();

		ll = (LinearLayout) findViewById(R.id.popup);
		ll2 = (LinearLayout) findViewById(R.id.popupDisplay);
		chooser = (LinearLayout) findViewById(R.id.timePicker);
		timePicker = (TimePicker) findViewById(R.id.timePicker1);
		datePicker = (DatePicker) findViewById(R.id.datePicker1);
		final Calendar calendar = Calendar.getInstance();
		datePicker.init(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), null);

		save = (Button) findViewById(R.id.l42);
		discard = (Button) findViewById(R.id.l41);
		pick = (Button) findViewById(R.id.pick);

		delete = (Button) findViewById(R.id.l41Display);

		listView = (ListView) findViewById(R.id.listView);

		et1 = (EditText) findViewById(R.id.locationEt);
		et2 = (TextView) findViewById(R.id.timeEt);
		et3 = (EditText) findViewById(R.id.noteEt);

		tv1 = (TextView) findViewById(R.id.locationEtDisplay);
		tv2 = (TextView) findViewById(R.id.timeEtDisplay);
		tv3 = (TextView) findViewById(R.id.noteEtDisplay);

		// locationManager = (LocationManager)
		// getSystemService(Context.LOCATION_SERVICE);
		// locationManager.requestLocationUpdates(
		// LocationManager.NETWORK_PROVIDER, 0, 0, this);
		
	//	TextView logout = (TextView)findViewById(R.id.logout);
		final UserLocalStore userLocalStore = new UserLocalStore(this);
	

		toDo = new ArrayList();
		locationList = new ArrayList();
		timeList = new ArrayList();
		miliTimeList = new ArrayList();
		toDoSharedPreferences = getSharedPreferences(toDoPREFERENCES,
				Context.MODE_PRIVATE);
		locationSharedPreferences = getSharedPreferences(locationPREFERENCES,
				Context.MODE_PRIVATE);
		timeSharedPreferences = getSharedPreferences(timePREFERENCES,
				Context.MODE_PRIVATE);

		miliTimeSharedPreferences = getSharedPreferences(miliTimePREFERENCES,
				Context.MODE_PRIVATE);

		add = (ImageView) findViewById(R.id.imageView1);
		populateLList();

		int yourEditCount = toDoSharedPreferences.getInt("count", 0);
		for (int i = 1; i <= yourEditCount; i++) {
			String data1 = toDoSharedPreferences.getString("data" + i, "");
			String data2 = timeSharedPreferences.getString("data" + i, "");
			
			String data3 = locationSharedPreferences.getString("data" + i, "");
			String data4 = miliTimeSharedPreferences.getString("data" + i, "");
			Calendar c = Calendar.getInstance();
			
			if (!data1.matches("")&& c.getTimeInMillis()<Long.parseLong(data4)) {
				toDo.add(data1);
				timeList.add(data2);
				locationList.add(data3);
				miliTimeList.add(Long.parseLong(data4));

			}

		}

		i = yourEditCount;
		lAdapter.notifyDataSetChanged();

		et2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Calendar C = Calendar.getInstance();
				int hour = C.get(Calendar.HOUR_OF_DAY);
				int min = C.get(Calendar.MINUTE);

				timePicker.setCurrentHour(hour);
				timePicker.setCurrentMinute(min);
				// listView.setVisibility(View.GONE);
				chooser.setVisibility(View.VISIBLE);
			}
		});

		pick.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// listView.setVisibility(View.VISIBLE);
				Calendar C = Calendar.getInstance();
				long seconds = System.currentTimeMillis();
				C.setTimeInMillis(seconds);

				String format = "";
				int hour = timePicker.getCurrentHour();
				int min = timePicker.getCurrentMinute();
				int month = datePicker.getMonth();
				int day = datePicker.getDayOfMonth();
				int year = datePicker.getYear();

				savedHour = hour;
				savedMin = min;
				savedMonth = month;
				savedDay = day;
				savedYear = year;

				if (hour == 0) {
					hour += 12;
					format = "AM";
				} else if (hour == 12) {
					format = "PM";
				} else if (hour > 12) {
					hour -= 12;
					format = "PM";
				} else {
					format = "AM";
				}

				String month_names[] = { "Jan", "Feb", "Mar", "Apr", "May",
						"Jun", "July", "Aug", "Sep", "Oct", "Nov", "Dec" };

				et2.setText(new StringBuilder().append(hour).append(" : ")
						.append(min).append(" ").append(format).append(" ,")
						.append(day + "").append(" " + month_names[month]));
				chooser.setVisibility(View.GONE);
			}
		});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				tv1.setText(locationList.get(position));
				tv2.setText(timeList.get(position));
				tv3.setText(toDo.get(position));
				ll2.setVisibility(View.VISIBLE);
			}
		});

		add.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mGoogleApiClient.isConnected() && mLastLocation != null) {
					startIntentService(mLastLocation);
				}

				et1.setText(address);
				// If GoogleApiClient isn't connected, we process the user's
				// request by setting
				// mAddressRequested to true. Later, when GoogleApiClient
				// connects, we launch the service to
				// fetch the address. As far as the user is concerned, pressing
				// the Fetch Address button
				// immediately kicks off the process of getting the address.
				mAddressRequested = true;
				listView.setVisibility(View.GONE);
				// TODO Auto-generated method stub
				ll.setVisibility(View.VISIBLE);
				// et1.setText("");
				// et2.setText(longitude+" "+lattitude);
				et2.setText("Click to add a reminder time");
				et3.setText("");

			}
		});
		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String location = et1.getText().toString();
				String time = et2.getText().toString();
				String note = et3.getText().toString();
				if (location.matches("") || time.matches("")
						|| note.matches("")) {
					Toast.makeText(getBaseContext(), "Fill all the fields",
							Toast.LENGTH_LONG).show();
					return;
				}
				i++;
				toDo.add(note);
				locationList.add(location);
				timeList.add(time);

				Calendar calendar = Calendar.getInstance();
				calendar.set(savedYear, savedMonth, savedDay, savedHour,
						savedMin, 0);
				long startTime = calendar.getTimeInMillis();

				// Calendar cl = Calendar.getInstance();
				// cl.setTimeInMillis(startTime);

				// Toast.makeText(getBaseContext(),
				// "time sent for alarm is "+cl.get(Calendar.HOUR)+":"+cl.get(Calendar.MINUTE),
				// Toast.LENGTH_LONG).show();
				listView.setVisibility(View.VISIBLE);
				
				
				miliTimeList.add(startTime);
				
				
				SharedPreferences.Editor toDoEditor = toDoSharedPreferences
						.edit();

				toDoEditor.putString("data" + i, note);
				toDoEditor.putInt("count", i);
				toDoEditor.commit();

				SharedPreferences.Editor timeEditor = timeSharedPreferences
						.edit();

				timeEditor.putString("data" + i, time);
				timeEditor.putInt("count", i);
				timeEditor.commit();

				SharedPreferences.Editor miliTimeEditor = miliTimeSharedPreferences
						.edit();

				miliTimeEditor.putString("data" + i, startTime+"");
				miliTimeEditor.putInt("count", i);
				miliTimeEditor.commit();

				
				SharedPreferences.Editor locationEditor = locationSharedPreferences
						.edit();

				locationEditor.putString("data" + i, location);
				locationEditor.putInt("count", i);
				locationEditor.commit();

				lAdapter.notifyDataSetChanged();
				// ll.animate().alpha(0.0f).setDuration(1000);

				info = et3.getText().toString() + ";Location:"
						+ et1.getText().toString() + ";Time:"
						+ et2.getText().toString();

				et1.setText("");
				et2.setText("");
				et3.setText("");
				ll.setVisibility(View.GONE);
				setAlarm(startTime);

			}
		});

		discard.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listView.setVisibility(View.VISIBLE);
				ll.setVisibility(View.GONE);
			}
		});

		delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				et1.setText("");
				et2.setText("");
				et3.setText("");
				listView.setVisibility(View.VISIBLE);
				ll2.setVisibility(View.GONE);
			}
		});

	}

	private void populateLList() {
		// TODO Auto-generated method stub
		lAdapter = new MyLAdapter();
		listView.setAdapter(lAdapter);

	}

	class MyLAdapter extends ArrayAdapter {
		public MyLAdapter() {
			super(MainActivity.this, R.layout.list, toDo);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View itemView = convertView;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(R.layout.list, parent,
						false);
			}
			// Find NavView to work with
			// ListContent newView = lList.get(position);

			TextView textView = (TextView) itemView.findViewById(R.id.title);
			textView.setText(toDo.get(position));
			return itemView;

		}

	}

	public void setAlarm(long time) {
		z++;

		String info1 = info.substring(0, 22);
		Intent intent = new Intent(MainActivity.this, MyBroadcastReceiver.class);
		intent.putExtra("details", info1);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				this.getApplicationContext(), z, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, (time - 15 * 60 * 1000),
				pendingIntent);
		// Toast.makeText(this, "Alarm set in " + time + " seconds",
		// Toast.LENGTH_LONG).show();
	}

	class AddressResultReceiver extends ResultReceiver {
		public AddressResultReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {

			// Display the address string
			// or an error message sent from the intent service.
			// mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
			// displayAddressOutput();
			// Show a toast message if an address was found.
			et1.setText(resultData.getString(Constants.RESULT_DATA_KEY));
			address = resultData.getString(Constants.RESULT_DATA_KEY);

			if (resultCode == Constants.SUCCESS_RESULT) {
				// showToast(getString(R.string.address_found));

				// Toast.makeText(getBaseContext(), "address found",
				// Toast.LENGTH_LONG).show();
			}

		}
	}

	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
	}

	protected void startIntentService(Location location) {
		Intent intent = new Intent(this, FetchAddressIntentService.class);
		intent.putExtra(Constants.RECEIVER, mResultReceiver);
		intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
		startService(intent);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// Gets the best and most recent location currently available, which may
		// be null
		// in rare cases when a location is not available.
		mLastLocation = LocationServices.FusedLocationApi
				.getLastLocation(mGoogleApiClient);
		// Toast.makeText(
		// getBaseContext(),
		// "loation is " + mLastLocation.getLatitude() + " "
		// / + mLastLocation.getLongitude(), Toast.LENGTH_LONG)
		// .show();
		if (mLastLocation != null) {
			// Determine whether a Geocoder is available.
			if (!Geocoder.isPresent()) {
				// Toast.makeText(this, R.string.no_geocoder_available,
				// Toast.LENGTH_LONG).show();
				return;
			}
			// It is possible that the user presses the button to get the
			// address before the
			// GoogleApiClient object successfully connects. In such a case,
			// mAddressRequested
			// is set to true, but no attempt is made to fetch the address (see
			// fetchAddressButtonHandler()) . Instead, we start the intent
			// service here if the
			// user has requested an address, since we now have a connection to
			// GoogleApiClient.
			if (mAddressRequested) {
				startIntentService(mLastLocation);
			}
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

}
