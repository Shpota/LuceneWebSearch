package org.shpota.config;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Initializes index on application startup
 */
@Component
public class IndexInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(IndexInitializer.class);

    private Directory directory;
    private Analyzer analyzer;

    @Autowired
    public IndexInitializer(Directory directory, Analyzer analyzer) {
        this.directory = directory;
        this.analyzer = analyzer;
    }

    /**
     * Initializes index in the file system on application startup
     * 
     * @throws IllegalStateException
     *             in case if index was not initialized because of IO issues
     */
    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        LOG.info("Initializing index");
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        // The index should not be overrided
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        try {
            // Opening and closing index writer instance will create the new
            // index if it is not exists
            new IndexWriter(directory, config).close();
        } catch (IOException e) {
            LOG.error("Error while initializing an index", e);
            // The application should not start w/o index
            throw new IllegalStateException("Index was not initialized", e);
        }
        LOG.info("Initialization completed");
    }

}