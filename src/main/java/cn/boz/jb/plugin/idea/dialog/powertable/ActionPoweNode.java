package cn.boz.jb.plugin.idea.dialog.powertable;

import cn.boz.jb.plugin.floweditor.gui.utils.StringUtils;
import cn.boz.jb.plugin.idea.bean.EcasActionPower;
import cn.boz.jb.plugin.idea.bean.EcasMenu;
import cn.boz.jb.plugin.idea.utils.DBUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ActionPoweNode extends NodeData {

    public EcasActionPower getMenuData(){
        return (EcasActionPower) this.getNodeData();
    }
    @Override
    public String getTitle() {
        return getMenuData().getDescription();
    }

    @Override
    public List<NodeData> loadSubNodes(Connection connection) {
        return new ArrayList<>();
//        List<EcasMenu> ecasMenus = null;
//        try {
//            String menuid = getMenuData().getMenuid();
//            if(!StringUtils.isBlank(menuid)){
//                ecasMenus = DBUtils.getInstance().querySubMenus("999", menuid,connection);
//            }else{
//                return new ArrayList<>();
//            }
//        }catch (NumberFormatException nfm){
//            return Collections.emptyList();
//        }
//        return ecasMenus.stream().map(item->{
//            ActionPoweNode menuNode = new ActionPoweNode();
//            menuNode.setNodeData(item);
//            menuNode.setSubDataLoaded(false);
//            return menuNode;
//        }).collect(Collectors.toList());
    }
}
