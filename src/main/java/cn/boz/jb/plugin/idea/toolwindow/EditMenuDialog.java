package cn.boz.jb.plugin.idea.toolwindow;

import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;
import cn.boz.jb.plugin.idea.configurable.SpdEditorNormState;
import cn.boz.jb.plugin.idea.dialog.MyLayoutManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlTag;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 新增菜单对话框
 */
public class EditMenuDialog extends DialogWrapper {


    @Override
    public void validate() {
        StringBuilder msg = new StringBuilder();
        String applid = applidt.getText();
        if (StringUtils.isBlank(applid)) {
            applidt.setBackground(Color.ORANGE);
            msg.append("applid不准为空");
            msg.append("\n");

        }


        String name = namet.getText();
        if (StringUtils.isBlank(name)) {
            namet.setBackground(Color.ORANGE);
            msg.append("name不准为空");
            msg.append("\n");

        }

        String lvl = lvlt.getText();
        if (StringUtils.isBlank(lvl)) {
            lvlt.setBackground(Color.ORANGE);
            msg.append("lvl不准为空");
            msg.append("\n");

        }

        String url = urlt.getText();
        if (StringUtils.isBlank(url)) {
            urlt.setBackground(Color.ORANGE);
            msg.append("url不准为空");
            msg.append("\n");

        }

        String parent = parentt.getText();
        if (StringUtils.isBlank(parent)) {
            parentt.setBackground(Color.ORANGE);
            msg.append("parent不准为空");
            msg.append("\n");

        }

        String img = imgt.getText();
        if (StringUtils.isBlank(img)) {
            imgt.setBackground(Color.ORANGE);
            msg.append("img不准为空");
            msg.append("\n");

        }

        String ischild = ischildt.getText();
        if (StringUtils.isBlank(ischild)) {
            ischildt.setBackground(Color.ORANGE);
            msg.append("ischild不准为空");
            msg.append("\n");

        }

        String groupid = groupidt.getText();
        if (StringUtils.isBlank(groupid)) {
            groupidt.setBackground(Color.ORANGE);
            msg.append("groupid不准为空");
            msg.append("\n");
        }
        if (!StringUtils.isBlank(msg.toString())) {
            throw new RuntimeException(msg.toString());
        }
    }

    @Override
    protected void doOKAction() {
        try {
            validate();
            super.doOKAction();
        } catch (Exception e) {
            Messages.showWarningDialog(e.getMessage(), "错误");
        }
    }

    protected EditMenuDialog(@Nullable Project project, boolean canBeParent, NodeData parentNodeData) {
        super(project, canBeParent);
        this.setParentMap(parentNodeData.getNodeData());
        init();
        setTitle("编辑菜单");
    }

    @Override
    protected boolean postponeValidation() {
        return super.postponeValidation();
    }

    private JLabel applid;
    private JTextField applidt;
    private JLabel menuid;
    private JLabel menuidlabel;
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


    private JPanel urltcontainer;

    private MenuIdDialog menuIdDialog;

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
        applidt = new JTextField(getMapVal("APPLID"));

        menuid = new JLabel("MENUID");
        menuidlabel = new JLabel(getMapVal("MENUID"));


        name = new JLabel("NAME");
        namet = new JTextField(getMapVal("NAME"));
        this.lvl = new JLabel("LVL");
        lvlt = new JTextField(getMapVal("LVL"));
        url = new JLabel("URL");

        urlt = new JTextField(getMapVal("URL"));
        urltcontainer = new JPanel();
        urltcontainer.setLayout(new BorderLayout());
        urltcontainer.add(urlt);
        JButton urlBtn = new JButton();

