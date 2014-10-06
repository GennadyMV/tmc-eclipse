package fi.helsinki.cs.tmc.core.io.zip.unzippingdecider;

import fi.helsinki.cs.tmc.core.io.FakeFileIO;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TmcProjectFileTest {

    @Test
    public void testLoading() throws IOException {

        final FakeFileIO file = getFile();
        file.setContents("extra_student_files:\n - \"one\"\n - \"two\"");

        final TmcProjectFile result = new TmcProjectFile(file);
        assertTrue(result.getExtraStudentFiles().contains("one"));
        assertTrue(result.getExtraStudentFiles().contains("two"));
        assertEquals(2, result.getExtraStudentFiles().size());
    }

    @Test
    public void testLoadingEmptyFile() throws IOException {

        final FakeFileIO file = getFile();
        final TmcProjectFile result = new TmcProjectFile(file);
        assertTrue(result.getExtraStudentFiles().isEmpty());
    }

    private FakeFileIO getFile() {

        return new FakeFileIO("/project/.tmcproject.yml");
    }

}
