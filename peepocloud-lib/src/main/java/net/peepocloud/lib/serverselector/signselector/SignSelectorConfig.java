package net.peepocloud.lib.serverselector.signselector;


public class SignSelectorConfig {
    private int updateDelay;
    private boolean signTitle;
    private String backBlockMaterialName;
    private byte backBlockFullServerSubId;
    private byte backBlockEmptyServerSubId;
    private byte backBlockNormalServerSubId;
    private byte backBlockNoServerSubId;
    private byte backBlockMaintenanceSubId;

    public SignSelectorConfig(int updateDelay, boolean signTitle, String backBlockMaterialName, byte backBlockFullServerSubId, byte backBlockEmptyServerSubId, byte backBlockNormalServerSubId, byte backBlockNoServerSubId, byte backBlockMaintenanceSubId) {
        this.updateDelay = updateDelay;
        this.signTitle = signTitle;
        this.backBlockMaterialName = backBlockMaterialName;
        this.backBlockFullServerSubId = backBlockFullServerSubId;
        this.backBlockEmptyServerSubId = backBlockEmptyServerSubId;
        this.backBlockNormalServerSubId = backBlockNormalServerSubId;
        this.backBlockNoServerSubId = backBlockNoServerSubId;
        this.backBlockMaintenanceSubId = backBlockMaintenanceSubId;
    }

    public int getUpdateDelay() {
        return updateDelay;
    }

    public boolean isSignTitle() {
        return signTitle;
    }

    public String getBackBlockMaterialName() {
        return backBlockMaterialName;
    }

    public byte getBackBlockFullServerSubId() {
        return backBlockFullServerSubId;
    }

    public byte getBackBlockEmptyServerSubId() {
        return backBlockEmptyServerSubId;
    }

    public byte getBackBlockNormalServerSubId() {
        return backBlockNormalServerSubId;
    }

    public byte getBackBlockNoServerSubId() {
        return backBlockNoServerSubId;
    }

    public byte getBackBlockMaintenanceSubId() {
        return backBlockMaintenanceSubId;
    }
}
