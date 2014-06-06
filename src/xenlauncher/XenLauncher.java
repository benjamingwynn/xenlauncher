/*
 * Work-in-progress.
 */

package xenlauncher;

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
        Output.print('I', "tempset: setWorkingDirectoryToDefault()");
        FileManager.setWorkingDirectoryToDefault();
        LauncherGUI.main(args);
    }
    
}
