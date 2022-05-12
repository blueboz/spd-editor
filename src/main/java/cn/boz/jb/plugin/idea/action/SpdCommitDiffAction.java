package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.addbom.utils.XmlUtils;
import cn.boz.jb.plugin.idea.utils.ChangeUtils;
import cn.boz.jb.plugin.idea.utils.CompareUtils;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.ListSelection;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.changes.Change;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpdCommitDiffAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        //比较选中版本
        try {
            ListSelection<Change> changeListSelection = anActionEvent.getData(VcsDataKeys.CHANGES_SELECTION);
            if (changeListSelection != null) {
                List<Change> changesList = changeListSelection.getList();
                if (changesList.size() == 1) {
                    Change change = changesList.get(0);
                    String changeAfterContent = ChangeUtils.getChangeAfterContent(change);
                    String changeAfterTitle = ChangeUtils.getChangeAfterTitle(change);
                    String changeBeforeContent = ChangeUtils.getChangeBeforeContent(change);
                    String changeBeforeTitle = ChangeUtils.getChangeBeforeTitle(change);

                    CompareUtils.compare(XmlUtils.readXmlAndSortAndFormat(changeBeforeContent)
                            , changeBeforeTitle, XmlUtils.readXmlAndSortAndFormat(changeAfterContent), changeAfterTitle, XmlFileType.INSTANCE, anActionEvent.getProject(), ChangeUtils.getChangeAvaiableFileName(change));

                    return;

                } else if (changesList.size() == 2) {
                    Change change1 = changesList.get(0);
                    Change change2 = changesList.get(1);

                    String change1AfterContent = ChangeUtils.getChangeAfterContent(change1);
                    String change2AfterContent = ChangeUtils.getChangeAfterContent(change2);
                    String change1AfterTitle = ChangeUtils.getChangeAfterTitleWithName(change1);
                    String change2AfterTitle = ChangeUtils.getChangeAfterTitleWithName(change2);

                    CompareUtils.compare(XmlUtils.readXmlAndSortAndFormat(change1AfterContent), change1AfterTitle, XmlUtils.readXmlAndSortAndFormat(change2AfterContent)
                            , change2AfterTitle, XmlFileType.INSTANCE, anActionEvent.getProject(), "diff");
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        ListSelection<Change> changeListSelection = e.getData(VcsDataKeys.CHANGES_SELECTION);
        if (changeListSelection != null) {
            List<Change> changesList = changeListSelection.getList();
            if (changesList.size() == 1) {
                Change change = changesList.get(0);
                e.getPresentation().setEnabledAndVisible(checkVisible(change));
                return;

            } else if (changesList.size() == 2) {
                Change change1 = changesList.get(0);
                Change change2 = changesList.get(1);
                e.getPresentation().setEnabledAndVisible(checkVisible(change1) && checkVisible(change2));
                return;
            }
        }
        e.getPresentation().setEnabledAndVisible(false);

    }

    private boolean checkVisible(Change change) {
        FileStatus fileStatus = change.getFileStatus();
        if (FileStatus.DELETED.equals(fileStatus)) {
            if (change.getBeforeRevision().getFile().getName().endsWith("spd")) {
                return true;
            }
        } else if (change.getAfterRevision().getFile().getName().endsWith("spd")) {
            return true;
        }
        return false;
    }
}
