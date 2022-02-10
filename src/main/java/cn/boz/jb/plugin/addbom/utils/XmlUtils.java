package cn.boz.jb.plugin.addbom.utils;

import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class XmlUtils {


    public byte[] readXmlAndSortAndFormat(byte[] bs) throws DocumentException, IOException {
        SAXReader saxReader = new SAXReader();
        Document read = saxReader.read(new ByteArrayInputStream(bs));
        Element rootElement = read.getRootElement();
        Element process = rootElement.element("process");
        Element diagram = rootElement.element("Diagram");
        List<Element> prcesss = process.elements();
        prcesss.sort((o1, o2) -> {
            String o1name = o1.getName();
            String o2name = o2.getName();
            int i = o1name.compareTo(o2name);
            if (i != 0) {
                return i;
            } else {
                return StringUtils.compareStringWithNumber(o1.attribute("id").getValue(),
                        o2.attribute("id").getValue());
            }
        });
        List<Element> diagrams = diagram.elements();
        diagrams.sort((o1, o2) -> {
            String o1name = o1.getName();
            String o2name = o2.getName();
            int i = o1name.compareTo(o2name);
            if (i != 0) {
                return i;
            } else {
                return StringUtils.compareStringWithNumber(o1.attribute("Element").getValue(),
                        o2.attribute("Element").getValue());
            }
        });

        for (Element element : prcesss) {
            process.remove(element);
        }
        for (Element element : prcesss) {
            process.add(element);
        }
        for (Element element : diagrams) {
            diagram.remove(element);
        }
        for (Element element : diagrams) {
            diagram.add(element);
        }
        OutputFormat xmlFormat = new OutputFormat();
        xmlFormat.setEncoding("UTF-8");
        xmlFormat.setNewlines(true);
        xmlFormat.setIndent(true);
        xmlFormat.setIndent("\t");


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLWriter xmlWriter = new XMLWriter(byteArrayOutputStream, xmlFormat);
        xmlWriter.write(rootElement);
        xmlWriter.close();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }
}
