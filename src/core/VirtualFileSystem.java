package core;

import java.util.List;

import exceptions.*;


/**
 * A representation of a virtual file system. This class also represents Virtual Disks as we consider that a Virtual File System is a Virtual Disk. 
 * A VirtualFileSystem object contains a tree structure where all its content is stored.
 * This object is serializable so that it can be saved on the host file system.
 * The "virtual disk file" is refering to the file on the host file system that will represent a virtual disk.
 * This file is formatted in the following way:
 * 	- a 12 bytes header containing:
 * 		- an integer that stores the size of the serialized VirtualFileSystem object.
 * 		- a long that stores the position of the first byte that will contain this object serialization in the same file.
 * 	- a space reserved to data blocks of 1 KB each. These blocks will contain files contents. When a file is stored on the virtual disk, its content 
 * 	is divided into blocks of 1016 bytes each. these 1016 bytes blocks as well as there positions (8 bytes) are stored on the 1 KB blocks on the virtual 
 * 	disk file in different positions. A block of the virtual file system is formatted in the following way: 1016 bytes of a file's content + 8 bytes 
 * 	storing a long value of the position of the next block of data that completes the file's content.
 * 	the number of blocks depends on how much space does a virtual disk have. Which is defined at its construction.
 * 	- the rest of the virtual disk file contains the serialized VirtualFileSystem object data.
 * 
 * @author Omar
 */
public class VirtualFileSystem implements java.io.Serializable{

	private static final long serialVersionUID = 5311338851615641725L;

	/* ----------------------------constants of the virtual file system---------------------------- */
	
	/** maximum size of a virtual disk*/
	public static final long MaxSizeAllowed = 1024*1024*20;	// 20 MB
	/** path separator of the virtual file system */
	/* absolute paths in this virtual file system are formatted in the following way:
	 *            /<VirtualDisk>:/<Directory>/<Directory>/.../<Directory>/
	 */
	public static final String separator = "/";			
	/** the root path of the virtual file system*/
	public static final String rootPath = separator;
	/** the separator string of the host file system*/
	public static final String HFSSeparator = java.nio.file.FileSystems.getDefault().getSeparator();
	/** virtual disk file extension*/
	public static final String VDFileExtension = ".vd";				
	/** the size of a Block of data on the virtual disk*/
	/* each block has the following format:  (a total size of 1KB)
	 * +--------------------------------------------------------------------------------------------------+
	 * |                       1016 bytes of data                        | position of next block of data |
	 * +--------------------------------------------------------------------------------------------------+
	 *                   BlockSize - long size = 1016 bytes                      long size = 8 bytes
	 *                
	 */
	public static final int BlockSize = 1024;   						// 1 KB	
	
	/** size of a long value (8 bytes) */
	public static final int longSize = Long.SIZE/8;						// 8 B
	/** size of an integer value (4 bytes) */	
	public static final int intSize = Integer.SIZE/8;					// 4 B
	/** the space reserved to data in a block*/
	public static final int DataSizeInBlock = BlockSize - longSize;		// 1016 B
	
	/** position of the first byte of the first data block on a virtual disk file*/
	/* 
	 * the 4 first bytes are reserved to store the VirtualDisk object serialized data length (integer type).
	 * the next 8 bytes are reserved to store the position (long type) where the VirtualDisk object serialization is stored.
	 * these 12 bytes are the header of the file. The first data block is then located at the following position:
	 **/
	public final static long firstBlockPosition = intSize + longSize;
	
	/* ----------------------------attributes---------------------------- */
	
	/** name of the virtual file system */
	public final String name;
	/** free space on the virtual disk */
	private long freeSpace;
	/** occupied space on the virtual disk */
	private long occupiedSpace;
	/** total space on the virtual disk */
	public final long totalSpace;
	/** array of boolean to keep track of the status (free/occupied) of a data block */
	private boolean[] freeBlocks; 
	/** path to the file where the virtual disk will be serialized on the host file system */
	public final String VDFileName;
	/** id of the virutal disk */
	public final int id;
	/** position of the first byte that will contain object serialized data on the virtual disk file */
	private final long objectDataPosition;
	/** virtual disks ids*/
	public static int VirtualDiskIDs = 0;			
	
