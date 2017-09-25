package sysu.consistency.datamining;

import java.io.IOException;

public class DataMining {

	public static void main(String[] args) throws IOException {
		String[] types = new String[]{"oldcomment","oldcode","newcode"};
		for(String type:types){
			WordMapping.mapping(type);
//			DataGenerator.generate(type);
//			EclatRelease ecla = new EclatRelease(type);
//			ecla.run();
//			StringGenerator.generate(type);
//			FrequencyAnalysis.analysis(type);
		}
	}
}
