package cn.boz.jb.plugin;

import cn.boz.jb.plugin.freemarker.MyTemplateMethodEx;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FreeMarkerDemo {
//    private String outputDest = "F:/Code/FMS/FMSS_xfunds";
//    private String configPath = "F:/Code/spd-editor/src/main/resources/config.json";
//    private String basePath = "F:/Code/spd-editor/src/main/resources/templates";

    private String outputDest = "/home/@chenweidian-yfzx/Code/FMS/FMSS_xfunds";
    private String configPath = "/home/@chenweidian-yfzx/Code/spd-editor/src/main/resources/Nafm会员信息.json";
    private String templatePath = "/home/@chenweidian-yfzx/Code/spd-editor/src/main/resources/templates";

    Set<String> skipList = Arrays.stream(new String[]{""}).collect(Collectors.toSet());
//    Map<String,String> namespacePoBaseMapper =new HashMap<String,String>();

    JSONObject mapper;
    private Configuration templateCgr = null;

    private StringTemplateLoader stringTemplateLoader = null;
    private Configuration strTemplateCfg = null;

    public FreeMarkerDemo() throws TemplateException, IOException {
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
    private boolean deletemode=false;

    public boolean isDeletemode() {
        return deletemode;
    }

    public void setDeletemode(boolean deletemode) {
        this.deletemode = deletemode;
    }

    public void process(String path) {
        try {
            boolean contains = skipList.contains(Path.of(path).getFileName());
            if (contains) {
                System.out.println("skip" + path);
                return;
            }
            path = path.replaceAll("\\\\", "/");
            String relPath = path.replace(templatePath, "");
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
            if(deletemode){
                return ;
            }
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(joinPath, StandardOpenOption.CREATE)) {
                template.process(mapper, bufferedWriter);
                bufferedWriter.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    Pattern pattern = Pattern.compile("(\\w+)\\(([\\w,]+)\\)");

    public void init() throws IOException, TemplateException {
        byte[] bytes = Files.readAllBytes(Path.of(configPath));
        mapper = JSON.parseObject(new String(bytes));
        JSONArray columns = mapper.getJSONArray("columns");
        for (int i = 0; i < columns.size(); i++) {
            JSONObject column = columns.getJSONObject(i);
            String dataType = column.getString("dataType");
            Matcher matcher = pattern.matcher(dataType);
            if(matcher.matches()){
                String group1 = matcher.group(1);
                String group2 = matcher.group(2);
                String[] split = group2.split(",");

                String objType=null;
                String maxLenInOracle=null;
                if(group1.toUpperCase().indexOf("VARCHAR")!=-1){
                    objType="String";
                    maxLenInOracle=split[0];
                }else if(group1.toUpperCase().indexOf("NUMBER")!=-1){
                    if(split.length==2){
                        Integer prec = Integer.parseInt(split[0]);
                        Integer scale = Integer.parseInt(split[1]);
                        String maxVal = String.format("%s.%sd", "9".repeat(prec - scale), "9".repeat(scale));
//                        System.out.println(maxVal);
                        objType="Double";
                        maxLenInOracle=(prec+1)+"";
                        column.put("limit",maxVal);
                        column.put("digital",scale);
                    }else if(split.length==1){
                        Integer maxLength = Integer.parseInt(split[0]);
                        maxLenInOracle=split[0];
                        if(maxLength>9){
                            objType="Long";
                            //使用Long
                        }else{
                            //使用Int
                            objType="Integer";
                        }
                        //Int 9位最多
                    }
                }
                column.put("objType",objType);
                column.put("maxLenInOracle",maxLenInOracle);

            }
        }

        templateCgr = new Configuration(Configuration.VERSION_2_3_30);
        templateCgr.setSharedVariable("myutils",new MyTemplateMethodEx());
        templateCgr.setDirectoryForTemplateLoading(new File(templatePath));
    }
    public String firstCharUpper(String str){
        return str.substring(0,1).toUpperCase()+str.substring(1);
    }

    public void iterTemplatePath() {

        File file = new File(templatePath);
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
        freeMarkerDemo.setDeletemode(false);
        freeMarkerDemo.iterTemplatePath();




    }

}
