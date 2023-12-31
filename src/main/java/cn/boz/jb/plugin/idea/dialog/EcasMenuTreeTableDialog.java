package cn.boz.jb.plugin.idea.dialog;

import cn.boz.jb.plugin.idea.bean.EcasMenu;
import cn.boz.jb.plugin.idea.dialog.powertable.ActionPoweNode;
import cn.boz.jb.plugin.idea.dialog.treetable.MenuNode;
import cn.boz.jb.plugin.idea.dialog.treetable.NodeData;
import cn.boz.jb.plugin.idea.dialog.treetable.RootNode;
import cn.boz.jb.plugin.idea.utils.Constants;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTreeTable;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModel;
import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.intellij.util.ui.ColumnInfo;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NotNull;
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

public class EcasMenuTreeTableDialog extends DialogWrapper {

    JPanel centerCompoent;
    private JBScrollPane jbScrollPane;

    public void initialCompoent(  ) {
        ColumnInfo[] columns = new ColumnInfo[]{ new ColumnInfo<DefaultMutableTreeNode, String>("menuid") {
            @Override
            public String valueOf(DefaultMutableTreeNode o) {
                Object userObject = o.getUserObject();
                if (userObject instanceof MenuNode) {
                    EcasMenu ecasMenu = (EcasMenu) ((MenuNode) userObject).getNodeData();
                    return ecasMenu.getMenuid();
                }
                return "1";
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
        },new ColumnInfo<DefaultMutableTreeNode, String>("applid") {

            @Override
            public String valueOf(DefaultMutableTreeNode o) {
                Object userObject = o.getUserObject();
                if (userObject instanceof MenuNode) {
                    EcasMenu ecasMenu = (EcasMenu) ((MenuNode) userObject).getNodeData();
                    return ecasMenu.getApplid();
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
                            if (uo instanceof MenuNode) {
                                try {
                                    List<NodeData> nds = uo.loadSubNodes(project);
                                    for (NodeData nd : nds) {
                                        node.add(new DefaultMutableTreeNode(nd, true));
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
        jbTreeTable.getTree().setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree jTree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                var uo = node.getUserObject();
                if (uo instanceof String) {
                    append((String) uo);
                }else {
                    NodeData data = (NodeData)uo;
                    append(data.getTitle());
                    if (data.isLoading()) {
                        setIcon(SpdEditorIcons.LOADING_16_ICON);
                    } else {
                        if(uo instanceof RootNode){
                            setIcon(SpdEditorIcons.FOLDER_16_ICON);
                        }else if(uo instanceof MenuNode){
                            setIcon(SpdEditorIcons.MENU2_16_ICON);

                        }
                    }
                }
            }
        });

        JBTable table = jbTreeTable.getTable();
        final TableColumn c0 = table.getColumnModel().getColumn(0);
        c0.setMaxWidth(60);
        c0.setWidth(60);
        c0.setMinWidth(60);

        final TableColumn c1 = table.getColumnModel().getColumn(1);
        c1.setMaxWidth(60);
        c1.setWidth(60);
        c1.setMinWidth(60);

        final TableColumn c2 = table.getColumnModel().getColumn(2);
        //设置ID列宽度
        c2.setWidth(170);
        c2.setMinWidth(170);

        final TableColumn c3 = table.getColumnModel().getColumn(3);
        //设置ID列宽度
        c3.setMaxWidth(24);
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

    public EcasMenuTreeTableDialog(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
        this.project=project;
        initialCompoent();
        setTitle("Ecas 树表");
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
