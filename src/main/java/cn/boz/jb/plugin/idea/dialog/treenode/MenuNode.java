package cn.boz.jb.plugin.idea.dialog.treenode;

import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;
import cn.boz.jb.plugin.idea.bean.EcasMenu;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import com.intellij.openapi.project.Project;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MenuNode extends NodeData{

    public EcasMenu getMenuData(){
        return (EcasMenu) this.getNodeData();
    }
    @Override
    public String getTitle() {
        return getMenuData().getName();
    }

    @Override
    public List<NodeData> loadSubNodes(Project project) {
        List<EcasMenu> ecasMenus = null;
        try {
            String pid = getMenuData().getParent();
            Integer.parseInt(pid);
            if(!StringUtils.isBlank(pid)){
                ecasMenus = DBUtils.getInstance().queryMenuById(project, pid);
            }else{
                return new ArrayList<>();
            }
        }catch (NumberFormatException nfm){
            return Collections.emptyList();
        }catch (Exception e) {
            DBUtils.dbExceptionProcessor(e,project);
        }
        return ecasMenus.stream().map(item->{
            MenuNode menuNode = new MenuNode();
            menuNode.setNodeData(item);
            menuNode.setSubDataLoaded(false);
            return menuNode;
        }).collect(Collectors.toList());
    }
}
