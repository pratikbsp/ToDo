package com.example.todo;

import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

public class LoginActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener {

	TextView header, register_here, existing_user;
	LinearLayout ll1, ll2;
	EditText editText_username1, editText_password1, editText_name2,
			editText_username2, editText_password2, editText_confirm_password2,
			editText_MobNo;
	Button submit1, submit2;
	UserLocalStore userLocalStore;
	boolean exists, mobExists;
	public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	int mode = 1;

	int pressed = 0;
//	LoginButton loginButton;
	private static final int RC_SIGN_IN = 0;
	// Logcat tag
	private static final String TAG = "MainActivity";

	// Profile pic image size in pixels
	private static final int PROFILE_PIC_SIZE = 400;

	// Google client to interact with Google API

	/**
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean mIntentInProgress;

	private boolean mSignInClicked;

	private ConnectionResult mConnectionResult;

	private GoogleApiClient mGoogleApiClient;
	private SignInButton btnSignIn;
	//CallbackManager callbackManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//FacebookSdk.sdkInitialize(getApplicationContext());
		setContentView(R.layout.activity_login);
		exists = false;
		mobExists = false;
		//btnSignIn = (SignInButton) findViewById(R.id.googlebtn);
		header = (TextView) findViewById(R.id.header);
		register_here = (TextView) findViewById(R.id.register_here);
		existing_user = (TextView) findViewById(R.id.loggedIn);
		ll1 = (LinearLayout) findViewById(R.id.linearLayout1);
		ll2 = (LinearLayout) findViewById(R.id.linearLayout2);
		editText_username1 = (EditText) findViewById(R.id.editText_username1);
		editText_password1 = (EditText) findViewById(R.id.editText_password1);
		editText_name2 = (EditText) findViewById(R.id.editText_name2);
		editText_username2 = (EditText) findViewById(R.id.editText_username2);
		editText_password2 = (EditText) findViewById(R.id.editText_password2);
		editText_confirm_password2 = (EditText) findViewById(R.id.editText_confirm_password2);
		editText_MobNo = (EditText) findViewById(R.id.editText_MobNo);
		submit1 = (Button) findViewById(R.id.submit1);
		submit2 = (Button) findViewById(R.id.submit2);

		userLocalStore = new UserLocalStore(this);
		ll1.setVisibility(View.VISIBLE);
		ll2.setVisibility(View.GONE);
		
		if(userLocalStore.getUserLoggedIn()){
			startActivity(new Intent(LoginActivity.this,MainActivity.class));
			finish();
			return;
		}


		btnSignIn  =(SignInButton)findViewById(R.id.googlebtn);
		
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
		btnSignIn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				signInWithGplus();
				pressed = 1;
			}
		});

		register_here.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				header.setText("Registration");
				ll1.setVisibility(View.GONE);
				ll2.setVisibility(View.VISIBLE);
				mode = 2;
			}
		});
		existing_user.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				header.setText("Login");
				ll1.setVisibility(View.VISIBLE);
				ll2.setVisibility(View.GONE);
				mode = 1;
			}
		});
		submit1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String email = editText_username1.getText().toString();
				String password = editText_password1.getText().toString();
				User user = new User(email, password);
				authenticate(user);

				// User user1 = userLocalStore.getLoggedInUser();

			}
		});

		submit2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String name = editText_name2.getText().toString();
				String email = editText_username2.getText().toString();
				String password = editText_password2.getText().toString();
				String confirm_password = editText_confirm_password2.getText()
						.toString();
				String mobNo = editText_MobNo.getText().toString();
				if (name.matches("") || email.matches("")
						|| password.matches("") || confirm_password.matches("")
						|| mobNo.matches("")) {
					Toast.makeText(getBaseContext(),
							"All fields are mandatory", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (!password.equals(confirm_password)) {
					Toast.makeText(getBaseContext(),
							"Please enter same password in both fields",
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (mobNo.length() < 7 || mobNo.length() > 13) {
					Toast.makeText(getBaseContext(),
							"Please enter valid mobile number",
							Toast.LENGTH_SHORT).show();
					return;
				}

				boolean value = checkEmail(email);
				if (!value) {
					// showEmailIncorrectMessage();
					Toast.makeText(getBaseContext(),
							"Please enter valid email address",
							Toast.LENGTH_SHORT).show();

					return;
				}

				check(new User(name, email, password, mobNo), getBaseContext());
				// checkMob(mobNo);
				/**
				 * if(!exists&&!mobExists){
				 * 
				 * 
				 * //Toast.makeText(AccountActivity.this,
				 * "value if exist is "+exists+" and mobExists is "+mobExists,
				 * Toast.LENGTH_LONG).show();;
				 * 
				 * registerUser(newUser); }
				 **/

			}
		});

	}

	private void registerUser(User user) {
		ServiceRequest serverRequests = new ServiceRequest(this);
		serverRequests.storeUserDataInBackground(user, new GetUserCallBack() {
			@Override
			public void done(User returnedUser) {
				// startActivity(new Intent(AccountActivity.this,));
				header.setText("Login");
				ll1.setVisibility(View.VISIBLE);
				ll2.setVisibility(View.GONE);
				mode = 1;
			}
		});

	}

	private void authenticate(final User user) {
		ServiceRequest serverRequests = new ServiceRequest(this);
		serverRequests.fetchUserDataInBackground(user, new GetUserCallBack() {
			@Override
			public void done(User returnedUser) {
				if (returnedUser == null) {
					showErrorMessage();
				} else {
					// String name = user.name.split(" ")[0];
					// MainActivity.userName.setText(user.name);
					// MainActivity.userName.setTextColor(Color.WHITE);

					logUserIn(returnedUser);
				}
			}
		});
	}

	private void check(final User user, Context context) {
		boolean ans = false;
		final ServiceRequest serverRequests = new ServiceRequest(this);
		serverRequests.CheckUserDataInBackground(user.email,
				new GetUserCallBack() {
					@Override
					public void done(User returnedUser) {
						if (returnedUser == null) {
							serverRequests.CheckMobUserDataInBackground(
									user.mobNo, new GetUserCallBack() {
										@Override
										public void done(User returnedUser1) {
											if (returnedUser1 == null) {
												registerUser(user);

											} else {

												showErrorMessage2();
											}
										}
									});

						} else {
							showErrorMessage1();
						}
					}
				});

	}

	/**
	 * private void checkMob(String mobNo) { ServerRequests serverRequests = new
	 * ServerRequests(this); serverRequests.CheckMobUserDataInBackground(mobNo,
	 * new GetUserCallBack() {
	 * 
	 * @Override public void done(User returnedUser) { if (returnedUser == null)
	 *           { //registerUser(returnedUser); mobExists = false; } else {
	 *           mobExists = true; showErrorMessage2(); } } }); }
	 **/
	private void showErrorMessage() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setMessage("Incorrect user details");
		dialogBuilder.setPositiveButton("Ok", null);
		dialogBuilder.show();
	}

	private void showErrorMessage1() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setMessage("email address already exists");
		dialogBuilder.setPositiveButton("Ok", null);
		dialogBuilder.show();
	}

	private void showEmailIncorrectMessage() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setMessage("Please enter a valid email address");
		dialogBuilder.setPositiveButton("Ok", null);
		dialogBuilder.show();
	}

	private void showErrorMessage2() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setMessage("This number is already registerd");
		dialogBuilder.setPositiveButton("Ok", null);
		dialogBuilder.show();
	}

	private void logUserIn(User user) {
		userLocalStore.storeUserData(user);
		userLocalStore.setUserLoggedIn(true);
		startActivity(new Intent(LoginActivity.this, MainActivity.class));
		finish();
	}

	private boolean checkEmail(String email) {
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}

	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {

		//if (pressed == 1) {
			if (requestCode == RC_SIGN_IN) {
				if (responseCode != RESULT_OK) {
					mSignInClicked = false;
				}

				mIntentInProgress = false;

				if (!mGoogleApiClient.isConnecting()) {
					mGoogleApiClient.connect();
				}
			}

		
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

		startActivity(new Intent(LoginActivity.this,MainActivity.class));
		finish();
		
		// Get user's information
		// getProfileInformation();

		// Update the UI after signin
		updateUI(true);

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
		updateUI(false);
	}

	/**
	 * Updating the UI, showing/hiding buttons and profile layout
	 * */
	private void updateUI(boolean isSignedIn) {
		if (isSignedIn) {
			btnSignIn.setVisibility(View.GONE);
			// btnSignOut.setVisibility(View.VISIBLE);

		} else {
			btnSignIn.setVisibility(View.VISIBLE);

		}
	}

	private void signInWithGplus() {
		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}
	}

	/**
	 * Method to resolve any signin errors
	 * */
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

}
