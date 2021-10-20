package cn.boz.jb.plugin.addbom;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AddBomAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(project==null){
            e.getPresentation().setEnabledAndVisible(false);
            return ;
        }
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if(psiFile==null){
            e.getPresentation().setEnabledAndVisible(false);
            return ;
        }
        FileType fileType = psiFile.getFileType();
        String name = fileType.getName();
        if("Image".equals(name)){
            e.getPresentation().setEnabledAndVisible(false);
            return ;
        }
        e.getPresentation().setEnabledAndVisible(true);


    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        VirtualFile virtualFile = psiFile.getVirtualFile();
        FileType fileType = virtualFile.getFileType();
        if(!"SQL".equals(fileType.getName())){
            int res = Messages.showYesNoCancelDialog("注意文件实际类型为" + fileType.getName() + "是否要添加BOM，可能导致文件不可用", "注意", SpdEditorIcons.WARN_64_ICON);
            if(Messages.NO==res||Messages.CANCEL==res){
                return ;
            }
        }
        try {
            InputStream inputStream = virtualFile.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, virtualFile.getCharset()));
            String line;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] utf8bom={(byte)0xef,(byte)0xbb,(byte)0xbf};
            baos.write(utf8bom);
            while((line=bufferedReader.readLine())!=null){
                baos.write(line.getBytes(StandardCharsets.UTF_8));
                baos.write("\n".getBytes(StandardCharsets.UTF_8));
            }
            baos.flush();
            WriteCommandAction.runWriteCommandAction(anActionEvent.getProject(),()->{
                try {
                    virtualFile.setBinaryContent(baos.toByteArray());
                    virtualFile.refresh(true,true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
