package no.simula.se.uncertainty.evolution.domain;

public class BCallOperation extends BOperation{
	
	private static final long serialVersionUID = -8834321717872551868L;

	public BCallOperation(String opName, Class<?>... pars){
		setName(opName);
		setOpName(opName);
		setPars(pars);
	}
	
	public BCallOperation(String guard,String instance, String opName, Class<?>... pars){
		this(opName, pars);
		setGuard(new BConstraint(guard, instance, ConstraintContainerType.Guard));
	}
}
