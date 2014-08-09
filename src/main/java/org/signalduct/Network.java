package org.signalduct;

import org.signalduct.filter.FilterChain;
import org.signalduct.filter.MessageFiltering;

/**
 * A network endpoint.  Base interface for client and server networks.
 */
public interface Network extends MessageFiltering {

    /**
     * Closes all open connections and unbinds any sockets.
     */
    void stop();

    /**
     * @return the currently used filter chain, which is used to process incoming and outgoing messages
     *         (e.g. to handle serialization, compression, and encryption).
     */
    FilterChain getFilterChain();

    /**
     * @param filterChain a new FilterChain to use for processing incoming and outgoing messages
     *                    (e.g. to handle serialization, compression, and encryption).
     *                    Replaces the existing FilterChain, so any filters previously added to the network will be removed.
     */
    void setFilterChain(FilterChain filterChain);
}
