package no.simula.se.uncertainty.evolution.cs;

import org.eclipse.emf.ecore.EObject;

import no.simula.se.uncertainty.evolution.domain.BConstraint;
import no.simula.se.uncertainty.evolution.util.ConstraintUtility;

public abstract class SutController {


	public void wait(int time){
		try {Thread.sleep(time);}catch (InterruptedException e) {e.printStackTrace();}
	}
	public void wait(Integer time){
		try {Thread.sleep(time.intValue());}catch (InterruptedException e) {e.printStackTrace();}
	}
	public void noAction(){
		System.out.println("no action executed!");
	}

	public String waitUtil(BConstraint bcons, int timeout, int freq){
		String ret="";
		W:
		while(! bcons.evaluate()){
			if(timeout <= 0){

				ret = "timeout is caused by "+bcons.getConstraint()+" of "+bcons.getInstance();
				break W;
			}
			wait(freq);
			timeout = timeout - freq;
		}
		return ret;
	}

	public void waitUtil(String cons, EObject ins){
		waitUtil(cons, ins, 3 * 60 * 1000, 200);
	}

	public String waitUtil(String cons, EObject ins, int timeout, int freq){
		String ret ="";
		W:
		while(! ConstraintUtility.evaluateOCL(cons, ins)){
			if(timeout <= 0){
				ret ="timeout is caused by "+cons+" of "+ins ;
				break W;
			}
			wait(freq);
			timeout = timeout - freq;
		}
		return ret;
	}

	//default is 3min, frequency is 0.2s
	public void waitUtil(BConstraint bcons){
		waitUtil(bcons, 3 * 60 * 1000, 200);
	}

	public abstract void resetSUT();

	public abstract void stopSUT();

	public abstract String getCsName();

	public abstract void afterteardown();


}
