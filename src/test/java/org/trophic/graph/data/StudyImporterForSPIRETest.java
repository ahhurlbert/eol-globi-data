package org.trophic.graph.data;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.neo4j.graphdb.Relationship;
import org.trophic.graph.domain.Study;

import java.io.IOException;

import static org.junit.Assert.assertThat;

public class StudyImporterForSPIRETest extends GraphDBTestCase {

    @Test
    public void importStudy() throws IOException, StudyImporterException {
        StudyImporterForSPIRE importer = new StudyImporterForSPIRE(null, nodeFactory);
        TestTrophicLinkListener listener = new TestTrophicLinkListener();
        importer.setTrophicLinkListener(listener);
        importer.importStudy();

        assertThat(listener.getCount(), Is.is(30196));
    }


    private static class TestTrophicLinkListener implements TrophicLinkListener {
        public int getCount() {
            return count;
        }

        private int count = 0;

        @Override
        public void newLink(Study study, String predatorName, String preyName) {
            count++;
        }
    }
}