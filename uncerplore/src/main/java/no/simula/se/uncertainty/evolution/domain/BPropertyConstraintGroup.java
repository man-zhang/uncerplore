package no.simula.se.uncertainty.evolution.domain;

import java.util.HashSet;
import java.util.Set;

public class BPropertyConstraintGroup {
	private String property;
	private String instance_name;
	private Set<String> consFrags;
	
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getInstance_name() {
		return instance_name;
	}
	public void setInstance_name(String instance_name) {
		this.instance_name = instance_name;
	}
	public Set<String> getConsFrags() {
		if(consFrags == null) consFrags = new HashSet<String>();
		return consFrags;
	}
	public void setConsFrags(Set<String> consFrags) {
		this.consFrags = consFrags;
	}
	
}
