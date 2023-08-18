package no.simula.se.uncertainty.evolution.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.FinalState;
import org.eclipse.uml2.uml.Pseudostate;
import org.eclipse.uml2.uml.PseudostateKind;
import org.eclipse.uml2.uml.State;

import no.simula.se.uncertainty.evolution.rules.ExploreUncertainWorldProblem;

public class BState extends BElement{

	private static final long serialVersionUID = -2062615792070748517L;

	private Set<String> excludedOps;
	private String replaceState;

	private List<String> sourceMaterial;
	private Set<String> possibleNextOps;

	private BConstraint constraint;
	private String instance_name;

	private boolean keep;

	private boolean isTerminate = false;
	private boolean isfinal = false;
	private boolean isInital = false;
	private boolean isChoice = false;

	private boolean isComposite = false;
	private BState compState;

	private String enterComp;

	//public static UncertainWorld world;

	private Set<String> occurWith;
	public NewlyDiscoverType ndType;

	public NewlyDiscoverType getNdType() {
		return ndType;
	}

	public void setNdType(NewlyDiscoverType ndType) {
		this.ndType = ndType;
	}

	public Set<String> getOccurWith() {
		if(occurWith == null) occurWith = new HashSet<String>();
		return occurWith;
	}

	public void setOccurWith(Set<String> occurWith) {
		this.occurWith = occurWith;
	}

	public BState(){}

	public BState(State state, String ins){
		setName(state.getQualifiedName());
		this.instance_name = ins;
		if(state.getOwnedRules().size() == 1){
			this.constraint = new BConstraint(state, ins);
		}else{
			this.constraint = null;
		}
	}

	public BState(FinalState state, String ins){
		setName(state.getQualifiedName());
		this.instance_name = ins;
		this.isfinal = true;
	}

	public BState(Pseudostate state, String ins){
		setName(state.getQualifiedName());
		this.instance_name = ins;
		if(state.getKind().equals(PseudostateKind.TERMINATE_LITERAL)){
			this.isTerminate = true;
		}else if(state.getKind().equals(PseudostateKind.INITIAL_LITERAL)){
			this.isInital = true;
		}else if(state.getKind().equals(PseudostateKind.CHOICE_LITERAL)){
			this.isChoice = true;
		}
	}

	public BState(String name, String cons, String ins){
		setName(name);
		this.constraint = new BConstraint(cons, ins, ConstraintContainerType.StInv);
		this.instance_name = ins;
	}

	public boolean isKeep() {
		return keep;
	}
	public void setKeep(boolean keep) {
		this.keep = keep;
	}



	public String getInstance_name() {
		return instance_name;
	}

	public void setInstance_name(String instance_name) {
		this.instance_name = instance_name;
	}

	public EObject getInstance() {
		return ExploreUncertainWorldProblem.world.getInstance(instance_name);
	}


	public BConstraint getConstraint() {
		return constraint;
	}

	public void setConstraint(BConstraint constraint) {
		this.constraint = constraint;
	}

	public boolean isTerminate() {
		return isTerminate;
	}

	public void setTerminate(boolean isTerminate) {
		this.isTerminate = isTerminate;
	}

	public boolean isIsfinal() {
		return isfinal;
	}

	public void setIsfinal(boolean isfinal) {
		this.isfinal = isfinal;
	}

	public boolean isInital() {
		return isInital;
	}

	public void setInital(boolean isInital) {
		this.isInital = isInital;
	}

	public boolean isComposite() {
		return isComposite;
	}

	public void setComposite(boolean isComposite) {
		this.isComposite = isComposite;
	}

	public BState getCompState() {
		return compState;
	}

	public void setCompState(BState compState) {
		this.compState = compState;
	}

	public boolean isChoice() {
		return isChoice;
	}

	public void setChoice(boolean isChoice) {
		this.isChoice = isChoice;
	}

	public String getEnterComp() {
		return enterComp;
	}

	public void setEnterComp(String enterComp) {
		this.enterComp = enterComp;
	}

	public Set<String> getExcludedOps() {
		return excludedOps;
	}

	public void setExcludedOps(Set<String> excludedOps) {
		this.excludedOps = excludedOps;
	}

	public Set<String> getPossibleNextOps() {
		if(possibleNextOps == null){ possibleNextOps = new HashSet<String>();}
		return possibleNextOps;
	}

	public void setPossibleNextOps(Set<String> possibleNextOps) {
		this.possibleNextOps = possibleNextOps;
	}

	public String getReplaceState() {
		return replaceState;
	}

	public void setReplaceState(String replaceState) {
		this.replaceState = replaceState;
	}
	public List<String> getSourceMaterial() {
		if(sourceMaterial == null) sourceMaterial = new ArrayList<String>();
		return sourceMaterial;
	}

	public void setSourceMaterial(List<String> combination) {
		this.sourceMaterial = combination;
	}

}
