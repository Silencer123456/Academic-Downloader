import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ZipUrlExtractor {


    public void extractZipFromUrl(String url, String destination) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements zipFiles = doc.select("a[href~=.*wk.*.zip$]");
        for (Element zipPath : zipFiles) {
            String linkUrl = zipPath.attr("abs:href");
            System.out.println("Downloading " + linkUrl);
            byte[] bytes = Jsoup.connect(linkUrl)
                    .referrer(url)
                    .ignoreContentType(true)
                    .maxBodySize(0)
                    .timeout(1000000)
                    .execute()
                    .bodyAsBytes();

            Path dirPath = Paths.get(destination);
            Files.createDirectories(dirPath);

            String filename = zipPath.text();
            FileOutputStream fos = new FileOutputStream(dirPath.normalize().toString() + "/" + filename);
            fos.write(bytes);
            fos.close();

            System.out.println("File has been downloaded.");
        }
    }
}
