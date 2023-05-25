package cn.boz.jb.plugin.floweditor.gui.file;

import cn.boz.jb.plugin.floweditor.gui.process.definition.ProcessDefinition;
import org.dom4j.DocumentException;

/**
 * 模板加载器
 */
public interface TemplateLoader {

    ProcessDefinition loadFromFile(String filename) throws DocumentException;

    void saveToFile(ProcessDefinition processDefinition, String filename);
}
