package core;

import java.util.*;

import exceptions.*;

/**
 * A virtual file system application that can be included in a user interface.
 * @author ayoub
 *
 */
public class VFSApp {
	
	/* ----------------------- attributes ----------------------- */
	
	
	/** the virtual file systems loaded in memory*/
	private ArrayList<VirtualFileSystem> vfsDataBase = new ArrayList<VirtualFileSystem>();
	/** current working virtual file system*/
	private VirtualFileSystem workingVFS = null;
	
	
	/* ----------------------- constructors ----------------------- */
	
	
	/**
	 * creates a VFSApp loading all existing virtual file systems on the working directory in the host file system.
	 */
	public VFSApp(){
		// getting host file system working directory
		java.io.File workingDir = new java.io.File(System.getProperty("user.dir"));
		// looking for virtual disk files
		for(java.io.File file: workingDir.listFiles()){
			if(file.isFile() && file.getName().endsWith(VirtualFileSystem.VDFileExtension)){
				vfsDataBase.add(new VirtualFileSystem(file.getName().substring(0, file.getName().length()-VirtualFileSystem.VDFileExtension.length())));
			}
		}
		// setting current working virtual file system if found
		if(!vfsDataBase.isEmpty())
			workingVFS = vfsDataBase.get(0);
	}
	
	
	/* ----------------------- getters ----------------------- */
	
	
	/**
	 * returns the list of virtual file systems load on the memory
	 * @return vfsDataBase attribute
	 */
	public List<VirtualFileSystem> getVFSDataBase(){
		return vfsDataBase;
	}
	
	/**
	 * returns the virtual file system with the given name
	 * @param vfsName name of the virtual file system to look for
	 * @return a reference to the VirtualFileSystem if found
	 * @throws NoSuchVirtualFileSystemException if no virtual disk found
	 */
	public VirtualFileSystem getVFSByName(String vfsName){
		VirtualFileSystem vfs = null;
		if(vfsName.equals("")) return workingVFS;
		for(VirtualFileSystem v : vfsDataBase)
			if(v.name.equals(vfsName)){
				vfs = v;
				break;
			}
		
		if(vfs==null) throw new NoSuchVirtualFileSystemException("\""+vfsName+"\" does not exist");
		return vfs;
	}
	
	/**
	 * returns the current working virtual file system
	 * @return workingVFS reference.
	 */
	public VirtualFileSystem getCurrentWorkingVFS(){
		return workingVFS;
	}
	
	
	/* ----------------------- VFS APP commands ----------------------- */
	
	
	/**
	 * returns a string where it lists all the content of a directory on a virtual file system.
	 * It is similar to the "ls" command on Unix systems.
	 * @param vfsName virtual file system name
	 * @param args arguments of the command 
	 * @param pathName the path name of the directory to show all its content
	 * @return a string where all the content of the directory is listed
	 * @throws NoSuchDirectoryException if the path name is not valid
	 * @throws NoSuchVirtualFileSystemException if no virtual file system correspond to the given name argument
	 */
	public String list(String vfsName,String args, String pathName){
		String r = "";				// string to return
		boolean lArg = false;		// checks if "-l" argument has been passed
		List<VNode> list = null;	// list of contents to return in the string
		
		// getting virtual file system
		VirtualFileSystem vfs = getVFSByName(vfsName);
		
		// checking if "-l" argument has been passed
		if(args.equals("-l"))
			lArg = true;
		
		// if no path name has been passed 
		if(pathName.equals(""))
			list = vfs.getWorkingDirectory().getContentList();
		
		else{
			// getting target node
			VNode target = vfs.get(pathName);	
			if(!target.isDirectory())
				throw new NoSuchDirectoryException("\""+pathName+"\" is not a directory");
			list = ((Directory) target).getContentList();
			
		}
		
		// bulding return string
		int lenMaxNames = 0;
		int lenMaxSizes = 0;
		for(VNode node : list){
			if(lenMaxNames<node.name.length())
				lenMaxNames = node.name.length();
			if(lenMaxSizes<Long.toString(node.size).length())
				lenMaxSizes = Long.toString(node.size).length();
		}
		
		for(VNode node : list){
			String tabAfterNames = "   ";
			String tabAfterSizes = "   ";
			for(int i=0;i<lenMaxNames-node.name.length();i++)
				tabAfterNames += " ";
			for(int i=0;i<lenMaxSizes-Long.toString(node.size).length();i++)
				tabAfterSizes += " ";
			
			r += node.name + tabAfterNames + ((lArg)?(node.size+tabAfterSizes):"") + ((node.type.equals("file"))?"f":"d") + "\n";
		}

		return r;
	}
	
