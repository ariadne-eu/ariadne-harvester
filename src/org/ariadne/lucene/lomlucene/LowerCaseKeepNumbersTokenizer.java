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
//import java.io.Reader;
//
//import org.apache.lucene.analysis.LetterTokenizer;
//
///**
// * Created by IntelliJ IDEA.
// * User: stefaan
// * Date: Apr 20, 2005
// * Time: 2:59:59 PM
// * To change this template use File | Settings | File Templates.
// */
//
//public final class LowerCaseKeepNumbersTokenizer extends LetterTokenizer {
// /** Construct a new LowerCaseKeepNumbersTokenizer. */
// public LowerCaseKeepNumbersTokenizer(Reader in) {
//   super(in);
// }
//
// /** Collects only characters which satisfy
//  * {@link Character#isLetter(char)}.*/
// protected char normalize(char c) {
//   return Character.toLowerCase(c);
// }
//
// /** Collects only characters which satisfy
//  * {@link Character#isLetter(char)}.*/
// protected boolean isTokenChar(char c) {
//     return Character.isLetterOrDigit(c);
// }
//}
