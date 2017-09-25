package sysu.consistency.db.dao;

import java.util.List;

import sysu.consistency.db.bean.CommentEntry;

public interface CommentEntryDAO {
    public List<CommentEntry> findByProject(String project,int start,int end);
    public List<CommentEntry> findByProjectAndIsChange(String project,boolean isChange);
    public CommentEntry findByCommentID(int commentID);
    public List<CommentEntry> find(int start,int end);
    
    public void updateIsChange(int commentID,boolean isChange);
    
    
}
