package net.peepocloud.lib.serverselector;


public class Position {
    private int x, y, z;
    private String world;

    public Position(int x, int y, int z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
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

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Position) {
            Position position = (Position) obj;
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
