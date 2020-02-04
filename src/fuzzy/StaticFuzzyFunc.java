package fuzzy;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import data.MultiDataSetInfo;
import data.Pattern;
import data.SingleDataSetInfo;
import main.Consts;
import method.MersenneTwisterFast;

/**
 * Fuzzy Systemに関する関数
 *
 */

public class StaticFuzzyFunc {
	// ************************************************************
	static int KK[] = {1,2,2,3,3,3,4,4,4,4,5,5,5,5,5,6,6,6,6,6,6,7,7,7,7,7,7,7};
	static int kk[] = {1,1,2,1,2,3,1,2,3,4,1,2,3,4,5,1,2,3,4,5,6,1,2,3,4,5,6,7};


	// ************************************************************

	// ************************************************************

	/**
	 * <h1>Membership Function</h1>
	 */
	public static double calcMembership(int attribute, int fuzzySet, double x) {
		double ans = 0.0;

		if(fuzzySet == 0) {//Don't Care
			ans = 1.0;
		}
		else if(fuzzySet < 0) {//Nominal
			if(fuzzySet == x) {
				ans = 1.0;
			}
			else {
				ans = 0.0;
			}
		}
		else if(fuzzySet > 0) {//Numeric
			double a = (double)(kk[fuzzySet]-1) / (double)(KK[fuzzySet]-1);
			double b = 1.0 / (double)(KK[fuzzySet]-1);

			ans = 1.0 - ( Math.abs(x - a) / b );

			if(ans < 0.0){
				ans = 0.0;
			}
		}

		return ans;
	}


	/**
	 * <h1>適合度</h1><br>
	 * <br>
	 * @param line
	 * @param rule
	 * @return
	 */
	public static double memberMulPure(Pattern line, int rule[]) {
		double ans = 1.0;
		int Ndim = rule.length;
		for(int i = 0; i < Ndim; i++) {
			ans *= calcMembership(i, rule[i], line.getDimValue(i));
			if(ans == 0) {
				break;
			}
		}
		return ans;
	}

