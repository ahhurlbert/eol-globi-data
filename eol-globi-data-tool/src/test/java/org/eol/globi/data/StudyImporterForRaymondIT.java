package org.eol.globi.data;

import org.eol.globi.service.GeoNamesService;
import org.eol.globi.service.GeoNamesServiceImpl;
import org.junit.Test;
import uk.me.jstott.jcoord.LatLng;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class StudyImporterForRaymondIT extends GraphDBTestCase {

    @Test
    public void importStudy() throws StudyImporterException {
        StudyImporterForRaymond importer = new StudyImporterForRaymond(new ParserFactoryImpl(), nodeFactory);
        importer.setGeoNamesService(new GeoNamesService() {
            @Override
            public boolean hasPositionForLocality(String spireLocality) {
                return true;
            }

            @Override
            public LatLng findPointForLocality(String spireLocality) throws IOException {
                return new LatLng(0, 0);
            }

            @Override
            public LatLng findLatLng(Long id) throws IOException {
                return new LatLng(0, 0);
            }
        });
        importer.importStudy();

        importer.setGeoNamesService(new GeoNamesServiceImpl());

        Collection<String> unmappedLocations = new HashSet<String>();
        for (String location : importer.getLocations()) {
            if (!importer.getGeoNamesService().hasPositionForLocality(location)) {
                unmappedLocations.add(location);
            }
        }

        assertThat(unmappedLocations,
                containsInAnyOrder("Not described",
                        "South African waters",
                        "Ocean location",
                        "subantarctic waters",
                        "oceanic habitat in Southern Ocean. 68� 07\u0019 S & 70�13\u0019 S",
                        "Subantarctic Pacific Ocean"));


    }
}