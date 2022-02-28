package cn.boz.test;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Dom4jTester {

    @Test
    public void test() throws IOException {
        Document doc = DocumentHelper.createDocument();
        Element usertask = doc.addElement("usertask");
        usertask.addAttribute("username", "&><=?\"\nfuckyou tonny");
        doc.addComment("&><=?sucker");

        OutputFormat format = OutputFormat.createPrettyPrint();

        format.setIndent(true);
        format.setIndent("\t");
//        format.setIndentSize(4);  // 行缩进
        format.setNewlines(true); // 一个结点为一行
        format.setTrimText(true); // 去重空格
        format.setPadText(true);
        //转义
        format.setNewLineAfterDeclaration(false);
        format.setEncoding("UTF-8");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLWriter xmlWriter = new XMLWriter(byteArrayOutputStream, format);
        xmlWriter.setEscapeText(false);
        xmlWriter.write(doc);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        String s = new String(bytes, StandardCharsets.UTF_8);

    }
}
