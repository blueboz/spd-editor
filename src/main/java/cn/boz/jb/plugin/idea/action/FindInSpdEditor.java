package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.floweditor.gui.control.PropertyObject;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.CallActivity;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.EndEvent;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ExclusiveGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ForeachGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ParallelGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.SequenceFlow;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ServiceTask;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.StartEvent;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.UserTask;
import cn.boz.jb.plugin.floweditor.gui.shape.Label;
import cn.boz.jb.plugin.floweditor.gui.shape.Line;
import cn.boz.jb.plugin.floweditor.gui.shape.Shape;
import cn.boz.jb.plugin.floweditor.gui.utils.IcoMoonUtils;
import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.TableSpeedSearch;
import com.intellij.ui.speedSearch.SpeedSearchSupply;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.intellij.ui.table.JBTable;
import com.intellij.util.Consumer;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBDimension;
import com.intellij.util.ui.ListTableModel;
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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.List;

public class FindInSpdEditor extends DumbAwareAction {

    public FindInSpdEditor() {
    }

    private ChartPanel chartPanel;

    public FindInSpdEditor(ChartPanel chartPanel) {
        //拷贝快捷键
        //注册快捷
        AnAction action = ActionManager.getInstance().getAction(IdeActions.ACTION_FIND);
        if (action != null) {
            this.copyShortcutFrom(action);
        }
        this.setEnabledInModalContext(true);
        this.chartPanel = chartPanel;

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        List<PropertyObject> propertyObjects = chartPanel.getAllPropertyObject();
        showListPopup(propertyObjects, anActionEvent.getProject(), o -> {
            chartPanel.selectObject(o);
        }, true);


    }

