package net.scriptgate.opengles.program;

import java.util.Collection;
import java.util.List;

import java8.util.function.ToIntFunction;

import static java.util.Arrays.asList;
import static java8.util.stream.StreamSupport.stream;

public enum AttributeVariable {

    POSITION("a_Position", 3),
    COLOR("a_Color", 4),
    NORMAL("a_Normal", 3),
    TEXTURE_COORDINATE("a_TexCoordinate", 2);


    private final String name;
    //Specifies the number of components per generic vertex attribute.
    private final int size;

    AttributeVariable(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public static String[] toStringArray(List<AttributeVariable> attributes) {
        String[] programAttributes = new String[attributes.size()];
        for (int i = 0; i < attributes.size(); i++) {
            programAttributes[i] = attributes.get(i).getName();
        }
        return programAttributes;
    }

    public static int sizeOf(Collection<AttributeVariable> attributeVariables) {
        return stream(attributeVariables)
                .mapToInt(toSize())
                .sum();
    }

    public static int sizeOf(AttributeVariable... attributeVariables) {
        return stream(asList(attributeVariables))
                .mapToInt(toSize())
                .sum();
    }

    private static ToIntFunction<AttributeVariable> toSize() {
        return new ToIntFunction<AttributeVariable>() {
            @Override
            public int applyAsInt(AttributeVariable value) {
                return value.getSize();
            }
        };
    }
}
