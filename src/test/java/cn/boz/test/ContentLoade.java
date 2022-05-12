package cn.boz.test;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ContentLoade {

    @Test
    public void testStringCompare() {
        Comparator<String> comparator = new Comparator<>() {
            @Override
            public int compare(String o1, String o2) {
                Pattern pattern = Pattern.compile("(\\w+)(\\d+)");
                Matcher m1 = pattern.matcher(o1);
                Matcher m2 = pattern.matcher(o2);
                if (m1.find() && m2.find()) {
                    int i = m1.group(1).compareTo(m2.group(1));
                    if (i != 0) {
                        return i;
                    } else {
                        int i1 = m1.group(2).compareTo(m2.group(2));
                        return i1;
                    }
                }

                return o1.compareTo(o2);
            }
        };
        Stream.of("hello1", "hello3", "hello100","hello2","hello10","hello11","hello8").sorted(comparator).forEach(System.out::println);
    }


    public static int compareStringWithNumber(String o1,String o2){
        Pattern pattern = Pattern.compile("(\\w+)(\\d+)");
        Matcher m1 = pattern.matcher(o1);
        Matcher m2 = pattern.matcher(o2);
        if (m1.find() && m2.find()) {
            int i = m1.group(1).compareTo(m2.group(1));
            if (i != 0) {
                return i;
            } else {
                int i1 = m1.group(2).compareTo(m2.group(2));
                return i1;
            }
        }

        return o1.compareTo(o2);
    }
    @Test
    public void test() {

        String fpath = "F:/Code/FMS/FMSS_xfunds/xfunds/stepProcess/intbank/同业本金变动撤销流程--deletePrincipalChgProcess.spd";

        try (FileInputStream fileInputStream = new FileInputStream(new File(fpath));) {
            byte[] bytes1 = fileInputStream.readAllBytes();
            byte[] bytes = readXmlAndSortAndFormat(bytes1);
            System.out.println(new String(bytes));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    @NotNull
    private byte[] readXmlAndSortAndFormat(byte[] bs) throws DocumentException, IOException {
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
                return compareStringWithNumber(o1.attribute("id").getValue(),
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
                return compareStringWithNumber(o1.attribute("Element").getValue(),
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
        xmlWriter.setEscapeText(false);
        xmlWriter.write(rootElement);
        xmlWriter.close();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }
}
