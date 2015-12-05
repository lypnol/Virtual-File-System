package core;

import exceptions.NoFreeSpaceLeftException;
import exceptions.VFSOperationFailException;
import tree.SimpleTreeNode;

/**
 * An absract representation of a node in the virtual file system tree. 
 * It can be a {@link File} or a  {@link Directory}.
 * @author ayoub
 */
public abstract class VNode extends SimpleTreeNode{
	
	/* ----------------------------attributes---------------------------- */
	
	private static final long serialVersionUID = 1353327752767596463L;
	
	/** the absolute path in the virtual file system */
	protected String absolutePath;
	/** the name of a node in the virtual file system*/
	protected String name;
	/** the type of a node in the virtual file system
	 * "file" or "directory" */
	protected String type;
	/** the size (in bytes) of a node in the virtual file system*/
	protected long size;
	/** the virtual disk object that contains the node */
	protected VirtualFileSystem virtualDisk;
	
	/* ----------------------------constructors---------------------------- */
	
	/**
	 * creates a VNode in the virtual file system of size 0 B with the given argument as name.
	 * @param name of the VNode
	 */
	protected VNode(String name){
		this.name = name;
		absolutePath = null;
		size = 0;
	}
	
	/* ----------------------------getters---------------------------- */
	
	/**
	 * returns the absolute path string in the virtual file system where it belongs. 
	 * @return the value of absolutePath
	 */
	public String getAbsolutePath() {
		if(isRoot())
			return getVirtualDisk().name+":"+VirtualFileSystem.separator;
		return absolutePath;
	}
	
	/**
	 * returns the name of node.
	 * @return the value of name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * returns the absolute pathname string in the virtual file system where it belongs.
	 * @return the value of absolutePath name
	 */
	public String getAbsolutePathName(){
		if(isRoot())
			return getVirtualDisk().name+":"+VirtualFileSystem.separator;
		return absolutePath+name+((isDirectory())?VirtualFileSystem.separator:"");
	}
	
	/**
	 * returns the size of node in bytes.
	 * @return the value of size
	 */
	public long getSize() {
		return size;
	}
	
	/**
	 * returns the type of node (file or directory)
	 * @return the value of type
	 */
	public String getType(){
		return type;
	}
	
	/**
	 * returns the directory that contains the node in the virtual file system where it belongs.
	 * @return the parent object of the node.
	 */
	public Directory getParent(){
		return (Directory) parent;
	}
	
	/**
	 * returns the virtual disk that contains the node.
	 * @return virtualDisk
	 */
	public VirtualFileSystem getVirtualDisk(){
		return virtualDisk;
	}
	
	/**
	 * checks if VNode is root of tree.
	 * @return parent == null
	 */
	public boolean isRoot(){
		return parent==null;
	}
	
	/* ----------------------------setters---------------------------- */
	
	
	/**
	 * sets the name of the VNode to the new one given as argument
	 * @param name the new name of the VNode
	 */
	public void rename(String name) {
		this.name = name;
		refreshAbsolutePathToAllChildren();
	}
	
	/**
	 * adds the given argument to the VNode's size value as well as to all its parents
	 * @param size a long value to add
	 */
	protected void addSize(long size){
		if(isRoot()) return;
		this.size += size;
		VNode p =  getParent();
		if(p!=null)
			p.addSize(size);
	}

	
	/* ----------------------------VFS operations---------------------------- */
	
	/**
	 * moves the VNode to another directory
	 * @param newDirectory to Directory where the VNode will be moved
	 * @throws VFSOperationFailException if moving data blocks fails
	 */
	abstract public void move(Directory newDirectory);
	
	/**
	 * copies the VNode to another directory
	 * @param newDirectory to Directory where the VNode will be moved
	 * @throws NoFreeSpaceLeftException if the virtual disk doesn't have enough space
	 * @throws VFSOperationFailException if copying data blocks fails
	 */
	abstract public void copy(Directory newDirectory);
	
	/**
	 * exports the VNode to the host file system
	 * @param exportPath the path to where the VNode will be exported on the host file system 
	 * @throws VFSOperationFailException if operation fails
	 */
	abstract public void exportToHostFileSystem(String exportPath);
	
	/**
	 * removes the VNode from the virtual file system where it belongs.
	 * every file's data is lost.
	 */
	abstract public void remove();
	
	/**
	 * checks if VNode is File
	 * @return true if VNode is File, false if not
	 */
	public boolean isFile(){
		return type.equals("file");
	}
	
	/**
	 * checks if VNode is Directory
	 * @return true if VNode is Directory, false if not
	 */
	public boolean isDirectory(){
		return type.equals("directory");
	}
		
	
	/* ----------------------------useful methods---------------------------- */
	
	/**
	 * refershes the absolute paths of all children.
	 */
	protected void refreshAbsolutePathToAllChildren(){
		for(SimpleTreeNode c : getChildrenList()){
			VNode child = (VNode) c;
			child.absolutePath = getAbsolutePathName();
			if(child.isDirectory())
				((Directory)child).refreshAbsolutePathToAllChildren();
		}
	}
	
	/**
	 * returns a String containing all the content of the VNode formatted in a tree structure.
	 * @return a String containing the tree structure starting from the receiver's node in the virtual file system
	 */
	public String printAllContent(){
		String s="";
		VNode currentParent = this;
		while(currentParent.getParent()!=null){
			s+="  ";
			currentParent = (VNode) currentParent.getParent();
		}
		
		s+=this.toString()+"\n";

		for(SimpleTreeNode c : this.getChildrenList()){
			s+=((VNode) c).printAllContent();
		}
		return s;
	}
	
	@Override
	/**
	 * two VNodes are equal if they have the same name and the same absolute path
	 */
	public boolean equals(Object o){
		if(o instanceof VNode)
			return getAbsolutePathName().equals(((VNode)o).getAbsolutePathName());
		return false;
	}
	
	@Override
	public String toString(){
		if(isRoot())
			return getVirtualDisk().name+":";

		return name + ((isDirectory())?VirtualFileSystem.separator:"");
	}

}
