package com.euroland.earningcalendar.model.source;

public class ElementBtn {
	
	private String name;
	private String clicks;
	private String selectorType;
	private String selector;
	private boolean clickOnLoad;

	private int index;
	private int incrementation;

	public boolean isClickOnLoad() {
		return clickOnLoad;
	}

	public void setClickOnLoad(boolean clickOnLoad) {
		this.clickOnLoad = clickOnLoad;
	}
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIncrementation() {
		return incrementation;
	}

	public void setIncrementation(int incrementation) {
		this.incrementation = incrementation;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getClicks() {
		return clicks;
	}
	
	public void setClicks(String clicks) {
		this.clicks = clicks;
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

	public ElementBtn() {
		super();
	}
	
}