	/** root directory */
	public final Directory rootDirectory;
	/** current working directory */
	private Directory workingDirectory;
	
	/* ----------------------------constructors/formatting method---------------------------- */
	
	/**
	 * creates a virtual file system.
	 * @param name name of the virtual file system
	 * @param space dimension (in bytes) of the virtual file system
	 * @throws VFSOperationFailException if formatting operation fails
	 * @throws SizeNotAllowedException if space dimension is too big
	 */
	public VirtualFileSystem(String name, long space){
		if(space > MaxSizeAllowed){
			throw new SizeNotAllowedException();
		}
		this.name = name;
		id = VirtualDiskIDs;
		VirtualDiskIDs++;
		freeBlocks = new boolean[((int)(space/DataSizeInBlock)) +1];
		totalSpace = ((long)freeBlocks.length)*DataSizeInBlock;
		objectDataPosition = firstBlockPosition + freeBlocks.length*BlockSize;
		VDFileName = name+VDFileExtension;
		rootDirectory = new Directory("");
		rootDirectory.virtualDisk = this;
		rootDirectory.absolutePath = rootPath;
		format();
	}
	
	/**
	 * creates a virtual file system with default name.
	 * @param space dimension (in bytes) of the virtual file system
	 * @throws VFSOperationFailException if formatting operation fails
	 * @throws SizeNotAllowedException if space dimension is too big
	 */
	public VirtualFileSystem(long space){
		if(space > MaxSizeAllowed){
			throw new SizeNotAllowedException();
		}
		id = VirtualFileSystem.VirtualDiskIDs;
		VirtualFileSystem.VirtualDiskIDs++;
		name = "VD"+id;
		freeBlocks = new boolean[((int)(space/DataSizeInBlock)) +1];
		totalSpace = ((long)freeBlocks.length)*DataSizeInBlock;
		objectDataPosition = firstBlockPosition + freeBlocks.length*BlockSize;
		VDFileName = name+VDFileExtension;
		rootDirectory = new Directory("");
		rootDirectory.virtualDisk = this;
		rootDirectory.absolutePath = rootPath;
		format();
	}
	
