package cn.boz.jb.plugin.idea.dialog.powertable;


import cn.boz.jb.plugin.idea.bean.EcasActionPower;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.openapi.project.Project;

import javax.swing.tree.DefaultMutableTreeNode;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RootNode extends NodeData {

    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    public RootNode( ){
    }


    public List<DefaultMutableTreeNode> initLoad(Project project) {
        List<DefaultMutableTreeNode> lis=new ArrayList<>();
        try {
            List<NodeData> nds =loadRoot(project);
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
        return title;
    }

    public List<NodeData> loadRoot(Project project){
        List<String> ecasMenus = null;
        try {
            ecasMenus = DBUtils.getInstance().queryRootActionPower("999", null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ecasMenus.stream().map(item->{
            RootNode menuNode = new RootNode();
            menuNode.setNodeData(item);
            menuNode.setTitle(item);
            menuNode.setSubDataLoaded(false);
            return menuNode;
        }).collect(Collectors.toList());
    }
    @Override

    public List<NodeData> loadSubNodes(Project project) {
        List<EcasActionPower> ecasMenus = null;
        try {
            ecasMenus = DBUtils.getInstance().querySubActionPower(getTitle(), project);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ecasMenus.stream().map(item->{
            ActionPoweNode menuNode = new ActionPoweNode();
            menuNode.setNodeData(item);
            menuNode.setSubDataLoaded(false);
            return menuNode;
        }).collect(Collectors.toList());
    }
}
