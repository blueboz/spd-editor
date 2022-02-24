package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.addbom.utils.XmlUtils;
import cn.boz.jb.plugin.idea.utils.ChangeUtils;
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

public class FileHistoryDiffAction extends AnAction {


    /**
     * Git单个文件提交历史框 部分示例代码
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
        final Change @NotNull [] changes = e.getRequiredData(VcsDataKeys.CHANGES);
        List<Change> result = ContainerUtil.newArrayList(changes);
        if (revisions.length == 1) {
            //如果是一个
            Change change = result.get(0);
            try {

                String changeAfterContent = ChangeUtils.getChangeAfterContent(change);
                String changeAfterTitle = ChangeUtils.getChangeAfterTitle(change);
                String changeBeforeContent = ChangeUtils.getChangeBeforeContent(change);
                String changeBeforeTitle = ChangeUtils.getChangeBeforeTitle(change);

                CompareUtils.compare(XmlUtils.readXmlAndSortAndFormat(changeBeforeContent)
                        , changeBeforeTitle, XmlUtils.readXmlAndSortAndFormat(changeAfterContent), changeAfterTitle, XmlFileType.INSTANCE, e.getProject(), ChangeUtils.getChangeAvaiableFileName(change));
            } catch (Exception ee) {
                ee.printStackTrace();
            }

        } else if (revisions.length == 2) {
            VcsFileRevision rev1 = revisions[1];
            VcsFileRevision rev2 = revisions[0];
            Change change = result.get(0);
            try {
                String rev1s = new String(rev1.loadContent());
                String rev2s = new String(rev2.loadContent());
                String rev1num = rev1.getRevisionNumber().asString();
                String rev2num = rev2.getRevisionNumber().asString();
                CompareUtils.compare(XmlUtils.readXmlAndSortAndFormat(rev1s), rev1num, XmlUtils.readXmlAndSortAndFormat(rev2s), rev2num
                        , XmlFileType.INSTANCE, e.getProject(),ChangeUtils.getChangeAvaiableFileName(change));
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
        if (data != null) {
            if (data.length == 1) {
                e.getPresentation().setEnabledAndVisible(true);
            } else if (data.length == 2) {
                e.getPresentation().setEnabledAndVisible(true);
            } else {
                e.getPresentation().setEnabledAndVisible(false);
            }
        }
    }
}
