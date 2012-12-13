package org.bioinfo.gcsa.lib.account;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.bioinfo.commons.log.Logger;
import org.bioinfo.gcsa.lib.account.beans.Data;
import org.bioinfo.gcsa.lib.account.beans.Plugin;
import org.bioinfo.gcsa.lib.account.beans.Project;
import org.bioinfo.gcsa.lib.account.beans.Session;
import org.bioinfo.gcsa.lib.account.db.AccountFileManager;
import org.bioinfo.gcsa.lib.account.db.AccountManagementException;
import org.bioinfo.gcsa.lib.account.db.AccountManager;
import org.bioinfo.gcsa.lib.account.db.AccountMongoDBManager;
import org.bioinfo.gcsa.lib.account.io.IOManagementException;
import org.bioinfo.gcsa.lib.account.io.IOManager;

public class CloudSessionManager {

	private AccountManager accountManager;
	private Logger logger;
	private IOManager ioManager;
	public static Properties properties;

	public CloudSessionManager() throws FileNotFoundException, IOException, AccountManagementException {
		this(System.getenv("GCSA_HOME"));
	}

	public CloudSessionManager(String gcsaHome) throws FileNotFoundException, IOException, AccountManagementException {
		logger = new Logger();
		properties = new Properties();

		File propertiesFile = new File(gcsaHome + "/conf/account.properties");
		if (gcsaHome != null && propertiesFile.exists()) {
			properties.load(new FileInputStream(propertiesFile));
			if (properties.getProperty("GCSA.ACCOUNT.MODE").equals("file")) {
				accountManager = (AccountManager) new AccountFileManager(properties);// TODO
			} else {
				accountManager = new AccountMongoDBManager(properties);
			}
			ioManager = new IOManager(properties);

			logger.info(properties.toString());
		} else {
			logger.error("properties file not found");
		}
	}

	/*******************************/
	public void createAccount(String accountId, String password, String accountName, String email, String sessionIp)
			throws AccountManagementException {
		checkStr(accountId, "accountId");
		checkStr(password, "password");
		checkStr(accountName, "accountName");
		checkEmail(email);
		checkStr(sessionIp, "sessionIp");
		Session session = new Session(sessionIp);

		accountManager.createAccount(accountId, password, accountName, email, session);
	}

	public String login(String accountId, String password, String sessionIp) throws AccountManagementException {
		checkStr(accountId, "accountId");
		checkStr(password, "password");
		checkStr(sessionIp, "sessionIp");
		Session session = new Session(sessionIp);
		return accountManager.login(accountId, password, session);
	}

	public void logout(String accountId, String sessionId) throws AccountManagementException {
		checkStr(accountId, "accountId");
		checkStr(sessionId, "sessionId");
		accountManager.logout(accountId, sessionId);
	}

	public void changePassword(String accountId, String sessionId, String password, String nPassword1, String nPassword2)
			throws AccountManagementException {
		checkStr(accountId, "accountId");
		checkStr(sessionId, "sessionId");
		checkStr(password, "password");
		checkStr(nPassword1, "nPassword1");
		checkStr(nPassword2, "nPassword2");
		if (!nPassword1.equals(nPassword2)) {
			throw new AccountManagementException("the new pass is not the same in both fields");
		}
		accountManager.changePassword(accountId, sessionId, password, nPassword1, nPassword2);
	}

	public void changeEmail(String accountId, String sessionId, String nEmail) throws AccountManagementException {
		checkStr(accountId, "accountId");
		checkStr(sessionId, "sessionId");
		checkEmail(nEmail);
		accountManager.changeEmail(accountId, sessionId, nEmail);
	}

	public void resetPassword(String accountId, String email) throws AccountManagementException {
		checkStr(accountId, "accountId");
		checkEmail(email);
		accountManager.resetPassword(accountId, email);
	}

	public String getAccountInfo(String accountId, String sessionId, String lastActivity)
			throws AccountManagementException {
		checkStr(accountId, "accountId");
		checkStr(sessionId, "sessionId");
		// lastActivity can be null
		return accountManager.getAccountBySessionId(accountId, sessionId, lastActivity);
	}

	public String getDataPath(String projectId, String dataId, String sessionId) {
		return accountManager.getDataPath(projectId, dataId, sessionId);
	}

	public void createProject(Project project, String accountId, String sessionId) throws AccountManagementException {
		checkStr(project.getName(), "projectName");
		checkStr(accountId, "accountId");
		checkStr(sessionId, "sessionId");
		accountManager.createProject(project, accountId, sessionId);
	}

	public String createDataToProject(String project, String accountId, String sessionId, Data data,
			InputStream fileData, String objectname, boolean parents) throws AccountManagementException,
			IOManagementException {
		checkStr(project, "project");
		checkStr(accountId, "accountId");
		checkStr(sessionId, "sessionId");
		checkStr(objectname, "objectname");
		checkObj(data, "data");

		String dataId = ioManager.createData(project, accountId, data, fileData, objectname, parents);
		logger.info(dataId);
		try {
			accountManager.createDataToProject(project, accountId, sessionId, data);
			return dataId;
		} catch (AccountManagementException e) {
			ioManager.deleteData(project, accountId, objectname, null);
			throw e;
		}
	}

	public void deleteDataFromProject(String project, String accountId, String sessionId, String objectname)
			throws AccountManagementException, IOManagementException {
		checkStr(project, "project");
		checkStr(accountId, "accountId");
		checkStr(sessionId, "sessionId");
		checkStr(objectname, "objectname");

		String dataId = ioManager.deleteData(project, accountId, objectname, null);
		accountManager.deleteDataFromProject(project, accountId, sessionId, dataId);
		logger.info(dataId);

	}

	public String getAccountProjects(String accountId, String sessionId) throws AccountManagementException {
		return accountManager.getAllProjectsBySessionId(accountId, sessionId);
	}

	public String createJob(String jobName, String jobFolder, String project, String toolName, List<String> dataList,
			String commandLine, String sessionId) {
		return accountManager.createJob(jobName, jobFolder, project, toolName, dataList, commandLine, sessionId);
	}

	public String getJobFolder(String project, String jobId, String sessionId) {
		return accountManager.getJobFolder(project, jobId, sessionId);
	}

	public List<Plugin> getUserAnalysis(String sessionId) throws AccountManagementException {
		return accountManager.getUserAnalysis(sessionId);
	}

	/********************/
	private void checkEmail(String email) throws AccountManagementException {
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		if (!pattern.matcher(email).matches()) {
			throw new AccountManagementException("email not valid");
		}
	}

	private void checkStr(String str, String name) throws AccountManagementException {
		if (str == null || str.equals("")) {
			throw new AccountManagementException("parameter '" + name + "' is null or empty: " + str + ".");
		}
	}

	private void checkObj(Object obj, String name) throws AccountManagementException {
		if (obj == null) {
			throw new AccountManagementException("parameter '" + name + "' is null.");
		}
	}
}