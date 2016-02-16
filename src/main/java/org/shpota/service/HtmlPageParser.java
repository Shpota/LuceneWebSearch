package org.shpota.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HtmlPageParser {

    private static final Logger LOG = LoggerFactory.getLogger(HtmlPageParser.class);
    
    private final Document doc;

    HtmlPageParser(String url) throws IOException {
        doc = getByURL(url);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating Parser instance for URL = " + url);
        }
    }

    Set<String> getAllLinks() throws IOException {
        Set<String> result = new HashSet<>();
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            result.add(link.attr("abs:href"));
        }
        if (LOG.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("Links:");
            result.forEach(link -> sb.append("\n - ").append(link));
            LOG.debug(sb.toString());
        }
        return result;
    }

    String geyTitle() throws IOException {
        String title = doc.title();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Title = " + title);
        }
        return title;
    }

    String getPlainText() throws IOException {
        String result = doc.text();
        if (LOG.isDebugEnabled()) {
            LOG.debug("\nText Content:\n\n" + result + "\n\n");
        }
        return result;
    }

    private Document getByURL(String url) throws IOException {
        return Jsoup.connect(url).timeout(10000000)
                .userAgent( // Set Agent to prevent HTTP 403 errors
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                .get();
    }

}
