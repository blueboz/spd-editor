package cn.boz.jb.plugin.idea.action.flowSearch;

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
import cn.boz.jb.plugin.idea.action.FindInSpdEditorAction;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.TableSpeedSearch;
import com.intellij.ui.table.JBTable;
import com.intellij.util.Consumer;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBDimension;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.List;

public class FlowSearchTable extends JBTable {

    private Consumer<? super Object> selectUserTaskConsumer;
    public static FlowSearchTableRender FLOW_SEARCH_TABLE_CELL_RENDER = new FlowSearchTableRender();

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return FLOW_SEARCH_TABLE_CELL_RENDER;
    }

    public static ColumnInfo[] FLOW_SEARCH_TABLE_COLUMN_INFO = new ColumnInfo[]{new ColumnInfo<PropertyObject, String>("type") {
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
            return FLOW_SEARCH_TABLE_CELL_RENDER;
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
            return FLOW_SEARCH_TABLE_CELL_RENDER;
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
            return FLOW_SEARCH_TABLE_CELL_RENDER;
        }
    }};

    private String processId;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public FlowSearchTable(List<PropertyObject> objects, Consumer<? super Object> selectUserTaskConsumer, String processId){
        this.selectUserTaskConsumer=selectUserTaskConsumer;
        this.processId=processId;


        //
        ListTableModel listTableModel = new ListTableModel(FLOW_SEARCH_TABLE_COLUMN_INFO, objects, 0);
        this.setModel(listTableModel);


        final TableColumn c0 = getColumnModel().getColumn(0);
        c0.setMaxWidth(24);
        c0.setWidth(24);
        c0.setMinWidth(24);

//        c0.setCellRenderer(myRenderer);
        final TableColumn c1 = getColumnModel().getColumn(1);
        c1.setWidth(250);
        c1.setMinWidth(250);

        setShowHorizontalLines(true);

        TableRowSorter sorter = new TableRowSorter<>(listTableModel);


        setRowSorter(sorter);


        if (getModel().getRowCount() == 0) {
            clearSelection();
        }

        TableSpeedSearch tableSpeedSearch = new TableSpeedSearch(this) {
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


        this.setMinimumSize(new JBDimension(800, 100));
    }

    public Consumer<? super Object> getSelectUserTaskConsumer() {
        return selectUserTaskConsumer;
    }

    public void setSelectUserTaskConsumer(Consumer<? super Object> selectUserTaskConsumer) {
        this.selectUserTaskConsumer = selectUserTaskConsumer;
    }

    public Object getSelectedObject(){
        ListTableModel model = (ListTableModel) this.getModel();
        int selectedRow = this.getSelectedRow();
        int i = this.convertRowIndexToModel(selectedRow);
        Object item = model.getItem(i);
        return item;
    }
}
