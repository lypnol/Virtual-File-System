package tests;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

import core.*;

public class VFSAppTest {


	@Test
	public void testGetVFSDataBase() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd1 = new VirtualFileSystem("testGetVFSDataBase",1024*1024);

		// creating the VFSApp
		VFSApp vfsApp = new VFSApp();
		
		assertTrue(vfsApp.getVFSDataBase().contains(vd1));
		
		// deleting virtual disks
		vd1.delete();

	}

	@Test
	public void testList() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd1 = new VirtualFileSystem("testList",1024*1024);
		
		// adding directory and file
		
		vd1.createDirectory("A");
		vd1.createFile("file", 10);
		
		vd1.save();

		// creating the VFSApp
		VFSApp vfsApp = new VFSApp();

		assertEquals("A      0    d\nfile   10   f\n",vfsApp.list("testList", "-l", "/"));
		assertEquals("A      d\nfile   f\n",vfsApp.list("testList", "", "/"));
		assertEquals("",vfsApp.list("testList", "", "A"));

		// deleting virtual disks
		vd1.delete();
	}

	@Test
	public void testChangeDir() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd1 = new VirtualFileSystem("testChangeDir",1024*1024L);
		
		// adding directory
		
		Directory A = vd1.createDirectory("A");
		
		vd1.save();

		// creating the VFSApp
		VFSApp vfsApp = new VFSApp();

		// changing directory
		
		vfsApp.changeDir("testChangeDir", "A");
		
		assertEquals(A,vfsApp.getVFSByName("testChangeDir").getWorkingDirectory());
		
		// deleting virtual disks
		vd1.delete();
	}

	@Test
	public void testMakeDir() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd1 = new VirtualFileSystem("testMakeDir",1024*1024L);
		
		// creating the VFSApp
		VFSApp vfsApp = new VFSApp();

		// making directory
		
		vfsApp.makeDir("testMakeDir", "A");
		
		assertEquals("A   d\n",vfsApp.list("testMakeDir", "", ""));
		
		// deleting virtual disks
		vd1.delete();
	}

	@Test
	public void testMakeFile() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd1 = new VirtualFileSystem("testMakeFile",1024*1024L);
		
		// creating the VFSApp
		VFSApp vfsApp = new VFSApp();

		// making file
		
		vfsApp.makeFile("testMakeFile", "file","10");
		
		assertEquals("file   10   f\n",vfsApp.list("testMakeFile", "-l", ""));
		
		// deleting virtual disks
		vd1.delete();
	}

	@Test
	public void testMove() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd1 = new VirtualFileSystem("testMove",1024*1024L);
		
		// creating the VFSApp
		VFSApp vfsApp = new VFSApp();

		// making file
		
		vfsApp.makeFile("testMove", "file","10");
		
		// making directory
		
		vfsApp.makeDir("testMove", "A");
		
		// moving and renaming file
		
		vfsApp.move("testMove", "file", "A/file1");
		
		assertEquals("A   10   d\n",vfsApp.list("testMove", "-l", ""));
		assertEquals("file1   10   f\n",vfsApp.list("testMove", "-l", "A"));
		
		
		// deleting virtual disks
		vd1.delete();
	}

	@Test
	public void testMoveAndOverwrite() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd1 = new VirtualFileSystem("testMoveAndOverwrite",1024*1024L);
		
		// creating the VFSApp
		VFSApp vfsApp = new VFSApp();

		// making file
		
		vfsApp.makeFile("testMoveAndOverwrite", "file","10");
		
		// making directory
		
		vfsApp.makeDir("testMoveAndOverwrite", "A");
		
		// making file inside directory with the same name
		
		vfsApp.makeFile("testMoveAndOverwrite", "A/file","12");
				
		// moving and overwriting file
		
		vfsApp.moveAndOverwrite("testMoveAndOverwrite", "file", "A");
		
		assertEquals("A   10   d\n",vfsApp.list("testMoveAndOverwrite", "-l", ""));
		assertEquals("file   10   f\n",vfsApp.list("testMoveAndOverwrite", "-l", "A"));
		
		
		// deleting virtual disks
		vd1.delete();
	}

	@Test
	public void testCopy() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd1 = new VirtualFileSystem("testCopy",1024*1024L);
		
		// creating the VFSApp
		VFSApp vfsApp = new VFSApp();

		// making file
		
		vfsApp.makeFile("testCopy", "file","10");
		
		// making directory
		
		vfsApp.makeDir("testCopy", "A");
		
		// copying file
		
		vfsApp.copy("testCopy", "file", "A/");
		
		assertEquals("file   10   f\nA      10   d\n",vfsApp.list("testCopy", "-l", ""));
		assertEquals("file   10   f\n",vfsApp.list("testCopy", "-l", "A"));
		
		// deleting virtual disks
		vd1.delete();
	}

	@Test
	public void testCopyAndOverwrite() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd1 = new VirtualFileSystem("testCopyAndOverwrite",1024*1024L);
		
		// creating the VFSApp
		VFSApp vfsApp = new VFSApp();

		// making file
		
		vfsApp.makeFile("testCopyAndOverwrite", "file","10");
		
		// making directory
		
		vfsApp.makeDir("testCopyAndOverwrite", "A");
		
		// making file inside directory with the same name
		
		vfsApp.makeFile("testCopyAndOverwrite", "A/file","12");
				
		// copying and overwriting file
				
		vfsApp.copyAndOverwrite("testCopyAndOverwrite", "file", "A/");
		
		assertEquals("file   10   f\nA      10   d\n",vfsApp.list("testCopyAndOverwrite", "-l", ""));
		assertEquals("file   10   f\n",vfsApp.list("testCopyAndOverwrite", "-l", "A"));
		
		// deleting virtual disks
		vd1.delete();
	}

	@Test
	public void testRemove() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd1 = new VirtualFileSystem("testRemove",1024*1024L);
		
		// creating the VFSApp
		VFSApp vfsApp = new VFSApp();

		// making file
		
		vfsApp.makeFile("testRemove", "file","10");
		
		// making directory
		
		vfsApp.makeDir("testRemove", "A");
		
		// removing file and directory
		
		vfsApp.remove("testRemove", "file");
		vfsApp.remove("testRemove", "A");
		
		assertEquals("",vfsApp.list("testRemove", "", ""));
		
		vd1.delete();
	}

	@Test
	public void testCreateVFS() {
		// creating the VFSApp
		VFSApp vfsApp = new VFSApp();
		
		// creating new virtual file system of 1 MB
		vfsApp.createVFS("testCreateVFS", 1024*1024L);
		
		assertTrue((new java.io.File("testCreateVFS.vd")).exists());
		
		// deleting vfs
		vfsApp.getVFSByName("testCreateVFS").delete();
	}

	@Test
	public void testRemoveVFS() {
		// creating the VFSApp
		VFSApp vfsApp = new VFSApp();
		
		// creating new virtual file system of 1 MB
		vfsApp.createVFS("testRemoveVFS", 1024*1024L);
		
		// removing vfs
		vfsApp.removeVFS("testRemoveVFS");

		assertFalse((new java.io.File("testRemoveVFS.vd")).exists());
		
	}

	@Test
	public void testImportToVFS() {
		// creating a virtual disk of 2 MB
		VirtualFileSystem vd = new VirtualFileSystem("testImportToVFS",2*1024*1024L);
		
		// creating vfs app
		VFSApp vfsApp = new VFSApp();
		
		//importing test folder
		
		vfsApp.importToVFS("test", "testImportToVFS", "/");
		
		assertEquals("test   792994   d\n",vfsApp.list("testImportToVFS","-l",""));
		assertEquals(vd.name+":/test/A/A2/test.txt\ntestImportToVFS:/test/B/B3/test.txt\n",vfsApp.find("testImportToVFS", "test.txt"));
		
		// deleting virtual disk
		vd.delete();
	}

	@Test
	public void testExportVFS() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem("testExportVFS",1024*1024L);

		// creating a new directory in current working directory
		vd.createDirectory("C");
		
		// creating a file in C
		vd.createFile("C/file", 12);
		
		vd.save();
				
		// deleting directory with the same name if it exists
		
		if((new java.io.File("testExportVFS"+VirtualFileSystem.HFSSeparator+"C")).exists()){
			(new java.io.File("testExportVFS"+VirtualFileSystem.HFSSeparator+"C")).listFiles()[0].delete();
			(new java.io.File("testExportVFS"+VirtualFileSystem.HFSSeparator+"C")).delete();
		}
		
		// exporting virtualdisk
		
		VFSApp vfsApp = new VFSApp();
		vfsApp.exportVFS("testExportVFS", System.getProperty("user.dir"));
		
		assertTrue((new java.io.File("testExportVFS"+VirtualFileSystem.HFSSeparator+"C")).exists() && (new java.io.File("testExportVFS"+VirtualFileSystem.HFSSeparator+"C")).isDirectory());
		
		// deleting virtual disk
		vd.delete();
		
		// deleting exported
		
		(new java.io.File("testExportVFS"+VirtualFileSystem.HFSSeparator+"C")).listFiles()[0].delete();
		(new java.io.File("testExportVFS"+VirtualFileSystem.HFSSeparator+"C")).delete();
		(new java.io.File("testExportVFS")).delete();
	}

	@Test
	public void testFree() {
		// creating the VFSApp
		VFSApp vfsApp = new VFSApp();
		
		// creating new virtual file system of 1 MB
		vfsApp.createVFS("testFree", 1024*1024L);
		
		assertEquals("testFree: total space (1 MB)\n          free space (1 MB) occupied space (0 B)",vfsApp.free("testFree"));
		
		// removing vfs
		vfsApp.removeVFS("testFree");
	}

	@Test
	public void testFind() {
		// creating the VFSApp
		VFSApp vfsApp = new VFSApp();
		
		// creating new virtual file system of 1 MB
		vfsApp.createVFS("testFind", 1024*1024L);
		
		// creating files and directory
		
		vfsApp.makeDir("testFind", "A");
		vfsApp.makeFile("testFind", "A/file", "10");
		vfsApp.makeFile("testFind", "file", "12");
		
		assertEquals("testFind:/A/file\ntestFind:/file\n",vfsApp.find("testFind", "file"));
		
		// removing vfs
		vfsApp.removeVFS("testFind");
	}

	@Test
	public void testGetPrompt() {
		// creating the VFSApp
		VFSApp vfsApp = new VFSApp();
		
		// creating new virtual file system of 1 MB
		vfsApp.createVFS("testGetPrompt", 1024*1024L);
		
		Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
		
		assertEquals(sdf.format(cal.getTime())+" testGetPrompt:/ ",vfsApp.getPrompt("testGetPrompt"));
		
		// removing vfs
		vfsApp.removeVFS("testGetPrompt");	
	}

}
