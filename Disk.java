import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class Disk {
    private static RandomAccessFile raf;

    public static void newDisk() {
        try {
            raf = new RandomAccessFile("virtualdisk1.txt", "rw");
            // Set length of file to 1 MB
            long fileSize = 1 * (1024 * 1024);
            raf.setLength(fileSize);
            byte[] b = {' '};
            for(long i = 0; i < fileSize; i++) {
                raf.write(b);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static Directory loadDisk() {
        try {
            raf = new RandomAccessFile("virtualdisk1.txt", "rw");
            FileReader fr = new FileReader("meta.txt");
            BufferedReader br = new BufferedReader(fr);
            String[] strings = br.readLine().split(" ");
            String dType = strings[0];
            int allocationType = Integer.parseInt(strings[1]);
            Directory directory;
            if(dType.equals("LLDirectory")) {
                directory = new LLDirectory(allocationType, true);
            } else {
                directory = new HTDirectory(allocationType, true);
            }
            MemoryManager mm = new MemoryManager(true);
            if(allocationType == File.CONTIGUOUS) {
                ContiguousFile.setMemoryManager(mm);
            } else if(allocationType == File.LINKED) {

            } else {

            }
            return directory;
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void write(byte[] b, long off, int len) {
        try {
            raf.seek(off);
            raf.write(b, 0, len);
        } catch(IOException io) {
            io.printStackTrace();
        }
    }

    public static void seek(long position) {
        try {
            raf.seek(position);
        } catch(IOException io) {
            io.printStackTrace();
        }
    }

    public static void writeInt(int w) {
        try {
            raf.writeInt(w);
        } catch(IOException io) {
            io.printStackTrace();
        }
    }

    public static void writeBoolean(boolean b) {
        try {
            raf.writeBoolean(b);
        } catch(IOException io) {
            io.printStackTrace();
        }
    }

    public static void write(String s, long start) {
        try {
            raf.seek(start);
            raf.writeChars(s);
        } catch(IOException io) {
            io.printStackTrace();
        }
    }

    public static int read(byte[] b, long off, int len) {
        int res = 0;
        try {
            raf.seek(off);
            res = raf.read(b, 0, len);
        } catch(IOException io) {
            io.printStackTrace();
        }
        return res;
    }

    public static int readInt() {
        int res = -1;
        try {
            res = raf.readInt();
        } catch(IOException io) {
            io.printStackTrace();
        }
        return res;
    }

    public static long getDiskOffset() {
        long res = -1;
        try {
            res = raf.getFilePointer();
        } catch(IOException io) {
            io.printStackTrace();
        }
        return res;
    }

    public static int getDiskSize() {
        int res = -1;
        try {
            res = (int)raf.length();
        } catch(IOException io) {
            io.printStackTrace();
        }
        return res;
    }
}
