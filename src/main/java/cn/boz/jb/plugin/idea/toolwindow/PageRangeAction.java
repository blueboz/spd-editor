package cn.boz.jb.plugin.idea.toolwindow;

import com.intellij.database.access.DatabaseCredentials;
import com.intellij.database.console.DatabaseServiceViewContributor;
import com.intellij.database.console.session.DatabaseInEditorResults;
import com.intellij.database.model.DasDataSource;
import com.intellij.database.model.RawConnectionConfig;
import com.intellij.database.psi.DataSourceManager;
import com.intellij.database.view.ui.DataSourceManagerDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.NlsActions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.util.List;

public class PageRangeAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
//        InputEvent inputEvent = anActionEvent.getInputEvent();
//        Component component = inputEvent.getComponent();
//        List<DataSourceManager<?>> managers = DataSourceManager.getManagers(ProjectManager.getInstance().getDefaultProject());
//        DatabaseCredentials instance = DatabaseCredentials.getInstance();
//
//        DataSourceManagerDialog.showDialog(anActionEvent.getProject(), null, null);
//        for (DataSourceManager<?> manager : managers) {
//            List<DasDataSource> dataSources = (List<DasDataSource>) manager.getDataSources();
//            for (DasDataSource dataSource : dataSources) {
//                String name = dataSource.getName();
//                RawConnectionConfig connectionConfig = dataSource.getConnectionConfig();
//                System.out.println(connectionConfig);
//            }
//        }
    }

    @Override
    public @Nullable @NlsActions.ActionText String getTemplateText() {
        return "Template text";
    }


    @Override
    public boolean displayTextInToolbar() {
        return true;
    }


}
