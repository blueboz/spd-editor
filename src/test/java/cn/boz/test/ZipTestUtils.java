package cn.boz.test;

import cn.boz.jb.plugin.idea.utils.ZipUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class ZipTestUtils {

    @Test
    public void test() throws IOException {
        String basepath="F:\\Code\\IDEA\\spd-editor\\src\\test\\java\\cn\\boz\\test\\";
        String[] filenames=new String[]{"Colorful.java","ContentLoade.java","Dom4jTester.java","swing\\FilteredList.java"};
        File[] fs=new File[filenames.length];
        for (int i = 0; i < filenames.length; i++) {
            String filename = filenames[i];
            File file = new File(basepath, filename);
            fs[i]=file;
        }
        File file = new File("F:\\Code\\IDEA\\spd-editor\\src\\test\\java\\cn\\boz\\test\\", "ggout.zip");

        file.createNewFile();
        //相对基础目录是相对于git 项目的基准目录
        ZipUtils.zipFiles(fs,file,"F:\\Code\\IDEA\\spd-editor");

    }
}
