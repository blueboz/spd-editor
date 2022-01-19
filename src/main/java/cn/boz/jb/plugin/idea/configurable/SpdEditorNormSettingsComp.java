package cn.boz.jb.plugin.idea.configurable;

import com.intellij.ide.util.DirectoryChooser;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileTypeDescriptor;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class SpdEditorNormSettingsComp {

    private TextFieldWithBrowseButton webRootEditor;

    private JPanel mainComponent = null;

    @SuppressWarnings("unchecked")
    public SpdEditorNormSettingsComp() {
        webRootEditor = new TextFieldWithBrowseButton();
        webRootEditor.addBrowseFolderListener(null, null,
                ProjectManager.getInstance().getDefaultProject(),
                FileChooserDescriptorFactory.createSingleFolderDescriptor());

        mainComponent = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("WebRoot:"), webRootEditor, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

    }

    public JPanel getPanel() {
        return mainComponent;
    }


    public JComponent getPreferredFocusedComponent() {
        return webRootEditor;
    }

    public void apply() {
        SpdEditorNormState spdEditorState = SpdEditorNormState.getInstance();

        spdEditorState.webroot = getWebRoot();

    }

    @SuppressWarnings("unchecked")
    public String getWebRoot() {
        return webRootEditor.getText();
    }

    public void setWebRoot(String webRoot) {
        this.webRootEditor.setText(webRoot);
    }

    public void reset() {
        SpdEditorNormState spdEditorState = SpdEditorNormState.getInstance();
        setWebRoot(spdEditorState.webroot);
    }

    /**
     * 判断配置信息是否发生了改变
     *
     * @return
     */
    public boolean isModified() {
        SpdEditorNormState spdEditorState = SpdEditorNormState.getInstance();
        if (!spdEditorState.webroot.equals(this.webRootEditor.getText())) {
            return true;
        }
        return false;
    }
}
