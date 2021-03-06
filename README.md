# Virtual-File-System
A virtual file system API with Command-line interface and a GUI.

## Building
Use ant to build the project and generate jar file

`ant jar`

## Launching
### Command line
The main class is CLUserInterface in bin/clui/

`java CLUserInterface`

Or use jar file:

`java -jar vfsmanager.jar`
### GUI
The main class is GUInterface in bin/gui/

`java GUInterface`
## Command line usage
- `prompt <vfsName>` activate the prompt and choose a working virtual file system. If no vfsName="-off" the prompt is deactivated.
- `ls <vfsName> <args> <pathName>` list the information concerning files and directories contained in the position corresponding to pathName. If no vfsName is given, the current working virtual file system is chosen by default. If no pathName is given, the current working directory of the virtual file system is chosen by default. If args="": simple display. If args="-l": display lengths.
- `cd <vfsName> <pathName>` change the current working directory in a Virtual file system. if no vfsName is given, the current working virtual file system is chosen by default.
- `mv <vfsName> <oldPathName> <newPath>` moves/renames a directory or file. if no vfsName is given, the current working virtual file system is chosen by default.
- `cp <vfsName> <oldPathName> <newPath>` moves/renames a directory or file. if no vfsName is given, the current working virtual file system is chosen by default.
- `rm <vfsName> <pathName>` removes the file/directory contained in pathName. if no vfsName is given, the current working virtual file system is chosen by default.
- `crvfs <vfsName> <dim>` creates a new virtual file system with name vfsName and a total space of dim bytes.
- `rmvfs <vfsName>: deletes a virtual file system named vfsName.
- `impvfs <hostPathName> <vfsName> <vfsPath>` imports the file/directory from the host file system at the position hostPathName to the virtual file system at vfsPath. if no vfsName is given, the current working virtual file system is chosen by default.
- `expvfs <vfsName> <hostPath>` exports the virtual file system named vfsName to the host file system at hostPath. if no vfsName is given , the current working virtual file system is chosen by default. If no hostPath is given, the current working directory where the VFSApp runs is chosen by default.
- `free <vfsName>` display the quantity of free/occupied space in the virual file system named vfsName. if no vfsName is given , the current working virtual file system is chosen by default.
- `tree <vfsName>` display the tree structure of files/directories contained in the virtual file system named vfsName. if no vfsName is given , the current working virtual file system is chosen by default.
- `find <vfsName> <fileName>` searches for all the files named fileName in the virtual file system starting from its current working directory. if no vfsName is given , the current working virtual file system is chosen by default.
- `help <command-name>` displays a help message 
- `md <vfsName> <pathName>` creates a new directory with the given pathname. if no vfsName is given , the current working virtual file system is chosen by default.
- `mf <vfsName> <pathName> <dim>` creates a new file with the given pathname and dimension in bytes. if no vfsName is given , the current working virtual file system is chosen by default.
- `format <vfsName>` formats a virtual file system. if no vfsName is given , the current working virtual file system is chosen by default.
