package cn.boz.jb.plugin.floweditor.gui.property;

import cn.boz.jb.plugin.floweditor.gui.widget.MyTable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * 定义一个属性
 */
public abstract class Property {
    private String propertyName;
    private Object operatedObj;
    private String displayProperyName;
    private Integer rowHeight;

    public Integer getRowHeight() {
        if (rowHeight == null) {
            return MyTable.DEFAULT_ROW_HEIGHT;
        }
        return rowHeight;
    }


    public void setRowHeight(Integer rowHeight) {
        this.rowHeight = rowHeight;
    }


    public Object getOperatedObj() {
        return operatedObj;
    }

    public void setOperatedObj(Object operatedObj) {
        this.operatedObj = operatedObj;
    }

    public String getDisplayProperyName() {
        return displayProperyName;
    }

    public void setDisplayProperyName(String displayProperyName) {
        this.displayProperyName = displayProperyName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * 获取该对象封装的编辑器
     *
     * @return
     */
    public abstract JComponent getEditor();

    private JLabel valueRender;


    private JLabel propertyRender;

    public JComponent getValueRender() {
//        valueRender.setText((String) getValue());
        valueRender.setText("<html>" + ((String) getValue()).replace("\n", "<br>") + "</html>");
        return valueRender;
    }


    public JComponent getPropertyRender() {
//        propertyRender.setText(getPropertyName());
        propertyRender.setText("<html>" + this.getPropertyName().replace("\n", "<br>") + "</html>");
        return propertyRender;
    }

    public Property(String propertyName, Object operatedObj) {
        this.propertyName = propertyName;
        this.operatedObj = operatedObj;
        this.valueRender = new JLabel();
        this.propertyRender = new JLabel();
        valueRender.setAutoscrolls(false);
        propertyRender.setAutoscrolls(false);

    }

    public Property(String propertyName, String displayProperyName, Object operatedObj) {
        this.displayProperyName = displayProperyName;
        this.propertyName = propertyName;
        this.operatedObj = operatedObj;
        this.valueRender = new JLabel();
        this.propertyRender = new JLabel();
        propertyRender.setAutoscrolls(false);
        valueRender.setAutoscrolls(false);
    }


    public abstract String getInputValue();

    public Field getFieldOfClass(Class clz, String propertyName) {
        try {
            Field field = clz.getDeclaredField(propertyName);
            return field;
        } catch (NoSuchFieldException e) {
            Type genericSuperclass = clz.getGenericSuperclass();
            if (genericSuperclass != null) {
                return getFieldOfClass((Class) genericSuperclass, propertyName);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取属性值
     *
     * @return
     */
    public Object getValue() {
        try {
            Field field = getFieldOfClass(operatedObj.getClass(), propertyName);
            if (field != null) {
                field.setAccessible(true);
                Object o = field.get(operatedObj);
                if (o == null) {
                    return "";
                }
                if (o instanceof String) {
                    String result = (String) o;
                    result = result.replace("#LEY#", "\n");
                    result = result.replace("&quot;", "\"");
                    return result;
                }
                return o;
            } else {
                throw new RuntimeException(propertyName + "不存在");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置绑定的属性值
     *
     * @param value
     */
    public void setValue(Object value) {
        try {
            Field field = getFieldOfClass(operatedObj.getClass(), propertyName);
            if (field != null) {
                field.setAccessible(true);
                if (value instanceof String) {
                    String result = (String) value;
                    result = result.replace("\n", "#LEY#");
                    result = result.replace("\"", "&quot;");
                    field.set(operatedObj, result);

                } else {
                    field.set(operatedObj, value);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}

