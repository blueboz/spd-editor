package cn.boz.jb.plugin.idea.toolwindow;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public @Nullable String getTemplateText() {
        return "Template text";
    }


    @Override
    public boolean displayTextInToolbar() {
        return true;
    }


}
