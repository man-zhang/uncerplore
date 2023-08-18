package no.simula.se.uncertainty.evolution.domain;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.uml2.uml.Constraint;

public class IndSp extends BElement{

	private static final long serialVersionUID = 3272597242526845340L;
	
	private BConstraint specification;
	private Set<BOperation> triggers;
	private Set<BOperation> releasers;
	public IndSp(){}
	
	public IndSp(BConstraint bcons){
		this.specification = bcons;
	}
	
	public IndSp(Constraint constraint, String instance_name){
		this(new BConstraint(constraint, instance_name));
	}

	public BConstraint getSpecification() {
		return specification;
	}
	public void setSpecification(BConstraint specification) {
		this.specification = specification;
	}
	public Set<BOperation> getTriggers() {
		if(triggers == null) triggers = new HashSet<BOperation>();
		return triggers;
	}
	public void setTriggers(Set<BOperation> triggers) {
		this.triggers = triggers;
	}

	public Set<BOperation> getReleasers() {
		if(releasers == null) releasers = new HashSet<BOperation>();
		return releasers;
	}

	public void setReleasers(Set<BOperation> releasers) {
		this.releasers = releasers;
	}
	
	
}