	/**
	 * <h1>信頼度</h1><br>
	 * （データから直接なので，データが大きいと結構重い処理（O[n]））<br>
	 * 1つのルールの条件部ファジィ集合に対して，各クラスへの信頼度を返す<br>
	 * <br>
	 * @param Dtra
	 * @param rule
	 * @param forkJoinPool
	 * @return
	 */
	public static double[] calcTrust(SingleDataSetInfo Dtra, int[] rule, ForkJoinPool forkJoinPool) {
		int Cnum = Dtra.getCnum();
		double[] trust = new double[Cnum];

		ArrayList<Double> part = new ArrayList<Double>();
		Optional<Double> partSum = null;

		for(int c = 0; c < Cnum; c++) {
			final int CLASSNUM = c;
			partSum = null;
			try {
				partSum = forkJoinPool.submit( () ->
							Dtra.getPatterns().parallelStream()
							.filter(s -> s.getConClass() == CLASSNUM)
							.map(s -> memberMulPure(s, rule))
							.reduce( (l,r) -> l+r)
						).get();
			} catch(InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			part.add( partSum.orElse(0.0) );
		}

		double all = 0.0;
		for(int c = 0; c < Cnum; c++) {
			all += part.get(c);
		}

		if(all != 0.0) {
			for(int c = 0; c < Cnum; c++) {
				trust[c] = part.get(c) / all;
			}
		}

		return trust;
	}

	/**
	 * <h1>信頼度(マルチラベル用)</h1>
	 * @param Dtra : MultiDataSetInfo :
	 * @param rule : int[] : Antecedent Part
	 * @param forkJoinPool
	 * @return double[][] : [Lnum][2], Lnum:#of Labels, 2:Associate or Non-associate
	 */
	public static double[][] calcTrustMulti(MultiDataSetInfo Dtra, int[] rule, ForkJoinPool forkJoinPool) {
		int Lnum = Dtra.getCnum();

		/** Lnum:#of Labels, 2:associate or non-associate */
		double[][] trust = new double[Lnum][2];

		ArrayList<Double> part = new ArrayList<>();
		Optional<Double> partSum = null;

		double sumAll;

		for(int l = 0; l < Lnum; l++) {
			for(int j = 0; j < 2; j++) {
				final int LABEL = l;
				final int ASSOCIATE = j;
				partSum = null;

				try {
					partSum = forkJoinPool.submit( () ->
								Dtra.getPatterns().parallelStream()
								.filter(p -> p.getConClass(LABEL) == ASSOCIATE)
								.map(p -> memberMulPure(p, rule) )
								.reduce( (acc,r) -> acc+r )
							).get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				// Membership Value
				// Label l == j
				trust[l][j] = partSum.orElse(0.0);
			}
		}

		//Calculate trust
		for(int l = 0; l < Lnum; l++) {
			//Sum of All patterns
			sumAll = trust[l][0] + trust[l][1];

			for(int j = 0; j < 2; j++) {
				if(sumAll != 0.0) {
					trust[l][j] = trust[l][j] / sumAll;
				}
				else {
					trust[l][j] = 0.0;
				}
			}
		}

		return trust;
	}


	/**
	 * <h1>結論部クラス</h1><br>
	 * 信頼度から結論部クラスを決定する<br>
	 * trust[]の最大を結論部クラスとする．<br>
	 * <br>
	 * @param trust
	 * @return
	 */
	public static int calcConclusion(double[] trust) {
		int Cnum = trust.length;
		int ans = 0;

		double max = 0.0;
		for(int c = 0; c < Cnum; c++) {
			if(max < trust[c]) {
				max = trust[c];
				ans = c;
			}
			else if(max == trust[c]) {
				ans = -1;
			}
		}

		return ans;
	}

	/**
	 * <h1>ルール重み</h1>
	 * @param conCla
	 * @param trust
	 * @return
	 */
	public static double calcCf(int conCla, double[] trust) {
		double ans = 0.0;
		int Cnum = trust.length;

		if(conCla == -1 || trust[conCla] <= 0.5) {
			ans = 0;
		}
		else {
			double sum = 0.0;
			for(int c = 0; c < Cnum; c++) {
				sum += trust[c];
			}
			ans = trust[conCla] - (sum - trust[conCla]);
		}

		return ans;
	}

	/**
	 * <h1>Rule Weight</h1>
	 * @param trust : double[][] : [Lnum][2]
	 * @return double[] : Rule Weight Vector for Each Label.
	 */
	public static double[] calcCfMulti(int[] conclusion, double[][] trust) {
		int Lnum = trust.length;

		double[] cfVector = new double[Lnum];

		for(int l = 0; l < Lnum; l++) {
			if(conclusion[l] == -1) {
				cfVector[l] = 0.0;
			}
			else {
				// |(nonAssociate) - (Associate)|
				cfVector[l] = Math.abs(trust[l][0] - trust[l][1]);
			}
		}

		return cfVector;
	}

	/**
	 * <h1>Heuristic Rule Generation Method - ヒューリスティックルール生成法</h1><br>
	 * 1つのパターンに対する各ファジィ集合のメンバシップ値を用いたルーレット選択によって，
	 * 各次元のファジィ集合を決定したファジィルールを生成する．<br>
	 * <br>
	 * @param line : Pattern : 生成に使用するパターン
	 * @param rnd
	 * @return int[] : ヒューリスティック生成法によって生成したファジィルールの前件部
	 *
	 */
	public static int[] heuristicGeneration(Pattern line, MersenneTwisterFast rnd) {
		int[] rule = new int[line.getNdim()];
		boolean isProb = Consts.IS_PROBABILITY_DONT_CARE;
		double dcRate;
		if(isProb) {
			dcRate = Consts.DONT_CARE_RT;
		}
		else {
			// (Ndim - const) / Ndim
			dcRate = (double)(((double)line.getNdim() - (double)Consts.ANTECEDENT_LEN)/(double)line.getNdim());
		}

		double[] membershipValueRoulette = new double[Consts.FUZZY_SET_NUM];
		for(int n = 0; n < line.getNdim(); n++) {
			if(rnd.nextDouble() < dcRate) {
				rule[n] = 0;
			}
			else {
				if(line.getDimValue(n) < 0) {
					//Categorical Dimension
					rule[n] = (int)line.getDimValue(n);
				}
				else {
					//Numerical Dimension
					double sumMembershipValue = 0.0;
					membershipValueRoulette[0] = 0;
					for(int f = 0; f < Consts.FUZZY_SET_NUM; f++) {
						sumMembershipValue += calcMembership(n, f+1, line.getDimValue(n));
						membershipValueRoulette[f] = sumMembershipValue;
					}

					double rr = rnd.nextDouble() * sumMembershipValue;
					for(int f = 0; f < Consts.FUZZY_SET_NUM; f++) {
						if(rr < membershipValueRoulette[f]) {
							rule[n] = f+1;
							break;
						}
					}
				}

			}
		}

		return rule;
	}



}




































