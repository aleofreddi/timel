package net.vleo.timel.compiler;

import lombok.Data;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface AlternativeProducer<T extends Weighted> extends Iterator<AlternativeProducer.Alternatives<T>> {
    @Data
    class Alternatives<T extends Weighted> {
        private final int weight;
        private final List<T> alternatives;
    }

    boolean hasNext();

    Alternatives<T> next();

    Alternatives<T> peek();

    default <U extends Weighted> AlternativeProducer<U> map(Function<T, U> mapper) {
        return new AlternativeProducer<U>() {
            @Override
            public boolean hasNext() {
                return AlternativeProducer.this.hasNext();
            }

            @Override
            public Alternatives<U> next() {
                Alternatives<T> value = AlternativeProducer.this.next();

                return new Alternatives<>(
                        value.weight,
                        value.alternatives.stream()
                                .map(mapper::apply)
                                .collect(Collectors.toList())
                );
            }

            @Override
            public Alternatives<U> peek() {
                Alternatives<T> value = AlternativeProducer.this.peek();

                return new Alternatives<>(
                        value.weight,
                        value.alternatives.stream()
                                .map(mapper::apply)
                                .collect(Collectors.toList())
                );
            }
        };
    }
}
