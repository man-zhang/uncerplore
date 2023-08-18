package no.simula.se.uncertainty.evolution.domain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import no.simula.se.uncertainty.evolution.cs.SutController;
import no.simula.se.uncertainty.evolution.rules.ExploreUncertainWorldProblem;
import no.simula.se.uncertainty.evolution.util.Utility;

public class BOperation extends BElement{

	private static final long serialVersionUID = 8913211881217642575L;

	private String opName;
	//private Class<?>[] pars;
	private String[] parClazz;

	private BConstraint guard;

	//private CSUtil sut;
	private String[] parObjNames;
	private Object[] parObjs;

	private boolean isIndSInput = false;

	public String getOpName() {
		return opName;
	}
	public void setOpName(String opName) {
		this.opName = opName;
	}

	public String execute(){
		Object output = null;
		count1Visit();
		Method method;
		try {
			if(parClazz != null && parClazz.length != 0){
				method = getSut().getClass().getMethod(this.getOpName(), getPars());
				if(parObjs != null){
					Object[] pars = new Object[parObjs.length];
					for(int i = 0; i < parObjs.length; i++){
						pars[i] = getPars()[i].cast(parObjs[i]);
					}
					output = method.invoke(getSut(), pars);
				}else{
					output = method.invoke(getSut(), generateData());
				}

			}else{
				method = getSut().getClass().getMethod(this.getOpName());
				output = method.invoke(getSut());
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		String suf = ((output != null && output instanceof String && !((String)output).isEmpty())?"_output["+(String)output+"]":"");
		return getName()+ suf;
	}

	public Object[] generateData(){
		return null;
	}

	public void executeOp(Object...args){
		//System.out.println("executing bop with args:"+this.getName());
		Method method;
		try {
			if(parClazz != null){
				//System.out.println(getSut()+" "+this.getOpName());
				method = getSut().getClass().getMethod(this.getOpName(), getPars());
				method.invoke(getSut(), args);
			}else{
				method = getSut().getClass().getMethod(this.getOpName());
				method.invoke(getSut());
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public Class<?>[] getPars() {
		if(this.parClazz == null) return null;
		Class<?>[] pars = new Class<?>[parClazz.length];
		for(int i = 0; i < parClazz.length; i++){
			try {
				pars[i] = Utility.forName(parClazz[i]);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return pars;
	}
	public void setPars(Class<?>... pars) {
		if(pars != null){
			this.parClazz = new String[pars.length];
			for(int i = 0; i < pars.length; i++){
				this.parClazz[i] = pars[i].getName();
			}
		}

	}

	public boolean validate(){
		if(this.getGuard() != null) return this.guard.evaluate();
		return true;
	}
	public SutController getSut() {
		return ExploreUncertainWorldProblem.cs;
	}
//	public void setSut(CSUtil sut) {
//		this.sut = sut;
//	}
	public Object[] getParObjs() {
		return parObjs;
	}
	public void setParObjs(Object... parObjs) {
		this.parObjs = parObjs;
	}
	public String[] getParObjNames() {
		return parObjNames;
	}
	public void setParObjNames(String[] parObjNames) {
		this.parObjNames = parObjNames;
	}
	public String[] getParClazz() {
		return parClazz;
	}
	public void setParClazz(String[] parClazz) {
		this.parClazz = parClazz;
	}
	public BConstraint getGuard() {
		return guard;
	}
	public void setGuard(BConstraint guard) {

		this.guard = guard;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setNameWithGuard(){
		this.setName(this.getOpName()+" with "+ guard.getConstraint());
	}

	public static String getNameOp(String opName, String guard){
		return opName + " with "+guard;
	}

	public void setObjs(List<Object> objs){
		this.parObjs = new Object[objs.size()];
		for(int i = 0; i < objs.size(); i++){
			this.parObjs[i] = objs.get(i);
		}
	}
	public boolean isIndSInput() {
		return isIndSInput;
	}
	public void setIndSInput(boolean isIndSInput) {
		this.isIndSInput = isIndSInput;
	}
}
