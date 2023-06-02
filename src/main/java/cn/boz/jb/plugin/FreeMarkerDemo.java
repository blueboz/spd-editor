package cn.boz.jb.plugin;

import cn.boz.jb.plugin.freemarker.MyTemplateMethodEx;
import com.alibaba.fastjson.JSON;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class FreeMarkerDemo {
    private String outputDest = "F:/Code/FMS/FMSS_xfunds";

    private String configPath = "F:/Code/spd-editor/src/main/resources/config.json";
    private String basePath = "F:/Code/spd-editor/src/main/resources/templates";

    Set<String> skipList = Arrays.stream(new String[]{"fundWhiteList.xls"}).collect(Collectors.toSet());
//    Map<String,String> namespacePoBaseMapper =new HashMap<String,String>();



    HashMap mapper;
    private Configuration templateCgr = null;

    private StringTemplateLoader stringTemplateLoader = null;
    private Configuration strTemplateCfg = null;

    public FreeMarkerDemo() throws TemplateException, IOException {
//        namespacePoBaseMapper.put("base","com.erayt.xfunds.base.domain.EngineRightPo");
//        namespacePoBaseMapper.put("pawn","com.erayt.xfunds.pawn.domain.PawnTrade");
//        namespacePoBaseMapper.put("forex","com.erayt.xfunds.forex.domain.ForexBaseDomain");
//        namespacePoBaseMapper.put("ndf","com.erayt.xfunds.ndf.domain.NdfBaseDomain");
//        namespacePoBaseMapper.put("fund","com.erayt.xfunds.fund.domain.FundBaseDomain");
//        namespacePoBaseMapper.put("option","com.erayt.xfunds.option.domain.OptionBaseDomain");
//        namespacePoBaseMapper.put("control","com.erayt.xfunds.control.domain.AML");
//        namespacePoBaseMapper.put("intbank","com.erayt.xfunds.intbank.domain.IntBankBaseDomain");

        this.init();
        this.initStringTemplate();
    }

    public void initStringTemplate() {

        // 创建 Configuration 对象
        strTemplateCfg = new Configuration(Configuration.VERSION_2_3_30);

        stringTemplateLoader = new StringTemplateLoader();
        strTemplateCfg.setTemplateLoader(stringTemplateLoader);


    }

    public String converPath(String pathOrg) {
        try {
            String string = UUID.randomUUID().toString();
            stringTemplateLoader.putTemplate(string, pathOrg);
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
            boolean contains = skipList.contains(Path.of(path).getFileName());
            if (contains) {
                System.out.println("skip" + path);
                return;
            }
            path = path.replaceAll("\\\\", "/");
            String relPath = path.replace(basePath, "");
            if (templateCgr == null) {
                return;
            }
            Template template = templateCgr.getTemplate(relPath);
            relPath = converPath(relPath);
            Path joinPath = Paths.get(outputDest, relPath);

            Path parent = joinPath.getParent();
            if (!Files.isDirectory(parent)) {
                Files.createDirectories(parent);
            }
            if(Files.isRegularFile(joinPath)){
                Files.delete(joinPath);
            }
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(joinPath, StandardOpenOption.CREATE)) {
                template.process(mapper, bufferedWriter);
                bufferedWriter.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() throws IOException, TemplateException {
        byte[] bytes = Files.readAllBytes(Path.of(configPath));
        mapper = JSON.parseObject(new String(bytes), HashMap.class);
//        String namespace = (String) mapper.get("namespace");
//        if(namespacePoBaseMapper.containsKey(namespace)){
//            String classFullName = namespacePoBaseMapper.get(namespace);
//            mapper.put("pojobaseclass",classFullName);
//            String className=classFullName.substring(classFullName.lastIndexOf(".")+1);
//            mapper.put("pojobaseclassname",className);
//        }else{
//            String classFullName = String.format("com.erayt.xfunds.%s.domain.%sBaseDomain", namespace, firstCharUpper(namespace));
//            String className=classFullName.substring(classFullName.lastIndexOf(".")+1);
//            mapper.put("pojobaseclass",className);
//        }

        templateCgr = new Configuration(Configuration.VERSION_2_3_30);
        templateCgr.setSharedVariable("myutils",new MyTemplateMethodEx());
        templateCgr.setDirectoryForTemplateLoading(new File(basePath));
    }
    public String firstCharUpper(String str){
        return str.substring(0,1).toUpperCase()+str.substring(1);
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
        freeMarkerDemo.iterTemplatePath();
    }

    public void dirIterator() {

    }

}
