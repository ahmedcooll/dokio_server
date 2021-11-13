/*
Приложение Dokio-server - учет продаж, управление складскими остатками, документооборот.
Copyright © 2020 Сунцов Михаил Александрович. mihail.suntsov@yandex.ru
Эта программа является свободным программным обеспечением: Вы можете распространять ее и (или) изменять,
соблюдая условия Генеральной публичной лицензии GNU редакции 3, опубликованной Фондом свободного
программного обеспечения;
Эта программа распространяется в расчете на то, что она окажется полезной, но
БЕЗ КАКИХ-ЛИБО ГАРАНТИЙ, включая подразумеваемую гарантию КАЧЕСТВА либо
ПРИГОДНОСТИ ДЛЯ ОПРЕДЕЛЕННЫХ ЦЕЛЕЙ. Ознакомьтесь с Генеральной публичной
лицензией GNU для получения более подробной информации.
Вы должны были получить копию Генеральной публичной лицензии GNU вместе с этой
программой. Если Вы ее не получили, то перейдите по адресу:
<http://www.gnu.org/licenses/>
 */
package com.dokio.controller;
import com.dokio.message.request.SignUpForm;
import com.dokio.message.request.UniversalForm;
import com.dokio.message.response.CompaniesJSON;
import com.dokio.message.request.CompaniesForm;
import com.dokio.message.request.SearchForm;
import com.dokio.message.response.FileInfoJSON;
import com.dokio.message.response.additional.FilesCompaniesJSON;
import com.dokio.message.response.Sprav.IdAndName;
import com.dokio.repository.CompanyRepositoryJPA;
import com.dokio.repository.FileRepositoryJPA;
import com.dokio.repository.UserGroupRepositoryJPA;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.dokio.service.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CompaniesController {
    Logger logger = Logger.getLogger("CompaniesController");

    @Autowired
    CompanyRepositoryJPA companyRepositoryJPA;
    @Autowired
    UserGroupRepositoryJPA userGroupRepositoryJPA;
    @Autowired
    StorageService storageService;
    @Autowired
    FileRepositoryJPA fileRepository;

    @PostMapping("/api/auth/getCompaniesTable")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> getCompaniesTable(@RequestBody SearchForm searchRequest) {
        logger.info("Processing post request for path /api/auth/getCompaniesTable: " + searchRequest.toString());

        int offset; // номер страницы. Изначально это null
        int result; // количество записей, отображаемых на странице
        String searchString = searchRequest.getSearchString();
        String sortColumn = searchRequest.getSortColumn();
        String sortAsc;
        List<CompaniesJSON> returnList;

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
        if (searchRequest.getOffset() != null && !searchRequest.getOffset().isEmpty() && searchRequest.getOffset().trim().length() > 0) {
            offset = Integer.parseInt(searchRequest.getOffset());
        } else {
            offset = 0;
        }
        int offsetreal = offset * result;//создана переменная с номером страницы
        returnList = companyRepositoryJPA.getCompaniesTable(result, offsetreal, searchString, sortColumn, sortAsc, searchRequest.getFilterOptionsIds());//запрос списка: взять кол-во rezult, начиная с offsetreal
        ResponseEntity<List> responseEntity = new ResponseEntity<>(returnList, HttpStatus.OK);
        return responseEntity;
    }

    @PostMapping("/api/auth/getCompaniesList")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> getCompaniesList() {
        logger.info("Processing post request for path /api/auth/getCompaniesList");

        List<IdAndName> companiesList;
        companiesList = companyRepositoryJPA.getCompaniesList();
        return new ResponseEntity<>(companiesList, HttpStatus.OK);
    }

    @PostMapping("/api/auth/insertCompany")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> insertCompany(@RequestBody CompaniesForm request){
        logger.info("Processing post request for path /api/auth/insertCompany: " + request.toString());

        Long newDocument = companyRepositoryJPA.insertCompany(request);
        if(newDocument!=null && newDocument>0){
            ResponseEntity<String> responseEntity = new ResponseEntity<>("[\n" + String.valueOf(newDocument)+"\n" +  "]", HttpStatus.OK);
            return responseEntity;
        } else {
            ResponseEntity<String> responseEntity = new ResponseEntity<>("Error when inserting", HttpStatus.INTERNAL_SERVER_ERROR);
            return responseEntity;
        }
    }
    @PostMapping("/api/auth/deleteCompanies")
    @SuppressWarnings("Duplicates")
    public  ResponseEntity<?> deleteCompanies(@RequestBody SignUpForm request){
        logger.info("Processing post request for path /api/auth/deleteCompanies: " + request.toString());

        String checked = request.getChecked() == null ? "": request.getChecked();
//        checked=checked.replace("[","");
//        checked=checked.replace("]","");
        if(companyRepositoryJPA.deleteCompanies(checked)){
            ResponseEntity<String> responseEntity = new ResponseEntity<>("[\n" + "    1\n" +  "]", HttpStatus.OK);
            return responseEntity;
        } else {
            ResponseEntity<String> responseEntity = new ResponseEntity<>("Error when requesting deleteCompanies", HttpStatus.INTERNAL_SERVER_ERROR);
            return responseEntity;
        }
    }
    @PostMapping("/api/auth/undeleteCompanies")
    @SuppressWarnings("Duplicates")
    public  ResponseEntity<?> undeleteCompanies(@RequestBody SignUpForm request){
        logger.info("Processing post request for path /api/auth/undeleteCompanies: " + request.toString());

        String checked = request.getChecked() == null ? "": request.getChecked();
//        checked=checked.replace("[","");
//        checked=checked.replace("]","");
        if(companyRepositoryJPA.undeleteCompanies(checked)){
            ResponseEntity<String> responseEntity = new ResponseEntity<>("[\n" + "    1\n" +  "]", HttpStatus.OK);
            return responseEntity;
        } else {
            ResponseEntity<String> responseEntity = new ResponseEntity<>("Error when requesting deleteCompanies", HttpStatus.INTERNAL_SERVER_ERROR);
            return responseEntity;
        }
    }
    @PostMapping("/api/auth/getCompaniesPagesList")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> getCompaniesPagesList(@RequestBody SearchForm searchRequest) {
        logger.info("Processing post request for path /api/auth/getCompaniesPagesList: " + searchRequest.toString());

        int offset; // номер страницы. Изначально это null
        int result; // количество записей, отображаемых на странице
        int pagenum;// отображаемый в пагинации номер страницы. Всегда на 1 больше чем offset. Если offset не определен то это первая страница
        String searchString = searchRequest.getSearchString();

        if (searchRequest.getResult() != null && !searchRequest.getResult().isEmpty() && searchRequest.getResult().trim().length() > 0) {
            result = Integer.parseInt(searchRequest.getResult());
        } else {
            result = 10;}
        if (searchRequest.getOffset() != null && !searchRequest.getOffset().isEmpty() && searchRequest.getOffset().trim().length() > 0) {
            offset = Integer.parseInt(searchRequest.getOffset());
        } else {
            offset = 0;}
        pagenum = offset + 1;
        int size = companyRepositoryJPA.getCompaniesSize(searchRequest);//  - общее количество записей выборки
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

    @PostMapping("/api/auth/getCompanyValues")//Отдает ЗНАЧЕНИЯ из таблицы companies по id предприятия
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> getCompanyValues(@RequestBody CompaniesForm companyRequest) {
        logger.info("Processing post request for path /api/auth/getCompanyValues: " + companyRequest.toString());

        CompaniesJSON company;
        Long id = companyRequest.getId();
        company=companyRepositoryJPA.getCompanyValues(id);//результат запроса помещается в объект
        ResponseEntity<CompaniesJSON> responseEntity = new ResponseEntity<>(company, HttpStatus.OK);
        return responseEntity;
    }

    @PostMapping("/api/auth/updateCompany")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> updateCompany(@RequestBody CompaniesForm companyRequest) {
        logger.info("Processing post request for path /api/auth/updateCompany: " + companyRequest.toString());

        if(companyRepositoryJPA.updateCompany(companyRequest)){
            return new ResponseEntity<>("[\n" + "    1\n" +  "]", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Error when updating", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(
            value = "/api/auth/getCompaniesPaymentAccounts",// отдаёт список банковских счетов контрагента или своего предприятия
            params = {"id"},
            method = RequestMethod.GET, produces = "application/json;charset=utf8")
    public ResponseEntity<?> getCompaniesPaymentAccounts(
            @RequestParam("id") Long id){
        logger.info("Processing post request for path /api/auth/getCompaniesPaymentAccounts, id = " + id);
        try {
            return  new ResponseEntity<>(companyRepositoryJPA.getCompanyPaymentAccounts(id), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("Ошибка запроса списка счетов предприятия", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//*****************************************************************************************************************************************************
//****************************************************   F   I   L   E   S   **************************************************************************
//*****************************************************************************************************************************************************

    @PostMapping("/api/auth/getListOfCompanyFiles")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> getListOfCompanyFiles(@RequestBody UniversalForm request)  {
        logger.info("Processing post request for path /api/auth/getListOfCompanyFiles: " + request.toString());

        Long companyId=request.getId();
        List<FilesCompaniesJSON> returnList;
        try {
            returnList = companyRepositoryJPA.getListOfCompanyFiles(companyId);
            return new ResponseEntity<>(returnList, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("Error when requesting list of files", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/auth/deleteCompanyFile")
    public ResponseEntity<?> deleteCompanyFile(@RequestBody SearchForm request) {
        logger.info("Processing post request for path /api/auth/deleteCompanyFile: " + request.toString());

        if(companyRepositoryJPA.deleteCompanyFile(request)){
            return new ResponseEntity<>("[\n" + "    1\n" +  "]", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Error when deleting file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @SuppressWarnings("Duplicates")
    @PostMapping("/api/auth/addFilesToCompany")
    public ResponseEntity<?> addFilesToCompany(@RequestBody UniversalForm request) {
        logger.info("Processing post request for path /api/auth/addFilesToCompany: " + request.toString());

        if(companyRepositoryJPA.addFilesToCompany(request)){
            ResponseEntity<String> responseEntity = new ResponseEntity<>("[\n" + "    1\n" +  "]", HttpStatus.OK);
            return responseEntity;
        } else {
            ResponseEntity<String> responseEntity = new ResponseEntity<>("Error when adding file", HttpStatus.INTERNAL_SERVER_ERROR);
            return responseEntity;
        }
    }

    @SuppressWarnings("Duplicates")
    @GetMapping("/api/auth/getCompanyCard/{fileId:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getCompanyCard(@PathVariable String fileId) throws UnsupportedEncodingException {
        logger.info("Processing get request for path /api/auth/getCompanyCard: fileId=" + fileId);

        FileInfoJSON fileInfo = fileRepository.getFileAuth(fileId); //Взять path файла, если есть права или если он открыт на общий доступ
        if(fileInfo !=null){
            fileInfo.setOriginal_name(fileId);//подменим в этом поле оригинальное название файла системным именем (типа 0f8fkdlk-234-342-34-43-343.docx)
            Resource file = companyRepositoryJPA.getCompanyCard(fileInfo);//и отправим экземпляр класса FileInfoJSON с путём к файлу и системным именем файла на получение карточки
            if(file!=null) {//если файл есть - значит docx4j отработал, и успешно записал файл Карточка предприятия.docx.
                String fileName = "Карточка предприятия.docx";
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(fileName, "UTF-8").replace("+", " ") + "\"")
                        .body(file);
            }else {ResponseEntity responseEntity = new ResponseEntity<>("Невозможно сформировать карточку предприятия", HttpStatus.INTERNAL_SERVER_ERROR);
                return responseEntity;}
        } else {ResponseEntity responseEntity = new ResponseEntity<>("Недостаточно прав на файл, или файла нет в базе данных.", HttpStatus.FORBIDDEN);
            return responseEntity;}
    }

}
