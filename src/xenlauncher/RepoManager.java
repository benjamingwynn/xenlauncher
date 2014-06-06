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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;

public class RepoManager {
    static ArrayList repo_list = new ArrayList();
    
    public static void addDefaultRepos() {
        repo_list.add("https://github.com/benjamingwynn/xenlauncher_repo.git");
    }
    
    public static void newRepo(String repo_url) throws GitAPIException {
            Output.print('I', "Getting repo at " + repo_url);
            repo_list.add(repo_url);
            gitClone(repo_url, FileManager.getRepoDirectory(repo_list.size()));
    }
    
    public static void pullAllRepoMeta() {
        for (int i = 0; i < repo_list.size(); i++) {
            try { gitClone(String.valueOf(repo_list.get(i)), "repo_" + String.valueOf(i));
            } catch (GitAPIException ex) {
                Output.print('F', "Couldn't refresh repo #" + String.valueOf(i));
                Logger.getLogger(RepoManager.class.getName()).log(Level.SEVERE, "Couldn't get that.", ex);
            }
        }
    }
    
    public static void gitClone(String REMOTE_URL, String FILE) throws GitAPIException {
        final File localPath = new File(FILE);
        Git.cloneRepository()
           .setURI(REMOTE_URL)
           .setDirectory(localPath)
           .setBranch(meta.getVersionName().toLowerCase())
           .call(); // jgit doesn't like this.
    }
}
