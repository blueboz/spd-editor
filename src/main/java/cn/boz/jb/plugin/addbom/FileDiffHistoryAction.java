package cn.boz.jb.plugin.addbom;

import cn.boz.jb.plugin.addbom.utils.XmlUtils;
import cn.boz.jb.plugin.idea.utils.CompareUtils;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.actions.CompareWithSelectedRevisionAction;
import com.intellij.openapi.vcs.history.VcsCachingHistory;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vcs.impl.VcsBackgroundableActions;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.vcsUtil.VcsUtil;
import org.dom4j.DocumentException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

/**
 * 文件差异历史
 */
public class FileDiffHistoryAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        final Project project = (e.getProject());
        final AbstractVcs vcs = (ProjectLevelVcsManager.getInstance(project).getVcsFor(file));


        VcsCachingHistory.collectInBackground(vcs, VcsUtil.getFilePath(file), VcsBackgroundableActions.COMPARE_WITH, session -> {
            if (session == null) {
                return;
            }
            final List<VcsFileRevision> revisions = session.getRevisionList();
            CompareWithSelectedRevisionAction.showListPopup(revisions, project,
                    selected -> {
                        try {
                            VcsRevisionNumber vcsRevisionNumber = selected.getRevisionNumber();
                            String fileName = file.getName();
                            String currentContent = XmlUtils.readXmlAndSortAndFormat(new String(file.contentsToByteArray(true)));
                            String selectedContent =  XmlUtils.readXmlAndSortAndFormat(new String(selected.loadContent()));
                            CompareUtils.compare(selectedContent,vcsRevisionNumber.asString(), currentContent,
                                     "currentVersion:" + fileName, XmlFileType.INSTANCE, e.getProject(),file.getName());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (VcsException ex) {
                            ex.printStackTrace();
                        } catch (DocumentException documentException) {
                            documentException.printStackTrace();
                        }

                    }, true);
        });


    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }

        String name = psiFile.getVirtualFile().getName();
        if (!name.endsWith(".spd")) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        e.getPresentation().setEnabledAndVisible(true);
    }
}