	/**
	 * loads an already existing virtual file system from the host file system
	 * @param fileName path name of the virtual file system already existing on the host file system
	 * @throws VFSOperationFailException if virtual disk file is not found or not formatted correctly
	 */
	public VirtualFileSystem(String fileName){
		java.io.RandomAccessFile vdFile = null;
		java.io.ObjectInputStream objectStream = null;
		try {
			// -----1. storing object data in a byte array:
			
			vdFile = new java.io.RandomAccessFile(fileName + VDFileExtension,"r");
			// The first 4 bytes of the file are reserved to store the VirtualDisk object serialized data length
			vdFile.seek(0);
			byte[] objectData = new byte[vdFile.readInt()];
			
			// the next 8 bytes are reserved to store the position (long type) of the first byte of the serialized VirtualDisk object data.
			
			long startingPosition = vdFile.readLong();
			
			// reading serialized VirtualDisk object data
			
			vdFile.seek(startingPosition);
			
			int bytesRead = 0;
			while(bytesRead < objectData.length){
				objectData[bytesRead]=(byte) vdFile.read();
				bytesRead++;
			}
			
			java.io.ByteArrayInputStream bytesStream = new java.io.ByteArrayInputStream(objectData);
			
			// -----2. deserializing object data :
			
			objectStream = new java.io.ObjectInputStream(bytesStream);
			VirtualFileSystem vd = (VirtualFileSystem) objectStream.readObject();
			
			// -----3. constructing object :
			
			id = VirtualFileSystem.VirtualDiskIDs;
			VirtualFileSystem.VirtualDiskIDs++;
			this.name = vd.name;
			this.totalSpace = vd.totalSpace;
			this.freeSpace = vd.freeSpace;
			this.occupiedSpace = vd.occupiedSpace;
			this.rootDirectory = vd.rootDirectory;
			this.freeBlocks = vd.freeBlocks;
			this.objectDataPosition = vd.objectDataPosition;
			this.workingDirectory = rootDirectory;
			this.VDFileName = vd.VDFileName;
			vd.finalize();

		} catch(java.io.FileNotFoundException e){
			throw new VFSOperationFailException("virtual disk file \""+fileName+"\" is not found.");
		} catch (java.io.IOException io) {
			throw new VFSOperationFailException("virtual disk file \""+fileName+"\" is not formatted correctly.\n"+io.getMessage());
		} catch (java.lang.ClassNotFoundException ce) {
			throw new VFSOperationFailException("virtual disk file \""+fileName+"\" is not formatted correctly.");
		} catch (Throwable e) {
			e.printStackTrace();
			throw new VFSOperationFailException("could not destroy loaded vd.");
			
		} finally{
			try{
				if(objectStream!=null) objectStream.close();
				if(vdFile!=null) vdFile.close();
			} catch (java.io.IOException io){
				throw new VFSOperationFailException(io.getMessage());
			}
		}
	}
	
	/**
	 * formats the virtual disk file. It makes all the data blocks empty and free.  
	 * @throws VFSOperationFailException if operation fails.
	 */
	public void format(){
		java.io.RandomAccessFile vdFile = null;
		try {
			vdFile = new java.io.RandomAccessFile(VDFileName, "rw");
			vdFile.seek(VirtualFileSystem.firstBlockPosition);
			
			//filling all data blocks with zeros and assigning next block positions to -1
			for(int i=0;i<freeBlocks.length;i++){
				byte[] zeros = new byte[VirtualFileSystem.DataSizeInBlock];
				vdFile.write(zeros);
				vdFile.writeLong(-1L);
				freeBlocks[i] = true;	//setting block free
			}
			rootDirectory.remove();
			workingDirectory = rootDirectory;
			freeSpace = totalSpace;
			occupiedSpace = 0L;
		} catch (java.io.IOException e) {
			throw new VFSOperationFailException("Cannot format virtual disk file \""+VDFileName+"\"");
		} finally{
			try{
				if(vdFile!=null) vdFile.close();
			} catch (java.io.IOException io){
				throw new VFSOperationFailException(io.getMessage());
			}
		}
		save();
	}
	
	
	/* ----------------------------getters---------------------------- */
	
	
	/**
	 * returns the current working directory of the virtual file system
	 * @return workingDirectory object
	 */
	public Directory getWorkingDirectory(){
		return workingDirectory;
	}
	
	/**
	 * returns the number of bytes that are free on the virtual disk
	 * @return freeSpace value
	 */
	public long getFreeSpace() {
		return freeSpace;
	}
	
	/**
	 * returns the number of bytes that are occupied on the virtual disk
	 * @return occupiedSpace value
	 */
	public long getOccupiedSpace() {
		return occupiedSpace;
	}
	
	/**
	 * returns the next free block position on the virtual disk file
	 * @return a long value containing the next free block position (-1 if all blocks are occupied)
	 */
	public long getNextFreeBlockPosition(){
		for(int i=0;i<freeBlocks.length;i++){
			if(freeBlocks[i]==true)
				return getBlockPositionOnFile(i);
		}
		return -1L;
	}
	
	/**
	 * checks if a data block is free
	 * @param position the position of the block on the virtual disk file
	 * @return a boolean to check if the block is free
	 */
	public boolean isFreeBlock(long position){
		return freeBlocks[getBlockIdx(position)];
	}
	
