package cn.boz.jb.plugin.idea.action.engineaction;

import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import cn.boz.jb.plugin.idea.bean.XfundsBatch;
import cn.boz.jb.plugin.idea.utils.Constants;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.ListTableModel;
import icons.SpdEditorIcons;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;

public class MockStartBatchAction extends MockStartBatchWithDataAction  {


    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        doLogic(anActionEvent,"{}");
    }

}
