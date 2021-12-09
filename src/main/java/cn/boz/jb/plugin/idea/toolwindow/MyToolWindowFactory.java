package cn.boz.jb.plugin.idea.toolwindow;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;

public class MyToolWindowFactory implements ToolWindowFactory {

    MyToolWindow myToolWindow;
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        myToolWindow = new MyToolWindow();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content title = contentFactory.createContent(myToolWindow, "菜单树", false);
        toolWindow.getContentManager().addContent(title);
    }
}
