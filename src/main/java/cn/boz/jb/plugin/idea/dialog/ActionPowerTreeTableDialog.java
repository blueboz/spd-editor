package cn.boz.jb.plugin.idea.dialog;

import cn.boz.jb.plugin.idea.bean.EcasActionPower;
import cn.boz.jb.plugin.idea.dialog.powertable.ActionPoweNode;
import cn.boz.jb.plugin.idea.dialog.powertable.NodeData;
import cn.boz.jb.plugin.idea.dialog.powertable.RootNode;
import cn.boz.jb.plugin.idea.utils.Constants;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTreeTable;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModel;
import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.intellij.util.ui.ColumnInfo;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.sql.Connection;
import java.util.List;
import java.util.concurrent.Executors;

public class ActionPowerTreeTableDialog extends DialogWrapper {

    JPanel centerCompoent;
    private JBScrollPane jbScrollPane;

    public void initialCompoent(  ) {
        ColumnInfo[] columns = new ColumnInfo[]{ new ColumnInfo<DefaultMutableTreeNode, String>("Powerbit") {
            @Override
            public String valueOf(DefaultMutableTreeNode o) {
                Object userObject = o.getUserObject();
                if (userObject instanceof ActionPoweNode) {
                    EcasActionPower EcasActionPower = (EcasActionPower) ((ActionPoweNode) userObject).getNodeData();
                    return EcasActionPower.getPowerbit()+"";
                }
                return "";
            }
        }, new ColumnInfo<DefaultMutableTreeNode, String>("path") {
            @Override
            public String valueOf(DefaultMutableTreeNode o) {
                Object userObject = o.getUserObject();
                if (userObject instanceof ActionPoweNode) {
                    EcasActionPower EcasActionPower = (EcasActionPower) ((ActionPoweNode) userObject).getNodeData();
                    return EcasActionPower.getPath();
                }
                return "";
            }
        }, new ColumnInfo<DefaultMutableTreeNode, String>("enable") {
            @Override
            public String valueOf(DefaultMutableTreeNode o) {
                Object userObject = o.getUserObject();
                if (userObject instanceof ActionPoweNode) {
                    EcasActionPower EcasActionPower = (EcasActionPower) ((ActionPoweNode) userObject).getNodeData();
                    return EcasActionPower.getEnabled()+"";
                }
                return "";
            }
        }, new ColumnInfo<DefaultMutableTreeNode, String>("weight") {
            @Override
            public String valueOf(DefaultMutableTreeNode o) {
                Object userObject = o.getUserObject();
                if (userObject instanceof ActionPoweNode) {
                    EcasActionPower EcasActionPower = (EcasActionPower) ((ActionPoweNode) userObject).getNodeData();
                    return EcasActionPower.getWeight()+"";
                }
                return "";
            }
        }, new ColumnInfo<DefaultMutableTreeNode, String>("menuId") {
            @Override
            public String valueOf(DefaultMutableTreeNode o) {
                Object userObject = o.getUserObject();
                if (userObject instanceof ActionPoweNode) {
                    EcasActionPower EcasActionPower = (EcasActionPower) ((ActionPoweNode) userObject).getNodeData();
                    return EcasActionPower.getMenuId()+"";
                }
                return "";
            }
        }
        };

        RootNode rootNode = new RootNode();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootNode, true);
        TreeTableModel treeTableModel = new ListTreeTableModel(root, columns);
        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
            List<DefaultMutableTreeNode> rootNodeChilds = rootNode.initLoad(project);
            for (DefaultMutableTreeNode rootNodeChild : rootNodeChilds) {
                root.add(rootNodeChild);
            }
        }, "..", true, project);


        JBTreeTable jbTreeTable = new JBTreeTable(treeTableModel);


        jbTreeTable.getTree().addTreeSelectionListener(e -> {

            Runnable r = (() -> {
                TreePath path = e.getPath();
                Object pathComponent = path.getLastPathComponent();
                if (pathComponent instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) pathComponent;
                    Object userObject = node.getUserObject();
                    if (userObject instanceof NodeData) {
                        NodeData uo = (NodeData) userObject;
                        uo.setLoading(true);
                        jbTreeTable.getTree().repaint();
                        boolean subDataLoaded = uo.isSubDataLoaded();
                        if (subDataLoaded == false) {
                            uo.setSubDataLoaded(true);
                            if (uo instanceof RootNode) {
                                try {
                                    Connection connection = DBUtils.getConnection(project);
                                    List<NodeData> nds = uo.loadSubNodes(connection);
                                    for (NodeData nd : nds) {
                                        node.add(new DefaultMutableTreeNode(nd, false));
                                    }
                                } catch (Exception ex) {
                                    DBUtils.dbExceptionProcessor(ex, project);
                                }

                            }
                        }
                        uo.setLoading(false);
                        jbTreeTable.getTree().repaint();

                    }

                }
            });
            Executors.newSingleThreadExecutor().execute(r);
        });
        jbTreeTable.getTree().setCellRenderer(new DefaultTreeCellRenderer() {

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                //默认都认为不是leaf节点，并且在userData里面设置信息
                super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                var uo = node.getUserObject();
                if (uo instanceof String) {
                    setText((String) uo);
                } else {
                    NodeData data = (NodeData) uo;
                    setText(data.getTitle());
                    if (data.isLoading()) {
                        setIcon(SpdEditorIcons.LOADING_16_ICON);
                    } else {
                        if (selected) {
                            setIcon(SpdEditorIcons.MENUE_16_ICON);
                        } else {
                            setIcon(SpdEditorIcons.MENUE_16_ICON_D);
                        }
                    }
                }
                return this;

            }
        });
        JBTable table = jbTreeTable.getTable();
        final TableColumn c0 = table.getColumnModel().getColumn(0);
        c0.setMaxWidth(64);
        c0.setWidth(64);
        c0.setMinWidth(64);

        final TableColumn c1 = table.getColumnModel().getColumn(1);
        c1.setMaxWidth(64);
        c1.setWidth(64);
        c1.setMinWidth(64);

        final TableColumn c2 = table.getColumnModel().getColumn(2);
        //设置ID列宽度
        c2.setWidth(170);
        c2.setMinWidth(170);

        final TableColumn c3 = table.getColumnModel().getColumn(3);
        //设置ID列宽度
        c3.setWidth(24);
        c3.setMinWidth(24);

        final TableColumn c4 = table.getColumnModel().getColumn(4);
        //设置ID列宽度
        c4.setWidth(250);
        c4.setMinWidth(250);

        this.jbScrollPane = new JBScrollPane(jbTreeTable);

        jbScrollPane.setPreferredSize(new Dimension(1000, 500));

        ActionManager instance = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) instance.getAction(Constants.ACTION_GROUP_OPEN_IN_TOOLWINDOW);
        ActionToolbar spd_tb = instance.createActionToolbar("spd tb", actionGroup, true);

        JComponent gotoactionScript = spd_tb.getComponent();


        centerCompoent = new JPanel();
        centerCompoent.setLayout(new BorderLayout());
        centerCompoent.add(jbScrollPane, BorderLayout.CENTER);
        centerCompoent.add(gotoactionScript, BorderLayout.SOUTH);

        centerCompoent.setFocusable(true);

    }
    private Project project;

    public ActionPowerTreeTableDialog(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
        this.project=project;
        initialCompoent();
        setTitle("ActionPower 树表");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        if(centerCompoent==null){
            initialCompoent();
        }
        return centerCompoent;
    }

    public JBScrollPane derive() {
        return this.jbScrollPane;

    }

}
