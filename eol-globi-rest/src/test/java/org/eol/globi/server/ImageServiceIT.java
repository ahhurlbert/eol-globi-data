package org.eol.globi.server;

import org.apache.http.client.HttpResponseException;
import org.eol.globi.util.HttpClient;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

public class ImageServiceIT extends ITBase {

    @Test
    public void findImagesForExternalId() throws IOException {
        String uri = getURLPrefix() + "images/EOL:276287";
        String response = HttpClient.httpGet(uri);
        assertThat(response, containsString("Oospila albicoma"));
    }

    @Test
    public void imagesForName() throws IOException {
        String uri = getURLPrefix() + "imagesForName/Homo%20sapiens";
        String response = HttpClient.httpGet(uri);
        assertThat(response, is(notNullValue()));
    }

    @Test(expected = HttpResponseException.class)
    public void imagesForNonExistentName() throws IOException {
        String uri = getURLPrefix() + "imagesForName/Donald%20duckus";
        try {
            HttpClient.httpGet(uri);
        } catch (HttpResponseException ex) {
            assertThat(ex.getStatusCode(), is(HttpStatus.NOT_FOUND.value()));
            throw ex;
        }
    }

    @Test
    public void imagesForNameLongName() throws IOException {
        String uri = getURLPrefix() + "imagesForName?name=Influenza%20A%20virus%20(A%2FIndia%2F77302%2F2001(H1N2))";
        String response = HttpClient.httpGet(uri);
        assertThat(response, is(notNullValue()));
    }

    @Test(expected = HttpResponseException.class)
    public void imagesForNameLongNameThatSpringDoesntLike() throws IOException {
        String uri = getURLPrefix() + "imagesForName/Influenza%20A%20virus%20(A%2FIndia%2F77302%2F2001(H1N2))";
        String response = HttpClient.httpGet(uri);
        assertThat(response, is(notNullValue()));
    }

    @Test
    public void imagesForNameLongNameOther() throws IOException {
        String uri = getURLPrefix() + "imagesForNames?name=Influenza%20A%20virus%20(A%2FIndia%2F77302%2F2001(H1N2))";
        String response = HttpClient.httpGet(uri);
        assertThat(response, is(notNullValue()));
    }

    @Test
    public void imagesForNames() throws IOException {
        String uri = getURLPrefix() + "imagesForNames?name=Homo%20sapiens&name=Ariopsis%20felis";
        String response = HttpClient.httpGet(uri);
        assertThat(response, is(notNullValue()));
    }

    @Test(expected = HttpResponseException.class)
    public void imagesForNamesNoParams() throws IOException {
        String uri = getURLPrefix() + "imagesForNames";
        try {
            HttpClient.httpGet(uri);
        } catch (HttpResponseException ex) {
            assertThat(ex.getStatusCode(), is(HttpStatus.BAD_REQUEST.value()));
            throw ex;
        }
    }

}
