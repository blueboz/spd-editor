package cn.boz.jb.plugin.idea.filetype;

import com.intellij.icons.AllIcons;
import com.intellij.ide.highlighter.XmlLikeFileType;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SpdFileType extends XmlLikeFileType {

    public static final SpdFileType INSTANCE = new SpdFileType();

    private SpdFileType() {
        super(SpdLanguage.INSTANCE);
    }


    @Override
    public @NonNls @NotNull String getName() {
        return "SpdFile";
    }

    @Override
    public @NotNull String getDescription() {
        return "Erayt Flow File";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "spd";
    }

    @Override
    public @Nullable Icon getIcon() {
        return SpdEditorIcons.FLOW_16_ICON;
    }


}
