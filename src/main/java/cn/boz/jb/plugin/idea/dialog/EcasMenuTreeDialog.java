package cn.boz.jb.plugin.idea.dialog;

import cn.boz.jb.plugin.idea.bean.EcasMenu;
import cn.boz.jb.plugin.idea.dialog.treenode.MenuNode;
import cn.boz.jb.plugin.idea.dialog.treenode.NodeData;
import cn.boz.jb.plugin.idea.dialog.treenode.RootNode;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTreeTable;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModel;
import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.intellij.util.ui.ColumnInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.sql.Connection;
import java.util.List;

public class EcasMenuTreeDialog extends DialogWrapper {

    private JBScrollPane jbScrollPane;

    public EcasMenuTreeDialog(Project project, String fileName) {
        super(true);
        setTitle("ecasMenu");
        ColumnInfo[] columns = new ColumnInfo[]{new ColumnInfo<DefaultMutableTreeNode, String>("applid") {
            @Override
            public String valueOf(DefaultMutableTreeNode o) {
                Object userObject = o.getUserObject();
                if (userObject instanceof MenuNode) {
                    EcasMenu ecasMenu = (EcasMenu) ((MenuNode) userObject).getNodeData();
                    return ecasMenu.getApplid();
                }
                return "";
            }

        }, new ColumnInfo<DefaultMutableTreeNode, String>("menuid") {
            @Override
            public String valueOf(DefaultMutableTreeNode o) {
                Object userObject = o.getUserObject();
                if (userObject instanceof MenuNode) {
                    EcasMenu ecasMenu = (EcasMenu) ((MenuNode) userObject).getNodeData();
                    return ecasMenu.getMenuid();
                }
                return "1";
            }
        }, new ColumnInfo<DefaultMutableTreeNode, String>("name") {
            @Override
            public String valueOf(DefaultMutableTreeNode o) {
                Object userObject = o.getUserObject();
                if (userObject instanceof MenuNode) {
                    EcasMenu ecasMenu = (EcasMenu) ((MenuNode) userObject).getNodeData();
                    return ecasMenu.getName();
                }
                return "";
            }
        }, new ColumnInfo<DefaultMutableTreeNode, String>("lvl") {
            @Override
            public String valueOf(DefaultMutableTreeNode o) {
                Object userObject = o.getUserObject();
                if (userObject instanceof MenuNode) {
                    EcasMenu ecasMenu = (EcasMenu) ((MenuNode) userObject).getNodeData();
                    return ecasMenu.getLvl();
                }
                return "";
            }
        }, new ColumnInfo<DefaultMutableTreeNode, String>("url") {
            @Override
            public String valueOf(DefaultMutableTreeNode o) {
                Object userObject = o.getUserObject();
                if (userObject instanceof MenuNode) {
                    EcasMenu ecasMenu = (EcasMenu) ((MenuNode) userObject).getNodeData();
                    return ecasMenu.getUrl();
                }
                return "";
            }
        }, new ColumnInfo<DefaultMutableTreeNode, String>("parent") {
            @Override
            public String valueOf(DefaultMutableTreeNode o) {
                Object userObject = o.getUserObject();
                if (userObject instanceof MenuNode) {
                    EcasMenu ecasMenu = (EcasMenu) ((MenuNode) userObject).getNodeData();
                    return ecasMenu.getParent();
                }
                return "";
            }
        }, new ColumnInfo<DefaultMutableTreeNode, String>("img") {
            @Override
            public String valueOf(DefaultMutableTreeNode o) {
                Object userObject = o.getUserObject();
                if (userObject instanceof MenuNode) {
                    EcasMenu ecasMenu = (EcasMenu) ((MenuNode) userObject).getNodeData();
                    return ecasMenu.getImg();
                }
                return "";
            }
        }, new ColumnInfo<DefaultMutableTreeNode, String>("ischild") {
            @Override
            public String valueOf(DefaultMutableTreeNode o) {
                Object userObject = o.getUserObject();
                if (userObject instanceof MenuNode) {
                    EcasMenu ecasMenu = (EcasMenu) ((MenuNode) userObject).getNodeData();
                    return ecasMenu.getIschild();
                }
                return "";
            }
        }, new ColumnInfo<DefaultMutableTreeNode, String>("groupid") {
            @Override
            public String valueOf(DefaultMutableTreeNode o) {
                Object userObject = o.getUserObject();
                if (userObject instanceof MenuNode) {
                    EcasMenu ecasMenu = (EcasMenu) ((MenuNode) userObject).getNodeData();
                    return ecasMenu.getGroupid();
                }
                return "";
            }
        }
        };

        RootNode rootNode = new RootNode(fileName);
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
            TreePath path = e.getPath();
            Object pathComponent = path.getLastPathComponent();
            if (pathComponent instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) pathComponent;
                Object userObject = node.getUserObject();
                if (userObject instanceof NodeData) {
                    NodeData uo = (NodeData) userObject;
                    boolean subDataLoaded = uo.isSubDataLoaded();
                    if (subDataLoaded == false) {
                        uo.setSubDataLoaded(true);
                        if (uo instanceof MenuNode) {
                            ProgressManager.getInstance().runProcessWithProgressAsynchronously(new Task.Backgroundable(project, "loading...", true) {
                                @Override
                                public void run(@NotNull ProgressIndicator progressIndicator) {
                                    try {
                                        Connection connection = DBUtils.getConnection(project);
                                        List<NodeData> nds = uo.loadSubNodes(connection);
                                        for (NodeData nd : nds) {
                                            node.add(new DefaultMutableTreeNode(nd, true));
                                        }
                                    } catch (Exception ex) {
                                        DBUtils.dbExceptionProcessor(ex, project);
                                    }
                                }
                            }, ProgressIndicatorProvider.getGlobalProgressIndicator());


                        }

                    }
                }

            }
        });
        jbTreeTable.getTree().setCellRenderer(new TreeCellRenderer() {

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                //默认都认为不是leaf节点，并且在userData里面设置信息
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                var uo = node.getUserObject();
                if (uo instanceof String) {
                    return new JLabel((String) uo);
                } else {
                    NodeData data = (NodeData) uo;
                    return new JLabel(data.getTitle());
                }
            }
        });

        this.jbScrollPane = new JBScrollPane(jbTreeTable);

        jbScrollPane.setPreferredSize(new Dimension(900, 400));
        init();

    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return this.jbScrollPane;
    }
}
