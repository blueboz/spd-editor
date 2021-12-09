package cn.boz.jb.plugin.idea.toolwindow;

import cn.boz.jb.plugin.idea.dialog.MyLayoutManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

/**
 * 新增菜单对话框
 */
public class AddMenuDialog extends DialogWrapper {

    protected AddMenuDialog(@Nullable Project project, boolean canBeParent,NodeData parentNodeData) {
        super(project, canBeParent);
        this.setParentMap(parentNodeData.getNodeData());
        init();
        setTitle("添加菜单");
    }

    private JLabel applid;
    private JTextField applidt;
    private JLabel menuid;
    private JTextField menuidt;
    private JLabel name;
    private JTextField namet;
    private JLabel lvl;
    private JTextField lvlt;
    private JLabel url;
    private JTextField urlt;
    private JLabel parent;
    private JTextField parentt;
    private JLabel img;
    private JTextField imgt;
    private JLabel ischild;
    private JTextField ischildt;
    private JLabel groupid;
    private JTextField groupidt;
    private Map<String, Object> parentMap;

    public void setParentMap(Map<String, Object> parentMap) {
        this.parentMap = parentMap;
    }

    private MyLayoutManager myLayoutManager;

    private JPanel jPanel;

    /**
     * "APPLID": 999,
     * "MENUID": 661,
     * "NAME": "统一额度使用情况",
     * "LVL": 1,
     * "URL": "riskctl/MarginUsagesStatus.html",
     * "PARENT": 5,
     * "IMG": "../ui/eraui/images/title_icon.gif",
     * "ISCHILD": 1,
     * "GROUPID": 0
     *
     * @return
     */
    @Override
    protected @Nullable JComponent createCenterPanel() {
        jPanel = new JPanel();
        myLayoutManager = new MyLayoutManager();
        jPanel.setLayout(myLayoutManager);
        applid = new JLabel("APPLID");
        applidt = new JTextField(getParentMapKey("APPLID"));

        menuid = new JLabel("MENUID");
        menuidt = new JTextField();
        name = new JLabel("NAME");
        namet = new JTextField();
        this.lvl = new JLabel("LVL");
        lvlt = new JTextField(getAndIncrease("LVL"));
        url = new JLabel("URL");
        urlt = new JTextField();
        parent = new JLabel("PARENT");
        parentt = new JTextField(getParentMapKey("MENUID"));
        img = new JLabel("IMG");
        imgt = new JTextField("../ui/eraui/images/title_icon.gif");
        ischild = new JLabel("ISCHILD");
        ischildt = new JTextField("1");
        groupid = new JLabel("GROUPID");
        groupidt = new JTextField("0");
        jPanel.add(applid);
        jPanel.add(applidt);
        jPanel.add(menuid);
        jPanel.add(menuidt);
        jPanel.add(name);
        jPanel.add(namet);
        jPanel.add(this.lvl);
        jPanel.add(lvlt);
        jPanel.add(url);
        jPanel.add(urlt);
        jPanel.add(parent);
        jPanel.add(parentt);
        jPanel.add(img);
        jPanel.add(imgt);
        jPanel.add(ischild);
        jPanel.add(ischildt);
        jPanel.add(groupid);
        jPanel.add(groupidt);
        jPanel.setPreferredSize(new Dimension(400, 800));
        return jPanel;
    }

    public String getParentMapKey(String key) {
        if (parentMap == null) {
            return "";
        }
        Object o = parentMap.get(key);
        if (o == null) {
            return "";
        }
        if (o instanceof String) {
            return (String) o;
        }
        return String.valueOf(o);
    }

    public String getAndIncrease(String key) {
        String val = getParentMapKey(key);
        if("".equals(val)){
            return "1";
        }
        return String.valueOf(Integer.parseInt(val)+1);
    }

    public Map getDataMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("APPLID", applidt.getText());
        map.put("MENUID", menuidt.getText());
        map.put("NAME", namet.getText());
        map.put("LVL", lvlt.getText());
        map.put("URL", urlt.getText());
        map.put("PARENT", parentt.getText());
        map.put("IMG", imgt.getText());
        map.put("ISCHILD", ischildt.getText());
        map.put("GROUPID", groupidt.getText());
        return map;
    }

}
