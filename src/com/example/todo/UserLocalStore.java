package com.example.todo;

import android.content.Context;
import android.content.SharedPreferences;

public class UserLocalStore {

	public static final String SP_NAME="userDetails";
	SharedPreferences userLocalDatabase;
	UserLocalStore(Context context){
		userLocalDatabase= context.getSharedPreferences(SP_NAME, 0);
		
	}
	public void storeUserData(User user){
		SharedPreferences.Editor spEditor = userLocalDatabase.edit();
		spEditor.putString("name", user.name);
		spEditor.putString("email", user.email);
		spEditor.putString("password", user.password);
		spEditor.putString("mobNo", user.mobNo);
		spEditor.commit();
		
	}
	public User getLoggedInUser(){
		String name = userLocalDatabase.getString("name", "");
		String email = userLocalDatabase.getString("email", "");
		String password = userLocalDatabase.getString("password", "");
		String mobNo = userLocalDatabase.getString("mobNo", "");
		User newUser = new User(name,email,password,mobNo);
		return newUser;
	}
	public void setUserLoggedIn(boolean loggedIn){
		SharedPreferences.Editor spEditor = userLocalDatabase.edit();
		spEditor.putBoolean("loggedIn", loggedIn);
		spEditor.commit();
	}
	public void clearUserData(){
		SharedPreferences.Editor spEditor = userLocalDatabase.edit();
		spEditor.clear();
		spEditor.commit();

	}
	public boolean getUserLoggedIn(){
		if(userLocalDatabase.getBoolean("loggedIn",false)==true){
			return true;
		}
		else
			return false;
	}
}
