package main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import data.Input;
import data.MultiDataSetInfo;
import emo.algorithms.Algorithm;
import emo.algorithms.moead.MOEA_D;
import emo.algorithms.nsga2.NSGA2;
import fgbml.multilabel.MOP_ExactMatchError;
import fgbml.multilabel.MOP_Fmeasure;
import fgbml.multilabel.MOP_HammingLoss;
import fgbml.multilabel.MultiPittsburgh;
import fgbml.multilabel.Output_MultiLabel;
import fgbml.multilabel.Problem_MultiLabel;
import fgbml.problem.OutputClass;
import ga.Population;
import method.MersenneTwisterFast;
import method.Output;
import method.ResultMaster;
import time.TimeWatcher;

public class MoFGBML_ML {


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
		String populationDir = resultRoot + sep + Consts.POPULATION;
		Output.mkdirs(populationDir);
		String offspringDir = resultRoot + sep + Consts.OFFSPRING;
		Output.mkdirs(offspringDir);

		Output.makeDir(populationDir, Consts.INDIVIDUAL);
		Output.makeDir(populationDir, Consts.RULESET);
		Output.makeDir(offspringDir, Consts.INDIVIDUAL);
		Output.makeDir(offspringDir, Consts.RULESET);


		/* ********************************************************* */
		//MOP No.
		int mopNo = Setting.mopNo;

		/* ********************************************************* */
		//Generate Problem
		Problem_MultiLabel mop = getMOP(mopNo, Dtra, Dtst);

		/* ********************************************************* */
		//Generate Algorithm
		Algorithm<MultiPittsburgh> algorithm;

		/* ********************************************************* */
		//Generate OutputClass
		OutputClass<MultiPittsburgh> output = new Output_MultiLabel();

		/* ********************************************************* */
		//Generate Individual Instance
		MultiPittsburgh instance = new MultiPittsburgh();
		instance.setCnum(((MultiDataSetInfo)mop.getTrain()).getCnum());

		/* ********************************************************* */
		//Timer start
		TimeWatcher timeWatcher = new TimeWatcher();	//All Exprimeint executing time
		TimeWatcher evaWatcher = new TimeWatcher();		//Evaluating time
		timeWatcher.start();

		/* ********************************************************* */
		//Population
		Population<MultiPittsburgh> population = new Population<>();

		/* ********************************************************* */
		/* ********************************************************* */
		//GA Start
		if(Setting.emoType == Consts.NSGA2) {
			algorithm = new NSGA2<MultiPittsburgh>();
			population = algorithm.main(mop, output, instance,
										resultMaster, rnd,
										timeWatcher, evaWatcher);
		}
		else if(Setting.emoType == Consts.WS ||
				Setting.emoType == Consts.TCHEBY ||
				Setting.emoType == Consts.PBI ||
				Setting.emoType == Consts.AOF) {
			algorithm = new MOEA_D<MultiPittsburgh>();
			population = algorithm.main(mop, output, instance,
										resultMaster, rnd,
										timeWatcher, evaWatcher);
		}
		/* ********************************************************* */
		/* ********************************************************* */

		/* ********************************************************* */
		//Predicted Labels Output
		String predictedTra = ((Output_MultiLabel)output).outputPredicted(population, Dtra);
		String predictedTst = ((Output_MultiLabel)output).outputPredicted(population, Dtst);
		String predictedTraFile = resultRoot + sep + "predicted_Tra.csv";
		String predictedTstFile = resultRoot + sep + "predicted_Tst.csv";
		Output.writeln(predictedTraFile, predictedTra);
		Output.writeln(predictedTstFile, predictedTst);

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

		System.out.println();
	}

	/**
	 * 1: MOP_ExactMatchError<br>
	 * 2: MOP_HammingLoss<br>
	 * 3: MOP_Fmeasure<br>
	 *
	 * @param mopNo
	 * @param Dtra
	 * @param Dtst
	 * @return MultiLabelProblem
	 */
	public static Problem_MultiLabel getMOP(int mopNo, MultiDataSetInfo Dtra, MultiDataSetInfo Dtst) {
		Problem_MultiLabel mop = null;
		switch(mopNo) {
		case 1:
			mop =  new MOP_ExactMatchError(Dtra, Dtst);
			break;
		case 2:
			mop = new MOP_HammingLoss(Dtra, Dtst);
			break;
		case 3:
			mop = new MOP_Fmeasure(Dtra, Dtst);
			break;
		}
		return mop;
	}
}
