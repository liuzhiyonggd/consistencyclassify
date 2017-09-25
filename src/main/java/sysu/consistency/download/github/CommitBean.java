package sysu.consistency.download.github;


import java.util.Date;
import java.util.List;

public class CommitBean {

	private int commitID;
	private String commitID2;
	private String author;
	private Date date;
	private String message;
	private String project;
	private boolean hasCoreClass=false;
	private boolean hasCoreMethod=false;
	private List<String> fileList;
	
	

	public String getCommitID2() {
		return commitID2;
	}

	public void setCommitID2(String commitID2) {
		this.commitID2 = commitID2;
	}

	public List<String> getFileList() {
		return fileList;
	}

	public void setFileList(List<String> fileList) {
		this.fileList = fileList;
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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isHasCoreClass() {
		return hasCoreClass;
	}

	public void setHasCoreClass(boolean hasCoreClass) {
		this.hasCoreClass = hasCoreClass;
	}

	public boolean isHasCoreMethod() {
		return hasCoreMethod;
	}

	public void setHasCoreMethod(boolean hasCoreMethod) {
		this.hasCoreMethod = hasCoreMethod;
	}
	
	

}
