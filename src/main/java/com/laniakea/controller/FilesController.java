package com.laniakea.controller;

import com.laniakea.message.request.*;
import com.laniakea.message.response.*;
import com.laniakea.model.FileCategories;
import com.laniakea.model.ProductCategories;
import com.laniakea.repository.*;
import com.laniakea.security.services.UserDetailsServiceImpl;
import com.laniakea.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FilesController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserRepositoryJPA userRepositoryJPA;
    @Autowired
    CompanyRepositoryJPA companyRepositoryJPA;
    @Autowired
    UserDetailsServiceImpl userRepository2;
    @Autowired
    UserGroupRepositoryJPA userGroupRepositoryJPA;
    @Autowired
    FileRepositoryJPA filesRepositoryJPA;
    @Autowired
    StorageService storageService;


    @PostMapping("/api/auth/getFilesTable")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> getFilesTable(@RequestBody SearchForm searchRequest) {
        int offset; // номер страницы. Изначально это null
        int result; // количество записей, отображаемых на странице
        int pagenum;// отображаемый в пагинации номер страницы. Всегда на 1 больше чем offset. Если offset не определен то это первая страница
        int companyId;//по какому предприятию показывать / 0 - по всем (подставляется ниже, а так то прередаётся "" если по всем)
        int categoryId;//по какой категории товаров показывать / 0 - по всем (--//--//--//--//--//--//--)
        String searchString = searchRequest.getSearchString();
        String sortColumn = searchRequest.getSortColumn();
        String sortAsc;
        List<FilesTableJSON> returnList;

        if (searchRequest.getSortColumn() != null && !searchRequest.getSortColumn().isEmpty() && searchRequest.getSortColumn().trim().length() > 0) {
            sortAsc = searchRequest.getSortAsc();// если SortColumn определена, значит и sortAsc есть.
        } else {
            sortColumn = "name";
            sortAsc = "asc";
        }
        if (searchRequest.getResult() != null && !searchRequest.getResult().isEmpty() && searchRequest.getResult().trim().length() > 0) {
            result = Integer.parseInt(searchRequest.getResult());
        } else {
            result = 10;
        }
        if (searchRequest.getCompanyId() != null && !searchRequest.getCompanyId().isEmpty() && searchRequest.getCompanyId().trim().length() > 0) {
            companyId = Integer.parseInt(searchRequest.getCompanyId());
        } else {
            companyId = 0;
        }
        if (searchRequest.getCategoryId() != null && !searchRequest.getCategoryId().isEmpty() && searchRequest.getCategoryId().trim().length() > 0) {
            categoryId = Integer.parseInt(searchRequest.getCategoryId());
        } else {
            categoryId = 0;
        }
        if (searchRequest.getOffset() != null && !searchRequest.getOffset().isEmpty() && searchRequest.getOffset().trim().length() > 0) {
            offset = Integer.parseInt(searchRequest.getOffset());
        } else {
            offset = 0;
        }
        int offsetreal = offset * result;//создана переменная с номером страницы/
        // searchRequest.isAny_boolean() - тут приходит true - показывать файлы корзины, false - показывать неудаленные файлы
        // searchRequest.isAny_boolean2() - true - показываем только files.anonyme_access доступ, false - все файлы
        returnList = filesRepositoryJPA.getFilesTable(
                result,
                offsetreal,
                searchString,
                sortColumn,
                sortAsc,
                companyId,
                categoryId,
                searchRequest.isAny_boolean(),
                searchRequest.isAny_boolean2()
        );//запрос списка: взять кол-во rezult, начиная с offsetreal
        ResponseEntity<List> responseEntity = new ResponseEntity<>(returnList, HttpStatus.OK);
        return responseEntity;
    }
    @PostMapping("/api/auth/getFilesPagesList")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> getFilesPagesList(@RequestBody SearchForm searchRequest) {
        int offset; // номер страницы. Изначально это null
        int result; // количество записей, отображаемых на странице
        int pagenum;// отображаемый в пагинации номер страницы. Всегда на 1 больше чем offset. Если offset не определен то это первая страница
        int companyId;//по какому предприятию показывать документы/ 0 - по всем
        int categoryId;//по какой категории товаров показывать / 0 - по всем (--//--//--//--//--//--//--)
        int disabledLINK;// номер страницы на паджинейшене, на которой мы сейчас. Изначально это 1.
        String searchString = searchRequest.getSearchString();
        companyId = Integer.parseInt(searchRequest.getCompanyId());
        String sortColumn = searchRequest.getSortColumn();
        if (searchRequest.getCategoryId() != null && !searchRequest.getCategoryId().isEmpty() && searchRequest.getCategoryId().trim().length() > 0) {
            categoryId = Integer.parseInt(searchRequest.getCategoryId());
        } else {
            categoryId = 0;}
        if (searchRequest.getResult() != null && !searchRequest.getResult().isEmpty() && searchRequest.getResult().trim().length() > 0) {
            result = Integer.parseInt(searchRequest.getResult());
        } else {
            result = 10;}
        if (searchRequest.getOffset() != null && !searchRequest.getOffset().isEmpty() && searchRequest.getOffset().trim().length() > 0) {
            offset = Integer.parseInt(searchRequest.getOffset());
        } else {
            offset = 0;}
        pagenum = offset + 1;
        int size = filesRepositoryJPA.getFilesSize(
                searchString,
                companyId,
                categoryId,
                searchRequest.isAny_boolean(),
                searchRequest.isAny_boolean2()
        );//  - общее количество записей выборки
        int offsetreal = offset * result;//создана переменная с номером страницы
        int listsize;//количество страниц пагинации
        if((size%result) == 0){//общее количество выборки делим на количество записей на странице
            listsize= size/result;//если делится без остатка
        }else{
            listsize= (size/result)+1;}
        int maxPagenumInBegin;//
        List<Integer> pageList = new ArrayList<Integer>();//список, в котором первые 3 места - "всего найдено", "страница", "всего страниц", остальное - номера страниц для пагинации
        pageList.add(size);
        pageList.add(pagenum);
        pageList.add(listsize);

        if (listsize<=5){
            maxPagenumInBegin=listsize;//
        }else{
            maxPagenumInBegin=5;
        }
        if(pagenum >=3) {
            if((pagenum==listsize)||(pagenum+1)==listsize){
                for(int i=(pagenum-(4-(listsize-pagenum))); i<=pagenum-3; i++){
                    if(i>0) {
                        pageList.add(i);  //создается список пагинации за - 4 шага до номера страницы (для конца списка пагинации)
                    }}}
            for(int i=(pagenum-2); i<=pagenum; i++){
                pageList.add(i);  //создается список пагинации за -2 шага до номера страницы
            }
            if((pagenum+2) <=listsize) {
                for(int i=(pagenum+1); i<=(pagenum+2); i++){
                    pageList.add(i);  //создается список пагинации  на +2 шага от номера страницы
                }
            }else{
                if(pagenum<listsize) {
                    for (int i = (pagenum + (listsize - pagenum)); i <= listsize; i++) {
                        pageList.add(i);  //создается список пагинации от номера страницы до конца
                    }}}
        }else{//номер страницы меньше 3
            for(int i=1; i<=pagenum; i++){
                pageList.add(i);  //создается список пагинации от 1 до номера страницы
            }
            for(int i=(pagenum+1); i<=maxPagenumInBegin; i++){
                pageList.add(i);  //создаются дополнительные номера пагинации, но не более 5 в сумме
            }}
        ResponseEntity<List> responseEntity = new ResponseEntity<>(pageList, HttpStatus.OK);
        return responseEntity;
    }


    @PostMapping("/api/auth/getFileValues")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> getTypePricesValuesById(@RequestBody SearchForm request) {
        FilesJSON response;
        int id = request.getId();
        response=filesRepositoryJPA.getFileValues(id);//результат запроса помещается в экземпляр класса
        try
        {
            List<Integer> valuesListId =filesRepositoryJPA.getFilesCategoriesIdsByFileId(Long.valueOf(id));
            response.setFile_categories_id(valuesListId);

            ResponseEntity<FilesJSON> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
            return responseEntity;
        }
        catch(NullPointerException npe){return null;}

    }

