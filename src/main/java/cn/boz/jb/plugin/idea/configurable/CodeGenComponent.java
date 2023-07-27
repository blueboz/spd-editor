package cn.boz.jb.plugin.idea.configurable;

import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import cn.boz.jb.plugin.idea.utils.ExceptionProcessorUtils;
import cn.boz.jb.plugin.idea.utils.NotificationUtils;
import cn.boz.jb.plugin.idea.utils.TemplateExtractor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Ref;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CodeGenComponent {

    private TextFieldWithBrowseButton outputDestEditor;

    private TextFieldWithBrowseButton templatePathEditor;

    private JButton generateTemplate;

    private JPanel mainComponent = null;

    @SuppressWarnings("unchecked")
    public CodeGenComponent(Project project) {
        outputDestEditor = new TextFieldWithBrowseButton();
        outputDestEditor.addBrowseFolderListener(null, null,
                ProjectManager.getInstance().getDefaultProject(),
                FileChooserDescriptorFactory.createSingleFolderDescriptor());


        templatePathEditor = new TextFieldWithBrowseButton();
        templatePathEditor.addBrowseFolderListener(null, null,
                ProjectManager.getInstance().getDefaultProject(),
                FileChooserDescriptorFactory.createSingleFolderDescriptor());

        generateTemplate = new JButton("注入模板到目录中");
        generateTemplate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //测试连接的时候，需要执行的操作
                CodeGenState instance = CodeGenState.getInstance(project);
                if(StringUtils.isBlank(instance.templatePath)){
                    Messages.showErrorDialog("请配置模版路径，不然我也不知道输出到哪里","错误");
                    return ;
                }
                try{

                    TemplateExtractor.extractTarGz(instance.templatePath);
                    NotificationUtils.notifyWithLink(new File(instance.templatePath),"导出到路径:"+instance.templatePath,project);
                }catch (Exception ee){
                    String errMsg = ExceptionProcessorUtils.generateRecrusiveException(ee);
                    NotificationUtils.warnning("导出失败",errMsg,project);
                }

            }
        });


        mainComponent = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("输出目录:"), outputDestEditor, 1, false)
                .addLabeledComponent(new JBLabel("模板目录:"), templatePathEditor, 1, false)
                .addComponent(generateTemplate)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

    }




    private static Set<String> walkAllClasses(List<URL> urls) throws URISyntaxException, IOException {
        Set<String> classes = new HashSet<>();
        for (URL url : urls) {
            if (url.toURI().getScheme().equals("file")) {//判断Scheme是不是file
                File file = new File(url.toURI());
                if (!file.exists()) {continue;}//是存在

                if (file.isDirectory()) {//如果是目录
                    Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {//遍历所有文件
                        @Override
                        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {

                            String name = path.toFile().getName();
                            System.out.println(name);
//                            if (path.toFile().getName().endsWith(".class")) {//判断是否为class后缀

//                                //去除路径截取包名
//                                String substring = path.toFile().getAbsolutePath().substring(file.getAbsolutePath().length());
//                                if (substring.startsWith(File.separator)) {
//                                    substring = substring.substring(1);
//                                }
//                                substring = substring.substring(0, substring.length() - 6);//去掉后缀
//                                classes.add(substring.replace(File.separator, "."));//把路径替换为.
//                            }
                            return super.visitFile(path, attrs);
                        }
                    });
                } else if (file.getName().endsWith(".jar")) {//如果是jar包
                    JarFile jarFile = new JarFile(file);
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {//遍历所有文件
                        JarEntry jarEntry = entries.nextElement();
                        if (!jarEntry.getName().endsWith(".class")) continue;//判断后缀是否为class
                        String replace = jarEntry.getName().replace("/", ".");//把路径替换为.
                        classes.add(replace.substring(0, replace.length() - 6));//去掉后缀
                    }
                }
            }
        }
        return classes;
    }


    public JPanel getPanel() {
        return mainComponent;
    }


    public JComponent getPreferredFocusedComponent() {
        return outputDestEditor;
    }

    public void apply(Project project) {
        CodeGenState codeGenState = CodeGenState.getInstance(project);
        codeGenState.outputDest = outputDestEditor.getText();
        codeGenState.templatePath=templatePathEditor.getText();
    }


    public void reset(Project project) {
        CodeGenState codeGenState = CodeGenState.getInstance(project);
        outputDestEditor.setText(codeGenState.outputDest);
        templatePathEditor.setText(codeGenState.templatePath);
    }

    /**
     * 判断配置信息是否发生了改变
     *
     * @return
     */
    public boolean isModified(Project project) {
        CodeGenState codeGenState = CodeGenState.getInstance(project);
        if (!codeGenState.outputDest.equals(this.outputDestEditor.getText())) {
            return true;
        }
        if(!codeGenState.templatePath.equals(this.templatePathEditor.getText())){
            return true;
        }
        return false;
    }
}
