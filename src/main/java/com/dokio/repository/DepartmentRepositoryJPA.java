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

package com.dokio.repository;

import com.dokio.message.request.DepartmentForm;
import com.dokio.message.response.DepartmentsListJSON;
import com.dokio.message.response.Settings.UserSettingsJSON;
import com.dokio.message.response.Sprav.IdAndName;
import com.dokio.model.Companies;
import com.dokio.model.Departments;
import com.dokio.message.response.DepartmentsJSON;
import com.dokio.model.Sprav.SpravSysDepartmentsList;
import com.dokio.security.services.UserDetailsServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository("DepartmentRepositoryJPA")
public class DepartmentRepositoryJPA {

    Logger logger = Logger.getLogger("DepartmentRepositoryJPA");

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory emf;

    @Autowired
    private UserDetailsServiceImpl userRepository;

    @Autowired
    private UserRepositoryJPA userRepositoryJPA;

    @Autowired
    SecurityRepositoryJPA securityRepositoryJPA;

    private static final Set VALID_COLUMNS_FOR_ORDER_BY
            = Collections.unmodifiableSet((Set<? extends String>) Stream
            .of("address","company","creator","date_time_created_sort","additional","name")
            .collect(Collectors.toCollection(HashSet::new)));
    private static final Set VALID_COLUMNS_FOR_ASC
            = Collections.unmodifiableSet((Set<? extends String>) Stream
            .of("asc","desc")
            .collect(Collectors.toCollection(HashSet::new)));
    @Transactional
    @SuppressWarnings("Duplicates")
    public List<DepartmentsJSON> getDepartmentsTable(int result, int offsetreal, String searchString, String sortColumn, String sortAsc, Long companyId, Set<Integer> filterOptionsIds) {
        if (securityRepositoryJPA.userHasPermissions_OR(4L, "14,13"))//(см. файл Permissions Id)
        {
            String stringQuery;
            UserSettingsJSON userSettings = userRepositoryJPA.getMySettings();
            String myTimeZone = userSettings.getTime_zone();
            String dateFormat = userSettings.getDateFormat();
            String timeFormat = (userSettings.getTimeFormat().equals("12")?" HH12:MI AM":" HH24:MI"); // '12' or '24'
            Long myMasterId = userRepositoryJPA.getUserMasterIdByUsername(userRepository.getUserName());
            boolean showDeleted = filterOptionsIds.contains(1);// Показывать только удаленные
            stringQuery = "select " +
                    "           p.id as id, " +
                    "           p.name as name, " +
                    "           us.username as creator, " +
                    "           uc.username as changer, " +
                    "           p.address as address, " +
                    "           p.additional as additional, " +
                    "           (select name from companies where id=p.company_id) as company, " +
                    "           (select count(*) from departments ds where ds.parent_id=p.id) as num_childrens," +
                    "           (select name from departments where id=p.parent_id) as parent, " +
                    "           to_char(p.date_time_created at time zone '"+myTimeZone+"', '"+dateFormat+timeFormat+"') as date_time_created, " +
                    "           to_char(p.date_time_changed at time zone '"+myTimeZone+"', '"+dateFormat+timeFormat+"') as date_time_changed, " +
                    "           p.date_time_created as date_time_created_sort, " +
                    "           p.date_time_changed as date_time_changed_sort " +
                    "           from departments p " +
                    "           INNER JOIN users u ON p.master_id=u.id " +
                    "           LEFT OUTER JOIN users us ON p.creator_id=us.id " +
                    "           LEFT OUTER JOIN users uc ON p.changer_id=uc.id " +
                    "           where  p.master_id=" + myMasterId +
                    "           and coalesce(p.is_deleted,false) ="+showDeleted;

            if (!securityRepositoryJPA.userHasPermissions_OR(4L, "14")) //Если нет прав на "Просмотр по всем предприятиям"
            {
                //остается только на своё предприятие
                stringQuery = stringQuery + " and p.company_id=" + userRepositoryJPA.getMyCompanyId();//т.е. нет прав на все предприятия, а на своё есть
            }
            if (searchString != null && !searchString.isEmpty()) {
                stringQuery = stringQuery + " and (" +
                        " upper(p.name)   like upper(CONCAT('%',:sg,'%'))"+ ")";
            }
            if (companyId > 0) {
                stringQuery = stringQuery + " and p.company_id=" + companyId;
            }

            if (VALID_COLUMNS_FOR_ORDER_BY.contains(sortColumn) && VALID_COLUMNS_FOR_ASC.contains(sortAsc)) {
                stringQuery = stringQuery + " order by " + sortColumn + " " + sortAsc;
            } else {
                throw new IllegalArgumentException("Invalid query parameters");
            }

            try{

                Query query = entityManager.createNativeQuery(stringQuery)
                        .setFirstResult(offsetreal)
                        .setMaxResults(result);

                if (searchString != null && !searchString.isEmpty())
                {query.setParameter("sg", searchString);}

                List<Object[]> queryList = query.getResultList();
                List<DepartmentsJSON> returnList = new ArrayList<>();
                for (Object[] obj : queryList) {
                    DepartmentsJSON doc = new DepartmentsJSON();

                    doc.setId(Long.parseLong(                           obj[0].toString()));
                    doc.setName((String)                                obj[1]);
                    doc.setCreator((String)                             obj[2]);
                    doc.setChanger((String)                             obj[3]);
                    doc.setAddress((String)                             obj[4]);
                    doc.setAdditional((String)                          obj[5]);
                    doc.setCompany((String)                             obj[6]);
                    doc.setNum_childrens(Long.parseLong(                obj[7].toString()));
                    doc.setParent((String)                              obj[8]);
                    doc.setDate_time_created((String)                   obj[9]);
                    doc.setDate_time_changed((String)                   obj[10]);
                    returnList.add(doc);
                }
                return returnList;

            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Exception in method getDepartmentsTable. SQL query:" + stringQuery, e);
                return null;
            }
        } else return null;
    }