//    @PostMapping("/api/auth/insertFile")
//    @SuppressWarnings("Duplicates")
//    public ResponseEntity<?> insertFile(@RequestBody FilesForm request) throws ParseException {
//        Long newDocument = filesRepositoryJPA.insertFile(request);
//        if(newDocument!=null && newDocument>0){
//            ResponseEntity<String> responseEntity = new ResponseEntity<>("[\n" + String.valueOf(newDocument)+"\n" +  "]", HttpStatus.OK);
//            return responseEntity;
//        } else {
//            ResponseEntity<String> responseEntity = new ResponseEntity<>("Error when inserting", HttpStatus.BAD_REQUEST);
//            return responseEntity;
//        }
//    }
    @PostMapping("/api/auth/updateFiles")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> updateFiles(@RequestBody FilesForm request) throws ParseException{
        if(filesRepositoryJPA.updateFiles(request)){
            ResponseEntity<String> responseEntity = new ResponseEntity<>("[\n" + "    1\n" +  "]", HttpStatus.OK);
            return responseEntity;
        } else {
            ResponseEntity<String> responseEntity = new ResponseEntity<>("Error when updating", HttpStatus.BAD_REQUEST);
            return responseEntity;
        }
    }

    @PostMapping("/api/auth/deleteFiles")
    @SuppressWarnings("Duplicates")
    public  ResponseEntity<?> deleteFiles(@RequestBody SignUpForm request) throws ParseException{
        String checked = request.getChecked() == null ? "": request.getChecked();
        if(filesRepositoryJPA.deleteFiles(checked)){
            ResponseEntity<String> responseEntity = new ResponseEntity<>("[\n" + "    1\n" +  "]", HttpStatus.OK);
            return responseEntity;
        } else {
            ResponseEntity<String> responseEntity = new ResponseEntity<>("Error when deleting", HttpStatus.INTERNAL_SERVER_ERROR);
            return responseEntity;
        }
    }

    @PostMapping("/api/auth/recoverFilesFromTrash")
    @SuppressWarnings("Duplicates")
    public  ResponseEntity<?> recoverFilesFromTrash(@RequestBody SignUpForm request) throws ParseException{
        String checked = request.getChecked() == null ? "": request.getChecked();
        if(filesRepositoryJPA.recoverFilesFromTrash(checked)){
            ResponseEntity<String> responseEntity = new ResponseEntity<>("[\n" + "    1\n" +  "]", HttpStatus.OK);
            return responseEntity;
        } else {
            ResponseEntity<String> responseEntity = new ResponseEntity<>("Error when repairing", HttpStatus.INTERNAL_SERVER_ERROR);
            return responseEntity;
        }
    }

    @PostMapping("/api/auth/deleteFilesFromTrash")
    @SuppressWarnings("Duplicates")
    public  ResponseEntity<?> deleteFilesFromTrash(@RequestBody SignUpForm request) throws ParseException{
        String checked = request.getChecked() == null ? "": request.getChecked();
        if(filesRepositoryJPA.deleteFilesFromTrash(checked)){
            ResponseEntity<String> responseEntity = new ResponseEntity<>("[\n" + "    1\n" +  "]", HttpStatus.OK);
            return responseEntity;
        } else {
            ResponseEntity<String> responseEntity = new ResponseEntity<>("Error when deleting", HttpStatus.INTERNAL_SERVER_ERROR);
            return responseEntity;
        }
    }

    @PostMapping("/api/auth/clearTrash")
    @SuppressWarnings("Duplicates")
    public  ResponseEntity<?> clearTrash(@RequestBody SignUpForm request) throws ParseException{
        Integer companyId = Integer.parseInt(request.getCompany_id());
        if(filesRepositoryJPA.clearTrash(companyId)){
            ResponseEntity<String> responseEntity = new ResponseEntity<>("[\n" + "    1\n" +  "]", HttpStatus.OK);
            return responseEntity;
        } else {
            ResponseEntity<String> responseEntity = new ResponseEntity<>("Error when deleting", HttpStatus.INTERNAL_SERVER_ERROR);
            return responseEntity;
        }
    }

    //*************************************************************************************************************************************************
