/*
 * XenLauncher
 * 
 * An open source Minecraft launcher. Work in progress.
 * Licenced under the GNU General Public License, version 2 - http://www.gnu.org/licenses/gpl-2.0.html
 * Created by Benjamin Gwynn (http://xenxier.tk)
 */
package xenlauncher;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;

/**
 * Make a Modpack for your face.
 * @author xenxier
 */
public class Modpack {
    
    private String repo_name;
    private String modpack_name;
    
    private String modpack_friendly_name;
    private String desc_short;
    private String desc;
    private String forge_version;
    private String minecraft_version;
    private double modpack_version;
    private boolean loaded;
    private boolean uses_forge;
    private List<String> info;
    private List<String> modlist;
    private List<String> configlist;
    private File path;
    
    private Exception valueNotFoundException;
    private Exception configNotFoundException;
    
    public void Modpack(String modpack_name, String repo_name) throws IOException, Exception {
        this.repo_name = repo_name;
        this.modpack_name = modpack_name;
        this.modpack_friendly_name = getValueFromMetaFile(getPathOfMeta("modpack_meta"), "name");
        this.desc_short = getValueFromMetaFile(getPathOfMeta("modpack_meta"), "short-description");
        this.desc = getValueFromMetaFile(getPathOfMeta("modpack_meta"), "description");
        this.loaded = false;
        try {
            this.forge_version = getValueFromMetaFile(getPathOfMeta("modpack_meta"), "forge-version");
            this.uses_forge = true;
        } catch (Exception ex) {
            Output.print('W',"This modpack is not using Minecraft Forge. Ignoring...");
            this.uses_forge = false;
        }
        this.minecraft_version = getValueFromMetaFile(getPathOfMeta("modpack_meta"), "minecraft-version");
        this.modpack_version = Double.parseDouble(getValueFromMetaFile(getPathOfMeta("modpack_meta"), "modpack-version"));
        this.info = readMetaFile(getPathOfMeta("modpack_info"));
        this.modlist = readMetaFile(getPathOfMeta("mod_list"));
        this.configlist = readMetaFile(getPathOfMeta("config_list"));
        this.path = new File("modpacks/" + repo_name + "/" + modpack_name + "/");
    }
    
    void launch() throws IOException {
        // WIP
        String jvm;
        if (System.getProperty("os.name").startsWith("Win")) {
            jvm = System.getProperties().getProperty("java.home") + File.pathSeparator + "bin" + File.pathSeparator + "java.exe";
        } else {
            jvm = System.getProperties().getProperty("java.home") + File.pathSeparator + "bin" + File.pathSeparator + "java";
        }
        String jvm_args = buildLaunchArguments();
        Process p = new ProcessBuilder(jvm, jvm_args).start();
        OutputStream output = p.getOutputStream();
    }
    
    private String buildLaunchArguments() {
        // WIP
        int alloc_mem = 1024;
        String lib_path = "natives_";
        String lib_jars = null;
        String mc_jar = null;
        String mc_class = "net.minecraft.client.main.Main";
        return "-Xmx" + alloc_mem + "M -Djava.library.path=" + lib_path + " -cp " + lib_jars + ":" + mc_jar + " " + mc_class;
    }
    
    void installConfigs() throws IOException, Exception {
        for (int i = 0; i < configlist.size(); i++) {
            installConfig(configlist.get(i));
        }
    }
    
    void downloadMods() throws IOException, MalformedURLException {
        for (int i = 0; i < modlist.size(); i++) {
            downloadMod(modlist.get(i));
        }
    }
    
