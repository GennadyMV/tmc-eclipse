package fi.helsinki.cs.tmc.core.spyware.utility;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ByteArrayGsonSerializerTest {

    private ByteArrayGsonSerializer serializer;

    @Before
    public void setUp() {

        serializer = new ByteArrayGsonSerializer();
    }

    @Test
    public void serializeReturnsNullIfDataIsNull() {

        assertEquals(JsonNull.INSTANCE, serializer.serialize(null, byte[].class, null));
    }

    @Test
    public void serializeReturnsJsonPrimitiveIfDataisOk() {

        assertNotNull(serializer.serialize(new byte[0], byte[].class, null));
    }

    @Test
    public void deserializeReturnsNullIfJsonElementContainsNull() {

        final JsonElement je = mock(JsonElement.class);
        when(je.isJsonNull()).thenReturn(true);
        assertNull(serializer.deserialize(je, byte[].class, null));
    }

    @Test(expected = JsonParseException.class)
    public void deserializeThrowsParseExceptionIfJsonElementIsNotJsonPrimitive() {

        final JsonElement je = mock(JsonElement.class);
        when(je.isJsonPrimitive()).thenReturn(false);
        serializer.deserialize(je, byte[].class, null);
    }

    @Test(expected = JsonParseException.class)
    public void deserializeThrowsParseExceptionIfJsonElementDoesNotContainString() {

        final JsonPrimitive jp = new JsonPrimitive(42);
        serializer.deserialize(jp, byte[].class, null);
    }

    @Test
    public void deSerializeReturnsDataIfInputIsOk() {

        final JsonPrimitive jp = new JsonPrimitive("derp");
        assertNotNull(serializer.deserialize(jp, byte[].class, null));
    }

}
