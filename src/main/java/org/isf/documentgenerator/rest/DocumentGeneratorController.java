/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.documentgenerator.rest;

import java.util.HashMap;

import org.isf.documentgenerator.DocumentGeneratorStrategyContext;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;

@RestController
@Api(value = "/documentGenerator", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = { @Authorization(value = "apiKey") })
public class DocumentGeneratorController {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DocumentGeneratorController.class);

	@Autowired
	private DocumentGeneratorStrategyContext service;

	@GetMapping(value = "/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
	public FileSystemResource getExams(@PathVariable int type) throws OHServiceException {
		LOGGER.debug("request for document type: " + type);
		return new FileSystemResource(service.generate(service.calculateStrategy(type).get(), new HashMap<String, Object>(), false));
	}

}
