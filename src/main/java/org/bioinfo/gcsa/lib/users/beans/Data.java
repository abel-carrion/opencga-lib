package org.bioinfo.gcsa.lib.users.beans;
import java.util.List;
import java.util.Vector;


public class Data {
	private String id;
	private String fileName;
	private String multiple;
	private String diskUsage;
	private String creationTime;
	private String responsible;
	private String organization;
	private String date;
	private String description;
	private String status;
	private String statusMessage;
	private List<Member> members;
	
	public Data(){
		members = new Vector<Member>();
		members.add(new Member());
		this.id = "";
		this.fileName = "";
		this.multiple = "";
		this.diskUsage = "";
		this.creationTime = "";
		this.responsible = "";
		this.organization = "";
		this.date = "";
		this.description = "";
		this.status = "";
		this.statusMessage = "";
	}

	public Data(String id, String fileName, String multiple, String diskUsage,
			String creationTime, String responsible, String organization,
			String date, String description, String status,
			String statusMessage, List<Member> members) {
		super();
		this.id = id;
		this.fileName = fileName;
		this.multiple = multiple;
		this.diskUsage = diskUsage;
		this.creationTime = creationTime;
		this.responsible = responsible;
		this.organization = organization;
		this.date = date;
		this.description = description;
		this.status = status;
		this.statusMessage = statusMessage;
		this.members = members;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getMultiple() {
		return multiple;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}

	public String getDiskUsage() {
		return diskUsage;
	}

	public void setDiskUsage(String diskUsage) {
		this.diskUsage = diskUsage;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public String getResponsible() {
		return responsible;
	}

	public void setResponsible(String responsible) {
		this.responsible = responsible;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}


	
	
}