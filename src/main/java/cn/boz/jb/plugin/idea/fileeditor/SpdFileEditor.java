package cn.boz.jb.plugin.idea.fileeditor;

import cn.boz.jb.plugin.idea.widget.SpdEditor;
import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.EditorCoreUtil;
import com.intellij.openapi.editor.ex.util.EditorUIUtil;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SpdFileEditor implements FileEditor {


    private VirtualFile virtualFile;
    private Project project;

    private SpdEditor spdEditor;
    private FileEditorState fileEditorState;
    private Map userdata = new HashMap<>();

    public SpdEditor getSpdEditor() {
        return spdEditor;
    }

    public void setSpdEditor(SpdEditor spdEditor) {
        this.spdEditor = spdEditor;
    }

    public SpdFileEditor(Project project, VirtualFile virtualFile) {
        this.virtualFile = virtualFile;
        this.project = project;
        spdEditor = new SpdEditor(project, virtualFile);
        spdEditor.load();
        spdEditor.getChartPanel().registerProcessSaveListener((bs) -> {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                virtualFile.refresh(true, true, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            virtualFile.setBinaryContent(bs);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            });
        });




    }

    @Override
    public @NotNull JComponent getComponent() {
        return spdEditor;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {

        return spdEditor.getChartPanel();
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getName() {
        return "Spd Editor";
    }

    @Override
    public void setState(@NotNull FileEditorState fileEditorState) {
        this.fileEditorState = fileEditorState;
    }

    @Override
    public @Nullable VirtualFile getFile() {
        return virtualFile;
    }

    @Override
    public boolean isModified() {
        return spdEditor.isModified();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {
        System.out.println("Add property ChangeListener..." + propertyChangeListener);
        //keneng
//        PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent
//        propertyChangeListener.propertyChange();
//        System.out.println(propertyChangeListener);
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {
    }

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
//        System.out.println("get current location");
        return new FileEditorLocation() {
            @Override
            public @NotNull FileEditor getEditor() {
                return SpdFileEditor.this;
            }

            @Override
            public int compareTo(@NotNull FileEditorLocation o) {
                return o.compareTo(this);
            }
        };
    }

    @Override
    public void dispose() {

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> @Nullable T getUserData(@NotNull Key<T> key) {
        return (T) userdata.get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T t) {
        userdata.put(key, t);
    }

}
