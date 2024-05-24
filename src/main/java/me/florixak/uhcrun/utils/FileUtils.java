package me.florixak.uhcrun.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.florixak.uhcrun.UHCRun;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils{

    private static final String API_URL = "https://paste.md-5.net/documents";
    private static final String PASTE_URL_DOMAIN = "https://paste.md-5.net/";

    public static File getResourceFile(JavaPlugin plugin, String fileName) {
        return getResourceFile(plugin, fileName, fileName);
    }

    public static File getResourceFile(JavaPlugin plugin, String fileName, String sourceName) {
        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()){
            // save resource
            plugin.saveResource(sourceName, false);
        }

        if (!fileName.equals(sourceName)){
            File sourceFile = new File(plugin.getDataFolder(), sourceName);
            sourceFile.renameTo(file);
        }

        if (!file.exists()){
            Bukkit.getLogger().severe("Failed to save file: " + fileName);
        }

        return file;
    }

    /**
     * Method used to upload text files to paste bin.
     * @param builder StringBuilder containing the text you want to be uploaded.
     * @return Returns the URL of the uploaded text.
     * @throws IOException Thrown when uploading fails.
     */
    public static String uploadTextFile(StringBuilder builder) throws IOException{
        String data = builder.toString();

        HttpsURLConnection connection = (HttpsURLConnection) new URL(API_URL).openConnection();

        // Add headers
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Accept", "application/json");
        connection.addRequestProperty("Content-Length", String.valueOf(data.length()));
        connection.setRequestProperty("Content-Type", "text/plain");
        connection.setRequestProperty("User-Agent", "UhcCore:"+ UHCRun.getInstance().getDescription().getVersion());

        // Send data
        connection.setDoOutput(true);
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.write(data.getBytes());
        outputStream.flush();
        outputStream.close();

        InputStream inputStream = connection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        JsonObject json = new JsonParser().parse(bufferedReader.readLine()).getAsJsonObject();

        bufferedReader.close();
        connection.disconnect();

        return PASTE_URL_DOMAIN + json.get("key").getAsString();
    }

    /**
     * Returns a list of child files
     * @param dir Directory child files are returned for
     * @param deep When true files in child directories also get returned
     * @return List of files
     */
    public static List<File> getDirFiles(File dir, boolean deep){
        List<File> files = new ArrayList<>();

        for (File file : dir.listFiles()){
            if (file.isDirectory()){
                if (deep){
                    files.addAll(getDirFiles(file, true));
                }
            }else{
                files.add(file);
            }
        }

        return files;
    }

    /**
     * Deletes file, in case of a directory all child files and directories are deleted
     * @param file File to delete
     * @return Returns true if file was deleted successfully
     */
    public static boolean deleteFile(File file) {
        if(file == null){
            return false;
        }

        if (file.isFile()) {
            return file.delete();
        }

        if (!file.isDirectory()) {
            return false;
        }

        File[] flist = file.listFiles();

        if (flist != null && flist.length > 0) {
            for (File f : flist) {
                if (!deleteFile(f)) {
                    return false;
                }
            }
        }

        return file.delete();
    }

    /**
     * Downloads a file from the internet
     * @param url Url of the file / api
     * @param path Path do the destination of the file
     * @throws IOException Thrown when file fails to download
     */
    public static void downloadFile(URL url, File path) throws IOException{
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.connect();

        InputStream in = connection.getInputStream();

        Files.copy(in, Paths.get(path.toURI()));

        in.close();
        connection.disconnect();
    }

    /**
     * Unzips zip file
     * @param zipFile Zip file
     * @param dir Directory to place unzipped files
     * @throws IOException Thrown when unzipping fails
     */
    public static void unzip(ZipFile zipFile, File dir) throws IOException{
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            File zipChild = new File(dir, entry.getName());

            if (entry.isDirectory()){
                zipChild.mkdirs();
            }else{
                zipChild.getParentFile().mkdirs();
                InputStream in = zipFile.getInputStream(entry);
                Files.copy(in, Paths.get(zipChild.toURI()));
                in.close();
            }
        }

        zipFile.close();
    }

}
