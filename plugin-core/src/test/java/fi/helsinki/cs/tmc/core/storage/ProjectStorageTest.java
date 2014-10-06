package fi.helsinki.cs.tmc.core.storage;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.Project;
import fi.helsinki.cs.tmc.core.io.FileIO;
import fi.helsinki.cs.tmc.core.ui.UserVisibleException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectStorageTest {

    private ProjectStorage storage;
    private FileIO io;
    private List<Project> projects;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {

        io = mock(FileIO.class);
        projects = new ArrayList<Project>();
        storage = new ProjectStorage(io);

    }

    @Test
    public void loadReturnsEmptyListIfFileDoesNotExist() {

        when(io.fileExists()).thenReturn(false);
        assertEquals(0, storage.load().size());
    }

    @Test(expected = UserVisibleException.class)
    public void loadThrowsIfReaderIsNull() {

        when(io.fileExists()).thenReturn(true);
        when(io.getReader()).thenReturn(null);
        storage.load();
    }

    @Test
    public void loadDoesNotThrowIfReaderCloseThrows() throws IOException {

        when(io.fileExists()).thenReturn(true);
        final Reader reader = mock(Reader.class);

        // return -1 to signify end of stream; otherwise gson deserialization
        // hangs while waiting for input
        when(reader.read(any(char[].class), anyInt(), anyInt())).thenReturn(-1);

        doThrow(new IOException("Foo")).when(reader).close();
        when(io.getReader()).thenReturn(reader);
        storage.load();
        verify(reader, times(1)).close();
    }

    @Test
    public void loadReturnsArrayWithCorrectSizeWithCorrectJSON() {

        final Reader reader = createMockWithJson();
        when(io.fileExists()).thenReturn(true);
        when(io.getReader()).thenReturn(reader);
        final List<Project> list = storage.load();
        assertEquals(1, list.size());
    }

    @Test
    public void loadReturnsArrayWithCorrectDataWithCorrectJSON() {

        final Reader reader = createMockWithJson();
        when(io.fileExists()).thenReturn(true);
        when(io.getReader()).thenReturn(reader);

        verifyData();
    }

    @Test
    public void loadReturnsArrayWithCorrectSizeIfReadIsSuccessfulButCloseThrowsException() throws IOException {

        final Reader reader = createMockWithJson();
        when(io.fileExists()).thenReturn(true);
        when(io.getReader()).thenReturn(reader);
        doThrow(new IOException("Foo")).when(reader).close();

        final List<Project> list = storage.load();
        assertEquals(1, list.size());
    }

    @Test
    public void loadReturnsArrayWithCorrectDataWithCorrectJSONIfCloseThrowsException() throws IOException {

        final Reader reader = createMockWithJson();
        when(io.fileExists()).thenReturn(true);
        when(io.getReader()).thenReturn(reader);
        doThrow(new IOException("Foo")).when(reader).close();
        verifyData();
    }

    @Test
    public void loadReturnsEmptyListIfReadingEmptyFile() throws IOException {

        final Reader reader = mock(Reader.class);
        when(io.fileExists()).thenReturn(true);
        when(io.getReader()).thenReturn(reader);
        when(reader.read(any(char[].class), anyInt(), anyInt())).thenReturn(-1);
        assertEquals(0, storage.load().size());
    }

    @Test
    public void loadReturnsEmptyListIfLoadThrowsAndReadingEmptyFile() throws IOException {

        final Reader reader = mock(Reader.class);
        when(io.fileExists()).thenReturn(true);
        when(io.getReader()).thenReturn(reader);
        when(reader.read(any(char[].class), anyInt(), anyInt())).thenReturn(-1);
        doThrow(new IOException("Foo")).when(reader).close();
        assertEquals(0, storage.load().size());
    }

    @Test(expected = UserVisibleException.class)
    public void loadThrowsUserVisibleExceptionIfReadFails() throws IOException {

        final Reader reader = mock(Reader.class);
        when(io.fileExists()).thenReturn(true);
        when(io.getReader()).thenReturn(reader);
        when(reader.read(any(char[].class), anyInt(), anyInt())).thenThrow(new IOException("Foo"));
        storage.load();
    }

    @Test(expected = UserVisibleException.class)
    public void saveThrowsIfIOIsNull() {

        storage = new ProjectStorage(null);

        storage.save(projects);
    }

    @Test(expected = UserVisibleException.class)
    public void saveThrowsIfWriterIsNull() {

        when(io.getWriter()).thenReturn(null);
        storage.save(projects);
    }

    @Test
    public void saveDoesNotThrowIfCloseThrows() throws IOException {

        final Writer writer = mock(Writer.class);
        when(io.getWriter()).thenReturn(writer);
        doThrow(new IOException("foo")).when(writer).close();
        storage.save(projects);
        verify(writer, times(1)).close();
    }

    private void verifyData() {

        final List<Project> list = storage.load();
        final Exercise e = list.get(0).getExercise();
        final Project p = list.get(0);
        assertEquals(843, e.getId());
        assertEquals("name", e.getName());
        assertEquals("course_name", e.getCourseName());
        assertEquals("course_name", e.getCourseName());
        assertEquals("http://solution_url.com", e.getSolutionDownloadUrl());
        assertEquals("/path/to", p.getRootPath());
    }

    private Reader createMockWithJson() {

        final Reader reader = mock(Reader.class);
        try {
            when(reader.read(any(char[].class), anyInt(), anyInt())).thenAnswer(new Answer<Integer>() {

                private final String json = "{\"projects\": [{\"exercise\": {\"id\": 843,\"name\": \"name\"," +
                        "\"courseName\": \"course_name\", \"deadlineDate\": null, \"deadline\": null," + "\"zip_url\": \"https://zip_url.com\"," +
                        "\"solution_zip_url\": \"http://solution_url.com\"," + "\"return_url\": \"https://return_url.com\"," +
                        "\"locked\": false, \"deadline_description\": null, \"returnable\": true," +
                        "\"requires_review\": false, \"attempted\": true, \"completed\": true," +
                        "\"reviewed\": true, \"all_review_points_given\": true," +
                        "\"oldChecksum\": \"bb0571149a58adf71f0f2980fb2980d3\", \"updateAvailable\": false," +
                        "\"checksum\": \"bb0571149a58adf71f0f2980fb2980d3\", \"memory_limit\": null" + "}," +
                        "\"projectFiles\": [\"/path/to/file\"], \"extraStudentFiles\": []," +
                        "\"rootPath\": \"/path/to\", \"status\": \"DOWNLOADED\"}]}";
                
                private final int position = 0;
                private StringReader stringReader;

                @Override
                public Integer answer(final InvocationOnMock invocation) throws Throwable {

                    if (stringReader == null) {
                        stringReader = new StringReader(json);
                    }

                    final char[] buffer = (char[]) invocation.getArguments()[0];
                    final int offset = (int) (invocation.getArguments()[1]);
                    final int length = (int) (invocation.getArguments()[2]);
                    return stringReader.read(buffer, offset, length);
                }
            });
        } catch (final IOException e) {
        }

        return reader;
    }
}
