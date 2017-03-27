import org.basex.api.client.ClientQuery;
import org.basex.api.client.ClientSession;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.XQuery;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.value.Value;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

public class Main {

    private final static String GENERIC_PATH = "C:\\Users\\Arfera\\Desktop\\EXERCICI_1\\";
    private final static String PLANETS_ENDPOINT = "planets.xml";

    public static void main(String[] args) {
	// write your code here

        //Scanner per comunicar-me amb l'usuari.
        Scanner in = new Scanner(System.in);

        //Variable per preguntar a l'usuari què vol fer al gestor...
        int opt;
        //Variable de sortida
        char surt;


        //Repeteixo el procés tantes vegades com l'usuari vulgui.
        do{

            System.out.println("\t\tConnexió a BaseX i consulta XPATH (1)");
            System.out.println("\t\tConsulta XQuery a BaseX (2)");
            opt = in.nextInt();

            switch(opt){
                case 1:
                    xpathQueryBasex();
                    break;

                case 2:
                    xqueryBasex();
                    break;

            }

            //Per últim pregunto si l'usuari vol continuar a la base de dades.
            System.out.println("\t\tVols sortir del gestor?(s/n)");
            surt = in.next().charAt(0);
            if(surt=='s'){
                System.out.println("\t\tAdéu!");
            }
        }while(surt=='n');


    }




    /**
     * Mètode que permet fer consultes XQuery a BaseX.
     */
    private static void xqueryBasex() {

        String res ="";
        Context context = new Context();
        String query =
                "<RESULT>{for $x in doc('" + GENERIC_PATH + PLANETS_ENDPOINT + "')//PLANETS/PLANET " +
                        "where $x/DISTANCE>100 return " +
                        "<PLANET> " +
                        "{$x/NAME} " +
                        "<COLOR>{data($x/@COLOR)}</COLOR> " +
                        "{$x/DISTANCE} " +
                        "</PLANET>}</RESULT>";

        try {
            System.out.println(new XQuery(query).execute(context));

            res = new XQuery(query).execute(context);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(res)));
            NodeList nodeList = doc.getElementsByTagName("PLANET");

            for (int i = 0; i <nodeList.getLength(); i++) {
                Element planet = (Element) nodeList.item(i);
                System.out.println("El planeta " + planet.getElementsByTagName("NAME").item(0).getTextContent() + " és de color " +
                planet.getElementsByTagName("COLOR").item(0).getTextContent() + " i es troba a una distància de " + planet.getElementsByTagName("DISTANCE").item(0).getTextContent());
            }



        } catch (BaseXException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Mètode que permet fer consultes xpath a Basex.
     */
    private static void xpathQueryBasex() {

        try {
            ClientSession session = new ClientSession("localhost", 1984, "admin", "admin");

            //Creo la base de dades per fer la query.
            session.execute(new CreateDB("input", GENERIC_PATH + PLANETS_ENDPOINT));

            //Executo la query.
            ClientQuery query = session.query("//PLANETS");
            System.out.println(query.execute());

            if(query==null){
                System.out.println("Query didn't find nothing!");
            }

            session.setOutputStream(null);
            session.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
