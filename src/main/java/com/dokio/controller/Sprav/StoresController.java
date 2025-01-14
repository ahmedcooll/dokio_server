package com.dokio.controller.Sprav;

import com.dokio.message.request.Search2Form;
import com.dokio.message.request.SignUpForm;
import com.dokio.message.request.Sprav.StoresForm;
import com.dokio.repository.StoreRepository;
import com.dokio.util.CommonUtilites;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;

@Controller
public class StoresController {

    Logger logger = Logger.getLogger("SpravTaxesController");

    @Autowired
    StoreRepository storesRepository;
    @Autowired
    CommonUtilites commonUtilites;

    @PostMapping("/api/auth/getStoresTable")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> getStoresTable(@RequestBody Search2Form searchRequest) {
        logger.info("Processing post request for path /api/auth/getStoresTable: " + searchRequest.toString());
        String searchString = searchRequest.getSearchString();
        String sortColumn = searchRequest.getSortColumn();
        String sortAsc;
        if (searchRequest.getSortColumn() != null && !searchRequest.getSortColumn().isEmpty() && searchRequest.getSortColumn().trim().length() > 0) {
            sortAsc = searchRequest.getSortAsc();// если SortColumn определена, значит и sortAsc есть.
        } else {
            sortColumn = "date_created";
            sortAsc = "asc";
        }
        int result = (Objects.isNull(searchRequest.getResult())?10:searchRequest.getResult()); // количество записей, отображаемых на странице (по умолчанию 10)
        int offset = (Objects.isNull(searchRequest.getOffset())?0:searchRequest.getOffset()); // номер страницы. Изначально это null
        int offsetreal = offset * result;//создана переменная с номером страницы
        return new ResponseEntity<List>(storesRepository.getStoresTable(result, offsetreal, searchString, sortColumn, sortAsc, searchRequest.getCompanyId(), searchRequest.getFilterOptionsIds()), HttpStatus.OK);
    }

    @PostMapping("/api/auth/getStoresPagesList")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> getStoresPagesList(@RequestBody Search2Form searchRequest) {
        logger.info("Processing post request for path /api/auth/getStoresPagesList: " + searchRequest.toString());
        int offset = (Objects.isNull(searchRequest.getOffset())?0:searchRequest.getOffset()); // номер страницы. Изначально это null
        int result = (Objects.isNull(searchRequest.getResult())?10:searchRequest.getResult()); // количество записей, отображаемых на странице (по умолчанию 10)
        String searchString = searchRequest.getSearchString();
        int size = storesRepository.getStoresSize(searchString, searchRequest.getCompanyId(), searchRequest.getFilterOptionsIds());//  - общее количество записей выборки
        return new ResponseEntity<List>(commonUtilites.getPagesList(offset + 1, size, result), HttpStatus.OK);
    }

    @PostMapping("/api/auth/insertStores")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> insertStores(@RequestBody StoresForm request){
        logger.info("Processing post request for path /api/auth/insertStores: " + request.toString());
        return new ResponseEntity<>(storesRepository.insertStores(request), HttpStatus.OK);
    }

    @RequestMapping(
            value = "/api/auth/getStoresValuesById",
            params = {"id"},
            method = RequestMethod.GET, produces = "application/json;charset=utf8")
    public ResponseEntity<?> getStoresValuesById(
            @RequestParam("id") Long id){
        logger.info("Processing get request for path /api/auth/getStoresValuesById with parameters: " + "id: " + id);
        try {return new ResponseEntity<>(storesRepository.getStoresValues(id), HttpStatus.OK);}
        catch (Exception e){return new ResponseEntity<>("Error loading document values", HttpStatus.INTERNAL_SERVER_ERROR);}
    }

    @PostMapping("/api/auth/updateStores")
    public ResponseEntity<?> updateStores(@RequestBody StoresForm request){
        logger.info("Processing post request for path /api/auth/updateStores: " + request.toString());
        try {return new ResponseEntity<>(storesRepository.updateStores(request), HttpStatus.OK);}
        catch (Exception e){return new ResponseEntity<>("Error saving document", HttpStatus.INTERNAL_SERVER_ERROR);}
    }

    @PostMapping("/api/auth/deleteStores")
    @SuppressWarnings("Duplicates")
    public  ResponseEntity<?> deleteStores(@RequestBody SignUpForm request) {
        logger.info("Processing post request for path /api/auth/deleteStores: " + request.toString());
        String checked = request.getChecked() == null ? "": request.getChecked();
        try {return new ResponseEntity<>(storesRepository.deleteStores(checked), HttpStatus.OK);}
        catch (Exception e){return new ResponseEntity<>("Deletion error", HttpStatus.INTERNAL_SERVER_ERROR);}
    }

    @PostMapping("/api/auth/undeleteStores")
    @SuppressWarnings("Duplicates")
    public  ResponseEntity<?> undeleteStores(@RequestBody SignUpForm request) {
        logger.info("Processing post request for path /api/auth/undeleteStores: " + request.toString());
        String checked = request.getChecked() == null ? "" : request.getChecked();
        try {return new ResponseEntity<>(storesRepository.undeleteStores(checked), HttpStatus.OK);}
        catch (Exception e){return new ResponseEntity<>("Restore error", HttpStatus.INTERNAL_SERVER_ERROR);}
    }

    @RequestMapping(
            value = "/api/auth/getStoresList",
            params = {"company_id"},
            method = RequestMethod.GET, produces = "application/json;charset=utf8")
    public ResponseEntity<?> getStoresList(
            @RequestParam("company_id") Long company_id){
        logger.info("Processing get request for path /api/auth/getStoresList with parameters: " + "company_id: " + company_id);
        try {return new ResponseEntity<>(storesRepository.getStoresList(company_id), HttpStatus.OK);}
        catch (Exception e){return new ResponseEntity<>("Error loading data", HttpStatus.INTERNAL_SERVER_ERROR);}
    }

    @RequestMapping(
            value = "/api/auth/getStoresLanguagesList",
            params = {"company_id"},
            method = RequestMethod.GET, produces = "application/json;charset=utf8")
    public ResponseEntity<?> getStoresLanguagesList(
            @RequestParam("company_id") Long company_id){
        logger.info("Processing get request for path /api/auth/getStoresLanguagesList with parameters: " + "company_id: " + company_id);
        try {return new ResponseEntity<>(storesRepository.getStoresLanguagesList(company_id), HttpStatus.OK);}
        catch (Exception e){return new ResponseEntity<>("Error loading document values", HttpStatus.INTERNAL_SERVER_ERROR);}
    }

    @RequestMapping(
            value = "/api/auth/getStoreCategoryTranslationsList",
            params = {"category_id"},
            method = RequestMethod.GET, produces = "application/json;charset=utf8")
    public ResponseEntity<?> getStoreCategoryTranslationsList(
            @RequestParam("category_id") Long category_id){
        logger.info("Processing get request for path /api/auth/getStoreCategoryTranslationsList with parameters: " + "category_id: " + category_id);
        try {return new ResponseEntity<>(storesRepository.getStoreCategoryTranslationsList(category_id), HttpStatus.OK);}
        catch (Exception e){return new ResponseEntity<>("Error loading document values", HttpStatus.INTERNAL_SERVER_ERROR);}
    }

}
