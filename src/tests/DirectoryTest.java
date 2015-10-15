package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import core.Directory;
import core.File;
import core.VirtualFileSystem;

public class DirectoryTest {

	@Test
	public void testMove() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(1024*1024);
		
		// creating a new directory in current working directory
		Directory A = vd.createDirectory("A");
		
		// creating a new directory in current working directory
		Directory B = vd.createDirectory("B");
		
		// creating a file in B
		File f = vd.createFile("B/file", 12);
				
		// moving B to A
		B.move(A);
		
		assertEquals(vd.name+":/A/B/file",f.getAbsolutePathName());
		assertEquals(12L,A.getSize());
		
		// deleting virtual disk
		vd.delete();
	}

	@Test
	public void testCopy() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(1024*1024);
		
		// creating a new directory in current working directory
		Directory A = vd.createDirectory("A");
		
		// creating a new directory in current working directory
		Directory B = vd.createDirectory("B");
		
		// creating a file in B
		vd.createFile("B/file", 12);
				
		// copying B to A
		B.copy(A);
		
		assertEquals(vd.name+":/A/B/file",A.get("B/file").getAbsolutePathName());
		
		// deleting virtual disk
		vd.delete();
	}

	@Test
	public void testExportToHostFileSystem() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(1024*1024);

		// creating a new directory in current working directory
		Directory C = vd.createDirectory("C");
		
		// creating a file in C
		vd.createFile("C/file", 12);
				
		// deleting directory with the same name if it exists
		
		if((new java.io.File("test"+VirtualFileSystem.HFSSeparator+"C")).exists()){
			(new java.io.File("test"+VirtualFileSystem.HFSSeparator+"C")).listFiles()[0].delete();
			(new java.io.File("test"+VirtualFileSystem.HFSSeparator+"C")).delete();
		}
		
		// exporting C
		
		C.exportToHostFileSystem("test");
		
		assertTrue((new java.io.File("test"+VirtualFileSystem.HFSSeparator+"C")).exists() && (new java.io.File("test"+VirtualFileSystem.HFSSeparator+"C")).isDirectory());
		
		// deleting virtual disk
		vd.delete();
		
		// deleting exported
		(new java.io.File("test"+VirtualFileSystem.HFSSeparator+"C")).listFiles()[0].delete();
		(new java.io.File("test"+VirtualFileSystem.HFSSeparator+"C")).delete();
	}

	@Test
	public void testRemove() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(1024*1024);
		
		// creating directory
		
		Directory A = vd.createDirectory("A");
		
		// creating file
		
		vd.createFile("A/created.f", 10);
		
		assertFalse(vd.isFreeBlock(12));
		
		// deleting
		A.remove();
		
		assertTrue(vd.isFreeBlock(12));
		
		// deleting virtual disk
		vd.delete();
	}

	@Test
	public void testAddVNode() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(1024*1024);
		
		// creating directory
		
		Directory A = vd.createDirectory("A");
		
		// creating file
		
		File f = vd.createFile("A/created.f", 10);
		
		assertEquals(10,A.getSize());
		assertEquals(vd.name+":/A/created.f",f.getAbsolutePathName());
		
		// deleting virtual disk
		vd.delete();
	}
	
	@Test
	public void testGetDirectory() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(1024*1024);
		
		// creating directory
		
		Directory A = vd.createDirectory("A");
		
		// creating directory
		
		Directory B = vd.createDirectory("A/B");
		
		assertEquals(B,A.getDirectory("B"));
		
		// deleting virtual disk
		vd.delete();
	}

	@Test
	public void testGetFile() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(1024*1024);
		
		// creating directory
		
		Directory A = vd.createDirectory("A");
		
		// creating file
		
		File f = vd.createFile("A/file",12);
		
		assertEquals(f,A.getFile("file"));
		
		// deleting virtual disk
		vd.delete();
	}

	@Test
	public void testGet() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(1024*1024);
		
		// creating directory
		
		Directory A = vd.createDirectory("A");
		
		// creating directory
		
		vd.createDirectory("A/B");
				
		// creating file
		
		File f = vd.createFile("A/B/file",12);
		
		assertEquals(f,A.get("B/file"));
		
		// deleting virtual disk
		vd.delete();
	}

	@Test
	public void testFind() {
		// creating a virtual disk of 1 MB
		VirtualFileSystem vd = new VirtualFileSystem(1024*1024);
		
		// creating directory
		
		Directory A = vd.createDirectory("A");
		
		// creating directory
		
		vd.createDirectory("A/B");
				
		// creating file
		
		File f = vd.createFile("A/B/file",12);
		File f2 = vd.createFile("A/file",12);
		
		assertTrue(A.find("file").contains(f) && A.find("file").contains(f2));
		
		// deleting virtual disk
		vd.delete();
	}

}
