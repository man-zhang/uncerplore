package no.simula.se.uncertainty.evolution.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.simula.se.uncertainty.evolution.util.DiscoverUncertaintyException;
import no.simula.se.uncertainty.evolution.util.Utility;

public class BModel extends BElement{

	private static final long serialVersionUID = 4203109304017807281L;
	public final static String UN_SEPATOR = "@";


	private Date startTime;
	private String expName;
	private int generation = -1;
	private double fitness;
	private String solution;

	private int moves = 0;

	private double minCoverageOfSt = 0.0;
	private double coverageOfSt = 0.0;

	private double minCoverageOfOp = 0.0;
	private double coverageOfOp = 0.0;

	private double minCoverageOfUn = 0.0;
	private double coverageOfUn = 0.0;

	private double coverageOfInds = 0.0;

	private int numOfnewSts = 0;
	private int numOfnewUns = 0;

	private List<LogItem> log;
	public BState unKnownS;
	public Map<String, BState> sts = new HashMap<String, BState>();
	public Map<String, BOperation> ops = new HashMap<String, BOperation>();
	public Map<String, BUncertainty> uns = new HashMap<String, BUncertainty>();
	public Map<String, IndSp> inds = new HashMap<String, IndSp>();
	//public Map<String, IndSp> inds = new HashMap<String, IndSp>();

	public Map<String, BConsFragment> pros= new HashMap<String, BConsFragment>();

	private String initalState;

	public BState getModelInital(){
		return this.getSts().get(this.getInitalState());
	}

	public BState getKnownTarget(BState current, BOperation bop){
		for(BUncertainty b : this.getUns().values()){
			if(b.getSs().getName().equals(current.getName()) && b.getOp().getName().equals(bop.getName())){
				return b.getTs();
			}
		}
		return null;
	}

	public BState getSpecifiedTarget(BState current, BOperation bop){
		for(BUncertainty b : this.getUns().values()){
			if(b.getSs().getName().equals(current.getName()) && b.getOp().getName().equals(bop.getName())){
				return b.getTs();
			}
		}

		if(!current.isPreDef()){
			if(current.getReplaceState() != null){
				BState sp = this.getSts().get(current.getReplaceState());
				return getSpecifiedTarget(sp, bop);
			}

		}
		return null;
	}

	public BState getCondSpecifiedTarget(BState current, BOperation bop){
		for(BUncertainty b : this.getUns().values()){
			if(b.getSs().getName().equals(current.getName()) && b.getOp().getName().equals(bop.getName())){
				return b.getTs();
			}
		}
		BState mid = null;
		for(BState cond : getPossibleNextS(current)){
			if(cond.isChoice()){
				mid = getKnownTarget(cond, bop);
			}
		}
		if(mid != null) return mid;
		if(!current.isPreDef()){
			BState  sp = this.getSts().get(current.getReplaceState());
			return getSpecifiedTarget(sp, bop);
		}
		return null;
	}

	public Set<BOperation> getBOpsInUns(BState bstate){
		Set<BOperation> nexts = new HashSet<BOperation>();
		for(BUncertainty b : this.getUns().values()){
			if(b.getSs().getName().equals(bstate.getName())){
				nexts.add(b.getOp());
			}
		}
		return nexts;
	}

	public Set<String> getPreDefNext(BState bs){
		Set<String> nexts = null;
		if(!bs.isPreDef()) return nexts;
		for(BUncertainty b : this.getUns().values()){
			if(b.isPreDef() && b.getSs().getName().equals(bs.getName())){
				if(nexts == null) nexts = new HashSet<String>();
				nexts.add(b.getName());
			}
		}
		return nexts;
	}

	public Set<BState> getNewStatesByReplace(BState cur, Set<BState> reps){
		if(cur.isPreDef()){
			return reps;
		}else{
			reps.add(cur);
			if(cur.getReplaceState() != null){
				return getNewStatesByReplace(this.getSts().get(cur.getReplaceState()), reps);
			}else{
				return reps;
			}

		}
	}

	public BState getPreDefState(BState cur){
		if(cur.isPreDef()){
			return cur;
		}else{
			if(cur.getReplaceState() != null){
				return getPreDefState(this.getSts().get(cur.getReplaceState()));
			}else{
				for(String os : cur.getSourceMaterial()){
					return getPreDefState(this.getSts().get(os));
				}
			}
		}
		return null;
	}

	public int possibleNext(BState state){
		int n1 = 0;
		int n2 = 0;

		Set<String> nexts = this.getPossibleNext(state);

		for(String nu : nexts){
			BUncertainty bun = this.uns.get(nu);
			if(bun.getDegree() < 1.0) n2++;
			else n1++;
		}
		if(n1==1 && n2 == 0) return 1;
		else if(n1 > 1  && n2 == 0) return 2;
		else if(n1 == 0 && n2 > 0) return 3;
		else if(n1 > 0 && n2 > 0) return 4;
		else {
			return -1;
		}
	}

