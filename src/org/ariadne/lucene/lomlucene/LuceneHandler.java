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
//import java.util.Vector;
//
//import org.apache.lucene.document.Document;
//import org.apache.lucene.document.Field;
//import org.xml.sax.Attributes;
//import org.xml.sax.SAXException;
//import org.xml.sax.helpers.DefaultHandler;
//
///**
// * Created by IntelliJ IDEA.
// * User: bram
// * Date: Aug 22, 2006
// * Time: 10:10:08 AM
// * To change this template use File | Settings | File Templates.
// */
//public class LuceneHandler extends DefaultHandler {
//
//    private Document doc;
//    private Vector path;
//    private String fullContent = "";
//
//    public LuceneHandler(){
//        doc = null;
//        path = new Vector();
//    }
//
//
//    public void startDocument() throws SAXException {
//        System.out.println("let's get going");
//        if (doc != null){
//            System.out.println("this should not be happening");
//        }
//        doc = new Document();
//        System.out.println("doc created");
//    }
//
//
//    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//        System.out.println("element found: " + qName);
//        path.add(qName);
//    }
//
//    public void characters (char ch[], int start, int length) throws SAXException {
//        String toAdd = new String(ch, start, length);
//
//
//
//        if (!(toAdd.equals(toAdd.toUpperCase()) && toAdd.equals(toAdd.toLowerCase()))){
//        String addPath = "";
//
//        for (int i = 0; i < path.size()-1; i++) {
//            String s = (String) path.elementAt(i);
//            addPath = addPath + s + "." ;
//        }
//
//        addPath = addPath + (String)path.elementAt(path.size()-1);
//        addPath = addPath.toLowerCase();
//        toAdd = toAdd.toLowerCase();
//        Field f = new Field(addPath, toAdd, Field.Store.YES, Field.Index.TOKENIZED);
//
//        System.out.println("added field " + addPath);
//        System.out.println("with value " + toAdd);
//        fullContent = fullContent + " " + toAdd;
//        doc.add(f);
//        }
//
//    }
//
//
//    public void endElement (String uri, String localName, String qName) throws SAXException{
//        System.out.println("end of element found" + qName);
//        path.removeElementAt(path.size()-1);
//    }
//
//
//    public void endDocument () throws SAXException {
//        Field f = new Field("fullContent", fullContent, Field.Store.YES, Field.Index.TOKENIZED);
//        System.out.println(fullContent);
//        doc.add(f);
//    }
//
//    public Document getDocument() {
//        return doc;
//    }
//
//    public void setDocument(Document tempDoc) {
//        this.doc = tempDoc;
//    }
//
//}
