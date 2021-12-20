package cn.boz.jb.plugin.idea.toolwindow;

import cn.boz.jb.plugin.idea.configurable.SpdEditorState;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.Menu;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EcasMenuToolWindow extends JComponent implements ClipboardOwner {
    private Tree tree;

    public EcasMenuToolWindow() {
        try {
            Connection connection = DBUtils.getConnection(SpdEditorState.getInstance());
            DefaultMutableTreeNode tn = new DefaultMutableTreeNode("菜单项");
            List<NodeData> apps = AppNode.initLoad(connection);
            List<DefaultMutableTreeNode> appTreeNodes = apps.stream().map(app -> new DefaultMutableTreeNode(app, true)).collect(Collectors.toList());
            appTreeNodes.stream().forEach(appTreeNode -> {
                tn.add(appTreeNode);
            });

            tree = new Tree(tn);
            tree.setDragEnabled(true);
            tree.addTreeSelectionListener(e -> {
                TreePath path = e.getPath();
                Object pathComponent = path.getLastPathComponent();
                if (!(pathComponent instanceof DefaultMutableTreeNode)) {
                    return;
                }
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) pathComponent;
                Object userObject = node.getUserObject();
                if (!(userObject instanceof NodeData)) {
                    return;
                }
                NodeData uo = (NodeData) userObject;
                if (!uo.isSubDataLoaded()) {
                    List<NodeData> subNodes = uo.loadSubNodes(connection);
                    uo.setSubDataLoaded(true);
                    List<DefaultMutableTreeNode> collect = subNodes.stream().map(sn -> new DefaultMutableTreeNode(sn, true)).collect(Collectors.toList());
                    for (DefaultMutableTreeNode o : collect) {
                        node.add(o);
                    }
                }
            });
            //用于树
            ToolbarDecorator decorator = ToolbarDecorator.createDecorator(tree);
            decorator.setEditAction(anActionButton -> {

            });

            decorator.setRemoveAction(anActionButton -> {
                TreePath selectionPath = tree.getSelectionPath();
                if (selectionPath == null) {
                    return;
                }
                DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                Object userObject = lastPathComponent.getUserObject();
                if (userObject instanceof String) {
                    return;
                }
                if (userObject instanceof AppNode) {
                    return;
                }
                if (userObject instanceof MenuNode) {
                    MenuNode menuNode = (MenuNode) userObject;
                    Map<String, Object> nodeData = menuNode.getNodeData();
                    String lvl = (String) nodeData.get("LVL");
                    if (lvl == null) {
                        return;
                    }
                    if (lvl.equals("1")) {
                        Messages.showWarningDialog("顶层不允许删除", "注意");
                        return;
                    }

                    Object menuid = nodeData.get("MENUID");
                    //执行删除操作
                    String sql = "select * from ECAS_MENU where menuid=" + menuid;
                    Messages.showYesNoDialog("execute?:"+sql,"不可回滚，确认执行？",Messages.getWarningIcon());


                }
            });
            decorator.setAddAction(anActionButton -> {
                //Can only operate tree model
                TreePath selectionPath = tree.getSelectionPath();
                if (selectionPath == null) {
                    return;
                }
                DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                Object userObject = lastPathComponent.getUserObject();
                if (!(userObject instanceof NodeData)) {
                    return;
                }
                AddMenuDialog addMenuDialog = new AddMenuDialog(ProjectManager.getInstance().getDefaultProject(), true, ((NodeData) userObject));
                boolean b = addMenuDialog.showAndGet();
                if (b) {
                    Map<String, Object> dataMap = addMenuDialog.getDataMap();

                    String sql = addMenuDialog.toSql();
                    int result = Messages.showDialog("SQL 如下，是否入库?\n" + sql, "确认", new String[]{"只复制Sql", "更新至DB(并复制sql)", "只入库", "取消"}, 1, Messages.getInformationIcon());
                    if (3 != result) {
                        if (1 == result) {
                            MenuNode menuNode = new MenuNode(dataMap);
                            model.insertNodeInto(new DefaultMutableTreeNode(menuNode), lastPathComponent, 0);
                            DBUtils instance = DBUtils.getInstance();
                            try (var conn = DBUtils.getConnection(SpdEditorState.getInstance())) {
                                instance.executeSql(conn, sql);
                            } catch (Exception e) {
                                Messages.showErrorDialog(e.getMessage(), "数据库失败");
                            }
                        }
                        if (2 != result) {
                            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                            StringSelection selection = new StringSelection(sql);
                            clipboard.setContents(selection, this);
                        }
                    }
                } else {
                    //用户取消
                }
                //根据节点进行操作
            });
            tree.setCellRenderer(new ColoredTreeCellRenderer() {
                @Override
                public void customizeCellRenderer(@NotNull JTree jTree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                    //里面提供一系列方法，供执行的时候根据状态进行调用
                    DefaultMutableTreeNode tn = (DefaultMutableTreeNode) value;
                    var uo = tn.getUserObject();
                    if (uo instanceof String) {
                        append((String) uo);
                    } else {
                        NodeData data = (NodeData) uo;
                        append(data.getTitle());
                    }
                }
            });
            this.setLayout(new BorderLayout());
            this.add(decorator.createPanel(), BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();

        }


    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }
}
