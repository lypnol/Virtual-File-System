package core;


import exceptions.*;

/**
 * A representation of a file in the virtual file system.
 * This class doesn't contain any attributes where to store binary data. 
 * However, it contains a long value containing the position of the first block of data on the virtual disk file.
 * see {@link VirtualFileSystem}.
 * 
 * @author Ayoub
 */
public class File extends VNode{
	
	/* ----------------------------attributes---------------------------- */
	
	private static final long serialVersionUID = 5622625632590122446L;
	
	/**
	 * the position of the first block on the virtual disk file that will contain this file's data
	 */
	private long firstBlockPosition;
	
	/* each block has the following form:  (a total size of BlockSize)
	 * +--------------------------------------------------------------------------------------------------+
	 * |                       1016 bytes of data                        | position of next block of data |
	 * +--------------------------------------------------------------------------------------------------+
	 *                   BlockSize - long size = 1016 bytes                      long value = 8 bytes
	 *                
	 *  long size is given by Long.BYTES
	 */
	
	
	
	/* ----------------------------constructors---------------------------- */
	
	/**
	 * creates a File with the given argument as name without any data
	 * this constructor is package private because a file cannot exist on its own.
	 * It must be within a virtual file system.
	 * @param name name of the file
	 */
	
	File(String name) {
		super(name);
		allowsChildren = false;
		type = "file";
		firstBlockPosition = -1L;
	}
	
	/**
	 * creates a File with the given arguments as name and size
	 * this constructor is package private because a file cannot exist on its own.
	 * It must be within a virtual file system.
	 * @param name name of the file
	 * @param size size value (in bytes) of the file
	 */
	File(String name, long size){
		super(name);
		this.size = size;
		this.allowsChildren = false;
		type = "file";
		firstBlockPosition = -1L;
	}
	
	
	/* ----------------------------getters---------------------------- */
	
	/**
	 * returns the position of the first block on the virtual disk file that will contain this file's data.
	 * see {@link VirtualFileSystem}
	 * @return the value of firtBlockPosition
	 */
	public long getFirstBlockPosition() {
		return firstBlockPosition;
	}
	
	/**
	 * returns the file's extension
	 * @return file extension string included in its name
	 */
	public String getExtension(){
		String[] parts = name.split("\\.");
		if(parts.length>=2)
			return parts[parts.length-1];
		else
			return "";
	}
	
	/* ----------------------------setters---------------------------- */
	
	/**
	 * sets the position of the first block on the virtual disk file that will contain this file's data.
	 * this method is package private to prevent external use and data loss.
	 * @param firstBlockPosition long value.
	 */
	void setFirstBlockPosition(long firstBlockPosition) {
		this.firstBlockPosition = firstBlockPosition;
	}

	
	/* ----------------------------VFS operations---------------------------- */
	
	@Override
	public void exportToHostFileSystem(String exportPath){
		java.io.BufferedOutputStream fileOut = null;
		java.io.RandomAccessFile vdFile = null;
		
		// reading file's data and writing it to the output file block by block
		try{
			if(!exportPath.endsWith(VirtualFileSystem.HFSSeparator))
				exportPath = exportPath + VirtualFileSystem.HFSSeparator;
			
			fileOut = new java.io.BufferedOutputStream(new java.io.FileOutputStream(exportPath+name));
			vdFile = new java.io.RandomAccessFile(getVirtualDisk().VDFileName, "rw");
			
			long currentBlockPosition = firstBlockPosition; 	//first block position that contains file's data
			
			long bytesLeft = size;
			
			while(bytesLeft > 0){
				vdFile.seek(currentBlockPosition);			// changing file's cursor
				
				byte[] bytesToWrite;
				if(bytesLeft >= VirtualFileSystem.DataSizeInBlock)
					bytesToWrite = new byte[(int) (VirtualFileSystem.DataSizeInBlock)];
				else
					bytesToWrite = new byte[(int) (bytesLeft)];
				
				vdFile.read(bytesToWrite);					// reading block
				fileOut.write(bytesToWrite);				// writing block
				
				bytesLeft -= bytesToWrite.length;
				
				currentBlockPosition = vdFile.readLong();	// reading next position
			}

		} catch(java.io.IOException e){
			throw new VFSOperationFailException("Cannot export \""+getAbsolutePathName()+"\" to \""+exportPath+"\"");
		} finally {
			try{
				if(vdFile!=null) vdFile.close();
				if(fileOut!=null) fileOut.close();
			} catch (java.io.IOException io){
				throw new VFSOperationFailException(io.getMessage());
			}
		}
	}	

