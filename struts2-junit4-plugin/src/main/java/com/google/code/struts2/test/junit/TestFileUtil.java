package com.google.code.struts2.test.junit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * A convenience/utility class that aids in locating resources such as .txt,
 * .xml, .properties files, etc.
 * 
 * @author rees.byars
 * 
 */
public class TestFileUtil {

    public static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * Returns a <code>Set</code> of file paths for files within the given root
     * package (including sub-packages of the given root package) in which the
     * file paths also include the given inner package name and end with the
     * given filename suffix.
     * <p>
     * For instance, given the following:
     * <p>
     * <code>
     * &nbsp; rootPackageName = "org.endogamous.oligarchy"<br>
     * &nbsp; innerPackageName = "chill.pill"<br>
     * &nbsp; fileNameSuffix = "context.xml"<br>
     * </code>
     * <p>
     * and a jar or class folder containing files with the paths:
     * <p>
     * &nbsp;&nbsp;
     * <tt>org/endogamous/oligarchy/laughers/chill/pill/heavens-context.xml</tt>
     * <p>
     * &nbsp; and
     * <p>
     * &nbsp;&nbsp;
     * <tt>org/endogamous/oligarchy/whiners/raw/deal/hells-context.xml</tt>
     * <p>
     * the returned <code>Set</code> will include the former as it begins with
     * the appropriate root package, contains the inner package, and has the
     * correct suffix. The latter is excluded as it does not contain the
     * required inner package, "chill.pill".
     * <p>
     * Null parameters may result cause a <code>NullPointerException</code>.
     * <p>
     * Tips and tricks:
     * <p>
     * <ul>
     * <li>
     * a <code>rootPackageName</code> of "" behaves differently in different
     * cases. In some cases (such as running your application in an IDE), a
     * <code>rootPackageName</code> of "" will cause all class folders and jars
     * to be searched. In other cases, such as in a WAR file on a WebLogic
     * server, all sub-directories of the server's home directory will be
     * searched (not the WAR file!). Just be aware.</li>
     * <li>
     * an <code>innerPackageName</code> of "" essentially means any inner
     * package is a match.</li>
     * <li>
     * a <code>fileNameSuffix</code> of "" essentially means any suffix is a
     * match.</li>
     * <li>
     * the <code>fileNameSuffix</code> can even include full filenames and path
     * information such as "chill/pill/heavens-context.xml" in order to be more
     * specific/restrictive.</li>
     * <li>
     * by setting the <code>fileNameSuffix</code> to ".class" or "Mapper.class"
     * or whatever, an instance of a <code>class</code> can be obtained by
     * <code>Class.forName(filePath.replace('/', '.'))</code></li>
     * </ul>
     * 
     * @param rootPackageName
     * @param innerPackageName
     * @param fileNameSuffix
     * @param callingClass
     * @return
     * @throws IOException
     */
    protected static Set<String> getFilePaths(String rootPackageName, String innerPackageName, String fileNameSuffix, Class<?> callingClass) throws IOException {

        Set<String> filePaths = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        filePaths = getFilePathsFromLoader(rootPackageName, innerPackageName, fileNameSuffix, loader);
        if (filePaths.size() == 0) {
            loader = callingClass.getClassLoader();
            filePaths = getFilePathsFromLoader(rootPackageName, innerPackageName, fileNameSuffix, loader);
            if (filePaths.size() == 0) {
                loader = TestFileUtil.class.getClassLoader();
                filePaths = getFilePathsFromLoader(rootPackageName, innerPackageName, fileNameSuffix, loader);
            }
        }
        return filePaths;
    }

    /**
     * 
     * See {@link #getFilePaths(String, String, String, Class)}
     * 
     * @param rootPackageName
     * @param innerPackageName
     * @param fileNameSuffix
     * @param loader
     * @return
     * @throws IOException
     */
    protected static Set<String> getFilePathsFromLoader(String rootPackageName, String innerPackageName, String fileNameSuffix, ClassLoader loader) throws IOException {

        Set<String> filePaths = new HashSet<String>();
        String path = rootPackageName.replace('.', '/');
        Enumeration<URL> resources = loader.getResources(path);
        if (resources == null) {
            resources = loader.getResources(path + '/');
        }
        if (resources != null) {
            while (resources.hasMoreElements()) {
                filePaths.addAll(getFilePathsFromURL(rootPackageName, innerPackageName, fileNameSuffix, resources.nextElement()));
            }
        }
        return filePaths;
    }

