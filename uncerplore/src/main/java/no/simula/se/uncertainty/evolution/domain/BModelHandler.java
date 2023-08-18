package no.simula.se.uncertainty.evolution.domain;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.CallEvent;
import org.eclipse.uml2.uml.ChangeEvent;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Event;
import org.eclipse.uml2.uml.FinalState;
import org.eclipse.uml2.uml.InstanceSpecification;
import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.OpaqueExpression;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Pseudostate;
import org.eclipse.uml2.uml.PseudostateKind;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.SignalEvent;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.TimeEvent;
import org.eclipse.uml2.uml.Transition;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import no.simula.se.testing.utility.ModelUtil;
import no.simula.se.testing.utility.belief.BeliefUtility;
import no.simula.se.testing.utility.belief.BeliefUtilityException;
import no.simula.se.uncertainty.evolution.util.BElementAdapter;
import no.simula.se.uncertainty.evolution.util.ModelUtility;

public class BModelHandler {

	public BModel bmodel;

	public static Gson gson = new Gson();

	public final static DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

//	public static BModel loadBModel(String path) {
//		try {
//			return BModelHandler.gson.fromJson(Files.newBufferedReader(Paths.get(path)), BModel.class);
//		} catch (JsonSyntaxException | JsonIOException | IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	static GsonBuilder  bulider = new GsonBuilder().registerTypeAdapter(BOperation.class, new BElementAdapter<BOperation>());

	private static Gson getGson(){
		return bulider.create();
	}

	public static BModel processPropertyGen(BModel bmodel){
		if(bmodel.pros != null)
			bmodel.pros.clear();
		for(BState bs : bmodel.sts.values()){
			if(bs.getConstraint() != null){
				bs.getConstraint().generateProperties();
				bs.getConstraint().generateConsFragement();

				for(BConsFragment bf : bs.getConstraint().getConsFragMaps().values()){
					if(bmodel.getPros().get(bf.getName()) == null){
						bmodel.getPros().put(bf.getName(), bf);
					}
				}
			}

		}
		return bmodel;
	}

