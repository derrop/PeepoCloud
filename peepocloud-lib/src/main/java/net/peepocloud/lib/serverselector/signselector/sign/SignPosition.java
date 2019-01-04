package net.peepocloud.lib.serverselector.signselector.sign;


public class SignPosition {
    private int x, y, z;
    private String world;
    private String savedOnGroup;

    public SignPosition(int x, int y, int z, String world, String savedOnGroup) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.savedOnGroup = savedOnGroup;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public String getSavedOnGroup() {
        return savedOnGroup;
    }

    @Override
    public String toString() {
        return String.format("x=%s,y=%s,z=%s,world=%s", this.x, this.y, this.z, this.world);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj instanceof SignPosition) {
            SignPosition position = (SignPosition) obj;
            return position.world.equalsIgnoreCase(this.world)
                    && position.x == this.x && position.y == this.y && position.z == this.z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.world.getBytes().length + this.x + this.y + this.y;
    }
}
