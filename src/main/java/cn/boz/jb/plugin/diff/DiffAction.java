package cn.boz.jb.plugin.diff;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class DiffAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        PsiFile data = anActionEvent.getData(CommonDataKeys.PSI_FILE);
//        VcsFileUtil.
//        VcsHistoryUtil.
        VcsFileRevision revision;
//        VcsHistoryUtil.

    }
}
