package org.sbgn.validator;

import com.google.gson.Gson;
import com.thaiopensource.validate.ValidationDriver;
import org.sbgn.SbgnUtil;
import org.sbgn.schematron.Issue;
import org.sbgn.schematron.SchematronValidator;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutModelPlugin;
import org.sbml.jsbml.ext.render.RenderLayoutPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import static spark.Spark.*;

import javax.xml.bind.JAXBException;
import javax.xml.bind.ValidationEvent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static String renderExt = "<extension>\n" +
            "      <renderInformation \n" +
            "        xmlns=\"http://www.sbml.org/sbml/level3/version1/render/version1\" id=\"renderInformation\" programName=\"sbgnviz\" programVersion=\"3.1.0\" backgroundColor=\"#000000\">\n" +
            "        <listOfColorDefinitions>\n" +
            "          <colorDefinition id=\"color_1\" value=\"#ffffff7f\"/>\n" +
            "          <colorDefinition id=\"color_2\" value=\"#555555\"/>\n" +
            "          <colorDefinition id=\"color_3\" value=\"#ff6c7d7f\"/>\n" +
            "        </listOfColorDefinitions>\n" +
            "        <listOfStyles>\n" +
            "          <style id=\"nodeffffff0.55555553.2514normalnormalHelvetica\" idList=\"_82f19e9e-6aa2-42b3-8b5e-8cee17197085 _9007194d-c2e6-4939-a795-be71ae35a60d e95a1256-8a47-4aaf-b3d8-7d8387d5a31d _23d436e2-6633-42f2-98d0-00dbb962ac3d\">\n" +
            "            <g fontSize=\"14\" fontFamily=\"Helvetica\" fontWeight=\"normal\" fontStyle=\"normal\" stroke=\"color_2\" strokeWidth=\"3.25\" fill=\"color_1\"/>\n" +
            "          </style>\n" +
            "          <style id=\"nodeffffff0.55555551.2511normalnormalHelvetica\" idList=\"_66737d5c-5193-43a2-baa6-094aa1c21654 be08c23c-dca4-43cd-b8a6-06f6ebdaf710 a083be43-422d-9067-c688-661cec17e472 d2531344-d005-4b92-afd2-14dcb7d40965 _135e2807-2b5c-4886-bbdf-d3c8bf433ab9 ed79ab12-7b79-4a8a-80b3-c65181c076e4 ce103257-dbf5-430f-9e6c-1745990341de _9886817c-7043-4de9-9895-0cd247f47190 b88b6460-4172-427f-abbe-d7c0f0a174ce fb8eb6bc-a39a-4361-b938-026a3b63a9b9 _8ba53135-01b4-4a4b-b87d-a5971850cb39 _06e41c55-1352-407e-9c4b-604d3f5f616b f3ac8950-6d69-4c5b-92dd-3020906ea2c8 _4b4a1add-6cf6-47cb-b952-5ea0ceade9a0 _8c083cee-7025-4035-9201-1319da22340b ac2f1b2b-2e6e-4647-84bd-c141925f4d85 _1129b8c7-71da-4a6c-8681-3082e6f04a6c _26a815b4-64c3-4797-9bfd-7dceafb3345d _5fcbfc9c-92a0-45b2-834b-ad8866d4008b _9b7f467a-30eb-4b8e-a966-43692648a46e _20e434c6-e738-4ae5-8a8a-e49f53fe85fb _85682413-94f7-8b3c-e052-59a72cd48e12 _1ce3ffae-f9a7-09a1-c91f-bf176708c1d2 _0fbd0d13-c59b-48ab-240d-c262dd1bc480 e889d01c-1eb1-4c83-8bd9-0160e9c2abcb _04ea25d8-dfaa-7912-303f-be00b342fc27 _343e0d0d-eeac-5cf5-acec-bc15975650a3 _96e17352-dd50-0332-a224-954cd97fc3c2 a3f830ac-34e3-466a-62ac-642bc3e51777 _6259ebd3-e879-4c70-b442-a602e5408989 fb04887c-b0a0-4a2d-8cdd-a70826c659ee\">\n" +
            "            <g fontSize=\"11\" fontFamily=\"Helvetica\" fontWeight=\"normal\" fontStyle=\"normal\" stroke=\"color_2\" strokeWidth=\"1.25\" fill=\"color_1\"/>\n" +
            "          </style>\n" +
            "          <style id=\"nodeffffff0.55555551.25\" idList=\"_96b935d7-083c-468d-8607-d3b70fff10a7 _2bdf0056-96f6-4184-a214-8c7cc7da6214 _3797d8d2-4f00-4d1f-9d85-ecad1e61746d _6865aebe-2564-4ff6-a15a-6bd07a3a0ef8 d9e08f4b-94b8-4285-af36-f4159b56a2fc _264a7088-e810-4679-9c92-f06886510f98 d756f4ab-9813-4d55-aa86-c37dc7f3b286 _3a96c155-e828-4038-8384-4c25f4f1aaf6 _97c2c87b-c5d2-41fb-97f6-a1001075cf08 _00ac7e0a-288f-42e0-b252-9bcb59027572 _1e9c2dbf-2a4d-48c9-9d47-de9c0314f5b8\">\n" +
            "            <g stroke=\"color_2\" strokeWidth=\"1.25\" fill=\"color_1\"/>\n" +
            "          </style>\n" +
            "          <style id=\"nodeff6c7d0.5555555511normalnormalHelvetica\" idList=\"a0a8dd07-644b-4646-a305-ef249fa67752\">\n" +
            "            <g fontSize=\"11\" fontFamily=\"Helvetica\" fontWeight=\"normal\" fontStyle=\"normal\" stroke=\"color_2\" strokeWidth=\"5\" fill=\"color_3\"/>\n" +
            "          </style>\n" +
            "          <style id=\"edge5555551.25\" idList=\"_3797d8d2-4f00-4d1f-9d85-ecad1e61746d-a083be43-422d-9067-c688-661cec17e472 d2531344-d005-4b92-afd2-14dcb7d40965-3797d8d2-4f00-4d1f-9d85-ecad1e61746d _9886817c-7043-4de9-9895-0cd247f47190-3797d8d2-4f00-4d1f-9d85-ecad1e61746d ce103257-dbf5-430f-9e6c-1745990341de-2bdf0056-96f6-4184-a214-8c7cc7da6214 ed79ab12-7b79-4a8a-80b3-c65181c076e4-2bdf0056-96f6-4184-a214-8c7cc7da6214 _135e2807-2b5c-4886-bbdf-d3c8bf433ab9-2bdf0056-96f6-4184-a214-8c7cc7da6214 _2bdf0056-96f6-4184-a214-8c7cc7da6214-d2531344-d005-4b92-afd2-14dcb7d40965 _96b935d7-083c-468d-8607-d3b70fff10a7-135e2807-2b5c-4886-bbdf-d3c8bf433ab9 a083be43-422d-9067-c688-661cec17e472-d9e08f4b-94b8-4285-af36-f4159b56a2fc _66737d5c-5193-43a2-baa6-094aa1c21654-96b935d7-083c-468d-8607-d3b70fff10a7 fb8eb6bc-a39a-4361-b938-026a3b63a9b9-96b935d7-083c-468d-8607-d3b70fff10a7 be08c23c-dca4-43cd-b8a6-06f6ebdaf710-d9e08f4b-94b8-4285-af36-f4159b56a2fc d9e08f4b-94b8-4285-af36-f4159b56a2fc-06e41c55-1352-407e-9c4b-604d3f5f616b _06e41c55-1352-407e-9c4b-604d3f5f616b-6865aebe-2564-4ff6-a15a-6bd07a3a0ef8 b88b6460-4172-427f-abbe-d7c0f0a174ce-6865aebe-2564-4ff6-a15a-6bd07a3a0ef8 _6865aebe-2564-4ff6-a15a-6bd07a3a0ef8-fb8eb6bc-a39a-4361-b938-026a3b63a9b9 _6865aebe-2564-4ff6-a15a-6bd07a3a0ef8-8ba53135-01b4-4a4b-b87d-a5971850cb39 f3ac8950-6d69-4c5b-92dd-3020906ea2c8-264a7088-e810-4679-9c92-f06886510f98 _264a7088-e810-4679-9c92-f06886510f98-4b4a1add-6cf6-47cb-b952-5ea0ceade9a0 _4b4a1add-6cf6-47cb-b952-5ea0ceade9a0-d756f4ab-9813-4d55-aa86-c37dc7f3b286 _8c083cee-7025-4035-9201-1319da22340b-d756f4ab-9813-4d55-aa86-c37dc7f3b286 d756f4ab-9813-4d55-aa86-c37dc7f3b286-ac2f1b2b-2e6e-4647-84bd-c141925f4d85 ac2f1b2b-2e6e-4647-84bd-c141925f4d85-3a96c155-e828-4038-8384-4c25f4f1aaf6 _06e41c55-1352-407e-9c4b-604d3f5f616b-264a7088-e810-4679-9c92-f06886510f98 _1129b8c7-71da-4a6c-8681-3082e6f04a6c-3a96c155-e828-4038-8384-4c25f4f1aaf6 _00ac7e0a-288f-42e0-b252-9bcb59027572-1129b8c7-71da-4a6c-8681-3082e6f04a6c _9b7f467a-30eb-4b8e-a966-43692648a46e-3a96c155-e828-4038-8384-4c25f4f1aaf6 _97c2c87b-c5d2-41fb-97f6-a1001075cf08-9b7f467a-30eb-4b8e-a966-43692648a46e _3a96c155-e828-4038-8384-4c25f4f1aaf6-04ea25d8-dfaa-7912-303f-be00b342fc27 _97c2c87b-c5d2-41fb-97f6-a1001075cf08-a0a8dd07-644b-4646-a305-ef249fa67752 _85682413-94f7-8b3c-e052-59a72cd48e12-97c2c87b-c5d2-41fb-97f6-a1001075cf08 a0a8dd07-644b-4646-a305-ef249fa67752-00ac7e0a-288f-42e0-b252-9bcb59027572 _20e434c6-e738-4ae5-8a8a-e49f53fe85fb-00ac7e0a-288f-42e0-b252-9bcb59027572 _85682413-94f7-8b3c-e052-59a72cd48e12-e889d01c-1eb1-4c83-8bd9-0160e9c2abcb _1e9c2dbf-2a4d-48c9-9d47-de9c0314f5b8-85682413-94f7-8b3c-e052-59a72cd48e12 _04ea25d8-dfaa-7912-303f-be00b342fc27-1e9c2dbf-2a4d-48c9-9d47-de9c0314f5b8 _1e9c2dbf-2a4d-48c9-9d47-de9c0314f5b8-6259ebd3-e879-4c70-b442-a602e5408989 _1e9c2dbf-2a4d-48c9-9d47-de9c0314f5b8-fb04887c-b0a0-4a2d-8cdd-a70826c659ee\">\n" +
            "            <g stroke=\"color_2\" strokeWidth=\"1.25\"/>\n" +
            "          </style>\n" +
            "        </listOfStyles>\n" +
            "      </renderInformation>\n" +
            "    </extension>";

    static String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>\n" +
            "<sbgn \n" +
            "  xmlns=\"http://sbgn.org/libsbgn/0.2\">\n" +
            "  <map language=\"process description\">\n" +
            renderExt +
            "  \t<glyph id=\"_23d436e2-6633-42f2-98d0-00dbb962ac3d\" class=\"compartment\">\n" +
            "      <label text=\"muscle cytosol\"/>\n" +
            "      <bbox x=\"761.1069337742531\" y=\"165.66118664676014\" w=\"475.0172196360252\" h=\"404.1823563460308\"/>\n" +
            "      <extension>\n" +
            "        <annotation>\n" +
            "          <rdf:RDF \n" +
            "            xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n" +
            "            xmlns:sio=\"http://semanticscience.org/resource/\"" +
            "               >\n" +
            "            <rdf:Description rdf:about=\"#a0a8dd07-644b-4646-a305-ef249fa67752\">\n" +
            "              <sio:SIO_000223>\n" +
            "                <rdf:Bag>\n" +
            "                  <rdf:li sio:SIO_000116=\"TEST\" rdf:value=\"value\"/>\n" +
            "                </rdf:Bag>\n" +
            "              </sio:SIO_000223>\n" +
            "            </rdf:Description>\n" +
            "          </rdf:RDF>\n" +
            "        </annotation>\n" +
            "      </extension>\n" +
            "    </glyph>\n" +
            "  \t<glyph id=\"a0a8dd07-644b-4646-a305-ef249fa67752\" class=\"macromolecule\" compartmentRef=\"_23d436e2-6633-42f2-98d0-00dbb962ac3d\">\n" +
            "      <label text=\"myosin\"/>\n" +
            "      <bbox x=\"1133.2858326079895\" y=\"260.05664504963187\" w=\"70\" h=\"35\"/>\n" +
            "      <extension>\n" +
            "        <annotation>\n" +
            "          <rdf:RDF \n" +
            "            xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n" +
            "            xmlns:sio=\"http://semanticscience.org/resource/\"" +
            "               >\n" +
            "            <rdf:Description rdf:about=\"#a0a8dd07-644b-4646-a305-ef249fa67752\">\n" +
            "              <sio:SIO_000223>\n" +
            "                <rdf:Bag>\n" +
            "                  <rdf:li sio:SIO_000116=\"TEST\" rdf:value=\"value\"/>\n" +
            "                </rdf:Bag>\n" +
            "              </sio:SIO_000223>\n" +
            "            </rdf:Description>\n" +
            "          </rdf:RDF>\n" +
            "        </annotation>\n" +
            "      </extension>\n" +
            "    </glyph>\n" +
            "  </map>\n" +
            "</sbgn>";

    public static void main(String[] args) {
        post("/returnString", "application/xml", (req, res) -> {
            String xml = req.queryParams("xml");

            return xml;
        });

        post("/test", (req, res) -> {
            return "hello world";
        });

        // test cmd:
        // curl -X POST --data "xml=$(cat src/main/resources/example.sbgn)" http://localhost:4567/validateString

        post("/validateString", "application/xml", (req, res) -> {
            String xml = req.queryParams("xml");
            //System.out.println("string input: " + xml);
            boolean isXsdValid = Main.validateXsd(xml);
            boolean isSchematronValid = Main.validateSchematron(xml);
            boolean isRenderValid = Main.validateRender(xml);
            boolean isAnnotationValid = Main.validateAnnotation(xml);

            Gson gson = new Gson();
            return gson.toJson(isXsdValid);

            /*return "Xsd valid: " + isXsdValid + "\nSchematron valid: " + isSchematronValid
                    + "\nRenderInfo valid: " + isRenderValid
                    + "\nAnnotation valid: " + isAnnotationValid;*/
        });

        // for testing purpose
        Main.validateExtensions(xml);
    }

    public static boolean validateXsd (String stringXml) {
        File tempXmlFile = null;
        try {
            tempXmlFile = Main.toTempFile(stringXml);
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean result = false;
        try {
            result = SbgnUtil.isValid(tempXmlFile);
        } catch (SAXException sex) {
            sex.printStackTrace();
            /*StackTraceElement elements[] = sex.getStackTrace();
            for (int i = 0, n = elements.length; i < n; i++) {
                System.err.println(elements[i].getFileName()
                        + ":" + elements[i].getLineNumber()
                        + ">> "
                        + elements[i].getMethodName() + "()");
            }*/
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean validateSchematron (String stringXml) {
        File tempXmlFile = null;
        try {
            tempXmlFile = Main.toTempFile(stringXml);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            List<Issue> l = SchematronValidator.validate(tempXmlFile);
            System.out.println("schematron " + l.toString());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static File toTempFile(String s) throws IOException {
        File temp = File.createTempFile("validator", ".xml.tmp");
        temp.deleteOnExit();
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(s);
        bw.close();
        return temp;
    }

    public static boolean validateExtensions(String stringXml) {
        return validateRender(stringXml);
    }

    public static boolean validateRender(String stringXml) {
        File sbgnFile = null;
        try {
            sbgnFile = Main.toTempFile(stringXml);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*Sbgn sbgn;
        try {
            sbgn= SbgnUtil.readFromFile(sbgnFile);
        } catch (JAXBException e) {
            e.printStackTrace();
        }*/

        // parse file as DOM
        Document doc = Main.fileAsDOM(sbgnFile);

        // get renderInformation as string
        NodeList nodeList=doc.getElementsByTagName("renderInformation");
        Element renderElement = (Element)nodeList.item(0); // only 1 renderInfo should be present in a file
        DOMImplementationLS lsImpl = (DOMImplementationLS)renderElement.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
        LSSerializer serializer = lsImpl.createLSSerializer();
        serializer.getDomConfig().setParameter("xml-declaration", false); //by default its true, so set it to false to get String without xml-declaration
        String renderinfoString = serializer.writeToString(renderElement);

        //System.out.println(renderinfoString);

        // write mock SBML
        SBMLDocument sbmlDoc = new SBMLDocument(3, 1);

        System.out.println(sbmlDoc);

        Model m = sbmlDoc.createModel("testRenderInfo");
        System.out.println(m);

        LayoutModelPlugin mLayout = (LayoutModelPlugin) m.createPlugin("layout");

        Layout l = mLayout.createLayout("EmptyLayout");

        //RenderListOfLayoutsPlugin renderPlugin = (RenderListOfLayoutsPlugin) mLayout.getListOfLayouts().createPlugin("render");

        RenderLayoutPlugin rl = (RenderLayoutPlugin) l.createPlugin("render");

        rl.getListOfLocalRenderInformation();
        rl.createLocalRenderInformation();

        // write SBML mock document to string then inject renderInfo in it
        String sbmlWithRenderInfo = null;
        try {
            String model = new SBMLWriter().writeSBMLToString(sbmlDoc);
            sbmlWithRenderInfo = model.replace("<render:renderInformation/>", renderinfoString);
            //System.out.println(sbmlWithRenderInfo);
        } catch (SBMLException | XMLStreamException e) {
            e.printStackTrace();
        }

        // load relaxng validator
        ValidationDriver vDriver = new com.thaiopensource.validate.ValidationDriver();
        try {
            vDriver.loadSchema(ValidationDriver.fileInputSource("src/main/resources/relaxng/sbml.rng"));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // validate
        try {
            vDriver.validate(new InputSource(new StringReader(sbmlWithRenderInfo)));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return false;
    }

    public static boolean validateAnnotation(String stringXml) {
        File sbgnFile = null;
        try {
            sbgnFile = Main.toTempFile(stringXml);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*Sbgn sbgn;
        try {
            sbgn= SbgnUtil.readFromFile(sbgnFile);
        } catch (JAXBException e) {
            e.printStackTrace();
        }*/

        // parse file as DOM
        Document doc = Main.fileAsDOM(sbgnFile);

        // extract annotation elements and transform to string
        NodeList nodeList=doc.getElementsByTagName("annotation");
        List<String> annotationList = new ArrayList<>();
        for (int i=0; i<nodeList.getLength(); i++)
        {
            // Get element
            Element element = (Element)nodeList.item(i);

            // transform to string
            DOMImplementationLS lsImpl = (DOMImplementationLS)element.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
            LSSerializer serializer = lsImpl.createLSSerializer();
            serializer.getDomConfig().setParameter("xml-declaration", false); //by default its true, so set it to false to get String without xml-declaration
            String annotationString = serializer.writeToString(element);
            annotationList.add(annotationString);
        }

        Main.buildMockSBML(annotationList);

        return false;
    }

    private static void buildMockSBML(List<String> annotationList) {
        SBMLDocument doc = new SBMLDocument(3, 1);

        System.out.println(doc);

        for(String s:annotationList) {
            //m.appendAnnotation(s);
            Model m = doc.createModel("TestANNOTATIONS");
            System.out.println(m);
            try {
                m.getAnnotation().appendNonRDFAnnotation(s);
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
            System.out.println(m.getAnnotation().getFullAnnotationString());

            doc.checkConsistency();

            System.out.println("error count: "+doc.getNumErrors());

            doc.printErrors(System.out);
        }


    }

    private static Document fileAsDOM (File f) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document doc = null;
        try {
            doc = db.parse(f);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doc.getDocumentElement().normalize();
        return doc;
    }

}
