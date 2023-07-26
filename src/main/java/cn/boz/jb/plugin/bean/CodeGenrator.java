package cn.boz.jb.plugin.bean;

import java.util.List;

public class CodeGenrator {
    private String packageName;
    private String className;
    private String beanName;
    private String namespace;
    private String tableName;
    private int menuID;
    private int pmenuID;
    private String powerBits;
    private String moduleName;
    private String functionBase;
    private String title;
    private String exceptionFullName;
    private String exceptionName;
    private String queryNamespace;
    private boolean importExcel;
    private List<CodeDef> codeDefs;
    private List<Column> columns;

    // 构造方法、Getter和Setter省略
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getMenuID() {
        return menuID;
    }

    public void setMenuID(int menuID) {
        this.menuID = menuID;
    }

    public int getPmenuID() {
        return pmenuID;
    }

    public void setPmenuID(int pmenuID) {
        this.pmenuID = pmenuID;
    }

    public String getPowerBits() {
        return powerBits;
    }

    public void setPowerBits(String powerBits) {
        this.powerBits = powerBits;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getFunctionBase() {
        return functionBase;
    }

    public void setFunctionBase(String functionBase) {
        this.functionBase = functionBase;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExceptionFullName() {
        return exceptionFullName;
    }

    public void setExceptionFullName(String exceptionFullName) {
        this.exceptionFullName = exceptionFullName;
    }

    public String getExceptionName() {
        return exceptionName;
    }

    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }

    public String getQueryNamespace() {
        return queryNamespace;
    }

    public void setQueryNamespace(String queryNamespace) {
        this.queryNamespace = queryNamespace;
    }

    public boolean isImportExcel() {
        return importExcel;
    }

    public void setImportExcel(boolean importExcel) {
        this.importExcel = importExcel;
    }

    public List<CodeDef> getCodeDefs() {
        return codeDefs;
    }

    public void setCodeDefs(List<CodeDef> codeDefs) {
        this.codeDefs = codeDefs;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public static class CodeDef {
        private String type;
        private String rmk1;
        private String rmk2;
        private String rmk3;
        private List<CodeItem> items;

        // 构造方法、Getter和Setter省略

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getRmk1() {
            return rmk1;
        }

        public void setRmk1(String rmk1) {
            this.rmk1 = rmk1;
        }

        public String getRmk2() {
            return rmk2;
        }

        public void setRmk2(String rmk2) {
            this.rmk2 = rmk2;
        }

        public String getRmk3() {
            return rmk3;
        }

        public void setRmk3(String rmk3) {
            this.rmk3 = rmk3;
        }

        public List<CodeItem> getItems() {
            return items;
        }

        public void setItems(List<CodeItem> items) {
            this.items = items;
        }
    }

    public static class CodeItem {
        private String name;
        private String value;

        // 构造方法、Getter和Setter省略

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Column {
        private String fieldChName;
        private String fieldName;
        private String dataType;
        private boolean nullable;
        private boolean isId;
        private String codeDef;
        private String classType;

        // 构造方法、Getter和Setter省略

        public String getFieldChName() {
            return fieldChName;
        }

        public void setFieldChName(String fieldChName) {
            this.fieldChName = fieldChName;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public boolean isNullable() {
            return nullable;
        }

        public void setNullable(boolean nullable) {
            this.nullable = nullable;
        }

        public boolean isId() {
            return isId;
        }

        public void setId(boolean id) {
            isId = id;
        }

        public String getCodeDef() {
            return codeDef;
        }

        public void setCodeDef(String codeDef) {
            this.codeDef = codeDef;
        }

        public String getClassType() {
            return classType;
        }

        public void setClassType(String classType) {
            this.classType = classType;
        }
    }
}