	/**
	 * returns the long value containing the position of the first byte, on the virtual disk file, corresponding to the data block index given as arguement
	 * @param blockIdx index of a data block on the virtual file system
	 * @return the position on the virtual disk file of the corresponding data block
	 */
	private static long getBlockPositionOnFile(int blockIdx){
		return (long) (blockIdx)*VirtualFileSystem.BlockSize+VirtualFileSystem.firstBlockPosition;
	}
	
	/**
	 * returns the index  of the data block corresponding to the position, on the virtual disk file, given as argument
	 * @param blockPosition the position, on the virtual disk file, of the corresponding data block
	 * @return index of a data block on the virtual file system
	 */
	private static int getBlockIdx(long blockPosition){
		return (int) ((blockPosition - VirtualFileSystem.firstBlockPosition)/VirtualFileSystem.BlockSize);
	}
	
	
	/* ----------------------------setters---------------------------- */
	
	/**
	 * sets the data block, which position is given in the argument, as occupied and changes the occupied/free space of the virtual disk.
	 * @param position position of the data block on the virtual disk file
	 */
	public void setOccupiedBlock(long position){
		System.out.println(" -> occupy block");
		freeBlocks[getBlockIdx(position)] = false;
		System.out.println(" --> original occupied space: "+this.occupiedSpace);
		System.out.println(" --> original free space:     "+this.freeSpace);
		this.occupiedSpace = this.occupiedSpace + ((long)DataSizeInBlock);
		System.out.println(" --> new occupied space: "+this.occupiedSpace);
		this.freeSpace = this.freeSpace - ((long)DataSizeInBlock);
		System.out.println(" --> new free space:     "+this.freeSpace);
	}
	
	/**
	 * sets the data block, which position is given in the argument, as free and changes the occupied/free space of the virtual disk.
	 * @param position position of the data block on the virtual disk file
	 */
	public void setFreeBlock(long position){
		freeBlocks[getBlockIdx(position)] = true;
		System.out.println(" -> Free block");
		System.out.println(" --> original occupied space: "+this.occupiedSpace);
		System.out.println(" --> original free space:     "+this.freeSpace);
		this.occupiedSpace = this.occupiedSpace - ((long)DataSizeInBlock);
		System.out.println(" --> new occupied space: "+this.occupiedSpace);
		this.freeSpace = this.freeSpace + ((long)DataSizeInBlock);
		System.out.println(" --> new free space:     "+this.freeSpace);
	}
	
	
    /* ----------------------------VFS operations---------------------------- */
	
	
	/** 
	 * serializes the virtual file system at the end of the virtual disk file.
	 * this operation saves the current state of the virtual disk.
	 * @throws VFSOperationFailException if the operation fails.
	 * */
	public void save(){
		java.io.RandomAccessFile vdFile = null;
		java.io.ByteArrayOutputStream byteArrayOut = null;
		java.io.ObjectOutputStream objectDataOut = null;
		
		try{
			vdFile = new java.io.RandomAccessFile(VDFileName, "rw");
		
			byteArrayOut = new java.io.ByteArrayOutputStream();
			objectDataOut = new java.io.ObjectOutputStream(byteArrayOut);
			
			objectDataOut.writeObject(this);
			
			byte[] objectData = byteArrayOut.toByteArray();
			
			if(byteArrayOut.size()>objectData.length){
				System.out.println("hey!!");
			}
			
			vdFile.seek(0);
			
			// writing virtual disk file header
			vdFile.writeInt(objectData.length);
			vdFile.writeLong(objectDataPosition);
			
			// writing virtual file system data at the end of the virtual disk file
			vdFile.seek(objectDataPosition);
			
			for(int i=0;i<objectData.length;i++){
				vdFile.write(objectData[i]);
			}
		
		} catch(java.io.IOException e){
			throw new VFSOperationFailException("cannot save virtual file system on "+VDFileName);
		} finally{
			try {
				if(vdFile!=null) vdFile.close();
				if(byteArrayOut!=null) byteArrayOut.close();
				if(objectDataOut!=null) objectDataOut.close();
			} catch (java.io.IOException e) {
				throw new VFSOperationFailException(e.getMessage());
			}
		}
		
	}
	
