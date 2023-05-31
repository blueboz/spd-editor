package cn.boz.jb.plugin;

import com.alibaba.fastjson.JSON;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ThymeleafDemo {
    
    public static void main(String[] args) throws IOException {
        // 创建模板引擎
        TemplateEngine templateEngine = new TemplateEngine();
        
        // 创建模板解析器
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/"); // 设置模板所在的目录
        resolver.setSuffix(".html"); // 设置模板文件的后缀名
        resolver.setCharacterEncoding("UTF-8"); // 设置字符编码
        resolver.setCacheable(false); // 禁用缓存
        
        // 将解析器注册到模板引擎中
        templateEngine.setTemplateResolver(resolver);

        byte[] bytes = Files.readAllBytes(Path.of("F:\\Code\\spd-editor\\src\\main\\resources\\config.json"));
        Map map = JSON.parseObject(new String(bytes), HashMap.class);
        // 创建渲染上下文
        Context context = new Context();

        context.setVariables(map);


        // 渲染模板文件
        String result = templateEngine.process("BaseInput", context);
        System.out.println(result);
    }
}
