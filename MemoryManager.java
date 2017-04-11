import java.util.LinkedList;

public class MemoryManager {
    private LinkedList<MemoryBlock> freeList;  // List of free blocks sorted according to starting position of the block

    public MemoryManager() {
        this.freeList = new LinkedList<MemoryBlock>();
        int size = Disk.getDiskSize();
        this.freeList.add(new MemoryBlock(size, 0, MemoryBlock.UNALLOCATED));
    }

    // Initialize the free list of the memory manager by reading the virtual disk
    public MemoryManager(boolean loadFromDisk) {
        this.freeList = new LinkedList<MemoryBlock>();
        int size = Disk.getDiskSize();
        for(int i = 0; i < size;) {
            Disk.seek(i);
            int blockSize = Disk.readInt();
            int allocated = Disk.readInt();
            if(allocated == MemoryBlock.UNALLOCATED) {
                this.freeList.add(new MemoryBlock(blockSize, i, MemoryBlock.UNALLOCATED));
            }
            i = i + blockSize;
        }
    }

    public void freeBlock(long position, int size) {
        size = size + MemoryBlock.HEADER_SIZE;
        position = position - MemoryBlock.HEADER_SIZE;
        for(int i = 0; i < freeList.size(); i++) {
            MemoryBlock current = freeList.get(i);
            if(position < current.getPosition()) {
                MemoryBlock freeBlock = new MemoryBlock(size, position, MemoryBlock.UNALLOCATED);
                freeList.add(i, freeBlock);
                coalesce(i);
                return;
            }
        }
    }

    private void coalesce(int index) {
        MemoryBlock prev, next, current;
        prev = next = null;
        current = freeList.get(index);
        if(index - 1 >= 0) {
            prev = freeList.get(index - 1);
        }
        if(index + 1 < freeList.size()) {
            next = freeList.get(index + 1);
        }

        if(prev != null) {
            if(prev.getSize() + prev.getPosition() == current.getPosition()) {
                prev.setSize(prev.getSize() + current.getSize());
                current = prev;
                freeList.remove(index);
            }
        }

        if(next != null) {
            if(current.getSize() + current.getPosition() == next.getPosition()) {
                current.setSize(current.getSize() + next.getSize());
                freeList.remove(next);
            }
        }
    }

    // Allocating free memory blocks using first fit algorithm
    public long allocate(int size) {
        int requiredSize = size + MemoryBlock.HEADER_SIZE;
        for(int i = 0; i < freeList.size(); i++) {
            MemoryBlock current = freeList.get(i);
            if(current.getSize() >= requiredSize) {
                long position = current.getPosition();
                int newSize = current.getSize() - requiredSize;
                if(newSize < 8) {
                    freeList.remove(i);
                } else {
                    current.setPosition(position + requiredSize);
                    current.setSize(current.getSize() - requiredSize);
                }

                new MemoryBlock(requiredSize, position, MemoryBlock.ALLOCATED);
                return position + MemoryBlock.HEADER_SIZE;
            }
        }
        return -1;
    }
}
