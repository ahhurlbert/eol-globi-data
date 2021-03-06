package org.eol.globi.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ExternalIdUtilTest {

    @Test
    public void mapping() {
        assertThat(ExternalIdUtil.infoURLForExternalId("http://blabla"), is("http://blabla"));
        assertThat(ExternalIdUtil.infoURLForExternalId("doi:someDOI"), is("http://dx.doi.org/someDOI"));
        assertThat(ExternalIdUtil.infoURLForExternalId("ENVO:00001995"), is("http://purl.obolibrary.org/obo/ENVO_00001995"));
        assertThat(ExternalIdUtil.infoURLForExternalId("bioinfo:ref:147884"), is("http://bioinfo.org.uk/html/b147884.htm"));
    }

    @Test
    public void getExternalId() {
        assertThat(ExternalIdUtil.getUrlFromExternalId("{ \"data\": [[]]}"), is("{}"));
        assertThat(ExternalIdUtil.getUrlFromExternalId("{ \"data\": []}"), is("{}"));
        assertThat(ExternalIdUtil.getUrlFromExternalId("{}"), is("{}"));
    }

    @Test
    public void buildCitation() {
        assertThat(ExternalIdUtil.toCitation("Joe Smith", "my study", "1984"), is("Joe Smith. 1984. my study"));
        assertThat(ExternalIdUtil.toCitation("Joe Smith", null, "1984"), is("Joe Smith. 1984"));
        assertThat(ExternalIdUtil.toCitation("Joe Smith", "my study", null), is("Joe Smith. my study"));
    }
}
