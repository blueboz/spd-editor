package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.floweditor.gui.property.InputLongTextDialog;
import cn.boz.jb.plugin.idea.bean.EngineAction;
import cn.boz.jb.plugin.idea.bean.EngineTask;
import cn.boz.jb.plugin.idea.callsearch.CallerSearcherCommentPanel;
import cn.boz.jb.plugin.idea.callsearch.CallerSearcherDetailComment;
import cn.boz.jb.plugin.idea.dialog.EngineActionDialog;
import cn.boz.jb.plugin.idea.dialog.EngineTaskDialog;
import cn.boz.jb.plugin.idea.dialog.min.EngineActionDerivePanel;
import cn.boz.jb.plugin.idea.dialog.min.EngineTaskDerivePanel;
import cn.boz.jb.plugin.idea.utils.NotificationUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManagerImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.ui.popup.util.PopupUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JsonFormatAction extends AnAction implements DumbAware {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Component component = anActionEvent.getInputEvent().getComponent();
        InputLongTextDialog dialog = (InputLongTextDialog) InputLongTextDialog.findInstance(component);
        if(dialog!=null){
            String inputText = dialog.getInputText();
            try{
                String json = JSON.toJSONString(JSON.parseObject(inputText), SerializerFeature.PrettyFormat);
                json=json.replaceAll("\t","  ");
                dialog.setInputText(json);
            }catch (Exception e){
                NotificationUtils.warnning("format error",inputText,anActionEvent.getProject());
            }
        }


    }


}
