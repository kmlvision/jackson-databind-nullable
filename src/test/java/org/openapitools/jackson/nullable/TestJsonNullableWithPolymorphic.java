package org.openapitools.jackson.nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.LinkedHashMap;
import java.util.Map;

public class TestJsonNullableWithPolymorphic extends ModuleTestBase
{
    static class ContainerA {
        @JsonProperty
        private JsonNullable<String> name = JsonNullable.undefined();
        @JsonProperty private JsonNullable<Strategy> strategy = JsonNullable.undefined();
    }

    static class ContainerB {
        @JsonProperty private JsonNullable<String> name = JsonNullable.undefined();
        @JsonProperty private Strategy strategy = null;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({ @JsonSubTypes.Type(name = "Foo", value = Foo.class),
            @JsonSubTypes.Type(name = "Bar", value = Bar.class),
            @JsonSubTypes.Type(name = "Baz", value = Baz.class) })
    interface Strategy { }

    static class Foo implements Strategy {
        @JsonProperty private final int foo;

        @JsonCreator
        Foo(@JsonProperty("foo") int foo) {
            this.foo = foo;
        }
    }

    static class Bar implements Strategy {
        @JsonProperty private final boolean bar;

        @JsonCreator
        Bar(@JsonProperty("bar") boolean bar) {
            this.bar = bar;
        }
    }

    static class Baz implements Strategy {
        @JsonProperty private final String baz;

        @JsonCreator
        Baz(@JsonProperty("baz") String baz) {
            this.baz = baz;
        }
    }

    static class AbstractJsonNullable {
        @JsonDeserialize(contentAs=Integer.class)
        public JsonNullable<java.io.Serializable> value;
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    final ObjectMapper MAPPER = mapperWithModule();

    public void testJsonNullableMapsFoo() throws Exception {

        Map<String, Object> foo = new LinkedHashMap<String, Object>();
        Map<String, Object> loop = new LinkedHashMap<String, Object>();
        loop.put("type", "Foo");
        loop.put("foo", 42);

        foo.put("name", "foo strategy");
        foo.put("strategy", loop);

        _test(MAPPER, foo);
    }

    public void testJsonNullableMapsBar() throws Exception {

        Map<String, Object> bar = new LinkedHashMap<String, Object>();
        Map<String, Object> loop = new LinkedHashMap<String, Object>();
        loop.put("type", "Bar");
        loop.put("bar", true);

        bar.put("name", "bar strategy");
        bar.put("strategy", loop);

        _test(MAPPER, bar);
    }

    public void testJsonNullableMapsBaz() throws Exception {
        Map<String, Object> baz = new LinkedHashMap<String, Object>();
        Map<String, Object> loop = new LinkedHashMap<String, Object>();
        loop.put("type", "Baz");
        loop.put("baz", "hello world!");

        baz.put("name", "bar strategy");
        baz.put("strategy", loop);

        _test(MAPPER, baz);
    }

    public void testJsonNullableWithTypeAnnotation13() throws Exception
    {
        AbstractJsonNullable result = MAPPER.readValue("{\"value\" : 5}",
                AbstractJsonNullable.class);
        assertNotNull(result);
        assertNotNull(result.value);
        Object ob = result.value.get();
        assertEquals(Integer.class, ob.getClass());
        assertEquals(Integer.valueOf(5), ob);
    }

    private void _test(ObjectMapper m, Map<String, ?> map) throws Exception {
        String json = m.writeValueAsString(map);

        ContainerA objA = m.readValue(json, ContainerA.class);
        assertNotNull(objA);

        ContainerB objB = m.readValue(json, ContainerB.class);
        assertNotNull(objB);
    }
}