	public Set<BState> getPossibleNextS(BState bs){
		Set<BState> bss = new HashSet<BState>();
		for(BUncertainty b : this.uns.values()){
			if(b.getSs().equals(bs)) bss.add(b.getTs());
		}
		return bss;
	}

	public Set<BState> getPossibleCondNextS(BState bs, BOperation bop){
		Set<BState> bss = new HashSet<BState>();
		for(BUncertainty b : this.uns.values()){
			if(b.getSs().getName().equals(bs.getName())&& b.getOp().getName().equals(bop.getName()) &&b.getTs().isChoice()) {
				bss.add(b.getTs());
			}
		}
		if(bss.size() > 0) return bss;

		if((!bs.isPreDef()) && bs.ndType.compareTo(NewlyDiscoverType.AndCombine)==0){
			for(String sn : bs.getSourceMaterial()){
				bss = getPossibleCondNextS(this.sts.get(sn), bop);
				if(bss.size() > 0) return bss;
			}
		}

		if(bs.getReplaceState() != null){
			bss = getPossibleCondNextS(this.sts.get(bs.getReplaceState()), bop);
			if(bss.size() > 0) return bss;
		}

		return bss;
	}

//	public boolean oneNext(BState bstate){
//		return this.getNext(bstate).size() == 1;
//	}

	public int getDiscoverdSt(){
		int i = 0;
		for(BState st: this.sts.values()){
			if(!st.isPreDef()) i++;
		}
		return i;
	}

	public int getDiscoverdTr(){
		int i = 0;

		for(BUncertainty bu : this.uns.values()){
			if(!bu.isPreDef()) i++;
		}
		return i;
	}

	public double getDiscoverdRate(){
		int i = 0;
		for(BState st: this.sts.values()){
			if(!st.isPreDef()) i++;
		}
		this.setNumOfnewSts(i);
		for(BUncertainty bu : this.uns.values()){
			if(!bu.isPreDef()) i++;
		}
		this.setNumOfnewUns(i - this.getNumOfnewSts());
		return Math.atan(i * 1.0) * 2 / Math.PI;
	}

	public Set<String> getExcludedOp(BState cur){
		Set<String> excludes = new HashSet<String>();
//		Set<String> includes = this.getPossibleNext(cur);

		//define the heuristic to generate exclude operation
//		for(BOperation op : this.ops.values()){
//			if(!includes.contains(op)){
//				excludes.add(op);
//			}
//		}
		return excludes;
	}

	public boolean hasExcludedOps(BState cur){
		return getExcludedOp(cur).size() > 0;
	}

	public boolean exisitUncertainty(BState cur){
		for(BUncertainty b : this.getUns().values()){
			if(b.getSs().getName().equals(cur.getName()) && (b.getDegree() < 1.0)){
				return true;
			}
		}
		return false;
	}

	public boolean exisitNewlyDiscovered(BState cur){
		for(BUncertainty b : this.getUns().values()){
			if(b.getSs().getName().equals(cur.getName()) && (!b.isPreDef())){
				return true;
			}
		}
		return false;
	}

	public boolean isUncertainty(BState cur, BOperation op){
		for(BUncertainty b : this.getUns().values()){
			if(b.getSs().getName().equals(cur.getName()) && b.getOp().getName().equals(op.getName())){
				return (b.getDegree() < 1.0);
			}
		}
		return false;
	}

	public boolean isNewlyDiscovered(BState cur, BOperation op){
		for(BUncertainty b : this.getUns().values()){
			if(b.getSs().getName().equals(cur.getName()) && b.getOp().getName().equals(op.getName())){
				return (!b.isPreDef());
			}
		}
		return false;
	}

	public BOperation getOperationByOption(BState cur, TransitionOption option) throws DiscoverUncertaintyException{

		switch(option){
		case LessExecuted:

			return getOperationLessExecuted(cur);
		case HighUncertain:
			return getHighUncertainy(cur);
//		case NewlyDiscovered:
//			return getNewlyDiscovered(cur);
//		case ExcludedRand:
//			return getExcludedRandom(cur);
		case ExcludedLessOp:
			if(this.getExcludedOp(cur) != null && this.getExcludedOp(cur).size() > 0){

			}
			return getOperationLessExecuted(cur);


		default:
			throw new DiscoverUncertaintyException("out of option");

		}

	}

	public Set<BOperation> getExecuteIndSOperation(BState cur, BOperation bop, IntroduceIndSOption option){
		for(BUncertainty bun : this.uns.values()){
			if(bun.getTs().equals(cur) && bun.getOp().equals(bop)){
				Set<IndSp> inds = bun.getIndSps();
				return getExecutedIndSOps(inds, option);
			}
		}
		return null;
	}

