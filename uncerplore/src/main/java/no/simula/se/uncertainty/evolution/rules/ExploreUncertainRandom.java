package no.simula.se.uncertainty.evolution.rules;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Program;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Rules;

import no.simula.se.uncertainty.evolution.cs.SutController;
import no.simula.se.uncertainty.evolution.domain.BModelHandler;
import no.simula.se.uncertainty.evolution.domain.UncertainWorld;
import no.simula.se.uncertainty.evolution.rules.action.Random;

public class ExploreUncertainRandom extends AbstractProblem {

	private final Rules rules;

	public static UncertainWorld world;
	public static SutController cs;

	public ExploreUncertainRandom(UncertainWorld register) {
		super(1, 1);

		rules = new Rules();

		rules.add(new Random());


		world = register;
		cs = world.cs;
	}
//
//	public DiscoverUncertainWorld(UncertainWorld world) {
//		this();
//
//	}

	@Override
	public synchronized void evaluate(Solution solution) {
		Program program = (Program)solution.getVariable(0);

		world.reset();

		while (world.getRemainingSteps() > 0 && !world.isTerminate()) {
			//System.out.println(beliefMap.getRemainingSteps());
			Environment environment = new Environment();
			environment.set("world", world);
			program.evaluate(environment);
		}
		double fit = world.getFitness();
		world.getCurrent().setFitness(fit);
		world.getCurrent().setSolution(program.toString());
		world.cs.stopSUT();
		world.getCurrent().setExpName("UHS_APP11_R");
		displayLastEvaluation();
		BModelHandler.saveBModelDate("Rlog", world.getCurrent());
		solution.setObjective(0, fit);

	}

	public void displayLastEvaluation() {
		world.display();
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 1);
		solution.setVariable(0, new Program(rules));
		return solution;
	}
}
