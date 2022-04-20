package cn.boz.jb.plugin.idea.codedef.dialog;

import cn.boz.jb.plugin.idea.dialog.MyLayoutManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.AnActionButton;
import com.intellij.util.ui.FormBuilder;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;

/**
 * 添加对话框包装器
 */
public class AddCodeDefDialog extends DialogWrapper {

    private JPanel jPanel;
    private MyLayoutManager myLayoutManager;



    public AddCodeDefDialog(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
        init();
        setTitle("添加备选代码");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = FormBuilder.createFormBuilder()
                .addLabeledComponent("类型", new JPanel() {
                    private JTextField typeTextField;

                    {

                        AnActionButton goBtn = new AnActionButton("go", "go", SpdEditorIcons.MENU_16_ICON) {
                            @Override
                            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                                System.out.println("go");
                            }
                        };
                        JComponent goBtnCmp = goBtn.getContextComponent();
                        typeTextField = new JTextField();
                        setLayout(new BorderLayout());
                        add(goBtnCmp, BorderLayout.CENTER);
                        add(typeTextField, BorderLayout.EAST);
                    }
                })
                .addComponentFillVertically(new JPanel(), 0).getPanel();

        return panel;
    }

}
