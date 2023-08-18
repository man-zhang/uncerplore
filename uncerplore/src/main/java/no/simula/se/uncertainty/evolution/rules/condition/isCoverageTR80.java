package no.simula.se.uncertainty.evolution.rules.condition;

import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;

import no.simula.se.uncertainty.evolution.domain.UncertainWorld;

//final state
public class isCoverageTR80 extends Node {

	public isCoverageTR80(){
		super(Boolean.class);
	}
	
	@Override
	public Node copyNode() {
		return new isCoverageTR80();
	}

	@Override
	public Boolean evaluate(Environment environment) {
		UncertainWorld map = environment.get(UncertainWorld.class, "world");
		return map.isCoverageOfTrBetween(0.8, 1.0);
	}

}
