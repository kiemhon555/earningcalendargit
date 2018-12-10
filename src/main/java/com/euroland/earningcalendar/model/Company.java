package com.euroland.earningcalendar.model;

public class Company {
	private String companyName;
	private String configFile;
	private String companyUrl;
	
	public Company() {
		super();
	}
	public Company(String companyName, String configFile, String companyUrl) {
		super();
		this.companyName = companyName;
		this.configFile = configFile;
		this.companyUrl = companyUrl;
	}
	
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getConfigFile() {
		return configFile;
	}
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
	public String getCompanyUrl() {
		return companyUrl;
	}
	public void setCompanyUrl(String companyUrl) {
		this.companyUrl = companyUrl;
	}

}
