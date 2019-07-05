package com.euroland.earningcalendar.model.mail;

public class MailObject {

	private String to;
	private String cc;
	private String subject;
	private String body;

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public MailObject(String to, String cc, String subject, String body) {
		super();
		this.to = to;
		this.cc = cc;
		this.subject = subject;
		this.body = body;
	}

	public MailObject() {
		super();
	}

	@Override
	public String toString() {
		return "MailObject [to=" + to + ", cc=" + cc + ", subject=" + subject + ", body=" + body + "]";
	}

}
