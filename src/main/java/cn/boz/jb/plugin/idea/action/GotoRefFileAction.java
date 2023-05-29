package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;
import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineActionInput;
import cn.boz.jb.plugin.idea.bean.EngineActionOutput;
import cn.boz.jb.plugin.idea.bean.EngineTask;
import cn.boz.jb.plugin.idea.bean.XfundsBatch;
import cn.boz.jb.plugin.idea.callsearch.CallerSearcherTableCellEditor;
import cn.boz.jb.plugin.idea.callsearch.CallerSearcherTablePanel;
import cn.boz.jb.plugin.idea.callsearch.CallerSearcherTableCellRender;
import cn.boz.jb.plugin.idea.configurable.SpdEditorDBState;
import cn.boz.jb.plugin.idea.callsearch.CallerSearcherCommentPanel;
import cn.boz.jb.plugin.idea.dialog.EngineActionDialog;
import cn.boz.jb.plugin.idea.dialog.EngineTaskDialog;
import cn.boz.jb.plugin.idea.utils.Constants;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.execution.RunManagerListener;
import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.icons.AllIcons;
import com.intellij.ide.IdeEventQueue;
import com.intellij.ide.UiActivity;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManagerImpl;
import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.lang.javascript.JavaScriptFileType;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.jvm.JvmMethod;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.util.BackgroundTaskUtil;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.SpeedSearchFilter;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.ui.popup.util.PopupUtil;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.popup.list.ListPopupImpl;
import com.intellij.ui.table.JBTable;
import com.intellij.util.Consumer;
import com.intellij.util.MessageBusUtil;
import com.intellij.util.messages.Topic;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBDimension;
import com.intellij.util.ui.ListTableModel;
import com.intellij.util.ui.UIUtil;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 用于导航到指定文件
 */
public class GotoRefFileAction extends AnAction {

    Topic<MyListener> findInProject = Topic.create("findInProject", MyListener.class);

    abstract class MyListener {
        abstract void doSomeghing();
    }

