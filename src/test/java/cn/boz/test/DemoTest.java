package cn.boz.test;

import org.testng.annotations.Test;

import java.util.Locale;

public class DemoTest {
    @Test
    public void test() {
        String input = "FundInfoIfaceServiceImpl";
        System.out.println(tranToSpringBeanName(input));

    }
    public String tranToSpringBeanName(String input){
        if(input==null){
            return "";
        }
        if("".equals(input.trim())){
            return "";
        }
        if(input.length()==1){
            return input.toLowerCase();
        }
        String prefix = input.substring(0, 1);
        String suffix = input.substring(1);
        System.out.println(prefix);
        System.out.println(suffix);
        return prefix.toLowerCase()+suffix;
    }
}

