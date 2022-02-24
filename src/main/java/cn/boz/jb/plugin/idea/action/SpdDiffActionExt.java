package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.addbom.utils.XmlUtils;
import cn.boz.jb.plugin.idea.utils.CompareUtils;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.util.containers.ContainerUtil;
import org.dom4j.DocumentException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class SpdDiffActionExt extends AnAction {


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
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        //这些datas
        VcsFileRevision[] revisions = e.getData(VcsDataKeys.VCS_FILE_REVISIONS);
        if (revisions.length == 1) {
            final Change @NotNull [] changes = e.getRequiredData(VcsDataKeys.CHANGES);
            //如果是一个
            List<Change> result = ContainerUtil.newArrayList(changes);
            Change change = result.get(0);
            try {

                String afterContent = change.getAfterRevision().getContent();
                ContentRevision beforeRevision = change.getBeforeRevision();
                String beforeContent;
                String beforeRev;
                if (beforeRevision != null) {
                    beforeContent = beforeRevision
                            .getContent();
                    beforeRev = beforeRevision.getRevisionNumber().asString();
                } else {
                    beforeContent = "";
                    beforeRev = "";
                }

                String afterRev = change.getAfterRevision().getRevisionNumber().asString();


                CompareUtils.compare(XmlUtils.readXmlAndSortAndFormat(beforeContent), beforeRev, XmlUtils.readXmlAndSortAndFormat(afterContent)
                        , afterRev, XmlFileType.INSTANCE, e.getProject());
            } catch (Exception ee) {
                ee.printStackTrace();
            }

        } else if (revisions.length == 2) {
            VcsFileRevision rev1 = revisions[1];
            VcsFileRevision rev2 = revisions[0];
            try {
                String rev1s = new String(rev1.loadContent());
                String rev2s = new String(rev2.loadContent());
                String rev1num = rev1.getRevisionNumber().asString();
                String rev2num = rev2.getRevisionNumber().asString();
                CompareUtils.compare(XmlUtils.readXmlAndSortAndFormat(rev1s), rev1num, XmlUtils.readXmlAndSortAndFormat(rev2s), rev2num
                        , XmlFileType.INSTANCE, e.getProject());
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (VcsException ex) {
                ex.printStackTrace();
            } catch (DocumentException ex) {
                ex.printStackTrace();
            }
        }

    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        //这些datas
        VcsFileRevision[] data = e.getData(VcsDataKeys.VCS_FILE_REVISIONS);
        if (data.length == 1) {
            e.getPresentation().setEnabledAndVisible(true);
        } else if (data.length == 2) {
            e.getPresentation().setEnabledAndVisible(true);
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}
