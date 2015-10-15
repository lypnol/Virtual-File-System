package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import core.*;

public class FileTest {

	@Test
	public void testMove() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(1024*1024);
		
		// creating a new directory in current working directory
		Directory A = vd.createDirectory("A");
		
		// creating a new file in current working directory
		File f1 = vd.createFile("1.r",50*1024);	// 50 KB
				
		// moving 1.r to A
		f1.move(A);
		
		assertEquals(vd.name+":/A/1.r",f1.getAbsolutePathName());
		
		// deleting virtual disk
		vd.delete();
	}

	@Test
	public void testCopy() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(1024*1024);
		
		// creating a new directory in current working directory
		Directory A = vd.createDirectory("A");
		
		// creating a new file in current working directory
		File f1 = vd.createFile("1.r",50*1024);	// 50 KB
				
		// copying 1.r to A
		f1.copy(A);
		
		assertEquals(vd.name+":/A/1.r",A.get("1.r").getAbsolutePathName());
		
		// deleting virtual disk
		vd.delete();
	}

	@Test
	public void testExportToHostFileSystem() {
		// creating a virtual disk of 2 MB
		VirtualFileSystem vd = new VirtualFileSystem(2*1024*1024);
		
		// creating and export file
		
		File f = vd.createFile("created.f", 10);
		
		// deleting existing file with the same name
		if((new java.io.File("test"+VirtualFileSystem.HFSSeparator+"created.f")).exists())
				(new java.io.File("test"+VirtualFileSystem.HFSSeparator+"created.f")).delete();
		
		f.exportToHostFileSystem("test");
		
		assertTrue((new java.io.File("test"+VirtualFileSystem.HFSSeparator+"created.f")).exists() && 
				(new java.io.File("test"+VirtualFileSystem.HFSSeparator+"created.f")).isFile() && 
				(new java.io.File("test"+VirtualFileSystem.HFSSeparator+"created.f")).length()==10);
		
		// deleting virtual disk
		vd.delete();
		
		//deleting exported
		(new java.io.File("test"+VirtualFileSystem.HFSSeparator+"created.f")).delete();
	}

	@Test
	public void testRemove() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(1024*1024);
		
		// creating file
		
		File f = vd.createFile("created.f", 10);
		
		assertFalse(vd.isFreeBlock(12));
		
		// deleting
		
		f.remove();
		
		assertTrue(vd.isFreeBlock(12));
		
		// deleting virtual disk
		vd.delete();
	}

	@Test
	public void testGetFirstBlockPosition() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(1024*1024);
		
		// creating file
		
		File f = vd.createFile("created.f", 10);
		
		assertEquals(12L,f.getFirstBlockPosition());
		
		// deleting virtual disk
		vd.delete();
				
	}

	@Test
	public void testGetExtension() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(1024*1024);
		
		// creating file
		
		File f = vd.createFile("created.fie", 10);
		
		assertEquals("fie",f.getExtension());
		
		// deleting virtual disk
		vd.delete();
	}

}
