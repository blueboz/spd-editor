package cn.boz.jb.plugin.idea.callsearch;


import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineTask;
import cn.boz.jb.plugin.idea.widget.SimpleIconControl;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.intellij.util.ui.ListTableModel;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.EventObject;

public class CallerSearcherTableCellEditor implements TableCellEditor {
    private int row;
    private int col;
    private Object value;
    private JCheckBox jCheckBox;

    private DefaultCellEditor defaultCellEditor;

    public CallerSearcherTableCellEditor() {
        this.defaultCellEditor = new DefaultCellEditor(new JTextField());

    }

    @Override
    public Component getTableCellEditorComponent(JTable jTable, Object val, boolean isSelected, int row, int col) {
        this.value = val;
        Color bg = isSelected ? jTable.getSelectionBackground() : jTable.getBackground();
        Color fg = isSelected ? jTable.getSelectionForeground() : jTable.getForeground();

        if (col == 1) {
            jCheckBox = new JCheckBox();
            jCheckBox.setBackground(bg);
            jCheckBox.setForeground(fg);
            jCheckBox.getModel().setSelected((Boolean) val);
            jCheckBox.getModel().addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent changeEvent) {
                    Object source = changeEvent.getSource();
                    JToggleButton.ToggleButtonModel toggleButtonModel= (JToggleButton.ToggleButtonModel) source;
                    ListTableModel model = (ListTableModel) jTable.getModel();
                    Object item = model.getItem(row);
                    if(item instanceof EngineTask){
                        ((EngineTask) item).setChecked((Boolean) toggleButtonModel.isSelected());
                    }else if(item instanceof EngineAction){
                        ((EngineAction) item).setChecked((Boolean) toggleButtonModel.isSelected());

                    }
                }
            });
            return jCheckBox;
        }
        return defaultCellEditor.getTableCellEditorComponent(jTable, val, isSelected, row, col);
    }

    @Override
    public Object getCellEditorValue() {
        return value;
    }

    @Override
    public boolean isCellEditable(EventObject eventObject) {
        return defaultCellEditor.isCellEditable(eventObject);
    }

    @Override
    public boolean shouldSelectCell(EventObject eventObject) {
        return defaultCellEditor.shouldSelectCell(eventObject);
    }

    @Override
    public boolean stopCellEditing() {
        return defaultCellEditor.stopCellEditing();
    }

    @Override
    public void cancelCellEditing() {
        defaultCellEditor.cancelCellEditing();
    }

    @Override
    public void addCellEditorListener(CellEditorListener cellEditorListener) {
        defaultCellEditor.addCellEditorListener(cellEditorListener);
    }

    @Override
    public void removeCellEditorListener(CellEditorListener cellEditorListener) {

        defaultCellEditor.removeCellEditorListener(cellEditorListener);
    }

//    @NotNull
//    @Override
//    public Component getTableCellRendererComponent(@NotNull JTable table,
//                                                   Object value,
//                                                   boolean isSelected,
//                                                   boolean hasFocus,
//                                                   int row,
//                                                   int column) {
//        Color bg = isSelected ? table.getSelectionBackground() : table.getBackground();
//        Color fg = isSelected ? table.getSelectionForeground() : table.getForeground();
//
//        if (column == 1) {
//            JCheckBox jCheckBox = new JCheckBox();
//            jCheckBox.setBackground(bg);
//            jCheckBox.setForeground(fg);
//
//            return jCheckBox;
//        }
//        return new DefaultCell;
//
//    }
}