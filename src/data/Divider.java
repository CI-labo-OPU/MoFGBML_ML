package data;

import java.net.InetSocketAddress;

import method.MersenneTwisterFast;

public class Divider {
	// ************************************************************
	static MersenneTwisterFast uniqueRnd;
	static int dpop;

	// ************************************************************


	// ************************************************************

	/**
	 * 島モデルにおける各島への部分学習用データセットの生成<br>
	 * @param islandNum : int : #of island
	 * @param dataset : DataSetInfo : Original DataSetInfo
	 * @param setting
	 * @param serverList
	 * @return DataSetInfo[] : Divided DataSetInfos
	 */
	public static SingleDataSetInfo[] devideIsland(int islandNum, SingleDataSetInfo dataset, int setting, InetSocketAddress[] serverList) {

		int Cnum = dataset.getCnum();
		int dataSize = dataset.getDataSize();

		//#of patterns for each class
		int[] eachClassSize = new int[Cnum];
		for(int p = 0; p < dataSize; p++) {
			eachClassSize[dataset.getPattern(p).getConClass()]++;
		}

		//#of patterns for each class in each island sub datasets
		int[][] classDividedSize = new int[Cnum][islandNum];
		int remainAddPoint = 0;
		for(int c = 0; c < Cnum; c++) {
			for(int i = 0; i < islandNum; i++) {
				classDividedSize[c][i] = eachClassSize[c] / islandNum;
			}
			int remain = eachClassSize[c] % islandNum;
			for(int i = 0; i < remain; i++) {
				int point = remainAddPoint % islandNum;
				classDividedSize[c][point]++;
				remainAddPoint++;
			}
		}

		//Size of each island sub dataset
		int[] eachDataSize = new int[islandNum];
		for(int c = 0; c < Cnum; c++) {
			for(int i = 0; i < islandNum; i++) {
				eachDataSize[i] += classDividedSize[c][i];
			}
		}

		//Distributing patterns into each island
		SingleDataSetInfo[] divided = new SingleDataSetInfo[islandNum + 1];	//Each island + Original
		for(int i = 0; i < islandNum; i++) {
			divided[i] = new SingleDataSetInfo();
			divided[i].setDataSize(eachDataSize[i]);
			divided[i].setNdim(dataset.getNdim());
			divided[i].setCnum(Cnum);
			divided[i].setSetting(setting);
			divided[i].setServerList(serverList);
		}

		//Sort patterns by classes
		dataset.sortPattern();

		int index = 0;
		for(int c = 0; c < Cnum; c++) {
			for(int i = 0; i < islandNum; i++) {

				for(int p = 0; p < classDividedSize[c][i]; p++) {
					divided[i].addPattern( dataset.getPattern(index++) );
				}

			}
		}

		//End of DataSetInfo[] is Original dataset
		divided[islandNum] = dataset;

		return divided;
	}


}




