	public OccurOfIndS getOccurrenceOfInds(Set<IndSp> inds){
		int specified = 0;
		int others = 0;
		for(IndSp ind : this.inds.values()){
			if(ind.getSpecification().evaluate()){
				if(inds.contains(ind)) specified ++;
				else others++;
			}
		}
		if(specified == 0 && others == 0){
			return OccurOfIndS.NoneInds;
		}else if(specified > 0 && others == 0){
			return OccurOfIndS.SpecifiedInds;
		}else if(specified == 0 && others > 0){
			return OccurOfIndS.OtherInds;
		}else
			return OccurOfIndS.Combined;
	}

	public IndSp getLessExecuted(Set<IndSp> inds){
		IndSp result = null;
		int times = -1;
		for(IndSp ind : inds){
			if(times == -1) {result = ind; times = ind.getvTimes();}
			else if(ind.getvTimes() < times) {
				result = ind; times = ind.getvTimes();
			}
		}
		return result;
	}

	public IndSp getLessExecuted(Set<IndSp> without, Collection<IndSp> inds){
		IndSp result = null;
		int times = -1;
		for(IndSp ind : inds){
			if(!without.contains(ind)){
				if(times == -1) {result = ind; times = ind.getvTimes();}
				else if(ind.getvTimes() < times) {
					result = ind; times = ind.getvTimes();
				}
			}
		}
		return result;
	}

	public String getOccurredIndSpsStr(){
		String inds = "";
		for(IndSp ind : this.inds.values()){
			if(ind.getSpecification().evaluate()){
				if(!inds.equals("")) inds = inds +"@";
				inds = inds + ind.getName();
			}
		}
		return inds;
	}

	public Set<BOperation> getExecutedIndSOps(Set<IndSp> inds, IntroduceIndSOption option){
		Set<BOperation> result = new HashSet<BOperation>();
		Set<IndSp> specified = new HashSet<IndSp>();
		Set<IndSp> others = new HashSet<IndSp>();
		for(IndSp ind : this.inds.values()){
			if(ind.getSpecification().evaluate()){
				if(inds.contains(ind)) specified.add(ind);
				else others.add(ind);
			}
		}
		if(specified.size() == 0 && others.size() == 0){
			switch(option){
			case MakeSpecifiedOccurred:
				result.addAll(getLessExecuted(inds).getTriggers());
				return result;
			case MakeNonSpecifiedOccurred:
				result.addAll(getLessExecuted(inds, this.inds.values()).getTriggers());
				return result;
			case MakeCombined:
				result.addAll(getLessExecuted(inds).getTriggers());
				result.addAll(getLessExecuted(inds, this.inds.values()).getTriggers());
				return result;
			case MakeNone:
				return result;
			default:
				break;
			}
		}else if(specified.size() > 0 && others.size() == 0){
			switch(option){
			case MakeSpecifiedOccurred:
				return result;
			case MakeNonSpecifiedOccurred:
				for(IndSp sp : specified){
					result.addAll(sp.getReleasers());
				}
				result.addAll(getLessExecuted(inds, this.inds.values()).getTriggers());
				return result;
			case MakeCombined:
				result.addAll(getLessExecuted(inds, this.inds.values()).getTriggers());
				return result;
			case MakeNone:
				for(IndSp sp : specified){
					result.addAll(sp.getReleasers());
				}
				return result;
			default:
				break;
			}
		}else if(specified.size() == 0 && others.size() > 0){
			switch(option){
			case MakeSpecifiedOccurred:
				for(IndSp sp : others){
					result.addAll(sp.getReleasers());
				}
				result.addAll(getLessExecuted(inds).getTriggers());
				return result;

			case MakeNonSpecifiedOccurred:
				return result;
			case MakeCombined:
				result.addAll(getLessExecuted(inds).getTriggers());
				return result;
			case MakeNone:
				for(IndSp sp : others){
					result.addAll(sp.getReleasers());
				}
				return result;
			default:
				break;
			}
		}else{
			switch(option){
			case MakeSpecifiedOccurred:
				for(IndSp sp : others){
					result.addAll(sp.getReleasers());
				}
				return result;
			case MakeNonSpecifiedOccurred:
				for(IndSp sp : specified){
					result.addAll(sp.getReleasers());
				}
				return result;
			case MakeCombined:
				return result;
			case MakeNone:
				for(IndSp sp : others){
					result.addAll(sp.getReleasers());
				}
				for(IndSp sp : specified){
					result.addAll(sp.getReleasers());
				}
				return result;
			default:
				break;
			}
		}
		return null;
	}

	//This action is used to select the next transition that is executed less than other known transitions.
	public BOperation getOperationLessExecuted(BState bs) throws DiscoverUncertaintyException{

		Set<String> uns = this.getPossibleNext(bs);
		if(uns != null && uns.size() > 0){
			return getOperationLessExecutedStr(uns);
		}
		this.getPossibleNext(bs);
		throw new DiscoverUncertaintyException("no operation is selected "+bs.getName());

	}

