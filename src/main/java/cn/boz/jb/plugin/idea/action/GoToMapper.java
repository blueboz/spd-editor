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


public class GoToMapper extends AnAction {

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
        PsiClass containingClass;
        if (containingMethod == null) {
            containingClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
            if (containingClass == null) {
                return;
            }
            //进入类
        } else {
            containingClass = containingMethod.getContainingClass();
        }

        String qualifiedName = containingClass.getQualifiedName();


        PsiFile psiFileV = PsiManager.getInstance(project).findFile(virtualFile);
        PsiDirectory containingDirectory = psiFileV.getContainingDirectory();

        PsiDirectory[] subdirectories = containingDirectory.getSubdirectories();

        crossDirectory(containingDirectory, new DoEachPsifile() {

            @Override
            public boolean doPsiFile(PsiFile psiFile) {
                if (psiFile.getFileType() instanceof XmlFileType) {
                    XmlDocument document = PsiTreeUtil.getChildOfType(psiFile, XmlDocument.class);
                    XmlTag sqlMapMaybe = document.getRootTag();
                    if (!"sqlMap".equals(sqlMapMaybe.getName())) {
                        return false;
                    }
                    XmlAttribute namespace = sqlMapMaybe.getAttribute("namespace");
                    String classNameMaybe = namespace.getValue();
                    if (!classNameMaybe.equals(qualifiedName)) {
                        return false;
                    }
                    XmlElement ele = namespace;
                    if (containingMethod != null) {

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
                                if (value.equals(containingMethod.getName())) {
                                    break;
                                }
                            }
                        }
                        JBPopup jbpopup = NavigationUtil.getPsiElementPopup(new PsiElement[]{ele, namespace}, "选择文件");
                        jbpopup.showCenteredInCurrentWindow(project);
                        return true;

                    } else {
                        JBPopup jbpopup = NavigationUtil.getPsiElementPopup(new PsiElement[]{namespace}, "选择文件");
                        jbpopup.showCenteredInCurrentWindow(project);
                        return true;

                    }
//                    Navigatable navigatable = PsiNavigationSupport.getInstance().createNavigatable(project, virtualFile, 0);
//                    navigatable.navigate();
                }
                return false;
            }
        });



    }

    /**
     * @param directory
     */
    public boolean crossDirectory(PsiDirectory directory, DoEachPsifile doer) {
        PsiFile[] files = directory.getFiles();
        for (PsiFile file : files) {
            boolean b = doer.doPsiFile(file);
            if (b == true) {
                return true;
            }
        }
        //遍历完文件后再遍历目录
        PsiDirectory[] subdirectories = directory.getSubdirectories();
        for (PsiDirectory subdirectory : subdirectories) {
            boolean b = crossDirectory(subdirectory, doer);
            if (b) {
                return true;
            }
        }
        //表示本次没有遍历到，接着遍历就好了
        return false;
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
                e.getPresentation().setVisible(true);
            } else {
                e.getPresentation().setEnabled(false);
                e.getPresentation().setVisible(false);
            }
        }
    }

}


interface DoEachPsifile {

    /**
     * 返回true终止后续遍历
     *
     * @param psiFile
     * @return
     */
    public boolean doPsiFile(PsiFile psiFile);
}

