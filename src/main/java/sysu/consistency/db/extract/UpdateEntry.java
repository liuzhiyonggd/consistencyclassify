package sysu.consistency.db.extract;

public class UpdateEntry {
	
	private String project;
	private int commitID;
	private String className;
	private int startLine;
	private int endLine;
	
	public UpdateEntry(String project,int commitID,String className,int startLine,int endLine){
		this.project = project;
		this.commitID = commitID;
		this.className = className;
		this.startLine = startLine;
		this.endLine = endLine;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public int getCommitID() {
		return commitID;
	}
	public void setCommitID(int commitID) {
		this.commitID = commitID;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public int getStartLine() {
		return startLine;
	}
	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}
	public int getEndLine() {
		return endLine;
	}
	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}
	
	

}