	public BOperation getHighUncertainy(BState bs) throws DiscoverUncertaintyException{
		Set<String> uns = this.getPossibleNext(bs);
		if(uns.size() == 0){
			System.err.println("state is "+bs.getName()+" "+bs.getReplaceState());
		}
		if(uns != null && uns.size() > 0){
			return getHighUncertainyStr(uns);
		}
		throw new DiscoverUncertaintyException("no operation is selected");
	}


	public BOperation getOperationLessExecuted(Set<BOperation> ops) throws DiscoverUncertaintyException{
		BOperation op = null;
		int vtimes = -1;
		for(BOperation bo : ops){
			if(vtimes == -1) {vtimes = bo.getvTimes(); op = bo;}
			else if(vtimes > bo.getvTimes()) op = bo;
		}
		return op;
	}

	public BOperation getOperationLessExecutedStr(Set<String> ops) throws DiscoverUncertaintyException{
		BOperation op = null;
		int vtimes = -1;
		for(String sbo : ops){
			BOperation bo = this.uns.get(sbo).getOp();
			if(vtimes == -1) {vtimes = bo.getvTimes(); op = bo;}
			else if(vtimes > bo.getvTimes()) op = bo;
		}
		return op;
	}

	public BOperation getHighUncertainyStr(Set<String> ops) throws DiscoverUncertaintyException{
		BOperation op = null;
		double degree = -1.0;
		for(String sbo : ops){
			BUncertainty un = this.uns.get(sbo);
			if(degree == -1.0) {degree = un.getDegree(); op = un.getOp();}
			else if(degree > un.getDegree()) op = un.getOp();
		}
		return op;
	}



	public BState getStateNow(BState cur, BOperation op) throws DiscoverUncertaintyException{

		Set<String> occurredStates = new HashSet<String>();
		Set<String> removeRed = new HashSet<String>();
		for(BState bst : this.getSts().values()){
			if(bst.getConstraint() != null && bst.getConstraint().evaluate()){

				if((!bst.isPreDef()) && bst.ndType.compareTo(NewlyDiscoverType.AndCombine) == 0){
					for(String os : occurredStates){
						if(bst.getSourceMaterial().contains(os)){
							removeRed.add(os);
						}
					}
				}
				occurredStates.add(bst.getName());

			}
		}
		occurredStates.removeAll(removeRed);
		BState specified = this.getSpecifiedTarget(cur, op);

		if(occurredStates.size() == 0){
			//create new state
			BState b = exploreUnknownState(cur, op, specified);
			if(b!=null && b.getConstraint() == null){
				System.err.println("unknown state has error");
			}
			if(b == null){
				b = createUnknownStates();
				b.setNdType(NewlyDiscoverType.UnknownTag);
			}
			return b;
		}else if(occurredStates.size() == 1){
			return (BState)this.sts.get(occurredStates.toArray()[0]);
		}
		else if(specified != null && containSpecifiedTarget(specified, occurredStates)){
			specified.getOccurWith().add(getOccurWithStates(op, specified, occurredStates));
			return specified;
		}

		else{
			BState tmp = currentAndStatesByName(occurredStates, specified);
			if(tmp.getConstraint() == null)
				System.err.println("newly created state does not includes constraint");
			tmp.setNdType(NewlyDiscoverType.AndCombine);
			return tmp;
		}
	}

	public boolean containSpecified(BState sp, Set<BState> sets){
		for(BState s : sets){
			if(s.getName().equals(sp.getName()))
				return true;
		}
		return false;
	}

	public boolean containSpecifiedTarget(BState sp, Set<String> sets){
		for(String s : sets){
			if(this.sts.get(s).getName().equals(sp.getName()))
				return true;
		}

		for(String s : sets){
			if(this.sts.get(s).getName().contains(sp.getName()))
				return true;
		}
		return false;
	}
	public String getOccurWithItem(BOperation op, BState specified, Set<BState> bstates){
		String sb = op.getName();
		for(BState bs : bstates){
			if(!bs.getName().equals(specified.getName())){
				sb = sb +"@"+bs.getName();
			}
		}
		return sb;
	}

	public String getOccurWithStates(BOperation op, BState specified, Set<String> bstates){
		String sb = op.getName();
		for(String bs : bstates){
			sb = sb +"@"+bs;
//			if(!bs.equals(specified.getName())){
//
//			}
		}
		return sb;
	}

