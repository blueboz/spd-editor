package cn.boz.test;

import org.testng.annotations.Test;

import java.io.File;

public class PathTest {

    @Test
    public void test() {

        System.out.println("--------------------");
        File file = new File("D:/cdadmin/UFSM/FMSS/save/FMTM/");
        System.out.println(file.exists());
        File file2 = new File("D:/cdadmin1/UFSM2/FMSS3/save4/FMTM5/");
        System.out.println(file2.exists());
        boolean mkdirs = file2.mkdirs();
        System.out.println(mkdirs);


    }
}
