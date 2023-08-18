package no.simula.se.uncertainty.evolution;

import no.simula.se.uncertainty.evolution.cs.SutController;
import no.simula.se.uncertainty.evolution.domain.BModel;
import no.simula.se.uncertainty.evolution.domain.BModelHandler;
import no.simula.se.uncertainty.evolution.domain.UncertainWorld;
import no.simula.se.uncertainty.evolution.rules.ExploreUncertainWorldProblem;
import no.simula.se.uncertainty.evolution.util.ModelUtility;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.AlgorithmFactory;

import java.util.Properties;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.common.util.UML2Util.EObjectMatcher;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.util.UMLUtil;

import com.google.gson.Gson;


public class UncerPloreRunner {

	public boolean run(String modelPath, UncertainWorld sutUnceraintySpaceToExplore, SutController sutController, int populationSize, int maxFitnessEvaluation) {
		try {

			BModel bmodel = sutUnceraintySpaceToExplore.getOrigin();
			if (bmodel == null) {
				bmodel = BModelHandler.loadBModel(modelPath);
				sutUnceraintySpaceToExplore.setOrigin(bmodel);
			}
			if (bmodel == null)
				throw new IllegalArgumentException("cannot evolve the model and explore the SUT without the model");

			if (sutUnceraintySpaceToExplore.cs == null) {
				sutUnceraintySpaceToExplore.cs = sutController;
			}

			if (sutUnceraintySpaceToExplore.cs == null)
				throw new IllegalArgumentException("cannot evolve the model and explore the SUT without implemented SutController");

			ExploreUncertainWorldProblem prob = new ExploreUncertainWorldProblem(sutUnceraintySpaceToExplore);

			Algorithm algorithm = null;
			Properties properties = new Properties();
			properties.setProperty("populationSize", String.valueOf(populationSize));

			sutUnceraintySpaceToExplore.setMaxSteps(100);

			try {
				algorithm = AlgorithmFactory.getInstance().getAlgorithm(
						"GA", properties, prob);

				int generation = 0;
				// run the GP solver
				while ((generation < maxFitnessEvaluation)) {
					bmodel.setGeneration(generation);
					algorithm.step();
					generation++;

					Solution result = algorithm.getResult().get(0);
					System.out.println("one generate is done " + result.getObjectives()[0] + " " + result.getVariable(0).toString());

				}
			} finally {
				if (algorithm != null) {
					algorithm.terminate();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}


	public boolean covertBM2ExBM(String beliefModelPath, String stateMachineName, String[] setupForTesting, String outputJsonFile) {

		if (beliefModelPath == null)
			throw new IllegalArgumentException("beliefModelPath must be specified");
		if (stateMachineName == null)
			throw new IllegalArgumentException("stateMachineName must be specified");
		if (setupForTesting == null)
			throw new IllegalArgumentException("setupForTesting(eg, package name in uml model) must be specified");
		if (outputJsonFile == null)
			throw new IllegalArgumentException("outputJsonFile must be specified");
		if (!outputJsonFile.endsWith(".json"))
			throw new IllegalArgumentException("We only support to convert the belief model into Json format, thus outputJsonFile must end with json");

		org.eclipse.uml2.uml.Package model = (org.eclipse.uml2.uml.Package) ModelUtility.loadUMLModel(beliefModelPath);
		StateMachine sm = (StateMachine) UMLUtil.findEObject(model.allOwnedElements(), new EObjectMatcher() {
			public boolean matches(EObject eObject) {
				return (eObject instanceof StateMachine) && ((StateMachine) eObject).getName().equals(stateMachineName);
			}
		});
		org.eclipse.uml2.uml.Package[] configs = ModelUtility.generateSpecifiedTestConfigs(model, setupForTesting);

		try {

			BModelHandler gen = new BModelHandler();
			gen.generateBModel(sm, configs);
			gen.bmodel.processLinkStates();
			gen.bmodel.findInitalState();

			Gson gson = new Gson();
			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputJsonFile))) {
				writer.write(gson.toJson(gen.bmodel));
			}

			System.out.println("Executable Belief Model has been outputted with the path:" + outputJsonFile);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