	public BState exploreUnknownState(BState cur, BOperation op, BState specified) throws DiscoverUncertaintyException{
		Set<String> content = new HashSet<String>();
		List<String> soM = new ArrayList<String>();
		BState newS = null;
		Set<String> relatedPros = null;
		if(specified != null){
			relatedPros = specified.getConstraint().getPropertiesMaps().keySet();
		}

		// list the constraint fragment
		for(BState sbs : this.getSts().values()){
			if(sbs.getConstraint() != null){
				for(BConsFragment bf : sbs.getConstraint().getConsFragMaps().values()){
					if(relatedPros != null){
						if(bf.containProperties(relatedPros) && bf.evaluate()){
							soM.add(sbs.getName());
							content.add(bf.getName());
						}
					}else{
						if(bf.evaluate()){
							soM.add(sbs.getName());
							content.add(bf.getName());
						}
					}
				}
			}


		}
		if(content.size() == 0){
			for(BState sbs : this.getSts().values()){
				if(sbs.getConstraint() != null){
					for(BConsFragment bf : sbs.getConstraint().getConsFragMaps().values()){
						Set<String> ts = bf.evaluateWeakens();
						if(relatedPros != null){
							if(bf.containProperties(relatedPros) && ts != null && ts.size() > 0){
								soM.add(sbs.getName());
								content.addAll(ts);
							}
						}else{
							if(ts != null && ts.size() > 0){
								soM.add(sbs.getName());
								content.addAll(ts);
							}
						}
					}
				}

			}

			if(content.size() == 0){
				for(BState sbs : this.getSts().values()){
					if(sbs.getConstraint() != null){
						for(BConsFragment bf : sbs.getConstraint().getConsFragMaps().values()){
							Set<String> ts = bf.evaluateWeakens();
							if(ts != null && ts.size() > 0){
								soM.add(sbs.getName());
								content.addAll(ts);
							}
						}
					}

				}
				if(content.size() > 0){
					if(specified != null){
						newS = (BState) Utility.deepClone(specified);
						newS.setNdType(NewlyDiscoverType.UnknwonExploreKnT_NRPWK);
					}else{
						//newS = (BState) Utility.deepClone(this.sts.get(soM.get(0)));
						throw new DiscoverUncertaintyException("should be handled before!");
					}
					newS.getConstraint().setConstraint(currentAndConstraints(content));
					newS.setName(newS.getName() + "_newly_created_"+(this.sts.values().size()+1));
					newS.setSourceMaterial(soM);
				}
			}else{
				if(specified != null){
					newS = (BState) Utility.deepClone(specified);
					newS.setNdType(NewlyDiscoverType.UnknwonExploreKnT_RPWK);
				}
				else{
					newS = (BState) Utility.deepClone(this.sts.get(soM.get(0)));
					newS.setNdType(NewlyDiscoverType.UnknwonExploreUKnT_WK);
				}

				newS.getConstraint().setConstraint(currentAndConstraints(content));
				newS.setName(newS.getName() + "_newly_created_"+(this.sts.values().size()+1));

				newS.setSourceMaterial(soM);
			}
		}else{
			if(specified != null){
				newS = (BState) Utility.deepClone(specified);
				newS.setNdType(NewlyDiscoverType.UnknwonExploreKnT_FC);
			}
			else{
				newS = (BState) Utility.deepClone(this.sts.get(soM.get(0)));
				newS.setNdType(NewlyDiscoverType.UnknwonExploreUKnT_FC);
			}
			newS.getConstraint().setConstraint(currentAndConstraints(content));
			newS.setName(newS.getName() + "_newly_created_"+(this.sts.values().size()+1));
			newS.setSourceMaterial(soM);
		}


		if(newS != null) {
			if(specified != null)
				newS.setReplaceState(specified.getName());
			else
				newS.setReplaceState(null);
			newS.setvTimes(1);
			newS.setPreDef(false);
			this.sts.put(newS.getName(), newS);
		}
		return newS;

	}

	public BState createUnknownStates(){
		if(unKnownS == null){
			String cons = "true";
			String name = "UNK_STATE";
			String ins = "";

			this.unKnownS = new BState(name, cons, ins);
			this.unKnownS.setPreDef(false);
		}
		return this.unKnownS;

//		for(BState bs : this.getSts().values()){
//			if(ins.isEmpty()) ins = bs.getInstance_name();
//			if(!cons.isEmpty()) cons = cons+ " and ";
//			cons = cons +"(not"+bs.getConstraint()+")";
//		}

	}

	public String currentAndConstraints(Set<String> bst){
		String cons = "";
		for(String bs : bst){
			if(!cons.equals("")) cons = cons+ " and ";
			cons = cons +"("+bs+")";
		}
		return cons;
	}

