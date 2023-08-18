package no.simula.se.uncertainty.evolution.rules.action;

import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;

import no.simula.se.uncertainty.evolution.domain.TransitionOption;
import no.simula.se.uncertainty.evolution.domain.UncertainWorld;
import no.simula.se.uncertainty.evolution.util.DiscoverUncertaintyException;


//1	TransitOption
//a.	T1:TransitLessExecutedInSpecified
//b.	T2:TransitHighUncertaintyInSpecified
//c.	T3:TransitNonSpecified
//2	IntroduceIndSOption
//a.	I1:MakeSpecifiedOccurred
//b.	I2:MakeNonSpecifiedOccurred
//c.	I3:MakeCombined
//d.	I4:MakeNone

public class T1 extends Node{
	public T1(){
		super();
	}

	@Override
	public Node copyNode() {
		return new T1();
	}

	@Override
	public Object evaluate(Environment environment) {
		UncertainWorld map = environment.get(UncertainWorld.class, "world");

		try {
			map.transit(TransitionOption.LessExecuted, null);
		} catch (DiscoverUncertaintyException e) {
			e.printStackTrace();
		}
		return null;
	}
}
