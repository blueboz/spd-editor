package cn.boz.jb.plugin.idea.action;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsActions;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.util.Consumer;
import com.intellij.util.SlowOperations;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class NewSpdFileAction extends CreateFileFromTemplateAction implements DumbAware {
    public NewSpdFileAction() {
        super("new Spd File","",null);
    }

    public NewSpdFileAction(@NlsActions.ActionText String text, @NlsActions.ActionDescription String description, Icon icon) {
        super(text, description, icon);
    }

    @Override
    protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory psiDirectory, CreateFileFromTemplateDialog.@NotNull Builder builder) {
        builder.setTitle("Spd File")
                .addKind("New Spd File",null,"spd");
        builder.show("t1", "t2", new CreateFileFromTemplateDialog.FileCreator<PsiElement>() {
            @Override
            public @Nullable PsiElement createFile(@NonNls @NotNull String s, @NonNls @NotNull String s1) {
                return null;
            }

            @Override
            public @NlsContexts.Command @NotNull String getActionName(@NonNls @NotNull String s, @NonNls @NotNull String s1) {
                return null;
            }

            @Override
            public boolean startInWriteAction() {
                return true;
            }
        }, new Consumer<PsiElement>() {
            @Override
            public void consume(PsiElement psiElement) {
                if (psiElement != null) {
                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    System.out.println(psiElement);


                }
            }
        });
    }

    @Override
    protected @NlsContexts.Command String getActionName(PsiDirectory psiDirectory, @NonNls @NotNull String s, @NonNls String s1) {
        return "new Spd file";
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
