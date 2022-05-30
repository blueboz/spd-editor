package cn.boz.jb.plugin.idea.configurable;

import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileTypeDescriptor;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UIUtil;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SpdEditorDBSettingsComp {

    private JBTextField jdbcUrlText;

    private JBTextField jdbcUsername;

    private JBPasswordField jdbcPassword;


    private JButton testConnection;

    private JPanel mainComponent;

    private JBList jdbcDriverList;

    private DefaultListModel<String> jdbcDriverListModel;

    private static final FileTypeDescriptor JAR_DESCRIPTOR =
            new FileTypeDescriptor("select jdbc driver", ".jar", ".JAR");


    @SuppressWarnings("unchecked")
    public SpdEditorDBSettingsComp() {
        jdbcPassword = new JBPasswordField();
        jdbcUrlText = new JBTextField();
        jdbcUsername = new JBTextField();

        //包装器
        jdbcDriverListModel = new DefaultListModel();
        jdbcDriverListModel.clear();
        jdbcDriverList = new JBList<>(jdbcDriverListModel);
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(jdbcDriverList).disableUpDownActions();
        decorator.setAddAction(anActionButton -> {

            FileChooser.chooseFiles(JAR_DESCRIPTOR, null, null, files -> {
                for (VirtualFile file : files) {
                    String path = file.getPath();
                    jdbcDriverListModel.addElement(path);
                }
            });

        });
        decorator.setRemoveAction(anActionButton -> {
            int selectedRow = jdbcDriverList.getSelectedIndex();
            jdbcDriverListModel.remove(selectedRow);
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


        mainComponent = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("JdbcURL:"), jdbcUrlText, 1, false)
                .addComponentToRightColumn(new JBLabel("jdbc:oracle:thin:@[host]:[port]:[sid]"))
                .addLabeledComponent(new JBLabel("Username:"), jdbcUsername, 1, false)
                .addLabeledComponent(new JBLabel("Password:"), jdbcPassword, 1, false)
                .addLabeledComponent(new JBLabel("Driver:"), decorator.createPanel())
                .addComponentToRightColumn(testConnection)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

    }

    public JPanel getPanel() {
        return mainComponent;
    }


    public JComponent getPreferredFocusedComponent() {
        return jdbcUrlText;
    }

    public void apply() {
        SpdEditorDBState spdEditorState = SpdEditorDBState.getInstance();

        spdEditorState.jdbcDriver = getJdbcDriver();
        spdEditorState.jdbcUrl = this.jdbcUrlText.getText();
        spdEditorState.jdbcUserName = this.jdbcUsername.getText();
        spdEditorState.jdbcPassword = new String(this.jdbcPassword.getPassword());
    }

    @SuppressWarnings("unchecked")
    public String getJdbcDriver() {
        StringBuilder jdbcDriverStrings = new StringBuilder();
        for (int i = 0; i < jdbcDriverListModel.size(); i++) {
            jdbcDriverStrings.append(jdbcDriverListModel.get(i));
            jdbcDriverStrings.append(";");
        }
        jdbcDriverStrings.deleteCharAt(jdbcDriverStrings.length() - 1);
        return jdbcDriverStrings.toString();
    }

    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriverListModel.clear();
        if (jdbcDriver != null) {
            String[] drivers = jdbcDriver.split(";");
            for (String driver : drivers) {
                this.jdbcDriverListModel.addElement(driver);
            }
        }
    }

    public void reset() {
        SpdEditorDBState spdEditorState = SpdEditorDBState.getInstance();
        setJdbcDriver(spdEditorState.jdbcDriver);
        this.jdbcUrlText.setText(spdEditorState.jdbcUrl);
        this.jdbcUsername.setText(spdEditorState.jdbcUserName);
        this.jdbcPassword.setText(spdEditorState.jdbcPassword);

    }

    /**
     * 判断配置信息是否发生了改变
     *
     * @return
     */
    public boolean isModified() {
        SpdEditorDBState spdEditorState = SpdEditorDBState.getInstance();
        if (!spdEditorState.jdbcDriver.equals(this.getJdbcDriver())) {
            return true;
        }
        if (!spdEditorState.jdbcUrl.equals(this.jdbcUrlText.getText())) {
            return true;
        }
        if (!spdEditorState.jdbcUserName.equals(this.jdbcUsername.getText())) {
            return true;
        }
        if (!spdEditorState.jdbcPassword.equals(new String(this.jdbcPassword.getPassword()))) {
            return true;
        }
        return false;
    }
}
