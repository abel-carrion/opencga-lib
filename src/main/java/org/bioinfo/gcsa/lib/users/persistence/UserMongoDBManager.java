package org.bioinfo.gcsa.lib.users.persistence;

import java.net.UnknownHostException;
import java.util.List;

import org.bioinfo.gcsa.lib.users.IOManager;
import org.bioinfo.gcsa.lib.users.beans.Project;
import org.bioinfo.gcsa.lib.users.beans.User;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

public class UserMongoDBManager implements UserManager {
	private IOManager ioManager = new IOManager();
	private Mongo mongo;
	private DB mongoDB;

	public UserMongoDBManager() throws UserManagementException {
		// TODO Hacer el pool de 10 conexiones a mongo
		try {
			mongo = new Mongo("127.0.0.1", 27017);
		} catch (UnknownHostException e) {
			throw new UserManagementException(
					"ERROR: Not connected to mongoDB " + e.toString());
		} catch (MongoException e) {
			throw new UserManagementException(
					"ERROR: Not connected to mongoDB " + e.toString());
		}
	}

	private DBCollection getCollection(String nameCollection) {
		DBCollection userCollection = null;
		
		if (!mongoDB.collectionExists("users")) {
			userCollection = mongoDB.createCollection("users", new BasicDBObject());
		} else {
			userCollection = mongoDB.getCollection("users");
		}
		
		return userCollection;
	}
	
	private boolean accountIdExist(String accountID)
			throws UserManagementException {

		return false;
	}

	
	private void getDataBase(String nameDataBase){
		
	}
	public void createAccountId(String accountId, String password,
			String accountName, String email) throws UserManagementException {

		ioManager.createAccountId(accountId);
		User user = new User(accountId, accountName, password, email);

		DB db = mongo.getDB("usertest");
		this.getDataBase("usertest");
		this.getCollection("user");
		
		// userCollection.insert((DBObject) JSON.parse(new Gson().toJson(user)
		// .toString()));
		System.out.println(new Gson().toJson(user).toString());
		System.out.println(">>>>>" + JSON.parse(new Gson().toJson(user)));
		//userCollection.insert((DBObject) JSON.parse(new Gson().toJson(user)));
		// //////////////////////////

	}

	public void createAnonymousUser(String accountId, String password,
			String email) {

	}

	public String login(String accountId, String password) {
		return null;
	}

	public String getUserByAccountId(String accountId, String sessionId) {
		return null;
	}

	public String getUserByEmail(String email, String sessionId) {
		return null;
	}

	public void checkSessionId(String accountId, String sessionId) {

	}

	public String getAllProjectsBySessionId(String accountId, String sessionId) {
		return null;
	}

	public void createProject(String accountId, String sessionId)
			throws UserManagementException {
		// try {
		//
		// }catch(IOException e) {
		// threo new UserMangeentException("": e.toString);
		// }
	}

	public List<Project> jsonToProjectList(String json) {
		return null;
	}

	public void createProject(Project project, String accountId,
			String sessionId) throws UserManagementException {
		// TODO Auto-generated method stub

	}

	public void createUser(String accountId, String password,
			String accountName, String email) throws UserManagementException {
		// TODO Auto-generated method stub

	}

}
