package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import core.Path;



public class PathTest {

	@Test
	public void testIsPath() {
		
		assertTrue(Path.isPath("vd:/A"));
		assertTrue(!(Path.isPath("test")));
	}

	@Test
	public void testIsAbsolutePath() {

		assertTrue(Path.isAbsolutePath("vd:/A"));
		assertTrue(Path.isAbsolutePath("disk1:/A/B"));
		assertTrue(!(Path.isAbsolutePath("A")));
	}

	@Test
	public void testGetVfsName() {
		assertEquals(Path.getVfsName("vd:/A/test.txt"), "vd");
		assertEquals(Path.getVfsName("disk1:/A/B/file"),"disk1");
	}

	@Test
	public void testGetAbsolutePathFromAbsolutePathName() {
		assertEquals(Path.getAbsolutePathFromAbsolutePathName("disk1:/A/B/file"),"disk1:/A/B/");
	}

	@Test
	public void testGetNameFromAbsolutePathName() {
		assertEquals(Path.getNameFromAbsolutePathName("disk1:/A/B/file"),"file");
	}

	@Test
	public void testGetPathFromPathName() {
		assertEquals(Path.getPathFromPathName("/A/B/file"), "/A/B/");
		assertEquals(Path.getPathFromPathName("disk1:/A/B/file"),"disk1:/A/B/");
		assertEquals(Path.getPathFromPathName("/A"), "/");
	}

	@Test
	public void testGetNameFromPathName() {
		assertEquals(Path.getNameFromPathName("/A/B/file"),"file");
		assertEquals(Path.getNameFromPathName("disk1:/A/B/file"),"file");
	}


}
