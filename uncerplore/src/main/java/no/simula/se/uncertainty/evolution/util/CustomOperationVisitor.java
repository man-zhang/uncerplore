package no.simula.se.uncertainty.evolution.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.SendSignalAction;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.expressions.OperationCallExp;
import org.eclipse.ocl.utilities.AbstractVisitor;

public class CustomOperationVisitor extends
		AbstractVisitor<Set<OperationCallExp<EClassifier, EOperation>>, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint> {

	public CustomOperationVisitor() {
		super(new HashSet<OperationCallExp<EClassifier, EOperation>>());
	}

	@Override
	public Set<OperationCallExp<EClassifier, EOperation>> visitOperationCallExp(
			OperationCallExp<EClassifier, EOperation> operation) {
		
		String opName = operation.getReferredOperation().getName();
		
		if(opName.equals("size")){}

		if (operation.getReferredOperation().getName().equals("size"))
			result.add(operation);
		if (operation.getReferredOperation().getName().equals("=")){
			for(@SuppressWarnings("rawtypes") OCLExpression exp : operation.getArgument()){
				System.out.println("argu:"+exp);
			}
			result.add(operation);
		}
		
		if (operation.getReferredOperation().getName().equals("and")){
			result.add(operation);
		}
		
		System.out.println(operation.getReferredOperation().getName());

		return super.visitOperationCallExp(operation);

	}

	@Override
	public Set<OperationCallExp<EClassifier, EOperation>> handleOperationCallExp(
			OperationCallExp<EClassifier, EOperation> operation,
			Set<OperationCallExp<EClassifier, EOperation>> sourceResult,
			List<Set<OperationCallExp<EClassifier, EOperation>>> argumentResults) {

		if (operation.getReferredOperation().getName().equals("size"))
			result.add(operation);

		if (operation.getReferredOperation().getName().equals("="))
			result.add(operation);
		
		return result;
	}
	
	public Set<OperationCallExp<EClassifier, EOperation>> getResult() {
		return result;
	}
}
