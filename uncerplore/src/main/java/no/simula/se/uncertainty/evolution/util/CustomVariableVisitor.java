package no.simula.se.uncertainty.evolution.util;

import java.util.HashSet;
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
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.expressions.VariableExp;
import org.eclipse.ocl.utilities.AbstractVisitor;

public class CustomVariableVisitor extends
		AbstractVisitor<Set<Variable<EClassifier, EParameter>>, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint> {

	public CustomVariableVisitor() {
		super(new HashSet<Variable<EClassifier, EParameter>>());
	}

	

	@Override
	public Set<Variable<EClassifier, EParameter>> visitVariableExp(VariableExp<EClassifier, EParameter> v) {

		
		Variable<EClassifier, EParameter> referredVar = v.getReferredVariable();
		
		if ("self".equals(referredVar.getName())) {
			 result.add(referredVar);
		}
		return result;
	}
}
