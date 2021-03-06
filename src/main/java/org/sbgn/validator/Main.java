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
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.ValidationEvent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.net.URL;
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
            Response response = new Response();
            response = Main.validateXsd(xml, response);
            response = Main.validateSchematron(xml, response);
            response = Main.validateRender(xml, response);
            response = Main.validateAnnotation(xml, response);

            Gson gson = new Gson();
            System.out.println(gson.toJson(response));
            return gson.toJson(response);

            /*return "Xsd valid: " + isXsdValid + "\nSchematron valid: " + isSchematronValid
                    + "\nRenderInfo valid: " + isRenderValid
                    + "\nAnnotation valid: " + isAnnotationValid;*/
        });

        // for testing purpose
        //Main.validateExtensions(xml);
        System.out.println("Spark is running...");
    }

    public static Response validateXsd (String stringXml, Response res) {
        File tempXmlFile = null;
        try {
            tempXmlFile = Main.toTempFile(stringXml);
        } catch (IOException e) {
            e.printStackTrace();
        }

        res.isXsdValid = false;
        try {
            res.isXsdValid = SbgnUtil.isValid(tempXmlFile);
        } catch (Exception e) {
            System.out.println("XSD validation exception");
            System.out.println(e.toString());
            res.xsdMessages.add("XSD error: " + e.toString());
        }

        return res;
    }

    public static Response validateSchematron (String stringXml, Response res) {
        File tempXmlFile = null;
        try {
            tempXmlFile = Main.toTempFile(stringXml);
        } catch (IOException e) {
            e.printStackTrace();
        }

        res.isSchematronValid = false;
        try {
            System.out.println("Validate schematron");
            List<Issue> l = SchematronValidator.validate(tempXmlFile);
            System.out.println("schematron errors: " + l.toString());
            if(l.size() == 0) {
                res.isSchematronValid = true;
            } else {
                for(Issue issue: l) {
                    System.out.println(issue.toString());
                    res.schematronMessages.add(issue.toString());
                }
            }
        } catch (Exception e) {
            System.out.println("Schematron validation exception");
            System.out.println(e.toString());
            e.printStackTrace();
            res.schematronMessages.add("Schematron error: " + e.toString());
        }

        return res;
    }

    private static File toTempFile(String s) throws IOException {
        File temp = File.createTempFile("validator", ".xml.tmp");
        temp.deleteOnExit();
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(s);
        bw.close();
        return temp;
    }

    public static Response validateRender(String stringXml, Response res) {
        File sbgnFile = null;
        try {
            sbgnFile = Main.toTempFile(stringXml);
        } catch (Exception e) {
            e.printStackTrace();
        }

        res.isRenderValid = false;

        /*Sbgn sbgn;
        try {
            sbgn= SbgnUtil.readFromFile(sbgnFile);
        } catch (JAXBException e) {
            e.printStackTrace();
        }*/

        System.out.println("before writing dom "+sbgnFile);
        // parse file as DOM
        Document doc = Main.fileAsDOM(sbgnFile);
        System.out.println("after getting dom "+ doc);

        NodeList nodeList=doc.getElementsByTagName("renderInformation");
        System.out.println("nodelist count "+nodeList.getLength());
        // no render extension present, consider it valid
        if(nodeList.getLength() == 0) {
            res.isRenderValid = true;
            return res;
        }

        // get renderInformation as string
        Element renderElement = (Element)nodeList.item(0); // only 1 renderInfo should be present in a file
        DOMImplementationLS lsImpl = (DOMImplementationLS)renderElement.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
        LSSerializer serializer = lsImpl.createLSSerializer();
        serializer.getDomConfig().setParameter("xml-declaration", false); //by default its true, so set it to false to get String without xml-declaration
        String renderinfoString = serializer.writeToString(renderElement);
        //System.out.println("after dom "+renderinfoString);

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
        System.out.println("after writing sbml");

        // write SBML mock document to string then inject renderInfo in it
        String sbmlWithRenderInfo = null;
        try {
            String model = new SBMLWriter().writeSBMLToString(sbmlDoc);
            sbmlWithRenderInfo = model.replace("<render:renderInformation/>", renderinfoString);
            System.out.println("SBML render mockup:\n"+sbmlWithRenderInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // load relaxng validator
        ValidationDriver vDriver = new com.thaiopensource.validate.ValidationDriver();
        try {
            InputStream is = getResource("/relaxng/sbml.rng");
            InputSource is2 = new InputSource(is);

            //ValidationDriver.fileInputSource();

            vDriver.loadSchema(is2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // validate
        try {

            // the validate function outputs errors to err
            // so we need to capture that

            // Create a stream to hold the output
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            // IMPORTANT: Save the old System.out!
            PrintStream old = System.err;
            PrintStream oldOut = System.out;
            // Tell Java to use your special stream
            System.setErr(ps);
            //System.setOut(ps);

            System.out.println("before render validate");
            res.isRenderValid = vDriver.validate(new InputSource(new StringReader(sbmlWithRenderInfo)));
            System.err.println("after after validate");

            // Put things back
            System.err.flush();
            //System.out.flush();
            //System.setOut(oldOut);
            System.setErr(old);
            // Show what happened
            System.out.println("Here: " + baos.toString()+" end");
        } catch (Exception e) {
            System.out.println("Render validation exception");
            System.out.println(e.toString());
            e.printStackTrace();
            res.renderMessages.add("Render error: " + e.toString());
        }

        return res;
    }

    public static Response validateAnnotation(String stringXml, Response res) {
        File sbgnFile = null;
        try {
            sbgnFile = Main.toTempFile(stringXml);
        } catch (IOException e) {
            e.printStackTrace();
        }
        res.isAnnotationValid = false;

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
        // no annotation extension present, consider it valid
        if(nodeList.getLength() == 0) {
            res.isAnnotationValid = true;
            return res;
        }

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

        SBMLDocument sbml = new SBMLDocument(3, 1);

        System.out.println(sbml);

        for(String s:annotationList) {
            //m.appendAnnotation(s);
            Model m = sbml.createModel("TestANNOTATIONS");
            System.out.println(m);
            try {
                m.getAnnotation().appendNonRDFAnnotation(s);
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*try {
                String output = new SBMLWriter().writeSBMLToString(sbml);
                System.out.println(output);
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }*/

            sbml.checkConsistency();

            System.out.println("error count: "+sbml.getNumErrors());
            System.out.println("list of errors: "+sbml.getListOfErrors().getValidationErrors().toString());

            sbml.printErrors(System.out);
        }

        return res;
    }

    private static Document fileAsDOM (File f) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            System.out.println("docbuilder "+db);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Document doc = null;
        try {
            doc = db.parse(f);
            System.out.println("in dom "+doc+" "+f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        doc.getDocumentElement().normalize();
        return doc;
    }

    public static InputStream getResource(String res) throws IOException
    {
        //InputStream url = Main.class.getResourceAsStream(res);
        InputStream url = new FileInputStream(res);
        if (url == null) throw new IOException("Could not find resource '" + res + "' in classpath");
        //return url.toString();
        return url;
    }

}

class Response {
    public boolean isXsdValid = false;
    public List<String> xsdMessages = new ArrayList<>();
    public boolean isSchematronValid = false;
    public List<String> schematronMessages = new ArrayList<>();
    public boolean isRenderValid = false;
    public List<String> renderMessages = new ArrayList<>();
    public boolean isAnnotationValid = false;
    public List<String> annotationMessages = new ArrayList<>();
}
