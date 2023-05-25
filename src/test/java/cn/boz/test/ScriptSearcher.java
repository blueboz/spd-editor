package cn.boz.test;


import org.testng.annotations.Test;

public class ScriptSearcher {

    @Test
    public void testRunScript() {
        String script = "rateMountPrev=rateMountPrevRepository.findRateMountPrevInfo(rateMountPrev)";
        if (script.contains("=")) {
            script = script.split("=")[1];
        }
        if(script.contains("(")){
            script = script.split("\\(")[0];
        }
        System.out.println(script);

    }
}
