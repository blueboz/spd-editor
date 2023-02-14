package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.idea.configurable.DBInfo;
import cn.boz.jb.plugin.idea.configurable.SpdEditorDBListState;
import cn.boz.jb.plugin.idea.configurable.SpdEditorDBState;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChangeDbActionGroup extends DefaultActionGroup {




    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent e) {
        System.out.println("get child");
        System.out.println(e);
        SpdEditorDBListState instance = SpdEditorDBListState.getInstance(e.getProject());

        List<DBInfo> configList = instance.configList;
        AnAction[] actions=new AnAction[configList.size()];
        for (int i = 0; i < configList.size(); i++) {
            DBInfo dbInfo = configList.get(i);
            actions[i]=new AnAction() {
                {
                    getTemplatePresentation().setText(dbInfo.showName);
                }
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    SpdEditorDBState instance = SpdEditorDBState.getInstance(e.getProject());
                    instance.jdbcUrl=dbInfo.jdbcUrl;
                    instance.jdbcDriver=dbInfo.jdbcDriver;
                    instance.jdbcUserName=dbInfo.jdbcUserName;
                    instance.jdbcPassword=dbInfo.jdbcPassword;
                }
            };
        }


        return actions;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        System.out.println("update");
        System.out.println(e);
        super.update(e);
    }
}
