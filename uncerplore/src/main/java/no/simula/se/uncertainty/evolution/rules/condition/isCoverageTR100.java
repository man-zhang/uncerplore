package no.simula.se.uncertainty.evolution.rules.condition;

import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;

import no.simula.se.uncertainty.evolution.domain.UncertainWorld;

//final state
public class isCoverageTR100 extends Node {

	public isCoverageTR100(){
		super(Boolean.class);
	}
	
	@Override
	public Node copyNode() {
		return new isCoverageTR100();
	}

	@Override
	public Boolean evaluate(Environment environment) {
		UncertainWorld map = environment.get(UncertainWorld.class, "world");
		return map.isCoverageOfTrBetween(0.6, 0.8);
	}

}
