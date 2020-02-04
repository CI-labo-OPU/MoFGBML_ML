package main;

import java.lang.reflect.Field;

/**
 */
public class Consts {

	//OS
	public static int WINDOWS = 0;	//windows
	public static int UNIX = 1;	//unix

	//Experiment's Parameters - 実験設定パラメータ
	public static boolean IS_RANDOM_PATTERN_SELECT = false;	//ランダムなパターンで組む
	public static boolean IS_NOT_EQUAL_DIVIDE_NUM = false;	//部分個体群とデータ分割数を一緒にしない
	public static boolean IS_ALL_MIGLATION = false;	//true: 各島の最良個体を全島で共有する, false: 各島の最良個体を隣の島に移住

	//GA Parameters - GA用設定パラメータ
	/** for optimizer */
	public static int MINIMIZE = 1;
	/** for optimizer */
	public static int MAXIMIZE = -1;
	/** 出力された部分データセットを用いるかどうか */
	public static boolean LOAD_SUBDATASET = false;

	//Parallel Parameters - 並列用パラメータ
	public static boolean IS_RULESETS_SORT = false;	//評価の際にルール数でソートするかどうか
	public static boolean IS_RULE_PARALLEL = true;	//ルールで並列化するかどうか（データのパターンでなく）
	public static boolean IS_ISLAND_TIME = false;	//サーバ1大の時に各島の時間も測る．(評価だけ並列の時)

	//GBML's parameters
	/** don't careにしない条件部の数 */
	public static int ANTECEDENT_LEN = 5;
	/** don't care適応確率 */
	public static double DONT_CARE_RT = 0.8;
	/** don't careを確率で行う */
	public static boolean IS_PROBABILITY_DONT_CARE = false;
	/** ミシガン操作時にルールを追加する（置き換えでなく） */
	public static boolean DO_ADD_RULES = false;
	/** ES型個体群更新戦略 */
	public static boolean IS_ES_UPDATE = false;

	/** Michigan適用確率 */
	public static double RULE_OPE_RT = 0.5;
	/** Michigan交叉確率 */
	public static double RULE_CROSS_RT = 0.9;
	/** ルール入れ替え割合 */
	public static double RULE_CHANGE_RT = 0.2;
	/** Michigan型GAの際のルール生成数 (true: 1, false: RULE_CHANGE_RT) */
	public static boolean RATE_OR_ONLY = false;

	public static boolean DO_LOG_PER_LOG = true;	//ログでログを出力
	/** Pittsburgh交叉確率 */
	public static double RULESET_CROSS_RT = 0.9;

	//NSGA-II's Parameters
	public static int NSGA2 = 0;	//NSGA-IIの番号
	public static int OBJECTIVE_DEGREES = 0;	//目的関数の回転度
	public static boolean DO_CD_NORMALIZE = false;	//Crowding Distance を正規化するかどうか
	public static boolean HAS_PARENT = false;

	//EMO's parameters
	public static int SECOND_OBJECTIVE_TYPE = 0;	//2目的目, 0:rule, 1:length, 2:rule * length, 4:length/rule

	//MOEA/D's parameters
	public static int[] VECTOR_DIVIDE_NUM = new int[] {99};			//分割数
	public static double MOEAD_ALPHA = 0.9;			//参照点のやつ
	public static double MOEAD_THETA = 5.0;			//シータ
	public static boolean IS_NEIGHBOR_SIZE = false;	//近傍サイズ false:個数指定, true:パーセント指定
	public static int NEIGHBOR_SIZE_RT = 10;			//近傍サイズ%
	public static int NEIGHBOR_SIZE = 10;			//近傍サイズ[個]
	public static int SELECTION_NEIGHBOR_NUM = 10;	//選択近傍サイズ
	public static int UPDATE_NEIGHBOR_NUM = 5;		//更新近傍サイズ

	/** weighted sum */
    public static int WS  = 1;
    /** Tchebycheff */
    public static int TCHEBY = 2;
    /** PBI */
    public static int PBI = 3;
    /** InvertedPBI */
    public static int IPBI = 4;
    /** Accuracy Oriented Function */
    public static int AOF = 5;
    public static boolean IS_AOF_VECTOR_INT = false;	//AOFのベクトルを書くルール数で固定する（これをすると島モデルやりづらい）
	public static boolean DO_NORMALIZE = false;	//正規化するかどうか
    public static boolean IS_BIAS_VECTOR = false;	//false: NObiasVector, true: biasVector

    public static double IS_FIRST_IDEAL_DOWN = 0.0;	//１目的目のみ下に動かす．（やらない場合は０に）
    public static boolean IS_WS_FROM_NADIA = false;	//WSをナディアポイントから

	//Fuzzy System's parameters
    /**
     * <h1>ファジィ集合の初期化方法</h1>
     * 0: 2-5分割 homogeneous triangle fuzzy partitions.<br>
     * 1: Input XML file<br>
     * 2: Inhomogeneous<br>
     */
    public static int FUZZY_SET_INITIALIZE = 0;
    /** Input XML file name */
    public static String XML_FILE = "a.xml";
    /** #of Inhomogeneous Partitions */
    public static int PARTITION_NUM = 5;
    /** Inhomogeneous Fuzzyfying Grade */
	public static double FUZZY_GRADE = 0.5;
	public static int FUZZY_SET_NUM = 14;			//ファジィ集合の種類数
	/** 初期ル―ル数 */
	public static int INITIATION_RULE_NUM = 30;
	public static int MAX_FUZZY_DIVIDE_NUM = 5;	//条件部の分割数の最大値
	/** 1識別器あたりの最大ルール数 */
	public static int MAX_RULE_NUM = 60;
	/** 1識別器あたりの最小ルール数 */
	public static int MIN_RULE_NUM = 1;
	/** ヒューリスティック生成法 */
	public static boolean DO_HEURISTIC_GENERATION = true;
	public static int DC_LABEL = 0;	//don't careを表すファジィ集合ラベル
	public static boolean DO_FUZZY_PARALLEL = false;	//ファジィ計算において並列化を行うかどうか
	public static boolean DO_PREFER_NOCLASS = true;	//足りていないクラスを結論部として持つルールを優先的に生成するかどうか

	//One Objective Weights
	public static int W1 = 1000;
	public static int W2 = -1;
	public static int W3 = -1;

	//Other parametaers
	/** 表示する世代間隔 */
	public static int PER_SHOW_GENERATION_NUM = 100;
	/** 詳細表示するドット間隔 */
	public static int PER_SHOW_GENERATION_DETAIL = 10;
	public static int WAIT_SECOND = 300000;
	public static int TIME_OUT_TIME = 30000;
	public static int SLEEP_TIME = 1000;

	public static int TRAIN = 0;	//学習用データインデックス
	public static int TEST = 1;	//評価用データインデックス


	//Folders' Name
	public static String ROOTFOLDER = "result";
	public static String RULESET = "ruleset";
	public static String INDIVIDUAL = "individual";
	public static String POPULATION = "population";
	public static String OFFSPRING = "offspring";
	public static String SUBDATA = "subdata";
	public static String VECSET = "vecset";
	public static String SOLUTION = "solution";
	public static String LOGS = "logs";
	public static String LOGS_READABLE = "logs_readable";
	public static String DATA = "data";
	public static String TIMES = "times";
	public static String OTHERS = "write";

	//Mistery Parameters
	public static double IS_CLASS_CLOSS_RATE = 0.0;	//クラス交叉確率(MOEA/D)
	public static boolean IS_DEMOCRACY = false;	//多数決にする


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
