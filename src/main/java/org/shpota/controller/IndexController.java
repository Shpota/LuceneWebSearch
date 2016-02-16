package org.shpota.controller;

import java.io.IOException;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.lucene.queryparser.classic.ParseException;
import org.shpota.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * View controller for index page
 */
@Controller
@RequestMapping("/index")
public class IndexController {

    private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);
    private static final UrlValidator URL_VALIDATOR = new UrlValidator(new String[] { "http", "https" });

    private SearchService searchServ;

    @Autowired
    public IndexController(SearchService searchServ) {
        this.searchServ = searchServ;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String loadPage() {
        LOG.debug("GET request to index page");
        return "index";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String postForm(Model model, @RequestParam(value = "q") String url) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("POST request to index page for URL = " + url);
        }

        boolean failed = false;
        try {
            if (URL_VALIDATOR.isValid(url)) {
                searchServ.addToIndex(url);
                model.addAttribute("successURL", url);
            } else {
                LOG.warn("Invalid URL: " + url);
                failed = true;
            }
        } catch (IOException | ParseException e) {
            LOG.error("Error while adding to index", e);
            failed = true;
        }

        model.addAttribute(failed ? "failedURL" : "successURL", url);

        return "index";
    }

}
