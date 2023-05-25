package cn.boz.jb.plugin.codegen;

import cn.boz.jb.plugin.idea.bean.UserTabCols;
import cn.boz.jb.plugin.idea.layoutmanager.MyLayoutManager;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import oracle.jdbc.OracleDriver;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeGenFrame extends JFrame {
    private static final String CONNECTION_URL = "jdbc:oracle:thin:@21.96.5.85:1521:FMSS";

    public CodeGenFrame() throws MalformedURLException, SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        initUI();
    }

    Connection connection = DBUtils.getConnection("xfunds201701", "Xfunds_1234", "jdbc:oracle:thin:@21.96.5.85:1521:FMSS", OracleDriver.class.getName());

    private void alignCenter() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = this.getWidth();
        int height = this.getHeight();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        this.setLocation(x, y);
    }

    JTextField packageName;
    JTextField clzName;

    JTextField searchField;

    private void initUI() throws MalformedURLException, SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        setTitle("My Swing Form");
        setSize(800, 800);
        alignCenter();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new MyLayoutManager());
        JScrollPane tableListScrollPane = createTableListScrollPane(searchField);
        searchField = createSearchField();
        JScrollPane jTable = createTable();
        Container contentPane = getContentPane();
        packageName = new JTextField();
        packageName.setText("com.erayt.xfunds.unknown");
        clzName = new JTextField();
        clzName.setText("NotKnown");
        contentPane.add(searchField);
        contentPane.add(tableListScrollPane);
        contentPane.add(jTable);
        contentPane.add(new JLabel("包路径"));
        contentPane.add(packageName);
        contentPane.add(new JLabel("类名"));
        contentPane.add(clzName);
        contentPane.add(jTable);

        setVisible(true);
    }

    DefaultTableModel defaultTableModel;

    private JScrollPane createTable() {
        defaultTableModel = new DefaultTableModel();
        defaultTableModel.addColumn("Table Name");
        defaultTableModel.addColumn("Column Name");
        defaultTableModel.addColumn("目标 ");
        defaultTableModel.addColumn("Data Type");
        defaultTableModel.addColumn("Comments");
        JTable table = new JTable(defaultTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        return scrollPane;

    }

    private JTextField createSearchField() {

        JTextField searchField = new JTextField();
        searchField.getDocument().addDocumentListener(new FilterListener());
        return searchField;
    }

    private class FilterListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            filter();
        }

        public void removeUpdate(DocumentEvent e) {
            filter();
        }

        public void changedUpdate(DocumentEvent e) {
            filter();
        }

        private void filter() {
            tableListListModel.filter(searchField.getText());
            ((HighlightingListCellRenderer) tableList.getCellRenderer()).setPattern(searchField.getText());
            tableList.repaint();
        }
    }

    FilteredListModel<String> tableListListModel;
    JList<String> tableList;

    private JScrollPane createTableListScrollPane(JTextField searchField) throws SQLException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, MalformedURLException {
        List<String> tableNames = DBUtils.getInstance().queryUserTables(connection);
        tableList = new JList<String>();
        tableListListModel = new FilteredListModel<>(tableNames);
        tableList.setCellRenderer(new HighlightingListCellRenderer());

        tableList.setModel(tableListListModel);

        tableList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedValue = tableList.getSelectedValue();
                    try {
                        String[] nsTName = nsTnameExtract(selectedValue);
                        if(nsTName.length!=0){
                            packageName.setText("com.erayt.xfunds."+StringUtils.lowerCase(nsTName[0]));
                            clzName.setText(StringUtils.capitalize(StringUtils.lowerCase(nsTName[1])));
                        }
                        List<UserTabCols> userTabCols = DBUtils.getInstance().queryUserTablesCols(connection, selectedValue);

                        defaultTableModel.setRowCount(0);
                        for (UserTabCols tabCols : userTabCols) {
                            Object[] rowData = {
                                    tabCols.getTableName(),
                                    tabCols.getColumnName(),
                                    tabCols.getColumnName().toLowerCase(),
                                    tabCols.getDataType(),
                                    tabCols.getComments(),
                                    // repeat for the remaining columns
                            };
                            defaultTableModel.addRow(rowData);
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        JScrollPane jScrollPane = new JScrollPane(tableList);
        jScrollPane.setPreferredSize(new Dimension(0, 150));
        return jScrollPane;
    }


    public static String[] nsTnameExtract(String tName) {
        Pattern compile = Pattern.compile("XFUNDS_([A-Z]+)_([A-Z]+)");
        Matcher matcher = compile.matcher(tName);
        if (matcher.matches()) {
            String ns = matcher.group(1);
            String objName = matcher.group(2);
            return new String[]{ns,objName};
        }
        return new String[0];
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new CodeGenFrame();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private class FilteredListModel<E> extends DefaultListModel<E> {
        private List<E> items;

        public FilteredListModel(List<E>items) {
            this.items=items;
            for (E item : items) {
                addElement(item);
            }
        }

        public void setItems(List<E> items) {
            this.items = items;
            // clear the model and populate it with the new items
            clear();
            for (E item : items) {
                addElement(item);
            }
        }

        public void filter(String search) {
            if (search.isEmpty()) {
                fireContentsChanged(this, 0, getSize() - 1);
            } else {
                List<E> filtered = new ArrayList<>();
                for (E item : items) {
                    if (item.toString().toLowerCase().contains(search.toLowerCase())) {
                        filtered.add(item);
                    }
                }
                fireContentsChanged(this, 0, getSize() - 1);
                clear();
                for (E item : filtered) {
                    addElement(item);
                }
            }
        }
    }

    private class HighlightingListCellRenderer extends DefaultListCellRenderer {
        private Pattern pattern;

        public HighlightingListCellRenderer() {
            setOpaque(true);
        }

        public void setText(String text) {
            if (pattern == null) {
                super.setText(text);
                return;
            }
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                StringBuilder builder = new StringBuilder();
                int lastMatch = 0;
                do {
                    builder.append(text.substring(lastMatch, matcher.start()));
                    builder.append("<font color='red'>");
                    builder.append(text.substring(matcher.start(), matcher.end()));
                    builder.append("</font>");
                    lastMatch = matcher.end();
                } while (matcher.find());
                builder.append(text.substring(lastMatch));
                super.setText("<html>" + builder.toString() + "</html>");
            } else {
                super.setText(text);
            }
        }

        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (pattern != null) {
                setText(value.toString());
            } else {
                setText((String) value);
            }
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            return this;
        }

        public void setPattern(String search) {
            if (search.isEmpty()) {
                pattern = null;
            } else {
                pattern = Pattern.compile(Pattern.quote(search), Pattern.CASE_INSENSITIVE);
            }
        }
    }



}