package main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import data.Input;
import data.MultiDataSetInfo;
import data.SingleDataSetInfo;
import emo.algorithms.Algorithm;
import emo.algorithms.moead.MOEA_D;
import emo.algorithms.nsga2.NSGA2;
import fgbml.SinglePittsburgh;
import fgbml.binary_relevance.BRpittsburgh;
import fgbml.binary_relevance.BRruleset;
import fgbml.binary_relevance.MOP_multi;
import fgbml.binary_relevance.MOP_single;
import fgbml.binary_relevance.Output_BR_multi;
import fgbml.binary_relevance.Output_BR_single;
import fgbml.binary_relevance.Problem_BR_Multi;
import fgbml.binary_relevance.Problem_BR_Single;
import fgbml.problem.OutputClass;
import fuzzy.SingleRuleSet;
import ga.Population;
import method.MersenneTwisterFast;
import method.Output;
import method.ResultMaster;
import time.TimeWatcher;

public class BinaryRelevance {
	public static void main(String[] args) {
		/* ********************************************************* */
		Start.init(args);

		/* ********************************************************* */
		Date start = new Date();
		System.out.println("START: ");
		System.out.println(start);

		/* ********************************************************* */
		//Make result directries
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmm");
		String sep = File.separator;
		String id = format.format(calendar.getTime());
		//format: ".\result\iris_20191021-1255"
		String resultRoot = System.getProperty("user.dir") + sep + "result" + sep
							+ Setting.saveDir + sep + Setting.dataName + "_" + id;
		Output.mkdirs(resultRoot);

		/* ********************************************************* */
		//Output "Experimental Settings"
		String consts = (new Consts()).getStaticValues();
		String settings = (new Setting()).getStaticValues();
		String fileName = resultRoot + sep + "Consts_" + id + ".txt";
		Output.writeln(fileName, consts);
		fileName = resultRoot + sep + "Setting_" + id + ".txt";
		Output.writeln(fileName, settings);

		/* ********************************************************* */
		//Result Master
		ResultMaster resultMaster = new ResultMaster(resultRoot, id);

		/* ********************************************************* */
		//Random Generator
		MersenneTwisterFast rnd = new MersenneTwisterFast(Setting.seed);

		/* ********************************************************* */
		//Start Experiment
		startExperiment(args,
						Setting.trainingFile, Setting.testFile,
						rnd, resultMaster);

		/* ********************************************************* */
		Date end = new Date();
		System.out.println("END: ");
		System.out.println(end);
		/* ********************************************************* */
	}

