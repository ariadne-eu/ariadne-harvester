/*******************************************************************************
 * Copyright (c) 2008 Ariadne Foundation.
 * 
 * This file is part of Ariadne Harvester.
 * 
 * Ariadne Harvester is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Ariadne Harvester is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Ariadne Harvester.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

//package org.ariadne.lucene.lomlucene;
//
//import java.io.File;
//import java.io.IOException;
//
//import org.apache.lucene.document.Document;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.index.Term;
//import org.apache.lucene.queryParser.ParseException;
//import org.apache.lucene.queryParser.QueryParser;
//import org.apache.lucene.search.Hits;
//import org.apache.lucene.search.IndexSearcher;
//import org.apache.lucene.search.Query;
//import org.apache.lucene.search.Searcher;
//import org.apache.lucene.search.TermQuery;
//
//
///**
// * Created by IntelliJ IDEA.
// * User: Gonzalo Parra
// * Date: 06/12/2005
// * Time: 11:59:47 AM
// * To change this template use File | Settings | File Templates.
// */
//public class TestLucene {
//
//    public TestLucene(){}
//
//    public Document getDoc() {
//        return doc;
//    }
//
//    public void setDoc(Document doc) {
//        this.doc = doc;
//    }
//
//    private Document doc;
//    private String luceneDir;
//
//    public void runTest(String dir) {
//        luceneDir = dir;
//        //initPropertiesManager();
//        System.out.println("Testing if directory '" + luceneDir + "' exists and if it contains a lucene index. " +
//                "Testing if a new item can be indexed and found afterwards.");
//        boolean success = checkLuceneDirectory();
//        if (!success) {
//            System.out.println("Directory '" + luceneDir +
//                    "' could not be found");
//            System.out.println("Check whether \"sidweb.luceneIndex\" is set correctly.");
//            return;
//        }
//
//        success = true; //checkUpdateIndex();
//        if (!success) {
//            System.out.println("Error occurred while indexing testfile");
//            System.out.println("Check whether \"sidweb.luceneIndex\" is contains a valid lucene index.\nCheck for an existing lock file");
//            return;
//        }
//
//        success = findItem();
//        if (!success) {
//            System.out.println("Testfile could not be found after indexing. Either indexing or retrieving the item failed.");
//            return;
//        }
//
//
//    }
//
//    private boolean checkLuceneDirectory() {
//        File dir = new File(luceneDir);
//        return (dir.exists() && dir.isDirectory());
//    }
//
////    private boolean checkUpdateIndex() {
////        try {
////            //LOMLuceneIndexCreator. deleteId("12345");
////            //LOMLuceneIndexCreator.updateIndex(doc);
////        } catch (IOException e) {
////            //e.printStackTrace();
////            return false;
////        }
////        return true;
////    }
//
//    private boolean findItem() {
//        Searcher searcher = null;
//                             IndexReader reader = null;
//      try {
//          reader = IndexReader.open(luceneDir);
//      } catch (IOException e) {
//          e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//      }
//            searcher = new IndexSearcher(reader);
//
//
//        QueryParser qp = new QueryParser("field", new SimpleKeepNumbersAnalyzer());
//        Query myQuery = null;
//                       TermQuery termQuery = new TermQuery(new Term("titel", "titel"));
//        try {
//            myQuery = qp.parse(termQuery.toString());
//        } catch (ParseException e) {
//            System.out.println("Query could not be parsed");
//            return false;
//        }
//
//        Hits hits = null;
//        try {
//            hits = searcher.search(myQuery);
//        } catch (IOException e) {
//            System.out.println("Query could not be executed");
//            return false;
//        }
//
//        for (int i = 0; i < hits.length(); i++) {
//            try {
//                Document d = hits.doc(i);
//                String[] test = d.getValues("id");
//                for (int j = 0; j < test.length; j++) {
//                    String s = test[j];
//                    if (s.indexOf("4") != -1) //was "12345"
//                        return true;
//                }
//            } catch (IOException e) {
//                System.out.println("returning document from hits failed");
//            } catch (NullPointerException e) {
//                System.out.println("Document title is null");
//            }
//        }
//
//        System.out.println("TestFile which should have been indexed could not be found");
//        return false;
//
//    }
//
//}
