package no.simula.se.uncertainty.evolution.domain;

import java.util.Set;

public class BTransitionProb {
	
	private String id;
	
	private BOperation op;
	private BState target;
	private double probTran = 1;
	
	private Set<IndSp> relatedIndsp;
	private double probTranWithInds;
	private boolean preDef;
	
	
	
	public BOperation getOp() {
		return op;
	}
	public void setOp(BOperation op) {
		this.op = op;
	}
	public BState getTarget() {
		return target;
	}
	public void setTarget(BState target) {
		this.target = target;
	}
	public double getProbTran() {
		return probTran;
	}
	public void setProbTran(double probTran) {
		this.probTran = probTran;
	}
	public Set<IndSp> getRelatedIndsp() {
		return relatedIndsp;
	}
	public void setRelatedIndsp(Set<IndSp> relatedIndsp) {
		this.relatedIndsp = relatedIndsp;
	}
	public double getProbTranWithInds() {
		return probTranWithInds;
	}
	public void setProbTranWithInds(double probTranWithInds) {
		this.probTranWithInds = probTranWithInds;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isPreDef() {
		return preDef;
	}
	public void setPreDef(boolean preDef) {
		this.preDef = preDef;
	}
	
	
}
