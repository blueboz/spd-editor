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

public class SpdSqlDiffAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Component requiredData = anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
        if (!(requiredData instanceof ChartPanel)) {
            return;
        }
        SpdEditor spdEditor = (SpdEditor) SwingUtilities.getAncestorOfClass(SpdEditor.class, requiredData);
        JBPopupFactory instance = JBPopupFactory.getInstance();
        BaseListPopupStep<String> baseListPopupStep = new BaseListPopupStep<String>("Wrap Or Not", Arrays.asList("Wrap", "NoWrap")) {

            @Override
            public @Nullable PopupStep<?> onChosen(String selectedValue, boolean finalChoice) {

                if (finalChoice) {
                    return doFinalStep(() -> doRun(selectedValue));
                }
                return PopupStep.FINAL_CHOICE;
            }

            private void doRun(String selectedValue) {
                if (spdEditor instanceof SpdEditor) {
                    ChartPanel chartPanel = spdEditor.getChartPanel();
                    List<String> sqls = chartPanel.generateSql();
                    Map<String, String> dataTobeCompare = DBUtils.getInstance().fetchAndCompare(sqls, chartPanel.getId(), "Wrap".equals(selectedValue));
                    String old = dataTobeCompare.get("old");
                    String aNew = dataTobeCompare.get("new");
                    CompareUtils.compare(old, "db version", aNew, "current ver", PlainTextFileType.INSTANCE, anActionEvent.getProject(), "Sql Into DbCompare");
                }
            }
        };
        ListPopup listPopup = instance.createListPopup(baseListPopupStep);
        MouseEvent me = (MouseEvent) anActionEvent.getInputEvent();
        listPopup.show(RelativePoint.fromScreen(me.getLocationOnScreen()));


    }
}