	public BState currentAndStates(Set<BState> bst, BState specified){
		String cons = "";
		String name = "";
		String ins = "";
		List<String> nlist = new ArrayList<String>();
		for(BState bs : bst){
			if(ins.isEmpty()) ins = bs.getInstance_name();
			if(!cons.isEmpty()) cons = cons+ " and ";
			cons = cons +"("+bs.getConstraint().getConstraint()+")";
			nlist.add(bs.getName());
		}
		Collections.sort(nlist, String.CASE_INSENSITIVE_ORDER);
		for(String n : nlist){
			if(!name.isEmpty()) name = name +" combined ";
			name = name + n;
		}
		BState newAndS = null;
		if(specified != null){
			newAndS = (BState) Utility.deepClone(specified);
			newAndS.setReplaceState(specified.getName());
		}else{
			newAndS = (BState) Utility.deepClone(this.sts.get(nlist.get(0)));
			newAndS.setReplaceState(null);
		}
		if(newAndS.getConstraint() == null){
			newAndS.setConstraint(new BConstraint(cons, ins,ConstraintContainerType.StInv));
		}else{
			newAndS.getConstraint().setConstraint(cons);
		}

		newAndS.setName(name);
		newAndS.setPreDef(false);
		this.getSts().put(name, newAndS);
		newAndS.setSourceMaterial(nlist);

		return newAndS;
	}

	public BState currentAndStatesByName(Set<String> bst, BState specified){
		String cons = "";
		String name = "";
		String ins = "";
		List<String> nlist = new ArrayList<String>();
		for(String bsn : bst){
			BState bs = this.sts.get(bsn);
			if(ins.isEmpty()) ins = bs.getInstance_name();
			if(!cons.isEmpty()) cons = cons+ " and ";
			cons = cons +"("+bs.getConstraint().getConstraint()+")";
			nlist.add(bs.getName());
		}
		Collections.sort(nlist, String.CASE_INSENSITIVE_ORDER);
		for(String n : nlist){
			if(!name.isEmpty()) name = name +" combined ";
			name = name + n;
		}
		BState newAndS = null;
		if(specified != null){
			newAndS = (BState) Utility.deepClone(specified);
			newAndS.setReplaceState(specified.getName());
		}else{
			newAndS = (BState) Utility.deepClone(this.sts.get(nlist.get(0)));
			newAndS.setReplaceState(null);
		}
		if(newAndS.getConstraint() == null){
			newAndS.setConstraint(new BConstraint(cons, ins,ConstraintContainerType.StInv));
		}else{
			newAndS.getConstraint().setConstraint(cons);
		}

		if(newAndS.getConstraint() == null){
			System.err.println(cons);
		}
		newAndS.setName(name);
		newAndS.setPreDef(false);
		this.getSts().put(name, newAndS);
		newAndS.setSourceMaterial(nlist);

		return newAndS;
	}

	public Set<String> getPossibleNext(BState cur){
		Set<String> nexts = new HashSet<String>();
		Set<String> tmp = this.getPossiblePreDefNext(cur);
		if(tmp != null){
			nexts.addAll(tmp);
		}
		Set<String> managed = new HashSet<String>();
		Set<String> tmp2 = this.getPossibleNewNexts(cur, managed);
	//	System.out.println(managed.size());
		if(tmp2 != null){
			nexts.addAll(tmp);
		}
		return nexts;
	}

	public Set<String> getPossiblePreDefNext(BState bs){
		Set<String> nexts = null;
		for(BUncertainty b : this.getUns().values()){
			if((b.isPreDef()) && b.getSs().getName().equals(bs.getName())){
				if(nexts == null) nexts = new HashSet<String>();
				nexts.add(b.getName());
			}
		}

		for(String sm : bs.getSourceMaterial()){
			Set<String> tmp = getPossiblePreDefNext(this.sts.get(sm));
			if(tmp != null && tmp.size() > 0){
				if(nexts == null) nexts = new HashSet<String>();
				nexts.addAll(tmp);
			}
		}

		if(bs.getReplaceState() != null){
			if(!this.sts.get(bs.getReplaceState()).isPreDef()){
				Set<String> tmp = getPossiblePreDefNext(this.getPreDefState(this.sts.get(bs.getReplaceState())));
				if(tmp != null && tmp.size() > 0){
					if(nexts == null) nexts = new HashSet<String>();
					nexts.addAll(tmp);
				}
			}else{
				Set<String> tmp = getPossiblePreDefNext(this.sts.get(bs.getReplaceState()));
				if(tmp != null && tmp.size() > 0){
					if(nexts == null) nexts = new HashSet<String>();
					nexts.addAll(tmp);
				}
			}

		}
		return nexts;
	}

