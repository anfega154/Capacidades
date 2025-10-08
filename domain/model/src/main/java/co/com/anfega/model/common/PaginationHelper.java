package co.com.anfega.model.common;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaginationHelper {

    private PaginationHelper() {
    }

    public static <T, V extends Comparable<? super V>> PageResponse<T> paginateAndSort(
            List<T> items,
            int page,
            int size,
            String direction,
            Function<T, V> sortKeyExtractor
    ) {
        if (items == null) items = Collections.emptyList();

        Comparator<T> comparator = Comparator.comparing(
                sortKeyExtractor,
                Comparator.nullsLast(Comparator.naturalOrder())
        );

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        List<T> sorted = items.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        return PageResponse.of(sorted, page, size);
    }
}

