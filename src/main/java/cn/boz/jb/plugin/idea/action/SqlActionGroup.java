package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import cn.boz.jb.plugin.idea.configurable.SpdEditorDBSettings;
import cn.boz.jb.plugin.idea.configurable.SpdEditorDBState;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import cn.boz.jb.plugin.idea.widget.SpdEditor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;


public class SqlActionGroup extends DefaultActionGroup implements ClipboardOwner {
    AnAction[] actions;

    public SqlActionGroup() {
        actions = new AnAction[]{
                new AnAction() {
                    {
                        getTemplatePresentation().setText("CopySql");
                    }

                    @Override
                    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                        Component requiredData = anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
                        if (!(requiredData instanceof ChartPanel)) {
                            return;
                        }
                        SpdEditor spdEditor = (SpdEditor) SwingUtilities.getAncestorOfClass(SpdEditor.class, requiredData);
                        if (spdEditor instanceof SpdEditor) {
                            ChartPanel chartPanel = spdEditor.getChartPanel();
                            List<String> sqls = chartPanel.generateSortedSql();
                            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                            String joiningSql = sqls.stream().map(sql -> sql.replace("\n", "")).collect(Collectors.joining(";\n")) + ";";
                            StringSelection selection = new StringSelection(joiningSql);
                            clipboard.setContents(selection, SqlActionGroup.this);
                        }
                    }
                },
                new AnAction() {
                    {
                        getTemplatePresentation().setText("更新至DB");
                    }
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                        Component requiredData = anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
                        if (!(requiredData instanceof ChartPanel)) {
                            return;
                        }
                        SpdEditor spdEditor = (SpdEditor) SwingUtilities.getAncestorOfClass(SpdEditor.class, requiredData);
                        if (spdEditor instanceof SpdEditor) {
                            ChartPanel chartPanel = spdEditor.getChartPanel();
                            List<String> sqls = chartPanel.generateSortedSql();
                            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                            String joiningSql = sqls.stream().map(sql -> sql.replace("\n", "")).collect(Collectors.joining(";\n")) + ";";
                            StringSelection selection = new StringSelection(joiningSql);
                            clipboard.setContents(selection, SqlActionGroup.this);
                            SpdEditorDBState instance = SpdEditorDBState.getInstance(anActionEvent.getProject());
                            try {
                                boolean b = DBUtils.executeSql(instance.jdbcUserName, instance.jdbcPassword, instance.jdbcUrl, instance.jdbcDriver, sqls);
                                if (b) {
                                    Messages.showMessageDialog("更新成功!!", "更新成功", UIUtil.getInformationIcon());
                                }
                            } catch (SQLException sqlException) {
                                int selidx = Messages.showDialog("数据库连接发生错误，检查数据库配置?" + sqlException.getMessage(), "数据库错误", new String[]{"打开配置项", "取消"}, 0, UIUtil.getErrorIcon());
                                if (selidx == 0) {
                                    ShowSettingsUtil.getInstance().showSettingsDialog(null, SpdEditorDBSettings.class);
                                }
                            } catch (Exception ee) {
                                ee.printStackTrace();
                                Messages.showErrorDialog("发生错误", ee.getMessage());
                            }
                        }
                    }
                },
                new AnAction() {
                    {
                        getTemplatePresentation().setText("Show Settings");
                    }
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                        ShowSettingsUtil.getInstance().showSettingsDialog(ProjectManager.getInstance().getDefaultProject(), SpdEditorDBSettings.class);

                    }
                },
                new AnAction() {
                    {
                        getTemplatePresentation().setText("CopySQL");
                    }
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                        Component requiredData = anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
                        if (!(requiredData instanceof ChartPanel)) {
                            return;
                        }
                        SpdEditor spdEditor = (SpdEditor) SwingUtilities.getAncestorOfClass(SpdEditor.class, requiredData);
                        if (spdEditor instanceof SpdEditor) {
                            ChartPanel chartPanel = spdEditor.getChartPanel();
                            List<String> sqls = chartPanel.generateSortedSql();
                            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                            String joiningSql = sqls.stream().map(sql -> sql.replace("\n", "")).collect(Collectors.joining(";\n")) + ";";
                            StringSelection selection = new StringSelection(joiningSql);
                            clipboard.setContents(selection, SqlActionGroup.this);
                            Project project = anActionEvent.getProject();
                            VirtualFile virtualFile = chartPanel.getVirtualFile();
                            FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
                            VirtualFile selectPath = FileChooser.chooseFile(fileChooserDescriptor, project, null);
                            //当前文件名称
                            String name = virtualFile.getName();
                            String nameSub = name.substring(0, name.lastIndexOf(".spd"));
//                    String path = selectPath.getPath();
                            //写入到文件
//                    selectPath.create
                            WriteCommandAction.runWriteCommandAction(ProjectManager.getInstance().getDefaultProject(), () -> {

                                try {
                                    byte[] bytes = joiningSql.getBytes(StandardCharsets.UTF_8);
                                    VirtualFile vfile = selectPath.findOrCreateChildData(null, nameSub + ".sql");
                                    //
                                    //如果文件没有保存提示保存

                                    vfile.setBinaryContent(new byte[]{});
                                    vfile.setCharset(StandardCharsets.UTF_8);
                                    vfile.setBOM(new byte[]{(byte) 0xef, (byte) 0xbb, (byte) 0xbf});
                                    vfile.setBinaryContent(bytes);
                                } catch (Exception ee) {
                                    ee.printStackTrace();
                                }
                            });
                        }
                    }
                }

        };

    }

    @Override
    public @NotNull AnAction[] getChildren(@Nullable AnActionEvent e) {
        return super.getChildren(e);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable transferable) {

    }
}
