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

//package org.ariadne.oai.harvestWriter;
//
//import java.io.IOException;
//import java.util.Vector;
//
//import org.ariadne.lucene.lomlucene.LOMLuceneIndexCreator;
//
//public class LuceneWriter extends GenericWriter {
//
//	public void CreateTarget(String location) {
//
//		this.target = new LOMLuceneIndexCreator(location);
//	}
//
//	public void pushAway(Vector lomVector, String repositoryIdentifier, String set) {
//		try {
//			((LOMLuceneIndexCreator)target).createIndex(lomVector);
//		} catch (IOException e) {
//			System.out.println(e.getMessage());
//		}
//
//	}
//
//	public void disconnect() {
//		// NOOP
//	}
//
//	@Override
//	public void connect() {
//		// NOOP
//		
//	}
//}
