package org.bioinfo.gcsa.lib.users.beans;

public class Account {
	private String service;
	private String status;
	private String user;
	private String pass;
	
	public Account(){
		this.status = "";
		this.service = "";
		this.user = "";
		this.pass = "";
	}

	public Account(String service, String user, String pass) {
		this.service = service;
		this.user = user;
		this.pass = pass;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
	
	
	
}
