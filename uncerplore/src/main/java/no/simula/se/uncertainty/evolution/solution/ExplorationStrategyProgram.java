package no.simula.se.uncertainty.evolution.solution;

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.variable.Program;
import org.moeaframework.util.tree.Rules;

@SuppressWarnings("serial")
public class ExplorationStrategyProgram extends Program {


	private List<String> newTransition;
	private List<String> newStates;

	public ExplorationStrategyProgram(Rules rules) {
		super(rules);
	}

	public List<String> getNewTransition() {
		return newTransition;
	}

	public void setNewTransition(List<String> newTransition) {
		if(this.newTransition == null) this.newTransition = new ArrayList<String>();
		this.newTransition.clear();
		for(String nTR : newTransition){
			this.newTransition.add(nTR);
		}
	}

	public List<String> getNewStates() {
		return newStates;
	}

	public void setNewStates(List<String> newStates) {
		if(this.newStates == null) this.newStates = new ArrayList<String>();
		this.newStates.clear();
		for(String nSt : newStates){
			this.newStates.add(nSt);
		}
	}

	public String showTRs(){
		String result = "";
		for(String tr : this.newTransition){
			result = result + tr+"\n";
		}
		return result;
	}

	@Override
	public ExplorationStrategyProgram copy() {
		return (ExplorationStrategyProgram)copyTree();
	}

	@Override
	public ExplorationStrategyProgram copyNode() {
		return new ExplorationStrategyProgram(getRules());
	}
}
