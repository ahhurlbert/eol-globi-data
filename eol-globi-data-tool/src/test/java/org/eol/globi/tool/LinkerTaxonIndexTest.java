package org.eol.globi.tool;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.eol.globi.data.CharsetConstant;
import org.eol.globi.data.GraphDBTestCase;
import org.eol.globi.data.NodeFactoryException;
import org.eol.globi.data.taxon.TaxonFuzzySearchIndex;
import org.eol.globi.data.taxon.TaxonIndexImpl;
import org.eol.globi.data.taxon.TaxonIndexImplTest;
import org.eol.globi.domain.PropertyAndValueDictionary;
import org.eol.globi.domain.RelTypes;
import org.eol.globi.domain.Taxon;
import org.eol.globi.domain.TaxonImpl;
import org.eol.globi.domain.TaxonNode;
import org.eol.globi.service.PropertyEnricher;
import org.eol.globi.service.PropertyEnricherException;
import org.eol.globi.service.TaxonUtil;
import org.eol.globi.util.NodeUtil;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class LinkerTaxonIndexTest extends GraphDBTestCase {

    @Test
    public void linking() throws NodeFactoryException {
        TaxonNode taxon = nodeFactory.getOrCreateTaxon("Homo sapiens", "Bar:123", "Animalia | Mammalia | Homo sapiens");
        TaxonImpl taxon1 = new TaxonImpl("Homo sapiens also", "FOO:444");
        taxon1.setPathIds("BARZ:111 | FOOZ:777");
        NodeUtil.connectTaxa(taxon1, taxon, getGraphDb(), RelTypes.SAME_AS);

        taxon = nodeFactory.getOrCreateTaxon("Bla blaus");
        taxon.setExternalId("FOO 1234");

        new LinkerTaxonIndex().link(getGraphDb());

        IndexHits<Node> hits = getGraphDb().index().forNodes(LinkerTaxonIndex.INDEX_TAXON_NAMES_AND_IDS).query("*:*");
        Node next = hits.next();
        assertThat(new TaxonNode(next).getName(), is("Homo sapiens"));
        assertThat(hits.hasNext(), is(true));
        hits.close();

        assertSingleHit(PropertyAndValueDictionary.PATH + ":BAR\\:123");
        assertSingleHit(PropertyAndValueDictionary.PATH + ":FOO\\:444");
        assertSingleHit(PropertyAndValueDictionary.PATH + ":FOO\\:444 " + PropertyAndValueDictionary.PATH + ":BAR\\:123");
        assertSingleHit(PropertyAndValueDictionary.PATH + ":BAR\\:*");
        assertSingleHit(PropertyAndValueDictionary.PATH + ":Homo");
        assertSingleHit(PropertyAndValueDictionary.PATH + ":\"Homo sapiens\"");

        TaxonNode node = nodeFactory.findTaxonByName("Homo sapiens");
        assertThat(node.getUnderlyingNode().getProperty(PropertyAndValueDictionary.EXTERNAL_IDS).toString()
                , is("Bar:123 | Animalia | Mammalia | Homo sapiens | FOO:444 | BARZ:111 | FOOZ:777"));

        assertThat(new TaxonFuzzySearchIndex(getGraphDb()).query("name:sapienz~").size(), is(1));
        assertThat(new TaxonFuzzySearchIndex(getGraphDb()).query("name:sapienz").size(), is(0));
    }

    @Test
    public void findByStringWithWhitespaces() throws NodeFactoryException {
        PropertyEnricher enricher = new PropertyEnricher() {
            @Override
            public Map<String, String> enrich(Map<String, String> properties) throws PropertyEnricherException {
                Taxon taxon = TaxonUtil.mapToTaxon(properties);
                taxon.setPath("kingdom" + CharsetConstant.SEPARATOR + "phylum" + CharsetConstant.SEPARATOR + "Homo sapiens" + CharsetConstant.SEPARATOR);
                taxon.setExternalId("anExternalId");
                taxon.setCommonNames(TaxonIndexImplTest.EXPECTED_COMMON_NAMES);
                taxon.setName("this is the actual name");
                return TaxonUtil.taxonToMap(taxon);
            }

            @Override
            public void shutdown() {

            }
        };
        TaxonIndexImpl taxonService = TaxonIndexImplTest.createTaxonService(getGraphDb());
        taxonService.setEnricher(enricher);
        taxonService.getOrCreateTaxon("Homo sapiens", null, null);
        new LinkerTaxonIndex().link(getGraphDb());

        assertThat(getGraphDb().index().existsForNodes("taxonNameSuggestions"), is(true));
        Index<Node> index = getGraphDb().index().forNodes("taxonNameSuggestions");
        Query query = new TermQuery(new Term("name", "name"));
        IndexHits<Node> hits = index.query(query);
        assertThat(hits.size(), is(1));

        hits = index.query("name", "s nme~");
        assertThat(hits.size(), is(1));

        hits = index.query("name", "geRman~");
        assertThat(hits.size(), is(1));

        hits = index.query("name:geRman~ AND name:som~");
        assertThat(hits.size(), is(1));

        hits = index.query("name:hmo~ AND name:SApiens~");
        assertThat(hits.size(), is(1));

        hits = index.query("name:hmo~ AND name:sapiens~");
        assertThat(hits.size(), is(1));

        // queries are case sensitive . . . should all be lower cased.
        hits = index.query("name:HMO~ AND name:saPIENS~");
        assertThat(hits.size(), is(0));
    }

    protected void assertSingleHit(String query) {
        IndexHits<Node> hits;
        Node next;
        hits = getGraphDb().index().forNodes(LinkerTaxonIndex.INDEX_TAXON_NAMES_AND_IDS).query(query);
        next = hits.next();
        assertThat(new TaxonNode(next).getName(), is("Homo sapiens"));
        assertThat(hits.hasNext(), is(false));
        hits.close();
    }
}
