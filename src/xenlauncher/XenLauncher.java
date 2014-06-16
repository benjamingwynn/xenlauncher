/*
 * Work-in-progress.
 */

package xenlauncher;

import java.io.IOException;

/**
 *
 * @author xenxier
 */
public class XenLauncher {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Output.print('I', "Welcome to XenLauncher " + meta.getFullVersionInformation());
        if (args.length > 0) {
            Output.print('W', "Command line arguments not yet supported. Continuing to open GUI.");
        }
        Output.print('I', "Reading Repository Database from file...");
        try {
            RepositoryDatabase.readListFromFile();
            RepositoryDatabase.refreshAllRepos();
        } catch (IOException ex) {
            Output.print('W', "IOException, maybe the file doesn't exist? Attempting to create...");
            RepositoryDatabase.createDatabase();
        } catch (ClassNotFoundException ex) {
            Output.error("Unexpected Java ClassNotFoundException(), did XenLauncher compile correctly?", ex);
        } catch (Exception ex) {
            Output.error("Failed to refesh Database. Vague Java Exception.", ex);
        }
        Output.print('I', "Creating GUI...");
        LauncherGUI.main(args);
    }
    
}
