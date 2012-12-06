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

package org.ariadne.oai.config.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.PropertyConfigurator;
import org.ariadne.config.PropertiesManager;
import org.ariadne.validation.Validator;
import org.ariadne.validation.exception.InitialisationException;

public class UpdateValidationPropertiesServlet extends HttpServlet {


	public void init() {
		try {
			String prefix = getServletContext().getRealPath("");
			PropertiesManager manager = new PropertiesManager();
			String fileName = prefix + File.separator + getInitParameter("validation-properties-file");
			File file = new File(fileName);
			if(!file.exists()) {
				file.createNewFile();
				manager.init(fileName);
				Validator.setPropertiesManager(manager);
				Validator.updatePropertiesFileFromRemote();
			}else {
				manager.init(fileName);
				Validator.setPropertiesManager(manager);	
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InitialisationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
