package com.example.todo;

public class info {

	String toDo, Location, time;

	public info(String toDo, String location, String time) {
		super();
		toDo = toDo;
		Location = location;
		this.time = time;
	}

	public void settoDo(String toDo) {
		toDo = toDo;
	}

	public void setLocation(String location) {
		Location = location;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String gettoDo() {
		return toDo;
	}

	public String getLocation() {
		return Location;
	}

	public String getTime() {
		return time;
	}

}
