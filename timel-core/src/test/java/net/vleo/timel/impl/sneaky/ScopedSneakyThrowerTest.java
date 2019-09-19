package net.vleo.timel.impl.sneaky;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Leofreddi
 */
class ScopedSneakyThrowerTest {
    @Test
    void shouldLimitSneakyScope() {
        try {
            Stream.of(1, 2)
                    .map(new ScopedSneakyThrower<IOException>().unchecked(this::throwingFunction))
                    .count();
        } catch(IOException e) {
        }
    }

    @Test
    void shouldWorkForStreamMap() {
        assertThrows(IOException.class, () ->
                Stream.of(1, 2, new IOException())
                        .map(new ScopedSneakyThrower<IOException>().unchecked(this::throwingFunction))
                        .reduce((t, a) -> a)
        );
    }

    private Object throwingFunction(Object obj) throws IOException {
        if(obj instanceof IOException)
            throw (IOException) obj;
        return obj;
    }
}