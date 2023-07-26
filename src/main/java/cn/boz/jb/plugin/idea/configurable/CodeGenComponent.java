package cn.boz.jb.plugin.idea.configurable;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

public class CodeGenComponent {

    private TextFieldWithBrowseButton webRootEditor;

    private JTextField mockBaseEditor;


    private JPanel mainComponent = null;

    @SuppressWarnings("unchecked")
    public CodeGenComponent() {
        webRootEditor = new TextFieldWithBrowseButton();
        webRootEditor.addBrowseFolderListener(null, null,
                ProjectManager.getInstance().getDefaultProject(),
                FileChooserDescriptorFactory.createSingleFolderDescriptor());

        mockBaseEditor=new JTextField();
        mainComponent = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("WebRoot:"), webRootEditor, 1, false)
                .addLabeledComponent(new JBLabel("MockBase:"), mockBaseEditor, 1, false)
                .addComponentToRightColumn(new JBLabel("add VM options in tomcat to enable mock server\n\"-DHTTP_TEST_SERVER_ENABLED=true -DHTTP_TEST_SERVER_PORT=10923\""))

                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

    }

    public JPanel getPanel() {
        return mainComponent;
    }


    public JComponent getPreferredFocusedComponent() {
        return webRootEditor;
    }

    public void apply(Project project) {
        SpdEditorNormState spdEditorState = SpdEditorNormState.getInstance(project);
        spdEditorState.webroot = getWebRoot();
        spdEditorState.mockbase=mockBaseEditor.getText();
    }

    @SuppressWarnings("unchecked")
    public String getWebRoot() {
        return webRootEditor.getText();
    }

    public void setWebRoot(String webRoot) {
        this.webRootEditor.setText(webRoot);
    }

    public void reset(Project project) {
        SpdEditorNormState spdEditorState = SpdEditorNormState.getInstance(project);
        setWebRoot(spdEditorState.webroot);
        this.mockBaseEditor.setText(spdEditorState.mockbase);
    }

    /**
     * 判断配置信息是否发生了改变
     *
     * @return
     */
    public boolean isModified(Project project) {
        SpdEditorNormState spdEditorState = SpdEditorNormState.getInstance(project);
        if (!spdEditorState.webroot.equals(this.webRootEditor.getText())) {
            return true;
        }
        if(!spdEditorState.mockbase.equals(this.mockBaseEditor.getText())){
            return true;
        }

        return false;
    }
}
