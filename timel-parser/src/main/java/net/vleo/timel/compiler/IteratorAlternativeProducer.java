package net.vleo.timel.compiler;

import lombok.Data;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IteratorAlternativeProducer<T extends IteratorAlternativeProducer.Weighted> implements Iterator<IteratorAlternativeProducer.Alternatives<T>> {
    public interface Weighted {
        int getWeight();
    }

    @Data
    public static class Alternatives<T extends IteratorAlternativeProducer.Weighted> {
        private final int weight;
        private final List<T> alternatives;

        public <U extends IteratorAlternativeProducer.Weighted> Alternatives<U> map(Function<T, U> mapper) {
            return new Alternatives<>(
                    weight,
                    alternatives.stream()
                            .map(mapper::apply)
                            .collect(Collectors.toList())
            );
        }
    }

    private final Iterator<T> source;
    private Alternatives<T> current, next;

    public IteratorAlternativeProducer(Iterator<T> source) {
        this.source = source;
    }

    public boolean hasNext() {
        fetch();

        return current != null;
    }

    public Alternatives<T> next() {
        fetch();

        if(current == null)
            throw new IndexOutOfBoundsException("No more elements");

        Alternatives<T> result = current;

        current = next;
        next = null;

        return result;
    }

    public Alternatives<T> peek() {
        fetch();

        return current;
    }

    private void fetch() {
        if(next != null && !source.hasNext())
            return;

        while(source.hasNext()) {
            T value = source.next();

            if(current == null)
                current = new Alternatives<>(value.getWeight(), Stream.of(value).collect(Collectors.toList()));

            if(value.getWeight() != current.getWeight()) {
                if(next != null)
                    throw new IllegalStateException("Expected next to be uninitialized");

                next = new Alternatives<>(value.getWeight(), Stream.of(value).collect(Collectors.toList()));

                break;
            }

            current.getAlternatives().add(value);
        }
    }
}
