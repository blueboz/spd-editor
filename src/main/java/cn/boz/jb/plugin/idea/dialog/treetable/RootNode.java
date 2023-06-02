package cn.boz.jb.plugin.idea.dialog.treetable;


import cn.boz.jb.plugin.idea.bean.EcasMenu;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.openapi.project.Project;

import javax.swing.tree.DefaultMutableTreeNode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RootNode extends NodeData {

    public RootNode( ){
    }

    public List<DefaultMutableTreeNode> initLoad(Project project) {
        List<DefaultMutableTreeNode> lis=new ArrayList<>();
        Connection connection = null;
        try {
            connection = DBUtils.getConnection(project);
            List<NodeData> nds =loadSubNodes(connection);
            for (NodeData nd : nds) {
                lis.add(new DefaultMutableTreeNode(nd,true));
            }
        }catch (Exception ex){
            DBUtils.dbExceptionProcessor(ex,project);
        }
        return lis;
    }

    @Override
    public String getTitle() {
        return "根";
    }

    @Override
    public List<NodeData> loadSubNodes(Connection connection) {
        List<EcasMenu> ecasMenus = null;
        try {
            ecasMenus = DBUtils.getInstance().queryRootMenus("999", connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ecasMenus.stream().map(item->{
            MenuNode menuNode = new MenuNode();
            menuNode.setNodeData(item);
            menuNode.setSubDataLoaded(false);
            return menuNode;
        }).collect(Collectors.toList());
    }
}
