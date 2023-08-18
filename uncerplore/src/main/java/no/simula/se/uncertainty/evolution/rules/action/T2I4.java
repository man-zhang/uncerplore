package no.simula.se.uncertainty.evolution.rules.action;

import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;

import no.simula.se.uncertainty.evolution.domain.IntroduceIndSOption;
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

public class T2I4 extends Node{
	public T2I4(){
		super();
	}

	@Override
	public Node copyNode() {
		return new T2I4();
	}

	@Override
	public Object evaluate(Environment environment) {
		UncertainWorld map = environment.get(UncertainWorld.class, "world");

		try {
			map.transit(TransitionOption.HighUncertain, IntroduceIndSOption.MakeNone);
		} catch (DiscoverUncertaintyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
