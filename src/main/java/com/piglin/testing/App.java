package com.piglin.testing;

import com.mongodb.*;

import com.mongodb.util.JSON;
import org.bson.BSONObject;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Hello world!
 */
public class App {

    private static String prefix = "http://www.topuniversities.com/university-rankings";
    private static String rankingName = "QS";
    private static String subjectPrefix = "university-subject-rankings";
    private static String overallPrefix = "world-university-rankings";

    private static Mongo mongo;
    private static DB mongoDb;
    private static DBCollection collection;
    private static DBCursor cursor;
    private static MapReduceCommand cmd;
    private static DatabaseMetaData dbm;
    private static ResultSet results;
    private static ResultSetMetaData resultsMeta;

    private static String map = "function() { " +
            "emit(this.title,this.score_overall,this.rank)" +
            "};";

    private static String reduce = "function(key, values) { " +
            "var result = {" +
            "\"title\" : \"\"," +
            "\"score1\" : \"\"" +
            "\"rank1\" : \"\"" +
            "\"score2\" : \"\"" +
            "\"rank2\" : \"\"" +
            "};" +
            "values.forEach(function(value) {" +
            "if(value.title !== null) {result.title = value.title;}" +
            "if(value.score1 !== null) {result.score1 = value.score_overall;}" +
            "if(value.rank1 !== null) {result.rank1 = value.rank;}" +
            "if(value.score2 !== null) {result.score2 = value.score_overall;}" +
            "if(value.rank2 !== null) {result.rank2 = value.rank;}" +
            "});" +
            "return result;" +
            "};";

    public static void main(String[] args) {

        try {
            mongo = new Mongo("localhost", 27017);

            mongoDb = mongo.getDB("universityrankings");

            //List<String> years = Arrays.asList("2004", "2005", "2006", "2007", "2008", "2009", "2011", "2012", "2013", "2014");
            List<String> years = Arrays.asList("2012", "2013", "2014");

            List<String> subjects = Arrays.asList("philosophy", "modern-languages", "history-archaeology", "linguistics", "english-language-literature", "computer-science-information-systems", "engineering-chemical", "engineering-civil-structural", "engineering-electrical-electronic", "engineering-mechanical", "medicine", "biological-sciences", "psychology", "pharmacy", "agriculture-forestry", "physics", "mathematics", "environmental-studies", "earth-marine-sciences", "chemistry", "materials-sciences", "geography", "statistics-operational-research", "sociology", "politics", "law-legal-studies", "economics-econometrics", "accounting-finance", "communication-media-studies", "education-training");

            String[][] successes = new String[years.size()][subjects.size() + 1];
            String result;

            Database.connect("localhost", "universityrankings", "root", "danilo");
            dbm = Database.getDatabaseMetaData();

            for (int i = 0; i < years.size(); i++) {

                System.out.print(i + ":");
                System.out.print("0,");
                //result = processCollection(rankingName, years.get(i), "");
                successes[i][0] = "skipped";//result;


                for (int j = 0; j < subjects.size(); j++) {

                    System.out.print((j + 1) + ",");
                    //result = processCollection(rankingName, years.get(i), subjects.get(j));
                    successes[i][j + 1] = "skipped";//result;

                }

                System.out.println();
            }

            for (int i = 0; i < years.size(); i++) {

                System.out.print(successes[i][0] + "\t");

                for (int j = 0; j < subjects.size(); j++) {

                    System.out.print(successes[i][j + 1] + "\t");

                }

                System.out.println();
            }

            String query = "";

            for (int i = 0; i < years.size(); i++) {

                exportSQL(rankingName, years.get(i), "");


                for (int j = 0; j < subjects.size(); j++) {

                    exportSQL(rankingName, years.get(i), subjects.get(j).replace("-", ""));

                }

            }

            String fields = "title VARCHAR(255), ";

            for (int i = 0; i < years.size(); i++) {

                fields = fields + "score_" + years.get(i) + "overall DOUBLE, ";
                fields = fields + "rank_" + years.get(i) + "overall INTEGER, ";

                for (int j = 0; j < subjects.size(); j++) {

                    fields = fields + "score_" + years.get(i) + subjects.get(j).replace("-", "") + " DOUBLE, ";
                    fields = fields + "rank_" + years.get(i) + subjects.get(j).replace("-", "") + " INTEGER, ";

                }

            }

            fields = fields + "PRIMARY KEY ( title )";

            SQL.createTableIfNotExists("JOINED",fields);

            for (int i = 0; i < years.size(); i++) {
                try {
                    System.out.println("AHOJ1");
                    results = joinSQL("JOINED", rankingName, years.get(i), "");
                    System.out.println("AHOJ2");
                    storeResults(results, rankingName + years.get(i) + "");
                    System.out.println("AHOJ3");
                } catch (NullPointerException ex) {
                    System.err.println("Error Storing: "+years.get(i)+", empty");
                }
                for (int j = 0; j < subjects.size(); j++) {
                    try {
                        System.out.println("AHOJ1");
                        results = joinSQL("JOINED", rankingName, years.get(i), subjects.get(j).replace("-", ""));
                        System.out.println("AHOJ2");
                        storeResults(results, rankingName + years.get(i) + "");
                        System.out.println("AHOJ3");
                    } catch (NullPointerException ex) {
                        System.err.println("Error Storing: "+years.get(i)+", "+subjects.get(j).replace("-", ""));
                    }
                }

            }

            mongo.close();
            Database.closeSQL();

            //collection = mongoDb.getCollection("result");
            //cursor = collection.find();
            //while (cursor.hasNext()) {
            //    System.out.println(cursor.next());
            //}

        } catch (Exception e) {
            System.err.println("Error in main app.");
            e.printStackTrace();
        } finally {

        }
    }

