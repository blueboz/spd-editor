package cn.boz.jb.plugin.codegen;

import cn.boz.jb.plugin.idea.bean.EcasMenu;
import cn.boz.jb.plugin.idea.utils.DBUtils;
import oracle.jdbc.OracleDriver;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import javax.swing.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜单树
 */
public class EcasMenuTreeTableModel extends AbstractTreeTableModel {

    private List<EcasMenu> data;

    public EcasMenuTreeTableModel(List<EcasMenu> data) {
        super(new Object());
        this.data = data;
    }

    @Override
    public int getColumnCount() {
        // 显示列数
        return 3;
    }

    @Override
    public String getColumnName(int column) {
        // 列名
        switch (column) {
            case 0:
                return "菜单名称";
            case 1:
                return "URL";
            case 2:
                return "级别";
            default:
                return "";
        }
    }

    @Override
    public Object getValueAt(Object node, int column) {
        // 表格单元格值
        if (node instanceof EcasMenu) {
            EcasMenu menu = (EcasMenu) node;
            switch (column) {
                case 0:
                    return menu.getName();
                case 1:
                    return menu.getUrl();
                case 2:
                    return menu.getLvl();
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    public Object getChild(Object parent, int index) {
        // 获取子节点
        if (parent instanceof EcasMenu) {
            EcasMenu menu = (EcasMenu) parent;
            if (menu.getIschild().equals("0")) {
                // 非叶子节点，需进行懒加载
                List<EcasMenu> children = loadChildren(menu.getApplid(), menu.getMenuid());
                if (children != null && !children.isEmpty()) {
                    return children.get(index);
                }
            }
        } else if (parent == root) {
            // 根节点，获取所有顶级节点
            List<EcasMenu> roots = getRoots();
            if (roots != null && !roots.isEmpty()) {
                return roots.get(index);
            }
        }
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        // 获取子节点数
        if (parent instanceof EcasMenu) {
            EcasMenu menu = (EcasMenu) parent;
            if (menu.getIschild().equals("0")) {
                // 非叶子节点，需进行懒加载
                List<EcasMenu> children = loadChildren(menu.getApplid(), menu.getMenuid());
                if (children != null && !children.isEmpty()) {
                    return children.size();
                }
            }
        } else if (parent == root) {
            // 根节点，获取所有顶级节点
            List<EcasMenu> roots = getRoots();
            if (roots != null && !roots.isEmpty()) {
                return roots.size();
            }
        }
        return 0;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        // 获取子节点索引
        if (parent instanceof EcasMenu && child instanceof EcasMenu) {
            EcasMenu menu = (EcasMenu) parent;
            EcasMenu childMenu = (EcasMenu) child;
            if (menu.getIschild().equals("0")) {
                // 非叶子节点，需进行懒加载
                List<EcasMenu> children = loadChildren(menu.getApplid(), menu.getMenuid());
                if (children != null && !children.isEmpty()) {
                    return children.indexOf(childMenu);
                }
            }
        } else if (parent == root && child instanceof EcasMenu) {
            // 根节点，获取所有顶级节点
            List<EcasMenu> roots = getRoots();
            if (roots != null && !roots.isEmpty()) {
                return roots.indexOf(child);
            }
        }
        return -1;
    }

    @Override
    public boolean isLeaf(Object node) {
        // 判断是否叶子节点
        if (node instanceof EcasMenu) {
            EcasMenu menu = (EcasMenu) node;
            return menu.getIschild().equals("1");
        }
        return true;
    }

    private List<EcasMenu> loadChildren(String applid, String parent) {
        // 使用数据库等方式加载子节点数据
        List<EcasMenu> result = new ArrayList<>();
        // TODO: 加载 applid、parent 下的所有子节点
        try {
            List<EcasMenu> maps = DBUtils.getInstance().queryRootMenus("999", null);
            result.addAll(maps);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<EcasMenu> getRoots() {
        // 使用数据库等方式加载顶级节点数据
        List<EcasMenu> result = new ArrayList<>();
        try {
            List<EcasMenu> maps = DBUtils.getInstance().queryRootMenus("999", null);
            result.addAll(maps);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                List<EcasMenu> list = new ArrayList<EcasMenu>();
                EcasMenuTreeTableModel model = new EcasMenuTreeTableModel(list);
                JXTreeTable jxTreeTable = new JXTreeTable(model);
                jxTreeTable.setTreeTableModel(model);
                jxTreeTable.setVisible(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

}
