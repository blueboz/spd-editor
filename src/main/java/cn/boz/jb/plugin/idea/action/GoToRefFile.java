package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.idea.configurable.SpdEditorState;
import cn.boz.jb.plugin.idea.dialog.EngineActionDialog;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.intention.impl.IntentionHintComponent;
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
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.ListPopupStep;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.ui.popup.util.PopupUtil;
import com.intellij.openapi.util.Ref;
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
import org.jetbrains.annotations.Nullable;

import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Button;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

                boolean tryToGotoFileRef = tryToGotoFileRef(psiFile, project, stringValue);
                if (tryToGotoFileRef) {
                    return;
                }
                boolean tryToGotoClassRef = tryToGotoClassRef(project, stringValue);
                if (tryToGotoClassRef) {
                    return;
                }
                boolean trytoGotoAction = tryToGotoAction(project, stringValue);
                if (trytoGotoAction) {
                    return;
                }
//                HintManager.getInstance().showErrorHint(editor, "Cannot find declaration to go to");
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

    private EngineActionDialog myFramw;

    /**
     * 跳转到Engine Action
     *
     * @param project
     * @param value
     * @return
     */
    private boolean tryToGotoAction(Project project, String value) {
        if(value==null){
            return false;
        }
        if(!value.contains(".do")){
            return false;
        }

        DBUtils instance = DBUtils.getInstance();
        Ref<Boolean> result=new Ref<>();
        Ref<EngineActionDialog> popupRef=new Ref<>();
        Ref<List<String>> ids=new Ref<>();
        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {

            try (Connection connection = instance.getConnection(SpdEditorState.getInstance());
            ) {
                List<Map<String, Object>> actions = instance.queryEngineActionWithIdLike(connection, value);
                if (actions.size() == 0) {
                    result.set(false);
                } else if (actions.size() == 1) {
                    Map<String, Object> engineAction = actions.get(0);
                    String id_ = (String) engineAction.get("ID_");
                    List<Map<String, Object>> actionInputs = instance.queryEngineActionInputIdMatch(connection, id_);
                    List<Map<String, Object>> actionOutputs = instance.queryEngineActionOutputIdMatch(connection, id_);
                    myFramw = new EngineActionDialog(engineAction,actionInputs,actionOutputs);
                    popupRef.set(myFramw);

//                    popup.showCenteredInCurrentWindow(project);

//

                } else {
                    List<String> ids_ = actions.stream().map(it -> (String) it.get("ID_")).collect(Collectors.toList());
                    ids.set(ids_);


                    //多选框
                }


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
            } catch (Exception e) {
                e.printStackTrace();
            }


        }, "数据获取中...", true, ProjectManager.getInstance().getDefaultProject());

        if(popupRef.get()!=null){

            EngineActionDialog myFramw = popupRef.get();
            JBPopup popup = JBPopupFactory.getInstance().createComponentPopupBuilder(myFramw, null)
                    .setCancelOnClickOutside(true)
                    .setFocusable(true)
                    .setRequestFocus(true)
                    .createPopup();
            popup.showInFocusCenter();
            return true;
        }

        List<String> strings = ids.get();

        if(strings!=null){
            BaseListPopupStep selPopup = new BaseListPopupStep("action",strings){
                private Object selectedValue;
                @Override
                public @Nullable PopupStep<?> onChosen(Object selectedValue, boolean finalChoice) {
                    this.selectedValue=selectedValue;

                    return super.onChosen(selectedValue, finalChoice);
                }

                @Override
                public PopupStep<?> doFinalStep(@Nullable Runnable runnable) {
                    if(selectedValue!=null){
                        tryToGotoAction(project, (String) selectedValue);
                    }
                    return super.doFinalStep(runnable);
                }

                @Override
                public @Nullable Runnable getFinalRunnable() {
                    return super.getFinalRunnable();
                }
            };

            selPopup.isSelectable(true);
            ListPopup listPopup = JBPopupFactory.getInstance().createListPopup(selPopup);
            listPopup.showInFocusCenter();
            return true;
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
