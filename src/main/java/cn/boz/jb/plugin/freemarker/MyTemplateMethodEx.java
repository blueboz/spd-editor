package cn.boz.jb.plugin.freemarker;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class MyTemplateMethodEx implements TemplateMethodModelEx {
    @Override
    public Object exec(List arguments) throws TemplateModelException {
        String fun =  arguments.get(0).toString();
        if("upper_case_first".equals(fun)){
            String firstWord =  arguments.get(1).toString();
            return new SimpleScalar(firstCharUpper(firstWord));
        }
        if("exception_name".equals(fun)){
            String firstWord = arguments.get(1).toString();
            return "Xfunds"+firstCharUpper(firstWord)+"Exception";

        }
        return new SimpleScalar("");


    }
    public String firstCharUpper(String str){
        return str.substring(0,1).toUpperCase()+str.substring(1);
    }
}
