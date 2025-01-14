/*
        Dokio CRM - server part. Sales, finance and warehouse management system
        Copyright (C) Mikhail Suntsov /mihail.suntsov@gmail.com/

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU Affero General Public License as
        published by the Free Software Foundation, either version 3 of the
        License, or (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU Affero General Public License for more details.

        You should have received a copy of the GNU Affero General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>
*/
package com.dokio.controller;

import com.dokio.message.request.UniversalForm;
import com.dokio.repository.ProductsRepositoryJPA;
import com.dokio.util.CommonUtilites;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommonUtilitesController {


    Logger logger = Logger.getLogger("CommonUtilites");

    @Autowired
    CommonUtilites commonUtilites;

    @SuppressWarnings("Duplicates")
    @RequestMapping(
            value = "/api/auth/isDocumentNumberUnical",
            params = {"company_id", "doc_number", "doc_id", "table"},
            method = RequestMethod.GET, produces = "application/json;charset=utf8")

    public ResponseEntity<?> isDocumentNumberUnical(
            @RequestParam("company_id") Long company_id,
            @RequestParam("doc_number") Integer doc_number,
            @RequestParam("doc_id") Long doc_id,
            @RequestParam("table") String table)
    {
        logger.info("Processing get request for path /api/auth/isDocumentNumberUnical with parameters: " +
                "company_id: " + company_id.toString() +
                ", doc_number: " + doc_number.toString() +
                ", doc_id: " + doc_id.toString() +
                ", table: "+ table);
        try {
            Boolean ret = commonUtilites.isDocumentNumberUnical(company_id, doc_number, doc_id, table);
            return new ResponseEntity<>(ret, HttpStatus.OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
