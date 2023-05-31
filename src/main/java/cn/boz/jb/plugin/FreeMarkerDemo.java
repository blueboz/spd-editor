package cn.boz.jb.plugin;

import com.alibaba.fastjson.JSON;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class FreeMarkerDemo {
    public static void main(String[] args) throws IOException, TemplateException {
        byte[] bytes = Files.readAllBytes(Path.of("F:\\Code\\spd-editor\\src\\main\\resources\\config.json"));
        HashMap hashMap = JSON.parseObject(new String(bytes), HashMap.class);
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
        configuration.setClassForTemplateLoading(FreeMarkerDemo.class, "/templates");
//        Template template = configuration.getTemplate("BaseInput.html");

        Template template = configuration.getTemplate("baseMod.js");
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(System.out);
        template.process(hashMap,outputStreamWriter);
        outputStreamWriter.flush();

    }
}
