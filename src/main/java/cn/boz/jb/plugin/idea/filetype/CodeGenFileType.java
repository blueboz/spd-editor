package cn.boz.jb.plugin.idea.filetype;

import com.intellij.ide.highlighter.XmlLikeFileType;
import com.intellij.json.JsonFileType;
import com.intellij.json.JsonLanguage;
import com.intellij.json.psi.JsonFile;
import com.jetbrains.jsonSchema.JsonSchemaFileType;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CodeGenFileType extends JsonFileType {
    public static final CodeGenFileType INSTANCE = new CodeGenFileType();

    private CodeGenFileType() {
        super(CodeGenLanguage.INSTANCE);
    }


    @Override
    public @NonNls @NotNull String getName() {
        return "CodeGen File";
    }

    @Override
    public @NotNull String getDescription() {
        return "CodeGen File";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "codegen";
    }

    @Override
    public @Nullable Icon getIcon() {
        return SpdEditorIcons.CODEGEN_16_ICON;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

}
