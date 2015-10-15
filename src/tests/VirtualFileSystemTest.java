package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import core.*;

public class VirtualFileSystemTest {

	@Test
	public void testConstructorDestructor() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(1024*1024);
		
		// checking attributes
		assertEquals(vd.name+".vd",vd.VDFileName);
		assertEquals(vd.name+":/",vd.rootDirectory.getAbsolutePathName());
		assertEquals(vd.name+":/",vd.getWorkingDirectory().getAbsolutePathName());
		assertTrue(vd.isFreeBlock(12));
		assertTrue(vd.isFreeBlock(12+1024));
		assertTrue(vd.isFreeBlock(12+1024*2));
		
		// deleting the virtual disk
		vd.delete();
	}

	@Test
	public void testSaveLoad() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(1024*1024);
		
		String name = vd.name;
		
		// changing VirtualDisk object attributes
		vd.setOccupiedBlock(12+1024);
		
		// saving virtual disk
		vd.save();
		
		// loading virtual disk
		vd = new VirtualFileSystem(name);
		
		assertFalse(vd.isFreeBlock(12+1024));
		
		String spacesToAdd = "  ";
		for(int i=0;i<name.length();i++)
			spacesToAdd += " ";

		assertEquals(name+": total space (1 MB)\n"+spacesToAdd+"free space (1024 KB) occupied space (1016 B)",vd.toString());
		
		// deleting virtual disk
		vd.delete();
	}

	@Test
	public void testGet() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(1024*1024);
		
		// creating a new directory in current working directory
		vd.createDirectory("A");
		
		// creating a new directory in current working directory
		vd.createDirectory("B");
		
		// creating a new directories in A
		vd.createDirectory("A/A1");
		vd.createDirectory("/A/A2");
		
		// creating a new directory in B
		vd.createDirectory("/B/B1/");
		
		// creating a new file in current working directory
		vd.createFile("1.r",50*1024);	// 50 KB
		
		// creating a new file in root directory
		vd.createFile("/2.r",2*1024);	// 2 KB
		
		// creating a new file in A
		vd.createFile("A/1.a",3*1024);	// 2 KB
		
		assertEquals("1.a (3 KB)",vd.get("A/1.a").toString());
		assertEquals("1.a (3 KB)",vd.get("/A/1.a").toString());
		assertEquals("2.r (2 KB)",vd.get("2.r").toString());
		
		// deleting virtual disk
		vd.delete();
	}
	
	
	@Test
	public void testCreateDirectoryFile() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(1024*1024);
		
		// creating a new directory in current working directory
		vd.createDirectory("A");
		
		// creating a new directory in current working directory
		vd.createDirectory("B");
		
		// creating a new directories in A
		vd.createDirectory("A/A1");
		vd.createDirectory("/A/A2");
		
		// creating a new directory in B
		vd.createDirectory("/B/B1/");
		
		// creating a new file in current working directory
		vd.createFile("1.r",50*1024);	// 50 KB
		
		// creating a new file in root directory
		vd.createFile("/2.r",2*1024);	// 2 KB
		
		// creating a new file in A
		vd.createFile("A/1.a",3*1024);	// 3 KB
		
		assertEquals(vd.name+":/A/1.a",vd.get("A/1.a").getAbsolutePathName());
		assertEquals(vd.name+":/A/A1/",vd.get("A/A1/").getAbsolutePathName());
		
		// deleting virtual disk
		vd.delete();
	}

	@Test
	public void testImportFromHostFileSystem() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(2*1024*1024);
		
		//importing file
		File file = (File) vd.importFromHostFileSystem("test"+VirtualFileSystem.HFSSeparator+"1.jpg");
		
		assertEquals(396493,file.getSize());
		assertEquals(vd.name+":/1.jpg",file.getAbsolutePathName());
		
		//importing directory
		Directory A = (Directory) vd.importFromHostFileSystem("test"+VirtualFileSystem.HFSSeparator+"A");
		
		assertEquals(396497,A.getSize());
		assertEquals(vd.name+":/A/A2/test.txt",vd.get("A/A2/test.txt").getAbsolutePathName());
		
		// deleting virtual disk
		vd.delete();
	}
	
	@Test
	public void testGetTree(){
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(1024*1024);
		
		// creating a new directory in current working directory
		vd.createDirectory("A");
		
		// creating a new directory in current working directory
		vd.createDirectory("B");
		
		// creating a new directories in A
		vd.createDirectory("A/A1");
		vd.createDirectory("/A/A2");
		
		// creating a new directory in B
		vd.createDirectory("/B/B1/");
		
		// creating a new file in current working directory
		vd.createFile("1.r",50*1024);	// 50 KB
		
		// creating a new file in root directory
		vd.createFile("/2.r",2*1024);	// 2 KB
		
		// creating a new file in A
		vd.createFile("A/1.a",3*1024);	// 3 KB
		
		// printing tree structure
		System.out.println(vd.getTree());

		// deleting virtual disk
		vd.delete();
	}

}
