package cn.boz.jb.plugin.idea.utils;

import cn.boz.jb.plugin.ReadTgz;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TemplateExtractor {

    public static void extractTarGz(String outputDirPath) {
        try (InputStream fis = ReadTgz.class.getClassLoader().getResourceAsStream("templates.tgz");
             GzipCompressorInputStream gzis = new GzipCompressorInputStream(fis);) {
            TarArchiveInputStream taris = new TarArchiveInputStream(gzis);
            TarArchiveEntry entry;
            while ((entry = taris.getNextTarEntry()) != null) {
                if (!entry.isDirectory()) {
                    File outputFile = new File(outputDirPath, entry.getName());
                    createParentDirectories(outputFile);

                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = taris.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createParentDirectories(File file) {
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
    }
}
