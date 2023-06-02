package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.idea.utils.DialogUtils;
import cn.boz.jb.plugin.idea.utils.FreeMarkerUtils;
import cn.boz.jb.plugin.idea.utils.MyStringUtils;
import cn.boz.jb.plugin.idea.utils.NotificationUtils;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiTreeUtil;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateEngineActionAction  extends AnAction implements ClipboardOwner {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        VirtualFile virtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        if (editor == null || psiFile == null || virtualFile == null) {
            return;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        PsiMethod method = PsiTreeUtil.getContextOfType(element, PsiMethod.class);
        PsiClass clazz = PsiTreeUtil.getContextOfType(element, PsiClass.class);
        PsiJavaFile javaFile = PsiTreeUtil.getContextOfType(element, PsiJavaFile.class);
        if (method == null) {
            NotificationUtils.warnning("无法定位方法", "请移动到方法名或者方法作用域", anActionEvent.getProject());
            return;
        }
        PsiParameterList parameterList = method.getParameterList();
        PsiParameter[] parameters = parameterList.getParameters();
        Map<String, Object> mapper = new HashMap<>();
        List<Map> parameterNames = new ArrayList<>();
        for (PsiParameter parameter : parameters) {
            Map<String, Object> pmap = new HashMap<>();
            PsiType type = parameter.getType();
            if(type instanceof PsiPrimitiveType){
                pmap.put("className", type.getCanonicalText());
            }else if(type instanceof PsiArrayType){
                pmap.put("className","[L"+ type.getDeepComponentType().getCanonicalText());
            }else{
                pmap.put("className",((PsiClassReferenceType) type).rawType().getCanonicalText());
            }
            pmap.put("shortName", parameter.getName());
            parameterNames.add(pmap);
        }
        mapper.put("parameters", parameterNames);
        mapper.put("actionId", "/" + method.getName() + ".do");
        PsiType returnType = method.getReturnType();
        mapper.put("needOutput",true);

        if(returnType instanceof PsiPrimitiveType){
            mapper.put("retBeanName", returnType.getCanonicalText());
        }else if(returnType instanceof PsiArrayType){
            mapper.put("retBeanName",""+ returnType.getDeepComponentType().getCanonicalText()+"s");
        }else{
            mapper.put("retBeanName",((PsiClassReferenceType) returnType).rawType().getCanonicalText());
        }
        mapper.put("retBeanName",MyStringUtils.firstCharLower((String)mapper.get("retBeanName")));

        String packageName = javaFile.getPackageName();
        String findStr = "com.erayt.xfunds.";
        String namespace = packageName;
        if (packageName.indexOf(findStr) != -1) {
            namespace = packageName.substring("com.erayt.xfunds.".length());
            if (namespace.indexOf(".") != -1) {
                namespace=namespace.substring(0,namespace.indexOf(".")-1);
            }
        }else{
            if(namespace.indexOf(".")!=-1){
                namespace=namespace.substring(namespace.lastIndexOf(".")+1);
            }
        }
        mapper.put("namespace", namespace);
        mapper.put("beanName",MyStringUtils.firstCharLower(clazz.getName()));
        mapper.put("methodName",method.getName());

        String process = FreeMarkerUtils.getINST().process("engineAction.sql", mapper);
        System.out.println(process);


        DialogUtils.hintSqlDialog(process,this,anActionEvent.getProject(),clazz.getName());

//        PsiElement psiRoot = PsiManager.getInstance(project).findFile(virtualFile);

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        boolean b = editor != null && psiFile != null;
        if (b) {
            FileType fileType = psiFile.getFileType();
            if (fileType instanceof JavaFileType) {
                e.getPresentation().setEnabled(true);
                e.getPresentation().setVisible(true);
                return;
            }
        }

    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }
}
