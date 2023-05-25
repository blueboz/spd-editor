package cn.boz.jb.plugin.idea.action.export;

import cn.boz.jb.plugin.idea.action.export.bean.FileWrapper;
import com.intellij.dvcs.repo.Repository;
import com.intellij.dvcs.repo.VcsRepositoryManager;
import com.intellij.openapi.ListSelection;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.ByteBackedContentRevision;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ExportAllSpdSqlAction extends ExportBaseAction {


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


        //选择导出的目录
        exportSpdAction(anActionEvent, fileWrappers,true,true,"SQL");
    }
}
