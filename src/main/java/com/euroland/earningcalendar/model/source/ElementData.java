package com.euroland.earningcalendar.model.source;

public class ElementData {

	private String name;
	private String type;
	private String ref;
	private String selector;
	private String selectorType;
	private String splitter;
	private int position;
	
	public String getSplitter() {
		return splitter;
	}

	public void setSplitter(String splitter) {
		this.splitter = splitter;
	}
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSelector() {
		return selector;
	}
	
	public void setSelector(String selector) {
		this.selector = selector;
	}
	
	public String getSelectorType() {
		return selectorType;
	}
	
	public void setSelectorType(String selectorType) {
		this.selectorType = selectorType;
	}

	public ElementData() {
		super();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}
}
