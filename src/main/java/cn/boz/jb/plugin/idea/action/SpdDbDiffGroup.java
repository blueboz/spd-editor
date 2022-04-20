package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import cn.boz.jb.plugin.idea.utils.CompareUtils;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import cn.boz.jb.plugin.idea.widget.SpdEditor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
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
            List<String> sqls = chartPanel.generateSql();
            Map<String, String> dataTobeCompare = DBUtils.getInstance().fetchAndCompare(sqls, chartPanel.getId(), wrap);
            String old = dataTobeCompare.get("old");
            String aNew = dataTobeCompare.get("new");
            CompareUtils.compare(old, "db version", aNew, "current ver", PlainTextFileType.INSTANCE, anActionEvent.getProject(), "Sql Into DbCompare");
        }
    }

}
