package no.simula.se.uncertainty.evolution.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.Query;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.helper.OCLHelper;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import no.simula.se.uncertainty.evolution.domain.BState;
import oclastview.visitors.XMLforOCL;

public class ConstraintUtility {

	private static XMLOutputter outputter = new XMLOutputter();

	public static boolean evaluateOCL(EClassifier e2, String cons, EObject obj) {
		boolean result = false;
		OCLExpression<EClassifier> query = null;
		try {
			// create an OCL instance for Ecore
			OCL<?, EClassifier, ?, ?, ?, ?, ?, ?, ?, Constraint, EClass, EObject> ocl;
			ocl = OCL.newInstance(EcoreEnvironmentFactory.INSTANCE);
			// create an OCL helper object
			OCLHelper<EClassifier, ?, ?, Constraint> helper = ocl.createOCLHelper();
			// set the OCL context classifier
			helper.setContext(e2);
			query = helper.createQuery(cons);
			Query<EClassifier, EClass, EObject> eval = ocl.createQuery(query);
			result = eval.check(obj);
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean evaluateOCL(String cons, EObject obj) {
		boolean result = false;
		EClassifier e2 = obj.eClass();
		OCLExpression<EClassifier> query = null;
		try {
			// create an OCL instance for Ecore
			OCL<?, EClassifier, ?, ?, ?, ?, ?, ?, ?, Constraint, EClass, EObject> ocl;
			ocl = OCL.newInstance(EcoreEnvironmentFactory.INSTANCE);
			// create an OCL helper object
			OCLHelper<EClassifier, ?, ?, Constraint> helper = ocl.createOCLHelper();
			// set the OCL context classifier
			helper.setContext(e2);
			query = helper.createQuery(cons);

			Query<EClassifier, EClass, EObject> eval = ocl.createQuery(query);
			result = eval.check(obj);

		} catch (ParserException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean validateOCL(String cons, EObject obj){
		boolean result = false;
		EClassifier e2 = obj.eClass();
		@SuppressWarnings("unused")
		OCLExpression<EClassifier> query = null;
		try {
			// create an OCL instance for Ecore
			OCL<?, EClassifier, ?, ?, ?, ?, ?, ?, ?, Constraint, EClass, EObject> ocl;
			ocl = OCL.newInstance(EcoreEnvironmentFactory.INSTANCE);
			// create an OCL helper object
			OCLHelper<EClassifier, ?, ?, Constraint> helper = ocl.createOCLHelper();
			// set the OCL context classifier
			helper.setContext(e2);
			query = helper.createQuery(cons);

			result = true;
		} catch (ParserException e) {
			result = false;
		}
		return result;
	}

	public static List<String> parseConstraint(String constraint) {
		List<String> result = null;

		return result;
	}

	// default AST
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Element getASTofOCL(String expression, EClass context, String key) {
		org.eclipse.ocl.OCL ocl = null;
		org.eclipse.ocl.helper.OCLHelper helper = null;

		Element res = new Element(key);
		res.setAttribute("textualInput", expression);

		if (context instanceof org.eclipse.uml2.uml.NamedElement) {
			ocl = org.eclipse.ocl.uml.OCL.newInstance();
		} else {
			ocl = org.eclipse.ocl.ecore.OCL.newInstance();
		}

		helper = ocl.createOCLHelper();

		// set our helper's context object to parse against it
		if ((context instanceof org.eclipse.emf.ecore.EClass) || (context instanceof org.eclipse.uml2.uml.Classifier)) {
			helper.setContext(context);
		} else if (context instanceof org.eclipse.emf.ecore.EOperation) {
			EOperation eOp = (EOperation) context;
			helper.setOperationContext(eOp.getEContainingClass(), eOp);
		} else if (context instanceof org.eclipse.uml2.uml.Operation) {
			org.eclipse.uml2.uml.Operation op = (org.eclipse.uml2.uml.Operation) context;
			helper.setOperationContext(op.getOwner(), op);
		} else if (context instanceof org.eclipse.emf.ecore.EStructuralFeature) {
			EStructuralFeature sf = (EStructuralFeature) context;
			helper.setAttributeContext(sf.getEContainingClass(), sf);
		} else if (context instanceof org.eclipse.uml2.uml.Property) {
			org.eclipse.uml2.uml.Property p = (org.eclipse.uml2.uml.Property) context;
			helper.setAttributeContext(p.getOwner(), p);
		}

		OCLExpression<EClassifier> oclExp = null;
		Element xmlAST = null;
		try {
			oclExp = helper.createQuery(expression);
		} catch (Exception e) {
			// xmlAST = reportException(e);
			res.addContent(xmlAST);
			return res;
		}

		XMLforOCL xfo = null;
		if (context instanceof org.eclipse.uml2.uml.NamedElement) {
			xfo = XMLforOCL.getUML2Version();
		} else {
			xfo = XMLforOCL.getEcoreVersion();
		}
		try {
			xmlAST = (Element) oclExp.accept(xfo);
		} catch (Exception e) {
			// xmlAST = reportException(e);
		}

		res.addContent(xmlAST);
		return res;
	}

	public static String elmentToString(Element ele){

		return outputter.outputString(ele);
	}



	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Element getAST(String expression, EClass context, String key) {
		org.eclipse.ocl.OCL ocl = null;
		org.eclipse.ocl.helper.OCLHelper helper = null;

		Element res = new Element(key);
		res.setAttribute("textualInput", expression);

		if (context instanceof org.eclipse.uml2.uml.NamedElement) {
			ocl = org.eclipse.ocl.uml.OCL.newInstance();
		} else {
			ocl = org.eclipse.ocl.ecore.OCL.newInstance();
		}

		helper = ocl.createOCLHelper();

		// set our helper's context object to parse against it
		if ((context instanceof org.eclipse.emf.ecore.EClass) || (context instanceof org.eclipse.uml2.uml.Classifier)) {
			helper.setContext(context);
		} else if (context instanceof org.eclipse.emf.ecore.EOperation) {
			EOperation eOp = (EOperation) context;
			helper.setOperationContext(eOp.getEContainingClass(), eOp);
		} else if (context instanceof org.eclipse.uml2.uml.Operation) {
			org.eclipse.uml2.uml.Operation op = (org.eclipse.uml2.uml.Operation) context;
			helper.setOperationContext(op.getOwner(), op);
		} else if (context instanceof org.eclipse.emf.ecore.EStructuralFeature) {
			EStructuralFeature sf = (EStructuralFeature) context;
			helper.setAttributeContext(sf.getEContainingClass(), sf);
		} else if (context instanceof org.eclipse.uml2.uml.Property) {
			org.eclipse.uml2.uml.Property p = (org.eclipse.uml2.uml.Property) context;
			helper.setAttributeContext(p.getOwner(), p);
		}

		OCLExpression<EClassifier> oclExp = null;
		Element xmlAST = null;
		try {
			oclExp = helper.createQuery(expression);
		} catch (Exception e) {
			// xmlAST = reportException(e);
			res.addContent(xmlAST);
			return res;
		}

		XMLforOCLUtil xfo = null;
		if (context instanceof org.eclipse.uml2.uml.NamedElement) {
			xfo = XMLforOCLUtil.getUML2Version();
		} else {
			xfo = XMLforOCLUtil.getEcoreVersion();
		}
		try {
			xfo.setConstraint(expression);
			xmlAST = (Element) oclExp.accept(xfo);
		} catch (Exception e) {
			// xmlAST = reportException(e);
		}

		res.addContent(xmlAST);

//		XMLOutputter outputter = new XMLOutputter();
//		String xmlString = outputter.outputString(res);
//		System.out.println("display AST:\n" + xmlString);
		// System.out.println("ast:"+res.);
		return res;

	}
	@SuppressWarnings("unchecked")
	public static void getLeafBooleanCallOpExp(Element root, List<Element> ops){

		List<Element> eles = root.getChildren();
		for(Element ele : eles){
			if(ele.getName().equals("OperationCallExp")){
				if(ele.getChildren("OperationCallExp").size() > 0 && ele.getChild("OperationCallExp").getAttribute("resultType").equals("Boolean")){
					getLeafBooleanCallOpExp(ele, ops);
				}if(ele.getChildren("OperationCallExp").size() > 0 && !ele.getChild("OperationCallExp").getAttribute("resultType").equals("Boolean")){
					ops.add(ele);
				}else{
					Element source = ele.getChild("source");
					if(source.getChildren("OperationCallExp").size() > 0 && source.getChild("OperationCallExp").getAttribute("resultType").equals("Boolean")){
						getLeafBooleanCallOpExp(ele, ops);
					}else if(source.getChildren("OperationCallExp").size() > 0 && source.getChild("OperationCallExp").getAttribute("resultType").equals("Boolean")){
						//getLeafCallOpExp(ele, ops);
						ops.add(source.getChild("OperationCallExp"));
					}else{
						ops.add(ele);
					}
				}
			}else{
				getLeafBooleanCallOpExp(ele, ops);
			}
		}
	}

	public static Element getRoot(Element ele){
		if(ele.getParent() == null){
			return ele;
		}else{
			return getRoot(ele.getParent());
		}
	}

	public static String getConsFragByVar(Element var, EObject eobj){
			if(var.getName().equals("OperationCallExp") && var.getAttribute("resultType")!=null && (var.getAttribute("resultType").getValue().equals("Boolean") || var.getAttribute("resultType").getValue().equals("NONE"))){
			if(ConstraintUtility.validateOCL(var.getAttribute("experssion").getValue(), eobj)){
				return var.getAttribute("experssion").getValue();
			}else{
				return getConsFragByVar(var.getParent(), eobj);
			}
			//return var.getAttribute("experssion").getValue();
		}else if(var.getName().equals("IteratorExp") && var.getAttribute("type")!=null && (var.getAttribute("type").getValue().equals("Boolean"))){

			if(ConstraintUtility.validateOCL(var.getAttribute("experssion").getValue(), eobj)){
				return var.getAttribute("experssion").getValue();
			}else{
				return getConsFragByVar(var.getParent(), eobj);
			}
			//return var.getAttribute("experssion").getValue();
		}else{
			// can not be decomposite
			if(var.getParent() == null && var.getName().equals("StInv")){
				return var.getAttribute("textualInput").getValue();
			}else{
				return getConsFragByVar(var.getParent(), eobj);
			}

		}
	}

	@SuppressWarnings("unchecked")
	public static String getCallOpExpToString(Element callOp){

		int numOfArg = 0;

		String args = callOp.getAttribute("has").getValue();

		if(args.equals("zero args")){
			numOfArg = 0;
		}else if(args.equals("one arg")){
			numOfArg = 1;
		}else{
			System.err.println("more than one args");
		}

		String source = "";
		String reset = "";

		for(Element ele : (List<Element>)callOp.getChildren()){
			if(ele.getName().equals("source")){
				Element souChild = ((List<Element>)callOp.getChildren()).get(0);
				if(souChild.getName().equals("OperationCallExp")){
					source = getCallOpExpToString(souChild);
				}else if(souChild.getName().equals("IteratorExp")){

				}else {

				}
			}else{

			}
		}

		if(numOfArg == 1){
			return source + callOp.getAttribute("name").getValue()+ reset;
		}else{
			return source;
		}
	}

	public static String getIteratorExp(Element itExp){
		String source = "";
		String iterators = "";
		String body = "";

		return source+"->"+itExp.getAttribute("name").getValue()+"("+body+")";
	}

//	public static String getConstraintByProperty(String constraint, String property){
//
//	}

	public static String getIntegerLiteralExp(Element inExp){
		return inExp.getAttribute("symbol").getValue();
	}

	public static String getRealLiteralExp(Element inExp){
		return inExp.getAttribute("symbol").getValue();
	}

	public static String getBooleanLiteralExp(Element inExp){
		return inExp.getAttribute("symbol").getValue();
	}
	public static String getEnumLiteralExp(Element inExp){
		return inExp.getAttribute("literal").getValue();
	}


	public static String getXMLStr(Element elem){
		return outputter.outputString(elem);
	}

	@SuppressWarnings("unchecked")
	public static void getVariableExp(Element root, List<Element> vars){
		List<Element> eles = root.getChildren();
		for(Element ele : eles){
			if(ele.getName().equals("VariableExp") && ele.getAttribute("name").getValue().equals("self")){
				vars.add(ele);
			}else{
				getVariableExp(ele, vars);
			}
		}
	}

	public static void printLeafPropertyExps(List<Element> vars){
		for(Element var : vars){
			String propertExp = var.getAttribute("name").getValue();
			propertExp = getPropertyExp(var, propertExp);
			System.out.println("property expression:"+propertExp);
		}
	}

//	public static void generatefPropertyExps(BConstraint bcons){
//		List<Element> vars = new ArrayList<Element>();
//		ConstraintUtility.getVariableExp(bcons.getAST(), vars);
//		for(Element var : vars){
//			String propertExp = var.getAttribute("name").getValue();
//			propertExp = getPropertyExp(var, propertExp);
//			//System.out.println("property expression:"+propertExp);
//			bcons.getProperties().add(propertExp);
//		}
//	}
//
	public static Set<String> getLeafPropertyExps(Element root){
		Set<String> set = new HashSet<String>();
		List<Element> vars = new ArrayList<Element>();
		ConstraintUtility.getVariableExp(root, vars);
		for(Element var : vars){
			String propertExp = var.getAttribute("name").getValue();
			propertExp = getPropertyExp(var, propertExp);
			set.add(propertExp);
		}
		return set;
	}

	public static String getPropertyExp(Element pro, String current){
		//System.out.println("current "+current+" "+(new XMLOutputter()).outputString(pro.getParent().getParent()));
		if(pro.getParent().getName().equals("PropertyCallExp")){
			current = current +"."+ pro.getParent().getAttribute("name").getValue();
			current = getPropertyExp(pro.getParent(), current);
		}else if(pro.getParent().getName().equals("source")){
			current = getPropertyExp(pro.getParent(), current);
		}

		return current;
	}


	public static String degradeConstraint(String constraint) {

		// boolean: or, and, xor, not, =, <>, implies (A implies B = ((not A) or
		// B))
		// integer, real: =, <>, <, >,<=, >=, +, -, *,/, mod, div, abs, max,
		// min, round, floor
		// string: =, <>, concat, size, toLower, toUpper, substring,

		String result = null;

		// degrade constraint

		// and -> or

		// forAll -> select -> one

		// equal -> less than, more than, or not equal

		// more than int/real -> more than int/real -1/0.1

		// less than int/real -> less than int/real +1/0.1

		// includesAll, includes -> %

		// excluedsAll, excludes -> %

		// cannot degrade, isEmpty(), <>, not

		return result;
	}

//	public static String weakenConstraint(Element ast){
//
//	}
//
//	public static String weakenAnd(Element ast){
//
//	}
//
//	public static String weakenForAll(Element ast){
//
//	}
//
//	public static String weakenEqual(Element ast){
//
//	}
//
//	public static String weakenMoreThan(){
//
//	}
//
//	public static String weakenLessThan(){
//
//	}
//
//	public static String weakenIncludesAll(){
//
//	}
//
//	public static String weakenExcludesAll(){
//
//	}



	public static String oppositeConstraint(String constraint) {
		String result = null;

		return result;
	}

	public static BState createState(BState specified) {
		BState newBState = null;

		// keep the same the classifier and instance as specified one

		return newBState;
	}
}
