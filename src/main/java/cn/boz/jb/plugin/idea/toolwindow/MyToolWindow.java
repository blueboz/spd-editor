package cn.boz.jb.plugin.idea.toolwindow;

import cn.boz.jb.plugin.idea.configurable.SpdEditorState;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.openapi.project.ProjectManager;
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
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyToolWindow extends JComponent {
    private Tree tree;

    public MyToolWindow() {

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
                } else {
                    MenuNode menuNode = MenuNode.buildEmptyNode();
                    node.add(new DefaultMutableTreeNode(menuNode));
                }
            });
            //用于树
            ToolbarDecorator decorator = ToolbarDecorator.createDecorator(tree);
            decorator.setAddAction(anActionButton -> {
                //Can only operate tree model
                TreePath selectionPath = tree.getSelectionPath();
                if (selectionPath == null) {
                    return;
                }
                DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                Object userObject = lastPathComponent.getUserObject();
                AddMenuDialog addMenuDialog = new AddMenuDialog(ProjectManager.getInstance().getDefaultProject(), true,((NodeData)userObject));
                boolean b = addMenuDialog.showAndGet();
                if(b){
                    Map dataMap = addMenuDialog.getDataMap();
                    MenuNode menuNode = new MenuNode(dataMap);
                    model.insertNodeInto(new DefaultMutableTreeNode(menuNode), lastPathComponent, 0);
                }else{
                    //用户取消
                }
                //根据节点进行操作
            });
            decorator.setRemoveAction(anActionButton -> {
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
}
