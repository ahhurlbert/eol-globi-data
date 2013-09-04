package org.eol.globi.export;

import org.eol.globi.domain.InteractType;
import org.eol.globi.domain.Specimen;
import org.eol.globi.domain.Study;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class ExporterMeasurementOrFact extends ExporterBase {

    protected String[] getFields() {
        return new String[]{
                EOLDictionary.MEASUREMENT_ID,
                EOLDictionary.OCCURRENCE_ID,
                EOLDictionary.MEASUREMENT_OF_TAXON,
                EOLDictionary.ASSOCIATION_ID,
                EOLDictionary.MEASUREMENT_TYPE,
                EOLDictionary.MEASUREMENT_VALUE,
                EOLDictionary.MEASUREMENT_UNIT,
                EOLDictionary.MEASUREMENT_ACCURACY,
                EOLDictionary.MEASUREMENT_DETERMINED_DATE,
                EOLDictionary.MEASUREMENT_DETERMINED_BY,
                EOLDictionary.MEASUREMENT_METHOD,
                EOLDictionary.MEASUREMENT_REMARKS,
                EOLDictionary.SOURCE,
                EOLDictionary.BIBLIOGRAPHIC_CITATION,
                EOLDictionary.CONTRIBUTOR
        };
    }

    @Override
    public void doExportStudy(Study study, Writer writer, boolean includeHeader) throws IOException {
        Map<String, String> properties = new HashMap<String, String>();

        Iterable<Relationship> specimens = study.getSpecimens();
        for (Relationship collectedRel : specimens) {
            Node specimenNode = collectedRel.getEndNode();
            writeMeasurements(writer, properties, specimenNode, collectedRel, study);

            Iterable<Relationship> interactRelationships = specimenNode.getRelationships(Direction.OUTGOING, InteractType.values());
            if (interactRelationships.iterator().hasNext()) {
                for (Relationship interactRel : interactRelationships) {
                    writeMeasurements(writer, properties, interactRel.getEndNode(), collectedRel, study);
                }
            }
        }
    }

    private void writeMeasurements(Writer writer, Map<String, String> properties, Node specimenNode, Relationship collectedRel, Study study) throws IOException {

        if (specimenNode.hasProperty(Specimen.LENGTH_IN_MM)) {
            Object property = specimenNode.getProperty(Specimen.LENGTH_IN_MM);
            properties.put(EOLDictionary.MEASUREMENT_VALUE, property.toString());
            properties.put(EOLDictionary.MEASUREMENT_TYPE, "specimen length");
            properties.put(EOLDictionary.MEASUREMENT_ID, "globi:occur:length:" + specimenNode.getId());
            properties.put(EOLDictionary.MEASUREMENT_UNIT, "http://purl.obolibrary.org/obo/UO_0000016");
            properties.put(EOLDictionary.MEASUREMENT_OF_TAXON, "yes");
            addCommonProperties(properties, specimenNode, collectedRel, study);
            writeProperties(writer, properties);
        }

        if (specimenNode.hasProperty(Specimen.STOMACH_VOLUME_ML)) {
            Object property = specimenNode.getProperty(Specimen.STOMACH_VOLUME_ML);
            properties.put(EOLDictionary.MEASUREMENT_VALUE, property.toString());
            properties.put(EOLDictionary.MEASUREMENT_TYPE, "stomach volume");
            properties.put(EOLDictionary.MEASUREMENT_OF_TAXON, "yes");
            properties.put(EOLDictionary.MEASUREMENT_ID, "globi:occur:stomach_volume:" + specimenNode.getId());
            properties.put(EOLDictionary.MEASUREMENT_UNIT, "http://purl.obolibrary.org/obo/UO_0000098");
            addCommonProperties(properties, specimenNode, collectedRel, study);
            writeProperties(writer, properties);
        }

        if (specimenNode.hasProperty(Specimen.VOLUME_IN_ML)) {
            Object property = specimenNode.getProperty(Specimen.VOLUME_IN_ML);
            properties.put(EOLDictionary.MEASUREMENT_VALUE, property.toString());
            properties.put(EOLDictionary.MEASUREMENT_TYPE, "volume");
            properties.put(EOLDictionary.MEASUREMENT_ID, "globi:occur:volume:" + specimenNode.getId());
            properties.put(EOLDictionary.MEASUREMENT_UNIT, "http://purl.obolibrary.org/obo/UO_0000098");
            properties.put(EOLDictionary.MEASUREMENT_OF_TAXON, "yes");
            addCommonProperties(properties, specimenNode, collectedRel, study);
            writeProperties(writer, properties);
        }
    }

    private void addCommonProperties(Map<String, String> properties, Node specimenNode, Relationship collectedRel, Study study) throws IOException {
        properties.put(EOLDictionary.SOURCE, study.getTitle());
        properties.put(EOLDictionary.CONTRIBUTOR, study.getContributor());
        addCollectionDate(properties, collectedRel, EOLDictionary.MEASUREMENT_DETERMINED_DATE);
        properties.put(EOLDictionary.OCCURRENCE_ID, "globi:occur:" + specimenNode.getId());
    }
}