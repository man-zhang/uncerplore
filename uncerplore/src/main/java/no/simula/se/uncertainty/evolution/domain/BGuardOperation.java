package no.simula.se.uncertainty.evolution.domain;

public class BGuardOperation extends BOperation{

	private static final long serialVersionUID = -8834321717872551868L;


	public BGuardOperation(String guard,String instance){
		setGuard(new BConstraint(guard, instance, ConstraintContainerType.Guard));
		this.setName(guard);
		this.setOpName("noAction");
	}

	public String execute(){
		count1Visit();
		return getName();
	}
}
