package org.bioinfo.gcsa.lib.users.beans;

import java.util.Map;

import javax.persistence.Version;

import org.bioinfo.commons.utils.StringUtils;
import org.bioinfo.gcsa.lib.GcsaUtils;


public class Session {
	private String id;
	private String ip;
	private String login;
	private String logout;
//	private Map<String, String> attributes;

	public Session() {
		this.id = StringUtils.randomString(20);
		this.ip = "";
		this.login = GcsaUtils.getTime();
		this.logout = "";
	}
	
	public Session(String ip){

		this.id = StringUtils.randomString(20);
		this.ip = ip;
		this.login = GcsaUtils.getTime();
		this.logout = "";
	}
	
	public Session(String id, String ip, String logout) {
		this.id = id;
		this.ip = ip;
		this.login = GcsaUtils.getTime();
		this.logout = logout;
	}
	
	public Session(String id, String ip, String login, String logout) {
		this.id = id;
		this.ip = ip;
		this.login = login;
		this.logout = logout;
	}

	@Override
	public String toString() {
		return id+"\t"+ip+"\t"+login+"\t"+logout;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getLogout() {
		return logout;
	}

	public void setLogout(String logout) {
		this.logout = logout;
	}
	
}