	public static BModel loadBModel(String filepath) {
		try {
			return getGson().fromJson(Files.newBufferedReader(Paths.get(filepath)), BModel.class);
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void saveBModel(String path) {
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path))) {
			writer.write(gson.toJson(bmodel));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void saveFitAndSol(String folder, String fileName, String content){
		createFolder(folder);
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(folder+"/"+fileName+".fv"))) {
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void saveFitness(String folder, String fileName, String content){
		createFolder(folder);
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(folder+"/"+fileName+".fun"))) {
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveSolution(String folder, String fileName, String content){
		createFolder(folder);
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(folder+"/"+fileName+".var"))) {
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createFolder(String folder){
		Path path = Paths.get(folder);
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void saveBModelDate(String folder, BModel bmodel, boolean first) {
		Path path = Paths.get(folder);
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String tag = first?"_first":"";

		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(folder + "/" + bmodel.getPath() + tag+".json"))) {
			writer.write(gson.toJson(bmodel));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveBModelDate(String folder, BModel bmodel) {
		Path path = Paths.get(folder);
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(folder + "/" + bmodel.getPath() + ".json"))) {
			writer.write(gson.toJson(bmodel));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveBModel(String path, BModel bmodel) {
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path))) {
			writer.write(gson.toJson(bmodel));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateBModel(StateMachine sm, org.eclipse.uml2.uml.Package... configs) {

		bmodel = new BModel();
		bmodel.setName(sm.getQualifiedName());
		Map<String, InstanceSpecification> objs = new HashMap<String, InstanceSpecification>();
		for (org.eclipse.uml2.uml.Package config : configs) {
			for (Element in : config.getOwnedElements()) {
				if (in instanceof InstanceSpecification) {
					objs.put(((InstanceSpecification) in).getName(), ((InstanceSpecification) in));
				}
			}
		}
		generateBEs(objs, sm.getRegions(), null);
	}

	public void generateBEs(Map<String, InstanceSpecification> objs, List<Region> regs, BState composite) {
		for (Region reg : regs) {
			for (Element ele : reg.allOwnedElements()) {
				BState bs = null;
				if (ele instanceof State) {
					State s = (State) ele;
					if (bmodel.sts.get(s.getName()) == null) {
						if (ele instanceof FinalState) {
							bs = new BState(((FinalState) ele), ModelUtility.getVar(objs, ele));
							bs.setIsfinal(true);
							bmodel.sts.put(s.getQualifiedName(), bs);
							bs.setCompState(composite);
						}
						if (s.isSimple()) {
							bs = new BState(s, ModelUtility.getVar(objs, ele));
							bmodel.sts.put(s.getQualifiedName(), bs);
							bs.setCompState(composite);
						} else if (s.isComposite()) {
							bs = new BState(s, ModelUtility.getVar(objs, ele));
							bs.setComposite(true);
							bmodel.sts.put(s.getQualifiedName(), bs);
							bs.setCompState(composite);
							generateBEs(objs, s.getRegions(), bs);
						} else if (s.isSubmachineState()) {
							bs = new BState(s, ModelUtility.getVar(objs, ele));
							bs.setComposite(true);
							bmodel.sts.put(s.getQualifiedName(), bs);
							generateBEs(objs, s.getSubmachine().getRegions(), bs);
						}
					}

				} else if (ele instanceof Pseudostate) {
					if (((Pseudostate) ele).getKind().equals(PseudostateKind.TERMINATE_LITERAL)
							|| ((Pseudostate) ele).getKind().equals(PseudostateKind.INITIAL_LITERAL)
							|| ((Pseudostate) ele).getKind().equals(PseudostateKind.CHOICE_LITERAL)) {
						bs = new BState(((Pseudostate) ele), ModelUtility.getVar(objs, ele));
						bmodel.sts.put(((Pseudostate) ele).getQualifiedName(), bs);
						bs.setCompState(composite);
					}
				}
			}
		}

		for (Region reg : regs) {
			for (Element ele : reg.allOwnedElements()) {
				generateTR(objs, ele);
			}
		}
	}

	public void generateTR(Map<String, InstanceSpecification> objs, Element ele) {
		if (ele instanceof Transition) {
			Transition t = (Transition) ele;
			String opKey = "";
			BOperation bop = null;
			String guard = "";
			if (t.getGuard() != null)
				guard = ModelUtil.getBodyConstraint(t.getGuard());
			if (t.getTriggers().size() == 0) {

				// if (t.getTarget() instanceof FinalState) {
				// bmodel.sts.get(t.getSource().getQualifiedName()).setIsfinal(true);
				// } else if (t.getTarget() instanceof Pseudostate
				// && ((Pseudostate)
				// t.getTarget()).getKind().equals(PseudostateKind.TERMINATE_LITERAL))
				// {
				// bmodel.sts.get(t.getSource().getQualifiedName()).setTerminate(true);
				// } else

				if (t.getTarget() instanceof FinalState) {
					 bmodel.sts.get(t.getSource().getQualifiedName()).setIsfinal(true);
				}
				else if (t.getTarget() instanceof Pseudostate
						&& ((Pseudostate) t.getTarget()).getKind().equals(PseudostateKind.TERMINATE_LITERAL)) {
					bmodel.sts.get(t.getSource().getQualifiedName()).setTerminate(true);
				}else if (t.getSource() instanceof Pseudostate
						&& ((Pseudostate) t.getSource()).getKind().equals(PseudostateKind.INITIAL_LITERAL)) {
					bmodel.sts.get(t.getTarget().getQualifiedName()).setInital(true);
				}

				if (t.getTarget() instanceof Pseudostate
						&& ((Pseudostate) t.getTarget()).getKind().equals(PseudostateKind.CHOICE_LITERAL)) {
					String[] opkeys = new String[t.getTarget().getOutgoings().size()];
					int loc = 0;
					for (Transition ct : t.getTarget().getOutgoings()) {
						opkeys[loc] = getOpKey(ct);
						generateTR(objs, ct);
						loc++;
					}
					opKey = BConditionOperation.getCondOpName(opkeys);
					if (bmodel.ops.get(opKey) == null) {
						bop = new BConditionOperation(getBOps(opkeys));
						bmodel.ops.put(bop.getName(), bop);
					}

				} else {
					if (t.getGuard() != null) {
						opKey = guard;
						if (bmodel.ops.get(opKey) == null) {
							bop = new BGuardOperation(opKey, ModelUtility.getVar(objs, ele));
							bmodel.ops.put(opKey, bop);
						}

					}
				}
				// FIXME condition is not handled in this case.
			} else if (t.getTriggers().size() == 1) {
				Event event = t.getTriggers().get(0).getEvent();

				if (event instanceof ChangeEvent) {
					opKey = ModelUtil
							.getBodyOpaqueExpression((OpaqueExpression) ((ChangeEvent) event).getChangeExpression());
					if (bmodel.ops.get(opKey) == null) {
						bop = new BChangeOperation(((ChangeEvent) event), ModelUtility.getVar(objs, ele));
						bmodel.ops.put(opKey, bop);
					}

				} else if (event instanceof CallEvent) {
					opKey = ((CallEvent) event).getOperation().getName();

					if (!guard.isEmpty()) {
						opKey = BOperation.getNameOp(opKey, guard);
					}

					if (bmodel.ops.get(opKey) == null) {
						bop = new BCallOperation(((CallEvent) event).getOperation().getName());
						//bop.setName(opKey);
						if (t.getGuard() != null) {
							bop.setGuard(new BConstraint(t, ModelUtility.getVar(objs, ele)));
							bop.setNameWithGuard();
						}

						bmodel.ops.put(opKey, bop);
					}
				} else if (event instanceof SignalEvent) {
					// FIXEME the signal event is not handled in this
					// version
					opKey = ((SignalEvent) event).getSignal().getName();
					if (!guard.isEmpty())
						opKey = BOperation.getNameOp(opKey, guard);
					if (bmodel.ops.get(opKey) == null) {
						bop = new BSignalOperation(((SignalEvent) event).getSignal().getName());
						bmodel.ops.put(opKey, bop);
						if (t.getGuard() != null) {
							bop.setGuard(
									new BConstraint(t, ModelUtility.getVar(objs, ((SignalEvent) event).getSignal())));
							bop.setNameWithGuard();
						}
					}
				} else if (event instanceof TimeEvent) {
					opKey = ((LiteralString) ((TimeEvent) event).getWhen().getExpr()).getValue();
					if (bmodel.ops.get(opKey) == null) {
						bop = new BAfterOperation(((TimeEvent) event));
						bmodel.ops.put(opKey, bop);
					}
				}

			}
			if (!opKey.equals("")) {

				bop = bmodel.ops.get(opKey);
				// create uncertainty
				BState ss = bmodel.sts.get(t.getSource().getQualifiedName());
				BState ts = bmodel.sts.get(t.getTarget().getQualifiedName());
				String un = BModel.getUnName(ss.getName(), opKey, ts.getName());// ss.getName()
																				// +
																				// "@"
																				// +
																				// opKey
																				// +
																				// "@"
																				// +
																				// ts.getName();
				if (bmodel.uns.get(un) == null) {
					BUncertainty bu = new BUncertainty(ss, bop, ts);
					bu.setName(un);
					bmodel.uns.put(un, bu);
					try {
						EObject unObj = BeliefUtility.getUncertaintiesOfTransition(t);
						if (unObj != null) {
							double[] degrees = BeliefUtility.getDoubleMeasurementOfUncertainty(unObj);
							if (degrees.length == 1) {
								bu.setDegree(degrees[0]);
							} else {
								bu.setDegree(1.0);
							}

							EList<?> indsObj = BeliefUtility.getIndSpecsOfUncertainty(unObj);
							if (indsObj != null) {
								for (Object obj : indsObj) {
									if (obj instanceof Constraint) {
										Constraint cons = (Constraint) obj;

										String indsInd = cons.getName();
										IndSp indsp = bmodel.inds.get(indsInd);
										if (indsp == null) {
											indsp = new IndSp(cons, ModelUtility.getVar(objs, cons));
											bmodel.inds.put(indsInd, indsp);
											EList<?> value = BeliefUtility.getIndInput(cons);
											if (value != null) {
												for (Object trigger : value) {
													if (trigger instanceof Operation) {
														String tkey = ((Operation) trigger).getName();
														BOperation top = bmodel.ops.get(tkey);
														if (top == null) {
															top = new BCallOperation(tkey);
															top.setIndSInput(true);
															bmodel.ops.put(tkey, top);
														}else if(!top.isIndSInput()) top.setIndSInput(true);
														indsp.getTriggers().add(top);
													}
												}
											}
											bu.getIndSps().add(indsp);
										}
									} else
										System.out.println("indspec is not constraint");
								}
							}
						}
					} catch (BeliefUtilityException e) {
						e.printStackTrace();
					}
				}
			} else {
				System.err.println(t + " opkey is null!");
			}
		}
	}

	public String getOpKey(Transition t) {
		String opKey = "";
		String guard = "";
		if (t.getGuard() != null)
			guard = ModelUtil.getBodyConstraint(t.getGuard());

		if (t.getTriggers().size() == 1) {
			Event event = t.getTriggers().get(0).getEvent();

			if (event instanceof ChangeEvent) {
				opKey = ModelUtil
						.getBodyOpaqueExpression((OpaqueExpression) ((ChangeEvent) event).getChangeExpression());

			} else if (event instanceof CallEvent) {
				opKey = ((CallEvent) event).getOperation().getName();

			} else if (event instanceof SignalEvent) {
				opKey = ((SignalEvent) event).getSignal().getName();

			} else if (event instanceof TimeEvent) {
				opKey = ((LiteralString) ((TimeEvent) event).getWhen().getExpr()).getValue();

			}
		}
		if (opKey.isEmpty())
			return guard;
		if (guard.isEmpty())
			return opKey;
		return BOperation.getNameOp(opKey, guard);
	}

	public BOperation[] getBOps(String[] opkeys) {
		BOperation[] ops = new BOperation[opkeys.length];
		for (int i = 0; i < opkeys.length; i++) {
			ops[i] = bmodel.ops.get(opkeys[i]);
			if (ops[i] == null)
				System.out.println("The error in getBOps: there is no op found by name as " + opkeys[i]);
		}
		return ops;
	}
}
