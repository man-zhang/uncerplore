package no.simula.se.uncertainty.evolution.rules.action;

import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;

import no.simula.se.uncertainty.evolution.domain.TransitionOption;
import no.simula.se.uncertainty.evolution.domain.UncertainWorld;
import no.simula.se.uncertainty.evolution.util.DiscoverUncertaintyException;

public class T4 extends Node{

	public T4(){
		super(Void.class, Void.class);
	}

	@Override
	public Node copyNode() {
		return new T4();
	}

	@Override
	public Object evaluate(Environment environment) {
		UncertainWorld map = environment.get(UncertainWorld.class, "world");

		if(map.hasExecludedOps()){
			try {
				map.transit(TransitionOption.ExcludedLessOp, null);
			} catch (DiscoverUncertaintyException e) {
				e.printStackTrace();
			}
		}else{
			return getArgument(0).evaluate(environment);
		}
		return null;
	}
}
