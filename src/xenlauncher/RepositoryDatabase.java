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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.RefNotFoundException;

public class RepositoryDatabase implements Serializable {
    
    private static List<List<String>> repository_list = new ArrayList<>();
    private static Exception repoNotFoundException;
    private static final String database_file = "XenLauncher_Repository_Database.dat";
    
    public static void readListFromFile() throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(database_file);
        try (ObjectInputStream ois = new ObjectInputStream(fis)) {
            List<List<String>> loaded_repository_list = (List<List<String>>) ois.readObject();
            RepositoryDatabase.repository_list = loaded_repository_list;
        }
    }
    
    public static void writeListToFile() throws IOException {
        FileOutputStream fos = new FileOutputStream(database_file);
        try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(repository_list);
        }
    }
    
    public static void addRepo(String repo_name, String repo_url) {
        try {
            Output.print('I', "Adding repository: '" + repo_name + "'...");
            RepoManager repo = new RepoManager();
            repo.register(repo_name, repo_url);
            repo.cloneRepo();
            repo.trackRepoBranch((meta.getVersionName().toLowerCase()));
            repo.pullRepo();
        } catch (IOException ex) {
            Output.error("Cannot create the repo. Maybe you don't have permission to write here? (Java IOException)", ex);
        } catch (JGitInternalException ex) {
            Output.error("Cannot create the repo. Maybe the repo already exists but XenLauncher didn't see it? (Java JGitInternelException)", ex);
        } catch (RefNotFoundException ex) {
            Output.error("Cannot create the repo. Reference not found. Outdated repo or typo. (Java RefNotFoundException)", ex);
        } catch (InvalidRefNameException ex) {
            Output.error("Cannot create the repo. Not a Git repo. (Java InvalidRefNameException)", ex);
        } catch (GitAPIException ex) {
            Output.error("Cannot create the repo. API Error. Bad Git repo? (Java GitAPIException)", ex);
        }
        finally {
            Output.print('I', "Adding new repository to the Repository Database...");
            ArrayList<String> new_repo_data = new ArrayList<>();
            new_repo_data.add(repo_name); // 0 - Repo Name
            new_repo_data.add(repo_url);  // 1 - Repo URL
            repository_list.add(new_repo_data);
        }
    }
    
    public static int getRepoCount() {
        return repository_list.size();
    }
    
    public static void refreshAllRepos() throws Exception { // TODO add specific throws
        Output.print('I', "Refreshing all repos...");
        for (int i = 0; i < repository_list.size(); i++) {
            Output.print('I', "Refreshing repo: '" + repository_list.get(i).get(0) + "'...");
            RepoManager repo = new RepoManager();
            repo.register(repository_list.get(i).get(0), repository_list.get(i).get(1));
            repo.trackRepoBranch(meta.getVersionName().toLowerCase());
            repo.pullRepo();
        }
    }
    
    public static void refreshRepo(String repo_name) throws Exception { // TODO add specific throws
        Output.print('I', "Attempting to refresh '" + repo_name + "'...");
        for (int i = 0; i < repository_list.size(); i++) {
            for (String listed_name : repository_list.get(i)) {
                if (repo_name.equals(listed_name)) {
                    RepoManager repo = new RepoManager();
                    // TEMP: URL may not always be second place (count from 0) in the list.
                    String repo_url = repository_list.get(i).get(1);
                    repo.register(repo_name, repo_url);
                    repo.trackRepoBranch(meta.getVersionName().toLowerCase());
                    repo.pullRepo();
                    return;
                }
            }
        }
        throw repoNotFoundException;
    }

    static void createDatabase() {
        Output.print('I', "Creating Repository Database...");
        Output.print('I', "Adding default repository...");
        addRepo("XenLauncher Default Repository", "git@github.com:benjamingwynn/xenlauncher-repo.git");
        Output.print('I', "Writing database to disk...");
        try {
            writeListToFile();
        } catch (IOException ex) {
            Output.error("Coudln't write database to disk. Maybe it's write protected? (Java IOException)", ex);
        }
    }
}
