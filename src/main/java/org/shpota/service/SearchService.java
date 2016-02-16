package org.shpota.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.shpota.domain.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Provides Search and Index functionality
 */
@Service
public class SearchService {

    private static final Logger LOG = LoggerFactory.getLogger(SearchService.class);

    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String URL = "url";
    private static final int INDEXING_DEPTH = 1;

    private Directory directory;
    private Analyzer analyzer;

    @Autowired
    public SearchService(Directory directory, Analyzer analyzer) {
        this.directory = directory;
        this.analyzer = analyzer;
    }

    /**
     * 
     * Adds URL and all its links to the index. Note: All IO Exceptions occurred
     * while parsing children URLs will be ignored, you can find them in server
     * logs
     * 
     * @throws IOException
     *             in case if it is occurred during parsing base url
     * @throws ParseException
     */
    public void addToIndex(String url) throws IOException, ParseException {
        if (isEmpty(url)) {
            return;
        }
        Set<String> indexedURLs = new HashSet<>();
        addLinkedPagesToIndex(cutURL(url), 0, indexedURLs);
        if (LOG.isInfoEnabled()) {
            StringBuilder sb = new StringBuilder("Indexed URLs (")
                    .append(indexedURLs.size()).append("):");
            indexedURLs.forEach(link -> sb.append("\n -- ").append(link));
            LOG.info(sb.toString());
        }
    }

    private void addLinkedPagesToIndex(String url, int depth, Set<String> indexedURLs)
            throws IOException, ParseException {
        if (depth > INDEXING_DEPTH || indexedURLs.contains(cutURL(url)) || isIndexed(url)) {
            return;
        }
        String cutUrl = cutURL(url);
        indexedURLs.add(cutUrl);
        HtmlPageParser parser = new HtmlPageParser(cutUrl);
        String content = parser.getPlainText();
        String title = parser.geyTitle();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try (IndexWriter iwriter = new IndexWriter(directory, config);) {
            Document doc = new Document();
            doc.add(new Field(URL, cutUrl, StringField.TYPE_STORED));
            doc.add(new Field(TITLE, title, TextField.TYPE_STORED));
            doc.add(new Field(CONTENT, content, TextField.TYPE_STORED));
            iwriter.addDocument(doc);
        }
        for (String link : parser.getAllLinks()) {
            if (!isEmpty(link)) {
                try {
                    addLinkedPagesToIndex(link, depth + 1, indexedURLs);
                } catch (IOException e) {
                    // Ignore errors for children URLs
                    LOG.warn("IOException occured while parsing " + link, e);
                }
            }
        }
    }

    /**
     * Get search results ordered by relevance
     *
     * @param text
     *            - searching string
     * @param startPageNumber
     *            - start position in search result
     * @param numOfResults
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public List<SearchResult> searchForString(String text, int startPageNumber, int numOfResults)
            throws IOException, ParseException {
        List<SearchResult> result = new ArrayList<>();
        DirectoryReader ireader = null;
        try {
            ireader = DirectoryReader.open(directory);
            Sort sort = new Sort(new SortField(CONTENT, SortField.Type.SCORE));
            IndexSearcher isearcher = new IndexSearcher(ireader);
            QueryParser parser = new QueryParser(CONTENT, analyzer);
            Query query = parser.parse(QueryParser.escape(text));
            ScoreDoc[] hits = isearcher.search(query, 1000, sort).scoreDocs;
            int endIndex = startPageNumber * numOfResults + numOfResults;
            if (hits.length < endIndex) {
                endIndex = hits.length;
            }
            for (int i = startPageNumber * numOfResults; i < endIndex; i++) {
                Document hitDoc = isearcher.doc(hits[i].doc);
                result.add(new SearchResult(hitDoc.get(TITLE), hitDoc.get(URL)));
            }
        } finally {
            if (ireader != null) {
                try {
                    ireader.close();
                } catch (IOException e) {
                    LOG.error("Error while closing DirectoryReader", e);
                }
            }
        }
        return result;
    }

    /**
     * Checks if URL present in the index
     * 
     * @param url
     * @return
     * @throws IOException
     * @throws ParseException
     */
    private boolean isIndexed(String url) throws IOException, ParseException {
        DirectoryReader ireader = null;
        try {
            ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);
            Query query = new TermQuery(new Term(URL, url));
            ScoreDoc[] hits = isearcher.search(query, 1).scoreDocs;
            return hits.length > 0;
        } finally {
            if (ireader != null) {
                try {
                    ireader.close();
                } catch (IOException e) {
                    LOG.error("Error while closing DirectoryReader", e);
                }
            }
        }
    }

    public int getNumberOfHits(String text) throws IOException, ParseException {
        DirectoryReader ireader = null;
        try {
            ireader = DirectoryReader.open(directory);
            QueryParser parser = new QueryParser(CONTENT, analyzer);
            Query query = parser.parse(QueryParser.escape(text));
            return new IndexSearcher(ireader).search(query, 1000).totalHits;
        } finally {
            if (ireader != null) {
                try {
                    ireader.close();
                } catch (IOException e) {
                    LOG.error("Error while closing DirectoryReader", e);
                }
            }
        }
    }

    private String cutURL(String url) {
        String result = url.trim();
        int index = result.indexOf('#');
        if (index != -1) {
            result = result.substring(0, index);
        }
        return result;
    }

    private boolean isEmpty(String string) {
        return string == null || "".equals(string.trim());
    }

}