    private static void storeResults(ResultSet results, String collectionName) throws SQLException {
        Database.executeQuery("INSERT INTO universityrankings.JOINED 'title','score_"+collectionName+"','rank_"+collectionName+"' VALUES ('" + results.getString("title") + "', '" + results.getString("score") + "', '" + results.getString("rank") + "');");
    }

    private static ResultSet joinSQL(String joined, String rankingName, String year, String subject) {
        String collectionName = rankingName + year + subject.replace("-", "");
        ResultSet results = null;

        try {
            results = Database.executeQuery("SELECT * FROM '"+joined+"' FULL OUTER JOIN '"+collectionName+"';");
        } catch (SQLException ex) {
            System.err.println("SQL Error: " + year + ", " + subject);
        }

        return results;
    }

    private static void exportSQL(String rankingName, String year, String subject) {
        try {
            String collectionName = rankingName + year + subject.replace("-", "");

            SQL.createTableIfNotExists(collectionName, "title VARCHAR(255) NOT NULL, score DOUBLE, rank INTEGER, PRIMARY KEY ( title )");

            DBCollection collection = mongoDb.getCollection(rankingName + year + subject);

            DBObject expr = new BasicDBObject();

            //expr.put("{}","{}");//'title':'title', 'score':'score_overall', 'rank':'rank'
            //cursor = collection.find(new BasicDBObject("title",1));
            cursor = collection.find();

            String field1;
            String field2;
            String field3;

            while (cursor.hasNext()) {
                BasicDBObject obj = (BasicDBObject) cursor.next();
                //System.out.println(obj.getString("title"));

                field1 = obj.getString("title");
                field1 = field1.replace('\'','\\');
                field2 = obj.getString("score_overall");
                field3 = obj.getString("rank");

                try {
                    Database.executeQuery("INSERT INTO universityrankings." + collectionName + " VALUES ('" + field1 + "', '" + field2 + "', '" + field3 + "');");
                } catch (SQLException ex) {
                    System.err.println("SQL Error: "+obj.getString("title"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String processCollection(String rankingName, String year, String subject) {

        String address;
        String collectionName;

        collectionName = rankingName + year + subject;
        DBCollection collection = mongoDb.getCollection(collectionName);

        if (collection.count() == 0) {
            address = extractAddress(getSubjectName(year, subject));
            return store(collection, address);
        } else {
            //System.out.println("Collection exists: " + collectionName);
            return "exists";
        }
    }

    private static String store(DBCollection collection, String address) {

        if (address == null) {
            return "error";
        } else if (address == "") {
            return "missing";
        }

        address = address.replace("\\", "");

        //System.out.println("Storing..." + address);

        String collectionData = Crawler.crawl(address);

        DBObject dbObject = (DBObject) JSON.parse(collectionData);

        int index = 0;
        DBObject current = (DBObject) dbObject.get("" + index);

        collection.drop();


        while (current != null) {
            collection.insert(current);

            index++;
            current = (DBObject) dbObject.get("" + index);
        }

        return "success";

    }

    public static String getSubjectName(String year, String subject) {

        return prefix + "/" + subjectPrefix + "/" + year + "/" + subject;

    }

    public static String getOverallName(String year) {

        return prefix + "/" + overallPrefix + "/" + year;

    }

    public static String extractAddress(String url) {

        String website = Crawler.crawl(url);
        if (website == null) {
            return null;
        }
        if (website.lastIndexOf("\"flat_file\":\"") == -1) {
            //System.out.println("Error: Wrong first index: " + website.lastIndexOf("\"flat_file\":\"") + " URL: " + url);
            return null;
        }
        if (website.lastIndexOf(".txt\"") == -1) {
            //System.out.println("Error: Wrong second index: " + website.lastIndexOf(".txt\"") + " URL: " + url);
            return null;
        }
        String address = website.substring(website.lastIndexOf("\"flat_file\":\"") + 13, website.lastIndexOf(".txt\"") + 4);
        return address;
    }

    public static void storeData() {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder;

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            //Document processDocument = documentBuilder.parse(processFile);
            //processDocument.getDocumentElement().normalize();

            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            //Document databaseDocument = documentBuilder.parse(databaseFile);
            //databaseDocument.getDocumentElement().normalize();

            XPathExpression expression;
            NodeList nodeList;

            //Node copiedNode;
            Node expandedNode;
            Node expandedNodeDatabase;

            expression = xpath.compile("//*[@id='Expanded']");

            //nodeList = (NodeList) expression.evaluate(processDocument, XPathConstants.NODESET);

            //expandedNode = nodeList.item(0);

            //nodeList = (NodeList) expression.evaluate(databaseDocument, XPathConstants.NODESET);

            //expandedNodeDatabase = nodeList.item(0);

            //removeAll(expandedNode);
            //removeAll(expandedNodeDatabase);

            String databaseName;
            String baseName;

            File f = new File(".");

            ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));

            Mongo mongo = new Mongo("localhost", 27017);
            DB db = mongo.getDB("universityrankings");

            for (int i = 0; i < names.size(); i++) {

                if (!names.get(i).matches(".*.json")) {
                    System.out.println("Skipping: " + names.get(i));
                    continue;
                }

                DBCollection collection = db.getCollection(names.get(i).substring(0, names.get(i).lastIndexOf('.')));

                System.out.println("Collection name: " + names.get(i).substring(0, names.get(i).lastIndexOf('.')));

                String collectionData = readFile(names.get(i), StandardCharsets.UTF_8);

                // convert JSON to DBObject directly
                //DBObject dbObject = (DBObject) JSON.parse(collectionData);
                DBObject dbObject = (DBObject) JSON.parse(collectionData);

                int index = 0;
                DBObject current = (DBObject) dbObject.get("" + index);

                collection.drop();


                while (current != null) {
                    collection.insert(current);

                    index++;
                    current = (DBObject) dbObject.get("" + index);
                }

                databaseName = names.get(i).substring(0, names.get(i).lastIndexOf('.'));
                baseName = databaseName.replaceFirst("2014QS", "");
                System.out.println(baseName);

            }

            System.out.println("Done");

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            //DOMSource source = new DOMSource(processDocument);
            StreamResult result = new StreamResult(new File("/Users/swyna/.RapidMiner/repositories/Local Repository/Mongo to CSV.rmp"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            //transformer.transform(source, result);

            System.out.println("File saved!");

            mongo.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            //} catch (SAXException e) {
            //    e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    public static void removeAll(Node node) {
        if (!node.hasChildNodes()) {
            return;
        }
        NodeList nodeList = node.getChildNodes();
        Node n;

        for (int i = 0; i < nodeList.getLength(); i++) {
            n = nodeList.item(i);

            if (n.hasChildNodes()) //edit to remove children of children
            {
                removeAll(n);
                node.removeChild(n);
            } else
                node.removeChild(n);
        }
    }

    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
