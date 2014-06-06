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

public class meta {
    public static double getVersionNumber() {
        return 0.1;
    }
    
    public static int getBuildNumber() {
        return 1;
    }
    
    public static String getVersionName() {
        return "Hydrogen";
    }
    
    public static boolean isDevVersion() {
        return true;
    }
    
    public static String getFullVersionInformation() {
        String i = " - ";
        if (!isDevVersion()) {
            i = "dev - ";
        }
        return "Version " + String.valueOf(getVersionNumber()) + "." + String.valueOf(getBuildNumber()) + i + getVersionName();
    }
}