	/**
	 * changes the current working directory of a virtual file system.
	 * It is similar to the "cd" command on Unix systems.
	 * @param vfsName virtual file system name
	 * @param pathName the path name of the directory where to go
	 * @throws NoSuchDirectoryException if the path name is not valid
	 * @throws NoSuchVirtualFileSystemException if no virtual file system correspond to the given name argument
	 */
	public void changeDir(String vfsName, String pathName){
		VirtualFileSystem vfs = getVFSByName(vfsName);
		vfs.navigate(pathName);
		if(!vfsName.equals(workingVFS.name))
			workingVFS = vfs;
	}
	
	/**
	 * creates a new directory in a virtual file system.
	 * It is similar to the "mkdir" command on Unix systems.
	 * @param vfsName virtual file system name
	 * @param pathName the path name of the directory to create
	 * @throws NameAlreadyExistsException if there's a VNode with the name of the new directory
	 * @throws NoSuchVirtualFileSystemException if no virtual file system correspond to the given name argument
	 */
	public void makeDir(String vfsName, String pathName){
		VirtualFileSystem vfs = getVFSByName(vfsName);
		vfs.createDirectory(pathName);
	}
	
	/**
	 * creates a new file in a virtual file system.
	 * It is similar to the "touch" command on Unix systems.
	 * @param vfsName virtual file system name
	 * @param pathName the path name of the file to create
	 * @param dim the size (in bytes) of the file
	 * @throws NameAlreadyExistsException if there's a VNode with the name of the new directory
	 * @throws NoSuchVirtualFileSystemException if no virtual file system correspond to the given name argument
	 */
	public void makeFile(String vfsName, String pathName, String dim){
		VirtualFileSystem vfs = getVFSByName(vfsName);
		vfs.createFile(pathName,Long.parseLong(dim));
	}
	
	/**
	 * moves/renames a VNode on a virtual file system.
	 * It is similar to the "mv" command on Unix systems.
	 * @param vfsName virtual file system name
	 * @param oldPathName path name of the VNode to move/rename
	 * @param newPathName new path name of the VNode to move/rename
	 * @throws NameAlreadyExistsException if there's a VNode with the same name
	 * @throws NoSuchDirectoryException if a path name is not valid
	 * @throws NoSuchVirtualFileSystemException if no virtual file system correspond to the given name argument
	 * @throws VFSOperationFailException if operation fails
	 */
	public void move(String vfsName, String oldPathName, String newPathName){
		VirtualFileSystem vfs = getVFSByName(vfsName);
		VNode toMove = vfs.get(oldPathName);
		VNode whereToMove = null;
		String newName;
		
		try{
			whereToMove = vfs.get(newPathName);
			if(whereToMove.isFile())
				throw new NoSuchDirectoryException("\""+newPathName+"\" is not a directory");
			newName = toMove.name;
		} catch (NoSuchFileOrDirectoryException e){
			if(!Path.isPath(newPathName)){
				whereToMove = vfs.getWorkingDirectory();
				newName = newPathName;
			}
			else {
				whereToMove = vfs.get(Path.getPathFromPathName(newPathName));
				newName = Path.getNameFromPathName(newPathName);
			}
		}
		
		if(!toMove.getParent().equals(whereToMove))
			toMove.move((Directory) whereToMove);
		toMove.rename(newName);
	}
	
	
	/**
	 * moves/renames a VNode on a virtual file system.
	 * If the file/directory already exists it overwrites it.
	 * It is similar to the "mv" command on Unix systems.
	 * @param vfsName virtual file system name
	 * @param oldPathName path name of the VNode to move/rename
	 * @param newPathName new path name of the VNode to move/rename
	 * @throws NoSuchDirectoryException if a path name is not valid
	 * @throws NoSuchVirtualFileSystemException if no virtual file system correspond to the given name argument
	 * @throws VFSOperationFailException if operation fails
	 */
	public void moveAndOverwrite(String vfsName, String oldPathName, String newPathName) {
		VirtualFileSystem vfs = getVFSByName(vfsName);
		VNode toMove = vfs.get(oldPathName);
		VNode whereToMove = null;
		
		try{
			whereToMove = vfs.get(newPathName);
			if(whereToMove.isFile())
				throw new NoSuchDirectoryException("\""+newPathName+"\" is not a directory");
		} catch (NoSuchFileOrDirectoryException e){
			if(!Path.isPath(newPathName)){
				whereToMove = vfs.getWorkingDirectory();
			}
			else {
				whereToMove = vfs.get(Path.getPathFromPathName(newPathName));
			}
		}
		
		((Directory) whereToMove).get(toMove.name).remove();
		
		toMove.move((Directory) whereToMove);
	}
	

