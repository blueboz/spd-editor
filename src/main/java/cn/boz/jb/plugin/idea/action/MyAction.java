package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import cn.boz.jb.plugin.idea.fileeditor.SpdFileEditor;
import cn.boz.jb.plugin.idea.widget.SpdEditor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class MyAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent){
        Project project = anActionEvent.getProject();
        FileEditor selectedEditor = FileEditorManager.getInstance(project).getSelectedEditor();
        if(selectedEditor instanceof SpdFileEditor){
            SpdFileEditor mfe= (SpdFileEditor) selectedEditor;
            SpdEditor spdEditor = mfe.getSpdEditor();
            ChartPanel chartPanel = spdEditor.getChartPanel();
            chartPanel.undo();
        }
    }
}
