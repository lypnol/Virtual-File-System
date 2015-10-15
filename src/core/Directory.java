package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import tree.SimpleTreeNode;
import exceptions.*;

/**
 * A representation of a directory in the virtual file system.
 * A Directory object contains a list of files/directories.
 * 
 * @author Ayoub
 */
public class Directory extends VNode{
	
	/* ----------------------------attributes---------------------------- */
	
	private static final long serialVersionUID = 9104109343882935613L;
	
	/* ----------------------------constructors---------------------------- */
	
	/**
	 * Creates a directory.
	 * This constructor is package private becase a directory cannot exist on its own.
	 * It must be created through a virtual file system.
	 * @param name name of the directory
	 */
	Directory(String name) {
		super(name);
		allowsChildren = true;
		type = "directory";
	}
	
	/**
	 * adds a VNode to the directory content list. It modifies the size of all parents of the directory.
	 * @param child VNode to add as child in the tree structure
	 * @throws NameAlreadyExistsException if the directory already contains a VNode with the same name in its content list
	 * @throws NoFreeSpaceLeftException if the directory's virtual disk doesn't have enough free space to add this new VNode
	 */
	public void addVNode(VNode child){
		if(getVirtualDisk()!=null && getVirtualDisk().getFreeSpace()<child.size) throw new NoFreeSpaceLeftException();
		for(VNode c : getContentList()){
			if(c.name.equals(child.name))
				throw new NameAlreadyExistsException("\""+child.name + "\" already exists in \""+getAbsolutePathName()+"\"");
		}
		addSize(child.size);
		child.absolutePath = getAbsolutePathName();
		child.virtualDisk = this.virtualDisk;
		if(child.isDirectory()) child.refreshAbsolutePathToAllChildren();
		addChild(child);
	}
	
	
	void addChild(VNode child){
		super.addChild(child);
	}
	
	/* ----------------------------getters---------------------------- */
	
	/**
	 * returns the list of content in the directory (files/directories)
	 * @return list of VNodes included in the directory
	 */
	public List<VNode> getContentList(){
		List<VNode> childrenList = new Vector<VNode>();
		for(SimpleTreeNode c : super.getChildrenList()){
			childrenList.add((VNode)c);
		}
		return childrenList;
	}
	
	/**
	 * returns the number of files/directories included in the directory
	 * @return the size of the content list
	 */
	public int getContentCount(){
		return this.getChildrenCount();
	}
	
	/**
	 * returns a Directory included in the content list
	 * @param name name of the directory to return
	 * @throws NoSuchDirectoryException if no directory found
	 * @return the reference to the directory found if its found
	 */
	public Directory getDirectory(String name){
		for(VNode n : getContentList()){
			if(n.name.equals(name) && n.type.equals("directory"))
				return (Directory) n;
		}
		throw new NoSuchDirectoryException();
	}
	
	/**
	 * returns a {@link File} object included in the content list
	 * @param name name of the file to return
	 * @throws NoSuchFileException if no file found
	 * @return the reference to the file found if its found
	 */
	public File getFile(String name){
		for(VNode n : getContentList()){
			if(n.name.equals(name) && n.type.equals("file"))
				return (File) n;
		}
		throw new NoSuchFileException();
	}
	
	/**
	 * returns the VNode at the relative path given as argument starting from the receiver.
	 * @param relatifPathName the string path name of the VNode. It must be relative to the receiver directory.
	 * @throws NoSuchFileOrDirectoryException if no VNode found.
	 * @return a reference to the found VNode.
	 */
	public VNode get(String relatifPathName){
		VNode currentNode = this;
		boolean lookingForDirectory = false;
		
		if(relatifPathName.endsWith(VirtualFileSystem.separator)){
			relatifPathName = relatifPathName.substring(0, relatifPathName.length()-VirtualFileSystem.separator.length());
			lookingForDirectory = true;
		}
		
		String[] names = relatifPathName.split(VirtualFileSystem.separator);
		for(int i=0;i<names.length;i++){
			boolean found = false;
			if(names[i].equals(".")){
				found = true;
				continue;
			}
			if(names[i].equals("..")){
				if(currentNode.getParent()!=null)
					currentNode = currentNode.getParent();
				
				found = true;
				continue;
			}
			if(currentNode.isFile()) return null;
			List<VNode> children = ((Directory)currentNode).getContentList();
			for(VNode child: children){
				if(child.name.equals(names[i])){
					currentNode=child;
					found = true;
				}
			}
			if(!found) throw new NoSuchFileOrDirectoryException("\""+currentNode.getAbsolutePathName()+names[i] + "\" does not exist");
		}
		if(lookingForDirectory && !currentNode.isDirectory())
			throw new NoSuchFileOrDirectoryException("\""+currentNode.getAbsolutePathName()+ "\" is not a directory");
			
		return currentNode;
	}

	
	/** search for files/directories linked to the receiver's node in the virtual file system
	 * @param name of the file/directory to search for 
	 * @return a list of found files/directories
	 */
	public List<VNode> find(String name){
		List<VNode> results = new ArrayList<VNode>();
		for(VNode child : getContentList()){
			if(child.name.equals(name))
				results.add(child);
			if(child.isDirectory())
				results.addAll(((Directory)child).find(name));
		}
			
		return results;
	}
	
	/* ----------------------------VFS operations---------------------------- */
	
	@Override
	public void exportToHostFileSystem(String exportPath) {
		if(!exportPath.endsWith(VirtualFileSystem.HFSSeparator))
			exportPath = exportPath + VirtualFileSystem.HFSSeparator;
		java.io.File exportDirectory = new java.io.File(exportPath +name);
		if(exportDirectory.mkdirs()){
			for(VNode child : getContentList()){
				child.exportToHostFileSystem(exportDirectory.getAbsolutePath());
			}
		}
		else throw new VFSOperationFailException("Cannot export \""+this.getAbsolutePathName()+"\" to host file system \""
						+exportDirectory.getAbsolutePath()+"\"");
			
	}
	
	@Override
	public void copy(Directory newDirectory) {
		Directory cp = new Directory(name);
		newDirectory.addVNode(cp);
		for(VNode child : getContentList()){
			child.copy(cp);
		}
	}
	
	@Override
	public void move(Directory newDirectory){
		long tmp = size;
		addSize(-size);
		size = tmp;
		this.cutLinkToTree();
		newDirectory.addVNode(this);
		if(this.equals(getVirtualDisk().getWorkingDirectory()))
			getVirtualDisk().navigate(((getParent()==null)?getParent().getAbsolutePathName():VirtualFileSystem.separator));
	}

	@Override
	public void remove(){
		if(this.equals(getVirtualDisk().getWorkingDirectory()))
			getVirtualDisk().navigate("..");
		for(VNode child : getContentList()){
			child.remove();
		}
		this.cutLinkToTree();
	}
}
