package cn.boz.test.swing;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class MyCell extends JComponent implements MouseListener {

    private JList<?> list;
    private Object value;
    private boolean isSelected;
    private boolean cellHasFocus;
    private int index;
    private boolean hover;

    public MyCell(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        this.setPreferredSize(new Dimension(80, 30));
        this.setSize(new Dimension(80, 30));
        this.list = list;
        this.value = value;
        this.isSelected = isSelected;
        this.cellHasFocus = cellHasFocus;
        this.index = index;
        this.addMouseListener(this);
        this.setFocusable(true);


    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        int width = this.getWidth();
        int height = this.getHeight();
        if (hover) {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);
            g2d.setColor(Color.black);
            g2d.drawLine(0, 0, width, height);
        } else {
            g2d.setColor(Color.black);
            g2d.fillRect(0, 0, width, height);
            g2d.setColor(Color.WHITE);
            g2d.drawLine(0, 0, width, height);
        }

        g.dispose();
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

        System.out.println("press");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("release");

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        System.out.println("mouse enter");
        hover = true;
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        System.out.println("mouse leave");
        hover = false;

        repaint();
    }

    public void update(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        this.list = list;
        this.value = value;
        this.isSelected = isSelected;
        this.cellHasFocus = cellHasFocus;
        this.index = index;

    }
}

public class FilteredList extends JList {
    private FilterModel model;

    private FilterField filterField;

    ListCellRenderer<Object> listCellRenderer = new ListCellRenderer<>() {
        private Map<Object, MyCell> cells = new HashMap<>();

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            System.out.println("get componenth");
            if (!cells.containsKey(value)) {
                synchronized (FilteredList.class) {
                    if (!cells.containsKey(value)) {
                        cells.put(value, new MyCell(list, value, index, isSelected, cellHasFocus));
                        return cells.get(value);
                    }
                }
            }
            MyCell myCell = cells.get(value);
            myCell.update(list, value, index, isSelected, cellHasFocus);
            return myCell;
        }
    };

    @Override
    public ListCellRenderer getCellRenderer() {
//        System.out.println("get cell render");
//        return listCellRenderer;
        return listCellRenderer;

    }

    public FilteredList() {
        model = new FilterModel();
        setModel(model);
        filterField = new FilterField(20);
//
    }

    public void addItem(Object o) {
        model.addElement(o);
    }

    public FilterField getFilterField() {
        return filterField;
    }

    private class FilterModel extends AbstractListModel {
        private List items;
        private List filteredItems;

        public FilterModel() {
            //主要是对FilterModel进行修改
            items = new ArrayList();
            filteredItems = new ArrayList();
        }

        public void addElement(Object o) {//添加一个项到列表中
            items.add(o);
            refilter();//每添加一个项就更新filterItems
        }

        private void refilter() {
            filteredItems.clear();
            String item = getFilterField().getText();
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).toString().toUpperCase().indexOf(item, 0) != -1
                        || items.get(i).toString().toLowerCase().indexOf(item,
                        0) != -1) {
                    filteredItems.add(items.get(i));
                }
            }
            fireContentsChanged(this, 0, filteredItems.size());

        }

        @Override
        public Object getElementAt(int index) {
            if (index < filteredItems.size()) {
                return filteredItems.get(index);
            }
            return null;
        }

        @Override
        public int getSize() {
            return filteredItems.size();
        }
    }

    /**
     * 继承自JTextField,只是实现了一个DocumentListener接口，，做到实时监听用户的任何动作，
     * 插入，删除，修改等，在用户触发这个监听的时候，就需要和FilterMode进行交互,动态变化FiltedList中的数据显示
     */
    private class FilterField extends JTextField implements DocumentListener {
        public FilterField(int width) {
            super(width);
            getDocument().addDocumentListener(this);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            model.refilter();//更新列表的显示数据，下同
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            model.refilter();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            model.refilter();
        }
    }

    public static void main(String[] args) {
        String[] listItems = {"Chris", "Joshua", "Daniel", "Michael", "Don",
                "Kimi", "Kelly", "Keagan"};
        JFrame frame = new JFrame("FilteredJList");
        frame.getContentPane().setLayout(new BorderLayout());
// populate list
        FilteredList list = new FilteredList();
        for (int i = 0; i < listItems.length; i++)
            list.addItem(listItems[i]);
// add to gui
        JScrollPane pane = new JScrollPane(list,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //一个是过滤列
        frame.getContentPane().add(pane, BorderLayout.CENTER);
        //另外一个是列表的过滤器
        frame.getContentPane().add(list.getFilterField(), BorderLayout.NORTH);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}


