package cn.boz.jb.plugin.idea.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

public class FreeMarkerUtils {

    private static final FreeMarkerUtils INST = new FreeMarkerUtils();

    public static FreeMarkerUtils getINST() {
        return INST;
    }

    Configuration configuration;

    private FreeMarkerUtils() {

        // 创建 Configuration 对象
        configuration = new Configuration(Configuration.VERSION_2_3_30);
        configuration.setClassForTemplateLoading(FreeMarkerUtils.class, "/temps");
    }

    public String process(String templateName, Map<String, Object> mapper) {
        try {
            Template template = configuration.getTemplate(templateName);
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 OutputStreamWriter outputStreamWriter = new OutputStreamWriter(baos)) {
                template.process(mapper, outputStreamWriter);
                outputStreamWriter.flush();
                return new String(baos.toByteArray());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