    @Transactional
    @SuppressWarnings("Duplicates")
    public int getDepartmentsSize(String searchString, int companyId, Set<Integer> filterOptionsIds) {
        String stringQuery;
        boolean needToSetParameter_MyDepthsIds= false;
        Long departmentOwnerId = userRepositoryJPA.getUserMasterIdByUsername(userRepository.getUserName());
        boolean showDeleted = filterOptionsIds.contains(1);// Показывать только удаленные
        stringQuery = "select " +
                "           p.id as id " +
                "           from departments p " +
                "           where  p.master_id=" + departmentOwnerId +
                "           and coalesce(p.is_deleted,false) ="+showDeleted;

        if (!securityRepositoryJPA.userHasPermissions_OR(4L, "14")) //Если нет прав на "Просмотр по всем предприятиям"
        {
            //остается только на своё предприятие
            stringQuery = stringQuery + " and p.company_id=" + userRepositoryJPA.getMyCompanyId();//т.е. нет прав на все предприятия, а на своё есть
        }

        if (searchString != null && !searchString.isEmpty()) {
            stringQuery = stringQuery + " and upper(p.name) like upper(CONCAT('%',:sg,'%'))";
        }
        if (companyId > 0) {
            stringQuery = stringQuery + " and p.company_id=" + companyId;
        }
        stringQuery = stringQuery + " and p.parent_id is null";

        Query query =  entityManager.createNativeQuery(stringQuery);

        if (searchString != null && !searchString.isEmpty())
        {query.setParameter("sg", searchString);}

        if(needToSetParameter_MyDepthsIds)//Иначе получим Unable to resolve given parameter name [myDepthsIds] to QueryParameter reference
        {query.setParameter("myDepthsIds", userRepositoryJPA.getMyDepartmentsIdWithTheirParents());}

        return query.getResultList().size();
    }

