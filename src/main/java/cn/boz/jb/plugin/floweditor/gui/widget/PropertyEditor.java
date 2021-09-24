package cn.boz.jb.plugin.floweditor.gui.widget;

import cn.boz.jb.plugin.floweditor.gui.events.ShapeSelectedEvent;
import cn.boz.jb.plugin.floweditor.gui.listener.ShapeSelectedListener;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.CallActivity;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ExclusiveGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ForeachGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ParallelGateway;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.SequenceFlow;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.ServiceTask;
import cn.boz.jb.plugin.floweditor.gui.process.fragment.UserTask;
import com.intellij.execution.util.StringWithNewLinesCellEditor;
import com.intellij.util.ui.AbstractTableCellEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.DefaultCellEditor;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.EventObject;

public class PropertyEditor extends JPanel implements ShapeSelectedListener {

    private Object operatedObject;

    public Object getOperatedObject() {
        return operatedObject;
    }

    public String trans(String value){
        if(value==null){
            return "";
        }
        return value.replace("#LEY#","\n");
    }
    public void setOperatedObject(Object operatedObject) {
        //修改操作的对象的时候，是需要重新进行维护的
        if(this.operatedObject==operatedObject){
            return ;
        }
        this.operatedObject = operatedObject;
        //
        DefaultTableModel model = (DefaultTableModel) jTable.getModel();
        model.setRowCount(0);
        if(operatedObject==null) {
        }
        if(operatedObject instanceof UserTask){
            UserTask userTask= (UserTask) operatedObject;
            model.addRow(new Object[]{"Bussines Key",trans(userTask.getBussinesKey())});
            model.addRow(new Object[]{"Bussines Id",trans(userTask.getBussinesId())});
            model.addRow(new Object[]{"Descrition",trans(userTask.getBussinesDescrition())});
            model.addRow(new Object[]{"Rights",trans(userTask.getRights())});
            model.addRow(new Object[]{"Expression",trans(userTask.getExpressionExt())});
            model.addRow(new Object[]{"Valid Second",trans(userTask.getValidSecond())});
            model.addRow(new Object[]{"Open Second",trans(userTask.getOpenSecond())});
            model.addRow(new Object[]{"Event Listener",trans(userTask.getEventListener())});
        }
        if(operatedObject instanceof ServiceTask){
            ServiceTask serviceTask= (ServiceTask) operatedObject;
            model.addRow(new Object[]{"Expression",trans(serviceTask.getExpression())});
            model.addRow(new Object[]{"Listener",trans(serviceTask.getListener())});
        }
        if(operatedObject instanceof SequenceFlow){
            SequenceFlow sequenceFlow=(SequenceFlow) operatedObject;
            model.addRow(new Object[]{"Condition",trans(sequenceFlow.getConditionExpression())});
            model.addRow(new Object[]{"Name",trans(sequenceFlow.getName())});
        }
        if(operatedObject instanceof ExclusiveGateway){
            ExclusiveGateway exclusiveGateway=(ExclusiveGateway)operatedObject ;
            model.addRow(new Object[]{"Name",exclusiveGateway.getName()});
        }
        if(operatedObject instanceof ParallelGateway){
            ParallelGateway parallelGateway=(ParallelGateway)operatedObject ;
            model.addRow(new Object[]{"Name",parallelGateway.getName()});
        }
        if(operatedObject instanceof ForeachGateway){
            ForeachGateway foreachGateway=(ForeachGateway)operatedObject ;
            model.addRow(new Object[]{"Name",foreachGateway.getName()});
        }

        if(operatedObject instanceof CallActivity){
            CallActivity callActivity=(CallActivity)operatedObject ;
            model.addRow(new Object[]{"Call Element",callActivity.getCalledElement()});
        }
        if(operatedObject instanceof ChartPanel){
            model.addRow(new Object[]{"id",chartPanel.getId()});
            model.addRow(new Object[]{"name",chartPanel.getName()});

        }
        jTable.setModel(model);
    }
    private ChartPanel chartPanel;

    private JTable jTable;
    private JScrollPane jScrollPane;

    public PropertyEditor(ChartPanel chartPanel){
        this.chartPanel=chartPanel;
        chartPanel.registerShapeSelectedListener(this);

        this.setPreferredSize(new Dimension(0,250));
        jTable = new JTable(){

            @Override
            public boolean isCellEditable(int row, int column) {
                if(column==0){
                    return false;
                }
                return super.isCellEditable(row, column);
            }

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if(operatedObject instanceof UserTask){
                    if(column==1)
                    {
                        if(row==4){
                            return new TableCellEditor() {

                                private JTextArea textArea;
                                private JTable table;
                                private JScrollPane jScrollPane;
                                @Override
                                public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                                    this.table=table;

                                    this.textArea=new JTextArea();
                                    jScrollPane = new JScrollPane(textArea);
                                    textArea.setText((String) value);
                                    textArea.setAutoscrolls(true);
                                    textArea.setLineWrap(true);
                                    textArea.addFocusListener(new FocusAdapter() {
                                        @Override
                                        public void focusGained(FocusEvent e) {
                                            DefaultTableModel dtm= (DefaultTableModel) table.getModel();
                                            table.setRowHeight(4,120);
                                            super.focusGained(e);
                                        }
                                    });
                                    return jScrollPane;
                                }

                                @Override
                                public Object getCellEditorValue() {
                                    return textArea.getText();
                                }

                                @Override
                                public boolean isCellEditable(EventObject anEvent) {
                                    System.out.println(anEvent);
                                    return true;
                                }

                                @Override
                                public boolean shouldSelectCell(EventObject anEvent) {
                                    return true;
                                }

                                @Override
                                public boolean stopCellEditing() {
                                    DefaultTableModel dtm= (DefaultTableModel) table.getModel();
                                    table.setRowHeight(4,38);
                                    return true;
                                }

                                @Override
                                public void cancelCellEditing() {

                                }

                                @Override
                                public void addCellEditorListener(CellEditorListener l) {
                                }

                                @Override
                                public void removeCellEditorListener(CellEditorListener l) {

                                }
                            };
                        }
                    }
                }
                return super.getCellEditor(row, column);
            }

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                //可以重写TableCellRender进行重新渲染
                return super.getCellRenderer(row, column);
            }
        };
        DefaultTableModel model = (DefaultTableModel) jTable.getModel();
        model.setRowCount(0);
        model.setColumnIdentifiers(new Object[]{"property","value"});

        jTable.setRowHeight(38);

        this.setLayout(new BorderLayout());
        JTableHeader tableHeader = jTable.getTableHeader();
        tableHeader.setPreferredSize(new Dimension(0,38));
        jScrollPane = new JScrollPane(jTable);
        this.add(jScrollPane ,BorderLayout.CENTER);
    }

    @Override
    public void shapeSelected(ShapeSelectedEvent shapeSelectedEvent) {
        Object selectedObject = shapeSelectedEvent.getSelectedObject();
        setOperatedObject(selectedObject);
    }
}
