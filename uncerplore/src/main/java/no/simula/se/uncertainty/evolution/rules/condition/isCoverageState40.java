package no.simula.se.uncertainty.evolution.rules.condition;

import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;

import no.simula.se.uncertainty.evolution.domain.UncertainWorld;

//final state
public class isCoverageState40 extends Node {

	public isCoverageState40(){
		super(Boolean.class);
	}
	
	@Override
	public Node copyNode() {
		return new isCoverageState40();
	}

	@Override
	public Boolean evaluate(Environment environment) {
		UncertainWorld map = environment.get(UncertainWorld.class, "world");
		return map.isCoverageOfStBetween(0.2, 0.4);
	}

}
