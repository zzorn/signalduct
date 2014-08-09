package org.signalduct;

import org.signalduct.filter.FilterChain;
import org.signalduct.filter.LowerLevel;
import org.signalduct.filter.impl.FilterChainImpl;
import org.signalduct.filter.MessageFilter;

import java.util.List;

import static org.flowutils.Check.notNull;

/**
 * Base class for networks, handles the filter chain management.
 */
public abstract class NetworkBase implements Network {

    private FilterChain filterChain = new FilterChainImpl();

    private boolean initialized = false;

    private LowerLevel networkLevel;
    private ConnectionListener applicationLevel;


    @Override public final FilterChain getFilterChain() {
        return filterChain;
    }

    @Override public final void setFilterChain(FilterChain filterChain) {
        notNull(filterChain, "filterChain");

        // De-initialize old chain
        if (this.filterChain != null) {
            this.filterChain.deInitialize();
        }

        // Set new chain, and initialize it if we are initialized
        this.filterChain = filterChain;
        if (initialized) {
            this.filterChain.initialize(networkLevel, applicationLevel);
        }
    }

    /**
     * Must be called before any filtering of messages can be done.
     *
     * @param networkLevel object to send events intended for the network level to.
     * @param applicationLevel object to send events intended for the application to.
     */
    protected final void initialize(LowerLevel networkLevel, ConnectionListener applicationLevel) {
        this.networkLevel = networkLevel;
        this.applicationLevel = applicationLevel;

        filterChain.initialize(networkLevel, applicationLevel);

        initialized = true;
    }


    /**
     * Can be called to remove references to the network and application level objects.
     */
    protected final void deInitialize() {
        initialized = false;

        filterChain.deInitialize();

        networkLevel = null;
        applicationLevel = null;
    }

    // Delegate filter chain management to the FilterChain object.
    @Override public final void addFilterFirst(MessageFilter filter) {filterChain.addFilterFirst(filter);}
    @Override public final void addFilterLast(MessageFilter filter) {filterChain.addFilterLast(filter);}
    @Override public final void addFilterAt(MessageFilter filter, int index) {filterChain.addFilterAt(filter, index);}
    @Override public final List<MessageFilter> getFilters() {return filterChain.getFilters();}
    @Override public final void removeFilter(MessageFilter filter) {filterChain.removeFilter(filter);}

}