    /**
     * See {@link #getFilePaths(String, String, String, Class)}
     * 
     * @param rootPackageName
     * @param innerPackageName
     * @param fileNameSuffix
     * @param url
     * @return
     * @throws IOException
     */
    protected static Set<String> getFilePathsFromURL(String rootPackageName, String innerPackageName, String fileNameSuffix, URL url) throws IOException {

        String urlFileName = url.getFile();
        Set<String> filePaths = new HashSet<String>();
        if (urlFileName != null) {
            urlFileName = convertWindowsSpaces(urlFileName);
            if (isJarPathable(urlFileName)) {
                String jarPath = getJarPath(urlFileName);
                filePaths.addAll(getFilePathsFromJarFile(rootPackageName, innerPackageName, fileNameSuffix, jarPath));
            } else {
                filePaths.addAll(getFilePathsFromDirectory(rootPackageName, innerPackageName, fileNameSuffix, new File(urlFileName)));
            }
        }
        return filePaths;
    }

    /**
     * See {@link #getFilePaths(String, String, String, Class)}
     * 
     * @param packageName
     * @param innerPackageName
     * @param fileNameSuffix
     * @param directory
     * @return
     */
    protected static Set<String> getFilePathsFromDirectory(String packageName, String innerPackageName, String fileNameSuffix, File directory) {

        Set<String> filePaths = new HashSet<String>();
        if (directory.exists() && directory.list() != null) {
            for (String file : directory.list()) {
                if (file.endsWith(fileNameSuffix) && packageName.contains(innerPackageName)) {
                    String filePath = packageName.replace('.', '/');
                    filePaths.add(filePath + '/' + file);
                } else {
                    File subDirectory = new File(directory.getPath() + '/' + file);
                    String subPackageName = packageName + '.' + file;
                    filePaths.addAll(getFilePathsFromDirectory(subPackageName, innerPackageName, fileNameSuffix, subDirectory));
                }
            }
        }
        return filePaths;
    }

    /**
     * See {@link #getFilePaths(String, String, String, Class)}
     * 
     * @param rootPackageName
     * @param innerPackageName
     * @param fileNameSuffix
     * @param jarPath
     * @return
     * @throws IOException
     */
    protected static Set<String> getFilePathsFromJarFile(String rootPackageName, String innerPackageName, String fileNameSuffix, String jarPath) throws IOException {

        Set<String> filePaths = new HashSet<String>();
        InputStream is = null;
        JarInputStream jarFile = null;
        try {
            is = new FileInputStream(jarPath);
            jarFile = new JarInputStream(is);
            JarEntry jarEntry;
            while ((jarEntry = jarFile.getNextJarEntry()) != null) {
                String filePath = jarEntry.getName();
                if (filePath.endsWith(fileNameSuffix)) {
                    String compareName = filePath.replace('/', '.');
                    if (compareName.startsWith(rootPackageName) && compareName.contains(innerPackageName)) {
                        filePaths.add(filePath);
                    }
                }
            }
        } finally {
            if (is != null) {
                is.close();
                if (jarFile != null) {
                    jarFile.close();
                }
            }
        }
        return filePaths;
    }

