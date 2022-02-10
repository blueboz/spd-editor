package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.idea.configurable.SpdEditorDBState;
import cn.boz.jb.plugin.idea.dialog.EngineActionDialog;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManagerImpl;
import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.lang.javascript.JavaScriptFileType;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.jvm.JvmMethod;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.SpeedSearchFilter;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用于导航到指定文件
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

        FileType fileType = psiFile.getFileType();
        PsiElement element = psiFile.findElementAt(offset);

        if (fileType instanceof JavaScriptFileType) {
            PsiElement context = element.getContext();
            if (context instanceof JSLiteralExpression) {
                JSLiteralExpression jsLiteralExpression = (JSLiteralExpression) context;
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
            }
        } else if (fileType instanceof XmlFileType || fileType instanceof HtmlFileType) {
            //xml 文件可能需要区分是不是mapper
            XmlTag rootTag = ((XmlFileImpl) element.getContainingFile()).getRootTag();
            if ("sqlMap".equals(rootTag.getName())) {
                if (tryToGotoDao(project, element)) {
                    return;
                }
            } else if ("html".equals(rootTag.getName())) {
                XmlAttribute attribute = PsiTreeUtil.getParentOfType(element, XmlAttribute.class);
                if (attribute != null) {
                    //才有继续处理的必要
                    if (tryToGotoFileRef(psiFile, project, attribute.getValue())) {
                        return;
                    }
                }
            }
        } else if (fileType instanceof JavaFileType) {
            //判断是不是Dao接口，如果是Dao 接口，name
            if (tryToGoToMapper(element)) {
                return;
            }
        }
        //try to use this function else do not use it.
        String text = element.getText();
        String allContributorsGroupId = SearchEverywhereManagerImpl.ALL_CONTRIBUTORS_GROUP_ID;
        SearchEverywhereManager.getInstance(project).show(allContributorsGroupId, text, anActionEvent);