        urlBtn.setIcon(SpdEditorIcons.MENU_16_ICON);
        urlBtn.addMouseListener(new MouseAdapter() {
            //搜索所有的html

            @Override
            public void mouseClicked(MouseEvent e) {
                Collection<VirtualFile> html = FilenameIndex.getAllFilesByExt(ProjectManager.getInstance().getDefaultProject(), "html");
                List<VirtualFile> collect = html.stream().collect(Collectors.toList());
                ListPopup listPopup = JBPopupFactory.getInstance().createListPopup(new BaseListPopupStep<VirtualFile>("select file", collect) {
                    @Override
                    public String getIndexedString(VirtualFile value) {
                        return value.getPath();
                    }

                    @Override
                    public @Nullable PopupStep<?> onChosen(VirtualFile selectedValue, boolean finalChoice) {
                        if (finalChoice) {
                            return doFinalStep(() -> doRun(selectedValue));
                        }
                        return PopupStep.FINAL_CHOICE;
                    }

                    private void doRun(VirtualFile selectedValue) {
                        SpdEditorNormState instance = SpdEditorNormState.getInstance();
                        if (StringUtils.isBlank(instance.webroot)) {
                            urlt.setText(String.valueOf(selectedValue.getPath()));
                        } else {
                            urlt.setText(String.valueOf(selectedValue.getPath()).replace(instance.webroot, ""));
                        }
                        urlt.setBackground(null);
                        PsiFile psiFile = PsiManager.getInstance(ProjectManager.getInstance().getDefaultProject()).findFile(selectedValue);

                        XmlDocument document = PsiTreeUtil.getChildOfType(psiFile, XmlDocument.class);
                        XmlTag rootTag = document.getRootTag();
                        XmlTag head = rootTag.findFirstSubTag("head");

                        if (head != null) {
                            XmlTag title = head.findFirstSubTag("title");
                            if (title != null) {
                                String text = title.getValue().getText();
                                if (text != null && !text.trim().equals("")) {
                                } else {
                                    text = title.getText();
                                    text = text.replace("<title>", "");
                                    text = text.replace("</title>", "");
                                }
                                namet.setText(text);
                                namet.setBackground(Color.WHITE);
                            }
                        }

                    }

                    @Override
                    public boolean isSpeedSearchEnabled() {
                        return true;
                    }
                });
                listPopup.showInCenterOf(EditMenuDialog.this.getContentPanel());


            }
        });


        urltcontainer.add(urlBtn, BorderLayout.EAST);

        parent = new JLabel("PARENT");
        parentt = new JTextField(getMapVal("PARENT"));
        img = new JLabel("IMG");
        imgt = new JTextField(getMapVal("IMG"));
        ischild = new JLabel("ISCHILD");
        ischildt = new JTextField(getMapVal("ISCHILD"));
        groupid = new JLabel("GROUPID");
        groupidt = new JTextField(getMapVal("GROUPID"));
        this.jPanel.add(applid);
        this.jPanel.add(applidt);
        this.jPanel.add(menuid);
        this.jPanel.add(menuidlabel);
        this.jPanel.add(name);
        this.jPanel.add(namet);
        this.jPanel.add(lvl);
        this.jPanel.add(lvlt);
        this.jPanel.add(url);
        this.jPanel.add(urltcontainer);
        this.jPanel.add(parent);
        this.jPanel.add(parentt);
        this.jPanel.add(img);
        this.jPanel.add(imgt);
        this.jPanel.add(ischild);
        this.jPanel.add(ischildt);
        this.jPanel.add(groupid);
        this.jPanel.add(groupidt);
        this.jPanel.setPreferredSize(new Dimension(400, 800));
        return this.jPanel;
    }


    public String getMapVal(String key) {
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

    public Map<String, Object> getDataMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("APPLID", applidt.getText());
        map.put("MENUID", menuidlabel.getText());
        map.put("NAME", namet.getText());
        map.put("LVL", lvlt.getText());
        map.put("URL", urlt.getText());
        map.put("PARENT", parentt.getText());
        map.put("IMG", imgt.getText());
        map.put("ISCHILD", ischildt.getText());
        map.put("GROUPID", groupidt.getText());
        return map;
    }


    /**
     * 转成SQL 准备入库
     *
     * @return
     */
    public String toSql() {
        String sql = "update ecas_menu set APPLID=" + applidt.getText() + ",  NAME='"
                + namet.getText()
                + "', LVL=" + lvlt.getText()
                + ", URL='" + urlt.getText()
                + "', PARENT=" + parentt.getText()
                + ", IMG='" + urlt.getText()
                + "', ISCHILD=" + ischildt.getText()
                + ", GROUPID=" + groupidt.getText() + " where MENUID=" + menuidlabel.getText();

        return sql;
    }

}
