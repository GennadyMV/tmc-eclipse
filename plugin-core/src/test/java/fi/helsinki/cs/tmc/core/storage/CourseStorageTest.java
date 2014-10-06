package fi.helsinki.cs.tmc.core.storage;

import com.google.gson.Gson;

import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.io.FileIO;
import fi.helsinki.cs.tmc.core.ui.UserVisibleException;
import fi.helsinki.cs.tmc.core.utils.jsonhelpers.CourseList;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CourseStorageTest {

    private FileIO io;
    private CourseStorage storage;

    @Before
    public void setUp() {

        io = mock(FileIO.class);
        storage = new CourseStorage(io);

        when(io.fileExists()).thenReturn(true);
    }

    @Test(expected = UserVisibleException.class)
    public void testExceptionIsThrownIfNullIO() {

        io = null;
        storage.load();
    }

    @Test
    public void testExceptionIsThrownIfFileDoesntExist() {

        when(io.fileExists()).thenReturn(false);
        assertTrue(storage.load() instanceof List && storage.load().size() == 0);
    }

    @Test(expected = UserVisibleException.class)
    public void testExceptionIsThrownIfReaderIsNull() {

        when(io.getReader()).thenReturn(null);
        storage.load();
    }

    @Test(expected = UserVisibleException.class)
    public void testExceptionIsThrownIfWriterIsNull() {

        when(io.getWriter()).thenReturn(null);
        storage.save(new ArrayList<Course>());
    }

    @Test(expected = UserVisibleException.class)
    public void saveThrowsErrorWhenIoIsNull() {

        final CourseStorage l = new CourseStorage(null);
        l.save(new ArrayList<Course>());
    }

    @Test
    public void loadReturnsCorrectListOfCourses() {

        final CourseList cl = buildMockCourseList();
        final String clJson = new Gson().toJson(cl);
        final Reader reader = new StringReader(clJson);
        when(io.getReader()).thenReturn(reader);

        final List<Course> returned = storage.load();
        assertEquals("c1", returned.get(0).getName());
    }

    @Test(expected = UserVisibleException.class)
    public void saveThrowsIfWritesIsNull() {

        when(io.getWriter()).thenReturn(null);
        storage.save(new ArrayList<Course>());
    }

    @Test
    public void exceptionIsCaughtIfClosingWriterThrows() throws IOException {

        final Writer writer = mock(Writer.class);
        when(io.getWriter()).thenReturn(writer);
        doThrow(new IOException("Foo")).when(writer).close();

        storage.save(new ArrayList<Course>());
        verify(writer, times(1)).close();
    }

    private CourseList buildMockCourseList() {

        final Course c1 = new Course("c1");
        final Course[] cl = { c1 };
        final CourseList courseList = new CourseList();
        courseList.setCourses(cl);
        courseList.setApiVersion("7");
        return courseList;
    }

}
