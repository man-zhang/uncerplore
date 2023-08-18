package no.simula.se.uncertainty.evolution.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.ChangeEvent;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.OpaqueExpression;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.Transition;
import org.jdom.Element;

import no.simula.se.testing.utility.ModelUtil;
import no.simula.se.uncertainty.evolution.rules.ExploreUncertainWorldProblem;
import no.simula.se.uncertainty.evolution.util.ConstraintUtility;

public class BConstraint extends BElement{

	private static final long serialVersionUID = -5379369183621186118L;
	private String constraint;
	private String instance_name;
	private ConstraintContainerType type;
	//private Element AST;

	private Map<String, BProperty> propertiesMaps;

	//private Map<String, Set<String>> propertiesMaps;//property -> constraints
	private Map<String, BConsFragment> consFragMaps;

	public BConstraint(){

	}

	public BConstraint(String constraint, String ins, ConstraintContainerType type){
		this.constraint = constraint;
		this.instance_name = ins;
		this.type = type;
	}

	public BConstraint(State state, String ins){
		this(ModelUtil.getBodyConstraint(state.getOwnedRules().get(0)), ins, ConstraintContainerType.StInv);
	}

	public BConstraint(Transition trans, String ins){
		this(ModelUtil.getBodyConstraint(trans.getGuard()), ins, ConstraintContainerType.Guard);
	}

	public BConstraint(ChangeEvent event, String ins){
		this(ModelUtil.getBodyOpaqueExpression((OpaqueExpression) event.getChangeExpression()), ins, ConstraintContainerType.ChgExp);
	}

	public BConstraint(Constraint indspec, String ins){
		this(ModelUtil.getBodyConstraint(indspec), ins, ConstraintContainerType.IndSpec);
	}

	public boolean evaluate(){
		return ConstraintUtility.evaluateOCL(constraint, getInstance());
	}

	@SuppressWarnings({ "null", "unused" })
	public Set<String> evaluateFragement(){
		Set<String> trueConstraints = null;
		for(BProperty bp : this.propertiesMaps.values()){
			for(String eval : bp.getConsfragements()){
				if((!trueConstraints.contains(eval)) && ConstraintUtility.evaluateOCL(eval, getInstance())){
					if(trueConstraints == null) trueConstraints = new HashSet<String>();
					trueConstraints.add(eval);
				}
			}
		}

		return trueConstraints;
	}

	public String getConstraint() {
		return constraint;
	}
	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

	public Element getAST() {
		return generateAST();
		//return AST;
	}

	//default one
	public Element generateASTOfOCL() {
		return ConstraintUtility.getASTofOCL(constraint, getInstance().eClass(), type.toString());
	}

	//custom with expression
	public Element generateAST() {
		return ConstraintUtility.getAST(constraint, getInstance().eClass(), type.toString());
	}

	public void generateProperties(){
		//System.out.println("constraint "+this.getConstraint()+"\n"+ConstraintUtility.getXMLStr(this.getAST()));
		if(this.getPropertiesMaps() != null)
			this.getPropertiesMaps().clear();
		List<Element> vars = new ArrayList<Element>();
		ConstraintUtility.getVariableExp(this.getAST(), vars);
		for(Element var : vars){
			String propertExp = var.getAttribute("name").getValue();
			propertExp = ConstraintUtility.getPropertyExp(var, propertExp);
			if(this.getPropertiesMaps().get(propertExp) == null){
				this.getPropertiesMaps().put(propertExp, new BProperty(propertExp));
			}
			String cons = ConstraintUtility.getConsFragByVar(var, this.getInstance());
			//System.out.println(propertExp+" "+cons);
			this.getPropertiesMaps().get(propertExp).getConsfragements().add(cons);
		}
	}

	public void generateConsFragement(){
		if(this.consFragMaps != null){
			this.consFragMaps.clear();
		}

		for(Entry<String, BProperty> proKey : this.propertiesMaps.entrySet()){
			for(String conf :proKey.getValue().getConsfragements()){
				if(this.getConsFragMaps().get(conf) == null){
					BConsFragment bf = new BConsFragment(conf);
					bf.setInstance_name(this.getInstance_name());
					this.consFragMaps.put(conf, bf);
				}
				this.consFragMaps.get(conf).getProperties().add(proKey.getKey());
			}
		}
	}

	public ConstraintContainerType getType() {
		return type;
	}

	public void setType(ConstraintContainerType type) {
		this.type = type;
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

	public Map<String, BProperty> getPropertiesMaps() {
		if(propertiesMaps == null)
			propertiesMaps = new HashMap<String, BProperty>();
		return propertiesMaps;
	}

	public void setPropertiesMaps(Map<String, BProperty> propertiesMaps) {
		this.propertiesMaps = propertiesMaps;
	}

	public Map<String, BConsFragment> getConsFragMaps() {
		if(consFragMaps == null) consFragMaps = new HashMap<String, BConsFragment>();
		return consFragMaps;
	}

	public void setConsFragMaps(Map<String, BConsFragment> consFragMaps) {
		this.consFragMaps = consFragMaps;
	}
}
