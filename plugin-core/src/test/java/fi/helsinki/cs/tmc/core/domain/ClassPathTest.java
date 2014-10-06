package fi.helsinki.cs.tmc.core.domain;

import com.google.common.io.Files;

import fi.helsinki.cs.tmc.core.io.FileUtil;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClassPathTest {

    private ClassPath classpath;
    private File root;

    @Before
    public void setUp() {

        root = Files.createTempDir();
        final File deepSubDir = new File(root.getAbsolutePath() + "/sub/subsub/subsubsub");
        deepSubDir.mkdirs();

        classpath = new ClassPath(FileUtil.getUnixPath(root.getAbsolutePath().toString() + "/*"));
    }

    @Test
    public void addingSamePathTwiceOnlyAddsItOnce() {

        classpath.add("a");
        classpath.add("a");
        assertEquals(2, classpath.getSubPaths().size());
    }

    @Test
    public void addingACompleteClasspathAddsAllItsSubpaths() {

        final ClassPath another = new ClassPath("another1");
        another.add("another2");

        classpath.add(another);
        assertTrue(classpath.getSubPaths().contains("another1"));
        assertTrue(classpath.getSubPaths().contains("another2"));
    }

    @Test
    public void addDirAndSubDirsAddsAllSubDirs() {

        classpath.addDirAndSubDirs(FileUtil.getUnixPath(root.getAbsolutePath().toString()) + "/sub/");
        final String rootPath = FileUtil.getUnixPath(root.getAbsolutePath());
        assertTrue(classpath.getSubPaths().contains(rootPath + "/sub/*"));
        assertTrue(classpath.getSubPaths().contains(rootPath + "/sub/subsub/*"));
        assertTrue(classpath.getSubPaths().contains(rootPath + "/sub/subsub/subsubsub/*"));
    }

    @Test
    public void providingFileToAddDirAndSubDirsUsesThatFilesParentDirectoryInstead() throws IOException {

        final String rootPath = FileUtil.getUnixPath(root.getAbsolutePath());
        final File tmpFile = new File(rootPath + "/sub/foo.txt");
        tmpFile.createNewFile();

        classpath.addDirAndSubDirs(rootPath + "/sub/foo.txt");

        assertTrue(classpath.getSubPaths().contains(rootPath + "/sub/*"));
        assertTrue(classpath.getSubPaths().contains(rootPath + "/sub/subsub/*"));
        assertTrue(classpath.getSubPaths().contains(rootPath + "/sub/subsub/subsubsub/*"));
    }

    @Test
    public void addDirAndSubDirsDoesNotAddThingsMultipleTimes() {

        classpath.addDirAndSubDirs(root.getAbsolutePath().toString() + "/sub/");
        classpath.addDirAndSubDirs(root.getAbsolutePath().toString() + "/sub/");
        assertEquals(4, classpath.getSubPaths().size());
    }

    @Test
    public void toStringReturnsCorrectlyAppendsFilesToString() {

        final String rootPath = FileUtil.getUnixPath(root.getAbsolutePath());

        classpath.addDirAndSubDirs(FileUtil.getUnixPath(root.getAbsolutePath().toString()) + "/sub/");
        final String expected = rootPath + "/*" + System.getProperty("path.separator") + rootPath + "/sub/*" + System.getProperty("path.separator") +
                rootPath + "/sub/subsub/*" + System.getProperty("path.separator") + rootPath + "/sub/subsub/subsubsub/*";
        assertEquals(expected, classpath.toString());
    }
}
