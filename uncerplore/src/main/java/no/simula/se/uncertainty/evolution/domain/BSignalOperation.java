package no.simula.se.uncertainty.evolution.domain;

public class BSignalOperation extends BOperation{
	
	private static final long serialVersionUID = -8834321717872551868L;

	public BSignalOperation(String opName, Class<?>... pars){
		setOpName(opName);
		setPars(pars);
	}
	
	public BSignalOperation(String guard,String instance, String opName, Class<?>... pars){
		this(opName, pars);
		setGuard(new BConstraint(guard, instance, ConstraintContainerType.Guard));
	}
}
