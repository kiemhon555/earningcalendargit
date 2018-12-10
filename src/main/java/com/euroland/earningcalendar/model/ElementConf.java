package com.euroland.earningcalendar.model;

public class ElementConf {

	private String name;
	private String type;
	private String selectorType;
	private String selector;
	private String action;
	private Integer actionTimes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSelectorType() {
		return selectorType;
	}

	public void setSelectorType(String selectorType) {
		this.selectorType = selectorType;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Integer getActionTimes() {
		return actionTimes;
	}

	public void setActionTimes(Integer actionTimes) {
		this.actionTimes = actionTimes;
	}

	public ElementConf() {
		super();
	}

}