	public static void startExperiment( String[] args, String traFile, String tstFile,
										MersenneTwisterFast rnd, ResultMaster resultMaster) {
		/* ********************************************************* */
		//START:

		/* ********************************************************* */
		//Load Dataset
		MultiDataSetInfo Dtra = new MultiDataSetInfo();
		MultiDataSetInfo Dtst = new MultiDataSetInfo();
		Input.inputMultiLabel(Dtra, traFile);
		Input.inputMultiLabel(Dtst, tstFile);

		/* ********************************************************* */
		//Make result directry
		String sep = File.separator;
		String resultRoot = resultMaster.getRootDir();

		String aggregationDir = resultRoot + sep + "aggregation";
		Output.mkdirs(aggregationDir);

		//Generate Problem
		Problem_BR_Multi mop = new MOP_multi(Dtra, Dtst);

		/* ********************************************************* */
		//Timer start
		TimeWatcher timeWatcher = new TimeWatcher();	//All Exprimeint executing time
		TimeWatcher evaWatcher = new TimeWatcher();		//Evaluating time
		timeWatcher.start();

		/* ********************************************************* */
		//Generate OutputClass
		Output_BR_multi output = new Output_BR_multi();

		/* ********************************************************* */
		//Generate Aggrigation Array
		SingleRuleSet[] bestRuleSets = new SingleRuleSet[mop.getTrain().getCnum()];

		/* ********************************************************* */
		/* ********************************************************* */
		//Binary Relevance Start
		for(int c = 0; c < mop.getTrain().getCnum(); c++) {
			System.out.println("---");
			System.out.println("Label: " + c);

			/* ********************************************************* */
			//Make result directry
			String trialBRroot = resultRoot + sep + "Label-" + String.format("%03d", c);
			resultMaster.setTrialRoot(trialBRroot);

			String populationDir = trialBRroot + sep + Consts.POPULATION;
			Output.mkdirs(populationDir);
			String offspringDir = trialBRroot + sep + Consts.OFFSPRING;
			Output.mkdirs(offspringDir);

			Output.makeDir(populationDir, Consts.INDIVIDUAL);
			Output.makeDir(populationDir, Consts.RULESET);
			Output.makeDir(offspringDir, Consts.INDIVIDUAL);
			Output.makeDir(offspringDir, Consts.RULESET);

			/* ********************************************************* */
			//Transform Dataset
			SingleDataSetInfo Dtra_single = mop.getDtra_single(c);

			/* ********************************************************* */
			//Generate Problem
			Problem_BR_Single mop_single = new MOP_single(Dtra_single);

			/* ********************************************************* */
			//Generate Individual Instance
			SinglePittsburgh instance = new SinglePittsburgh();

			/* ********************************************************* */
			//Generate Algorithm
			Algorithm<SinglePittsburgh> algorithm;

			/* ********************************************************* */
			//Generate OutputClass
			OutputClass<SinglePittsburgh> output_single = new Output_BR_single();

			/* ********************************************************* */
			//Population in Final Generation
			Population<SinglePittsburgh> population = new Population<>();

			/* ********************************************************* */
			/* ********************************************************* */
			//GA Start
			if(Setting.emoType == Consts.NSGA2) {
				algorithm = new NSGA2<SinglePittsburgh>();
				population = algorithm.main(mop_single, output_single, instance,
											resultMaster, rnd,
											timeWatcher, evaWatcher);
			}
			else if(Setting.emoType == Consts.WS ||
					Setting.emoType == Consts.TCHEBY ||
					Setting.emoType == Consts.PBI ||
					Setting.emoType == Consts.AOF) {
				algorithm = new MOEA_D<SinglePittsburgh>();
				population = algorithm.main(mop_single, output_single, instance,
											resultMaster, rnd,
											timeWatcher, evaWatcher);
			}
			/* ********************************************************* */
			/* ********************************************************* */

			//GA End
			timeWatcher.stop();
			resultMaster.addTimes( timeWatcher.getSec() );
			resultMaster.addEvaTimes( evaWatcher.getSec() );

			//Output One Trial Information
			resultMaster.outputIndividual(populationDir, offspringDir);
			resultMaster.population.clear();
			resultMaster.ruleSetPopulation.clear();
			resultMaster.offspring.clear();
			resultMaster.ruleSetOffspring.clear();

			//Get Best Single Classifier
			bestRuleSets[c] = getBestSingleRuleSet(population);
			bestRuleSets[c].radixSort();

			System.out.println();
		}
		/* ********************************************************* */
		/* ********************************************************* */

		/* ********************************************************* */
		//Generate Aggrigation RuleSet
		BRruleset ruleSet = new BRruleset(bestRuleSets);
		ruleSet.aggregationRuleNum();

		/* ********************************************************* */
		//Generate Aggrigation Pittsburgh Individual
		BRpittsburgh individual = new BRpittsburgh(ruleSet);
		individual.setObjectiveNum(mop.getObjectiveNum());
		individual.setNdim(Dtra.getNdim());

		//Appendix
		mop.evaluate(individual);
		mop.setAppendix(individual);

		//Output
		String individualStr = output.outputPittsburgh(individual);
		String fileName = aggregationDir + sep + "individual.csv";
		Output.writeln(fileName, individualStr);
		String ruleSetStr = output.outputRuleSet(individual);
		fileName = aggregationDir + sep + "ruleSet.txt";
		Output.writeln(fileName, ruleSetStr);

		/* ********************************************************* */
		//Predicted Labels Output
		String predictedTra = outputPredicted(individual, mop, 0);
		String predictedTst = outputPredicted(individual, mop, 1);
		String predictedTraFile = resultRoot + sep + "predicted_Tra.csv";
		String predictedTstFile = resultRoot + sep + "predicted_Tst.csv";
		Output.writeln(predictedTraFile, predictedTra);
		Output.writeln(predictedTstFile, predictedTst);

	}

	public static String outputPredicted(BRpittsburgh individual, Problem_BR_Multi mop, int dataID) {
		String ln = System.lineSeparator();
		String c = ",";
		String strs = "";
		String str = "";

		//Header
		str = "truly" + c + "predicted";
		strs += str + ln;

		int[][] predicted = mop.getClassifiedParallel(dataID, individual);
		MultiDataSetInfo dataset = mop.getMultiDataSet(dataID);
		for(int i = 0; i < dataset.getDataSize(); i++) {
			str = array2str(dataset.getPattern(i).getConClass());
			str += c + array2str(predicted[i]);
			strs += str + ln;
		}
		return strs;
	}

	public static String array2str(int[] array) {
		String str = "";
		for(int i = 0; i < array.length; i++) {
			if(array[i] == -1) {
				str += "2";
			}
			else {
				str += String.valueOf(array[i]);
			}
		}
		return str;
	}

	public static SingleRuleSet getBestSingleRuleSet(Population<SinglePittsburgh> population) {

		double min = Double.MAX_VALUE;
		int minIndex = 0;

		for(int p = 0; p < population.getIndividuals().size(); p++) {
			double f = population.getIndividual(p).getFitness(0);
			if(f < min) {
				min = f;
				minIndex = p;
			}
		}

		SinglePittsburgh best = population.getIndividual(minIndex);

		return best.getRuleSet();
	}
}

























