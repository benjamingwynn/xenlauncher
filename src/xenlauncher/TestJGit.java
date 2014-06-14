package xenlauncher;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.internal.storage.file.FileRepository;

public class TestJGit {

    private String localPath, remotePath;
    private Repository localRepo;
    private Git git;

    public void init() throws IOException {
        localPath = "/home/me/repos/mytest";
        remotePath = "git@github.com:me/mytestrepo.git";
        localRepo = new FileRepository(localPath + "/.git");
        git = new Git(localRepo);
    }

    public void testCreate() throws IOException {
        Repository newRepo = new FileRepository(localPath + ".git");
        newRepo.create();
    }

    public void testClone() throws IOException, NoFilepatternException, GitAPIException {
        Git.cloneRepository().setURI(remotePath)
            .setDirectory(new File(localPath)).call();
    }

    public void testAdd() throws IOException, NoFilepatternException, GitAPIException {
        File myfile = new File(localPath + "/myfile");
        myfile.createNewFile();
        git.add().addFilepattern("myfile").call();
    }

    public void testCommit() throws IOException, NoHeadException,
            NoMessageException, ConcurrentRefUpdateException,
            JGitInternalException, WrongRepositoryStateException, GitAPIException {
        git.commit().setMessage("Added myfile").call();
    }
    
    public void testPush() throws IOException, JGitInternalException,
            InvalidRemoteException, GitAPIException {
        git.push().call();
    }

    public void testTrackMaster() throws IOException, JGitInternalException,
            RefAlreadyExistsException, RefNotFoundException,
            InvalidRefNameException, GitAPIException {
        git.branchCreate().setName("master")
            .setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM)
            .setStartPoint("origin/master").setForce(true).call();
    }

    public void testPull() throws IOException, WrongRepositoryStateException,
            InvalidConfigurationException, DetachedHeadException,
            InvalidRemoteException, CanceledException, RefNotFoundException,
            NoHeadException, GitAPIException {
        git.pull().call();
    }
}