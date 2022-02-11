package cn.boz.jb.plugin.idea.fileeditor;

import cn.boz.jb.plugin.idea.filetype.SpdFileType;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SpdFileEditorProvider implements FileEditorProvider, DumbAware {
    private Map<VirtualFile,FileEditor> fileFileEditorMap=new HashMap<>();

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        if (virtualFile.getFileType() instanceof SpdFileType) {
            return true;
        }
        return false;
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        if(!fileFileEditorMap.containsKey(virtualFile)){
            synchronized (SpdFileEditorProvider.class){
                if(!fileFileEditorMap.containsKey(virtualFile)){
                    fileFileEditorMap.put(virtualFile,new SpdFileEditor(project, virtualFile));
                }
            }
        }
        return fileFileEditorMap.get(virtualFile);
    }

    @Override
    public @NotNull @NonNls String getEditorTypeId() {
        return "myspdeditor";
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
    }

    @Override
    public void disposeEditor(@NotNull FileEditor editor) {
        FileEditorProvider.super.disposeEditor(editor);
    }
}
