package com.example.MTConnect;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.glassfish.jaxb.runtime.v2.runtime.IllegalAnnotationsException;
import org.testng.annotations.Test;

public class JaxbDebugTest {
    @Test
    void debugJaxb() {
        try {
            JAXBContext.newInstance(com.example.MTConnect.model.MTConnectStreams.class);
        } catch (JAXBException ex) {
            System.err.println("JAXBContext error: " + ex.getMessage());
            Throwable linked = ex.getLinkedException();
            if (linked instanceof IllegalAnnotationsException iae) {
                iae.getErrors().forEach(err -> System.err.println(" - " + err));
            } else if (linked != null) {
                linked.printStackTrace();
            } else {
                ex.printStackTrace();
            }
            throw new AssertionError("See stderr for details");
        }
    }
}
