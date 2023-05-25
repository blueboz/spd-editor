package cn.boz.jb.plugin.idea.filetype;

import com.intellij.ide.highlighter.XmlLikeFileType;
import com.intellij.lang.xml.XMLLanguage;
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
        return "Spd File";
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
        return SpdEditorIcons.FLOW_GRAY_16_ICON;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

}
