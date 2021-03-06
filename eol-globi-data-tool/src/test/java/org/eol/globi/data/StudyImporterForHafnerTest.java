package org.eol.globi.data;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class StudyImporterForHafnerTest extends GraphDBTestCase {

    @Test
    public void importAll() throws StudyImporterException, NodeFactoryException {

        StudyImporter importer = new StudyImporterForHafner(new ParserFactoryImpl(), nodeFactory);
        importer.importStudy();


        assertThat(nodeFactory.findTaxonByName("Orthogeomys_cherriei"), is(notNullValue()));
    }
}
