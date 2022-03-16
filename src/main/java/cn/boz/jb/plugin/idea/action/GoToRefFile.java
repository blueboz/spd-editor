package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.floweditor.gui.process.fragment.ServiceTask;
import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineTask;
import cn.boz.jb.plugin.idea.configurable.SpdEditorDBState;
import cn.boz.jb.plugin.idea.dialog.EngineActionDialog;
import cn.boz.jb.plugin.idea.dialog.EngineTaskDialog;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import cn.boz.jb.plugin.idea.widget.SimpleIconControl;
import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.find.FindModel;
import com.intellij.find.findInProject.FindInProjectManager;
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
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.SpeedSearchFilter;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
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
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.TableSpeedSearch;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.intellij.ui.table.JBTable;
import com.intellij.util.Consumer;
import com.intellij.util.ThrowableRunnable;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBDimension;
import com.intellij.util.ui.ListTableModel;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                    if (tryToGotoFileRef(psiFile, project, attribute.getValue())) {
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
            try {
                Class.forName("com.intellij.lang.javascript.JavaScriptFileType");

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
                }
            } catch (ClassNotFoundException classNotFoundException) {
            }

        }
        //try to use this function else do not use it.
        String text = element.getText();
        String allContributorsGroupId = SearchEverywhereManagerImpl.ALL_CONTRIBUTORS_GROUP_ID;
        SearchEverywhereManager.getInstance(project).show(allContributorsGroupId, text, anActionEvent);
