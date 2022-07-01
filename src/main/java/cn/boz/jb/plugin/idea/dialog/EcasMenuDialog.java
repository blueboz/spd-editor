package cn.boz.jb.plugin.idea.dialog;

import cn.boz.jb.plugin.idea.bean.EcasMenu;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

public class EcasMenuDialog extends DialogWrapper {


    private final List<EcasMenu> ecasMenuList;
    private final JBScrollPane jbScrollPane;

    @SuppressWarnings("all")
    public EcasMenuDialog(List<EcasMenu> ecasMenuList) {
        super(true);
        setTitle("ecasMenu");
        this.ecasMenuList = ecasMenuList;
        //applid, menuid, name, lvl, url, parent, img, ischild, groupid
        ColumnInfo[] columns = new ColumnInfo[]{new ColumnInfo<EcasMenu, String>("applid") {
            @Nullable
            @Override
            public String valueOf(EcasMenu o) {
                return o.getApplid();
            }

        }, new ColumnInfo<EcasMenu, String>("menuid") {
            @Nullable
            @Override
            public String valueOf(EcasMenu o) {
                return o.getMenuid();
            }
        }, new ColumnInfo<EcasMenu, String>("name") {
            @Nullable
            @Override
            public String valueOf(EcasMenu o) {
                return o.getName();
            }
        }, new ColumnInfo<EcasMenu, String>("lvl") {
            @Nullable
            @Override
            public String valueOf(EcasMenu o) {
                return o.getLvl();
            }
        }, new ColumnInfo<EcasMenu, String>("url") {
            @Nullable
            @Override
            public String valueOf(EcasMenu o) {
                return o.getUrl();
            }
        }, new ColumnInfo<EcasMenu, String>("parent") {
            @Nullable
            @Override
            public String valueOf(EcasMenu o) {
                return o.getParent();
            }
        }, new ColumnInfo<EcasMenu, String>("img") {
            @Nullable
            @Override
            public String valueOf(EcasMenu o) {
                return o.getImg();
            }
        }, new ColumnInfo<EcasMenu, String>("ischild") {
            @Nullable
            @Override
            public String valueOf(EcasMenu o) {
                return o.getIschild();
            }
        }, new ColumnInfo<EcasMenu, String>("groupid") {
            @Nullable
            @Override
            public String valueOf(EcasMenu o) {
                return o.getGroupid();
            }


        }
        };



        JBTable jbTable = new JBTable(new ListTableModel<EcasMenu>(columns, ecasMenuList, 0)){
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        this.jbScrollPane = new JBScrollPane(jbTable);
        jbScrollPane.setPreferredSize(new Dimension(900,400));
        init();


    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return this.jbScrollPane;
    }
}
