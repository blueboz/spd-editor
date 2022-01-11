package cn.boz.jb.plugin.floweditor.gui.widget;

import cn.boz.jb.plugin.floweditor.gui.property.Property;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import java.awt.Component;
import java.util.EventObject;

public class SpdTableCellEditor implements TableCellEditor {
    private EventListenerList listenerList = new EventListenerList();

    private JTable table;
    private int row;
    private Property property;
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        //这里需要返回什么呢
        this.row=row;
        this.table=table;
        //这里表单里面的内容需要根据value 中的editor 进行处理
        //根据行
        //返回的值是property可以直接取得编辑器
        //按照这里的来进行返回的比较好
        this.property= (Property) value;
        return property.getEditor();
    }

    @Override
    public Object getCellEditorValue() {
        //这里一般是用于处理编辑器值的
        return property.getInputValue();
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }

    @Override
    public boolean stopCellEditing() {
        ChangeEvent changeEvent = new ChangeEvent(this);
        Object[]  listeners= this.listenerList.getListenerList();
        for (Object listener : listeners) {
            if(listener instanceof CellEditorListener){
                CellEditorListener cel= (CellEditorListener) listener;
                cel.editingStopped(changeEvent);
            }
        }
        return true;
    }

    @Override
    public void cancelCellEditing() {

    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        listenerList.add(CellEditorListener.class,l);
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        listenerList.remove(CellEditorListener.class,l);
    }


}
