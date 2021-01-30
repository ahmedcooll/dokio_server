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
package com.dokio.controller.Sprav;
import com.dokio.message.response.Sprav.SpravSysPaymentMethodsJSON;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Controller
@Repository
public class SpravSysPaymentMethodsController {
    Logger logger = Logger.getLogger("SpravSysPaymentMethodsController");

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("Duplicates")
    @RequestMapping(
            value = "/api/auth/getSpravSysPaymentMethods",
            method = RequestMethod.GET, produces = "application/json;charset=utf8")
    public ResponseEntity<?> getSpravSysPaymentMethods()
    {
        logger.info("Processing get request for path /api/auth/getSpravSysPaymentMethods");

        String stringQuery=
                "select p.id as id, " +
                        "p.name as name, " +
                        "p.id_api_atol as id_api_atol," +
                        "p.name_api_atol as name_api_atol" +
                        " from sprav_sys_payment_methods";

        Query query =  entityManager.createNativeQuery(stringQuery);

        List<Object[]> queryList = query.getResultList();
        List<SpravSysPaymentMethodsJSON> returnList = new ArrayList<>();
        for(Object[] obj:queryList) {
            SpravSysPaymentMethodsJSON doc=new SpravSysPaymentMethodsJSON();
            doc.setId(Long.parseLong(       obj[0].toString()));
            doc.setName((String)            obj[1]);
            doc.setId_api_atol((Integer)    obj[2]);
            doc.setName_api_atol((String)   obj[3]);
            returnList.add(doc);
        }
        return new ResponseEntity<List>(returnList, HttpStatus.OK);
    }
}