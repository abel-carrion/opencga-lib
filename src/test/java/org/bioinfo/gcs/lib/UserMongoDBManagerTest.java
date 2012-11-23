package org.bioinfo.gcs.lib;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.bioinfo.gcsa.lib.users.CloudSessionManager;
import org.bioinfo.gcsa.lib.users.persistence.UserManager;
import org.junit.Test;

public class UserMongoDBManagerTest {
	private UserManager userManager;
	@Test
	public void createFileToProjectTest() {
		try {
			CloudSessionManager cloudSessionManager = new CloudSessionManager(System.getenv("GCSA_HOME"));
			userManager = cloudSessionManager.userManager;
			
			String sessionId = "JIzomN9jETGIC4RSXsX0";
			String fileName = "datos_experimento.txt";
			String data = "sampletext";
			InputStream fileData = new ByteArrayInputStream(data.getBytes("UTF-8"));  
			
			userManager.createFileToProject("Default", fileName, fileData, sessionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}