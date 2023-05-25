package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.addbom.utils.XmlUtils;
import cn.boz.jb.plugin.idea.utils.ChangeUtils;
import cn.boz.jb.plugin.idea.utils.CompareUtils;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.changes.Change;
import org.jetbrains.annotations.NotNull;

public class RepositoryChangeDiffAction extends AnAction {


    /**
     * Git 提交历史框 部分示例代码
     * com.intellij.openapi.vcs.history.impl.VcsSelectionHistoryDialog#createBottomPanel()
     * JPanel tablePanel = new JPanel(new BorderLayout());
     * tablePanel.add(ScrollPaneFactory.createScrollPane(myList), BorderLayout.CENTER);
     * //通过ColumnInfo来定义每一列怎么渲染
     * this.myDefaultColumns = new ColumnInfo[]{new RevisionColumnInfo((Comparator)null), new DateColumnInfo(), new AuthorColumnInfo(), new MessageColumnInfo(project)};
     * this.myListModel = new ListTableModel(this.myDefaultColumns);
     *
     * @param e
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Change[] selectChange = e.getData(VcsDataKeys.SELECTED_CHANGES);
        //这些datas
        if (selectChange != null) {
            try {
                if (selectChange.length == 1) {

                    Change change = selectChange[0];
                    String changeAfterContent = ChangeUtils.getChangeAfterContent(change);
                    String changeAfterTitle = ChangeUtils.getChangeAfterTitle(change);
                    String changeBeforeContent = ChangeUtils.getChangeBeforeContent(change);
                    String changeBeforeTitle = ChangeUtils.getChangeBeforeTitle(change);

                    CompareUtils.compare(XmlUtils.readXmlAndSortAndFormat(changeBeforeContent)
                            , changeBeforeTitle, XmlUtils.readXmlAndSortAndFormat(changeAfterContent), changeAfterTitle, XmlFileType.INSTANCE, e.getProject(), ChangeUtils.getChangeAvaiableFileName(change));

                } else if (selectChange.length == 2) {

                    Change change1 = selectChange[0];
                    Change change2 = selectChange[1];
                    String change1AfterContent = ChangeUtils.getChangeAfterContent(change1);
                    String change2AfterContent = ChangeUtils.getChangeAfterContent(change2);
                    String change1AfterTitle = ChangeUtils.getChangeAfterTitleWithName(change1);
                    String change2AfterTitle = ChangeUtils.getChangeAfterTitleWithName(change2);

                    CompareUtils.compare(XmlUtils.readXmlAndSortAndFormat(change1AfterContent), change1AfterTitle, XmlUtils.readXmlAndSortAndFormat(change2AfterContent)
                            , change2AfterTitle, XmlFileType.INSTANCE, e.getProject(), "diff");
                }
            } catch (Exception eee) {
                eee.printStackTrace();
            }
        }

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Change[] selectChange = e.getData(VcsDataKeys.SELECTED_CHANGES);
        //这些datas
        if (selectChange != null) {
            if (selectChange.length == 1) {
                Change change = selectChange[0];
                e.getPresentation().setEnabledAndVisible(checkVisible(change));
                return;

            } else if (selectChange.length == 2) {
                Change change1 = selectChange[0];
                Change change2 = selectChange[1];
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