	/**
	 * copies a VNode on a virtual file system.
	 * It is similar to the "cp" command on Unix systems.
	 * @param vfsName virtual file system name
	 * @param oldPathName path name of the VNode to copy
	 * @param newPathName new path name of the directory where to copy
	 * @throws NoSuchDirectoryException if a path name is not valid
	 * @throws NameAlreadyExistsException if there's a VNode with the same name
	 * @throws NoSuchVirtualFileSystemException if no virtual file system correspond to the given name argument
	 * @throws VFSOperationFailException if operation fails
	 * @throws NoFreeSpaceLeftException if the virtual file system does not have enough space left
	 */
	public void copy(String vfsName, String oldPathName, String newPathName){
		VirtualFileSystem vfs = getVFSByName(vfsName);
		VNode toCopy = vfs.get(oldPathName);
		VNode whereToCopy = vfs.get(newPathName);
		if(!whereToCopy.isDirectory())
			throw new NoSuchDirectoryException("\""+newPathName+"\" is not a directory");
		toCopy.copy((Directory) whereToCopy);
	}
	
	
	/**
	 * copies a VNode on a virtual file system.
	 * If the VNode already exists it overwrites it.
	 * It is similar to the "cp" command on Unix systems.
	 * @param vfsName virtual file system name
	 * @param oldPathName path name of the VNode to copy
	 * @param newPathName new path name of the directory where to copy
	 * @throws NoSuchDirectoryException if a path name is not valid
	 * @throws NoSuchVirtualFileSystemException if no virtual file system correspond to the given name argument
	 * @throws VFSOperationFailException if operation fails
	 * @throws NoFreeSpaceLeftException if the virtual file system does not have enough space left
	 */
	public void copyAndOverwrite(String vfsName, String oldPathName, String newPathName){
		VirtualFileSystem vfs = getVFSByName(vfsName);
		VNode toCopy = vfs.get(oldPathName);
		VNode whereToCopy = vfs.get(newPathName);
		if(!whereToCopy.isDirectory())
			throw new NoSuchDirectoryException("\""+newPathName+"\" is not a directory");
		((Directory) whereToCopy).get(toCopy.name).remove();
		toCopy.copy((Directory) whereToCopy);
	}
	

	/**
	 * removes a VNode from a virtual file system.
	 * It is similar to the "rm" command on Unix systems.
	 * @param vfsName virtual file system name
	 * @param pathName path name of the VNode to remove
	 * @throws NoSuchFileOrDirectoryException if pathName is not valid
	 * @throws NoSuchVirtualFileSystemException if no virtual file system correspond to the given name argument
	 * @throws VFSOperationFailException if operation fails
	 */
	public void remove(String vfsName, String pathName){
		VirtualFileSystem vfs = getVFSByName(vfsName);
		VNode toRemove = vfs.get(pathName);
		if(vfs.equals(workingVFS) && toRemove.equals(vfs.getWorkingDirectory()))
			changeDir("","..");
		toRemove.remove();
	}
	
	/**
	 * creates a new virtual file system of the a given dimension
	 * @param vfsName name of the virtual file system to create
	 * @param dim a long value representing the dimension (in bytes) of the new virtual file system
	 * @throws SizeNotAllowedException if space dimension is too big
	 * @throws VFSOperationFailException if formatting operation fails
	 * @see VirtualFileSystem
	 */
	public void createVFS(String vfsName,long dim){
		vfsDataBase.add(new VirtualFileSystem(vfsName,dim));
	}
	
	/**
	 * deletes a virtual file system
	 * @param vfsName name of the virtual file system to delete
	 * @throws VFSOperationFailException if operation fails
	 * @throws NoSuchVirtualFileSystemException if no virtual file system correspond to the given name argument
	 * @see VirtualFileSystem
	 */
	public void removeVFS(String vfsName){
		VirtualFileSystem vfs = getVFSByName(vfsName);
		vfs.delete();
		vfsDataBase.remove(vfs);
	}
	
