package cn.boz.jb.plugin.idea.configurable;

import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.ide.highlighter.ArchiveFileType;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SpdEditorSettingsComp {

    private JBTextField jdbcUrlText;

    private JBTextField jdbcUsername;

    private JBPasswordField jdbcPassword;

    private TextFieldWithBrowseButton jdbcDriver;

    private JButton testConnection;

    private JPanel mainComponent;

    @SuppressWarnings("unchecked")
    public SpdEditorSettingsComp() {
        jdbcPassword = new JBPasswordField();
        jdbcUrlText = new JBTextField();
        jdbcUsername = new JBTextField();
        testConnection = new JButton("Test Connection");
        testConnection.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //测试连接的时候，需要执行的操作
                String jdbcUser = jdbcUsername.getText();
                String jdbcPass = jdbcPassword.getText();
                String jdbcUrl = jdbcUrlText.getText();
                String jdbcDriverText = jdbcDriver.getText();
                try {
                    boolean execute = DBUtils.testConnection(jdbcUser, jdbcPass, jdbcUrl, jdbcDriverText);
                    if (execute) {
                        Messages.showMessageDialog("连接成功", "连接成功", null);
                    } else {
                        Messages.showWarningDialog("未知情况", "连接成功，但是查询是返回false,可能是sql 不兼容");
                    }
                } catch (Exception ex) {
                    Messages.showErrorDialog(ex.getMessage(), "连接失败");
                }
            }
        });
        FileChooserDescriptor chooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor().withFileFilter(file -> {
            FileType fileType = file.getFileType();
            if (fileType instanceof ArchiveFileType) {
                return true;
            }
            return false;
        });
        chooserDescriptor.setTitle("select jar");
        jdbcDriver = new TextFieldWithBrowseButton();
        jdbcDriver.addBrowseFolderListener("select jar", null, null, chooserDescriptor);
        mainComponent = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("JdbcURL:"), jdbcUrlText, 1, false)
                .addComponentToRightColumn(new JBLabel("jdbc:oracle:thin:@[host]:[port]:[sid]"))
                .addLabeledComponent(new JBLabel("Username:"), jdbcUsername, 1, false)
                .addLabeledComponent(new JBLabel("Password:"), jdbcPassword, 1, false)
                .addLabeledComponent(new JBLabel("Driver:"), jdbcDriver)
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
        SpdEditorState spdEditorState = SpdEditorState.getInstance();
        spdEditorState.jdbcDriver = this.jdbcDriver.getText();
        spdEditorState.jdbcUrl = this.jdbcUrlText.getText();
        spdEditorState.jdbcUserName = this.jdbcUsername.getText();
        spdEditorState.jdbcPassword = this.jdbcPassword.getText();
    }

    public void reset() {
        SpdEditorState spdEditorState = SpdEditorState.getInstance();
        this.jdbcDriver.setText(spdEditorState.jdbcDriver);
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
        SpdEditorState spdEditorState = SpdEditorState.getInstance();
        if (!spdEditorState.jdbcDriver.equals(this.jdbcDriver.getText())) {
            return true;
        }
        if (!spdEditorState.jdbcUrl.equals(this.jdbcUrlText.getText())) {
            return true;
        }
        if (!spdEditorState.jdbcUserName.equals(this.jdbcUsername.getText())) {
            return true;
        }
        if (!spdEditorState.jdbcPassword.equals(this.jdbcPassword.getText())) {
            return true;
        }
        return false;
    }
}
