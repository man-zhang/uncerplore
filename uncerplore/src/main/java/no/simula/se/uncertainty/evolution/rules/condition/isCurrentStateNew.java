package no.simula.se.uncertainty.evolution.rules.condition;

import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;

import no.simula.se.uncertainty.evolution.domain.UncertainWorld;

//final state
public class isCurrentStateNew extends Node {

	public isCurrentStateNew(){
		super(Boolean.class);
	}
	
	@Override
	public Node copyNode() {
		return new isCurrentStateNew();
	}

	@Override
	public Boolean evaluate(Environment environment) {
		UncertainWorld map = environment.get(UncertainWorld.class, "world");
		return map.isCurrentStateNew();
	}

}
