package main;

public class Start {

	public static void init(String[] args) {
		/* ********************************************************* */
		String version = "22.0";
		System.out.println("ver.: " + version);

		/* ********************************************************* */
		/* Load ".properties" */
		String currentDir = "./";
		String settingSource = args[0];
		Setting.setSettings(currentDir, settingSource);

		/* Command Line Arguments */
		if(args.length > 1) {
			Setting.trainingFile = args[1];
			Setting.testFile = args[2];
			Setting.saveDir = args[3];
			Setting.mopNo = Integer.parseInt(args[4]);
		}

		/* ********************************************************* */
		System.out.println("Processors: " + Runtime.getRuntime().availableProcessors() + " ");
		System.out.println();
		System.out.print("args: ");
		for(int i = 0; i < args.length; i++) {
			System.out.print(args[i] + " ");
		}
		System.out.println();
		System.out.println();

	}
}
