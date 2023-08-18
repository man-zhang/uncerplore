package no.simula.se.uncertainty.evolution.rules;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Program;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.IfElse;
import org.moeaframework.util.tree.Rules;
import org.moeaframework.util.tree.Sequence;

import no.simula.se.uncertainty.evolution.cs.SutController;
import no.simula.se.uncertainty.evolution.domain.BModelHandler;
import no.simula.se.uncertainty.evolution.domain.UncertainWorld;
import no.simula.se.uncertainty.evolution.rules.action.T1;
import no.simula.se.uncertainty.evolution.rules.action.T2;
import no.simula.se.uncertainty.evolution.rules.action.T4;
import no.simula.se.uncertainty.evolution.rules.condition.BranchCoverageTR;
import no.simula.se.uncertainty.evolution.rules.condition.BranchPossibleNext;
import no.simula.se.uncertainty.evolution.rules.condition.isCurrentStateNew;

public class ExploreUncertainWorldProblem extends AbstractProblem {

	public static boolean first = true;

	private final Rules rules;

	public static UncertainWorld world;
	public static SutController cs;

	public ExploreUncertainWorldProblem(UncertainWorld register) {
		super(1, 1);

		rules = new Rules();

		rules.add(new T1());
		rules.add(new T2());
		rules.add(new T4());
		rules.add(new isCurrentStateNew());
		rules.add(new IfElse(Void.class));
		rules.add(new BranchCoverageTR());
		rules.add(new BranchPossibleNext());
		rules.add(new Sequence(Void.class, Void.class));
		rules.setReturnType(Void.class);

		world = register;
		cs = world.cs;
	}

	@Override
	public synchronized void evaluate(Solution solution) {

		Program program = (Program)solution.getVariable(0);

		world.reset();

		while (world.getRemainingSteps() > 0 && !world.isTerminate()) {
			Environment environment = new Environment();
			environment.set("world", world);
			program.evaluate(environment);
		}
		double fit = world.getFitness();
		world.getCurrent().setFitness(fit);
		world.getCurrent().setSolution(program.toString());
		world.cs.stopSUT();
		world.getCurrent().setExpName(world.cs.getCsName());
		displayLastEvaluation();
		BModelHandler.saveBModelDate("log", world.getCurrent(), first);
		first = false;
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
