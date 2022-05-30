package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.floweditor.gui.control.PropertyObject;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.UserTask;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import cn.boz.jb.plugin.idea.configurable.SpdEditorDBSettings;
import cn.boz.jb.plugin.idea.dialog.EngineRightDialog;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.util.BackgroundTaskUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * 尝试获取权限脚本
 */
public class GotoRightAction extends AnAction {

    private EngineRightDialog engineRightDialog;
    private JBPopup popup;

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        ChartPanel chartPanel = anActionEvent.getData(ChartPanel.CURRENT_CHART_PANEL);
        if (chartPanel == null) {
            anActionEvent.getPresentation().setEnabledAndVisible(false);

            return;
        }
        if (chartPanel.getSelectedObject() instanceof UserTask) {
            anActionEvent.getPresentation().setEnabledAndVisible(true);
            return;
        }
        anActionEvent.getPresentation().setEnabledAndVisible(false);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        /**
         *
         * 这个DataProvider会往上级的Component元素进行查找的。所以，只要上级元素有，就能够拿到
         *  DataProvider dataProvider = getDataProviderEx(c);
         *   if (dataProvider != null) {
         *       Object data = this.getDataFromProvider(dataProvider, dataId, (Set)null, rule);
         *       if (data != null) {
         *           var8 = data;
         *           break;
         *       }
         *   }
         *
         *   c = ((Component)c).getParent();
         *         ChartPanel chartPanel = anActionEvent.getData(ChartPanel.CURRENT_CHART_PANEL);
         *         SpdEditor spdEditor = anActionEvent.getData(SpdEditor.SPD_EDITOR);
         */
        ChartPanel chartPanel = anActionEvent.getData(ChartPanel.CURRENT_CHART_PANEL);

        if (chartPanel == null) {
            return;
        }
        PropertyObject selectedObject = chartPanel.getSelectedObject();
        if (selectedObject instanceof UserTask) {
            UserTask userTask = (UserTask) selectedObject;
            String rights = userTask.getRights();
            ProgressIndicator pg = BackgroundTaskUtil.executeAndTryWait(progressIndicator -> () -> {
                DBUtils instance = DBUtils.getInstance();
                try (Connection connection = instance.getConnection()) {
                    //RIGHTS_, CANDIDATE_, SQLCONDITION_, DOCONDITION_
                    List<Map<String, Object>> engineRights = instance.queryEngineRights(connection, rights);
                    if (engineRights.size() < 1) {
                        Notification spdEditorNotification = new Notification("Spd Editor", SpdEditorIcons.FLOW_16_ICON, NotificationType.INFORMATION);
                        spdEditorNotification.setTitle("switcher");
                        spdEditorNotification.setContent("disable auto save spd editor");
                        spdEditorNotification.notify(anActionEvent.getProject());
                    } else if (engineRights.size() == 1) {
                        Map<String, Object> map = engineRights.get(0);
                        String candidate = (String) map.get("CANDIDATE_");
                        String sqlcondition = (String) map.get("SQLCONDITION_");
                        String docondition = (String) map.get("DOCONDITION_");
                        engineRightDialog = new EngineRightDialog(candidate, sqlcondition, docondition);
                        popup = JBPopupFactory.getInstance()
                                .createComponentPopupBuilder(engineRightDialog, null)
                                .setRequestFocus(true)
                                .setFocusable(true)
                                .setProject(anActionEvent.getProject())
                                .setCancelOnOtherWindowOpen(true)
                                .createPopup();
                        popup.showCenteredInCurrentWindow(anActionEvent.getProject());
                    } else if (engineRights.size() > 1) {
                    }
                } catch (Exception ee) {
                   DBUtils.dbExceptionProcessor(ee,anActionEvent.getProject());
                    //数据库
                }
            }, null);


            //查询
        }
    }
}
