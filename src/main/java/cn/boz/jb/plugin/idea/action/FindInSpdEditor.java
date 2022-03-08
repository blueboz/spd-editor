package cn.boz.jb.plugin.idea.action;

import cn.boz.jb.plugin.floweditor.gui.widget.ChartPanel;
import cn.boz.jb.plugin.floweditor.gui.widget.FlowEditorComponent;
import cn.boz.jb.plugin.idea.widget.SpdEditor;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.SearchTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import java.awt.Container;

import static javax.swing.JLayeredPane.POPUP_LAYER;

public class FindInSpdEditor extends DumbAwareAction {

    public FindInSpdEditor() {
    }
    private ChartPanel chartPanel;

    public FindInSpdEditor(ChartPanel chartPanel){
        //拷贝快捷键
        //注册快捷
        AnAction action = ActionManager.getInstance().getAction(IdeActions.ACTION_FIND);
        if (action != null) {
            this.copyShortcutFrom(action);
        }
        this.setEnabledInModalContext(true);
        this.chartPanel=chartPanel;
        System.out.println("##########################");
        System.out.println("##########################");
        System.out.println("##########################");
        System.out.println("##########################");
        System.out.println("##########################");
        System.out.println("##########################");
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
//        System.out.println("action being perform");
//        SearchTextField searchTextField = new SearchTextField();
//        SpdEditor ancestorOfClass = (SpdEditor) SwingUtilities.getAncestorOfClass(SpdEditor.class, chartPanel);
//        JRootPane rootPane = ancestorOfClass.getRootPane();
//        if(rootPane!=null){
//            JLayeredPane layeredPane = rootPane.getLayeredPane();
//            if(layeredPane!=null){
//                searchTextField.setBounds(0,0,500,50);
//                layeredPane.add(searchTextField,POPUP_LAYER);
//            }
//        }


//        this.myPopupLayeredPane = rootPane == null ? null : rootPane.getLayeredPane();

//        SearchTextField.FindAction;
//        FindAction
//

    }

}
