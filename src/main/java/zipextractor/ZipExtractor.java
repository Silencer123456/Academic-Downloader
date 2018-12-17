package zipextractor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipExtractor {
    public void extractDirectory(String pathToDir, String extractedPath) throws IOException {
        File dir = new File(pathToDir);
        if (dir.isFile()) {
            System.err.println("The path " + pathToDir + " is not a directory.");
            return;
        }

        String[] extensions = new String[] { "zip" };
        String dirName = dir.getName();
        List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);

        for (File file : files) {
            String s = file.getParent();
            s = s.substring(s.lastIndexOf(dirName));
            s = extractedPath + s + "/" + FilenameUtils.getBaseName(file.getName()) + "/";

            System.out.println(s);
            extractFile(file.getCanonicalPath(), s);
        }
    }

    private void extractFile(String zipPath, String dirPath) throws IOException {
        ZipFile zipFile = new ZipFile(zipPath);
        Enumeration<?> enu = zipFile.entries();
        while (enu.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) enu.nextElement();

            String name = zipEntry.getName();
            long size = zipEntry.getSize();
            long compressedSize = zipEntry.getCompressedSize();
            System.out.printf("name: %-20s | size: %6d | compressed size: %6d\n",
                    name, size, compressedSize);

            File file = new File(dirPath + name);
            if (name.endsWith("/")) {
                file.mkdirs();
                continue;
            }

            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }

            InputStream is = zipFile.getInputStream(zipEntry);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = is.read(bytes)) >= 0) {
                fos.write(bytes, 0, length);
            }
            is.close();
            fos.close();

        }
        zipFile.close();
    }
}
