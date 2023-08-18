package no.simula.se.uncertainty.evolution.rules.action;

import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;

import no.simula.se.uncertainty.evolution.domain.TransitionOption;
import no.simula.se.uncertainty.evolution.domain.UncertainWorld;
import no.simula.se.uncertainty.evolution.util.DiscoverUncertaintyException;

public class TransitLessExecuted extends Node{

	public TransitLessExecuted(){
		super();
	}

	@Override
	public Node copyNode() {
		return new TransitLessExecuted();
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
