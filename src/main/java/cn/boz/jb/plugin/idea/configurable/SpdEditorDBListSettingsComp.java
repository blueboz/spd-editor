package cn.boz.jb.plugin.idea.configurable;

import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UIUtil;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import static javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION;

public class SpdEditorDBListSettingsComp implements ListSelectionListener {

    private JBTextField showName;

    private JBTextField jdbcUrlText;

    private JBTextField jdbcUsername;

    private JBPasswordField jdbcPassword;

    private JButton testConnection;
    private JButton setAsActive;

    private JBList jdbcDriverList;

    private JPanel mainComponent;
    JBList<String> idList;
    DefaultListModel idListModels;

    private DefaultListModel<String> jdbcDriverListModel;


    private static final FileChooserDescriptor FILECHOOSERDESCRIPTOR = new FileChooserDescriptor(true, false, true, true, false, true);

    @SuppressWarnings("unchecked")
    public SpdEditorDBListSettingsComp(Project project) {


        //生成绘制对象
        idListModels = new DefaultListModel();
        idListModels.clear();
        idList = new JBList<>(idListModels) {
        };
        idList.setSelectionMode(SINGLE_INTERVAL_SELECTION);

        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(idList).disableUpDownActions();
        decorator.setAddAction(anActionButton -> {
            //直接往列表新增，插入
            DBInfo dbInfo = new DBInfo();
            dbInfo.id = UUID.randomUUID().toString().replaceAll("-", "");
            //用对象来关联
            idListModels.add(idListModels.size(), dbInfo);

        });
        decorator.setRemoveAction(anActionButton -> {
            //失去焦点自动保存
            int[] selectedIndices = idList.getSelectedIndices();
            for (int selectedIndex : selectedIndices) {
                Object elementAt = idListModels.getElementAt(selectedIndex);
                if (elementAt != null) {
                    idListModels.remove(selectedIndex);
                }
            }
//            int selectedIndex = idList.getSelectedIndex();

        });
        idList.addListSelectionListener(this);

        JBSplitter splitPane = new JBSplitter();
        splitPane.setFirstComponent(decorator.createPanel());

        splitPane.setProportion(.3f);
        splitPane.setResizeEnabled(false);
        JComponent form = makeForm(project);
        splitPane.setSecondComponent(form);
        mainComponent = new JPanel();
        mainComponent.setLayout(new BorderLayout());
        mainComponent.add(splitPane, BorderLayout.CENTER);
    }


