package cn.boz.jb.plugin.idea.action;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class NewCodeGenAction extends CreateFileFromTemplateAction implements DumbAware {

    public NewCodeGenAction() {
        super("CodeGen File", "", null);
    }

    public NewCodeGenAction(String text, String description, Icon icon) {
        super(text, description, icon);
    }

    @Override
    protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory psiDirectory, CreateFileFromTemplateDialog.@NotNull Builder builder) {
        builder.setTitle("New CodeGen File")
                .addKind("CodeGen File", SpdEditorIcons.CODEGEN_16_ICON, "config.codegen");
    }

    @Override
    protected String getActionName(PsiDirectory psiDirectory, @NonNls @NotNull String s, @NonNls String s1) {
        return "CodeGen file";
    }


}
