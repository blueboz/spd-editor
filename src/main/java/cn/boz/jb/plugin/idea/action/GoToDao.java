package cn.boz.jb.plugin.idea.action;

import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;


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

        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (containingMethod == null) {
            return;
        }
        String methodName = containingMethod.getName();
        PsiClass containingClass = containingMethod.getContainingClass();
        String qualifiedName = containingClass.getQualifiedName();


        PsiFile psiFileV = PsiManager.getInstance(project).findFile(virtualFile);
        PsiDirectory containingDirectory = psiFileV.getContainingDirectory();
        PsiDirectory[] subdirectories = containingDirectory.getSubdirectories();

        for (PsiDirectory subdir : subdirectories) {
            System.out.println("----------------");
            System.out.println(subdir.getName());
            PsiFile[] psiFiles = subdir.getFiles();
            for (PsiFile file : psiFiles) {
                System.out.println(file.getFileType());
                System.out.println(file.getName());
                //遍历显然就不是一个好的方法
                if (file.getFileType() instanceof XmlFileType) {
                    //解析xml文件?
                    XmlDocument document = PsiTreeUtil.getChildOfType(file, XmlDocument.class);
                    XmlTag sqlMapMaybe = document.getRootTag();
                    if (!"sqlMap".equals(sqlMapMaybe.getName())) {

                        continue;
                    }
                    XmlAttribute namespace = sqlMapMaybe.getAttribute("namespace");
                    String classNameMaybe = namespace.getValue();
                    if (!classNameMaybe.equals(qualifiedName)) {
                        continue;
                    }
                    XmlElement ele = namespace;
                    while (true) {
                        XmlTag tags = PsiTreeUtil.getNextSiblingOfType(ele, XmlTag.class);
                        if (tags == null) {
                            break;
                        }
                        ele = tags;
                        XmlAttribute id = tags.getAttribute("id");
                        if (id != null) {
                            String value = id.getValue();
                            //判断ID是否已经有了
                            if (value.equals(methodName)) {
                                System.out.println("yes! it's me ");
                                break;
                            }
                        }
                    }
                    JBPopup jbpopup = NavigationUtil.getPsiElementPopup(new PsiElement[]{ele}, "选择文件");
                    jbpopup.showCenteredInCurrentWindow(project);
//                    Navigatable navigatable = PsiNavigationSupport.getInstance().createNavigatable(project, virtualFile, 0);
//                    navigatable.navigate();
                    return;
                }
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
            if (fileType instanceof JavaFileType) {
                e.getPresentation().setEnabled(true);
                e.getPresentation().setVisible(false);
            } else {
                e.getPresentation().setEnabled(false);
                e.getPresentation().setVisible(false);
            }
        }
    }

}


