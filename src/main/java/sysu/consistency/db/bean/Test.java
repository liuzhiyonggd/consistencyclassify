package sysu.consistency.db.bean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.ClassMessageRepository;

public class Test {
	
	public static void main(String[] args) throws IOException{
		testClassTable();
	}
	
	public static void testClassTable() throws IOException{
		List<String> idFile = FileUtils.readLines(new File("e:/注释作用域实验/purpose_id.txt"),"UTF-8");
		Set<String> randomIdList = new HashSet<String>();
		Random random = new Random();
		while(randomIdList.size()<2000) {
			int randomIndex = random.nextInt(idFile.size()-1);
			randomIdList.add(idFile.get(randomIndex));
		}
		FileUtils.writeLines(new File("e:/注释作用域实验/purpose_id_random.txt"), randomIdList);
	}

}
