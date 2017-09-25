package sysu.consistency.download.github;

import java.io.File;

import org.eclipse.jgit.api.Git;

public class CloneRepo {
	
	public static void main(String[] args)throws Exception{
//		clone("https://github.com/hibernate/hibernate-orm.git","D:/git-repo/hibernate");
//		clone("https://github.com/spring-projects/spring-framework.git","D:/git-repo/spring");
//		clone("https://github.com/apache/struts.git","D:/git-repo/struts");
//		clone("https://github.com/apache/log4j.git","D:/git-repo/log4j");
//		clone("https://github.com/apache/commons-logging.git","D:/git-repo/commons-logging");
//		clone("https://github.com/apache/maven.git","D:/git-repo/maven");
//		clone("https://github.com/apache/hadoop.git","D:/git-repo/hadoop");
//		clone("https://github.com/apache/tomcat.git","D:/git-repo/tomcat");
//		clone("https://github.com/apache/tomcat70.git","D:/git-repo/tomcat70");
//		clone("https://github.com/apache/tomcat80.git","D:/git-repo/tomcat80");
//		clone("https://github.com/apache/openwebbeans.git","D:/git-repo/openwebbeans");
//		clone("https://github.com/apache/commons-net.git","D:/git-repo/commons-net");
//		clone("https://github.com/apache/commons-io.git","D:/git-repo/commons-io");
//		clone("https://github.com/apache/commons-csv.git","D:/git-repo/commons-csv");
//		clone("https://github.com/shekhargulati/strman-java.git","D:/git-repo/strman-java");
//		clone("https://github.com/lwhite1/tablesaw.git","D:/git-repo/tablesaw");
//		clone("https://github.com/elastic/elasticsearch.git","D:/git-repo/elasticsearch");
		
		clone("https://github.com/OpenHFT/Chronicle-Map.git","D:/git-repo/chronicle-map");
		clone("https://github.com/worstcase/gumshoe.git","D:/git-repo/gumshoe");
		
		
		
		
	}
	
	
	private static void clone(String url,String savePath) throws Exception{
		Git.cloneRepository().setURI(url).setDirectory(new File(savePath)).call();
	}

}