	/**
	 * formats a virtual file system
	 * @param vfsName name of the virtual file system to format
	 * @throws NoSuchVirtualFileSystemException if no virtual file system correspond to the given name argument
	 * @throws VFSOperationFailException if operation fails
	 * @see VirtualFileSystem
	 */
	public void formatVFS(String vfsName){
		VirtualFileSystem vfs = getVFSByName(vfsName);
		vfs.format();
	}
	
	/**
	 * imports a file/directory from the host file system to a virtual file system
	 * @param hostPathName path name of the file/directory to import from the host file system
	 * @param vfsName virtual file system where to import
	 * @param pathName path name of the directory where to import on the virtual file system
	 * @throws NoFreeSpaceLeftException if the virtual file system does not have enough space left
	 * @throws NameAlreadyExistsException if there's a VNode with the same name
	 * @throws NoSuchDirectoryException if the path name is not valid
	 * @throws NoSuchVirtualFileSystemException if no virtual file system correspond to the given name argument
	 * @throws VFSOperationFailException if operation fails
	 * @see VirtualFileSystem
	 */
	public void importToVFS(String hostPathName, String vfsName, String pathName){
		VirtualFileSystem vfs = getVFSByName(vfsName);
		vfs.importFromHostFileSystem(hostPathName, pathName);
	}
	
	/**
	 * exports a virtual file system to the host file system
	 * @param vfsName the name of the virtual file system to export
	 * @param pathName path name on the host file system of the directory where to export
	 * @throws NoSuchVirtualFileSystemException if no virtual file system correspond to the given name argument
	 * @throws VFSOperationFailException if operation fails
	 * @see VNode
	 */
	public void exportVFS(String vfsName, String pathName){
		VirtualFileSystem vfs = getVFSByName(vfsName);
		vfs.rootDirectory.rename(vfs.name);
		vfs.rootDirectory.exportToHostFileSystem(pathName);
		vfs.rootDirectory.rename("");
	}
	
	/**
	 * returns a string showing the free/occupied space of a virtual file system
	 * @param vfsName name of the virtual file system
	 * @return toString() value of the virtual file system object
	 * @throws NoSuchVirtualFileSystemException if no virtual file system correspond to the given name argument
	 * @see VirtualFileSystem
	 */
	public String free(String vfsName){
		VirtualFileSystem vfs = getVFSByName(vfsName);
		return vfs.toString();
	}
	
	/**
	 * returns a String containing all the content of a virtual file system formatted in a tree structure.
	 * @param vfsName the name of the virtual file system
	 * @return getTree() value of the virtual file system object
	 * @throws NoSuchVirtualFileSystemException if no virtual file system correspond to the given name argument
	 * @see VirtualFileSystem
	 */
	public String tree(String vfsName){
		VirtualFileSystem vfs = getVFSByName(vfsName);
		return vfs.getTree();
	}
	
	/**
	 * finds all files with a given name in a virtual file system
	 * @param vfsName name of the virtual file system
	 * @param fileName file's name to look for
	 * @return a string containing all absolute path names of the files found
	 * @throws NoSuchVirtualFileSystemException if no virtual file system correspond to the given name argument
	 * @throws NoSuchFileOrDirectoryException if no file is found
	 */
	public String find(String vfsName,String fileName){
		VirtualFileSystem vfs = getVFSByName(vfsName);
		String pathNamesFound = "";
		
		for(VNode found: vfs.find(fileName)){
			if(found.isFile())
				pathNamesFound += found.getAbsolutePathName() + "\n";
		}
		
		return pathNamesFound;
	}
	
	/**
	 * sets the current working virtual file system and returns the prompt string in the following format: 
	 * "(time) (working virtual file system) :/(working directory)/ "
	 * @param vfsName name of the virtual file system 
	 * @return the prompt string
	 * @throws NoSuchVirtualFileSystemException if no virtual file system correspond to the given name argument
	 */
	public String getPrompt(String vfsName){
		if(vfsName.equals("-off"))
			return "";
		VirtualFileSystem vfs = getVFSByName(vfsName);
		workingVFS = vfs;
		Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
		return sdf.format(cal.getTime())+" "+workingVFS.getWorkingDirectory().getAbsolutePathName()+" ";
	}
	
	/**
	 * returns the prompt string in the following format: 
	 * "(time) (working virtual file system) :/(working directory)/ "
	 * @return the prompt string
	 */
	public String getPrompt(){
		return getPrompt(workingVFS.name);
	}

	
	/**
	 * saves all virtual file systems.
	 */
	public void saveAllVFS() {
		for(VirtualFileSystem vfs: vfsDataBase){
			vfs.save();
		}
	}
	
}