//**************************************************  C A T E G O R I E S  ************************************************************************
//*************************************************************************************************************************************************
    @PostMapping("/api/auth/getFileCategoriesTrees")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> getFileCategoriesTrees(@RequestBody SearchForm request) throws ParseException {
        List<FileCategories> returnList;
        List<Integer> categoriesRootIds = filesRepositoryJPA.getCategoriesRootIds(Long.valueOf(Integer.parseInt((request.getCompanyId()))));//
        try {
            returnList = filesRepositoryJPA.getFileCategoriesTrees(categoriesRootIds);
            ResponseEntity responseEntity = new ResponseEntity<>(returnList, HttpStatus.OK);
            return responseEntity;
        } catch (Exception e){
            ResponseEntity responseEntity = new ResponseEntity<>("Error when requesting", HttpStatus.BAD_REQUEST);
            return responseEntity;
        }
    }

    @PostMapping("/api/auth/getRootFileCategories")
    @SuppressWarnings("Duplicates")
    //отдает только список корневых категорий, без детей
    //нужно для изменения порядка вывода корневых категорий
    public ResponseEntity<?> getRootFileCategories(@RequestBody SearchForm request) throws ParseException {
        List<FileCategoriesTableJSON> returnList ;//
        try {
            returnList = filesRepositoryJPA.getRootFileCategories(Long.valueOf(Integer.parseInt((request.getCompanyId()))));
            ResponseEntity responseEntity = new ResponseEntity<>(returnList, HttpStatus.OK);
            return responseEntity;
        } catch (Exception e){
            ResponseEntity responseEntity = new ResponseEntity<>("Error when requesting", HttpStatus.BAD_REQUEST);
            return responseEntity;
        }
    }


    @PostMapping("/api/auth/getChildrensFileCategories")//нужно для изменения порядка вывода категорий
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> getChildrensFileCategories(@RequestBody FileCategoriesForm request) throws ParseException {
        List<FileCategoriesTableJSON> returnList;
        try {
            returnList = filesRepositoryJPA.getChildrensFileCategories(request.getParentCategoryId());
            ResponseEntity responseEntity = new ResponseEntity<>(returnList, HttpStatus.OK);
            return responseEntity;
        } catch (Exception e){
            ResponseEntity responseEntity = new ResponseEntity<>("Error when requesting", HttpStatus.BAD_REQUEST);
            return responseEntity;
        }
    }

    @PostMapping("/api/auth/searchFileCategory")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> searchFileCategory(@RequestBody SearchForm request) throws ParseException {
        Long companyId=Long.valueOf(Integer.parseInt((request.getCompanyId())));
        List<FileCategoriesTableJSON> returnList;
        try {
            returnList = filesRepositoryJPA.searchFileCategory(companyId,request.getSearchString());
            ResponseEntity responseEntity = new ResponseEntity<>(returnList, HttpStatus.OK);
            return responseEntity;
        } catch (Exception e){
            ResponseEntity responseEntity = new ResponseEntity<>("Error when requesting", HttpStatus.BAD_REQUEST);
            return responseEntity;
        }
    }
    @PostMapping("/api/auth/insertFileCategory")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> insertFileCategory(@RequestBody FileCategoriesForm request) throws ParseException {
        try {
            Long categoryId = filesRepositoryJPA.insertFileCategory(request);
            ResponseEntity<Long> responseEntity = new ResponseEntity<>(categoryId, HttpStatus.OK);
            return responseEntity;
        }
        catch (Exception e) {
            e.printStackTrace();
            ResponseEntity<Long> responseEntity = new ResponseEntity<>(0L, HttpStatus.BAD_REQUEST);
            return responseEntity;
        }
    }

    @PostMapping("/api/auth/updateFileCategory")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> updateFileCategory(@RequestBody FileCategoriesForm request) throws ParseException{
        if(filesRepositoryJPA.updateFileCategory(request)){
            ResponseEntity<String> responseEntity = new ResponseEntity<>("[\n" + "    1\n" +  "]", HttpStatus.OK);
            return responseEntity;
        } else {
            ResponseEntity<String> responseEntity = new ResponseEntity<>("Error when updating", HttpStatus.BAD_REQUEST);
            return responseEntity;
        }
    }

    @PostMapping("/api/auth/deleteFileCategory")
    public ResponseEntity<?> deleteFileCategory(@RequestBody FileCategoriesForm request) throws ParseException{
        if(filesRepositoryJPA.deleteFileCategory(request)){
            ResponseEntity<String> responseEntity = new ResponseEntity<>("[\n" + "    1\n" +  "]", HttpStatus.OK);
            return responseEntity;
        } else {
            ResponseEntity<String> responseEntity = new ResponseEntity<>("Error when updating", HttpStatus.BAD_REQUEST);
            return responseEntity;
        }
    }

    @PostMapping("/api/auth/saveChangeFileCategoriesOrder")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> saveChangeFileCategoriesOrder(@RequestBody List<FileCategoriesForm> request) throws ParseException {
        if(filesRepositoryJPA.saveChangeCategoriesOrder(request)){
            ResponseEntity<String> responseEntity = new ResponseEntity<>("[\n" + "    1\n" +  "]", HttpStatus.OK);
            return responseEntity;
        } else {
            ResponseEntity<String> responseEntity = new ResponseEntity<>("Error when saving", HttpStatus.BAD_REQUEST);
            return responseEntity;
        }
    }

}