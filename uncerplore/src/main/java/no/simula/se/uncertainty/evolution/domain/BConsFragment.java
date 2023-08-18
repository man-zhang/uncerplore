package no.simula.se.uncertainty.evolution.domain;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import no.simula.se.uncertainty.evolution.rules.ExploreUncertainWorldProblem;
import no.simula.se.uncertainty.evolution.util.ConstraintUtility;
import no.simula.se.uncertainty.evolution.util.WeakenConstraint;

public class BConsFragment extends BElement {

	private static final long serialVersionUID = 1L;

	public BConsFragment(){}

	public BConsFragment(String name){
		this.setName(name);
	}

	private Set<String> properties;
	private String instance_name;

	public Set<String> getProperties() {
		if(properties == null) properties = new HashSet<String>();
		return properties;
	}

	public void setProperties(Set<String> properties) {
		this.properties = properties;
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

	public boolean evaluate(){
		if(this.getName() == null){
			System.err.println("name is null");
		}
		if(this.getInstance() == null){
			System.err.println(this.getName()+" instance is null");
		}
		return ConstraintUtility.evaluateOCL(this.getName(), getInstance());
	}

	public boolean containProperties(Set<String> properties){
		for(String pro : properties){
			if(this.getProperties().contains(pro))
				return true;
		}
		return false;
	}

	public Set<String> weakenConstraints(){
		Set<String> result = new HashSet<String>();
		for(String property : this.getProperties()){
			Set<String> tmp = WeakenConstraint.weakenConstraint(property,this.getName());
			if(tmp != null) result.addAll(tmp);
		}
		return result;
	}

	public Set<String> evaluateWeakens(){
		Set<String> result = new HashSet<String>();
		for(String cons : weakenConstraints()){
			if(ConstraintUtility.evaluateOCL(cons, getInstance())){
				result.add(cons);
			}
		}
		return result;
	}
}
