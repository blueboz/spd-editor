package cn.boz.jb.plugin;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DemoTest {
    public static void main(String[] args) {
        String originPath = "/home/@chenweidian-yfzx/Code/FMS/FMSS_xfunds/xfunds/WebContent/js/fund/fundSubscribeInput.js";
        String targetBase="/cygdrive/d/app/xfunds.ear/xfunds.war/";
        String identify = "/WebContent/";
        Path origin = Paths.get(originPath);
        if (originPath.indexOf(identify) != -1) {
            String relPath = originPath.substring(originPath.indexOf(identify) + identify.length() );
            Path path = Paths.get(targetBase, relPath);
        }

    }
}
