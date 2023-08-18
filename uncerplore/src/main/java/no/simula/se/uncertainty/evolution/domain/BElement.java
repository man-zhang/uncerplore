package no.simula.se.uncertainty.evolution.domain;

import java.io.Serializable;

public abstract class BElement implements Serializable  {
	

	public String getEleType() {
		return eleType;
	}

	public void setEleType(String eleType) {
		this.eleType = eleType;
	}

	private static final long serialVersionUID = -4596502736152844899L;
	
	private String id;
	private String name;
	
	private String eleType = this.getClass().getName();

	private boolean preDef = true;
	private int vTimes = -1;

	public String toString(){
		return BModelHandler.gson.toJson(this);
	}

	public boolean isPreDef() {
		return preDef;
	}

	public void setPreDef(boolean preDef) {
		this.preDef = preDef;
	}

	public int getvTimes() {
		if(vTimes == -1) vTimes = 0;
		return vTimes;
	}

	public void setvTimes(int vTimes) {
		this.vTimes = vTimes;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void count1Visit(){
		int pre = this.getvTimes();
		int t = pre +1;
		this.setvTimes(t);
		if(this.getvTimes() - pre == 1){
			//System.out.println("++++++"+this.getName()+"+++++"+this.getvTimes()+"+++++++++++++++++");
		}
		else
			try {
				throw new Exception("wrong");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	

}
