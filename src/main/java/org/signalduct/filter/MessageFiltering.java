package org.signalduct.filter;

import java.util.List;

/**
 * Something that has message filters and provides methods for managing them.
 */
public interface MessageFiltering {

    /**
     * Adds a new filter that gets applied before the existing filters to incoming network events.
     * That is, it is at the lowest level.
     *
     * @param filter a filter to apply to incoming and outgoing events.
     */
    void addFilterFirst(MessageFilter filter);

    /**
     * Adds a new filter that gets applied after the existing filters to incoming network events.
     * That is, it is at the highest level.
     *
     * @param filter a filter to apply to incoming and outgoing events.
     */
    void addFilterLast(MessageFilter filter);

    /**
     * @param filter a filter to apply to incoming and outgoing events.
     * @param index the index to add the filter at, among the other filters.
     *              0 == first to be applied to incoming events (lowest level).
     *              A negative index counts from the end of the filter list,
     *              so -1 = add last, -2 = add next to last, etc.
     */
    void addFilterAt(MessageFilter filter, int index);

    /**
     * @return a read only list with the filters currently added to the network.
     */
    List<MessageFilter> getFilters();

    /**
     * @param filter filter to remove.
     */
    void removeFilter(MessageFilter filter);

}
