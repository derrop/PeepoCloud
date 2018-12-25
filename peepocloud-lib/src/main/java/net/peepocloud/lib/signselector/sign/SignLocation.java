package net.peepocloud.lib.signselector.sign;


public class SignLocation {
    private double x, y, z;
    private String world;

    public SignLocation(double x, double y, double z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SignLocation) {
            SignLocation signLocation = (SignLocation) obj;
            return signLocation.world.equalsIgnoreCase(this.world)
                    && signLocation.x == this.x && signLocation.y == this.y && signLocation.z == this.z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int) (this.world.getBytes().length + this.x + this.y + this.y);
    }
}
