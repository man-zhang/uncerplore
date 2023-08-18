package no.simula.se.uncertainty.evolution.rules.condition;

import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;

import no.simula.se.uncertainty.evolution.domain.UncertainWorld;

//final state
public class BranchCoverageTR extends Node {

	public BranchCoverageTR(){
		super(Void.class, Void.class, Void.class, Void.class, Void.class);
	}
	
	@Override
	public Node copyNode() {
		return new BranchCoverageTR();
	}

	@Override
	public Object evaluate(Environment environment) {
		UncertainWorld map = environment.get(UncertainWorld.class, "world");
		if(map.isCoverageOfTrBetween(0.0, 0.25)){
			return getArgument(0).evaluate(environment);
		}else if(map.isCoverageOfTrBetween(0.25, 0.5)){
			return getArgument(1).evaluate(environment);
		}else if (map.isCoverageOfTrBetween(0.5, 0.75)){
			return getArgument(2).evaluate(environment);
		}else{
			return getArgument(3).evaluate(environment);
		}
	}
}
