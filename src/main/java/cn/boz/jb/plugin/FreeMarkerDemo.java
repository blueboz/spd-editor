package cn.boz.jb.plugin;

import com.alibaba.fastjson.JSON;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

public class FreeMarkerDemo {
    private String outputDest = "F://home//outputDest";

    private String configPath = "F://Code//spd-editor//src//main//resources//config.json";
    private String basePath = "F:/Code/spd-editor/src/main/resources/templates";

    HashMap mapper;
    private Configuration templateCgr = null;

    private StringTemplateLoader stringTemplateLoader=null;
    private Configuration strTemplateCfg=null;
    public void initStringTemplate(){

        // 创建 Configuration 对象
        strTemplateCfg  = new Configuration(Configuration.VERSION_2_3_30);

        stringTemplateLoader = new StringTemplateLoader();
        strTemplateCfg.setTemplateLoader(stringTemplateLoader);


    }

    public String converPath(String pathOrg){
        try {
//            Template template = strTemplateCfg.getTemplate(pathOrg,strTemplateCfg.getLocale(),"UTF-8",false,true);
//            if(template==null){
            String string = UUID.randomUUID().toString();
            stringTemplateLoader.putTemplate(string, pathOrg);
//            }
            Template template = strTemplateCfg.getTemplate(string);
            StringWriter out = new StringWriter();
            template.process(mapper, out);
            return out.toString();
        } catch (TemplateException e) {
            e.printStackTrace();
            return pathOrg;
        } catch (IOException e) {
            e.printStackTrace();
            return pathOrg;
        }
    }
    public void process(String path) {
        try {
            path = path.replaceAll("\\\\", "/");
            String relPath = path.replace(basePath, "");
            System.out.println(relPath);
            if (templateCgr == null) {
                return;
            }
            Template template = templateCgr.getTemplate(relPath);

            relPath = converPath(relPath);
            File file = new File(outputDest, relPath);
            file.mkdirs();
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));

            template.process(mapper, outputStreamWriter);
            outputStreamWriter.flush();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void init() throws IOException, TemplateException {
        byte[] bytes = Files.readAllBytes(Path.of(configPath));
        mapper = JSON.parseObject(new String(bytes), HashMap.class);
        templateCgr = new Configuration(Configuration.VERSION_2_3_30);
        templateCgr.setDirectoryForTemplateLoading(new File(basePath));
    }

    public void iterTemplatePath() {

        File file = new File(basePath);
        Deque<File> queue = new LinkedList<>();
        queue.add(file);
        while (!queue.isEmpty()) {
            File poll = queue.poll();
            if (poll.isFile()) {
                process(poll.getPath());
            } else {
                File[] files = poll.listFiles();
                for (File f : files) {
                    queue.add(f);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, TemplateException {

        FreeMarkerDemo freeMarkerDemo = new FreeMarkerDemo();
        freeMarkerDemo.init();
        freeMarkerDemo.initStringTemplate();
        freeMarkerDemo.iterTemplatePath();
//        freeMarkerDemo.process();
//        freeMarkerDemo.proc();

    }

    public void dirIterator() {

    }

}
