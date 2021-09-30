package cn.boz.jb.plugin.idea.filetype;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class SpdFileType implements FileType {
    @Override
    public @NonNls @NotNull String getName() {
        return "Spd";
    }

    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "Erayt Flow";
    }

    @Override
    public @NlsSafe @NotNull String getDefaultExtension() {
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
}
