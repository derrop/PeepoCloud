package net.peepocloud.lib.serverselector.signselector;


public class SignLayout {
    private String layoutName;
    private String[] lines;
    private String[] signTitle;

    public SignLayout(String layoutName, String[] lines, String[] signTitle) {
        this.layoutName = layoutName;
        this.lines = lines;
        this.signTitle = signTitle;
    }

    public String getLayoutName() {
        return layoutName;
    }

    public String[] getLines() {
        return lines;
    }

    public String[] getSignTitle() {
        return signTitle;
    }
}
