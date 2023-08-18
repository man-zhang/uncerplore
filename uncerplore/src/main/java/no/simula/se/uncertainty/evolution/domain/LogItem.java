package no.simula.se.uncertainty.evolution.domain;

import java.util.Date;

public class LogItem {
	private Date date;

	private String ss;
	private String op;
	private String ts;
	private String inds;
	
	public LogItem(Date date, String ss, String op, String ts, String inds){
		this.date = date;
		this.ss = ss;
		this.op = op;
		this.ts = ts;
		this.inds = inds;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getSs() {
		return ss;
	}
	public void setSs(String ss) {
		this.ss = ss;
	}
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
	}
	public String getTs() {
		return ts;
	}
	public void setTs(String ts) {
		this.ts = ts;
	}
	public String getInds() {
		return inds;
	}
	public void setInds(String inds) {
		this.inds = inds;
	}
	
	
}
