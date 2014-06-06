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

public class FileManager {
    static String workingDirectory;
    
    private static String getDefaultWorkingDirectory() {
        return ".xenlauncher/";
    }
    
    public static String getWorkingDirectory() {
        return workingDirectory;
    }
    
    public static void setWorkingDirectory(String dir) {
        workingDirectory = dir;
    }
    
    public static boolean isWorkingDirectoryDefault() {
        return getDefaultWorkingDirectory().matches(workingDirectory);
    }
    
    public static void setWorkingDirectoryToDefault() {
        setWorkingDirectory(getDefaultWorkingDirectory());
    }
    
    public static String getRepoDirectory(int reponumber) {
        return getWorkingDirectory() + "repos/repo_" + reponumber;
    }
}
