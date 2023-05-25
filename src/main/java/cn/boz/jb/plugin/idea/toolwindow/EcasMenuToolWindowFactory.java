package cn.boz.jb.plugin.idea.toolwindow;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class EcasMenuToolWindowFactory implements ToolWindowFactory {

    private EcasMenuToolWindow myToolWindow;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        myToolWindow = new EcasMenuToolWindow(project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content title = contentFactory.createContent(myToolWindow, "Ecas Menu Tree", false);
        toolWindow.getContentManager().addContent(title);
    }


}
