package no.simula.se.uncertainty.evolution.domain;

import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.TimeEvent;

import no.simula.se.uncertainty.evolution.util.Utility;

public class BAfterOperation extends BOperation{
	

	private static final long serialVersionUID = -5135422600840257957L;

	public BAfterOperation(String strTime, int time){
		this.setName(strTime);
		this.setPars(Integer.class);
		this.setOpName("wait");
		this.setParObjs(time);
		//System.out.println(this.getParObjs().length+" "+this.getParObjs()[0]);
	}
	
	
	public BAfterOperation(String strTime){
		this(strTime, Utility.parseTimeExpression(strTime));
		
	}
	
	public BAfterOperation(TimeEvent event){
		this(((LiteralString)((TimeEvent)event).getWhen().getExpr()).getValue());
	}
	
}
