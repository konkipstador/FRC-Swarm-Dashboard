/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package swarm_dashboard;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Caleb
 */
public class Swarm_Dashboard extends Application {
    
    SocketManagement SocketManagement = new SocketManagement();
    File fXmlFile;
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder;
    Document doc;
    NodeList nList;
    Node nNode;
    Element eElement;
    String[] keys;  
    String[] data;
    int[] labelsX;
    int[] labelsY;
    
    public void aquireXML(String link){
        try{
            fXmlFile = new File(link);
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(fXmlFile);
        }catch(Exception e){
            System.out.println("Failed to aquire XML file");
        }
        doc.getDocumentElement().normalize();
        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        nList = doc.getElementsByTagName("label");
        int keyNumber = 0;
        
        keys = new String[nList.getLength()];
        data = new String[nList.getLength()];
        labelsX = new int[nList.getLength()];
        labelsY = new int[nList.getLength()];
        
        
        for (int temp = 0; temp < nList.getLength(); temp++) {

            nNode = nList.item(temp);

            System.out.println("\nCurrent Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                eElement = (Element) nNode;
                System.out.println("Label id : " + eElement.getAttribute("id"));
                keys[keyNumber] = eElement.getElementsByTagName("key").item(0).getTextContent();
                labelsX[keyNumber] = Integer.parseInt(eElement.getElementsByTagName("x").item(0).getTextContent());
                labelsY[keyNumber] = Integer.parseInt(eElement.getElementsByTagName("y").item(0).getTextContent());
                System.out.println(keys[keyNumber]);
                keyNumber++;

            }
        }
        String[] data = new String[keys.length];
    }
 
    List<Label> labels = new ArrayList<Label>();
    
    public void aquireLabels(){
        for(int i = 0; i < keys.length; i++){
           labels.add(new Label("label " + i));
           labels.get(i).setText(keys[i]);
           labels.get(i).setTranslateX(labelsX[i]);
           labels.get(i).setTranslateY(-labelsY[i]);
        }
    }
    
    @Override
    public void start(Stage primaryStage) {
        aquireXML("C:/Users/Caleb/SwarmDashboard/Swarm_Dashboard/xmlSaves/swdConfig.xml");
        aquireLabels();
        
        SocketManagement.importKeys(keys);
        SocketManagement.enableSockets("10.9.57.2",5800,5801); 

        StackPane root = new StackPane();
        root.getChildren().addAll(labels);
        
        Scene scene = new Scene(root, 1920, 800);
        
        primaryStage.setTitle("Hello World!");
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
        
        //Sets up and starts a new Matt Daemon to update the GUI
        Task task = new Task<Void>() {
            @Override
            public Void call(){
            int i = 0;
                while (true) {
                    final int finalI = i;
                    Platform.runLater(new Runnable() {
                        @Override
                        
                        public void run() {
                            //This part updates the GUI by asking for socket
                            //data from the data management thread
                            
                            for(int i = 0; i < keys.length; i++){
                                labels.get(i).setText(keys[i]+": "+SocketManagement.requestData(keys[i]));
                            }
                            
                        }
                        });
                    i++;
                try {Thread.sleep(50);} catch (InterruptedException ex) {}
                }
            }
        };
        Thread Matt = new Thread(task);
        Matt.setDaemon(true);
        Matt.start(); 
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
