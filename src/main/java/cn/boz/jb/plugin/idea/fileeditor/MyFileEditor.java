package cn.boz.jb.plugin.idea.fileeditor;

import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class MyFileEditor implements FileEditor {

    private VirtualFile virtualFile;

    private ChartPanel chartPanel;

    public MyFileEditor(VirtualFile virtualFile) {
        this.virtualFile = virtualFile;
        chartPanel = new ChartPanel();
        chartPanel.loadFromFile(new File(virtualFile.getPath()));

    }

    @Override
    public @NotNull JComponent getComponent() {
        return chartPanel;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return chartPanel;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getName() {
        return "钿哥的流程编辑器";
    }

    @Override
    public void setState(@NotNull FileEditorState fileEditorState) {

    }

    @Override
    public @Nullable VirtualFile getFile() {
        return virtualFile;
    }

    @Override
    public boolean isModified() {
        return chartPanel.isModified();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {
        //keneng
//        PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent
//        propertyChangeListener.propertyChange();
//        System.out.println(propertyChangeListener);
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {
        System.out.println(propertyChangeListener);
    }

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void dispose() {

    }

    @Override
    public <T> @Nullable T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T t) {

    }
}
