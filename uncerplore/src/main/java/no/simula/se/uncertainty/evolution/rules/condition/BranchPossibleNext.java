package no.simula.se.uncertainty.evolution.rules.condition;

import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;

import no.simula.se.uncertainty.evolution.domain.UncertainWorld;
import no.simula.se.uncertainty.evolution.util.DiscoverUncertaintyException;

//final state
public class BranchPossibleNext extends Node {

	public BranchPossibleNext(){
		super(Void.class, Void.class, Void.class, Void.class, Void.class);
	}

	@Override
	public Node copyNode() {
		return new BranchPossibleNext();
	}

	@Override
	public Object evaluate(Environment environment) {
		UncertainWorld map = environment.get(UncertainWorld.class, "world");
		int option = map.getPossibleNext();
		if( option == 1){
			return getArgument(0).evaluate(environment);
		}else if(option == 2){
			return getArgument(1).evaluate(environment);
		}else if (option == 3){
			return getArgument(2).evaluate(environment);
		}else if (option == 4){
			return getArgument(3).evaluate(environment);
		}

		else{
			map.getPossibleNext();
			map.setTerminate(true);
			try {
				throw new DiscoverUncertaintyException("out of branch option "+map.getCurrentState().getName());
			} catch (DiscoverUncertaintyException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
