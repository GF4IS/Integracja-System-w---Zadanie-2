package is_baza;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONException;
import java.util.regex.Pattern;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//Wymagane biblioteki: java-json.jar, mysql-connector-java-5.1.40-bin.jar, xercesImpl-2.9.1.jar

public class IS_Baza extends JFrame {
    String ID, Name, Type, Genre, Average, Language, Status, Runtime, Premiered, Network, Country, URL, Image, Thumbnail, Serie, XML, XML_F, wybor, SELECT;
    private final String XML_BEGIN = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><TVSeries>";
    private final String XML_END = "</TVSeries>";
    private final String SERIE_END = "</serie>";
    private final String CONNECTION = ""; // set connection (jdbc:mysql://<host>:3306/<database>?zeroDateTimeBehavior=convertToNull)
    private final String USER = ""; // set username
    private final String PASS = ""; // set password
    private final String SET_CONCAT_LENGTH = "SET SESSION group_concat_max_len = 1000000";

    JPanel myjPanel;
    JTextArea myjTextArea;
    JButton myjButton, myjButton2, myjButton3, myjButton4, myjButton5;
    JComboBox myjComboBox, myjComboBox2;
    JScrollPane myjScrollPane;
    JLabel myjLabel, myjLabel2;
    JRadioButton optionSimple, optionAll;
    ButtonGroup tryb;

