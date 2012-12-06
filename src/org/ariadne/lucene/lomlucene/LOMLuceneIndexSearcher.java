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
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Set;
//import java.util.Vector;
//
//import org.apache.log4j.Logger;
//import org.apache.lucene.analysis.Analyzer;
//import org.apache.lucene.analysis.standard.StandardAnalyzer;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.queryParser.QueryParser;
//import org.apache.lucene.search.Hits;
//import org.apache.lucene.search.Query;
//import org.apache.lucene.search.Searcher;
//
///**
// * @author Michael Meire
// * @version 1.0 pre-alpha
// * @since Feb 2, 2006
// */
//public class LOMLuceneIndexSearcher {
//
//    private String luceneDirectory;
//    public static final String[] ALL_SAMGILUCENE_FIELDS = new String[]{
//            /*LuceneIndexer.SAMGILUCENE_FULLLOM,*/
//    	LOMLuceneIndexCreator.lomfield_GeneralTitle(), LOMLuceneIndexCreator.lomfield_GeneralLanguage(), LOMLuceneIndexCreator.lomfield_GeneralKeyWords(),
//            LOMLuceneIndexCreator.lomfield_GeneralDescription(), LOMLuceneIndexCreator.lomfield_LifecycleContributeEntity(), LOMLuceneIndexCreator.lomfield_RightsDescription(),
//            LOMLuceneIndexCreator.lomfield_TechnicalFormat(), LOMLuceneIndexCreator.lomfield_TechnicalLocation(), LOMLuceneIndexCreator.lomfield_TechnicalSize()};
//
//
//    public LOMLuceneIndexSearcher(String luceneDirectory) {
//        this.luceneDirectory = luceneDirectory;
//    }
//
//    public String getLuceneDirectory() {
//        return luceneDirectory;
//    }
//
//    public void setLuceneDirectory(String luceneDirectory) {
//        this.luceneDirectory = luceneDirectory;
//    }
//
//
//    /**
//     * Search for the given query, and for each result, retrieve the details that are given by fieldsToGet
//     *
//     * @param inputQuery  The input query
//     * @param fieldsToGet For example: {LuceneIndexer.SAMGILUCENE_GENERALTITLE, LuceneIndexer.SAMGILUCENE_TECHNICALSIZE}. If null is provided
//     *                    then all fields (except the full lom) is retrieved
//     * @return The result of the query, with the fields that you asked for.
//     */
//    public Vector searchLuceneIndex(String inputQuery, String[] fieldsToGet) {
//        return searchLuceneIndex(inputQuery, fieldsToGet, -1, -1);
//    }
//
//
//    /**
//     * Search for the given query, get the nbResult number of results starting from begin and for each result,
//     * retrieve the details that are given by fieldsToGet. Each result is a Hashmap with the fieldnName as key, and the fieldValue as value
//     *
//     * @param inputQuery  The input query
//     * @param fieldsToGet For example: {LOMLuceneIndexCreator.LUCENE_GENERALTITLE, LOMLuceneIndexCreator.LUCENE_TECHNICALSIZE}. If null is provided
//     *                    then all fields (except the full lom) is retrieved
//     * @param begin       The index of the first result you want to retrieve. We start counting from 1
//     * @param nbResults   The number of results you want to retrieve.
//     * @return The result of the query, with the fields that you asked for.
//     */
//    public Vector searchLuceneIndex(String inputQuery, String[] fieldsToGet, int begin, int nbResults) {
//        Logger.getLogger(LOMLuceneIndexCreator.LOGGER).info("LOMLuceneIndexSearcher::searchLuceneIndex(String inputQuery, String[] fieldsToGet, int begin, int nbResults)...");
//        Searcher searcher = null;
//        if (fieldsToGet == null) {
//            fieldsToGet = ALL_SAMGILUCENE_FIELDS;
//        }
//        try {
//            // Initialize the searcher and analyzer
//            searcher = new org.apache.lucene.search.IndexSearcher(getLuceneDirectory());
//            Analyzer analyzer = new StandardAnalyzer();
//
//            // Build up the Lucene query:
//            QueryParser qp = new QueryParser(LOMLuceneIndexCreator.lomfield_GeneralTitle(), analyzer);
//
//            Query query = qp.parse(inputQuery);
//            Logger.getLogger(LOMLuceneIndexCreator.LOGGER).info("Searching for: " + query.toString());
//            Hits hits = searcher.search(query);
//            Logger.getLogger(LOMLuceneIndexCreator.LOGGER).info(hits.length() + " total matching documents");
//
//            Vector resultVector = new Vector();
//            if (begin == -1) {
//                begin = 1;
//            }
//            if (nbResults == -1) {
//                nbResults = hits.length();
//            }
//            if (begin > hits.length()) {
//                //
//            } else {
//                int end = Math.min((begin + nbResults - 1), hits.length());
//                try {
//                    for (int i = (begin - 1); i < end; i++) {
//                        HashMap resultDetails = new HashMap(fieldsToGet.length);
//                        Document doc = hits.doc(i);
//                        for (int j = 0; j < fieldsToGet.length; j++) {
//                            String fieldToGet = fieldsToGet[j];
//                            String fieldInformation = doc.get(fieldToGet);
//                            resultDetails.put(fieldToGet, fieldInformation);
//                        }
//                        resultVector.add(resultDetails);
//                    }
//                } catch (Exception e) {
//                    Logger.getLogger(LOMLuceneIndexCreator.LOGGER).error("LOMLuceneIndexSearcher::searchLuceneIndex error: " + e);
//                }
//
//            }
//
//            // searcher.close(); DO NOT put this here, because then the code in the finally block will fail, and null will be returned
//            return resultVector;
//
//        } catch (Exception e) {
//            Logger.getLogger(LOMLuceneIndexCreator.LOGGER).error("LOMLuceneIndexSearcher::searchLuceneIndex error: " + e);
//            return null;
//        } finally {
//            try {
//                searcher.close();
//            } catch (Exception e) {
//                return null;
//            }
//        }
//    }
//
//    public int searchLuceneIndexGetNbResults(String inputQuery) {
//        Logger.getLogger(LOMLuceneIndexCreator.LOGGER).info("LOMLuceneIndexSearcher::searchLuceneIndexGetNbResults(String inputQuery)...");
//        Searcher searcher = null;
//        try {
//            // Initialize the searcher and analyzer
//            searcher = new org.apache.lucene.search.IndexSearcher(getLuceneDirectory());
//            Analyzer analyzer = new StandardAnalyzer();
//
//            // Build up the Lucene query:
//            QueryParser qp = new QueryParser("dc:title", analyzer);
//            Query query = qp.parse(inputQuery);
//            //Query query = QueryParser.parse(inputQuery, LOMLuceneIndexCreator.LUCENE_FULLLOM, analyzer);
//            Logger.getLogger(LOMLuceneIndexCreator.LOGGER).info("Searching for: " + query.toString(LOMLuceneIndexCreator.lomfield_FullLom()));
//            Hits hits = searcher.search(query);
//            return hits.length();
//
//        } catch (Exception e) {
//            Logger.getLogger(LOMLuceneIndexCreator.LOGGER).error("LOMLuceneIndexSearcher::searchLuceneIndex error: " + e);
//            return -1;
//        } finally {
//            try {
//                searcher.close();
//            } catch (Exception e) {
//                return -1;
//            }
//        }
//
//    }
//
//
//    /**
//     * Search the lucene index that is located at getLuceneDirectory(). Example input queries are:
//     * - abc def: this will retrieve all results that contain abc and/or def
//     * - abc AND def: this will retrieve all results that contain abc and def
//     * - "abc def": this will retrieve all results that contain the exact combination "abc def"
//     * - ...
//     *
//     * @param fieldsToGet For example: {LOMLuceneIndexCreator.LUCENE_GENERALTITLE, LOMLuceneIndexCreator.LUCENE_TECHNICALSIZE}. If null is provided
//     *                    then all fields (except the full lom) is retrieved
//     */
//    public void searchLuceneIndexCommandLine(String[] fieldsToGet) {
//        Searcher searcher = null;
//        if (fieldsToGet == null) {
//            fieldsToGet = ALL_SAMGILUCENE_FIELDS;
//        }
//
//        try {
//            // Initialize the searcher and analyzer
//            searcher = new org.apache.lucene.search.IndexSearcher(getLuceneDirectory());
//            //Analyzer analyzer = new StandardAnalyzer();
//
//            // Read input queries from standard input
//            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//            while (true) {
//                // Read the input query from standard input
//                System.out.print("Query: ");
//                String line = in.readLine();
//                if (line.length() == -1)
//                    break;
//
//                // Build up the Lucene query:
//                java.util.Vector resultDetailsVector = searchLuceneIndex(line, fieldsToGet);
//                System.out.println(resultDetailsVector.size() + " total matching documents");
//
//                // Show the results with HITS_PER_PAGE at a time
//                final int HITS_PER_PAGE = 10;
//                for (int start = 0; start < resultDetailsVector.size(); start += HITS_PER_PAGE) {
//                    int end = Math.min(resultDetailsVector.size(), start + HITS_PER_PAGE);
//                    for (int i = start; i < end; i++) {
//                        System.out.println("Result " + (i + 1) + ": \n-------------------------");
//                        HashMap resultDetails = (HashMap) resultDetailsVector.elementAt(i);
//                        Set resultDetailsSet = resultDetails.entrySet();
//                        Iterator resultDetailsIterator = resultDetailsSet.iterator();
//                        int j = 1;
//                        while (resultDetailsIterator.hasNext()) {
//                            Map.Entry o = (Map.Entry) resultDetailsIterator.next();
//                            System.out.println(o.getKey() + " = " + o.getValue());
//                            j++;
//                        }
//                        System.out.println("\n");
//                    }
//                    System.out.println("Retrieved results " + (start + 1) + " to " + end + " of " + resultDetailsVector.size());
//                    if (resultDetailsVector.size() > end) {
//                        System.out.print("more (y/n) ? ");
//                        line = in.readLine();
//                        if (line.length() == 0 || line.charAt(0) == 'n')
//                            break;
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            System.out.println(" caught a " + e.getClass() +
//                    "\n with message: " + e.getMessage());
//        } finally {
//            try {
//                searcher.close();
//            } catch (Exception e) {
//                //
//            }
//        }
//    }
//
//
//}