	@Override
	public void copy(Directory newDirectory){
		java.io.RandomAccessFile vdOutFile = null;
		java.io.RandomAccessFile vdInFile = null;
		if(size>getVirtualDisk().getFreeSpace()) throw new NoFreeSpaceLeftException();
		try{
			// -----1. creating new File 
			
			File cp = getVirtualDisk().createFile(newDirectory.getAbsolutePathName()+
					((!newDirectory.equals(newDirectory.getVirtualDisk().rootDirectory))?VirtualFileSystem.separator:"")+name, size);
			
			// -----2. copying file's content block by block
			
			vdInFile = new java.io.RandomAccessFile(getVirtualDisk().VDFileName,"rw");
			if(!newDirectory.getVirtualDisk().VDFileName.equals(newDirectory.getVirtualDisk().VDFileName))	// checks if the virtual file systems aren't the same
				vdOutFile = new java.io.RandomAccessFile(newDirectory.getVirtualDisk().VDFileName,"rw");
			else
				vdOutFile = vdInFile;
			
			long currentBlockPositionIn = this.firstBlockPosition;
			long currentBlockPositionOut = cp.firstBlockPosition;
			
			while(currentBlockPositionIn!=-1L){
				// ---copying a block of data
				vdInFile.seek(currentBlockPositionIn);			// changing infile's cursor
				
				byte[] bytesToWrite = new byte[(int) (VirtualFileSystem.DataSizeInBlock)];
				vdInFile.read(bytesToWrite);					// reading block
				currentBlockPositionIn = vdInFile.readLong();	// reading next position
				
				
				vdOutFile.seek(currentBlockPositionOut);   		// changing outfile's cursor 
				
				vdOutFile.write(bytesToWrite);					// writing block
				currentBlockPositionOut = vdOutFile.readLong();	// reading next position
			}
			
		} catch (java.io.IOException e){
			e.printStackTrace();
			throw new VFSOperationFailException("Cannot copy \""+getAbsolutePathName()+"\" to \""+newDirectory.getAbsolutePathName()+"\"");
		} finally {
			try{
				if(vdInFile!=null) vdInFile.close();
				if(vdOutFile!=null) vdOutFile.close();
			} catch (java.io.IOException io){
				throw new VFSOperationFailException(io.getMessage());
			}
		}
	}
	
	@Override
	public void move(Directory newDirectory){
		java.io.RandomAccessFile vdOutFile = null;
		java.io.RandomAccessFile vdInFile = null;
		try {
			if(!(this.getVirtualDisk().equals(newDirectory.getVirtualDisk()))){ // checks if the virtual file systems aren't the same
				// -----1. creating new file 
				
				File cp = getVirtualDisk().createFile(newDirectory.getAbsolutePathName()+
						((!newDirectory.equals(newDirectory.getVirtualDisk().rootDirectory))?VirtualFileSystem.separator:"")+name, size);
				
				// -----2. moving file's content block by block
				
				VirtualFileSystem vdOut = newDirectory.getVirtualDisk();
				VirtualFileSystem vdIn = this.getVirtualDisk();
				
				vdOutFile = new java.io.RandomAccessFile(vdOut.VDFileName,"rw");
				vdInFile = new java.io.RandomAccessFile(vdIn.VDFileName,"rw");
				
				long currentBlockPositionIn = this.firstBlockPosition;
				long currentBlockPositionOut = cp.firstBlockPosition;
				
				while(currentBlockPositionIn!=-1L){
					vdInFile.seek(currentBlockPositionIn);					// changing infile's cursor
		
					byte[] bytesToWrite = new byte[(int) (VirtualFileSystem.DataSizeInBlock)];
					
					vdInFile.read(bytesToWrite);							// reading block
					vdIn.setFreeBlock(currentBlockPositionIn);				// free block
					currentBlockPositionIn = vdInFile.readLong();			// reading next position
					
					vdOutFile.seek(currentBlockPositionOut);   				// changing outfile's cursor 
					
					vdOutFile.write(bytesToWrite);							// writing block
					vdOut.setOccupiedBlock(currentBlockPositionOut);		// setting block to occupied
					currentBlockPositionOut = vdOutFile.readLong();			// reading next position
				}
				
				// -----3. deleting VNode from tree structure
				
				this.addSize(-size);
				this.cutLinkToTree();				
			}
			else{
				for(VNode c : newDirectory.getContentList()){
					if(c.name.equals(name)){
						throw new NameAlreadyExistsException("\""+name + "\" already exists in \""+newDirectory.getAbsolutePathName()+"\"");
					}
				}
				long tmp = size;					// copying size
				this.addSize(-size);				// removing size from all parents
				this.size = tmp;
				this.cutLinkToTree();
				newDirectory.addSize(size);
				absolutePath = newDirectory.getAbsolutePathName();
				newDirectory.addChild(this);		
			}
			
		} catch (java.io.IOException e){
			throw new VFSOperationFailException("Cannot move \""+getAbsolutePathName()+"\" to \""+newDirectory.getAbsolutePathName()+"\"");
		} finally {
			try{
				if(vdInFile!=null) vdInFile.close();
				if(vdOutFile!=null) vdOutFile.close();
			} catch (java.io.IOException io){
				throw new VFSOperationFailException(io.getMessage());
			}
		}
	}
	
	@Override
	public void remove() {
		java.io.RandomAccessFile vdFile = null;
		
		try{
			vdFile = new java.io.RandomAccessFile(this.getVirtualDisk().VDFileName,"rw");
			VirtualFileSystem vd = this.getVirtualDisk();
			long currentBlockPosition = firstBlockPosition;
			
			while(currentBlockPosition!=-1L){
				vd.setFreeBlock(currentBlockPosition);
				vdFile.seek(currentBlockPosition + VirtualFileSystem.DataSizeInBlock);
				
				currentBlockPosition = vdFile.readLong();
			}

			this.addSize(-size);
			this.cutLinkToTree(); 
		} catch (java.io.IOException e) {
			throw new VFSOperationFailException("cannot remove \""+getAbsolutePathName()+"\" from the virtual file system \""+getVirtualDisk().name+"\"");
		} finally {
			try{
				if(vdFile!=null) vdFile.close();
			} catch (java.io.IOException io){
				throw new VFSOperationFailException(io.getMessage());
			}
		}
	}
	
}
