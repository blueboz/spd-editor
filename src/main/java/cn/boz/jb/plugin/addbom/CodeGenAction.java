package cn.boz.jb.plugin.addbom;

import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;
import cn.boz.jb.plugin.freemarker.MyTemplateMethodEx;
import cn.boz.jb.plugin.idea.configurable.CodeGenSettings;
import cn.boz.jb.plugin.idea.configurable.CodeGenState;
import cn.boz.jb.plugin.idea.utils.Constants;
import cn.boz.jb.plugin.idea.utils.ExceptionProcessorUtils;
import cn.boz.jb.plugin.idea.utils.NotificationUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.ide.scratch.ScratchFileCreationHelper;
import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.ide.scratch.ScratchFileServiceImpl;
import com.intellij.ide.scratch.ScratchRootType;
import com.intellij.ide.scratch.ScratchUtil;
import com.intellij.json.JsonFileType;
import com.intellij.json.JsonLanguage;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeGenAction extends AnAction implements ClipboardOwner {

    Configuration templateCgr;
    Configuration strTemplateCfg;
    StringTemplateLoader stringTemplateLoader;
    String templatePath = "";
    String outputDest = "";
    JSONObject mapper;
    boolean deletemode = false;

    public CodeGenAction() {
        templateCgr = new Configuration(Configuration.VERSION_2_3_30);
        templateCgr.setSharedVariable("myutils", new MyTemplateMethodEx());

        strTemplateCfg = new Configuration(Configuration.VERSION_2_3_30);
        stringTemplateLoader = new StringTemplateLoader();
        strTemplateCfg.setTemplateLoader(stringTemplateLoader);

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (virtualFile == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        boolean b = virtualFile.getName().endsWith(".codegen");
        if (b) {
            e.getPresentation().setEnabledAndVisible(true);
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        VirtualFile virtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        try {
            CodeGenState codeGenState = CodeGenState.getInstance(anActionEvent.getProject());
            if (StringUtils.isBlank(codeGenState.outputDest)) {
                int idx = Messages.showDialog("输出路径未配置", "配置信息缺失", new String[]{"去配置", "取消"}, 0, SpdEditorIcons.CODEGEN_16_ICON);
                if (idx == 0) {
                    Messages.showErrorDialog("输出路径未配置", "");
                    ShowSettingsUtil.getInstance().showSettingsDialog(anActionEvent.getProject(), CodeGenSettings.class);
                }
                return;
            }
            if (StringUtils.isBlank(codeGenState.templatePath)) {
                int idx = Messages.showDialog("模板路径未配置", "配置信息缺失", new String[]{"去配置", "取消"}, 0, SpdEditorIcons.CODEGEN_16_ICON);
                if (idx == 0) {
                    Messages.showErrorDialog("模板路径未配置", "");
                    ShowSettingsUtil.getInstance().showSettingsDialog(anActionEvent.getProject(), CodeGenSettings.class);
                }
                return;
            }
            templatePath = codeGenState.templatePath;
            outputDest = codeGenState.outputDest;

            byte[] bytes = Files.readAllBytes(Path.of(virtualFile.getPath()));
            String configText = new String(bytes);
            mapper = JSON.parseObject(configText);
            filterAndProcessConfig(mapper);


            templateCgr.setDirectoryForTemplateLoading(new File(codeGenState.templatePath));


            File file = new File(codeGenState.templatePath);
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
            File outputdest = new File(codeGenState.outputDest);

            String prettyFormat = JSON.toJSONString(mapper, true);

            String hint="Export into" + outputDest;
//            NotificationUtils.notifyWithLink(outputdest, "Export into" + outputDest, anActionEvent.getProject());

            Notification codeGenNotify = new Notification(Constants.NOTIFY_GROUP_GLOBAL, SpdEditorIcons.FLOW_16_ICON, NotificationType.INFORMATION);
            codeGenNotify.setTitle("exported");
            codeGenNotify.addAction(new DumbAwareAction() {
                {
                    Presentation presentation = this.getTemplatePresentation();
                    presentation.setText("Open");
                }

                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }


                }
            });
            codeGenNotify.addAction(new DumbAwareAction() {
                {
                    Presentation templatePresentation = this.getTemplatePresentation();
                    templatePresentation.setText("Copy");
                }
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    StringSelection selection = new StringSelection(prettyFormat);
                    clipboard.setContents(selection, CodeGenAction.this);
                }

            });
            codeGenNotify.addAction(new DumbAwareAction() {
                {
                    Presentation templatePresentation = this.getTemplatePresentation();
                    templatePresentation.setText("Open Scratch");
                }
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    PsiFile psiFile = ScratchFileCreationHelper.parseHeader(anActionEvent.getProject(), JsonLanguage.INSTANCE, prettyFormat);
                    psiFile.setName(virtualFile.getName());
                    VirtualFile vf = psiFile.getVirtualFile();
                    // 打开 Scratch 文件
                    FileEditorManager.getInstance(anActionEvent.getProject()).openFile(vf, true);
                }

            });
            codeGenNotify.setContent(hint);
            codeGenNotify.notify(anActionEvent.getProject());


        } catch (IOException e) {
            NotificationUtils.warnning("生成代码发生错误", ExceptionProcessorUtils.generateRecrusiveException(e), anActionEvent.getProject());
        }


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

    public void process(String path) {
        try {
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
            if (Files.isRegularFile(joinPath)) {
                Files.delete(joinPath);
            }
            if (deletemode) {
                return;
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

    public JSONObject filterAndProcessConfig(JSONObject mapper) {

        JSONArray columns = mapper.getJSONArray("columns");
        for (int i = 0; i < columns.size(); i++) {
            JSONObject column = columns.getJSONObject(i);
            String dataType = column.getString("dataType");
            Matcher matcher = pattern.matcher(dataType);
            if (matcher.matches()) {
                String group1 = matcher.group(1);
                String group2 = matcher.group(2);
                String[] split = group2.split(",");

                String objType = null;
                String maxLenInOracle = null;
                if (group1.toUpperCase().indexOf("VARCHAR") != -1) {
                    objType = "String";
                    maxLenInOracle = split[0];
                } else if (group1.toUpperCase().indexOf("NUMBER") != -1) {
                    if (split.length == 2) {
                        Integer prec = Integer.parseInt(split[0]);
                        Integer scale = Integer.parseInt(split[1]);
                        String maxVal = String.format("%s.%sd", "9".repeat(prec - scale), "9".repeat(scale));
//                        System.out.println(maxVal);
                        objType = "Double";
                        maxLenInOracle = (prec + 1) + "";
                        column.put("limit", maxVal);
                        column.put("digital", scale);
                    } else if (split.length == 1) {
                        Integer maxLength = Integer.parseInt(split[0]);
                        maxLenInOracle = split[0];
                        if (maxLength > 9) {
                            objType = "Long";
                            //使用Long
                        } else {
                            //使用Int
                            objType = "Integer";
                        }
                        //Int 9位最多
                    }
                }
                column.put("objType", objType);
                column.put("maxLenInOracle", maxLenInOracle);

            }
        }

        return mapper;
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }
}
