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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;

public class RepositoryDatabase implements Serializable {
    
    private static List<List<String>> repository_list = new ArrayList<>();
    private static Exception repoNotFoundException;
    private static Exception repoRemoveFailException;
    private static final String database_file = "XenLauncher_Repository_Database.dat";
    
    public static void readListFromFile() throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(database_file);
        try (ObjectInputStream ois = new ObjectInputStream(fis)) {
            List<List<String>> loaded_repository_list = (List<List<String>>) ois.readObject();
            RepositoryDatabase.repository_list = loaded_repository_list;
        }
    }
    
    public static void writeListToFile() throws IOException {
        FileUtils.deleteQuietly(new File (database_file));
        FileOutputStream fos = new FileOutputStream(database_file);
        try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(repository_list);
        }
    }
    
    public static void addRepo(String repo_name, String repo_url) throws IOException, GitAPIException {
        Output.print('I', "Adding repository: '" + repo_name + "'...");
        RepoManager repo = new RepoManager();
        repo.register(getRepoPath(repo_name), repo_url);
        repo.cloneRepo();
        repo.trackRepoBranch((meta.getVersionName().toLowerCase()));
        repo.pullRepo();
        Output.print('I', "Adding new repository to the Repository Database...");
        ArrayList<String> new_repo_data = new ArrayList<>();
        new_repo_data.add(repo_name); // 0 - Repo Name
        new_repo_data.add(repo_url);  // 1 - Repo URL
        repository_list.add(new_repo_data);
        writeListToFile();
    }
    
    static void removeRepositoryFolderCatch(String repo_name) {
        Output.print('I', "Attempting to remove repo folder...");
        try {
            removeRepositoryFolder(repo_name);
        } catch (IOException ex) {
            Output.error("Cannot delete the repository directory. (Java IOException)", ex);
        }
    }
    
    private static void removeRepositoryFolder(String repo_name) throws IOException {
            FileUtils.deleteDirectory(new File (getRepoPath(repo_name)));
    }
    
    static void removeRepository(String repo_name) throws Exception {
        Output.print('I', "Attempting to remove repo '" + repo_name + "'...");
        if (repo_name.equals("XenLauncher Default Repository")) {
            Output.error("Request refused. Will not delete default repository.");
            throw repoRemoveFailException;
        }
        try {
            removeRepositoryFolder(repo_name);
        } catch (IOException ex) {
            Output.error("Cannot delete repo folder. (Java IOException)", ex);
            throw repoRemoveFailException;
        }
        Output.print('I', "Attempting to remove repo from database...");
        repository_list.remove(selectRepo(repo_name));
        Output.print('I', "Saving changes to database...");
        try {
            writeListToFile();
        } catch (IOException ex) {
            Output.error("Cannot save repository database changes. (Java IOException)", ex);
            throw repoRemoveFailException;
        }
        Output.print('I', "Repository deleted.");
    }
    
    public static int getRepoCount() {
        return repository_list.size();
    }
    
    public static void refreshAllRepos() throws Exception { // TODO add specific throws
        Output.print('I', "Refreshing all repos...");
        for (int i = 0; i < repository_list.size(); i++) {
            Output.print('I', "Refreshing repo: '" + repository_list.get(i).get(0) + "'...");
            RepoManager repo = new RepoManager();
            repo.register(getRepoPath(repository_list.get(i).get(0)), repository_list.get(i).get(1));
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
                    new LauncherGUI().setProgressBar(0);
                    String repo_url = repository_list.get(i).get(1);
                    new LauncherGUI().setProgressBar(25);
                    repo.register(repo_name, repo_url);
                    new LauncherGUI().setProgressBar(50);
                    repo.trackRepoBranch(meta.getVersionName().toLowerCase());
                    new LauncherGUI().setProgressBar(75);
                    repo.pullRepo();
                    new LauncherGUI().setProgressBar(100);
                    return;
                }
            }
        }
        throw repoNotFoundException;
    }
    
    static ArrayList getListOfRepoNames() {
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < repository_list.size(); i++) {
            names.add(repository_list.get(i).get(0));
        }
        return names;
    }
    
    static List<String> getRepositoryMeta(String repository_name, String requested) throws FileNotFoundException, IOException {
        List<String> lines = FileUtils.readLines(new File(getRepoPath(repository_name) + "/meta/" + requested + ".meta"), "utf-8");
        return lines;
    }
    
    static String getRepoPath(String repository_name) {
        return "repositories/" + repository_name;
    }
    
    static int selectRepo(String repository_name) {
        for (int i = 0; i < repository_list.size(); i++) {
            if ((repository_list.get(i).get(0)) == null ? repository_name == null : (repository_list.get(i).get(0)).equals(repository_name)) {
                return i;
            }
        }
        return 0;
    }
    
    static String selectRepo(int repository_number) {
        return repository_list.get(repository_number).get(0);
    }

    static void createDatabase() {
        Output.print('I', "Creating Repository Database...");
        Output.print('I', "Adding default repository...");
        try {
            addRepo("XenLauncher Default Repository", "git@github.com:benjamingwynn/xenlauncher-repo.git");
        } catch (IOException | GitAPIException ex) {
            Logger.getLogger(RepositoryDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        Output.print('I', "Writing database to disk...");
        try {
            writeListToFile();
        } catch (IOException ex) {
            Output.error("Coudln't write database to disk. Maybe it's write protected? (Java IOException)", ex);
        }
    }
    
    public static boolean isDatabaseCreated() {
        return !(repository_list.isEmpty());
    }
}
