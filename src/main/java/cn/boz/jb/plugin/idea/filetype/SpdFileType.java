package cn.boz.jb.plugin.idea.filetype;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class SpdFileType implements FileType {

    public static final SpdFileType INSTANCE=new SpdFileType();
    @Override
    public @NonNls @NotNull String getName() {
        return "Spd";
    }

    @Override
    public  @NotNull String getDescription() {
        return "Erayt Flow";
    }

    @Override
    public  @NotNull String getDefaultExtension() {
        return "spd";
    }

    @Override
    public @Nullable Icon getIcon() {

        return SpdEditorIcons.FLOW_GRAY_16_ICON;
    }

    @Override
    public boolean isBinary() {
        return false;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public @Nullable String getCharset(@NotNull VirtualFile virtualFile, byte @NotNull [] bytes) {
        return null;
    }
}
