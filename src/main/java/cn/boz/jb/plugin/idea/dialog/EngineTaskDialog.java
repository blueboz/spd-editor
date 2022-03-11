package cn.boz.jb.plugin.idea.dialog;

import cn.boz.jb.plugin.idea.bean.EngineTask;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class EngineTaskDialog extends JComponent {
    private MyLayoutManager myLayoutManager;

    public EngineTaskDialog(EngineTask engineTask) {
        myLayoutManager = new MyLayoutManager();

        this.setLayout(myLayoutManager);

        String id = engineTask.getId();
        String title = engineTask.getTitle();
        String type = engineTask.getType();
        String bussinesid = engineTask.getBussinesid();
        String bussineskey = engineTask.getBussineskey();
        String opensecond = engineTask.getOpensecond();
        String validsecond = engineTask.getValidsecond();
        String expression = engineTask.getExpression();
        String returnvalue = engineTask.getReturnvalue();
        String tasklistener = engineTask.getTasklistener();

        JLabel idlabel = new JLabel("id");
        JTextArea idArea = new JTextArea(id);

        JLabel titleLabel = new JLabel("title");
        JTextArea titleArea = new JTextArea(title);

        JLabel typeLabel = new JLabel("type");
        JTextArea typeArea = new JTextArea(type);

        JLabel busLabel = new JLabel("bussinesid");
        JTextArea busArea = new JTextArea(bussinesid);

        JLabel bussineskeyLabel = new JLabel("bussineskey");
        JTextArea bussineskeyArea = new JTextArea(bussineskey);

        JLabel opensecondLabel = new JLabel("opensecond");
        JTextArea opensecondArea = new JTextArea(opensecond);

        JLabel validsecondLabel = new JLabel("validsecond");
        JTextArea validsecondArea = new JTextArea(validsecond);

        JLabel expressionLabel = new JLabel("expression");
        JTextArea expressionTA = new JTextArea("", 7, 30);
        expressionTA.setText(expression);


        JLabel returnvalueLabel = new JLabel("returnvalue");
        JTextArea returnvalueArea = new JTextArea(returnvalue);

        JLabel tasklistenerLabel = new JLabel("tasklistener");
        JTextArea tasklistenerArea = new JTextArea(tasklistener);


        JBScrollPane expressionTASc = new JBScrollPane(expressionTA);


        this.add(idlabel);
        this.add(idArea);
        this.add(titleLabel);
        this.add(titleArea);
        this.add(typeLabel);
        this.add(typeArea);
        this.add(busLabel);
        this.add(busArea);
        this.add(bussineskeyLabel);
        this.add(bussineskeyArea);
        this.add(opensecondLabel);
        this.add(opensecondArea);
        this.add(validsecondLabel);
        this.add(validsecondArea);
        this.add(expressionLabel);
        this.add(expressionTASc);
        this.add(returnvalueLabel);
        this.add(returnvalueArea);
        this.add(tasklistenerLabel);
        this.add(tasklistenerArea);

        this.setFocusable(true);
    }
}