    // Возвращаем id в случае успешного создания
    // Возвращаем -1 при недостатке прав
    // Возвращаем null в случае ошибки
    @SuppressWarnings("Duplicates")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class})
    public Long insertDepartment(DepartmentForm request) {

        EntityManager emgr = emf.createEntityManager();
        Companies companyOfCreatingDoc = emgr.find(Companies.class, request.getCompany_id());//предприятие для создаваемого документа
        Long DocumentMasterId=companyOfCreatingDoc.getMaster().getId(); //владелец предприятия создаваемого документа.
        Long myMasterId=userRepositoryJPA.getMyMasterId(); //владелец предприятия создаваемого документа.
        //plan limit check
        if(!userRepositoryJPA.isPlanNoLimits(userRepositoryJPA.getMasterUserPlan(myMasterId))) // if plan with limits - checking limits
            if(userRepositoryJPA.getMyConsumedResources().getDepartments()>=userRepositoryJPA.getMyMaxAllowedResources().getDepartments())
                return -120L; // number of companies is out of bounds of tariff plan

        if (    //если есть право на создание
                (securityRepositoryJPA.userHasPermissions_OR(4L, "11")) &&
                //создается документ для предприятия моего владельца (т.е. под юрисдикцией главного аккаунта)
                DocumentMasterId.equals(myMasterId))
        {
            String stringQuery;
            Long myId = userRepository.getUserId();
            Long newDocId;


            String timestamp = new Timestamp(System.currentTimeMillis()).toString();
            stringQuery = "insert into departments (" +
                    " master_id," + //мастер-аккаунт
                    " creator_id," + //создатель
                    " company_id," + //предприятие, для которого создается документ
                    " date_time_created," + //дата и время создания
                    " boxoffice_id," + //id кассы отделения
                    " payment_account_id, " + // id банковского счета
                    " name," +
                    " address," +
                    " price_id," +
                    " additional" +//доп. информация по отделению
                    ") values ("+
                    myMasterId + ", "+//мастер-аккаунт
                    myId + ", "+ //создатель
                    request.getCompany_id() + ", "+//предприятие, для которого создается документ
                    "to_timestamp('"+timestamp+"','YYYY-MM-DD HH24:MI:SS.MS')," +//дата и время создания
                    request.getBoxoffice_id() + ", " +
                    request.getPayment_account_id() + ", " +
                    ":name," +
                    ":address," +
                    request.getPrice_id() + ", " +
                    ":additional)";
            try{
                Query query = entityManager.createNativeQuery(stringQuery);

                query.setParameter("name",request.getName());
                query.setParameter("address",request.getAddress());
                query.setParameter("additional",request.getAdditional());
                query.executeUpdate();
                stringQuery="select id from departments where date_time_created=(to_timestamp('"+timestamp+"','YYYY-MM-DD HH24:MI:SS.MS')) and creator_id="+myId;
                Query query2 = entityManager.createNativeQuery(stringQuery);
                newDocId=Long.valueOf(query2.getSingleResult().toString());

                return newDocId;

            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                logger.error("Exception in method insertDepartment on inserting into departments. SQL query:"+stringQuery, e);
                e.printStackTrace();
                return null;
            }
        } else {
            return -1L;
        }
    }


    @SuppressWarnings("Duplicates")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class})
    public Integer  updateDepartment(DepartmentForm request){
        //Если есть право на "Редактирование по всем предприятиям" и id принадлежат владельцу аккаунта (с которого апдейтят ), ИЛИ
        if(     (securityRepositoryJPA.userHasPermissions_OR(4L,"16") && securityRepositoryJPA.isItAllMyMastersDocuments("departments",request.getId().toString())) ||
                //Если есть право на "Редактирование по своему предприятияю" и  id принадлежат владельцу аккаунта (с которого апдейтят) и предприятию аккаунта, ИЛИ
                (securityRepositoryJPA.userHasPermissions_OR(4L,"15") && securityRepositoryJPA.isItAllMyMastersAndMyCompanyDocuments("departments",request.getId().toString())))
        {
            Long myId = userRepository.getUserIdByUsername(userRepository.getUserName());

            String stringQuery;
            stringQuery =   " update departments set " +
                            " address = :address," +//
                            " name = :name," +//
                            " additional = :additional, " +
                            " price_id = " + request.getPrice_id()+"," +
                            " boxoffice_id="+request.getBoxoffice_id()+"," +
                            " payment_account_id="+request.getPayment_account_id()+"," +
                            " changer_id = " + myId + ","+
                            " date_time_changed= now()" +
                            " where " +
                            " id= "+request.getId();
            try
            {
                Query query = entityManager.createNativeQuery(stringQuery);
                query.setParameter("name",request.getName());
                query.setParameter("address",request.getAddress());
                query.setParameter("additional",request.getAdditional());
                query.executeUpdate();
                return 1;
            }catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                logger.error("Exception in method updateDepartment. SQL query:"+stringQuery, e);
                e.printStackTrace();
                return null;
            }
        } else return -1; //недостаточно прав
    }

    public Departments getDepartmentById(Long id){
        EntityManager em = emf.createEntityManager();
        Departments d = em.find(Departments.class, id);
        return d;
    }

    @Transactional
    @SuppressWarnings("Duplicates")
    public List<Departments> getDeptChildrens(int parentDeptId){
        String stringQuery;
        Long departmentOwnerId = userRepositoryJPA.getUserMasterIdByUsername(userRepository.getUserName());
        stringQuery="select " +
                "           p.id as id, " +
                "           p.name as name, " +
                "           u.username as owner, " +
                "           us.username as creator, " +
                "           uc.username as changer, " +
                "           p.master_id as owner_id, " +
                "           p.creator_id as creator_id, " +
                "           p.changer_id as changer_id, " +
                "           p.company_id as company_id, " +
                "           p.parent_id as parent_id, " +
                "           p.price_id as price_id, " +
                "           p.address as address, " +
                "           p.additional as additional, " +
                "           (select name from companies where id=p.company_id) as company, " +
                "           (select count(*) from departments ds where ds.parent_id=p.id) as num_childrens," +
                "           (select name from departments where id=p.parent_id) as parent, " +
                "           p.date_time_created as date_time_created, " +
                "           p.date_time_changed as date_time_changed " +
                "           from departments p " +
                "           INNER JOIN users u ON p.master_id=u.id " +
                "           LEFT OUTER JOIN users us ON p.creator_id=us.id " +
                "           LEFT OUTER JOIN users uc ON p.changer_id=uc.id " +
                "           where  p.master_id="+departmentOwnerId;

        stringQuery = stringQuery+" and p.parent_id="+parentDeptId;
        stringQuery = stringQuery+" order by p.name asc";
        Query query =  entityManager.createNativeQuery(stringQuery, DepartmentsJSON.class);
        return query.getResultList();
    }

    public List<DepartmentsListJSON> getDepartmentsListByCompanyId(int company_id, boolean has_parent) {
        String stringQuery;

        Long companyOwnerId = userRepositoryJPA.getUserMasterIdByUsername(userRepository.getUserName());

        stringQuery="select " +
                "           p.id as id, " +
                "           p.name as name, " +
                "           p.price_id as pricetype_id " +
                "           from departments p " +
                "           where  p.master_id="+companyOwnerId;

        if(company_id>0)stringQuery = stringQuery+" and p.company_id="+company_id;

        stringQuery = stringQuery+"and coalesce(p.is_deleted,false) !=true";

        if(has_parent){
            stringQuery = stringQuery+" and p.parent_id is not null";
        }else{
            stringQuery = stringQuery+" and p.parent_id is null";
        }
        stringQuery = stringQuery+" order by p.name asc";

        Query query =  entityManager.createNativeQuery(stringQuery);
        List<Object[]> queryList = query.getResultList();
        List<DepartmentsListJSON> returnList = new ArrayList<>();
        for(Object[] obj:queryList) {
            DepartmentsListJSON doc = new DepartmentsListJSON();
            doc.setId(Long.parseLong(                                           obj[0].toString()));
            doc.setName((String)                                                obj[1]);
            doc.setPricetype_id(obj[2]!=null?Long.parseLong(                    obj[2].toString()):null);
            returnList.add(doc);
        }
        return returnList;
    }

    public List<IdAndName> getDepartmentsList(Long company_id) {
        String stringQuery="select " +
                "           p.id as id, " +
                "           p.name as name " +
                "           from departments p " +
                "           where  p.company_id = "+company_id+
                "           and coalesce(p.is_deleted, false) = false" +
                "           order by p.name asc";

        try{
        Query query =  entityManager.createNativeQuery(stringQuery);
        List<Object[]> queryList = query.getResultList();
        List<IdAndName> returnList = new ArrayList<>();
        for(Object[] obj:queryList) {
            IdAndName doc = new IdAndName();
            doc.setId(Long.parseLong(       obj[0].toString()));
            doc.setName((String)            obj[1]);
            returnList.add(doc);
        }
        return returnList;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in method getDepartmentsList. SQL query:" + stringQuery, e);
            return null;
        }
    }

    @Transactional
    @SuppressWarnings("Duplicates")
    public List<DepartmentsListJSON> getMyDepartmentsListByCompanyId(int company_id, boolean has_parent) {
        String stringQuery;

        Long companyOwnerId = userRepositoryJPA.getUserMasterIdByUsername(userRepository.getUserName());
        List<Integer>param=userRepositoryJPA.getMyDepartmentsId();
        String ids="";
        for (int i:param){ids=ids+i+",";}ids=ids+"0";//Костыли, т.к. хз почему не отрабатывает query.setParameter("param"...)

        stringQuery="select " +
                "           p.id as id, " +
                "           p.name as name, " +
                "           p.price_id as pricetype_id " +
                "           from departments p " +
                "           where  p.master_id="+companyOwnerId+
                "           and p.id in ("+ids+")";

        if(company_id>0)stringQuery = stringQuery+" and p.company_id="+company_id;
        stringQuery = stringQuery+"and coalesce(p.is_deleted,false) !=true";
        if(has_parent){
            stringQuery = stringQuery+" and p.parent_id is not null";
        }else{
            stringQuery = stringQuery+" and p.parent_id is null";
        }
        stringQuery = stringQuery+" order by p.name asc";
        Query query =  entityManager.createNativeQuery(stringQuery);
        List<Object[]> queryList = query.getResultList();
        List<DepartmentsListJSON> returnList = new ArrayList<>();
        for(Object[] obj:queryList) {
            DepartmentsListJSON doc = new DepartmentsListJSON();
            doc.setId(Long.parseLong(                                           obj[0].toString()));
            doc.setName((String)                                                obj[1]);
            doc.setPricetype_id(obj[2]!=null?Long.parseLong(                    obj[2].toString()):null);
            returnList.add(doc);
        }
        return returnList;
    }
    @Transactional
    @SuppressWarnings("Duplicates")
    public List<Departments> getMyDepartmentsList() {
        String stringQuery;

        Long companyOwnerId = userRepositoryJPA.getUserMasterIdByUsername(userRepository.getUserName());
        List<Integer>param=userRepositoryJPA.getMyDepartmentsId();
        String ids="";
        for (int i:param){ids=ids+i+",";}ids=ids+"0";//Костыли, т.к. хз почему не отрабатывает query.setParameter("param"...)

        stringQuery="select " +
                "           p.id as id, " +
                "           p.name as name " +
                "           from departments p " +
                "           where  p.master_id="+companyOwnerId+
                "           and p.id in ("+ids+")";

        stringQuery = stringQuery+" order by p.name asc";
        Query query =  entityManager.createNativeQuery(stringQuery, SpravSysDepartmentsList.class);
        return query.getResultList();
    }

    @Transactional
    @SuppressWarnings("Duplicates")
    public DepartmentsJSON getDepartmentValuesById(Long id) {
        if (securityRepositoryJPA.userHasPermissions_OR(4L, "13,14") &&// Отделения: "Просмотр своего" "Просмотр всех"
                securityRepositoryJPA.isItMyMastersDepartment(id))//принадлежит к отделениям моего родителя
        {
            String stringQuery;
            UserSettingsJSON userSettings = userRepositoryJPA.getMySettings();
            String myTimeZone = userSettings.getTime_zone();
            String dateFormat = userSettings.getDateFormat();
            String timeFormat = (userSettings.getTimeFormat().equals("12")?" HH12:MI AM":" HH24:MI"); // '12' or '24'
            Long myCompanyId = userRepositoryJPA.getMyCompanyId_();
            stringQuery = "select p.id as id, " +
                    "           us.name as creator, " +
                    "           uc.name as changer, " +
                    "           p.creator_id as creator_id, " +
                    "           p.changer_id as changer_id, " +
                    "           p.company_id as company_id, " +
                    "           cmp.name as company, " +
                    "           p.name as name, " +
                    "           p.price_id as price_id, " +
                    "           to_char(p.date_time_created at time zone '" + myTimeZone + "', '"+dateFormat+timeFormat+"') as date_time_created, " +
                    "           to_char(p.date_time_changed at time zone '" + myTimeZone + "', '"+dateFormat+timeFormat+"') as date_time_changed, " +
                    "           p.address as address, " +
                    "           p.additional as additional, " +
                    "           coalesce(p.parent_id,'0') as parent_id, " +
                    "           coalesce((select name from departments where id=coalesce(p.parent_id,'0')),'') as parent, " +
                    "           (select count(*) from departments ds where ds.parent_id=p.id) as num_childrens," +
                    "           p.boxoffice_id as boxoffice_id, " +
                    "           p.payment_account_id as payment_account_id " +

                    "           from departments p" +
                    "           INNER JOIN companies cmp ON p.company_id=cmp.id " +
                    "           LEFT OUTER JOIN users us ON p.creator_id=us.id " +
                    "           LEFT OUTER JOIN users uc ON p.changer_id=uc.id " +
                    "           where p.id= " + id;

            if (!securityRepositoryJPA.userHasPermissions_OR(4L, "14")) //Если нет прав на просм по всем предприятиям
            {//остается на: своё предприятие
                stringQuery = stringQuery + " and p.company_id=" + myCompanyId;//т.е. нет прав на все предприятия, а на своё есть
            }

            try {
                Query query = entityManager.createNativeQuery(stringQuery);

                List<Object[]> queryList = query.getResultList();

                DepartmentsJSON returnObj = new DepartmentsJSON();

                for (Object[] obj : queryList) {
                    returnObj.setId(Long.parseLong(                             obj[0].toString()));
                    returnObj.setCreator((String)                               obj[1]);
                    returnObj.setChanger((String)                               obj[2]);
                    returnObj.setCreator_id(Long.parseLong(                     obj[3].toString()));
                    returnObj.setChanger_id(obj[4] != null ? Long.parseLong(    obj[4].toString()) : null);
                    returnObj.setCompany_id(Long.parseLong(                     obj[5].toString()));
                    returnObj.setCompany((String)                               obj[6]);
                    returnObj.setName((String)                                  obj[7]);
                    returnObj.setPrice_id(obj[8] != null ? Long.parseLong(      obj[8].toString()) : null);
                    returnObj.setDate_time_created((String)                     obj[9]);
                    returnObj.setDate_time_changed((String)                     obj[10]);
                    returnObj.setAddress((String)                               obj[11]);
                    returnObj.setAdditional((String)                            obj[12]);
                    returnObj.setParent_id(obj[13] != null ? Long.parseLong(    obj[13].toString()) : null);
                    returnObj.setParent((String)                                obj[14]);
                    returnObj.setNum_childrens(Long.parseLong(                  obj[15].toString()));
                    returnObj.setBoxoffice_id(obj[16] != null ? Long.parseLong( obj[16].toString()) : null);
                    returnObj.setPayment_account_id(obj[17]!=null?Long.parseLong(obj[17].toString()) : null);
                }
                return returnObj;
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Exception in method getDepartmentValuesById. SQL query:" + stringQuery, e);
                return null;
            }
        } else return null;
    }


    public Set<Departments> getDepartmentsSetBySetOfDepartmentsId(Set<Long> departments) {
        EntityManager em = emf.createEntityManager();
        Departments dep;
        Set<Departments> departmentsSet = new HashSet<>();
        for (Long i : departments) {
            dep = em.find(Departments.class, i);
            departmentsSet.add(dep);
        }
        return departmentsSet;
    }

    @Transactional
    @SuppressWarnings("Duplicates")
    public Integer deleteDepartments(String delNumbers) {
        //Если есть право на "Удаление по всем предприятиям" и все id для удаления принадлежат владельцу аккаунта (с которого удаляют), ИЛИ
        if ((securityRepositoryJPA.userHasPermissions_OR(4L, "12") && securityRepositoryJPA.isItAllMyMastersDocuments("departments", delNumbers)) ||
                //Если есть право на "Удаление по своему предприятияю" и все id для удаления принадлежат владельцу аккаунта (с которого удаляют) и предприятию аккаунта
                (securityRepositoryJPA.userHasPermissions_OR(4L, "12") && securityRepositoryJPA.isItAllMyMastersAndMyCompanyDocuments("departments", delNumbers)))
        {
            Long myMasterId = userRepositoryJPA.getUserMasterIdByUsername(userRepository.getUserName());
            Long myId = userRepositoryJPA.getMyId();
            String stringQuery = "update departments p" +
                    " set changer_id="+ myId + ", " + // кто изменил (удалил)
                    " date_time_changed = now(), " +//дату и время изменения
                    " is_deleted=true " +
                    " where p.master_id=" + myMasterId +
                    " and p.id in ("+delNumbers.replaceAll("[^0-9\\,]", "")+")";
            try{
                Query query = entityManager.createNativeQuery(stringQuery);
                query.executeUpdate();
                return 1;
            } catch (Exception e) {
                logger.error("Exception in method deleteDepartments. SQL query:"+stringQuery, e);
                e.printStackTrace();
                return null;
            }
        } else return -1;
    }

    @Transactional
    @SuppressWarnings("Duplicates")
    public Integer undeleteDepartments(String delNumbers) {
        //Если есть право на "Удаление по всем предприятиям" и все id для удаления принадлежат владельцу аккаунта (с которого удаляют), ИЛИ
        if(     (securityRepositoryJPA.userHasPermissions_OR(4L,"12") && securityRepositoryJPA.isItAllMyMastersDocuments("departments",delNumbers)) ||
        //Если есть право на "Удаление по своему предприятияю" и все id для удаления принадлежат владельцу аккаунта (с которого удаляют) и предприятию аккаунта
        (securityRepositoryJPA.userHasPermissions_OR(4L,"12") && securityRepositoryJPA.isItAllMyMastersAndMyCompanyDocuments("departments",delNumbers)))
        {
            //plan limit check
            Long masterId =  userRepositoryJPA.getMyMasterId();
            long amountToRepair = delNumbers.split(",").length;
            if(!userRepositoryJPA.isPlanNoLimits(userRepositoryJPA.getMasterUserPlan(masterId))) // if plan with limits - checking limits
                if((userRepositoryJPA.getMyConsumedResources().getDepartments()+amountToRepair)>userRepositoryJPA.getMyMaxAllowedResources().getDepartments())
                    return -120; // number of users is out of bounds of tariff plan
            // на MasterId не проверяю , т.к. выше уже проверено
            Long myId = userRepositoryJPA.getMyId();
            String stringQuery;
            stringQuery = "Update departments p" +
                    " set changer_id="+ myId + ", " + // кто изменил (восстановил)
                    " date_time_changed = now(), " +//дату и время изменения
                    " is_deleted=false " + //не удалена
                    " where p.id in ("+delNumbers.replaceAll("[^0-9\\,]", "")+")";
            try{
                Query query = entityManager.createNativeQuery(stringQuery);
                if (!stringQuery.isEmpty() && stringQuery.trim().length() > 0) {
                    query.executeUpdate();
                    return 1;
                } else return null;
            }catch (Exception e) {
                logger.error("Exception in method undeleteDepartments. SQL query:"+stringQuery, e);
                e.printStackTrace();
                return null;
            }
        } else return -1;
    }

    //true если id отделения принадлежит предприятию.
    public Boolean departmentBelongToCompany(Long compId,Long depId){
        String stringQuery = "select p.id from departments p where p.id="+depId+" and p.company_id=" + compId ;
        Query query = entityManager.createNativeQuery(stringQuery);
        return (query.getResultList().size() > 0);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class})
    public Long insertDepartmentFast(DepartmentForm request,Long companyId, Long myMasterId) {
//        EntityManager emgr = emf.createEntityManager();
        String stringQuery;
        Long newDocId;
        String timestamp = new Timestamp(System.currentTimeMillis()).toString();
        stringQuery = "insert into departments (" +
                " master_id," + //мастер-аккаунт
                " creator_id," + //создатель
                " company_id," + //предприятие, для которого создается документ
                " date_time_created," + //дата и время создания
                " price_id, " +
                " boxoffice_id, " +
                " payment_account_id, " +
                " name," +
                " address" +
                ") values ("+
                myMasterId + ", "+//мастер-аккаунт
                myMasterId + ", "+ //создатель
                companyId + ", "+//предприятие, для которого создается документ
                "to_timestamp('"+timestamp+"','YYYY-MM-DD HH24:MI:SS.MS')," +//дата и время создания
                request.getPrice_id()+ ", "+ // тип цены
                request.getBoxoffice_id()+ ", "+ // касса предприятия
                request.getPayment_account_id() + ", "+ //
                ":name," +
                "''"+
                ")";
        try{
            Query query = entityManager.createNativeQuery(stringQuery);
            query.setParameter("name",request.getName());
            query.executeUpdate();
            stringQuery="select id from departments where date_time_created=(to_timestamp('"+timestamp+"','YYYY-MM-DD HH24:MI:SS.MS')) and creator_id="+myMasterId;
            Query query2 = entityManager.createNativeQuery(stringQuery);
            newDocId=Long.valueOf(query2.getSingleResult().toString());
            return newDocId;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("Exception in method insertDepartmentFast. SQL query:"+stringQuery, e);
            e.printStackTrace();
            return null;
        }
    }

}
