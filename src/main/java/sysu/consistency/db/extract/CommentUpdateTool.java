package sysu.consistency.db.extract;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import sysu.consistency.db.mongo.RepositoryFactory;
import sysu.consistency.db.repository.CommentRepository;

public class CommentUpdateTool {
	
	/**
	 * ����ͬ������Ϊ�������ı��ͬ��
	 * @param filePath ͬ��������ļ���
	 * @throws IOException 
	 */
	public static void update1(String filePath) throws IOException{
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();
		
		List<String> fileLines = FileUtils.readLines(new File(filePath),"UTF-8");
		
		List<UpdateEntry> updateEntryList = new ArrayList<UpdateEntry>();
		for(String line:fileLines){
			String[] temps = line.split(";");
			String project = temps[0];
			int commitID = Integer.parseInt(temps[1]);
			String className = temps[2]+".java";
			int startLine = Integer.parseInt(temps[3]);
			int endLine = Integer.parseInt(temps[4]);
			
			UpdateEntry entry = new UpdateEntry(project,commitID,className,startLine,endLine);
			updateEntryList.add(entry);
		}
		
	}

}