//        SearchEverywhereManager searchEverywhereManager = SearchEverywhereManager.getInstance(project);
//        HintManager.getInstance().showErrorHint(editor, "Cannot find declaration to go to");

    }

    EngineTaskDialog engineTaskDialog;
    ListPopup listPopup;

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
            //进入类
        } else {
            containingClass = containingMethod.getContainingClass();
        }

        String qualifiedName = containingClass.getQualifiedName();
        String name = containingMethod.getName();
        //
        SpdEditorDBState instance = SpdEditorDBState.getInstance();

        DBUtils dbUtils = DBUtils.getInstance();

        Ref<List<EngineTask>> engineTaskRef = new Ref<>();
        Ref<List<EngineAction>> engineActionRef = new Ref<>();
        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
            try (Connection connection = dbUtils.getConnection()) {
                List<EngineTask> engineTasks = dbUtils.queryEngineTaskByExpression(connection, name);
                List<EngineAction> engineActions = dbUtils.queryEngineActionByActionScript(connection, name);
                engineTaskRef.set(engineTasks);
                engineActionRef.set(engineActions);
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
        }, "Loading...", true, anActionEvent.getProject());
        if (engineTaskRef.isNull() && engineActionRef.isNull()) {
            return false;
        }
        List<Object> objects = new ArrayList<>();
        if (!engineTaskRef.isNull()) {
            objects.addAll(engineTaskRef.get());
        }
        if (!engineActionRef.isNull()) {
            objects.addAll(engineActionRef.get());
        }

        //弹出框应该包含提示语
        showListPopup(objects, anActionEvent.getProject(), new Consumer<Object>() {
            @Override
            public void consume(Object selectedValue) {
                //选择的值可以进行跳转
                if (selectedValue instanceof EngineAction) {
                    EngineAction engineAction = (EngineAction) selectedValue;
                    GoToRefFile.this.tryToGotoAction(anActionEvent.getProject(), engineAction.getId());
                } else {
                    engineTaskDialog = new EngineTaskDialog((EngineTask) selectedValue);
                    JBScrollPane jbScrollPane = new JBScrollPane(engineTaskDialog);
                    popup = JBPopupFactory.getInstance()
                            .createComponentPopupBuilder(jbScrollPane, null)
                            .setRequestFocus(true)
                            .setFocusable(true)
                            .setCancelOnOtherWindowOpen(true)
                            .setProject(anActionEvent.getProject())
                            .createPopup();

                    popup.showCenteredInCurrentWindow(anActionEvent.getProject());
                }
            }
        }, true);
        return true;
    }

    private static JPanel createCommentsPanel(final JBTable table) {
        final JTextArea textArea = new JTextArea("", 7, 30);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(@NotNull ListSelectionEvent e) {
                ListTableModel model = (ListTableModel) table.getModel();
                int selectedRow = table.getSelectedRow();
                int i = table.convertRowIndexToModel(selectedRow);
                Object item = model.getItem(i);
                String hint = "";
                if (item instanceof EngineTask) {
                    hint = ((EngineTask) item).getExpression();
                } else if (item instanceof EngineAction) {
                    hint = ((EngineAction) item).getActionscript();
                }

                textArea.setText(hint);
                textArea.select(0, 0);
            }
        });
        JPanel jPanel = new JPanel(new BorderLayout());
        JScrollPane textScrollPane = ScrollPaneFactory.createScrollPane(textArea);
        JLabel commentLabel = new JLabel("script");
        jPanel.add(commentLabel, "North");
        commentLabel.setBorder(IdeBorderFactory.createBorder(11));
        textScrollPane.setBorder((Border) null);
        jPanel.add(textScrollPane, "Center");
        jPanel.setPreferredSize(new JBDimension(800, 200));
        return jPanel;
    }


    @SuppressWarnings("unchecked")
    public static void showListPopup(final List<Object> objects, Project project, Consumer<? super Object> selectUserTaskConsumer, boolean showComments) {
        MyRenderer myRenderer = new MyRenderer();
        ColumnInfo[] columns = new ColumnInfo[]{new ColumnInfo<Object, String>("type") {


            @Override
            public @Nullable String valueOf(Object o) {
                if (o instanceof EngineTask) {
                    return "task";
                } else if (o instanceof EngineAction) {
                    return "action";
                }
                return "";
            }

        }, new ColumnInfo<Object, String>("id") {
            @Nullable
            @Override
            public String valueOf(Object o) {
                if (o instanceof EngineTask) {
                    return ((EngineTask) o).getId();
                } else if (o instanceof EngineAction) {
                    return ((EngineAction) o).getId();
                }
                return "";
            }

            @Override
            public @Nullable TableCellRenderer getRenderer(Object o) {
                return myRenderer;
            }


        }, new ColumnInfo<Object, String>("name") {
            @Nullable
            @Override
            public String valueOf(Object o) {
                if (o instanceof EngineTask) {
                    return ((EngineTask) o).getTitle();
                } else if (o instanceof EngineAction) {
                    return ((EngineAction) o).getNamespace();
                }
                return "";

            }

            @Override
            public @Nullable TableCellRenderer getRenderer(Object o) {
                return myRenderer;
            }


        }, new ColumnInfo<Object, String>("expressionOrScript") {
            @Nullable
            @Override
            public String valueOf(Object o) {
                if (o instanceof EngineTask) {
                    return ((EngineTask) o).getExpression();
                } else if (o instanceof EngineAction) {
                    return ((EngineAction) o).getActionscript();
                }
                return "";
            }

            @Override
            public @Nullable TableCellRenderer getRenderer(Object o) {
                return myRenderer;
            }
        }};

        //
        JBTable jbTable = new JBTable(new ListTableModel(columns, objects, 0)) {
            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                return myRenderer;
            }
        };
        final TableColumn c0 = jbTable.getColumnModel().getColumn(0);
        c0.setMaxWidth(24);
        c0.setWidth(24);
        c0.setMinWidth(24);

//        c0.setCellRenderer(myRenderer);
        final TableColumn c1 = jbTable.getColumnModel().getColumn(1);
        c1.setWidth(250);
        c1.setMinWidth(250);
//        c1.setCellRenderer(myRenderer);