	public Set<String> getPossibleNewNexts(BState bs, Set<String> managed){
		Set<String> nexts = null;

		if(!managed.contains(bs)){
			managed.add(bs.getName());
			for(BUncertainty b : this.getUns().values()){
				if((!b.isPreDef()) && b.getSs().getName().equals(bs.getName())){
					if(nexts == null) nexts = new HashSet<String>();
					nexts.add(b.getName());
				}
			}

			if(!bs.isPreDef()){
				for(String sm : bs.getSourceMaterial()){
					if(!managed.contains(sm)){
						managed.add(sm);
						Set<String> tmp = getPossibleNewNexts(this.sts.get(sm), managed);
						if(tmp != null && tmp.size() > 0){
							if(nexts == null) nexts = new HashSet<String>();
							nexts.addAll(tmp);
						}

					}

				}
				if(!managed.contains(bs.getReplaceState())){
					managed.add(bs.getReplaceState());
					if(bs.getReplaceState() != null){
						if(!this.sts.get(bs.getReplaceState()).isPreDef()){
							Set<BState> reps = new HashSet<BState>();
							reps = this.getNewStatesByReplace(bs, reps);
							for(BState b : reps){
								if(!managed.contains(b)){
									managed.add(b.getName());
									Set<String> tmp = getPossibleNewNexts(b, managed);
									if(tmp != null && tmp.size() > 0){
										if(nexts == null) nexts = new HashSet<String>();
										nexts.addAll(tmp);
									}
								}

							}
						}else{
							Set<String> tmp = getPossibleNewNexts(this.sts.get(bs.getReplaceState()), managed);
							if(tmp != null && tmp.size() > 0){
								if(nexts == null) nexts = new HashSet<String>();
								nexts.addAll(tmp);
							}
						}

					}
				}

			}

		}
		return nexts;
	}

	public boolean existSame(List<String> list){
		for(int i = 0; i < list.size() - 1; i++){
			for(int j = i+1; j < list.size(); j++){
				if(list.get(i).equals(list.get(j))){
					return true;
				}
			}
		}
		return false;
	}

	public BOperation getNewlyDiscovered(BState cur) throws DiscoverUncertaintyException{
		//BOperation op = null;
		Set<BOperation> ops = new HashSet<BOperation>();
		for(BUncertainty un : this.getUns().values()){
			if(un.getSs().getName().equals(cur.getName()) && (!un.getOp().isPreDef())){
				ops.add(un.getOp());
			}
		}
		if(ops.size() == 0){
			return null;
		}else if(ops.size() == 1){
			return (BOperation)ops.toArray()[0];
		}else{
			return getOperationLessExecuted(ops);
		}
	}

	public BOperation getExcludedRandom(BState cur){
		int size = this.getExcludedOp(cur).size();
		return (BOperation)this.getExcludedOp(cur).toArray()[UncertainWorld.rand.nextInt(size)];
	}

	// public BOperation getExcludedLessExecuted(BState cur) throws
	// DiscoverUncertaintyException{
	//
	// }

	public double calculateCoverageOfOp(){
		int total = 0;
		int uncover = 0;
		for(BOperation op : this.ops.values()){
			if(op.isPreDef() && op.getvTimes() == 0) uncover++;
			if(op.isPreDef()) total++;
		}
		return 1.0 - (uncover * 1.0 )/total;
	}

	public double calculateCoverageOfSt(){
		int total = 0;
		int uncover = 0;
		for(BState st : this.sts.values()){
			if(st.isPreDef() && st.getvTimes() == 0) uncover++;
			if(st.isPreDef()) total++;
		}
		return 1.0 - (uncover * 1.0 )/total;
	}

	public double calculateCoverageOfTr(){
		int total = 0;
		int uncover = 0;
		for(BUncertainty un : this.uns.values()){
			if(un.isPreDef() && un.getvTimes() == 0) uncover++;
			if(un.isPreDef()) total++;
		}
		return 1.0 - (uncover * 1.0 )/total;
	}

	public void updateCoverage(){
		this.setCoverageOfInds(this.calculateCoverageOfInds());
		this.setCoverageOfOp(this.calculateCoverageOfOp());
		this.setCoverageOfSt(this.calculateCoverageOfSt());
		this.setCoverageOfUn(this.calculateCoverageOfTr());
	}

	public double calculateCoverageOfInds(){
		int total = 0;
		int uncover = 0;
		for(IndSp un : this.inds.values()){
			if(un.isPreDef() && un.getvTimes() == 0) uncover++;
			if(un.isPreDef()) total ++;
		}
		return 1.0 - (uncover * 1.0 )/total;
	}


	public static String getUnName(String ss, String op, String ts){
		return ss+UN_SEPATOR+op+UN_SEPATOR+ts;
	}

	public void processLinkStates(){
		List<BState> removeds= new ArrayList<BState>();
		for(BState st : this.sts.values()){
			boolean isolate = true;
			Checkun:
			for(BUncertainty un : uns.values()){
				if(un.getTs().equals(st) || un.getSs().equals(st)){
					isolate = false;
					break Checkun;
				}
			}
			if(isolate){
				removeds.add(st);
			}
		}
		//System.out.println("before"+this.getSts().size()+" "+removeds.size());
		for(BState key : removeds){
			this.getSts().remove(key.getName(),key);
		}
		//System.out.println("after"+this.getSts().size());

	}

