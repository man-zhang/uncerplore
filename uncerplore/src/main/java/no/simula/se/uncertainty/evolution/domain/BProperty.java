package no.simula.se.uncertainty.evolution.domain;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import no.simula.se.uncertainty.evolution.rules.ExploreUncertainWorldProblem;

public class BProperty extends BElement {

	private static final long serialVersionUID = 1L;

	public BProperty(){}

	public BProperty(String name){
		this.setName(name);
	}

	private Set<String> consfragements;
	private String instance_name;


	public Set<String> getConsfragements() {
		if(consfragements == null) consfragements = new HashSet<String>();
		return consfragements;
	}

	public void setConsfragements(Set<String> consfragements) {
		this.consfragements = consfragements;
	}

	public String getInstance_name() {
		return instance_name;
	}

	public void setInstance_name(String instance_name) {
		this.instance_name = instance_name;
	}

	public EObject getInstance() {
		return ExploreUncertainWorldProblem.world.getInstance(instance_name);
	}
}
