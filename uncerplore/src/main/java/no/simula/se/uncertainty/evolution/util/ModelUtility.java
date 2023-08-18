package no.simula.se.uncertainty.evolution.util;

import java.io.File;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.InstanceSpecification;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import no.simula.se.testing.utility.CustomURIConverter;

public class ModelUtility {
	
	
	public static org.eclipse.uml2.uml.Package[] generateSpecifiedTestConfigs(org.eclipse.uml2.uml.Package model, String... setups){
		
		org.eclipse.uml2.uml.Package[] configs = new org.eclipse.uml2.uml.Package[setups.length];
		
		int i = 0;
		for(Element e: model.allOwnedElements()){
			if(e instanceof org.eclipse.uml2.uml.Package){
				org.eclipse.uml2.uml.Package _pack = (org.eclipse.uml2.uml.Package)e;
				if(isPart(setups, _pack.getName())){
					configs[i] = _pack;
					i++;
				}
			}
		}
		return configs;
	}
	
	public static boolean isPart(String[] all, String one){
		for(String e : all){
			if(e.equals(one)) return true;
		}
		return false;
	}
	
	public static EObject loadUMLModel( String uurl) {
		ResourceSet rs = new ResourceSetImpl();
		EObject loaded = null;
		rs.setURIConverter(new CustomURIConverter());
		
		UMLResourcesUtil.init(rs);

		Resource resource = rs.getResource(createFileURI(uurl), true);
		EObject umlModel = (EObject) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.PACKAGE);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
				new XMIResourceFactoryImpl());
		loaded = umlModel;
		EcoreUtil.resolveAll(rs);


		return loaded;
	}
	
//	public static org.eclipse.uml2.uml.Package loadPackage(String url) {
//		ResourceSet rs = new ResourceSetImpl();
//		rs.setURIConverter(new CustomURIConverter());
//		rs.getPackageRegistry().put(HandlingSystem_ULMA_INTE_V2_1Package.eNS_URI,  HandlingSystem_ULMA_INTE_V2_1Package.eINSTANCE);
//		rs.getPackageRegistry().put(typesPackage.eNS_URI,  typesPackage.eINSTANCE);
//		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
//				new XMIResourceFactoryImpl());
//		UMLResourcesUtil.init(rs);
//
//		Resource resource = rs.getResource(createFileURI(url), true);
//		System.out.println("Root objects count: " + resource.getContents().size());
//		org.eclipse.uml2.uml.Package uml = (org.eclipse.uml2.uml.Package) EcoreUtil
//				.getObjectByType(resource.getContents(), UMLPackage.Literals.PACKAGE);
//		return uml;
//	}
	
	private static URI createFileURI(String relativePath) {
		return URI.createFileURI(new File(relativePath).getAbsolutePath());
	}
	
	
	public static String getVar(Map<String, InstanceSpecification> objs, EObject e) {
		String clazz = getClassifierOwner(e).getName();
		for (String key : objs.keySet()) {
			if (objs.get(key).getClassifier(clazz) != null)
				return key;
		}
		return null;
	}

	public static org.eclipse.uml2.uml.Class getClassifierOwner(EObject object) {
		if ((((Element) object).getOwner() instanceof org.eclipse.uml2.uml.Class)
				&& !(((Element) object).getOwner() instanceof org.eclipse.uml2.uml.StateMachine)) {
			return (org.eclipse.uml2.uml.Class) ((Element) object).getOwner();
		}
		return getClassifierOwner(((Element) object).getOwner());
	}
}
