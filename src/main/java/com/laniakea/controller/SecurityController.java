package com.laniakea.controller;

import com.laniakea.message.request.SearchForm;
import com.laniakea.message.response.IsItMy_JSON;
import com.laniakea.message.response.IsItMy_Sprav_JSON;
import com.laniakea.repository.*;
import com.laniakea.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class SecurityController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserRepositoryJPA userRepositoryJPA;
    @Autowired
    CompanyRepositoryJPA companyRepositoryJPA;
    @Autowired
    DepartmentRepositoryJPA departmentRepositoryJPA;
    @Autowired
    UserGroupRepositoryJPA userGroupRepositoryJPA;
    @Autowired
    UserDetailsServiceImpl userRepository2;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private UserDetailsServiceImpl userService;
    @Autowired
    SecurityRepositoryJPA securityRepositoryJPA;

    //Отдает набор прав пользователя из таблицы permissions по id документа
    @PostMapping("/api/auth/giveMeMyPermissions")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> giveMeMyPermissions(@RequestBody SearchForm request) {
        Long id = Long.valueOf(Integer.parseInt(request.getDocumentId()));
        List<Integer> depList =securityRepositoryJPA.giveMeMyPermissions(id);
        ResponseEntity<List> responseEntity = new ResponseEntity<>(depList, HttpStatus.OK);
        return responseEntity;
    }
    //Отдает весь набор прав пользователя из таблицы permissions
    @PostMapping("/api/auth/getAllMyPermissions")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> getAllMyPermissions(@RequestBody SearchForm request) {
        List<Integer> depList =securityRepositoryJPA.getAllMyPermissions();
        ResponseEntity<List> responseEntity = new ResponseEntity<>(depList, HttpStatus.OK);
        return responseEntity;
    }
    //Проверяет моё ли это предприятие по его id
    @PostMapping("/api/auth/isItMyCompany")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> isItMyCompany(@RequestBody SearchForm request) {
        Long id = Long.valueOf(Integer.parseInt(request.getDocumentId()));
        boolean isItMyCompany =securityRepositoryJPA.isItMyCompany(id);
        ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(isItMyCompany, HttpStatus.OK);
        return responseEntity;
    }
    //Проверяет моё ли это отделение по его id
    @PostMapping("/api/auth/isItMyDepartment")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> isItMyDepartment(@RequestBody SearchForm request) {
        Long id = Long.valueOf(Integer.parseInt(request.getDocumentId()));
        boolean isItMyDepartment =securityRepositoryJPA.isItMyDepartment(id);
        ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(isItMyDepartment, HttpStatus.OK);
        return responseEntity;
    }
    //Проверяет мой ли это аккаунт по id
    @PostMapping("/api/auth/isItMyUser")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> isItMyUser(@RequestBody SearchForm request) {
        Long id = Long.valueOf(Integer.parseInt(request.getDocumentId()));
        boolean isItMyUser =securityRepositoryJPA.isItMyUser(id);
        ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(isItMyUser, HttpStatus.OK);
        return responseEntity;
    }
    //Проверяет, что этот документ мой (создатель)
    @PostMapping("/api/auth/isItMyUserGroup")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> isItMyUserGroup(@RequestBody SearchForm request) {
        Long id = Long.valueOf(Integer.parseInt(request.getDocumentId()));
        boolean isItMyUserGroup =securityRepositoryJPA.isItMyUserGroup(id);
        ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(isItMyUserGroup, HttpStatus.OK);
        return responseEntity;
    }
    //Возвращает набор проверок на документ (документ мой?/документ моих отделений?/документ моего предприятия?)
    @PostMapping("/api/auth/getIsItMy_TradeResults_JSON")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> getIsItMy_TradeResults_JSON(@RequestBody SearchForm request) {
        Long id = Long.valueOf(Integer.parseInt(request.getDocumentId()));
        IsItMy_JSON response;
        response=securityRepositoryJPA.getIsItMy_TradeResults_JSON(id);
        ResponseEntity<IsItMy_JSON> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        return responseEntity;
    }
    //Возвращает набор проверок на документ (документ моего предприятия?/документ предприятий мастер-аккаунта?)
    @PostMapping("/api/auth/getIsItMy_SpravSysEdizm_JSON")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> getIsItMy_SpravSysEdizm_JSON(@RequestBody SearchForm request) {
        Long id = Long.valueOf(Integer.parseInt(request.getDocumentId()));
        IsItMy_Sprav_JSON response;
        response=securityRepositoryJPA.getIsItMy_SpravSysEdizm_JSON(id);
        ResponseEntity<IsItMy_Sprav_JSON> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        return responseEntity;
    }
    //Возвращает набор проверок на документ (документ моего предприятия?/документ предприятий мастер-аккаунта?)
    @PostMapping("/api/auth/getIsItMy_TypePrices_JSON")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> getIsItMy_TypePrices_JSON(@RequestBody SearchForm request) {
        Long id = Long.valueOf(Integer.parseInt(request.getDocumentId()));
        IsItMy_Sprav_JSON response;
        response=securityRepositoryJPA.getIsItMy_TypePrices_JSON(id);
        ResponseEntity<IsItMy_Sprav_JSON> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        return responseEntity;
    }
    //Возвращает набор проверок на документ (документ моего предприятия?/документ предприятий мастер-аккаунта?)
    @PostMapping("/api/auth/getIsItMy_ProductGroups_JSON")
    @SuppressWarnings("Duplicates")
    public ResponseEntity<?> getIsItMy_ProductGroups_JSON(@RequestBody SearchForm request) {
        Long id = Long.valueOf(Integer.parseInt(request.getDocumentId()));
        IsItMy_Sprav_JSON response;
        response=securityRepositoryJPA.getIsItMy_ProductGroups_JSON(id);
        ResponseEntity<IsItMy_Sprav_JSON> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        return responseEntity;
    }

}