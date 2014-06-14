/*
 * Work-in-progress.
 */

package xenlauncher;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xenxier
 */
public class XenLauncher {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Output.print('I', "Welcome to XenLauncher Version " + meta.getVersionNumber());
        Output.print('I', "Reading Repository Database from file...");
        try {
            RepositoryDatabase.readListFromFile();
        } catch (IOException ex) {
            Output.print('W', "IOException, maybe the file doesn't exist? Attempting to create...");
            RepositoryDatabase.createDatabase();
        } catch (ClassNotFoundException ex) {
            Output.error("Unexpected Java ClassNotFoundException(), did XenLauncher compile correctly?", ex);
        }
        finally {
            try {
                RepositoryDatabase.refreshAllRepos();
            } catch (Exception ex) { // TODO add specific catches
                Output.error("Coudln't refresh database. (Java Vauge Exception)", ex);
            }
        }
        Output.print('I', "Creating GUI...");
        LauncherGUI.main(args);
    }
    
}
