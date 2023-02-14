package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import cn.boz.jb.plugin.idea.configurable.SpdEditorDBState;
import cn.boz.jb.plugin.idea.utils.CompareUtils;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import cn.boz.jb.plugin.idea.widget.SpdEditor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class SpdDbDiffGroup {
    public static class SpdDbDiffKeepWrapAction extends AnAction {
        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            //保持所有wrap
            dbdiff(anActionEvent, false);
        }
    }

    public static class SpdDbDiffRemoveWrapAction extends AnAction {
        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            //移除所有wrap
            dbdiff(anActionEvent, true);
        }
    }

    private static void dbdiff(@NotNull AnActionEvent anActionEvent, boolean wrap) {
        Component requiredData = anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
        if (!(requiredData instanceof ChartPanel)) {
            return;
        }
        SpdEditor spdEditor = (SpdEditor) SwingUtilities.getAncestorOfClass(SpdEditor.class, requiredData);

        if (spdEditor instanceof SpdEditor) {
            ChartPanel chartPanel = spdEditor.getChartPanel();
            List<String> sqls = chartPanel.generateSortedSql();
            Map<String, String> dataTobeCompare = DBUtils.getInstance().fetchAndCompare(anActionEvent.getProject(),sqls, chartPanel.getId(), wrap);
            String old = dataTobeCompare.get("old");
            String aNew = dataTobeCompare.get("new");
            SpdEditorDBState instance = SpdEditorDBState.getInstance(anActionEvent.getProject());
            CompareUtils.compare(old, "db version with:"+instance.jdbcUrl, aNew, "current ver", PlainTextFileType.INSTANCE, anActionEvent.getProject(), "Sql Into DbCompare");
        }
    }

}
