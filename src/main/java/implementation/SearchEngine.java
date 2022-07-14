package implementation;

import java.util.Arrays;
import java.util.List;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import java.io.*;


import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SearchEngine implements ISearchEngine{
    class Subject implements Comparable<Subject>{
        String id ;
        String url ;
        String title ;
        int freq;


        Subject(){
            freq = 0;
        }

        public void increment(){
            freq++;
        }
        public void decrement(){
            freq--;
        }


        @Override
        public int compareTo(Subject o) {
            if (this.freq > o.freq){
                return -1;
            }
            else  if (this.freq < o.freq){
                return 1;
            }
            else {
                return 0;
            }
        }
    }


    BTreeImpl<String ,ArrayList<Subject>> bTree;


    SearchEngine(int t){
        bTree = new BTreeImpl<>(t);
    }




    @Override
    public void indexWebPage(String filePath) {
        // Instantiate the Factory
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      try {
          // parse XML file
          DocumentBuilder db = dbf.newDocumentBuilder();

          Document doc = db.parse(new File(filePath));

          // optional, but recommended
          doc.getDocumentElement().normalize();

//          System.out.println("Root Element :" + doc.getDocumentElement().getNodeName());
//          System.out.println("------");

          // get <staff>
          NodeList list = doc.getElementsByTagName("doc");

          for (int temp = 0; temp < list.getLength(); temp++) {

              Node node = list.item(temp);

              if (node.getNodeType() == Node.ELEMENT_NODE) {

                  Element element = (Element) node;

                  // get staff's attribute
                  Subject initiativeSubject = new Subject();
                  initiativeSubject.id = element.getAttribute("id");
                  initiativeSubject.url = element.getAttribute("url");
                  initiativeSubject.title = element.getAttribute("title");
                  initiativeSubject.increment();
                  // get text
                  String txt = element.getTextContent();
                  String[] words  = txt.split("[^a-zA-Z\\d]+");
                  
                  for (String word : words) {
                      word = word.toLowerCase();
//                      System.out.println(word +"  " +bTree.getSize());
                      //assuming bTree.search() returns null when the element doesn`t exist
                      ArrayList<Subject> wordSubjects = bTree.search(word);

                      if (wordSubjects == null){// if word doesn`t have any subjects before(doesn`t exist in the tree)
                          wordSubjects= new ArrayList<>();
                          wordSubjects.add(initiativeSubject);
                          bTree.insert(word , wordSubjects);
                      }
                      else {//if word has subjects before (exists already)

                          Subject subjectInTree = searchByID(wordSubjects , initiativeSubject.id);

                          //if word doesn`t have the subject we search in before
                          if ( subjectInTree == null){
                              wordSubjects.add(initiativeSubject);
                          }
                          //if the word subjects contains the subject we search in
                          else{
                              subjectInTree.increment();
                          }
                      }
                  }

//                  System.out.println(initiativeSubject.title + " finishid!");
//                  System.out.println("Tree size = " + Integer.toString(bTree.getSize()) );
//                  System.out.println("******");

              }
          }

      }
      catch (ParserConfigurationException | SAXException | IOException e) {
          e.printStackTrace();
      }

    }

    private Subject searchByID(ArrayList<Subject> wordSubjects, String id) {
        for (int i = 0; i < wordSubjects.size(); i++) {
            if (wordSubjects.get(i).id == id){
                return wordSubjects.get(i);
            }
        }
        return null;
    }

    @Override
    public void indexDirectory(String directoryPath) {
        File dic = new File(directoryPath);
        if (!dic.isDirectory()){
            System.out.println("It`s not directory");
            return;
        }
        for (String fileName : dic.list()) {
            String filePath = directoryPath + "\\" + fileName;
            File file = new File(filePath);
            if (file.isDirectory()){
                indexDirectory(filePath);
            }
            else if(fileName.startsWith("wiki")){
                indexWebPage(filePath);
                System.out.println(fileName + " was indexed successfully!");
                System.out.println("Tree size = " + bTree.getSize());
            }
            else {
                continue;
            }


        }


    }

    @Override
    public void deleteWebPage(String filePath) {
        // Instantiate the Factory
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      try {
          // parse XML file
          DocumentBuilder db = dbf.newDocumentBuilder();

          Document doc = db.parse(new File(filePath));

          // optional, but recommended
          doc.getDocumentElement().normalize();

//          System.out.println("Root Element :" + doc.getDocumentElement().getNodeName());
//          System.out.println("------");

          // get <staff>
          NodeList list = doc.getElementsByTagName("doc");

          for (int temp = 0; temp < list.getLength(); temp++) {

              Node node = list.item(temp);

              if (node.getNodeType() == Node.ELEMENT_NODE) {

                  Element element = (Element) node;

                  // get staff's attribute
                  Subject initiativeSubject = new Subject();
                  initiativeSubject.id = element.getAttribute("id");
                  initiativeSubject.url = element.getAttribute("url");
                  initiativeSubject.title = element.getAttribute("title");
                  initiativeSubject.increment();

                  // get text
                  String txt = element.getTextContent();
                  String[] words  = txt.split(" ");

                  for (String word : words) {
                      ArrayList<Subject> wordSubjects = bTree.search(word);

                      if (wordSubjects == null){// if word doesn`t have any subjects before(doesn`t exist in the tree)
                         continue;
                      }
                      else {// exists already
                          removeByID(wordSubjects , initiativeSubject.id);

                      }
                  }

//                  System.out.println(initiativeSubject.title + " finishid!");
//                  System.out.println("Tree size = " + Integer.toString(bTree.getSize()) );
//                  System.out.println("******");

              }
          }

      }
      catch (ParserConfigurationException | SAXException | IOException e) {
          e.printStackTrace();
      }

    }

    private void removeByID(ArrayList<Subject> wordSubjects, String id) {
        for (int i = 0; i < wordSubjects.size(); i++) {
            if (wordSubjects.get(i).id == id){
                wordSubjects.remove(i);
                return;
            }
        }
    }

    @Override
    public List<ISearchResult> searchByWordWithRanking(String word) {
        word = word.toLowerCase();
        ArrayList<Subject> subjects = bTree.search(word);
        if (subjects == null){
            return null;
        }
        else{
            List<ISearchResult> results = subjectsToSearchResults(subjects);
            return results;
        }

    }

    @Override
    public List<ISearchResult> searchByMultipleWordWithRanking(String sentence) {
        String[] words = sentence.split("[^a-zA-Z\\d]+");
        ArrayList<Subject> overallSubjects = new ArrayList<>();
        List<ISearchResult> results = new ArrayList<>();
        
        for (String word : words) {
            ArrayList<Subject> wordSubjects = bTree.search(word);
            merge(overallSubjects , wordSubjects);
            results = subjectsToSearchResults(overallSubjects);
        }
        return results;
    }

    private List<ISearchResult> subjectsToSearchResults(ArrayList<Subject> subjects) {
        List<ISearchResult> results = new ArrayList<>();
        subjects.sort(Subject::compareTo);
        for (int i = 0; i < subjects.size(); i++) {
            SearchResult searchResult = new SearchResult(subjects.get(i).id , i+1);
            results.add(searchResult);
        }


        return results;
    }

    private void merge(List<Subject> list1 , ArrayList<Subject> list2) {
        if (list1 == null || list1.size() == 0){
            for (int i = 0; i < list2.size(); i++) {
                list1.add(list2.get(i));
            }

        }
        else {
            ArrayList<Integer> indexes = new ArrayList<>();
            int len2 = list2.size();
            int len1 = list1.size();
            for (int i = 0; i < len2; i++) {
                boolean flag = true;
                for (int j = 0; j < len1; j++) {
                    Subject o1 = list1.get(j);
                    Subject o2 = list2.get(i);
                    //the ith subject of list2 is in list1
                    if (o1.id.equals(o2.id) ){
                        o1.id += o2.id;
                        flag = false;
                        break;
                    }
                }
                //the ith subject of list2 is not in list1
                if (flag){
                    list1.add(list2.get(i));
                }
            }
        }
    }

    public void printSearchResults(List<ISearchResult> results){
        for (int i = 0; i < results.size(); i++) {
            String id = results.get(i).getId();
            int rank = results.get(i).getRank();
            System.out.println("\nID : " + id +"\nRank: " + rank);
            System.out.println("************");
        }
    }

    public static void main(String[] args) {
      String dir = "C:\\Users\\mahmo\\BtreeApp\\src\\main\\java\\Wikipedia Data Sample";
      String file = "C:\\Users\\mahmo\\BtreeApp\\src\\main\\java\\Wikipedia Data Sample\\wiki_00";
      SearchEngine searchEngine = new SearchEngine(128);
//      searchEngine.indexDirectory(dir);
      searchEngine.indexWebPage(file);
      List<ISearchResult> results = new ArrayList<>();
      results = searchEngine.searchByWordWithRanking("ray");
      searchEngine.printSearchResults(results);
      
    }


}