	/**
	 * deletes the virtual disk file from the host file system.
	 * this operation deletes all the virtual file system content including data blocks and oject serialization.
	 * all data is lost.
	 * @throws VFSOperationFailException if operation fails.
	 */
	public void delete(){
		try{
			(new java.io.File(VDFileName)).delete();
		} catch (SecurityException e){
			throw new VFSOperationFailException("couldn't delete virtual disk file from host file system");
		}
	}
	
	/**
	 * returns the VNode corresponding to the path name given as arguement
	 * @param pathName path name of the VNode in the virtual file system. It can be absolute or relative to working directory.
	 * @throws NoSuchFileOrDirectoryException if no VNode found
	 * @return the VNode that corresponds to the path name
	 */
	public VNode get(String pathName){
		if(pathName.equals(".") || pathName.equals("."+separator))
			return workingDirectory;
		if(pathName.equals("..") || pathName.equals(".."+separator))
			return ((workingDirectory.getParent()!=null)?workingDirectory.getParent():workingDirectory);
			
		
		if(pathName.equals(separator) || pathName.equals(name+":"+separator))
			return rootDirectory;
		
		if(Path.isAbsolutePath(pathName)){
			if(!Path.getVfsName(pathName).equals(name))
				throw new NoSuchFileOrDirectoryException("\""+pathName+"\" is not reachable from this virtual disk \""+name+"\"");
			return rootDirectory.get(pathName.substring(name.length()+1+separator.length()));
		}
		if(pathName.startsWith(separator))
			return rootDirectory.get(pathName.substring(separator.length()));
		else 
			return workingDirectory.get(pathName);
	}
	
	/**
	 * navigate through the virtual file system. it changes the current working directory.
	 * @param pathName the navigation target path name. It can be an absolute path or a relative path to the current working directory.
	 * @throws NoSuchFileOrDirectoryException if path is not valid
	 */
	public void navigate(String pathName){
		if(!pathName.endsWith(separator))
			pathName = pathName + separator;
		
		workingDirectory = (Directory) get(pathName);
	}
	
	/**
	 * search for flies/directories linked to the current working directory
	 * @param name of the file/directory to search for 
	 * @throws NoSuchFileOrDirectoryException if no VNode found
	 * @return a list of found files/directories
	 */
	public List<VNode> find(String name){
		return workingDirectory.find(name);
	}
	
	/**
	 * creates a new directory at the specified pathName
	 * @param pathName path + name of the new directory
	 * @throws NoSuchFileOrDirectoryException if path name is not valid
	 * @throws VFSOperationFailException if saving operation fails
	 * @return a reference to the Directory object created
	 */
	public Directory createDirectory(String pathName){
		VNode whereToCreate = get(Path.getPathFromPathName(pathName));
		String name = Path.getNameFromPathName(pathName);
		
		if(whereToCreate.isFile())
			throw new NoSuchDirectoryException("\""+whereToCreate.getAbsolutePathName()+"\" is not a directory");
		
		Directory newDirectory = new Directory(name);

		((Directory)whereToCreate).addVNode(newDirectory);
		
		return newDirectory;
	}
	