    public IS_Baza() {
	setTitle("IS_Baza");
	setSize(800,400);
        setResizable(false);
	setLocation(400,200); 
        setMinimumSize(new Dimension(800, 400));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
		System.exit(0);
            }
	} );
        FrameInit();
    }

    public static void main(String[] args) throws JSONException, IOException, MalformedURLException, ClassNotFoundException, SQLException {
        IS_Baza baza = new IS_Baza();
        baza.show();
    }
    
    public static String stringRemoveChars(String str) {
        if (str != null && str.length() > 0) {
            str = str.substring(0, str.length()-2);
        }
        return str;
    }
    
    private void runStoredFunction() {
        try {
            PreparedStatement ct, ss;
            Connection conn = DriverManager.getConnection(CONNECTION, USER, PASS);
            ss = conn.prepareStatement(SET_CONCAT_LENGTH);
            ss.executeUpdate();
            wybor = myjComboBox.getSelectedItem().toString();
            if(wybor.equals("Wszystkie serie")) SELECT = "SELECT Funkcja1()";
            else if(wybor.equals("Same tytuły")) SELECT = "SELECT Funkcja2()";
            else if(wybor.equals("Serie o id powyżej...")) SELECT = "SELECT Funkcja3("+Integer.valueOf(myjComboBox2.getSelectedItem().toString())+")";
            else if(wybor.equals("Serie o id poniżej...")) SELECT = "SELECT Funkcja4("+Integer.valueOf(myjComboBox2.getSelectedItem().toString())+")";
            else if(wybor.equals("Serie o średniej powyżej...")) SELECT = "SELECT Funkcja5("+Double.valueOf(myjComboBox2.getSelectedItem().toString())+")";
            else if(wybor.equals("Serie o średniej poniżej...")) SELECT = "SELECT Funkcja6("+Double.valueOf(myjComboBox2.getSelectedItem().toString())+")";
            else if(wybor.equals("Serie z kraju...")) {
                String kraj = myjComboBox2.getSelectedItem().toString();
                if(kraj.equals("USA")) SELECT = "SELECT Funkcja7('United States')";
                else if(kraj.equals("Kanada")) SELECT = "SELECT Funkcja7('Canada')";
                else if(kraj.equals("Japonia")) SELECT = "SELECT Funkcja7('Japan')";
                else if(kraj.equals("Francja")) SELECT = "SELECT Funkcja7('France')";
                else if(kraj.equals("Wielka Brytania")) SELECT = "SELECT Funkcja7('United Kingdom')";
                else if(kraj.equals("Irlandia")) SELECT = "SELECT Funkcja7('Ireland')"; 
            }
            else if(wybor.equals("Serie typu...")) {
                String typ = myjComboBox2.getSelectedItem().toString();
                if(typ.equals("Fabularne")) SELECT = "SELECT Funkcja8('Scripted')";
                else if(typ.equals("Animowane")) SELECT = "SELECT Funkcja8('Animation')";
                else if(typ.equals("Dokumentalne")) SELECT = "SELECT Funkcja8('Documentary')";
                else if(typ.equals("Reality Show")) SELECT = "SELECT Funkcja8('Reality')";
                else if(typ.equals("Talk Show")) SELECT = "SELECT Funkcja8('Talk Show')";
                else if(typ.equals("Pozostałe")) SELECT = "SELECT Funkcja8('Variety')";
            }
            else if(wybor.equals("Serie o statusie...")) {
                String status = myjComboBox2.getSelectedItem().toString();
                if(status.equals("Trwające")) SELECT = "SELECT Funkcja9('Running')";
                if(status.equals("Zakończone")) SELECT = "SELECT Funkcja9('Ended')";
                if(status.equals("Oczekujące na decyzję")) SELECT = "SELECT Funkcja9('To Be Determined')";
            }
            else if(wybor.equals("Serie stacji...")) SELECT = "SELECT Funkcja10('"+myjComboBox2.getSelectedItem().toString()+"')";
            else SELECT = "";
            ct = conn.prepareStatement(SELECT);
            ResultSet res = ct.executeQuery();
            if(res.next()){
                String resst = res.getString(1);
                resst = stringRemoveChars(resst);
                XML = XML_BEGIN;
                try {
                    String[] rows = resst.split(Pattern.quote("$#,"));

                    for (String row : rows){
                        String[] parts = row.split(Pattern.quote("$%"));

                        ID = "<serie id=\"" + parts[0] + "\">";
                        Name = "<name>" + parts[1] + "</name>";
                        if(!wybor.equals("Same tytuły"))
                        {
                            Type = "<type>" + parts[2] + "</type>";
                            Genre = "<genre>" + parts[3] + "</genre>";
                            Average = "<average>" + parts[4] + "</average>";

                            if(optionAll.isSelected())
                            {
                                Language = "<language>" + parts[5] + "</language>";
                                Status = "<status>" + parts[6] + "</status>";
                                Runtime = "<runtime>" + parts[7] + "</runtime>"; 
                                Premiered = "<premiered>" + parts[8] + "</premiered>"; 
                                Network = "<network>" + parts[9] + "</network>";
                                Country = "<country>" + parts[10] + "</country>";
                                URL = "<url>" + parts[11] + "</url>";
                                Image = "<image>" + parts[12] + "</image>";
                                Thumbnail = "<thumbnail>" + parts[13] + "</thumbnail>";

                                Serie = ID+Name+Type+Genre+Average+Language+Status+Runtime+Premiered+Network+Country+URL+Image+Thumbnail+SERIE_END;
                                XML+=Serie;
                            }
                            else
                            {
                                Serie = ID+Name+Type+Genre+Average+SERIE_END;
                                XML+=Serie;
                            }
                        }
                        else {
                            Serie = ID+Name+SERIE_END;
                            XML+=Serie;
                        }
                    }
                }
                catch (Exception e){}
                
                XML+=XML_END;
                XML_F = XmlFormatter.format(XML);
                myjTextArea.setText(XML_F);
                myjTextArea.grabFocus();
                myjTextArea.setCaretPosition(0); 
            }
        }
        catch (SQLException e){}
    }
    
    private void copyToClipboard() {
        StringSelection stringSelection = new StringSelection(myjTextArea.getText());
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }
    
    private void cleanTextArea() {
        myjTextArea.setText("");
    }
    
    private void showInfos() {
        JOptionPane.showMessageDialog(null, "Program pobierający, z wykorzystaniem funkcji składowanych, informacje \nz bazy danych i wyświetlający je w postaci XML. \nPowstał na laboratoria Integracji Systemów na Politechnice Lubelskiej\nw 2016 roku.\n\nAutor:  Grzegorz Fila, IMST 2.2.4", "O programie", JOptionPane.PLAIN_MESSAGE);
    }

    private void FrameInit() {
        myjPanel = new JPanel();
        myjLabel = new JLabel();
        myjLabel2 = new JLabel();
        myjTextArea = new JTextArea();
        myjScrollPane = new JScrollPane(myjTextArea);
        myjButton = new JButton();
        myjButton2 = new JButton();
        myjButton3 = new JButton();
        myjButton4 = new JButton();
        myjButton5 = new JButton();
        myjComboBox = new JComboBox();
        myjComboBox2 = new JComboBox();
        optionSimple = new JRadioButton("Uproszczone",true);
        optionAll = new JRadioButton("Pełne");
        tryb = new ButtonGroup();
        
        setLayout(null);
        
        myjLabel2.setText("Dane");
        myjLabel2.setBounds(10,100,200,20);
        add(myjLabel2);
        
        tryb.add(optionSimple);
        tryb.add(optionAll);
        optionSimple.setBounds(10,120,100,20);
        optionAll.setBounds(120,120,100,20);
        add(optionSimple);
        add(optionAll);
        
        myjTextArea.setText("");
        myjTextArea.setEditable(false);
        myjScrollPane.setBounds(220,10,565,350);
        add(myjScrollPane);
                
        myjLabel.setText("Pobierz");
        myjLabel.setBounds(10,10,200,20);
        add(myjLabel);
        
        myjComboBox.setBounds(10,30,200,20);
        myjComboBox.setMaximumRowCount(11);
        myjComboBox.addItem("Wszystkie serie");
        myjComboBox.addItem("Same tytuły");
        myjComboBox.addItem("Serie o id powyżej...");
        myjComboBox.addItem("Serie o id poniżej...");
        myjComboBox.addItem("Serie o średniej powyżej...");
        myjComboBox.addItem("Serie o średniej poniżej...");
        myjComboBox.addItem("Serie z kraju...");
        myjComboBox.addItem("Serie typu...");
        myjComboBox.addItem("Serie o statusie...");
        myjComboBox.addItem("Serie stacji...");
        add(myjComboBox);
        myjComboBox.addActionListener((ActionEvent e) -> {
            String element = myjComboBox.getSelectedItem().toString();
            if(element.equals("Wszystkie serie")){
                myjComboBox2.removeAllItems();
                myjComboBox2.addItem("<Wybierz>");
                myjComboBox2.setVisible(false);
            }
            else if(element.equals("Same tytuły")){
                myjComboBox2.removeAllItems();
                myjComboBox2.addItem("<Wybierz>");
                myjComboBox2.setVisible(false);
            }
            else if(element.equals("Serie o id powyżej...") || element.equals("Serie o id poniżej...")){
                myjComboBox2.removeAllItems();
                myjComboBox2.addItem("50");
                myjComboBox2.addItem("100");
                myjComboBox2.addItem("150");
                myjComboBox2.addItem("200");
                myjComboBox2.addItem("250");
                myjComboBox2.addItem("300");
                myjComboBox2.addItem("350");
                myjComboBox2.addItem("400");
                myjComboBox2.addItem("450");
                myjComboBox2.addItem("500");
                myjComboBox2.setVisible(true);
            }
            else if(element.equals("Serie o średniej powyżej...") || element.equals("Serie o średniej poniżej...")){
                myjComboBox2.removeAllItems();
                myjComboBox2.addItem("1");
                myjComboBox2.addItem("2");
                myjComboBox2.addItem("3");
                myjComboBox2.addItem("4");
                myjComboBox2.addItem("5");
                myjComboBox2.addItem("6");
                myjComboBox2.addItem("7");
                myjComboBox2.addItem("8");
                myjComboBox2.addItem("9");
                myjComboBox2.setVisible(true);
            }
            else if(element.equals("Serie z kraju...")){
                myjComboBox2.removeAllItems();
                myjComboBox2.addItem("USA");
                myjComboBox2.addItem("Kanada");
                myjComboBox2.addItem("Japonia");
                myjComboBox2.addItem("Francja");
                myjComboBox2.addItem("Wielka Brytania");
                myjComboBox2.addItem("Irlandia");
                myjComboBox2.setVisible(true);
            }
            else if(element.equals("Serie typu...")){
                myjComboBox2.removeAllItems();
                myjComboBox2.addItem("Fabularne");
                myjComboBox2.addItem("Animowane");
                myjComboBox2.addItem("Dokumentalne");
                myjComboBox2.addItem("Reality Show");
                myjComboBox2.addItem("Talk Show");
                myjComboBox2.addItem("Pozostałe");
                myjComboBox2.setVisible(true);
            }
            else if(element.equals("Serie o statusie...")){
                myjComboBox2.removeAllItems();
                myjComboBox2.addItem("Trwające");
                myjComboBox2.addItem("Zakończone");
                myjComboBox2.addItem("Oczekujące na decyzję");
                myjComboBox2.setVisible(true);
            }
            else if(element.equals("Serie stacji...")){
                myjComboBox2.removeAllItems();
                myjComboBox2.addItem("ABC");
                myjComboBox2.addItem("Adult Swim");
                myjComboBox2.addItem("AMC");
                myjComboBox2.addItem("Audience Network");
                myjComboBox2.addItem("BBC America");
                myjComboBox2.addItem("BBC One");
                myjComboBox2.addItem("BBC Two");
                myjComboBox2.addItem("BBC Three");
                myjComboBox2.addItem("BET");
                myjComboBox2.addItem("Bravo");
                myjComboBox2.addItem("Cartoon Network");
                myjComboBox2.addItem("CBS");
                myjComboBox2.addItem("Channel 4");
                myjComboBox2.addItem("Cinemax");
                myjComboBox2.addItem("City");
                myjComboBox2.addItem("CMT");
                myjComboBox2.addItem("CNN");
                myjComboBox2.addItem("Comedy Central");
                myjComboBox2.addItem("Disney Channel");
                myjComboBox2.addItem("Disney XD");
                myjComboBox2.addItem("E!");
                myjComboBox2.addItem("E4");
                myjComboBox2.addItem("El Rey Network");
                myjComboBox2.addItem("France 4");
                myjComboBox2.addItem("FreeForm");
                myjComboBox2.addItem("Fuji TV");
                myjComboBox2.addItem("FX");
                myjComboBox2.addItem("FXX");
                myjComboBox2.addItem("FOX");
                myjComboBox2.addItem("Global");
                myjComboBox2.addItem("H2");
                myjComboBox2.addItem("HBO");
                myjComboBox2.addItem("History");
                myjComboBox2.addItem("ITV");
                myjComboBox2.addItem("ITV2");
                myjComboBox2.addItem("Lifetime");
                myjComboBox2.addItem("MTV");
                myjComboBox2.addItem("NBC");
                myjComboBox2.addItem("Nickelodeon");
                myjComboBox2.addItem("nicktoons");
                myjComboBox2.addItem("NTV");
                myjComboBox2.addItem("REELZ");
                myjComboBox2.addItem("RTÉ ONE");
                myjComboBox2.addItem("Showtime");
                myjComboBox2.addItem("Sky 1");
                myjComboBox2.addItem("Sky ARTS");
                myjComboBox2.addItem("space");
                myjComboBox2.addItem("Spike");
                myjComboBox2.addItem("starz");
                myjComboBox2.addItem("Sundance TV");
                myjComboBox2.addItem("Syfy");
                myjComboBox2.addItem("Syndication");
                myjComboBox2.addItem("TBS");
                myjComboBox2.addItem("TF1");
                myjComboBox2.addItem("The CW");
                myjComboBox2.addItem("The Movie Network");
                myjComboBox2.addItem("The WB");
                myjComboBox2.addItem("TNT");
                myjComboBox2.addItem("Travel Channel");
                myjComboBox2.addItem("truTV");
                myjComboBox2.addItem("TV Land");
                myjComboBox2.addItem("UPN");
                myjComboBox2.addItem("USA Network");
                myjComboBox2.addItem("VH1");
                myjComboBox2.addItem("WGN America");
                myjComboBox2.setVisible(true);
            }
        });
        
        myjComboBox2.setBounds(10,60,200,20);
        myjComboBox2.setMaximumRowCount(10);
        myjComboBox2.addItem("<Wybierz>");
        myjComboBox2.setVisible(false);
        add(myjComboBox2);
        
        myjButton.setText("Wykonaj");
        myjButton.setBounds(10,180,200,20);
        add(myjButton);
        myjButton.addActionListener((ActionEvent evt) -> {
            runStoredFunction();   
        });
        
        myjButton2.setText("Kopiuj do schowka");
        myjButton2.setBounds(10,230,200,20);
        add(myjButton2);
        myjButton2.addActionListener((ActionEvent evt) -> {
            copyToClipboard();   
        });
        
        myjButton4.setText("Wyczyść");
        myjButton4.setBounds(10,260,200,20);
        add(myjButton4);
        myjButton4.addActionListener((ActionEvent evt) -> {
            cleanTextArea();   
        });
        
        myjButton3.setText("O programie");
        myjButton3.setBounds(10,310,200,20);
        add(myjButton3);
        myjButton3.addActionListener((ActionEvent evt) -> {
            showInfos();   
        });
        
        myjButton5.setText("Wyjście");
        myjButton5.setBounds(10,340,200,20);
        add(myjButton5);
        myjButton5.addActionListener((ActionEvent evt) -> {
            System.exit(0);   
        });
        
    }
 
    public static class XmlFormatter {
        public static String format(String unformattedXml) {
            try {
                Document document = parseXmlFile(unformattedXml);

                OutputFormat format = new OutputFormat(document);
                format.setLineWidth(65);
                format.setIndenting(true);
                format.setIndent(2);
                Writer out = new StringWriter();
                XMLSerializer serializer = new XMLSerializer(out, format);
                serializer.serialize(document);

                return out.toString();
            } catch (IOException e) {
                return "";
            }
        }

        private static Document parseXmlFile(String in) {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource is = new InputSource(new StringReader(in));
                return db.parse(is);
            } catch (ParserConfigurationException | SAXException | IOException e) {}
            return null;
        }

        public static String makeXMLString(Document doc) {
            String xmlString = "";
            if (doc != null) {
                try {
                    TransformerFactory transfac = TransformerFactory.newInstance();
                    Transformer trans = transfac.newTransformer();
                    trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                    trans.setOutputProperty(OutputKeys.INDENT, "yes");
                    StringWriter sw = new StringWriter();
                    StreamResult result = new StreamResult(sw);
                    DOMSource source = new DOMSource(doc);
                    trans.transform(source, result);
                    xmlString = sw.toString();
                } catch (IllegalArgumentException | TransformerException e) {}
            }
            return xmlString;
        }
    }
}