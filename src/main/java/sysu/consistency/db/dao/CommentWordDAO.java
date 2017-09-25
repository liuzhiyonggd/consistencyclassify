package sysu.consistency.db.dao;

import java.util.List;

import sysu.consistency.db.bean.CommentWord;

public interface CommentWordDAO {
	
	public List<CommentWord> findByProject(String project,int start,int end);
	public CommentWord findByCommentID(int commentID);

}
