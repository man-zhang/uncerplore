package no.simula.se.uncertainty.evolution.rules.condition;

import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;

import no.simula.se.uncertainty.evolution.domain.UncertainWorld;
import no.simula.se.uncertainty.evolution.util.DiscoverUncertaintyException;

//final state
public class BranchStatusOfInds extends Node {

	public BranchStatusOfInds(){
		super(Void.class, Void.class, Void.class, Void.class, Void.class);
	}

	@Override
	public Node copyNode() {
		return new BranchStatusOfInds();
	}

	@Override
	public Object evaluate(Environment environment) {
		UncertainWorld map = environment.get(UncertainWorld.class, "world");
		if(map.getPossibleNext() == 1){
			return getArgument(0).evaluate(environment);
		}else if(map.getPossibleNext() == 2){
			return getArgument(1).evaluate(environment);
		}else if (map.getPossibleNext() == 3){
			return getArgument(2).evaluate(environment);
		}else if (map.getPossibleNext() == 4){
			return getArgument(3).evaluate(environment);
		}

		else{
			map.setTerminate(true);
			try {
				throw new DiscoverUncertaintyException("out of branch option");
			} catch (DiscoverUncertaintyException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
