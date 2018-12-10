package com.euroland.earningcalendar.model;

public class DatePattern {
	
	private String regex;
	private  Integer year;
	private Integer month;
	private Integer day;
	
	public String getRegex() {
		return regex;
	}
	
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	public Integer getYear() {
		return year;
	}
	
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public Integer getMonth() {
		return month;
	}
	
	public void setMonth(Integer month) {
		this.month = month;
	}
	
	public Integer getDay() {
		return day;
	}
	
	public void setDay(Integer day) {
		this.day = day;
	}

	public DatePattern() {
		super();
	}
}
