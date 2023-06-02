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

    public EcasActionPower(){

    }

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

    public void setApplid(int applid) {
        this.applid = applid;
    }

    public int getPowerbit() {
        return powerbit;
    }

    public void setPowerbit(int powerbit) {
        this.powerbit = powerbit;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getEngModule() {
        return engModule;
    }

    public void setEngModule(String engModule) {
        this.engModule = engModule;
    }

    public String getEngDesc() {
        return engDesc;
    }

    public void setEngDesc(String engDesc) {
        this.engDesc = engDesc;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }
}