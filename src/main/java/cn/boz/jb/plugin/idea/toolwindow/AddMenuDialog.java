package cn.boz.jb.plugin.idea.toolwindow;

import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;
import cn.boz.jb.plugin.idea.configurable.SpdEditorNormState;
import cn.boz.jb.plugin.idea.layoutmanager.MyLayoutManager;
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

import javax.swing.*;
import java.awt.*;
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
public class AddMenuDialog extends DialogWrapper {

    private Project project;

    @Override
    public void validate() {
        StringBuilder msg = new StringBuilder();
        String applid = applidt.getText();
        if (StringUtils.isBlank(applid)) {
            applidt.setBackground(Color.ORANGE);
            msg.append("applid不准为空");
            msg.append("\n");

        }

        String menuid = menuidt.getText();
        if (StringUtils.isBlank(menuid)) {
            menuidt.setBackground(Color.ORANGE);
            msg.append("menuid不准为空");
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

    protected AddMenuDialog(@Nullable Project project, boolean canBeParent, NodeData parentNodeData) {
        super(project, canBeParent);
        this.setParentMap(parentNodeData.getNodeData());
        init();
        setTitle("添加菜单");
        this.project=project;
    }

    @Override
    protected boolean postponeValidation() {
        return super.postponeValidation();
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


    private JPanel menuidcontainer;
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
        applidt = new JTextField(getParentMapKey("APPLID"));

        menuid = new JLabel("MENUID");

        menuidcontainer = new JPanel();

        menuidt = new JTextField();
        JButton menuidtBtn = new JButton();

        menuidtBtn.setIcon(SpdEditorIcons.MENU_16_ICON);
        menuidtBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                menuIdDialog = new MenuIdDialog(project,Integer.parseInt(getParentMapKey("APPLID"))) {
                    @Override
                    public void onChoosen(Integer chooseValue) {
                        menuidt.setText(String.valueOf(chooseValue));
                        menuidt.setBackground(Color.white);
                    }
                };
                JBPopup popup = JBPopupFactory.getInstance()
                        .createComponentPopupBuilder(menuIdDialog, null)
                        .setRequestFocus(true)
                        .setFocusable(true)
                        .createPopup();
                popup.showInCenterOf(AddMenuDialog.this.getContentPanel());

            }
        });
        menuidcontainer.setLayout(new BorderLayout());
        menuidcontainer.add(menuidt, BorderLayout.CENTER);
        menuidcontainer.add(menuidtBtn, BorderLayout.EAST);


        name = new JLabel("NAME");
        namet = new JTextField();
        this.lvl = new JLabel("LVL");
        lvlt = new JTextField(getAndIncrease("LVL"));
        url = new JLabel("URL");

        urlt = new JTextField();
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
                            urlt.setText(String.valueOf(selectedValue.getPath()).replace(instance.webroot.replace("\\", "/"), ""));
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
                listPopup.showInCenterOf(AddMenuDialog.this.getContentPanel());


            }
        });


        urltcontainer.add(urlBtn, BorderLayout.EAST);

        parent = new JLabel("PARENT");
        parentt = new JTextField(getParentMapKey("MENUID"));
        img = new JLabel("IMG");
        imgt = new JTextField("../ui/eraui/images/title_icon.gif");
        ischild = new JLabel("ISCHILD");
        ischildt = new JTextField("1");
        groupid = new JLabel("GROUPID");
        groupidt = new JTextField("0");
        this.jPanel.add(applid);
        this.jPanel.add(applidt);
        this.jPanel.add(menuid);
        this.jPanel.add(menuidcontainer);
        this.jPanel.add(name);
        this.jPanel.add(namet);
        this.jPanel.add(this.lvl);
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
        if ("".equals(val)) {
            return "1";
        }
        return String.valueOf(Integer.parseInt(val) + 1);
    }

    public Map<String, Object> getDataMap() {
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


    /**
     * 转成SQL 准备入库
     *
     * @return
     */
    public String toSql() {
        String sql = "INSERT INTO ECAS_MENU (APPLID, MENUID, NAME, LVL, URL, PARENT, IMG, ISCHILD, GROUPID) " +
                "VALUES (" + applidt.getText() + ", "
                + menuidt.getText() + ", '"
                + namet.getText() + "', "
                + lvlt.getText() + ", '"
                + urlt.getText() + "', "
                + parentt.getText() + ", '"
                + imgt.getText() + "', "
                + ischildt.getText() + ", "
                + groupidt.getText() + ")";
        return sql;
    }

}
