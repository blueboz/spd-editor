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
import java.nio.charset.StandardCharsets;
import java.util.List;

public class XmlUtils {
    public static String readXmlAndSortAndFormat(String str) throws DocumentException {
        str = str.replaceAll("\n", "");
        str = str.replaceAll("\t", "");
        SAXReader saxReader = new SAXReader();
        Document read = saxReader.read(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)));
        Element rootElement = read.getRootElement();
        if (rootElement != null) {
            Element process = rootElement.element("process");
            if (process != null) {
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

                for (Element element : prcesss) {
                    process.remove(element);
                }
                for (Element element : prcesss) {
                    process.add(element);
                }
            }
            Element diagram = rootElement.element("Diagram");

            if (diagram != null) {

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


                for (Element element : diagrams) {
                    diagram.remove(element);
                }
                for (Element element : diagrams) {
                    diagram.add(element);
                }
            }
        }

        return formmaterOutput(read);
    }

    public static final String formmaterOutput(Document document) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            OutputFormat xmlFormat = new OutputFormat();
            xmlFormat.setEncoding("UTF-8");
            xmlFormat.setNewlines(true);
            xmlFormat.setIndent(true);
            xmlFormat.setIndent("\t");
            XMLWriter xmlWriter = new XMLWriter(byteArrayOutputStream, xmlFormat);
            xmlWriter.setEscapeText(false);
            xmlWriter.write(document);
            xmlWriter.close();
            String s = new String(byteArrayOutputStream.toByteArray());
//            s = s.replaceAll("&amp;", "&");
            s=s.replaceAll("\n\n","\n");
            return s;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
