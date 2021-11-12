package cn.boz.jb.plugin.idea.action;

import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.lang.jvm.JvmMethod;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class GoToDao extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        if (editor == null || psiFile == null) {
            return;
        }
        int offset = editor.getCaretModel().getOffset();
        Project project = anActionEvent.getData(CommonDataKeys.PROJECT);
        VirtualFile virtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);

        PsiElement element = psiFile.findElementAt(offset);

        if (element == null) {
            return;
        }

        PsiFile containingFile = element.getContainingFile();
        if (!(containingFile instanceof XmlFile)) {
            return;
        }

        XmlTag sqlMapMaybe = ((XmlFileImpl) element.getContainingFile()).getRootTag();
        XmlAttribute namespace = sqlMapMaybe.getAttribute("namespace");
        String classNameMaybe = namespace.getValue();
        if (classNameMaybe != null) {
            PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(classNameMaybe, GlobalSearchScope.allScope(project));

            XmlElement ele;
            //确定选中元素的标签
            if (!(element instanceof XmlElement)) {
                ele = PsiTreeUtil.getParentOfType(element, XmlElement.class);
            } else {
                ele = (XmlElement) element;
            }
            List<String> namelist = Stream.of("select", "update", "insert", "delete").collect(Collectors.toList());
            while (true) {
                XmlTag tg = PsiTreeUtil.getParentOfType(ele, XmlTag.class);
                if (tg == null) {
                    NavigationUtil.activateFileWithPsiElement(aClass);
                    return;
                }
                String name = tg.getName();
                if (namelist.contains(name)) {
                    XmlAttribute methodName = tg.getAttribute("id");
                    //导航到Java类与其方法
                    String value = methodName.getValue();
                    JvmMethod[] methodsByName = aClass.findMethodsByName(value);
                    if (methodsByName.length > 0) {
                        PsiElement[] psiElements = new PsiElement[methodsByName.length];
                        for (int i = 0; i < methodsByName.length; i++) {
                            psiElements[i] = methodsByName[i].getSourceElement();
                        }
                        if(psiElements.length>1){
                            JBPopup jbpopup = NavigationUtil.getPsiElementPopup(psiElements, "选择文件");
                            jbpopup.showCenteredInCurrentWindow(project);
                        }else{
                            //直接跳过去了
                            NavigationUtil.activateFileWithPsiElement(psiElements[0]);
                        }
                        return;
                    } else {
                        NavigationUtil.activateFileWithPsiElement(aClass);
                        return;
                    }
                }
                ele = tg;
            }


        }


    }

    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        boolean b = editor != null && psiFile != null;
        if (b) {
            FileType fileType = psiFile.getFileType();
            if (fileType instanceof XmlFileType) {
                e.getPresentation().setEnabled(true);
                e.getPresentation().setVisible(true);
            } else {
                e.getPresentation().setEnabled(false);
                e.getPresentation().setVisible(false);
            }
        }

    }

}


