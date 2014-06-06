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

import java.util.ArrayList;

public class LaunchGame {
    public void LaunchGameEvent(String username) {
        Output.print('I', "Attempting to launch...");
        if (!testUsername(username)) {
            Output.print('E', "Invalid Username.");
            new LauncherGUI().newInfoText("Bad username.");
        }
    }
    
    private static boolean testUsername(String username) {
        ArrayList badContain = new ArrayList();
        badContain.add('!');
        badContain.add(' ');
        badContain.add('@');
        badContain.add('"');
        badContain.add('£');
        badContain.add('/');
        badContain.add('\'');
        badContain.add('\\');
        badContain.add('[');
        badContain.add(']');
        badContain.add('$');
        badContain.add('%');
        badContain.add('^');
        badContain.add('*');
        badContain.add('(');
        badContain.add(')');
        badContain.add('{');
        badContain.add('}');
        badContain.add(';');
        badContain.add(':');
        badContain.add(',');
        badContain.add('.');
        badContain.add('=');
        badContain.add('+');
        badContain.add('?');
        badContain.add('~');
        badContain.add('#');
        badContain.add('|');
        badContain.add('`');
        badContain.add('¬');
        badContain.add('>');
        badContain.add('<');
        for (int i = 0; i < badContain.size(); i++) {
            if (username.contains(String.valueOf(badContain.get(i)))) {
               return false; 
            }
        }
        return true;
    }
}