//        HintManager.getInstance().showErrorHint(editor, "Cannot find declaration to go to");

    }

    /**
     * 从java接口跳转到mapper文件里面
     *
     * @param element
     * @return
     */
    private boolean tryToGoToMapper(PsiElement element) {
        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        PsiClass containingClass;
        if (containingMethod == null) {
            containingClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
            if (containingClass == null) {
                return false;
            }
            //进入类
        } else {
            containingClass = containingMethod.getContainingClass();
        }

        String qualifiedName = containingClass.getQualifiedName();
        PsiFile psiFileV = element.getContainingFile();
        PsiDirectory containingDirectory = psiFileV.getContainingDirectory();

        return crossDirectory(containingDirectory, psiFile -> {
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
                XmlAttribute targetAttribute = null;
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
                                targetAttribute = id;
                                break;
                            }
                        }
                    }
                    if (targetAttribute != null) {
                        NavigationUtil.activateFileWithPsiElement(targetAttribute.getValueElement());
                    } else {
                        NavigationUtil.activateFileWithPsiElement(ele);
                    }
                    return true;

                } else {
                    NavigationUtil.activateFileWithPsiElement(namespace);
                    return true;

                }
            }
            return false;
        });
    }

    /**
     * 尝试从xml文件跳转到Dao
     *
     * @param project
     * @param element
     * @return
     */
    private boolean tryToGotoDao(Project project, PsiElement element) {
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
            List<String> namelist = List.of("select", "update", "insert", "delete");
            while (true) {
                XmlTag tg = PsiTreeUtil.getParentOfType(ele, XmlTag.class);
                if (tg == null) {
                    if (aClass != null) {
                        NavigationUtil.activateFileWithPsiElement(aClass);
                        return true;
                    } else {
                        return false;
                    }
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
                        if (psiElements.length > 1) {
                            JBPopup jbpopup = NavigationUtil.getPsiElementPopup(psiElements, "选择文件");
                            jbpopup.showCenteredInCurrentWindow(project);
                        } else {
                            //直接跳过去了
                            NavigationUtil.activateFileWithPsiElement(psiElements[0]);
                        }
                        return true;
                    } else {
                        NavigationUtil.activateFileWithPsiElement(aClass);
                        return true;
                    }
                }
                ele = tg;
            }
        }
        return false;

    }

    private JBPopup popup;
    private EngineActionDialog temporyDialog;

    /**
     * 跳转到Engine Action
     *
     * @param project
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
    private boolean tryToGotoAction(Project project, String value) {
        if (value == null) {
            return false;
        }
        if (!value.contains(".do")) {
            return false;
        }
        if (value.contains("?")) {
            ///只取问号前面部分
            value = value.split("\\?")[0];
        }
        if (value.contains("/")) {
            //a/b/c.do
            value = value.substring(value.lastIndexOf("/") + 1);
        }

        DBUtils instance = DBUtils.getInstance();
        Ref<Boolean> result = new Ref<>();
        Ref<EngineActionDataContainer> engineActionRef = new Ref<>();
        Ref<List<String>> ids = new Ref<>();
        final String query = value;
        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {

            try (Connection connection = DBUtils.getConnection(SpdEditorDBState.getInstance());
            ) {
                List<Map<String, Object>> actions = instance.queryEngineActionWithIdLike(connection, query);
                if (actions.size() == 0) {
                    result.set(false);
                } else if (actions.size() == 1) {
                    Map<String, Object> engineAction = actions.get(0);
                    String id_ = (String) engineAction.get("ID_");
                    List<Map<String, Object>> actionInputs = instance.queryEngineActionInputIdMatch(connection, id_);
                    List<Map<String, Object>> actionOutputs = instance.queryEngineActionOutputIdMatch(connection, id_);
                    engineActionRef.set(new EngineActionDataContainer(engineAction, actionInputs, actionOutputs));
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


        }, "Loading...", true, ProjectManager.getInstance().getDefaultProject());

        if (!engineActionRef.isNull()) {
            EngineActionDataContainer container = engineActionRef.get();

            temporyDialog = new EngineActionDialog(container.getEngineAction(), container.getEngineActionInput(), container.getEngineActionOutput());

            popup = JBPopupFactory.getInstance()
                    .createComponentPopupBuilder(temporyDialog, null)
//                    .setCancelOnClickOutside(true)
                    .setRequestFocus(true)
                    .setFocusable(true)
                    .setCancelOnOtherWindowOpen(true)
                    .createPopup();

            popup.showInFocusCenter();
            return true;
        }

        if (!ids.isNull()) {
            List<String> actionSorted = ids.get().stream().sorted().collect(Collectors.toList());

            @SuppressWarnings("unchecked")
            BaseListPopupStep selPopup = new BaseListPopupStep<String>("action", actionSorted, SpdEditorIcons.ACTION_16_ICON) {
                @Override
                public @Nullable PopupStep<?> onChosen(String selectedValue, boolean finalChoice) {
                    if (finalChoice) {
                        return doFinalStep(() -> doRun(selectedValue));
                    }
                    return PopupStep.FINAL_CHOICE;
                }

                private void doRun(String selectedValue) {
                    //选择的值可以进行跳转
                    tryToGotoAction(project, (String) selectedValue);
                }

                @Override
                public SpeedSearchFilter<String> getSpeedSearchFilter() {
                    return super.getSpeedSearchFilter();
                }

                public boolean isSpeedSearchEnabled() {
                    return true;
                }
            };

            //过滤框
            selPopup.isSelectable(true);
            ListPopup listPopup = JBPopupFactory.getInstance()
                    .createListPopup(selPopup);
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
            if (fileType instanceof HtmlFileType || fileType instanceof JavaScriptFileType || fileType instanceof XmlFile || fileType instanceof JavaFileType) {
                e.getPresentation().setEnabled(true);
                e.getPresentation().setVisible(true);
            } else {
                e.getPresentation().setEnabled(true);
                e.getPresentation().setVisible(true);
            }
        }
    }


    /**
     * 使用递归方式进行
     *
     * @param directory
     * @param doer
     * @return
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
}
