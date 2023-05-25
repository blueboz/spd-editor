package cn.boz.jb.plugin.idea.bean;

public class EcasActionPower {
    private int applid;
    private int powerbit;
    private String path;
    private String description;
    private int enabled;
    private String moduleName;
    private int weight;
    private String engModule;
    private String engDesc;
    private int menuId;

    public EcasActionPower(int applid, int powerbit, String path, String description, int enabled, String moduleName, int weight, String engModule, String engDesc, int menuId) {
        this.applid = applid;
        this.powerbit = powerbit;
        this.path = path;
        this.description = description;
        this.enabled = enabled;
        this.moduleName = moduleName;
        this.weight = weight;
        this.engModule = engModule;
        this.engDesc = engDesc;
        this.menuId = menuId;
    }

    public int getApplid() {
        return applid;
    }

    public int getPowerbit() {
        return powerbit;
    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public int getEnabled() {
        return enabled;
    }

    public String getModuleName() {
        return moduleName;
    }

    public int getWeight() {
        return weight;
    }

    public String getEngModule() {
        return engModule;
    }

    public String getEngDesc() {
        return engDesc;
    }

    public int getMenuId() {
        return menuId;
    }
}