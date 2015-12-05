package core;

/**
 * Path strings management
 * @author ayoub
 *
 */
public class Path {
	
	/**
	 * checks if the given argument is a path string
	 * @param arg string to check
	 * @return true if the given argument is a path, false if not.
	 */
	static public boolean isPath(String arg){
		if(arg.equals(VirtualFileSystem.separator)) return true;
		if(arg.endsWith(VirtualFileSystem.separator))
			return arg.substring(0, arg.length() - VirtualFileSystem.separator.length()).contains(VirtualFileSystem.separator);
		return arg.contains(VirtualFileSystem.separator);
	}
	
	/**
	 * checks if the given argument is an absolute path string.
	 * absolute paths are formatted in the following way: /VirtualDisk:/Directory/Directory/.../Directory/
	 * @param arg string to check
	 * @return true if the given argument is an absolute path string, false if not
	 */
	static public boolean isAbsolutePath(String arg){
		if(arg.endsWith(":"+VirtualFileSystem.separator))
			return true;
		for(int i=0;i<arg.length();i++){
			if(i+1+VirtualFileSystem.separator.length()<arg.length())
				if(arg.substring(i, i + 1 + VirtualFileSystem.separator.length()).equals(":"+VirtualFileSystem.separator))
					return true;
		}
		return false;
	}
	
	/**
	 * extracts the virtual file system name from an absolute path string
	 * @param absolutePath string containing an absolute path
	 * @return virtual file system name at the begining of the absolute path string. null if the given string argument is not formatted correctly.
	 */
	static public String getVfsName(String absolutePath){
		if(!isAbsolutePath(absolutePath))
			return null;
		return absolutePath.split(":")[0];
	}
	
	/**
	 * extracts the absolute path from an absolute path name. 
	 * if the given argument is "disk1:/A/B/file" 
	 * the return string is "disk1:/A/B/"
	 * @param absolutePathName string containing an absolute path name
	 * @return the absolute path corresponding to the given absolute path name
	 */
	static public String getAbsolutePathFromAbsolutePathName(String absolutePathName){
		if(!isAbsolutePath(absolutePathName))
			return null;
		if(absolutePathName.endsWith(":"+VirtualFileSystem.separator))
			return absolutePathName;
		if(absolutePathName.endsWith(VirtualFileSystem.separator))
			absolutePathName = absolutePathName.substring(0, absolutePathName.length()-VirtualFileSystem.separator.length());
		
		return absolutePathName.substring(0,absolutePathName.length()-absolutePathName.split(VirtualFileSystem.separator)[absolutePathName.split(VirtualFileSystem.separator).length-1].length());
	}
	
	/**
	 * extracts the name from an absolute path name.  
	 * if the given argument is "disk1:/A/B/file"  
	 * the return string is "file"
	 * @param absolutePathName string containing an absolute path name
	 * @return the name corresponding to the given absolute path name
	 */
	static public String getNameFromAbsolutePathName(String absolutePathName){
		if(!isAbsolutePath(absolutePathName))
			return null;
		if(absolutePathName.endsWith(":"+VirtualFileSystem.separator))
			return VirtualFileSystem.separator;
		if(absolutePathName.endsWith(VirtualFileSystem.separator))
			absolutePathName = absolutePathName.substring(0, absolutePathName.length()-VirtualFileSystem.separator.length());

		return absolutePathName.split(VirtualFileSystem.separator)[absolutePathName.split(VirtualFileSystem.separator).length-1];
	}
	
	/**
	 * extracts the path from a path name. 
	 * if the given argument is "A/B/file" 
	 * the return string is "A/B/"
	 * @param pathName string containing a path name
	 * @return the path corresponding to the given path name
	 */
	static public String getPathFromPathName(String pathName){
		if(!isPath(pathName))
			return ".";
		if(isAbsolutePath(pathName)){	
			return getAbsolutePathFromAbsolutePathName(pathName);
		}
		
		if(pathName.equals(VirtualFileSystem.separator))
			return pathName;
		
		if(pathName.endsWith(VirtualFileSystem.separator))
			pathName = pathName.substring(0, pathName.length()-VirtualFileSystem.separator.length());
		
		if(pathName.split(VirtualFileSystem.separator).length==2 && pathName.split(VirtualFileSystem.separator)[0].equals(""))
			return VirtualFileSystem.separator;
		
		return pathName.substring(0,pathName.length()-pathName.split(VirtualFileSystem.separator)[pathName.split(VirtualFileSystem.separator).length-1].length());
	}
	
	/**
	 * extracts the name from a path name. 
	 * if the given argument is "A/B/file" 
	 * the return string is "file"
	 * @param pathName string containing a path name
	 * @return the name corresponding to the given path name
	 */
	static public String getNameFromPathName(String pathName){
		if(!isPath(pathName))
			return pathName;
		if(isAbsolutePath(pathName))
			return getNameFromAbsolutePathName(pathName);
		
		if(pathName.equals(VirtualFileSystem.separator))
			return pathName;
		
		if(pathName.endsWith(VirtualFileSystem.separator))
			pathName = pathName.substring(0, pathName.length()-VirtualFileSystem.separator.length());

		return pathName.split(VirtualFileSystem.separator)[pathName.split(VirtualFileSystem.separator).length-1];
	}
	
}
