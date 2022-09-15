package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import cn.boz.jb.plugin.idea.utils.Constants;
import com.intellij.dvcs.repo.Repository;
import com.intellij.dvcs.repo.VcsRepositoryManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.ListSelection;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.ByteBackedContentRevision;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vfs.VirtualFile;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExportAllSpdSql extends AnAction {

    class FileWrapper {
        private String path;
        private byte[] content;

        public FileWrapper(String path, byte[] content) {
            this.path = path;
            this.content = content;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public byte[] getContent() {
            return content;
        }

        public void setContent(byte[] content) {
            this.content = content;
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        ListSelection<Change> listSelection = anActionEvent.getData(VcsDataKeys.CHANGES_SELECTION);
        List<Change> changes = listSelection.getList();
        List<ExportAllSpdSql.FileWrapper> fileWrappers = new ArrayList<>();


        for (Change change : changes) {
            if (Change.Type.DELETED.equals(change.getType())) {
                continue;
            }
            ContentRevision afterRevision = change.getAfterRevision();
            String path = afterRevision.getFile().getPath();
            VcsRepositoryManager instance = VcsRepositoryManager.getInstance(anActionEvent.getProject());
            Repository repositoryForFile = instance.getRepositoryForFile(afterRevision.getFile(), true);
            String rootpath = repositoryForFile.getRoot().getPath();
            path = path.replace(rootpath, "");
            if (afterRevision instanceof ByteBackedContentRevision) {
                try {
                    byte[] bytes = ((ByteBackedContentRevision) afterRevision).getContentAsBytes();
                    fileWrappers.add(new ExportAllSpdSql.FileWrapper(path, bytes));
                } catch (VcsException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    String content = afterRevision.getContent();
                    Charset charset = afterRevision.getFile().getCharset();
                    if (charset == null) {
                        charset = StandardCharsets.UTF_8;
                    }
                    byte[] bytes = content.getBytes(charset);
                    fileWrappers.add(new ExportAllSpdSql.FileWrapper(path, bytes));
                } catch (VcsException e) {
                    e.printStackTrace();
                }
            }


        }


        //选择导出的目录
        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
        VirtualFile virtualFile = FileChooser.chooseFile(fileChooserDescriptor, anActionEvent.getProject(), null);
        if (virtualFile == null) {
            return;
        }

        WriteCommandAction.runWriteCommandAction(anActionEvent.getProject(), () -> {
            try {

                String filename = "SQLEXPORT_" + new Date().getTime() + ".zip";
                VirtualFile exportDest = virtualFile.createChildData(null, filename);
                ZipOutputStream zos = new ZipOutputStream(exportDest.getOutputStream(null));

                for (ExportAllSpdSql.FileWrapper fileWrapper : fileWrappers) {
                    if(!fileWrapper.getPath().endsWith(".spd")){
                        continue;
                    }

                    String orgPath = fileWrapper.getPath();
                    String nameSub = orgPath.substring(0, orgPath.lastIndexOf(".spd"))+".sql";
                    ChartPanel cp=new ChartPanel();
                    cp.loadFromInputStream(new ByteArrayInputStream(fileWrapper.getContent()));
                    List<String> sqls = cp.generateSql();
                    String joiningSql = sqls.stream().map(sql -> sql.replace("\n", "")).collect(Collectors.joining(";\n")) + ";";
                    byte[] bytes = joiningSql.getBytes(StandardCharsets.UTF_8);
                    zos.putNextEntry(new ZipEntry(nameSub));

                    zos.write(new byte[]{(byte) 0xef, (byte) 0xbb, (byte) 0xbf});
                    zos.write(bytes, 0, bytes.length);
                    zos.closeEntry();
                }
                zos.close();

                Notification spdEditorNotification = new Notification(Constants.NOTIFY_GROUP_GLOBAL, SpdEditorIcons.FLOW_16_ICON, NotificationType.INFORMATION);
                spdEditorNotification.setTitle("exported");
                spdEditorNotification.setContent("Export as zip in " + exportDest.getPath());
                spdEditorNotification.notify(anActionEvent.getProject());

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
