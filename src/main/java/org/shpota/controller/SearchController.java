package org.shpota.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.shpota.domain.SearchResult;
import org.shpota.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for search page
 */
@Controller
public class SearchController {

    private static final int NUMBER_OF_RESULTS_PER_PAGE = 10;
    private static final int MAX_PAGE_NUMBER = 10;
    private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);

    private SearchService searchServ;

    @Autowired
    public SearchController(SearchService searchServ) {
        this.searchServ = searchServ;
    }

    @RequestMapping(value = { "/", "/search" }, method = GET)
    public String serchPage() {
        LOG.debug("GET request to search page");
        return "search";
    }

    @RequestMapping(value = "/search", method = POST)
    public String searchQery(Model model, 
            @RequestParam(value = "q") String text, 
            @RequestParam(value = "page") int pageNumber) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("POST request to search page with query = " + text + " page number = " + pageNumber);
        }
        if (text == null || "".equals(text.trim())) {
            return "search";
        }
        try {
            List<SearchResult> result = searchServ.searchForString(
                    text, 
                    pageNumber, 
                    NUMBER_OF_RESULTS_PER_PAGE);
            int hitsNumber = searchServ.getNumberOfHits(text);
            model.addAttribute("results", result);
            model.addAttribute("hitsNumber", hitsNumber);
            model.addAttribute("q", text);
            model.addAttribute("currentPage", pageNumber);
            int numberOfPages = hitsNumber / NUMBER_OF_RESULTS_PER_PAGE > MAX_PAGE_NUMBER
                    ? MAX_PAGE_NUMBER : hitsNumber / NUMBER_OF_RESULTS_PER_PAGE + 1;
            model.addAttribute("numberOfPages", numberOfPages);
        } catch (IOException | ParseException e) {
            LOG.error("Exception while featching results", e);
            return "redirect:/internalerror";
        }
        return "search";
    }

}
