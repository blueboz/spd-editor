package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.idea.configurable.SpdEditorState;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.lang.javascript.JavaScriptFileType;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 用于
 */
public class GoToRefFile extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        if (editor == null || psiFile == null) {
            return;
        }
        int offset = editor.getCaretModel().getOffset();
        Project project = anActionEvent.getData(CommonDataKeys.PROJECT);
        VirtualFile virtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);

        FileType fileType = psiFile.getFileType();
        if (fileType instanceof JavaScriptFileType) {
            PsiElement element = psiFile.findElementAt(offset);
            PsiElement context = element.getContext();
            if (context instanceof JSLiteralExpression) {
                JSLiteralExpression jsLiteralExpression = (JSLiteralExpression) context;
                System.out.println(jsLiteralExpression);
                String stringValue = jsLiteralExpression.getStringValue();
                String[] pathRel = stringValue.split("\\?");
                VirtualFile relFile = virtualFile.getParent().findFileByRelativePath(pathRel[0]);
                if (relFile != null) {

                    PsiFile relPsiFile = PsiManager.getInstance(project).findFile(relFile);
                    NavigationUtil.activateFileWithPsiElement(relPsiFile);
                } else {
                    HintManager.getInstance().showErrorHint(editor, "Cannot find declaration to go to");
                }

            }
        } else if (fileType instanceof XmlFileType) {
            PsiElement element = psiFile.findElementAt(offset);
            XmlAttribute attribute = PsiTreeUtil.getParentOfType(element, XmlAttribute.class);
            if (attribute != null) {
                //才有继续处理的必要
                String value = attribute.getValue();

                boolean tryToGotoFileRef = tryToGotoFileRef(psiFile, project, value);
                if (tryToGotoFileRef) {
                    return;
                }
                boolean tryToGotoClassRef = tryToGotoClassRef(project, value);
                if (tryToGotoClassRef) {
                    return;
                }
                boolean trytoGotoAction = tryToGotoAction(project, value);
                if (trytoGotoAction) {
                    return;
                }
                HintManager.getInstance().showErrorHint(editor, "Cannot find declaration to go to");
            }

        } else if (fileType instanceof HtmlFileType) {
            PsiElement element = psiFile.findElementAt(offset);
            XmlAttribute attribute = PsiTreeUtil.getParentOfType(element, XmlAttribute.class);
            if (attribute != null) {
                //才有继续处理的必要
                String value = attribute.getValue();
                String[] split = value.split("\\?");
                VirtualFile fileByRelativePath = psiFile.getVirtualFile().getParent().findFileByRelativePath(split[0]);
                if (fileByRelativePath != null) {
                    PsiFile targetJsFile = PsiManager.getInstance(project).findFile(fileByRelativePath);
                    NavigationUtil.activateFileWithPsiElement(targetJsFile);
                    return;
                } else {
                    HintManager.getInstance().showErrorHint(editor, "Cannot find declaration to go to");
                }
            }
        }

    }



    /**
     * 跳转到Engine Action
     *
     * @param project
     * @param value
     * @return
     */
    private boolean tryToGotoAction(Project project, String value) {

        try (Connection connection = DBUtils.getConnection(SpdEditorState.getInstance());
             Statement statement = connection.createStatement();
        ) {
            ResultSet resultSet = statement.executeQuery("select * from engine_action where id like '%" + value + "%'");
            while (resultSet.next()) {
            }
            ;
            //进行连接
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * @param project
     * @param value
     */
    private boolean tryToGotoClassRef(Project project, String value) {
        PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(value, GlobalSearchScope.allScope(project));
        if (aClass != null) {
            NavigationUtil.activateFileWithPsiElement(aClass);
            return true;
        }
        return false;
    }


    private boolean tryToGotoFileRef(PsiFile psiFile, Project project, String value) {
        String[] split = value.split("\\?");
        VirtualFile fileByRelativePath = psiFile.getVirtualFile().getParent().findFileByRelativePath(split[0]);
        if (fileByRelativePath != null) {
            PsiFile targetJsFile = PsiManager.getInstance(project).findFile(fileByRelativePath);
            NavigationUtil.activateFileWithPsiElement(targetJsFile);
            return true;
        } else {
            return false;
            //可以尝试类全限定名
        }
    }

    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        boolean b = editor != null && psiFile != null;
        if (b) {
            FileType fileType = psiFile.getFileType();
            if (fileType instanceof HtmlFileType || fileType instanceof JavaScriptFileType || fileType instanceof XmlFile) {
                e.getPresentation().setEnabled(true);
                e.getPresentation().setVisible(true);
            } else {
                e.getPresentation().setEnabled(true);
                e.getPresentation().setVisible(true);
            }
        }
    }
}
