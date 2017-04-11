public class MemoryBlock {
    private int size, allocated;
    private long position;

    public static final int ALLOCATED = 1;
    public static final int UNALLOCATED = 0;
    public static final int HEADER_SIZE = 8;

    public MemoryBlock(int size, long position, int allocated) {
        this.position = position;
        setSizeAndAllocation(size, allocated);
    }

    public long getPosition() {
        return position;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int newSize) {
        this.size = newSize;
        Disk.seek(position);
        Disk.writeInt(size);
    }

    public void setPosition(long newPosition) {
        this.position = newPosition;
        setSizeAndAllocation(this.size, this.allocated);
    }

    public void setAllocated(int allocated) {
        this.allocated = allocated;
        Disk.seek(position + 4);
        Disk.writeInt(this.allocated);
    }

    public void setSizeAndAllocation(int newSize, int allocated) {
        System.out.println("Writing size " + newSize);
        this.size = newSize;
        this.allocated = allocated;
        if(newSize < 8) {
            return;
        }
        Disk.seek(position);
        Disk.writeInt(this.size);
        Disk.writeInt(this.allocated);

        // Remove
        Disk.seek(position);
        int tsize = Disk.readInt();
        System.out.println("At position " + position + " size is " + tsize);
    }
}