    @SuppressWarnings("unchecked")
    public static void showListPopup(List<PropertyObject> objects, Project project, Consumer<? super Object> selectUserTaskConsumer, boolean showComments) {
        MyRenderer myRenderer = new MyRenderer();
        ColumnInfo[] columns = new ColumnInfo[]{new ColumnInfo<PropertyObject, String>("type") {
            @Override
            public @Nullable String valueOf(PropertyObject o) {
                return o.getClass().getSimpleName();
            }

        }, new ColumnInfo<PropertyObject, String>("id") {
            @Nullable
            @Override
            public String valueOf(PropertyObject o) {
                if (o instanceof Label) {
                    return ((Label) o).getId();
                } else if (o instanceof Shape) {
                    return ((Shape) o).getId();
                } else if (o instanceof Line) {
                    return ((Line) o).getId();
                }
                return "";
            }

            @Override
            public @Nullable TableCellRenderer getRenderer(PropertyObject o) {
                return myRenderer;
            }


        }, new ColumnInfo<Object, String>("name") {
            @Nullable
            @Override
            public String valueOf(Object o) {
                if (o instanceof Label) {
                    return ((Label) o).getName();
                } else if (o instanceof Shape) {
                    return ((Shape) o).getName();
                } else if (o instanceof Line) {
                    return ((Line) o).getName();
                } else {
                    return "";
                }
            }

            @Override
            public @Nullable TableCellRenderer getRenderer(Object o) {
                return myRenderer;
            }


        }, new ColumnInfo<Object, String>("expressionOrScript") {
            @Nullable
            @Override
            public String valueOf(Object o) {
                if (o instanceof Label) {
                    return ((Label) o).getText();
                } else if (o instanceof ServiceTask) {
                    return ((ServiceTask) o).getExpression();
                } else if (o instanceof UserTask) {
                    return ((UserTask) o).getExpression();
                } else if (o instanceof CallActivity) {
                    return ((CallActivity) o).getCalledElement();
                } else if (o instanceof ForeachGateway) {
                    return ((ForeachGateway) o).getExpression();
                } else if (o instanceof ParallelGateway) {
                    return "";
                } else if (o instanceof ExclusiveGateway) {
                    return "";
                } else if (o instanceof StartEvent) {
                    return "";
                } else if (o instanceof EndEvent) {
                    return "";
                } else if (o instanceof SequenceFlow) {
                    return ((SequenceFlow) o).getConditionExpression();
                }
                return "";
            }

            @Override
            public @Nullable TableCellRenderer getRenderer(Object o) {
                return myRenderer;
            }
        }};


        //
        ListTableModel listTableModel = new ListTableModel(columns, objects, 0);

        JBTable jbTable = new JBTable(listTableModel) {
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

        jbTable.setShowHorizontalLines(true);

        TableRowSorter sorter = new TableRowSorter<>(listTableModel);


        jbTable.setRowSorter(sorter);

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
                    //为不同列设置排序器
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


        jbTable.setMinimumSize(new JBDimension(800, 100));
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

    private static JPanel createCommentsPanel(final JBTable table) {
        JTextArea textArea = new JTextArea();

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(@NotNull ListSelectionEvent e) {

                ListTableModel model = (ListTableModel) table.getModel();
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    return;
                }
                int i = table.convertRowIndexToModel(selectedRow);
                Object item = model.getItem(i);
                String hint = "";
                if (item instanceof ServiceTask) {
                    hint = ((ServiceTask) item).getExpression();
                    if (!hint.contains("\n")) {
                        hint = hint.replaceAll(";", ";\n");
                    }
                } else if (item instanceof UserTask) {
                    hint = ((UserTask) item).getExpression();
                } else if (item instanceof SequenceFlow) {
                    hint = ((SequenceFlow) item).getConditionExpression();

                } else if (item instanceof CallActivity) {
                    hint = ((CallActivity) item).getCalledElement();

                } else if (item instanceof ForeachGateway) {
                    hint = ((ForeachGateway) item).getExpression();
                } else if (item instanceof Label) {
                    hint = ((Label) item).getText();
                }


                textArea.setText(hint);
                Highlighter highlighter = textArea.getHighlighter();
                highlighter.removeAllHighlights();

                SpeedSearchSupply speedSearch = SpeedSearchSupply.getSupply(table);
                if (speedSearch == null) {
                    return;
                }
                //底层匹配的时候，可能使用的是最大公共子串的算法
                Iterable<TextRange> textRanges = speedSearch.matchingFragments(hint);
                if (textRanges == null) {
                    return;
                }

                DefaultHighlighter.DefaultHighlightPainter defaultHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.ORANGE);

                for (TextRange textRange : textRanges) {
                    try {
                        highlighter.addHighlight(textRange.getStartOffset(), textRange.getEndOffset(), defaultHighlightPainter);
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                }


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
                myComponent.setFont(IcoMoonUtils.getFont16());
                if (ServiceTask.class.getSimpleName().equals(value)) {
                    myComponent.append(IcoMoonUtils.getServiceTask());
                } else if (UserTask.class.getSimpleName().equals(value)) {
                    myComponent.append(IcoMoonUtils.getUserTask());
                } else if (CallActivity.class.getSimpleName().equals(value)) {
                    myComponent.append(IcoMoonUtils.getCallActivity());
                } else if (ExclusiveGateway.class.getSimpleName().equals(value)) {
                    myComponent.append(IcoMoonUtils.getExclusiveGateway());
                } else if (ParallelGateway.class.getSimpleName().equals(value)) {
                    myComponent.append(IcoMoonUtils.getParallelGateway());
                } else if (ForeachGateway.class.getSimpleName().equals(value)) {
                    myComponent.append(IcoMoonUtils.getForeachGateway());
                } else if (SequenceFlow.class.getSimpleName().equals(value)) {
                    myComponent.append(IcoMoonUtils.getSequenceFlow());
                } else if (StartEvent.class.getSimpleName().equals(value)) {
                    myComponent.append(IcoMoonUtils.getStartEvent());
                } else if (EndEvent.class.getSimpleName().equals(value)) {
                    myComponent.append(IcoMoonUtils.getEndEvent());
                } else if (Label.class.getSimpleName().equals(value)) {
                    myComponent.append(IcoMoonUtils.getHText());
                } else {
                    System.out.println("unrecognized " + value);
                }
            } else {
                Font font = table.getFont();
                myComponent.setFont(font);

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

}
