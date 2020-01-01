package me.geek.tom.deobfer.mappings;

@SuppressWarnings("WeakerAccess")
public class MethodMapping {
    private String orgName;
    private String obfName;
    private String desc;
    private String className;

    public String getOrgName() {
        return orgName;
    }

    public String getObfName() {
        return obfName;
    }

    public String getDesc() {
        return desc;
    }

    public String getClassName() {
        return className;
    }

    public MethodMapping(String orgName, String obfName, String desc, String className) {
        this.orgName = orgName;
        this.obfName = obfName;
        this.desc = desc;
        this.className = className;
    }

    public boolean shouldRename(String currentName, String currentDesc) {
        if (this.getObfName().equals(currentName))
            return this.getDesc().equals(currentDesc);
        return false;
    }
}
