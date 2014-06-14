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

import java.util.logging.Level;
import java.util.logging.Logger;

public class Output {
    public static void print(char type, String text) {
        System.out.println("XenLauncher " + String.valueOf(meta.getVersionNumber()) + ": [" + type + "] " + text);
    }
    
    public static void error(String msg) {
        print('E', msg + " - No trace.");
        new LauncherGUI().showError(msg);
    }
    
    public static void error(String msg, Exception ex) {
        new LauncherGUI().showError(msg);
        print('E', msg + " - Tracing...");
        Logger.getLogger(LauncherGUI.class.getName()).log(Level.SEVERE, null, ex);
    }
}