    /**
     * Given a file path and encoding, returns the resource as a
     * <code>String</code>.
     * <p>
     * if <code>encoding</code> is <code>null</code>, UTF-8 is used.
     * 
     * @param filePath
     * @param encoding
     * @param callingClass
     * @return
     * @throws IOException
     */
    protected static String getFileAsString(String filePath, String encoding, Class<?> callingClass) throws IOException {

        if (encoding == null) {
            encoding = DEFAULT_ENCODING;
        }
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = getInputStream(filePath, callingClass);
            br = new BufferedReader(new InputStreamReader(is, encoding));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            sb.replace(sb.lastIndexOf("\n"), sb.length(), "");
        } finally {
            if (is != null) {
                is.close();
                if (br != null) {
                    br.close();
                }
            }
        }
        return sb.toString();
    }

    protected static String getFileAsString(String rootPackage, String innerPackage, String fileName, String encoding, Class<?> callingClass) throws IOException {
        Set<String> filePathCandidates = getFilePaths(rootPackage, innerPackage, fileName, callingClass);
        String fileAsString = null;
        for (String candidateFilePath : filePathCandidates) {
            if (candidateFilePath.substring(candidateFilePath.lastIndexOf("/") + 1).equals(fileName)) {
                fileAsString = getFileAsString(candidateFilePath, encoding, callingClass);
            }
        }
        return fileAsString;
    }

    protected static InputStream getInputStream(String filePath, Class<?> callingClass) {

        InputStream is = null;
        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        if (is == null) {
            is = callingClass.getClassLoader().getResourceAsStream(filePath);
            if (is == null) {
                is = TestFileUtil.class.getClassLoader().getResourceAsStream(filePath);
                if (is == null) {
                    if (filePath.startsWith("/")) {
                        filePath = filePath.substring(1);
                    } else {
                        filePath = "/" + filePath;
                    }
                    is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
                    if (is == null) {
                        is = callingClass.getClassLoader().getResourceAsStream(filePath);
                        if (is == null) {
                            is = TestFileUtil.class.getClassLoader().getResourceAsStream(filePath);
                        }
                    }
                }
            }
        }
        return is;
    }

    protected static boolean isJarPathable(String filePath) {
        return (filePath.indexOf("!") > 0) & (filePath.indexOf(".jar") > 0);
    }

    protected static String getJarPath(String filePath) {
        String jarPath = filePath.substring(0, filePath.indexOf("!")).substring(filePath.indexOf(":") + 1);
        if (jarPath.indexOf(":") >= 0) {
            jarPath = jarPath.substring(1);
        }
        return jarPath;
    }

    protected static String convertWindowsSpaces(String filePath) {
        if (filePath.indexOf("%20") > 0) {
            filePath = filePath.replaceAll("%20", " ");
        }
        return filePath;
    }

    protected static Set<File> getFiles(String rootPackage, String innerPackage, String fileSuffix, Class<?> callingClass) throws IOException {

        Set<String> filePathCandidates = getFilePaths(rootPackage, innerPackage, fileSuffix, callingClass);
        Set<File> files = new HashSet<File>();

        for (int i = 0; i < filePathCandidates.size(); i++) {
            InputStream in = null;
            OutputStream out = null;
            File file = null;
            String filePath = filePathCandidates.iterator().next();
            try {
                in = getInputStream(filePath, TestFileUtil.class);
                String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
                String prefix = fileName.substring(0, fileName.indexOf('.'));
                String suffix = fileName.substring(fileName.indexOf('.'));
                file = File.createTempFile(prefix, suffix);
                out = new FileOutputStream(file);
                byte buf[] = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                files.add(file);
            } catch (IOException ioe) {
                // catch and continue
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ioe) {
                    // catch and continue
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException ioe) {
                        // catch and continue
                    }

                }
            }
        }
        return files;
    }

    protected static File getFile(String rootPackage, String innerPackage, String fileName, Class<?> callingClass) throws IOException {
        File file = null;
        Set<File> fileCandidates = getFiles(rootPackage, innerPackage, fileName, callingClass);
        for (File candidateFile : fileCandidates) {
            if (candidateFile.getName().contains(fileName.substring(0, fileName.indexOf('.')))) {
                file = candidateFile;
                break;
            }
        }
        return file;
    }

    protected static byte[] getBytesFromFile(File file) throws IOException {

        InputStream is = null;
        byte[] bytes = null;

        try {

            is = new FileInputStream(file);

            // Get the size of the file
            long length = file.length();

            // You cannot create an array using a long type.
            // It needs to be an int type.
            // Before converting to an int type, check
            // to ensure that file is not larger than Integer.MAX_VALUE.
            if (length > Integer.MAX_VALUE) {
                // File is too large
                throw new IOException("file too large " + file.getName());
            }

            // Create the byte array to hold the data
            bytes = new byte[(int) length];

            // Read in the bytes
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            // Ensure all the bytes have been read in
            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }

        } finally {
            // Close the input stream and return bytes
            if (is != null) {
                is.close();
            }
        }

        return bytes;
    }

    public static String getFileAsString(String fileName) throws IOException {
        return getFileAsString("", "", fileName, DEFAULT_ENCODING, TestFileUtil.class);
    }

    public static File getFile(String fileName) throws IOException {
        return getFile("", "", fileName, TestFileUtil.class);
    }

    public static byte[] getFileBytes(String fileName) throws IOException {
        return getBytesFromFile(getFile(fileName));
    }
}
