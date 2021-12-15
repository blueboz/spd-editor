package cn.boz.jb.plugin.idea.action;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public class NewSpdFileAction extends CreateFileFromTemplateAction implements DumbAware {

    public NewSpdFileAction() {
        super("Spd File", "", null);
    }

    public NewSpdFileAction(String text, String description, Icon icon) {
        super(text, description, icon);
    }

    @Override
    protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory psiDirectory, CreateFileFromTemplateDialog.@NotNull Builder builder) {
        builder.setTitle("New Spd File")
                .addKind("Spd File", SpdEditorIcons.FLOW_16_ICON, "Spd Process.spd");
    }

    @Override
    protected String getActionName(PsiDirectory psiDirectory, @NonNls @NotNull String s, @NonNls String s1) {
        return "Spd file";
    }


//    @Override
//    protected PsiElement @NotNull [] create(@NotNull String newName, PsiDirectory directory) throws Exception {
//        String fileName = newName + ".spd";
//        TemplateLoaderImpl instance = TemplateLoaderImpl.getInstance();
//        ProcessDefinition processDefinition = new ProcessDefinition();
//        processDefinition.setId("newprocess");
//        processDefinition.setName(processDefinition.getId());
//        byte[] bytes = instance.saveToBytes(processDefinition);
//
//        PsiFile psiFile = PsiFileFactory.getInstance(directory.getProject())
//                .createFileFromText(fileName, SpdFileType.INSTANCE, new String(bytes,"UTF-8"));
//        PsiElement add = directory.add(psiFile);
//        CreateFileFromTemplateDialog.
//        return new PsiElement[]{add};
//    }

//    @Override
//    protected @NlsContexts.DialogTitle String getErrorTitle() {
//        return "发生错误";
//    }

//    @Override
//    protected @NlsContexts.Command String getActionName(PsiDirectory psiDirectory, String s) {
//        return "new spd file";
//    }

//    @Override
//    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
//
//    }
}
