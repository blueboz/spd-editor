package cn.boz.jb.plugin.floweditor.gui.file;

import cn.boz.jb.plugin.floweditor.gui.process.definition.ProcessDefinition;

/**
 * 模板加载器
 */
public interface TemplateLoader {

    ProcessDefinition loadFromFile(String filename);

    void saveToFile(ProcessDefinition processDefinition,String filename);
}
