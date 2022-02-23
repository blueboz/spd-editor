package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.idea.utils.CompareUtils;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpdDiffActionExt extends AnAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        final Change @NotNull [] changes = e.getRequiredData(VcsDataKeys.CHANGES);
        List<Change> result = ContainerUtil.newArrayList(changes);
        System.out.println(result);
        Change change = result.get(0);
        try {

            String afterContent = change.getAfterRevision().getContent();
            String beforeContent = change.getBeforeRevision().getContent();
            CompareUtils.compare(beforeContent,afterContent, XmlFileType.INSTANCE,e.getProject());
        } catch (Exception ee) {

        }
//        showDiffForChange(project, result, 0);

    }
}
