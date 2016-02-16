package org.shpota.config;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.shpota.service.SearchService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = SearchService.class)
public class LuceneConfig {

    @Bean
    public Directory getFileSystemDirectory() throws IOException {
        FSDirectory directory = FSDirectory.open(Paths.get("./index/searchindex"));
        return directory;   
    }
    
    @Bean
    public Analyzer getAnalyzer() {
        return new StandardAnalyzer();
    }

}
