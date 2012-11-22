package org.bioinfo.gcsa.lib.users.persistence;

import java.util.List;

import org.bioinfo.gcsa.lib.users.beans.Project;
import org.bioinfo.gcsa.lib.users.beans.Session;

public interface UserManager {

	/*
	 * User methods
	 */
	public void createUser(String accountId, String password,String accountName, String email,Session session) throws UserManagementException;
	
	public void createAnonymousUser(String accountId, String password, String email);
	
	public String login(String accountId, String password, Session session);
	
	public String testPipe(String accountId, String password); //Pruebas, hay que borrarlo
	
	public String getUserByAccountId(String accountId, String sessionId);
	
	public String getUserByEmail(String email, String sessionId);
	
	
	/*
	 * Project methods
	 */
	public void checkSessionId(String accountId, String sessionId);
	
	public String getAllProjectsBySessionId(String accountId, String sessionId);
	
	public void createProject(Project project, String accountId, String sessionId) throws UserManagementException;

	//add file to project
	public String createFileToProject(Project project, String accountId, String sessionId);
	
	
	/*
	 * Utils
	 */
	public List<Project> jsonToProjectList(String json);
	
}
