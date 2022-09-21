package cn.boz.jb.plugin.idea.action.fmss.dialog;

import cn.boz.jb.plugin.idea.layoutmanager.MyLayoutManager;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class StringSorterDialog extends DialogWrapper {
    public StringSorterDialog(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
        init();
        setTitle("字符串排序");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new MyLayoutManager());
        JTextArea textArea = new JTextArea(8, 30);
        JBScrollPane jbScrollPane = new JBScrollPane(textArea);
        jbScrollPane.setPreferredSize(new Dimension(0,300));
        jPanel.add(jbScrollPane);
        ActionManager instance = ActionManager.getInstance();
//        instance.createActionToolbar("tool",)
        ActionToolbar tool = instance.createActionToolbar("tool", new ActionGroup() {
            @Override
            public AnAction @NotNull [] getChildren(@Nullable AnActionEvent anActionEvent) {
                return new AnAction[]{new AnAction() {
                    {
                        this.getTemplatePresentation().setIcon(AllIcons.Actions.Execute);
                    }

                    @Override
                    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                        String text = textArea.getText();
                        //按照换行符进行切割
                        String[] split = text.split("[\n]");
                        if (split != null && split.length > 0) {
                            for (int i = 0; i < split.length; i++) {
                                split[i]=split[i].trim();
                            }
                            Arrays.sort(split);
                            StringBuilder sb = new StringBuilder();
                            for (String s : split) {
                                if("".equals(s.trim())){
                                    continue;
                                }
                                sb.append(s);
                                sb.append("\n");
                            }

                            textArea.setText(sb.toString());
                        }

                    }
                }};
            }
        }, true);
        jPanel.add(tool.getComponent());


        return jPanel;
    }
}
