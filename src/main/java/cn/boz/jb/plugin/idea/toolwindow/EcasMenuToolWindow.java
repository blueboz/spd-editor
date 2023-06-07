package cn.boz.jb.plugin.idea.toolwindow;

import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.treeStructure.Tree;
import icons.SpdEditorIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EcasMenuToolWindow extends JComponent implements ClipboardOwner {
    private Tree tree;

    public EcasMenuToolWindow(Project project) {
        try {
            DefaultMutableTreeNode tn = new DefaultMutableTreeNode("菜单项");
            List<NodeData> apps = AppNode.initLoad(project);
            List<DefaultMutableTreeNode> appTreeNodes = apps.stream().map(app -> new DefaultMutableTreeNode(app, true)).collect(Collectors.toList());
            appTreeNodes.stream().forEach(appTreeNode -> {
                tn.add(appTreeNode);
            });


            tree = new Tree(tn);

            tree.setDragEnabled(true);

            tree.addTreeSelectionListener(e -> {
                TreePath path = e.getPath();
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                loadTreeNode(selectedNode,project);

            });
            //用于树
            ToolbarDecorator decorator = ToolbarDecorator.createDecorator(tree);

            decorator.setEditAction(anActionButton -> {
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
                    BigDecimal lvl = null;
                    Object lvlobj = nodeData.get("LVL");
                    if (lvlobj instanceof String) {
                        lvl = new BigDecimal((String) lvlobj);
                    } else {
                        lvl = (BigDecimal) lvlobj;
                    }
                    if (lvl == null) {
                        return;
                    }
                    EditMenuDialog addMenuDialog = new EditMenuDialog(ProjectManager.getInstance().getDefaultProject(), true, ((NodeData) userObject));
                    boolean b = addMenuDialog.showAndGet();
                    if (b) {
                        Map<String, Object> dataMap = addMenuDialog.getDataMap();
                        String sql = addMenuDialog.toSql();
                        int result = Messages.showDialog("SQL 如下，是否入库?\n" + sql, "确认", new String[]{"只复制Sql", "更新至DB(并复制sql)", "只入库", "取消"}, 1, Messages.getInformationIcon());
                        if (3 != result) {
                            if (1 == result) {
                                try{
                                    DBUtils.getInstance().executeSql(project, sql);
                                }catch (Exception e){
                                    DBUtils.dbExceptionProcessor(e,project);
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

                }
            });

            decorator.addExtraAction(new AnActionButton() {
                {
                    getTemplatePresentation().setIcon(AllIcons.Actions.Refresh);
                }

                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                    TreePath selectionPath = tree.getSelectionPath();
                    if (selectionPath == null) {
                        return;
                    }
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
                    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                    Object userObject = selectedNode.getUserObject();
                    if (userObject instanceof String) {
                        return;
                    }
                    if (userObject instanceof AppNode) {
                        AppNode appNode= (AppNode) userObject;

                        if(appNode.isSubDataLoaded()){
                            appNode.setSubDataLoaded(false);

                            while(selectedNode.getChildCount()>0){
                                DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) selectedNode.getFirstChild();
                                selectedNode.remove(firstChild);
                            }
                            model.reload(selectedNode);
                        }
                        return;
                    }
                }
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
                    Object lvlv = nodeData.get("LVL");
                    BigDecimal lvl = null;
                    if (lvlv instanceof String) {
                        lvl = new BigDecimal((String) lvlv);
                    } else if (lvlv instanceof BigDecimal) {
                        lvl = (BigDecimal) lvlv;
                    }

                    if (lvl == null) {
                        return;
                    }
                    if (lvl.intValue() < 1) {
                        Messages.showWarningDialog("顶层不允许删除", "注意");
                        return;
                    }

                    BigDecimal menuid = (BigDecimal) nodeData.get("MENUID");
                    //执行删除操作
                    String sql = "delete from ECAS_MENU where menuid=" + menuid.intValue();
                    int result = Messages.showDialog("execute?\n" + sql, "不可回滚，确认执行？", new String[]{"拷贝SQL", "执行删除", "取消"}, 1, Messages.getWarningIcon());
                    if (1 == result) {
                        DBUtils instance = DBUtils.getInstance();
                        try  {
                            instance.executeSql(project, sql);
                            model.removeNodeFromParent(lastPathComponent);
                        } catch (Exception e) {
                            DBUtils.dbExceptionProcessor(e,project);
                        }

                    }
                    if (0 == result) {
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        StringSelection selection = new StringSelection(sql);
                        clipboard.setContents(selection, this);
                    }
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
                AddMenuDialog addMenuDialog = new AddMenuDialog(project, true, ((NodeData) userObject));
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
                            try {
                                instance.executeSql(project, sql);
                            } catch (Exception e) {
                                DBUtils.dbExceptionProcessor(e,project);
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

                    if(uo instanceof String){
                        setIcon(SpdEditorIcons.ROOT_16_ICON);
                    }else if(uo instanceof AppNode){
                        setIcon(SpdEditorIcons.APP_16_ICON);
                    }else if(uo instanceof MenuNode){
                        setIcon(SpdEditorIcons.MENU2_16_ICON);
                    }

                }
            });
            this.setLayout(new BorderLayout());
            this.add(decorator.createPanel(), BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();

        }


    }
    public void loadTreeNode(DefaultMutableTreeNode selectedNode,Project project) {
//        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object userObject = selectedNode.getUserObject();
        if (!(userObject instanceof NodeData)) {
            return;
        }
        NodeData uo = (NodeData) userObject;
        if (!uo.isSubDataLoaded()) {
            List<NodeData> subNodes = uo.loadSubNodes(project);
            uo.setSubDataLoaded(true);
            List<DefaultMutableTreeNode> collect = subNodes
                    .stream()
                    .map(sn -> new DefaultMutableTreeNode(sn, true))
                    .collect(Collectors.toList());
            for (DefaultMutableTreeNode o : collect) {
                selectedNode.add(o);
            }
        }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }
}
