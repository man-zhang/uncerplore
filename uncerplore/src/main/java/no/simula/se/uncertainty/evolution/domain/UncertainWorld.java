package no.simula.se.uncertainty.evolution.domain;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.InstanceSpecification;

import no.simula.se.uncertainty.evolution.cs.SutController;
import no.simula.se.uncertainty.evolution.util.DiscoverUncertaintyException;
import no.simula.se.uncertainty.evolution.util.ModelUtility;
import no.simula.se.uncertainty.evolution.util.Utility;

public abstract class UncertainWorld{

	private String[] monPars;
	private EObject[] monEObjs;

	public static Random rand = new Random();

	public boolean terminate = false;

	private BModel origin;
	private BModel current;

	private BState currentState;

	private int trOption=0;
	private int indOption=0;

	public int maxSteps;

	public SutController cs;

	public UncertainWorld() throws ExecutionException, Exception{
		setMonPars(this.setupParameters());
		setMonEObjs(this.setupEObjects());
	}

	public void transit() throws DiscoverUncertaintyException{
		transit(TransitionOption.values()[this.trOption], null);
	}

	public boolean oneNKN(){
		return this.current.getPossibleNext(currentState).size() == 1;
	}

	public boolean hasUncertainty(){
		return this.current.exisitUncertainty(currentState);
	}

	public boolean hasExecludedOps(){
		return this.current.hasExcludedOps(currentState);
	}

	public int getPossibleNext(){
		return this.current.possibleNext(currentState);
	}

	public boolean isCurrentStateNew(){
		return !this.currentState.isPreDef();
	}

	public void randTransit() throws DiscoverUncertaintyException{
		if(rand.nextBoolean() ){
			transit(TransitionOption.LessExecuted, null);
		}else{
			transit(TransitionOption.HighUncertain, null);
		}
	}

	public void transit(TransitionOption _trOption, IntroduceIndSOption _indsOption) throws DiscoverUncertaintyException{
		String key = this.currentState.getName();
		BOperation op = this.current.getOperationByOption(this.currentState, _trOption);
		if(op!=null){
			key = key +"@"+op.getName();
			//FIXME indeterminacy source
			if(_indsOption != null){
				Set<BOperation> exInds = this.current.getExecuteIndSOperation(this.currentState, op, _indsOption);
				if(exInds != null){
					for(BOperation exInd : exInds){
						exInd.execute();
					}
				}

			}
			String opName_output = op.execute();
			BState target = null;
			if(op instanceof BConditionOperation){
				Set<BState> conds = this.current.getPossibleCondNextS(this.currentState, op);
				if(conds.size() == 1){
					this.currentState = (BState)conds.toArray()[0];

					target = this.current.getStateNow(this.currentState, this.current.getOps().get(opName_output.contains("_ouput[")? opName_output.split("_output[")[0]:opName_output));
				}else if(conds.size() == 0){
					//create new choice output

				}else{
					throw new DiscoverUncertaintyException("the size of choice point is "+this.currentState.getName());
				}
			}else{
				target = this.current.getStateNow(this.currentState, op);
			}
			target.count1Visit();
			if(target.isChoice()){
				System.err.println(this.currentState.getName()+"\n"+target.getName());
			}
			key = key +"@"+target.getName();

			BUncertainty bu = this.current.getUns().get(key);

			if( bu == null){
				bu = new BUncertainty(this.currentState, op, target);
				bu.setPreDef(false);
				bu.setDegree(0.0);
				this.current.getUns().put(key, bu);
				bu.setName(key);
			}
			bu.count1Visit();
			step();
			this.current.updateCoverage();
			this.current.getLog().add(new LogItem(new Date(), this.currentState.getName(), opName_output//op.getName()
					, target.getName(), this.current.getOccurredIndSpsStr()));
			this.currentState = target;
			// check if it is the composite state

			if(currentState.isComposite()){
				BState tmp = this.current.getSts().get(currentState.getEnterComp());
				if(tmp.getConstraint().evaluate()) {
					this.currentState = tmp;
					tmp.count1Visit();
				}

			}
			// check if it is the final state inside composite state
			if(currentState.isIsfinal() && currentState.getCompState() != null){
				BState tmp = this.currentState.getCompState();
				if(tmp.getConstraint().evaluate()) {
					this.currentState = tmp;
					tmp.count1Visit();
				}
			}
			if((currentState.isIsfinal() && currentState.getCompState() == null) || currentState.isTerminate()){
				this.resetSUT();
			}
		}else{
			this.setTerminate(true);
			throw new DiscoverUncertaintyException("out of branch option");

		}

	}

