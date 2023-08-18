package no.simula.se.uncertainty.evolution.rules.action;

import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;

import no.simula.se.uncertainty.evolution.domain.TransitionOption;
import no.simula.se.uncertainty.evolution.domain.UncertainWorld;
import no.simula.se.uncertainty.evolution.util.DiscoverUncertaintyException;

public class TransitHighUncertainty extends Node{

	public TransitHighUncertainty(){
		super();
	}

	@Override
	public Node copyNode() {
		return new TransitHighUncertainty();
	}

	@Override
	public Object evaluate(Environment environment) {
		UncertainWorld map = environment.get(UncertainWorld.class, "world");
		try {
			map.transit(TransitionOption.HighUncertain, null);
		} catch (DiscoverUncertaintyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