//        final TableView<Object> table = new TableView(new ListTableModel(columns, objects, 0));
//        jbTable.setCellSelectionEnabled(true);
        jbTable.setShowHorizontalLines(true);
        jbTable.setTableHeader((JTableHeader) null);

        jbTable.setRowSorter(new TableRowSorter<>(jbTable.getModel()));

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

        TableSpeedSearch tableSpeedSearch = new TableSpeedSearch(jbTable) {
            @Override
            protected void onSearchFieldUpdated(String pattern) {
                super.onSearchFieldUpdated(pattern);
                RowSorter<? extends TableModel> sorter0 = myComponent.getRowSorter();
                if (!(sorter0 instanceof TableRowSorter)) {
                    return;
                }
                //noinspection unchecked
                TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) sorter0;
                if (StringUtil.isNotEmpty(pattern)) {

                    sorter.setRowFilter(new RowFilter<>() {
                        @Override
                        public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
                            return isMatchingRow(entry.getIdentifier(), pattern);
                        }
                    });
                } else {
                    sorter.setRowFilter(null);
                }
            }

            protected boolean isMatchingRow(int modelRow, String pattern) {
                int columns = myComponent.getColumnCount();
                for (int col = 0; col < columns; col++) {
                    String str = (String) myComponent.getModel().getValueAt(modelRow, col);
                    if (str != null && compare(str, pattern)) {
                        return true;
                    }
                }
                return false;
            }
        };

        jbTable.setMinimumSize(new JBDimension(800, 400));
        PopupChooserBuilder builder = new PopupChooserBuilder(jbTable);
        if (showComments) {
            //注释
            builder.setSouthComponent(createCommentsPanel(jbTable));
        }


        builder.setTitle("UserTaskSearcher").setItemChoosenCallback(runnable).setResizable(true)
                .setDimensionServiceKey("UserTaskSearcher").setMinSize(new JBDimension(300, 300));
        JBPopup popup = builder.createPopup();
        popup.showCenteredInCurrentWindow(project);

        ActionManager instance = ActionManager.getInstance();

        ActionGroup ag = (ActionGroup) instance.getAction("spd.gotorefaction.group");

        PopupHandler.installPopupHandler(jbTable, ag, ActionPlaces.UPDATE_POPUP);


        //安装一个右键菜单供使用
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
                    List<String> collect = Stream.of(s, id).collect(Collectors.toList());

                    BaseListPopupStep selPopup = new BaseListPopupStep<String>("id search", collect, SpdEditorIcons.ACTION_16_ICON) {
                        @Override
                        public @Nullable PopupStep<?> onChosen(String selectedValue, boolean finalChoice) {
                            if (finalChoice) {
                                return doFinalStep(() -> doRun(selectedValue));
                            }
                            return PopupStep.FINAL_CHOICE;
                        }


                        private void doRun(String selectedValue) {
                            //选择的值可以进行跳转
                            if (selectedValue.equals(s)) {
                                String allContributorsGroupId = SearchEverywhereManagerImpl.ALL_CONTRIBUTORS_GROUP_ID;
                                SearchEverywhereManager instance = SearchEverywhereManager.getInstance(anActionEvent.getProject());
                                instance.show(allContributorsGroupId, s, anActionEvent);
                            } else {
                                FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(anActionEvent.getProject());
                                FindModel findModel = new FindModel();
                                findModel.setStringToFind(id);
                                findInProjectManager.findInProject(anActionEvent.getDataContext(), findModel);
                            }
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


                } else {
                    FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(anActionEvent.getProject());
                    FindModel findModel = new FindModel();
                    findModel.setStringToFind(id);
                    findInProjectManager.findInProject(anActionEvent.getDataContext(), findModel);
                }
            } else if (item instanceof EngineAction) {
                EngineAction engineAction = (EngineAction) item;
                String id = engineAction.getId();

                FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(anActionEvent.getProject());
                FindModel findModel = new FindModel();
                findModel.setStringToFind(id.replaceFirst("/",""));
                findInProjectManager.findInProject(anActionEvent.getDataContext(), findModel);


            }
        }
    }

    private static class MyRenderer implements TableCellRenderer {
        private final SimpleColoredComponent myComponent = new SimpleColoredComponent();

        @NotNull
        @Override
        public Component getTableCellRendererComponent(@NotNull JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {

            Color bg = isSelected ? table.getSelectionBackground() : table.getBackground();
            Color fg = isSelected ? table.getSelectionForeground() : table.getForeground();
            myComponent.clear();

            if (column == 0) {
                SimpleIconControl simpleIconControl = null;
                if ("task".equals(value)) {
                    simpleIconControl = new SimpleIconControl(SpdEditorIcons.GEAR_16_ICON);
                } else if ("action".equals(value)) {
                    simpleIconControl = new SimpleIconControl(SpdEditorIcons.ACTION_SCRIPT_16_ICON);

                }
                if (simpleIconControl != null) {
                    simpleIconControl.setBackground(bg);
                    simpleIconControl.setForeground(fg);
                    return simpleIconControl;
                }
            } else {
                if (value != null) {
                    myComponent.append((String) value);
                } else {
                    myComponent.append((String) "");
                }
            }
            myComponent.setBackground(bg);
            myComponent.setForeground(fg);

            SpeedSearchUtil.applySpeedSearchHighlighting(table, myComponent, true, hasFocus);
            return myComponent;

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
                    .setProject(project)
                    .setFocusable(true)
                    .setCancelOnOtherWindowOpen(true)
                    .createPopup();

            popup.showCenteredInCurrentWindow(project);
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