	/**
	 * creates a new file at the specified pathName
	 * @param pathName path + name of the new file
	 * @param size size in bytes of the new file
	 * @throws NoSuchFileOrDirectoryException if path name is not valid
	 * @throws NoFreeSpaceLeftException if virtual disk does not have enough space
	 * @throws VFSOperationFailException if operation fails
	 * @return a reference to the File object created
	 */
	public File createFile(String pathName, long size){
		VNode whereToCreate = get(Path.getPathFromPathName(pathName));
		String name = Path.getNameFromPathName(pathName);
	
		if(whereToCreate.isFile())
			throw new NoSuchDirectoryException("\""+whereToCreate.getAbsolutePathName()+"\" is not a directory");
		
		java.io.RandomAccessFile vdFile = null;
		File newFile = new File(name,size);
		((Directory)whereToCreate).addVNode(newFile);
		
		
		// allocating data blocks for the new file
		try{
			vdFile = new java.io.RandomAccessFile(VDFileName, "rw");
			long occupiedBytes = 0;
			
			long currentBlockPosition = getNextFreeBlockPosition();
			newFile.setFirstBlockPosition(currentBlockPosition);
			
			while(occupiedBytes < size){
				setOccupiedBlock(currentBlockPosition);
				occupiedBytes += VirtualFileSystem.DataSizeInBlock;
				
				long nextPosition;
				if(occupiedBytes < size)
					nextPosition = getNextFreeBlockPosition();
				else
					nextPosition = -1L;
				
				vdFile.seek(currentBlockPosition + VirtualFileSystem.DataSizeInBlock);
				vdFile.writeLong(nextPosition);

				currentBlockPosition = nextPosition;
			}
			
		} catch(java.io.IOException e){
			throw new VFSOperationFailException("cannot create new file on virtual disk : \""+this.name+"\"");
		} finally{
			try {
				if(vdFile!=null) vdFile.close();
			} catch (java.io.IOException e) {
				throw new VFSOperationFailException(e.getMessage());
			}
		}
		
		return newFile;
	}
	
	/**
	 * imports a file/directory from the host file system to the virtual file system
	 * @param importPathName path + name of the file/directory to import
	 * @param pathName path name of the directory where to import on the virtual file system
	 * @throws NoSuchFileOrDirectoryException if path name is not valid
	 * @throws NoFreeSpaceLeftException if virtual disk does not have enough space
	 * @throws VFSOperationFailException if operation fails
	 * @return a reference to the VNode object imported
	 */
	public VNode importFromHostFileSystem(String importPathName, String pathName){
		if(get(pathName).isFile())
			throw new NoSuchDirectoryException("\""+get(pathName).getAbsolutePathName()+"\" is not a directory");
		Directory whereToCreate = (Directory) get(pathName);
		VNode created = null;
		
	
		java.io.File toImport = new java.io.File(importPathName);
		if(!toImport.exists()) throw new NoSuchFileOrDirectoryException(importPathName+" does not exist on the host file system");
		if(VirtualFileSystem.getHFSDirectorySize(toImport)>getFreeSpace()) throw new NoFreeSpaceLeftException();
		
		if(toImport.isDirectory()) {
			// creating new directory
			created = createDirectory(whereToCreate.getAbsolutePathName()+toImport.getName());
			
			//importing all directory's content to the new one created
			for(java.io.File f : toImport.listFiles()){
				importFromHostFileSystem(f.getAbsolutePath(),created.getAbsolutePathName());
			}
		} else{
			// creating new file
			created = createFile(whereToCreate.getAbsolutePathName()+toImport.getName(),toImport.length()); 
			java.io.RandomAccessFile vdFile = null;
			java.io.BufferedInputStream inStream = null;
		
			//importing data and storing it in file's blocks
			try{
				vdFile = new java.io.RandomAccessFile(VDFileName, "rw");
				inStream = new java.io.BufferedInputStream(new java.io.FileInputStream(toImport));
				
				long currentBlockPosition = ((File) created).getFirstBlockPosition();
				
				long bytesLeft = created.size;
				
				while(bytesLeft>0){
					// setting file cursor position
					vdFile.seek(currentBlockPosition);
	
					// reading data block
					byte[] bytesToImport;
				
					if(bytesLeft > VirtualFileSystem.DataSizeInBlock)
						bytesToImport = new byte[VirtualFileSystem.DataSizeInBlock];
	
					else
						bytesToImport = new byte[(int) bytesLeft];
	
					inStream.read(bytesToImport);
					
					// writing data block
					vdFile.write(bytesToImport);
					
					bytesLeft -= VirtualFileSystem.DataSizeInBlock;
					
					// getting next block position
					long nextBlockPosition = vdFile.readLong();
					
					// changing current position to next position
					currentBlockPosition = nextBlockPosition;
				}
			} catch(java.io.IOException e){
				throw new VFSOperationFailException("cannot import "+importPathName+" to virtual disk\n"+e.getMessage());
			} finally{
				try {
					if(vdFile!=null) vdFile.close();
					if(inStream!=null) inStream.close();
				} catch (java.io.IOException e) {
					throw new VFSOperationFailException(e.getMessage());
				}
			}
		}
		
		return created;
	}
	
