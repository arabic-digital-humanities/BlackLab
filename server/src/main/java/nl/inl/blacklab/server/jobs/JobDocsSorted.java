package nl.inl.blacklab.server.jobs;

import nl.inl.blacklab.search.results.DocResults;
import nl.inl.blacklab.server.datastream.DataStream;
import nl.inl.blacklab.server.exceptions.BlsException;
import nl.inl.blacklab.server.requesthandlers.SearchParameters;
import nl.inl.blacklab.server.search.SearchManager;

/**
 * Represents a docs search and sort operation.
 */
public class JobDocsSorted extends JobWithDocs {

    public static class JobDescDocsSorted extends JobDescription {

        DocSortSettings sortSettings;

        public JobDescDocsSorted(SearchParameters param, JobDescription hitsToSort, SearchSettings searchSettings,
                DocSortSettings sortSettings) {
            super(param, JobDocsSorted.class, hitsToSort, searchSettings);
            this.sortSettings = sortSettings;
        }

        @Override
        public DocSortSettings getDocSortSettings() {
            return sortSettings;
        }

        @Override
        public String uniqueIdentifier() {
            return super.uniqueIdentifier() + sortSettings + ")";
        }

        @Override
        public void dataStreamEntries(DataStream ds) {
            super.dataStreamEntries(ds);
            ds.entry("sortSettings", sortSettings);
        }

        @Override
        public String getUrlPath() {
            return "docs";
        }

    }

    public JobDocsSorted(SearchManager searchMan, User user, JobDescription par) throws BlsException {
        super(searchMan, user, par);
    }

    @Override
    public void performSearch() throws BlsException {
        DocResults unsorted = ((JobWithDocs) inputJob).getDocResults();
        setPaused(unsorted.threadPauser()); // set prio manually, so we don't expose the unsorted results by assigning to this.docResults
        // Now, sort the docs.
        DocSortSettings docSortSett = jobDesc.getDocSortSettings();
        if (docSortSett.sortBy() != null) {
            // Be lenient of clients passing wrong sortBy values; ignore bad sort requests
            docResults = unsorted.sort(docSortSett.sortBy());
        }
        docResults = unsorted; // now that we sorted them we can make them available
    }

    @Override
    protected void dataStreamSubclassEntries(DataStream ds) {
        super.dataStreamSubclassEntries(ds);
        ds.entry("numberOfDocResults", docResults == null ? -1 : docResults.size());
    }

}