    public JComponent makeForm(Project project) {
        showName = new JBTextField();
        showName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int selectedIndex = idList.getSelectedIndex();
                Object elementAt = idListModels.getElementAt(selectedIndex);
                if (elementAt != null) {
                    DBInfo dbInfo = (DBInfo) elementAt;
                    dbInfo.showName = showName.getText();
                    idList.repaint();
                }
            }
        });


        jdbcPassword = new JBPasswordField();
        jdbcPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int selectedIndex = idList.getSelectedIndex();
                Object elementAt = idListModels.getElementAt(selectedIndex);
                if (elementAt != null) {
                    DBInfo dbInfo = (DBInfo) elementAt;
                    dbInfo.jdbcPassword = jdbcPassword.getText();
                }
            }
        });


        jdbcUrlText = new JBTextField();
        jdbcUrlText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int selectedIndex = idList.getSelectedIndex();
                Object elementAt = idListModels.getElementAt(selectedIndex);
                if (elementAt != null) {
                    DBInfo dbInfo = (DBInfo) elementAt;
                    dbInfo.jdbcUrl = jdbcUrlText.getText();
                }
            }
        });


        jdbcUsername = new JBTextField();
        jdbcUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int selectedIndex = idList.getSelectedIndex();
                Object elementAt = idListModels.getElementAt(selectedIndex);
                if (elementAt != null) {
                    DBInfo dbInfo = (DBInfo) elementAt;
                    dbInfo.jdbcUserName = jdbcUsername.getText();
                }
            }
        });


        //包装器
        jdbcDriverListModel = new DefaultListModel();
        jdbcDriverListModel.removeAllElements();
        jdbcDriverList = new JBList<>(jdbcDriverListModel);
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(jdbcDriverList).disableUpDownActions();
        decorator.setAddAction(anActionButton -> {
            FileChooser.chooseFiles(FILECHOOSERDESCRIPTOR, null, null, files -> {
                for (VirtualFile file : files) {
                    String path = file.getPath();
                    jdbcDriverListModel.addElement(path);
                }
            });

            //失去焦点自动保存
            int selectedIndex = idList.getSelectedIndex();
            Object elementAt = idListModels.getElementAt(selectedIndex);
            if (elementAt != null) {
                DBInfo dbInfo = (DBInfo) elementAt;
                dbInfo.jdbcDriver = getJdbcDriver();
            }
        });
        decorator.setRemoveAction(anActionButton -> {
            int[] selectedIndices = jdbcDriverList.getSelectedIndices();
            for (int selectedIndex : selectedIndices) {
                jdbcDriverListModel.remove(selectedIndex);
            }

        });


        testConnection = new JButton("Test Connection");
        testConnection.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //测试连接的时候，需要执行的操作
                String jdbcUser = jdbcUsername.getText();
                @SuppressWarnings("all")
                String jdbcPass = new String(jdbcPassword.getPassword());
                String jdbcUrl = jdbcUrlText.getText();
                String jdbcDriverText = getJdbcDriver();
                final Ref<Exception> exception = Ref.create();
                ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
                    try {
                        DBUtils.testConnection(jdbcUser, jdbcPass, jdbcUrl, jdbcDriverText);
                    } catch (Exception ee) {
                        exception.set(ee);
                    }
                }, "Testing Connection", true, ProjectManager.getInstance().getDefaultProject());
                if (exception.isNull()) {
                    Messages.showMessageDialog("Connection Success", "Connection Success", UIUtil.getInformationIcon());
                } else {
                    Exception ee = exception.get();
                    ee.printStackTrace();

                    Messages.showErrorDialog("Connection Failed\n" + exception.get().getMessage(), "Connection Fail");
                }
            }
        });
        setAsActive = new JButton("SetAsActive");
        setAsActive.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedIndex = idList.getSelectedIndex();
                if (selectedIndex == -1) {
                    return;
                }
                Object elementAt = idListModels.getElementAt(selectedIndex);
                if (elementAt == null) {
                    return;
                }
                DBInfo dbInfo = (DBInfo) elementAt;
                SpdEditorDBState instance = SpdEditorDBState.getInstance(project);
                instance.jdbcUrl = dbInfo.jdbcUrl;
                instance.jdbcDriver = dbInfo.jdbcDriver;
                instance.jdbcUserName = dbInfo.jdbcUserName;
                instance.jdbcPassword = dbInfo.jdbcPassword;

            }
        });


        return FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("ShowName:"), showName, 1, false)
                .addLabeledComponent(new JBLabel("JdbcURL:"), jdbcUrlText, 1, false)
                .addComponentToRightColumn(new JBLabel("jdbc:oracle:thin:@[host]:[port]:[sid]"))
                .addLabeledComponent(new JBLabel("Username:"), jdbcUsername, 1, false)
                .addLabeledComponent(new JBLabel("Password:"), jdbcPassword, 1, false)
                .addLabeledComponent(new JBLabel("Driver:"), decorator.createPanel())
                .addComponentToRightColumn(testConnection)
                .addComponentToRightColumn(setAsActive)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public String getJdbcDriver() {
        StringBuilder jdbcDriverStrings = new StringBuilder();
        for (int i = 0; i < jdbcDriverListModel.size(); i++) {
            jdbcDriverStrings.append(jdbcDriverListModel.get(i));
            jdbcDriverStrings.append(";");
        }
        jdbcDriverStrings.deleteCharAt(jdbcDriverStrings.length() - 1);
        return jdbcDriverStrings.toString();
    }

    public JPanel getPanel() {
        return mainComponent;
    }


    public JComponent getPreferredFocusedComponent() {
        return idList;
//        return jdbcUrlText;
    }

    //查询数
    public void apply(Project project) {
        //进行持久化
        SpdEditorDBListState spdEditorState = SpdEditorDBListState.getInstance(project);
        //应用到当前列表
        Enumeration elements = this.idListModels.elements();
        List<DBInfo> list = new ArrayList<>();
        while (elements.hasMoreElements()) {
            DBInfo o = (DBInfo) elements.nextElement();
            list.add(o);
        }
        spdEditorState.configList = list;

    }


    public void reset(Project project) {
        SpdEditorDBListState spdEditorState = SpdEditorDBListState.getInstance(project);
        List<DBInfo> configList = spdEditorState.configList;
        idListModels.clear();
        if (!CollectionUtils.isEmpty(configList)) {
            idListModels.addAll(configList);
        }
    }

    /**
     * 判断配置信息是否发生了改变
     *
     * @return
     */
    public boolean isModified(Project project) {
        SpdEditorDBListState spdEditorState = SpdEditorDBListState.getInstance(project);
        if(idListModels==null&&spdEditorState.configList==null){
            return false;
        }
        if(idListModels==null&&spdEditorState.configList!=null){
            return true;
        }
        if(idListModels!=null&&spdEditorState.configList==null){
            return true;
        }
        if (idListModels.size() != spdEditorState.configList.size()) {

            return true;
        }
        if(idListModels==null){
            return false;
        }
        Enumeration elements = idListModels.elements();
        while (elements.hasMoreElements()) {
            Object o = elements.nextElement();
            //如果不包含，则有差异
            if (!spdEditorState.configList.contains(o)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        if (!listSelectionEvent.getValueIsAdjusting()) {

            int selectedIndex = idList.getSelectedIndex();
            if (selectedIndex == -1) {
                return;


            }

            Object elementAt = idListModels.getElementAt(selectedIndex);
            if (elementAt != null) {
                DBInfo dbInfo = (DBInfo) elementAt;
                showName.setText(dbInfo.showName);
                jdbcUrlText.setText(dbInfo.jdbcUrl);
                jdbcUsername.setText(dbInfo.jdbcUserName);
                jdbcPassword.setText(dbInfo.jdbcPassword);
                setJdbcDriver(dbInfo.jdbcDriver);
            }
        }
        //正向送
//            int selectedIndex = idList.getSelectedIndex();
//            Object elementAt = idListModels.getElementAt(selectedIndex);
//            if(elementAt!=null){
//                DBInfo dbInfo= (DBInfo) elementAt;
//                dbInfo.showName=showName.getText();
//                dbInfo.jdbcUrl=jdbcUrlText.getText();
//                dbInfo.jdbcUserName=jdbcUsername.getText();
//                dbInfo.jdbcPassword=jdbcPassword.getText();
//                dbInfo.jdbcDriver=getJdbcDriver();
//
    }

    public void setJdbcDriver(String jdbcDriver) {
        jdbcDriverListModel.removeAllElements();
        if (jdbcDriver != null) {
            String[] drivers = jdbcDriver.split(";");
            for (String driver : drivers) {
                if (!StringUtils.isBlank(driver)) {
                    this.jdbcDriverListModel.addElement(driver);
                }
            }
        }
    }
}
