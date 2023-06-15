package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.floweditor.gui.property.InputLongTextDialog;
import cn.boz.jb.plugin.idea.utils.JsonFormatUtils;
import cn.boz.jb.plugin.idea.utils.NotificationUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class JsonFormatAction extends AnAction implements DumbAware {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Component component = anActionEvent.getInputEvent().getComponent();
        InputLongTextDialog dialog = (InputLongTextDialog) InputLongTextDialog.findInstance(component);
        if(dialog!=null){
            String inputText = dialog.getInputText();
            try{
                String json=JsonFormatUtils.formatJson(inputText);
                dialog.setInputText(json);
            }catch (Exception e){
                NotificationUtils.warnning("format error",inputText,anActionEvent.getProject());
            }
        }


    }


}
