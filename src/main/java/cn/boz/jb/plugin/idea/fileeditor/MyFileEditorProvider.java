package cn.boz.jb.plugin.idea.fileeditor;

import cn.boz.jb.plugin.idea.filetype.SpdFileType;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MyFileEditorProvider implements FileEditorProvider, DumbAware {
    private static Map<VirtualFile, FileEditor> fileEditorMap = new HashMap<>();

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        if (virtualFile.getFileType() instanceof SpdFileType) {
            return true;
        }
        return false;
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return new MyFileEditor(project, virtualFile);
    }

    @Override
    public @NotNull @NonNls String getEditorTypeId() {
        return "myspdeditor";
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }
}
