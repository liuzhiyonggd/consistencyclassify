package sysu.consistency.db.extract;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommentExtractor {
	
	public static void extract(String project){
		JavadocExtractor.extract(project);
		BlockExtractor.extract(project);
		LineExtractor.extract(project);
	}
	
	public static void main(String[] args) throws IOException {
//		List<String> projects = FileUtils.readLines(new File("D:/projects.txt"));
		//,
		String[] projects = new String[]{"jhotdraw","jedit","ejbca","htmlunit","freecol","jamwiki","opennms","kablink"};

		DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		for(String project:projects){
			CommentExtractor.extract(project);
			System.out.println(project +"is done. "+sdf2.format(new Date()));
		}
	}

}
