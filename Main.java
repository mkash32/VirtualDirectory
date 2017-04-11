import java.util.Scanner;

public class Main {
	public static void main(String args[]) throws Exception
	{
			Scanner scan = new Scanner(System.in);
      Directory directory = null;
      System.out.println("1. Create New Disk\n"
                         + "2. Load Disk");
      int op1 = scan.nextInt();
      if(op1 == 2) {
          directory = Disk.loadDisk();
      } else {
          Disk.newDisk();
          System.out.println("Select the type of Directory implementation to be used:\n"
                             + "1. Linked List\n"
                             + "2. Hash Table");
          int dType = scan.nextInt();

          System.out.println("Select the File Allocation strategy be used:\n"
                             + "1. Contiguous Allocation\n"
                             + "2. Linked Allocation");
          int fType = scan.nextInt();

          if(fType != 1 && fType != 2) {
              System.out.println("Invalid File Allocation strategy selected. Exiting");
              return;
          }

          if(dType == 1) {
              directory = new LLDirectory(fType);
          } else if(dType == 2) {
              directory = new HTDirectory(fType);
          } else {
              System.out.println("Invalid Directory option selected. Exiting.");
              return;
          }
      }

			while(true)
			{

          System.out.println("Select the operation to perform from the following list:\n1. Create File\n2. Delete File\n3. Rename File\n4. Read File\n5. Write File\n6. List Files\n7. Exit");
        String name;
        File file;
				int op = scan.nextInt();
        scan.nextLine();
        boolean result = false;
        System.out.println();
        switch(op) {
        case 1: // Create File
            System.out.println("Enter name of file to be created: ");
            name = scan.nextLine().trim();
            result = directory.createFile(name);
            break;
        case 2: // Delete File
            System.out.println("Enter name of file to be removed: ");
            name = scan.nextLine().trim();;
            result = directory.removeFile(name);
            break;
        case 3: // Rename File
            System.out.println("Enter name of file to be renamed: ");
            String oldName = scan.nextLine().trim();
            System.out.println("Enter new name: ");
            String newName = scan.nextLine().trim();
            result = directory.renameFile(oldName, newName);
            break;
        case 4: // Read File
            System.out.println("Enter name of file to be read: ");
            name = scan.nextLine().trim();
            file = directory.findFile(name);
            if(file == null) {
                System.out.println("File does not exist.\n");
                result = false;
            } else {
                String fileData = file.readFile(file.getSize());
                System.out.println(fileData);
                result = true;
            }
            break;
        case 5: // Write File
            System.out.println("Enter name of file to be modified: ");
            name = scan.nextLine().trim();
            file = directory.findFile(name);
            if(file == null) {
                System.out.println("File does not exist.\n");
                result = false;
            } else {
                System.out.println("Enter text to be written to the file: ");
                String content = scan.nextLine();
                result = file.writeFile(content.getBytes());
            }
            break;
        case 6: // List Files
            File[] files = directory.listFiles();
            String indent = "                                         ";
            for(int i=0; i < files.length; i++) {
                int indentLength = 25 - files[i].getName().length();
                System.out.println(files[i].getName() +
                                   indent.substring(0, indentLength) +
                                   files[i].getSize() + " Bytes");
            }
            result = true;
            break;
        case 7: // Exit
            directory.exit();
            return;
        }

        if(result) {
            System.out.println("Command Executed Successfully.\n");
        } else {
            System.out.println("Command Failed.\n");
        }
	}
}
}
