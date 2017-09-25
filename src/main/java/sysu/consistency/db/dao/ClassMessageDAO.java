package sysu.consistency.db.dao;

import java.util.List;

import sysu.consistency.db.bean.ClassMessage;

public interface ClassMessageDAO {
	
	public ClassMessage findByClassID(int classID);
	public List<ClassMessage> findByProjectAndCommitID(String project,String commitID);
	public List<ClassMessage> findByProject(String project,int start,int end);
	public List<ClassMessage> findByProjectAndType(String project,String type,int start,int end);
	public List<ClassMessage> find(int start,int end);

}
