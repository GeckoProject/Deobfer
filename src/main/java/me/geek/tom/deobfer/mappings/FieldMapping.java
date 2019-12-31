package me.geek.tom.deobfer.mappings;

@SuppressWarnings("WeakerAccess")
public class FieldMapping {
    private String orgName;
    private String obfName;
    private String className;

    public String getOrgName() {
        return orgName;
    }

    public String getObfName() {
        return obfName;
    }

    public String getClassName() {
        return className;
    }

    public FieldMapping(String orgName, String obfName, String className) {
        this.orgName = orgName;
        this.obfName = obfName;
        this.className = className;
    }
}