    void downloadForge() throws IOException, MalformedURLException {
        // http://files.minecraftforge.net/maven/net/minecraftforge/forge/1.7.2-10.12.2.1121/forge-1.7.2-10.12.2.1121-installer.jar
        String combined_version = this.minecraft_version + "-" + this.forge_version;
        downloadFile("http://files.minecraftforge.net/maven/net/minecraftforge/forge/" + combined_version + "/forge-" + combined_version + "-installer.jar", ".temp/forge-installer.jar");
        // forge-1.7.2-10.12.21121-universal.jar.
        extractFromZip("forge-" + combined_version + "-universal.jar", ".temp/forge-installer.jar");
        FileUtils.copyFile(new File("forge-" + combined_version + "-universal.jar"), new File("libraries/" + this.minecraft_version + "/forge-" + combined_version + "-universal.jar"));
    }
    
    void downloadLibs() {
        // TODO
    }
    
    void downloadNatives() {
        // TODO
    }
    
    void downloadMinecraft() {
        // TODO
    }
    
    protected void downloadFile(String download_from, String download_to) throws MalformedURLException, IOException {
        FileUtils.copyURLToFile(new URL(download_from), new File(this.path + download_to));
    }
    
    protected void extractFromZip(String file, String zip) throws FileNotFoundException, IOException {
        // http://stackoverflow.com/questions/5484158/what-is-the-fastest-way-to-extract-1-file-from-a-zip-file-which-contain-a-lot-of
        OutputStream out = new FileOutputStream(file);
        FileInputStream fin = new FileInputStream(zip);
        BufferedInputStream bin = new BufferedInputStream(fin);
        ZipInputStream zin = new ZipInputStream(bin);
        ZipEntry ze = null;
        while ((ze = zin.getNextEntry()) != null) {
            if (ze.getName().equals(file)) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = zin.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.close();
                break;
            }
        }
    }
    
    private void installConfig(String config) throws IOException, Exception {
        File file = new File("repositories/" + this.repo_name + "/mod_configs/" + config);
        if (file.isFile()) {
            Output.print('I', "Installing configuration file...");
            FileUtils.copyFile(file, new File(this.path + "config/" + config));
        } else if (file.isDirectory()) {
            Output.print('I', "Installing configuration folder...");
            FileUtils.copyDirectory(file, new File(this.path + "config/" + config));
        } else {
            throw configNotFoundException;
        }
    }
    
    private void downloadMod(String mod) throws IOException, MalformedURLException {
        List<String> files_to_download = FileUtils.readLines(new File("repositories/" + this.repo_name + "/mods" + mod + "/download.meta"), "utf-8");
        for (int i = 0; i < files_to_download.size(); i++) {
            if (files_to_download.size() == 1) {
                downloadFile(files_to_download.get(i), this.path + "mods/" + mod + ".jar");
            } else {
                downloadFile(files_to_download.get(i), this.path + "mods/" + mod + "_" + i + ".jar");
            }
        }
    }
    
    private String getPathOfMeta(String file) {
        return ("repositories/" + this.repo_name +"/modpacks/" + this.modpack_name + "/" + file + ".meta");
    }
    
    private List<String> readMetaFile(String path) throws IOException {
        return FileUtils.readLines(new File(path), "utf-8");
    }
    
    private String getValueFromMetaFile(String file, String value) throws IOException, Exception {
        List<String> lines = FileUtils.readLines(new File(getPathOfMeta(file)), "utf-8");
        String prefix = value + ":";
        for (int i = 0; i < lines.size(); i++) {
            if (!lines.get(i).startsWith("#")) { // If the line is not a comment.
                if (lines.get(i).startsWith(prefix)) {
                    return lines.get(i).replaceAll(prefix, ""); // variable:value
                } else if (lines.get(i).startsWith(prefix + " ")) {
                    return lines.get(i).replaceAll(prefix + " ", ""); // variable: value
                }
            }
        }
        throw valueNotFoundException;
    }
    
    String getModpackName() {
        return this.modpack_friendly_name;
    }
    
    String getShortDescription() {
        return this.desc_short;
    }
    
    String getLongDescription() {
        return this.desc;
    }
    
    Double getVersion() {
        return this.modpack_version;
    }
}