    public GotoRefFileAction() {
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(findInProject, new MyListener() {

            @Override
            void doSomeghing() {
                System.out.println("done");
            }
        });
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        VirtualFile virtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        if (editor == null || psiFile == null) {
            return;
        }
        int offset = editor.getCaretModel().getOffset();
        Project project = anActionEvent.getData(CommonDataKeys.PROJECT);

        FileType fileType = psiFile.getFileType();
        PsiElement element = psiFile.findElementAt(offset);

        if (fileType instanceof XmlFileType || fileType instanceof HtmlFileType) {
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
                    if (tryToGotoFileRef(psiFile, anActionEvent, attribute.getValue())) {
                        return;
                    }
                }
            }
        } else if (fileType instanceof JavaFileType) {
            //判断是不是Dao接口，如果是Dao 接口，name
            if (tryToGoToMapper(element)) {
                return;
            } else {
                if (tryToSearchUsageExt(element, anActionEvent)) {
                    return;
                }
            }
        } else {
            if (fileType instanceof JavaScriptFileType) {
                if (element != null) {
                    PsiElement context = element.getContext();

                    if (context instanceof JSLiteralExpression) {
                        JSLiteralExpression jsLiteralExpression = (JSLiteralExpression) context;
                        String stringValue = jsLiteralExpression.getStringValue();
                        boolean tryToGotoFileRef = tryToGotoFileRef(psiFile, anActionEvent, stringValue);
                        if (tryToGotoFileRef) {
                            return;
                        }
                        boolean tryToGotoClassRef = tryToGotoClassRef(project, stringValue);
                        if (tryToGotoClassRef) {
                            return;
                        }
                        boolean trytoGotoAction = tryToGotoAction(stringValue, anActionEvent, true);
                        if (trytoGotoAction) {
                            return;
                        }
                        boolean founded = tryToGotoHtmlFile(anActionEvent);
                        if (founded) {
                            return;
                        }
                        //尝试搜索文件名

                    }
                }


            }

            boolean founded = tryToGotoHtmlFile(anActionEvent);
            if (founded) {
                return;
            }


        }
        //try to use this function else do not use it.
        String text = element.getText();
        text = processStringForSearchable(text);
        if (goToPathSearchRecusive(psiFile, project, text)) {
            return;
        }
        text = removeStringContainsPath(text);

        String allContributorsGroupId = SearchEverywhereManagerImpl.ALL_CONTRIBUTORS_GROUP_ID;
        SearchEverywhereManager.getInstance(project).show(allContributorsGroupId, text, anActionEvent);

    }

    private boolean tryToGotoHtmlFile(AnActionEvent anActionEvent) {
        AnAction referJsUsage = ActionManager.getInstance().getAction("ReferJsUsage");
        referJsUsage.actionPerformed(anActionEvent);
        return true;
    }


    private String removeStringContainsPath(String text) {
        int i = text.lastIndexOf("/");
        if (i == -1) {
            return text;
        }
        return text.substring(i + 1);
    }


    /**
     * 处理字符串作为可以搜索的字符串
     *
     * @param text
     * @return
     */
    private String processStringForSearchable(String text) {
        if (text == null) {
            return "";
        }
        text = text.trim();
        if (text.startsWith("\"") && text.endsWith("\"")) {
            text = text.substring(1, text.length() - 1);
        }
        String[] split = text.split("\\?");
        return split[0];
    }

    /**
     * 迭代递归的去往指定的路径
     *
     * @param psiFile 文件
     * @param project 工程
     * @param text    文本
     * @return
     */
    public static boolean goToPathSearchRecusive(PsiFile psiFile, Project project, String text) {
        VirtualFile virtualFile = psiFile.getVirtualFile();
        while (true) {
            if (virtualFile == null) {
                break;
            }

            VirtualFile selected = virtualFile.findFileByRelativePath(text);
            if (selected != null) {
                PsiFile targetJsFile = PsiManager.getInstance(project).findFile(selected);
                NavigationUtil.activateFileWithPsiElement(targetJsFile);
                return true;
            } else {
                virtualFile = virtualFile.getParent();
            }

        }
        return false;
    }


    public static String tranToSpringBeanName(String input) {
        if (input == null) {
            return "";
        }
        if ("".equals(input.trim())) {
            return "";
        }
        if (input.length() == 1) {
            return input.toLowerCase();
        }
        if (input.endsWith("Impl")) {
            input = input.substring(0, input.length() - 4);

        }
        return input;
    }

    /**
     * 搜索在Engine_ACTION中的引用以及ENGINE_FLOW 中ENGINE_TASK 中的引用，并作列表快速搜索
     *
     * @param element
     */
    private boolean tryToSearchUsageExt(PsiElement element, AnActionEvent anActionEvent) {
        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        PsiClass containingClass;
        if (containingMethod == null) {
            containingClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
            if (containingClass == null) {
                return false;
            }

            String name = containingClass.getName();
            if (tryToSearchUsageByCodeFragment(anActionEvent, tranToSpringBeanName(name), "")) {
                return false;
            }
//            PsiReference reference = containingClass.getReference();

            return true;
        } else {
            //进入类
            String name = containingMethod.getName();
            String qualifiedName = containingMethod.getContainingClass().getQualifiedName();
            //

            if (tryToSearchUsageByCodeFragment(anActionEvent, name, qualifiedName)) {
                return false;
            }
            return true;


        }


    }

    public static boolean tryToSearchUsageByCodeFragment(AnActionEvent anActionEvent, String name, String qualifieredName) {
        DBUtils dbUtils = DBUtils.getInstance();

        Ref<List<EngineTask>> engineTaskRef = new Ref<>();
        Ref<List<EngineAction>> engineActionRef = new Ref<>();
        Ref<List<XfundsBatch>> xfundsBatchRef = new Ref<>();
        Ref<Exception> exceptionRef = new Ref<>();
        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
            try (Connection connection = DBUtils.getConnection(anActionEvent.getProject())) {
                List<EngineTask> engineTasks = dbUtils.queryEngineTaskByExpression(connection, name);
                List<EngineAction> engineActions = dbUtils.queryEngineActionByActionScript(connection, name);
                List<XfundsBatch> xfundsBatches = dbUtils.queryXfundsBatchByEnterName(connection, name);
                engineTaskRef.set(engineTasks);
                engineActionRef.set(engineActions);
                xfundsBatchRef.set(xfundsBatches);
            } catch (Exception e) {
                exceptionRef.set(e);
            }
        }, "Loading...", true, anActionEvent.getProject());
        if (engineTaskRef.isNull() && engineActionRef.isNull()) {
            DBUtils.dbExceptionProcessor(exceptionRef.get(), anActionEvent.getProject());
            return true;
        }
        List<Object> objects = new ArrayList<>();
        if (!engineTaskRef.isNull()) {
            objects.addAll(engineTaskRef.get());
        }
        if (!engineActionRef.isNull()) {
            objects.addAll(engineActionRef.get());
        }
        if (!xfundsBatchRef.isNull()) {
            objects.addAll(xfundsBatchRef.get());
        }

        if (objects.size() == 0) {
            return true;
        }
        //弹出框应该包含提示语
        showListPopup(objects, anActionEvent.getProject(), new Consumer<Object>() {
            @Override
            public void consume(Object selectedValue) {
                //选择的值可以进行跳转
                if (selectedValue instanceof EngineAction) {
                    EngineAction engineAction = (EngineAction) selectedValue;
                    tryToGotoAction(engineAction.getId(), anActionEvent, true);
                } else {
                    EngineTaskDialog engineTaskDialog = new EngineTaskDialog((EngineTask) selectedValue);
                    JBScrollPane jbScrollPane = new JBScrollPane(engineTaskDialog);
                    JBPopup popup;
                    popup = JBPopupFactory.getInstance().createComponentPopupBuilder(jbScrollPane, null).setRequestFocus(true).setFocusable(true).setMovable(true).setTitle("EngineTask").setCancelOnOtherWindowOpen(true).setProject(anActionEvent.getProject()).createPopup();

                    popup.showCenteredInCurrentWindow(anActionEvent.getProject());
                }
            }
        }, true, name, qualifieredName);


        return false;
    }

    public static CallerSearcherTableCellRender CALL_SEARCHER_TABLE_RENDERER = new CallerSearcherTableCellRender();

    public static CallerSearcherTableCellEditor CALL_SEARCHER_TABLE_EDITOR = new CallerSearcherTableCellEditor();


    public static ColumnInfo[] CALL_SEARCHER_TABLE_COLUMN_INFO = new ColumnInfo[]{new ColumnInfo<Object, String>("T") {

        @Override
        public @Nullable String valueOf(Object o) {
            if (o instanceof EngineTask) {
                return "task";
            } else if (o instanceof EngineAction) {
                return "action";
            } else if (o instanceof XfundsBatch) {
                return "batch";
            }
            return "";
        }

        @Override
        public boolean isCellEditable(Object o) {
            return false;
        }
    }, new ColumnInfo<Object, Object>("F") {
        @Nullable
        @Override
        public Object valueOf(Object o) {
            return o;
//            if (o instanceof EngineTask) {
//                return ((EngineTask) o).isChecked();
//            } else if (o instanceof EngineAction) {
//                return ((EngineAction) o).isChecked();
//            }else if(o instanceof XfundsBatch){
//                return ((XfundsBatch)o).isChecked();
//            }
//            return false;
        }

        @Override
        public boolean isCellEditable(Object o) {
            return true;
        }

    }, new ColumnInfo<Object, String>("id") {
        @Nullable
        @Override
        public String valueOf(Object o) {
            if (o instanceof EngineTask) {
                return ((EngineTask) o).getId();
            } else if (o instanceof EngineAction) {
                return ((EngineAction) o).getId();
            } else if (o instanceof XfundsBatch) {
                return ((XfundsBatch) o).getSeqNo();
            }
            return "";
        }

        @Override
        public boolean isCellEditable(Object o) {
            return true;
        }


    }, new ColumnInfo<Object, String>("name") {
        @Nullable
        @Override
        public String valueOf(Object o) {
            if (o instanceof EngineTask) {
                return ((EngineTask) o).getTitle();
            } else if (o instanceof EngineAction) {
                return ((EngineAction) o).getNamespace();
            } else if (o instanceof XfundsBatch) {
                return ((XfundsBatch) o).getDescr();
            }
            return "";

        }

        @Override
        public boolean isCellEditable(Object o) {
            return true;
        }


    }, new ColumnInfo<Object, String>("expressionOrScript") {
        @Nullable
        @Override
        public String valueOf(Object o) {
            if (o instanceof EngineTask) {
                return ((EngineTask) o).getExpression();
            } else if (o instanceof EngineAction) {
                return ((EngineAction) o).getActionscript();
            } else if (o instanceof XfundsBatch) {
                return ((XfundsBatch) o).getEnterName();
            }
            return "";
        }

        @Override
        public boolean isCellEditable(Object o) {
            return false;
        }


    }};


    @SuppressWarnings("unchecked")
    public static void showListPopup(final List<Object> objects, Project project, Consumer<? super Object> selectUserTaskConsumer, boolean showComments, String queryName, String qualifierName) {
        //
        CallerSearcherTablePanel jbTable = new CallerSearcherTablePanel(new ListTableModel<>(CALL_SEARCHER_TABLE_COLUMN_INFO, objects, 0));

        jbTable.setQueryName(queryName);
        jbTable.setQualifierName(qualifierName);

        Runnable runnable = () -> {
            ListTableModel model = (ListTableModel) jbTable.getModel();
            int selectedRow = jbTable.getSelectedRow();
            int i = jbTable.convertRowIndexToModel(selectedRow);
            Object item = model.getItem(i);
            if (item != null) {
                selectUserTaskConsumer.consume(item);
            }

        };
        if (jbTable.getModel().getRowCount() == 0) {
            jbTable.clearSelection();
        }

        PopupChooserBuilder builder = JBPopupFactory.getInstance().createPopupChooserBuilder(jbTable);

        if (showComments) {
            //注释
            builder.setSouthComponent(new CallerSearcherCommentPanel(jbTable));
        }
        String format;
        if (StringUtils.isBlank(qualifierName)) {
            format = String.format("%s", queryName);
        } else {
            format = String.format("%s.%s", qualifierName, queryName);
        }


        builder.setTitle("caller Searcher:" + format)
                .setItemChoosenCallback(runnable)
                .setResizable(true)
                .setMovable(true)
                .setDimensionServiceKey("callerSearcher")
                .setMinSize(new JBDimension(800, 400));


        JBPopup popup = builder.createPopup();


        popup.showCenteredInCurrentWindow(project);


        ActionManager instance = ActionManager.getInstance();

        ActionGroup ag = (ActionGroup) instance.getAction(Constants.ACTION_GROUP_REF_TABLE_SEARCH_ID);

        PopupHandler.installPopupHandler(jbTable, ag, ActionPlaces.UPDATE_POPUP);


        //安装一个右键菜单供使用
    }


    /**
     * this is beging use by
     *
     * @see GotoRefFileAction#showListPopup(List, Project, Consumer, boolean, String, String)
     */
    public static class ActionOrTaskDetailAction extends DumbAwareAction {

        /**
         * @param anActionEvent
         * @see GotoRefFileAction#showListPopup(List, Project, Consumer, boolean, String, String)
         */
        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            Component com = anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
            if (com instanceof JBTable) {
                JBTable jbTable = (JBTable) com;
                ListTableModel model = (ListTableModel) jbTable.getModel();
                int selectedRow = jbTable.getSelectedRow();
                int i = jbTable.convertRowIndexToModel(selectedRow);
                Object item = model.getItem(i);
                if (item != null) {
                    if (item instanceof EngineAction) {
                        EngineAction engineAction = (EngineAction) item;
                        tryToGotoAction(engineAction.getId(), anActionEvent, true);
                    } else {
                        EngineTaskDialog engineTaskDialog = new EngineTaskDialog((EngineTask) item);
                        JBScrollPane jbScrollPane = new JBScrollPane(engineTaskDialog);
                        JBPopup popup;
                        popup = JBPopupFactory.getInstance().createComponentPopupBuilder(jbScrollPane, null).setRequestFocus(true).setFocusable(true).setMovable(true).setTitle("EngineTask").setCancelOnOtherWindowOpen(true).setProject(anActionEvent.getProject()).createPopup();

                        popup.showCenteredInCurrentWindow(anActionEvent.getProject());
                    }
                }
            }

        }
    }

    @SuppressWarnings("unchecked")
    public static class SearchIdAction extends DumbAwareAction {
        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            JBTable jbTable = (JBTable) anActionEvent.getRequiredData(PlatformDataKeys.CONTEXT_COMPONENT);
//            JBTable jbTablle = GoToRefFile.this.jbTable;
            int selectedRow = jbTable.getSelectedRow();
            int modelidx = jbTable.convertRowIndexToModel(selectedRow);
            //如何拿到当前控件
            ListTableModel model = (ListTableModel) jbTable.getModel();
            Object item = model.getItem(modelidx);
            if (item instanceof EngineTask) {
                EngineTask engineTask = (EngineTask) item;
                String id = engineTask.getId();
                if (id.contains("_")) {
                    String s = id.split("_")[0];
                    List<String> collect = Stream.of("Search:" + s, "Find:" + s).collect(Collectors.toList());

                    BaseListPopupStep selPopup = new BaseListPopupStep<String>("id search", collect, SpdEditorIcons.ACTION_16_ICON) {

                        @Override
                        public Icon getIconFor(String value) {
                            if (value.startsWith("Search")) {
                                return AllIcons.Actions.Search;
                            } else {
                                return AllIcons.Actions.Find;
                            }
                        }

                        @Override
                        public @Nullable PopupStep<?> onChosen(String selectedValue, boolean finalChoice) {
                            if (finalChoice) {
                                return doFinalStep(() -> doRun(selectedValue));
                            }
                            return PopupStep.FINAL_CHOICE;
                        }


                        private void doRun(String selectedValue) {
                            //选择的值可以进行跳转
                            if (selectedValue.startsWith("Search")) {
                                String allContributorsGroupId = SearchEverywhereManagerImpl.ALL_CONTRIBUTORS_GROUP_ID;
                                SearchEverywhereManager instance = SearchEverywhereManager.getInstance(anActionEvent.getProject());
                                instance.show(allContributorsGroupId, s, anActionEvent);
                            } else {
                                EventQueue.invokeLater(() -> {
                                    FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(anActionEvent.getProject());
                                    FindModel findModel = new FindModel();
                                    findModel.setStringToFind(s);
                                    findInProjectManager.findInProject(anActionEvent.getDataContext(), findModel);
                                });
                            }
                        }

                        @Override
                        public SpeedSearchFilter<String> getSpeedSearchFilter() {
                            return super.getSpeedSearchFilter();
                        }

                        @Override
                        public boolean isSpeedSearchEnabled() {
                            return true;
                        }
                    };

                    //过滤框
                    selPopup.isSelectable(true);
                    ListPopup listPopup = JBPopupFactory.getInstance().createListPopup(selPopup);
                    InputEvent inputEvent = anActionEvent.getInputEvent();
                    if (inputEvent instanceof MouseEvent) {
                        MouseEvent me = (MouseEvent) inputEvent;
                        listPopup.show(RelativePoint.fromScreen(me.getLocationOnScreen()));
                    } else {
                        listPopup.showInFocusCenter();
                    }


                } else {
                    EventQueue.invokeLater(() -> {

                        FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(anActionEvent.getProject());
                        FindModel findModel = new FindModel();
                        findModel.setStringToFind(id);
                        findInProjectManager.findInProject(anActionEvent.getDataContext(), findModel);
                    });
                }
            } else if (item instanceof EngineAction) {
                JBPopup popup = PopupUtil.getPopupContainerFor(jbTable);
                Disposer.dispose(popup);
                while (!Disposer.isDisposed(popup)) {

                }

                EngineAction engineAction = (EngineAction) item;
                String id = engineAction.getId();
                EventQueue.invokeLater(() -> {

                    FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(anActionEvent.getProject());
                    FindModel findModel = new FindModel();
                    findModel.setStringToFind(id.replaceFirst("/", ""));
                    findModel.setFileFilter(".js");
                    findInProjectManager.findInProject(anActionEvent.getDataContext(), findModel);
                });

            }
        }
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


    /**
     * 跳转到Engine Action
     *
     * @param
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean tryToGotoAction(String value, AnActionEvent anActionEvent, boolean strict) {
        if (value == null) {
            return false;
        }
        if (!value.contains(".do") && strict) {
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
        Task.WithResult<Object, Exception> objectExceptionWithResult = new Task.WithResult<Object, Exception>(anActionEvent.getProject(), "connect", true) {
            @Override
            protected Object compute(@NotNull ProgressIndicator progressIndicator) throws Exception {
                try (Connection connection = DBUtils.getConnection(SpdEditorDBState.getInstance(anActionEvent.getProject()));) {
                    List<EngineAction> actions = instance.queryEngineActionWithIdLike(connection, query);
                    if (actions.size() == 0) {
                        result.set(false);
                    } else if (actions.size() == 1) {
                        EngineAction engineAction = actions.get(0);
                        String id_ = engineAction.getId();
                        List<EngineActionInput> actionInputs = instance.queryEngineActionInputIdMatch(connection, id_);
                        List<EngineActionOutput> actionOutputs = instance.queryEngineActionOutputIdMatch(connection, id_);
                        engineAction.setInputs(actionInputs);
                        engineAction.setOutputs(actionOutputs);
                        engineActionRef.set(new EngineActionDataContainer(engineAction));
                    } else {
                        List<String> ids_ = actions.stream().map(it -> (String) it.getId()).collect(Collectors.toList());
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
                return null;
            }
        };
        try {
            Object run = ProgressManager.getInstance().run(objectExceptionWithResult);
        } catch (Exception e) {
            e.printStackTrace();
        }


        JBPopup popup;
        EngineActionDialog temporyDialog;
        if (!engineActionRef.isNull()) {
            EngineActionDataContainer container = engineActionRef.get();
            temporyDialog = new EngineActionDialog(container.getEngineAction());
            popup = JBPopupFactory.getInstance().createComponentPopupBuilder(temporyDialog, null).setRequestFocus(true).setTitle("Action").setMovable(true).setProject(anActionEvent.getProject()).setFocusable(true).setCancelOnOtherWindowOpen(true).createPopup();
            //应该
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            popup.showCenteredInCurrentWindow(anActionEvent.getProject());

            return true;
        }

        if (!ids.isNull()) {
            List<String> actionSorted = ids.get().stream().sorted().limit(20).collect(Collectors.toList());

            @SuppressWarnings("unchecked") BaseListPopupStep selPopup = new BaseListPopupStep<String>("action", actionSorted, SpdEditorIcons.ACTION_16_ICON) {

                @Override
                public @Nullable PopupStep<?> onChosen(String selectedValue, boolean finalChoice) {
                    if (finalChoice) {
                        return doFinalStep(() -> doRun(selectedValue));
                    }
                    return PopupStep.FINAL_CHOICE;
                }


                private void doRun(String selectedValue) {
                    //选择的值可以进行跳转
                    tryToGotoAction((String) selectedValue, anActionEvent, true);
                }

                @Override
                public SpeedSearchFilter<String> getSpeedSearchFilter() {
                    return super.getSpeedSearchFilter();
                }

                @Override
                public boolean isSpeedSearchEnabled() {
                    return true;
                }


            };


            //过滤框
            selPopup.isSelectable(true);
            ListPopup listPopup = JBPopupFactory.getInstance().createListPopup(selPopup);
            listPopup.showCenteredInCurrentWindow(anActionEvent.getProject());
            return true;
        }

        return false;
    }

    static ListPopup listPopup;

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


    private boolean tryToGotoFileRef(PsiFile psiFile, AnActionEvent anActionEvent, String value) {
        Project project = anActionEvent.getProject();
        String[] split = value.split("\\?");
        VirtualFile fileByRelativePath = psiFile.getVirtualFile().getParent().findFileByRelativePath(split[0]);
        if (fileByRelativePath != null) {
            PsiFile targetJsFile = PsiManager.getInstance(project).findFile(fileByRelativePath);
            NavigationUtil.activateFileWithPsiElement(targetJsFile);
            return true;
        } else {
            return tryToGotoAction(value, anActionEvent, false);
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
            if (fileType instanceof HtmlFileType || fileType instanceof XmlFile || fileType instanceof JavaFileType) {
                e.getPresentation().setEnabled(true);
                e.getPresentation().setVisible(true);
                return;
            } else {
                try {
                    Class.forName("com.intellij.lang.javascript.JavaScriptFileType");
                    if (fileType instanceof JavaScriptFileType) {
                        e.getPresentation().setEnabled(true);
                        e.getPresentation().setVisible(true);
                    }
                    return;
                } catch (ClassNotFoundException classNotFoundException) {
                    e.getPresentation().setEnabled(true);
                    e.getPresentation().setVisible(true);
                }
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

