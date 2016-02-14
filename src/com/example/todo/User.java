package com.example.todo;

public class User {
	String name, email, password, mobNo;

	User(String name, String email, String password, String MobNo) {

		this.name= name;
		this.email=email;
		this.password=password;
		this.mobNo= MobNo;
	}

	User(String email, String password) {

		this.name= "";
		this.email=email;
		this.password=password;
		this.mobNo= "";
	}

}
