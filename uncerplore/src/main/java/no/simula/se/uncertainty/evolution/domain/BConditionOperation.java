package no.simula.se.uncertainty.evolution.domain;

import no.simula.se.uncertainty.evolution.rules.ExploreUncertainWorldProblem;

public class BConditionOperation extends BOperation{

	private static final long serialVersionUID = -8834321717872551868L;

	private BOperation[] branches;
	private int index = -1;

	public BConditionOperation(BOperation... ops){
		this.branches = new BOperation[ops.length];
		String name = "";
		int i = 0;
		for(BOperation op: ops){
			if(!name.isEmpty()) name = name + "$";
			name = name + op.getName();
			branches[i] = op;
			i++;
		}
		this.setName(name);
	}

	public BOperation[] getBranches() {
		return branches;
	}

	public void setBranches(BOperation[] branches) {
		this.branches = branches;
	}


	public String execute(){
		count1Visit();

		this.index = getExecuteOp();
		return this.getBranches()[index].execute();
	}

	public boolean validate(){
		int tTimes = 0;
		for(BOperation op: this.getBranches()){
			if( op.getGuard().evaluate()) tTimes++;
		}
		return (tTimes == 1);
	}

	public int getExecuteOp(){
		int i = 0;
		for(BOperation op: this.getBranches()){
			if( op.getGuard().evaluate()) return i;
		}
		return -1;
	}

	public int getErrorType(){
		int tTimes = 0;
		for(BOperation op: this.getBranches()){
			if( op.getGuard().evaluate()) tTimes++;
		}
		if(tTimes > 1) return -2;
		return -1;
	}

	public static String getCondOpName(String[] opkeys){
		String name = "";
		for(String key: opkeys){
			if(!name.isEmpty()) name = name + "$";
			name = name + key;
			//name = name +"$"+key;
		}
		return name;
	}

	public BOperation getExceptedBOperation(){
		return this.getBranches()[index];
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public UncertainWorld getWorld(){
		return ExploreUncertainWorldProblem.world;
	}
}
