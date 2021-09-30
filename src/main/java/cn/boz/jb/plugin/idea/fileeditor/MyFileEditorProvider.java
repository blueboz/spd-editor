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

public class MyFileEditorProvider implements FileEditorProvider, DumbAware {
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
//        MessageBus messageBus = project.getMessageBus();
//        messageBus.connect().subscribe(VirtualFileManager.VFS_CHANGES);
        if(virtualFile.getFileType() instanceof SpdFileType){
            return true;
        }
        return false;
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return new MyFileEditor(virtualFile);
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
