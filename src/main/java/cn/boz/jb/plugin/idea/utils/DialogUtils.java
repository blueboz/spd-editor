package cn.boz.jb.plugin.idea.utils;

import cn.boz.jb.plugin.idea.configurable.SpdEditorDBSettings;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.UIUtil;
import icons.SpdEditorIcons;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class DialogUtils {

    public static void hintSqlDialog(String text, ClipboardOwner owner, Project project, String savefileNameNoPrefix) {
        int idx = Messages.showDialog(text, "SQL", new String[]{"复制Sql", "更新至DB", "打开配置项", "导出到目录", "确定"}, 3, SpdEditorIcons.FLOW_16_ICON);
        if (idx == 0) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(text);
            clipboard.setContents(selection, owner);
        } else if (idx == 1) {
            //更新至db
            try {
                boolean b = DBUtils.executeSqlBatchs(DBUtils.getConnection(project), text);
                if (b) {
                    Messages.showMessageDialog("更新成功!!", "更新成功", UIUtil.getInformationIcon());
                }
            } catch (SQLException sqlException) {
                int selidx = Messages.showDialog("数据库连接发生错误，检查数据库配置?" + sqlException.getMessage(), "数据库错误", new String[]{"打开配置项", "取消"}, 0, UIUtil.getErrorIcon());
                if (selidx == 0) {
                    ShowSettingsUtil.getInstance().showSettingsDialog(project, SpdEditorDBSettings.class);
                }
            } catch (Exception ee) {
                ee.printStackTrace();
                Messages.showErrorDialog("发生错误", ee.getMessage());
            }
        } else if (idx == 2) {
            ShowSettingsUtil.getInstance().showSettingsDialog(project, SpdEditorDBSettings.class);

        } else if (idx == 3) {
            FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
            VirtualFile selectPath = FileChooser.chooseFile(fileChooserDescriptor, project, null);
            //当前文件名称
//                    String path = selectPath.getPath();
            //写入到文件
//                    selectPath.create
            WriteCommandAction.runWriteCommandAction(ProjectManager.getInstance().getDefaultProject(), () -> {

                try {
                    byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
                    VirtualFile vfile = selectPath.findOrCreateChildData(null, savefileNameNoPrefix + ".sql");
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