	/**
	 * imports a file/directory from the host file system to the current working directory of the virtual file system
	 * @param importPathName path + name of the file/directory to import
	 * @throws NoFreeSpaceLeftException if virtual disk does not have enough space
	 * @throws VFSOperationFailException if operation fails
	 * @return a reference to the VNode object imported
	 */
	public VNode importFromHostFileSystem(String importPathName){
		return importFromHostFileSystem(importPathName,workingDirectory.getAbsolutePathName());
	}
	
	/* ----------------------------useful methods---------------------------- */
	
	/**
	 * returns a string containing an occupation bar of the virtual disk
	 * example: ####___ (59% occupied | 41% free)
	 * @return a string containing the occupation bar
	 */
	public String getSpaceOccupationBar(){
		int free = (int) (((double)(freeSpace/(double)(totalSpace)))*20);
		int occupied = (int) (((double)(occupiedSpace/(double)(totalSpace)))*20);
		String s="";
		for(int i=0;i<occupied;i++)
			s+="#";
		for(int i=0;i<free;i++)
			s+="_";
		
		return s;
	}
	
	/**
	 * returns the total size of a directory on the host file system
	 * @param directory on the host file system 
	 * @return total size of a directory on the host file system
	 */
	public static long getHFSDirectorySize(java.io.File directory) {
	    long length = 0;
	    if(directory.isFile()) return directory.length();
	    if(directory.isDirectory())
		    for (java.io.File file : directory.listFiles()) {
		        if (file.isFile())
		            length += file.length();
		        else
		            length += getHFSDirectorySize(file);
		    }
	    return length;
	}
	
	/**
	 * returns a String containing all the content of virtual disk formatted in a tree structure.
	 * @return a String containing the tree structure starting from root directory of the virtual file system
	 **/
	public String getTree(){
		return rootDirectory.printAllContent();
	}
	
	public static String getSimplifiedSize(long value){
		String unit = "B";
		long printedValue = value;
		
		if (printedValue>=1024*1024*1024) {unit = "GB"; printedValue = Math.round( ((double)printedValue)/(1024.0*1024*1024));}
		else if(printedValue>=1024*1024) {unit = "MB"; printedValue = Math.round( ((double)printedValue)/(1024.0*1024));}
		else if(printedValue>=1024) {unit = "KB"; printedValue = Math.round( ((double)printedValue)/(1024.0));}
		
		return ""+printedValue+" "+unit;
	}
	
	@Override
	public String toString(){
		String spacesToAdd = "  ";
		for(int i=0;i<name.length();i++)
			spacesToAdd += " ";
		
		return name+": total space ("+getSimplifiedSize(totalSpace)+")\n"+spacesToAdd+
		"free space ("+getSimplifiedSize(freeSpace)+") " + "occupied space ("+getSimplifiedSize(occupiedSpace)+")";
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof VirtualFileSystem){
			return name.equals(((VirtualFileSystem)o).name);
		}
		return false;
	}
	
}
