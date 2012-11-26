package org.bioinfo.gcs.lib;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.bioinfo.gcsa.lib.users.CloudSessionManager;
import org.bioinfo.gcsa.lib.users.beans.Session;
import org.bioinfo.gcsa.lib.users.persistence.UserManager;
import org.junit.Test;

public class UserMongoDBManagerTest {
	private UserManager userManager;
	@Test
	public void createFileToProjectTest() {
		if(new File("/home/examples/bam/HG00096.chrom20.ILLUMINA.bwa.GBR.exome.20111114.bam").exists()){
			try {
				CloudSessionManager cloudSessionManager = new CloudSessionManager(System.getenv("GCSA_HOME"));
				userManager = cloudSessionManager.userManager;
				
				String sessionId = "JIzomN9jETGIC4RSXsX0";
				File f = new File("/home/examples/bam/HG00096.chrom20.ILLUMINA.bwa.GBR.exome.20111114.bam");
				String data = "sampletext";
				InputStream fileData = new FileInputStream(f);
				userManager.createFileToProject("Default", f.getName(), fileData, sessionId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	@Test
	public void getUserBySessionIdTest() {
		try {
			CloudSessionManager cloudSessionManager = new CloudSessionManager(System.getenv("GCSA_HOME"));
			userManager = cloudSessionManager.userManager;
			
			String sessionId = "OPq8ebQ3FQAPsVZ8cFAc";
			System.out.println(userManager.getUserBySessionId(sessionId));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
//	@Test
//	public void loginTest() {
//	}
	
//	@Test
//	public void createTest() {
//			try {
//				CloudSessionManager cloudSessionManager = new CloudSessionManager(System.getenv("GCSA_HOME"));
//				userManager = cloudSessionManager.userManager;
//				
//				String accountId = "fsalavert";
//				String password = "hola";
//				Session session = new Session();
//				
//				System.out.println(userManager.login(accountId, password, session));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//	}
	
//	@Test
	public void createJobTest() {
		try {
			CloudSessionManager cloudSessionManager = new CloudSessionManager(System.getenv("GCSA_HOME"));
			userManager = cloudSessionManager.userManager;
			
			String sessionId = "JLymlrv3eWm5jIjAVwty";
			
			userManager.createJob("", "", new ArrayList<String>(), sessionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
