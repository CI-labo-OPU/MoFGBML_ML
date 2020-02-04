package main;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ForkJoinPool;

import method.OsSpecified;

/**
 */
public class Setting {

	/* ********************************************************* */
	/** Random seed */
	public static int seed = 2019;
	/** Dataset Name */
	public static String dataName = "emotion";
	/** Training Dataset File */
	public static String trainingFile = "a0_0_emotions-10tra.dat";
	/** Test Dataset File */
	public static String testFile = "a0_0_emotions-10tst.dat";
	/** Place of stored results */
	public static String saveDir = "results";

	/** 0:CF-mean, 1:CF-vector */
	public static int CFtype = 0;

	/** 1:MOP_SubACC, 2:MOP_HL, 3:MOP_FM */
	public static int mopNo = 1;

	/** EMOA (0:NSGA-II, 1:WS, 2:TCH, 3:PBI, 4:IPBI, 5:AOF) */
	public static int emoType = 0;
	/** #of Population */
	public static int populationSize = 60;
	/** #of Offspring */
	public static int offspringSize = 60;
	/** Termination Criteria (true: generation, false: evaluation) */
	public static boolean terminationCriteria = true;
	/** #of Generation */
	public static int generationNum = 1000;
	/** #of Evaluation */
	public static int evaluationNum = 60000;

	/** Timing of output Population (generation based) */
	public static int timingOutput = 1000;
	/** #of CPU cores */
	public static int parallelCores = 1;
	// ************************************************************
	/** Consts */
	public static int osType;
	public static ForkJoinPool forkJoinPool = new ForkJoinPool(parallelCores);
	public static int calclationType = 0;	//0:Single node
	/** Dataset preDivide Number */
	public static int preDivNum = 1;
	/** (true:Stop by each trial) */
	public static boolean isOnceExe = true;

	// ************************************************************
	// ************************************************************
	public static void setSettings(String dir, String source) {

		URLClassLoader urlLoader = null;
		ResourceBundle bundle = null;
		try {
			urlLoader = new URLClassLoader(new URL[] {new File(dir).toURI().toURL()});
			bundle = ResourceBundle.getBundle(source, Locale.getDefault(), urlLoader);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		//The type of OS
		if(OsSpecified.isLinux() == true || OsSpecified.isMac() == true) {
			osType = Consts.UNIX;	//Linux or Mac
			System.out.println("OS: Linux or Mac");
		} else {
			osType = Consts.WINDOWS;	//windows
			System.out.println("OS: Windows");
		}

		if(bundle.containsKey("seed")) { seed = Integer.parseInt(bundle.getString("seed")); }
		if(bundle.containsKey("dataName")) { dataName = bundle.getString("dataName"); }
		if(bundle.containsKey("trainingFile")) { trainingFile = bundle.getString("trainingFile"); }
		if(bundle.containsKey("testFile")) { testFile = bundle.getString("testFile"); }
		if(bundle.containsKey("saveDir")) { saveDir = bundle.getString("saveDir"); }
		if(bundle.containsKey("emoType")) { emoType = Integer.parseInt(bundle.getString("emoType")); }
		if(bundle.containsKey("populationSize")) { populationSize = Integer.parseInt(bundle.getString("populationSize")); }
		if(bundle.containsKey("offspringSize")) { offspringSize = Integer.parseInt(bundle.getString("offspringSize")); }
		if(bundle.containsKey("terminationCriteria")) { terminationCriteria = Boolean.parseBoolean(bundle.getString("terminationCriteria")); }
		if(bundle.containsKey("generationNum")) { generationNum = Integer.parseInt(bundle.getString("generationNum")); }
		if(bundle.containsKey("evaluationNum")) { evaluationNum = Integer.parseInt(bundle.getString("evaluationNum")); }
		if(bundle.containsKey("CFtype")) { CFtype = Integer.parseInt(bundle.getString("CFtype")); }
		if(bundle.containsKey("mopNo")) { mopNo = Integer.parseInt(bundle.getString("mopNo")); }
		if(bundle.containsKey("timingOutput")) { timingOutput = Integer.parseInt(bundle.getString("timingOutput")); }
		if(bundle.containsKey("parallelCores")) { parallelCores = Integer.parseInt(bundle.getString("parallelCores")); }
		if(bundle.containsKey("calclationType")) { calclationType = Integer.parseInt(bundle.getString("calclationType")); }
		if(bundle.containsKey("preDivNum")) { preDivNum = Integer.parseInt(bundle.getString("preDivNum")); }
		if(bundle.containsKey("isOnceExe")) { isOnceExe = Boolean.parseBoolean(bundle.getString("isOnceExe")); }

		forkJoinPool = new ForkJoinPool(parallelCores);
		bundle = null;
	}


	public String getStaticValues() {
		StringBuilder sb = new StringBuilder();
		String sep = System.lineSeparator();
		sb.append("Class: " + this.getClass().getCanonicalName() + sep);
		sb.append("Settings: " + sep);
		for(Field field : this.getClass().getDeclaredFields()) {
			try {
				field.setAccessible(true);
				sb.append(field.getName() + " = " + field.get(this) + sep);
			} catch(IllegalAccessException e) {
				sb.append(field.getName() + " = " + "access denied" + sep);
			}
		}
		return sb.toString();
	}






}
