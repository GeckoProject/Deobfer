package me.geek.tom.deobfer.mappings;

@SuppressWarnings("WeakerAccess")
public class ClassMapping {
    private String orgName;
    private String obfName;

    public String getOrgName() {
        return orgName;
    }

    public String getObfName() {
        return obfName;
    }

    public ClassMapping(String orgName, String obfName) {
        this.orgName = orgName;
        this.obfName = obfName;
    }
}
