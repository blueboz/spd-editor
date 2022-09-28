package cn.boz.jb.plugin.idea.action.export;

import cn.boz.jb.plugin.idea.action.export.bean.FileWrapper;
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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExportAsZipAction extends ExportBaseAction {



    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        ListSelection<Change> listSelection = anActionEvent.getData(VcsDataKeys.CHANGES_SELECTION);
        List<Change> changes = listSelection.getList();
        List<FileWrapper> fileWrappers = new ArrayList<>();


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
                    fileWrappers.add(new FileWrapper(path, bytes));
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
                    fileWrappers.add(new FileWrapper(path, bytes));
                } catch (VcsException e) {
                    e.printStackTrace();
                }
            }


        }
        exportSpdAction(anActionEvent,fileWrappers,false,false,"zip");



    }



}
