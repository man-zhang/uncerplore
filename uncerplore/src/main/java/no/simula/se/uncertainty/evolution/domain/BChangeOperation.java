package no.simula.se.uncertainty.evolution.domain;


import org.eclipse.uml2.uml.ChangeEvent;

public class BChangeOperation extends BOperation{

	private static final long serialVersionUID = -7641076069273239314L;
	private BConstraint constraint;
	
	
	public BChangeOperation(BConstraint bcons){
		this.setName(bcons.getConstraint());
		this.setPars(BConstraint.class);
		this.setOpName("waitUtil");
		this.setParObjs(bcons);
		
	}
	
	public BChangeOperation(ChangeEvent event, String instance){
		this(new BConstraint(event, instance));
	}

	public BConstraint getConstraint() {
		return constraint;
	}

	public void setConstraint(BConstraint constraint) {
		this.constraint = constraint;
	}
}
