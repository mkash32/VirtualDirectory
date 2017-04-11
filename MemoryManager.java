import java.util.LinkedList;

public class MemoryManager {
    private LinkedList<MemoryBlock> freeList;  // List of free blocks maintained according to starting position of the block

    public MemoryManager() {
        this.freeList = new LinkedList<MemoryBlock>();
        int size = Disk.getDiskSize();

        // Initially there is one large free block
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

    // Allocating free memory blocks using First-Fit algorithm
    public long allocate(int size) {
        // Actual required block size is the requested size + size of header data
        int requiredSize = size + MemoryBlock.HEADER_SIZE;

        for(int i = 0; i < freeList.size(); i++) {
            MemoryBlock current = freeList.get(i);
            if(current.getSize() >= requiredSize) {
                // Free block of size greater than requested size will
                // be split into two blocks
                long position = current.getPosition();
                int newSize = current.getSize() - requiredSize;
                // If remaining size of unallocated block after splitting is less
                // than the header size then remove the free block from the free list.
                // External Fragmentation is caused by these wasted free blocks.
                if(newSize < MemoryBlock.HEADER_SIZE) {
                    freeList.remove(i);
                } else {
                    // Set the new size and position of the split free block
                    current.setPosition(position + requiredSize);
                    current.setSize(current.getSize() - requiredSize);
                }

                // Create the Allocated block and return the position of the memory block
                // to the requester
                new MemoryBlock(requiredSize, position, MemoryBlock.ALLOCATED);
                return position + MemoryBlock.HEADER_SIZE;
            }
        }
        return -1;
    }

    // Deallocates a block of memory starting at the given position and with the given size
    public void freeBlock(long position, int size) {
        // Total block size is the size of the file plus the header data
        size = size + MemoryBlock.HEADER_SIZE;
        // The starting position of the block is actually the starting of the header
        position = position - MemoryBlock.HEADER_SIZE;

        // Insert a new free block at the appropriate index of the free list
        // according to block position
        for(int i = 0; i < freeList.size(); i++) {
            MemoryBlock current = freeList.get(i);
            if(position < current.getPosition()) {
                MemoryBlock freeBlock = new MemoryBlock(size, position, MemoryBlock.UNALLOCATED);
                freeList.add(i, freeBlock);

                // Coalesce the free block with its neighbor blocks if possible
                coalesce(i);
                return;
            }
        }
    }

    // Coalesces the free block at the given index with its neighboring free blocks,
    // if possible. This method is called only when there are requests to free blocks.
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

        // If previous block exists, and the ending position of the previous free
        // block coincides with the starting position of the current free block,
        // then merge the blocks into the previous block and remove the current
        // block from the free list. The merged block will then be the current block.
        if(prev != null) {
            if(prev.getSize() + prev.getPosition() == current.getPosition()) {
                prev.setSize(prev.getSize() + current.getSize());
                current = prev;
                freeList.remove(index);
            }
        }

        // If next block exists, and the ending position of the current free
        // block coincides with the starting position of the next free block,
        // then merge the blocks into the current block and remove the next
        // block from the free list.
        if(next != null) {
            if(current.getSize() + current.getPosition() == next.getPosition()) {
                current.setSize(current.getSize() + next.getSize());
                freeList.remove(next);
            }
        }
    }
}