	public void findInitalState(){
		int countNumOfinitial = 0;
		for(BState b : this.getSts().values()){
			if(b.isInital() && b.getCompState() == null){
				countNumOfinitial++;
				//System.out.println(b.getName());
				this.setInitalState(b.getName());
			}
			if(b.isComposite()){
				findInitalState(b.getName());
			}
		}
		//System.out.println("num of inital states of bModel is "+countNumOfinitial);
	}

	public void findInitalState(String compName){
		int countNumOfinitial = 0;
		for(BState b : this.getSts().values()){
			if(b.isInital() && b.getCompState()!= null && b.getCompState().getName().equals(compName)){
				countNumOfinitial++;
				//System.out.println(b.getName());
				this.getSts().get(compName).setEnterComp(b.getName());
			}
		}
		//System.out.println("num of inital states of "+compName +" is "+countNumOfinitial);
	}

	public String getCompletePath(){
		return expName+"_"+getTimePath()+"_"+generation;
	}

	public String getPath(){
		if(this.getExpName() != null && !this.getExpName().equals("") && this.getGeneration() != -1)
			return this.getCompletePath();
		return this.getTimePath();
	}

	public String getTimePath(){
		return BModelHandler.formatter.format(this.getStartTime());
	}
	public Map<String, BState> getSts() {
		return sts;
	}
	public void setSts(Map<String, BState> sts) {
		this.sts = sts;
	}
	public Map<String, BOperation> getOps() {
		return ops;
	}
	public void setOps(Map<String, BOperation> ops) {
		this.ops = ops;
	}
	public Map<String, BUncertainty> getUns() {
		return uns;
	}
	public void setUns(Map<String, BUncertainty> uns) {
		this.uns = uns;
	}
	public Map<String, IndSp> getInds() {
		return inds;
	}
	public void setInds(Map<String, IndSp> inds) {
		this.inds = inds;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public static String getUnSepator() {
		return UN_SEPATOR;
	}
	public String getExpName() {
		return expName;
	}
	public void setExpName(String expName) {
		this.expName = expName;
	}
	public int getGeneration() {
		return generation;
	}
	public void setGeneration(int generation) {
		this.generation = generation;
	}
	public double getFitness() {
		return fitness;
	}
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getSolution() {
		return solution;
	}
	public void setSolution(String solution) {
		this.solution = solution;
	}
	public int getMoves() {
		return moves;
	}
	public void setMoves(int moves) {
		this.moves = moves;
	}
	public double getMinCoverageOfSt() {
		return minCoverageOfSt;
	}
	public void setMinCoverageOfSt(double minCoverageOfSt) {
		this.minCoverageOfSt = minCoverageOfSt;
	}
	public double getCoverageOfSt() {
		return coverageOfSt;
	}
	public void setCoverageOfSt(double coverageOfSt) {
		this.coverageOfSt = coverageOfSt;
	}
	public double getMinCoverageOfOp() {
		return minCoverageOfOp;
	}
	public void setMinCoverageOfOp(double minCoverageOfOp) {
		this.minCoverageOfOp = minCoverageOfOp;
	}
	public double getCoverageOfOp() {
		return coverageOfOp;
	}
	public void setCoverageOfOp(double coverageOfOp) {
		this.coverageOfOp = coverageOfOp;
	}
	public double getMinCoverageOfUn() {
		return minCoverageOfUn;
	}
	public void setMinCoverageOfUn(double minCoverageOfUn) {
		this.minCoverageOfUn = minCoverageOfUn;
	}
	public double getCoverageOfUn() {
		return coverageOfUn;
	}
	public void setCoverageOfUn(double coverageOfUn) {
		this.coverageOfUn = coverageOfUn;
	}
	public int getNumOfnewSts() {
		return numOfnewSts;
	}
	public void setNumOfnewSts(int numOfnewSts) {
		this.numOfnewSts = numOfnewSts;
	}
	public int getNumOfnewUns() {
		return numOfnewUns;
	}
	public void setNumOfnewUns(int numOfnewUns) {
		this.numOfnewUns = numOfnewUns;
	}
	public List<LogItem> getLog() {
		if(this.log == null) log = new ArrayList<LogItem>();
		return log;
	}
	public void setLog(List<LogItem> log) {
		this.log = log;
	}

	public String getInitalState() {
		return initalState;
	}

	public void setInitalState(String initalState) {
		this.initalState = initalState;
	}



	public double getCoverageOfInds() {
		return coverageOfInds;
	}

	public void setCoverageOfInds(double coverageOfInds) {
		this.coverageOfInds = coverageOfInds;
	}

	public Map<String, BConsFragment> getPros() {
		if(pros == null) pros = new HashMap<String, BConsFragment>();
		return pros;
	}

	public void setPros(Map<String, BConsFragment> pros) {
		this.pros = pros;
	}
}
