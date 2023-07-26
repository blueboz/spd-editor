package cn.boz.jb.plugin.addbom;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GenerateCodeAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if(virtualFile==null){
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        boolean b = virtualFile.getName().endsWith(".codegen");
        if(b){
            e.getPresentation().setEnabledAndVisible(true);
        }else{
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        VirtualFile virtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        try {
            byte[] bytes = Files.readAllBytes(Path.of(virtualFile.getPath()));
            String configText = new String(bytes);
            JSONObject mapper = JSON.parseObject(configText);
            System.out.println(mapper);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
