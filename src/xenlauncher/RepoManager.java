/*
 * XenLauncher
 * 
 * An open source Minecraft launcher. Work in progress.
 * Licenced under the GNU General Public License, version 2 - http://www.gnu.org/licenses/gpl-2.0.html
 * Created by Benjamin Gwynn (http://xenxier.tk)
 */

/**
 * @name XenLauncher
 * @author xenxier
 */

package xenlauncher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

public class RepoManager {
    
    private String localPath, remotePath;
    private Repository localRepo;
    private Git git;
    
    public void register(String localPath, String remotePath) throws IOException {
        Output.print('I', "Selecting new working repository...");
        this.remotePath = remotePath; this.localPath = localPath;
        this.localRepo = new FileRepository(localPath + "/.git");
        git = new Git(this.localRepo);
    }
    
    public void trackRepoBranch(String branch) throws IOException, JGitInternalException,
            RefAlreadyExistsException, RefNotFoundException,
            InvalidRefNameException, GitAPIException {
        Output.print('I', "Switching repository branch...");
        git.branchCreate().setName(branch)
            .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
            .setStartPoint("origin/" + branch).setForce(true).call();
    }
    
    public void cloneRepo() throws IOException, NoFilepatternException, GitAPIException {
        Git.cloneRepository().setURI(this.remotePath)
            .setDirectory(new File(this.localPath)).call();
            Output.print('I', "Cloning repository...");
    }
    
    public void pullRepo() throws IOException, WrongRepositoryStateException,
            InvalidConfigurationException, DetachedHeadException,
            InvalidRemoteException, CanceledException, RefNotFoundException,
            NoHeadException, GitAPIException {
        Output.print('I', "Pulling repository...");
        git.pull().call();
    }
}
