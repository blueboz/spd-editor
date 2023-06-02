package cn.boz.jb.plugin.idea.dialog.treetable;

import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;
import cn.boz.jb.plugin.idea.bean.EcasMenu;
import cn.boz.jb.plugin.idea.utils.DBUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MenuNode extends NodeData {

    public EcasMenu getMenuData(){
        return (EcasMenu) this.getNodeData();
    }
    @Override
    public String getTitle() {
        return getMenuData().getName();
    }

    @Override
    public List<NodeData> loadSubNodes(Connection connection) {
        List<EcasMenu> ecasMenus = null;
        try {
            String menuid = getMenuData().getMenuid();
            if(!StringUtils.isBlank(menuid)){
                ecasMenus = DBUtils.getInstance().querySubMenus("999", menuid,connection);
            }else{
                return new ArrayList<>();
            }
        }catch (NumberFormatException nfm){
            return Collections.emptyList();
        }
        return ecasMenus.stream().map(item->{
            MenuNode menuNode = new MenuNode();
            menuNode.setNodeData(item);
            menuNode.setSubDataLoaded(false);
            return menuNode;
        }).collect(Collectors.toList());
    }
}
