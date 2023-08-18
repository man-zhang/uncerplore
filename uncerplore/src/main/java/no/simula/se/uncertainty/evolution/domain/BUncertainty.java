package no.simula.se.uncertainty.evolution.domain;

import java.util.HashSet;
import java.util.Set;

public class BUncertainty extends BElement{
	
	private static final long serialVersionUID = 3179019205648959189L;
	
	private BState ss;
	private BOperation op;
	private BState ts;
	
	private double degree = 1.0;
	
	private Set<IndSp> indSps;
	
	public BUncertainty(BState ss, BOperation op, BState ts){
		this.ss = ss;
		this.op = op;
		this.ts = ts;
	}

	public BState getSs() {
		return ss;
	}

	public void setSs(BState ss) {
		this.ss = ss;
	}

	public BOperation getOp() {
		return op;
	}

	public void setOp(BOperation op) {
		this.op = op;
	}

	public BState getTs() {
		return ts;
	}

	public void setTs(BState ts) {
		this.ts = ts;
	}

	public double getDegree() {
		return degree;
	}

	public void setDegree(double degree) {
		this.degree = degree;
	}

	public Set<IndSp> getIndSps() {
		if(indSps == null) indSps = new HashSet<IndSp>();
		return indSps;
	}

	public void setIndSps(Set<IndSp> indSps) {
		this.indSps = indSps;
	}

	
}