	public void resetSUT(){
		cs.resetSUT();
		this.currentState = this.current.getModelInital();
	}

	public void display(){
		System.out.println("=======\ndisplay: coverage "+this.getCurrent().getCoverageOfSt()+" "+
				this.getCurrent().getCoverageOfUn()+" \ndiscover "+this.getCurrent().getDiscoverdSt()+" and "+this.getCurrent().getDiscoverdTr());
	}

	public double getFitness(){
		double coverOfSt = 1.0 - this.getCurrent().getCoverageOfSt();
		double coverOfUn = 1.0 - this.getCurrent().getCoverageOfUn();
		double discoverF = 1.0 - this.current.getDiscoverdRate();

		return weight(coverOfSt, coverOfUn, discoverF);
	}

	public double weight(double a, double b, double c){
		return (a * 0.2 + c * 0.8);
	}

	public boolean isCoverageOfStBetween(double min, double max){
		return this.current.getCoverageOfSt() >= min && this.current.getCoverageOfSt() < max;
	}
	public boolean isCoverageOfOpBetween(double min, double max){
		return this.current.getCoverageOfOp() >= min && this.current.getCoverageOfOp() < max;
	}
	public boolean isCoverageOfTrBetween(double min, double max){
		return this.current.getCoverageOfUn() >= min && this.current.getCoverageOfUn() < max;
	}

	public boolean isCoverageOfIndSpBetween(double min, double max){
		return this.current.getCoverageOfInds() >= min && this.current.getCoverageOfInds() < max;
	}

	public void nextTrOption(){
		this.trOption = (this.trOption + 1) % TransitionOption.values().length;
		step();
	}

	public void nextIndOption(){
		this.indOption = (this.indOption + 1) % IndetermiancySourceOption.values().length;
	}

	public void reset(){
		this.current = (BModel)Utility.deepClone(this.origin);
		this.current.setStartTime(new Date());
		this.currentState = this.current.getModelInital();
		this.terminate = false;
		try {
			setup();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public BModel getCurrentModel(){
		return current;
	}
	public void loadBModel(String path){
		this.origin = BModelHandler.loadBModel(path);
	}

	public void setup() throws ExecutionException, Exception {}
	public String[] getMonPars() {
		return monPars;
	}

	public void setMonPars(String[] monPars) {
		this.monPars = monPars;
	}

	public EObject[] getMonEObjs() {
		return monEObjs;
	}

	public void setMonEObjs(EObject[] monEObjs) {
		this.monEObjs = monEObjs;
	}
	public EObject getInstance(Map<String, InstanceSpecification> objs, EObject e){
		return getInstance(ModelUtility.getVar(objs, e));
	}

	public EObject getInstance(String str_instance){
		int index = getIndexOfPars(str_instance);
		if(index != -1) return this.getMonEObjs()[index];
		return null;
	}

	public int getIndexOfPars(String str_instance){
		int i = 0;

		for(String par : this.getMonPars()){
			if(par.equals(str_instance)) return i;
			i++;
		}

		return -1;
	}

	public boolean isTerminate() {
		return terminate;
	}

	public void setTerminate(boolean terminate) {
		this.terminate = terminate;
	}

	public BModel getOrigin() {
		return origin;
	}

	public void setOrigin(BModel origin) {
		this.origin = origin;
	}

	public BModel getCurrent() {
		return current;
	}

	public void setCurrent(BModel current) {
		this.current = current;
	}

	public BState getCurrentState() {
		return currentState;
	}

	public void setCurrentState(BState currentState) {
		this.currentState = currentState;
	}

	public int getRemainingSteps(){
		return this.maxSteps - this.current.getMoves();
	}

	public double getRemaingCoverageOfSt(){
		return this.current.getMinCoverageOfSt() - this.current.getCoverageOfSt();
	}

	public double getRemaingCoverageOfTr(){
		return this.current.getMinCoverageOfUn() - this.current.getCoverageOfUn();
	}

	public int getTrOption() {
		return trOption;
	}

	public void setTrOption(int trOption) {
		this.trOption = trOption;
	}

	public int getIndOption() {
		return indOption;
	}

	public void setIndOption(int indOption) {
		this.indOption = indOption;
	}

	public int getMaxSteps() {
		return maxSteps;
	}

	public void setMaxSteps(int maxSteps) {
		this.maxSteps = maxSteps;
	}

	public void step(){
		this.getCurrent().setMoves(this.getCurrent().getMoves()+1);
		System.out.println("************"+this.getCurrent().getMoves()+"****************");
	}


	public abstract String[] setupParameters();

	public abstract EObject[] setupEObjects();
}
