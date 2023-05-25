package cn.boz.jb.plugin.addbom;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RemoveBomAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (virtualFile == null) {
            return;
        }
        byte[] bom = virtualFile.getBOM();
        if (bom == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        FileType fileType = psiFile.getFileType();
        String name = fileType.getName();
        if ("Image".equals(name)) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        e.getPresentation().setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        VirtualFile virtualFile = psiFile.getVirtualFile();

        //先对项目进行保存
        Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
        String text = document.getText();

        virtualFile.refresh(true, true, () -> {

            //使用异步刷新然后处理的操作
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //convert into utf-8 format
                byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
                baos.write(bytes);
                baos.flush();
                WriteCommandAction.runWriteCommandAction(anActionEvent.getProject(), () -> {
                    try {
                        virtualFile.setBinaryContent(baos.toByteArray());
                        virtualFile.refresh(true, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        });

    }

}
