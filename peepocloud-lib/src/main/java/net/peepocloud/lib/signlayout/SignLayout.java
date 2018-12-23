package net.peepocloud.lib.signlayout;


public class SignLayout {
    private String[] lines;
    private String layoutName;

    public SignLayout(String layoutName, String[] lines) {
        this.layoutName = layoutName;
        this.lines = lines;
    }

    public String getLayoutName() {
        return layoutName;
    }

    public String[] getLines() {
        return lines;
    }
}
