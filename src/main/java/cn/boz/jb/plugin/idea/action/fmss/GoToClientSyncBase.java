package cn.boz.jb.plugin.idea.action.fmss;

import cn.boz.jb.plugin.idea.notification.SpdEditorNotification;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.sql.Connection;

public class GoToClientSyncBase extends AnAction {


    public GoToClientSyncBase() {
        super("ClientSync");
    }

    public GoToClientSyncBase(@Nullable @Nls(capitalization = Nls.Capitalization.Title) String text) {
        super(text);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        //打开如下目录，并且查询系统当前日期，并且建立虚拟文件
        //由于客户只更不增，所以虚拟的文件建议从库里面搜索已有的客户
        String syncBasePath = "D:/cdadmin/UFSM/FMSS/save/FMTM/";
        DBUtils dbUtils = DBUtils.getInstance();

        try (Connection connection = DBUtils.getConnection(anActionEvent.getProject())) {
            String sysDay = dbUtils.querySysDay(connection);
            File file = new File(syncBasePath + sysDay);
            //临时目录
            if (!file.exists()) {
                file.mkdirs();
            }
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            SpdEditorNotification.get(anActionEvent.getProject()).notifyDbInvalid();
        }

    }


}
