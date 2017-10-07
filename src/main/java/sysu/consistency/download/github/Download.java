package sysu.consistency.download.github;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

public class Download {
	
	public static void main(String[] args) throws Exception {
		//sueecss:"hibernate","spring","struts","commons-csv","commons-io","elasticsearch","maven","strman-java","tablesaw",
		//error:"tomcat","commons-logging","commons-net","hadoop","log4j","openwebbeans","tomcat70","tomcat80"
		String[] projects = new String[]{
				"hibernate","spring","struts","commons-csv","commons-io","elasticsearch","maven","strman-java","tablesaw"
				};
		for(String str:projects){
		    downloadCommitList("d:/git-repo",str);
		    System.out.println(str+" is done.");
		}
		
	}
	
	public static void downloadCommitList(String gitRepoLocated,String project) throws Exception{
		Git git = Git.open(new File(gitRepoLocated+"/"+project));

		Repository repository = git.getRepository();

		RevWalk revWalk = new RevWalk(repository);
		ObjectId commitId = repository.resolve("refs/heads/master");
		revWalk.markStart(revWalk.parseCommit(commitId));
		RevCommit preCommit = null;
		//commit遍历顺序为最新的commit到最旧的commit,preCommit为当前commit
		for (RevCommit commit : revWalk) {
			if(preCommit==null){
				preCommit = commit;
				continue;
			}
		
			CommitBean commitBean = new CommitBean();
			int commitID = preCommit.getId().toString().hashCode();
			String author = preCommit.getAuthorIdent().getName();
			String message = preCommit.getFullMessage();
			commitBean.setCommitID(commitID);
			commitBean.setCommitID2(preCommit.getId().toString());
			commitBean.setAuthor(author);
			commitBean.setMessage(message);
			commitBean.setProject(project);
			commitBean.setDate(new Date(((long)preCommit.getCommitTime())*1000));
			
			List<String> fileList = new ArrayList<String>();
			List<DiffEntry> diffEntryList = getDiffEntryList(git, preCommit.getId());
			for (DiffEntry diffEntry : diffEntryList) {
				if (diffEntry.getChangeType() == ChangeType.ADD) {
					if (diffEntry.getNewPath().endsWith(".java")) {
                        fileList.add("add:"+diffEntry.getNewPath());
					}
				} else if (diffEntry.getChangeType() == ChangeType.DELETE && preCommit != null) {
					if (diffEntry.getOldPath().endsWith(".java")) {
						fileList.add("delete:"+diffEntry.getOldPath());
					}
				} else if (diffEntry.getChangeType() == ChangeType.MODIFY) {
					if (diffEntry.getOldPath().endsWith(".java")) {
						fileList.add("modify:"+diffEntry.getNewPath()+","+diffEntry.getOldPath());
					}
				}
			}
			commitBean.setFileList(fileList);
			CommitDAO.insert(commitBean);
			preCommit = commit;
		}
	}

	//下载commit中变化的java源代码
	public static void downloadCommit(String gitRepoLocated,String project,String savePath) throws Exception {
		Git git = Git.open(new File(gitRepoLocated+"/"+project));

		Repository repository = git.getRepository();

		RevWalk revWalk = new RevWalk(repository);
		ObjectId commitId = repository.resolve("refs/heads/master");
		revWalk.markStart(revWalk.parseCommit(commitId));
		RevCommit preCommit = null;
		
		//commit遍历顺序为最新的commit到最旧的commit,preCommit为当前commit
		for (RevCommit commit : revWalk) {
			if(preCommit==null){
				preCommit = commit;
				continue;
			}
			List<DiffEntry> diffEntryList = getDiffEntryList(git, preCommit.getId());
			for (DiffEntry diffEntry : diffEntryList) {
				if (diffEntry.getChangeType() == ChangeType.ADD) {
					if (diffEntry.getNewPath().endsWith(".java")) {

						ByteArrayOutputStream os = readFile(git, preCommit.getId(), diffEntry.getNewPath());
						if (os != null) {
							FileUtils.writeByteArrayToFile(
									new File(savePath+"/"+project+"/" + preCommit.getId() + "/new/" + diffEntry.getNewPath()),
									os.toByteArray());
						}
					}
				} else if (diffEntry.getChangeType() == ChangeType.DELETE && preCommit != null) {
					if (diffEntry.getOldPath().endsWith(".java")) {
						ByteArrayOutputStream os = readFile(git, commit.getId(), diffEntry.getOldPath());
						if (os != null) {
							FileUtils.writeByteArrayToFile(
									new File(savePath+"/"+project+"/" + preCommit.getId() + "/old/" + diffEntry.getOldPath()),
									os.toByteArray());
						}
					}
				} else if (diffEntry.getChangeType() == ChangeType.MODIFY) {
					if (diffEntry.getOldPath().endsWith(".java")) {
						ByteArrayOutputStream os = readFile(git, preCommit.getId(), diffEntry.getNewPath());
						if (os != null) {
							FileUtils.writeByteArrayToFile(
									new File(savePath+"/"+project+"/"+ preCommit.getId() + "/new/" + diffEntry.getNewPath()),
									os.toByteArray());
						}
						if (preCommit != null) {
							os = readFile(git, commit.getId(), diffEntry.getOldPath());
							if(os!=null){
							    FileUtils.writeByteArrayToFile(
									    new File(savePath+"/"+project+"/"+ preCommit.getId() + "/old/" + diffEntry.getOldPath()),
									    os.toByteArray());
							}
						}
					}
				}

			}
			preCommit = commit;
		}
	}

	//获取java中变化的实体列表
	private static List<DiffEntry> getDiffEntryList(Git git, ObjectId objId) throws Exception {

		Repository repository = git.getRepository();

		Iterable<RevCommit> allCommitsLater = git.log().add(objId).call();
		Iterator<RevCommit> iter = allCommitsLater.iterator();
		RevCommit commit = iter.next();
		TreeWalk tw = new TreeWalk(repository);
		tw.addTree(commit.getTree());
		commit = iter.next();
		if (commit != null) {
			tw.addTree(commit.getTree());
		} else {
			return new ArrayList<DiffEntry>();
		}

		tw.setRecursive(true);
		RenameDetector rd = new RenameDetector(repository);
		rd.addAll(DiffEntry.scan(tw));
		List<DiffEntry> diffEntries = rd.compute();
		return diffEntries;
	}

	//读取文件
	private static ByteArrayOutputStream readFile(Git git, ObjectId objId, String path) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Repository repository = null;
		try {
			repository = git.getRepository();
			RevWalk walk = new RevWalk(repository);
			RevCommit revCommit = walk.parseCommit(objId);
			RevTree revTree = revCommit.getTree();

			TreeWalk treeWalk = TreeWalk.forPath(repository, path, revTree);
			if (treeWalk == null) {
				return null;
			}
			ObjectId blobId = treeWalk.getObjectId(0);
			ObjectLoader loader = repository.open(blobId);
			loader.copyTo(out);
		} catch (IOException e) {
		} catch (JGitInternalException e) {
		} finally {
			if (repository != null)
				repository.close();
		}

		return out;
	}

}
