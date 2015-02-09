package org.eol.globi.service;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

public class GitHubUtilIT {

    public static final String TEMPLATE_DATA_REPOSITORY = "globalbioticinteractions/template-dataset";

    @Test
    public void discoverRepos() throws IOException, URISyntaxException {
        List<String> reposWithData = GitHubUtil.find();
        assertThat(reposWithData, hasItem(TEMPLATE_DATA_REPOSITORY));
    }

}