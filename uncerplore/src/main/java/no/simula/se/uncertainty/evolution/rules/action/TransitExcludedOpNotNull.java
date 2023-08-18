package no.simula.se.uncertainty.evolution.rules.action;

import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;

import no.simula.se.uncertainty.evolution.domain.TransitionOption;
import no.simula.se.uncertainty.evolution.domain.UncertainWorld;
import no.simula.se.uncertainty.evolution.util.DiscoverUncertaintyException;

public class TransitExcludedOpNotNull extends Node{

	public TransitExcludedOpNotNull(){
		super(Void.class, Void.class);
	}

	@Override
	public Node copyNode() {
		return new TransitExcludedOpNotNull();
	}

	@Override
	public Object evaluate(Environment environment) {
		UncertainWorld map = environment.get(UncertainWorld.class, "world");
		if(map.hasExecludedOps()){
			try {
				map.transit(TransitionOption.LessExecuted, null);
			} catch (DiscoverUncertaintyException e) {
				e.printStackTrace();
			}
		}else{
			return getArgument(0).evaluate(environment);
		}
		return null;
	}
}
