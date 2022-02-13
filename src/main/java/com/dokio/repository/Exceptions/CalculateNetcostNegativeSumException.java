/*
Copyright © 2020 Сунцов Михаил Александрович. mihail.suntsov@yandex.ru
Эта программа является свободным программным обеспечением: Вы можете распространять ее и (или) изменять,
соблюдая условия Генеральной публичной лицензии GNU Affero GPL редакции 3 (GNU AGPLv3),
опубликованной Фондом свободного программного обеспечения;
Эта программа распространяется в расчёте на то, что она окажется полезной, но
БЕЗ КАКИХ-ЛИБО ГАРАНТИЙ, включая подразумеваемую гарантию КАЧЕСТВА либо
ПРИГОДНОСТИ ДЛЯ ОПРЕДЕЛЕННЫХ ЦЕЛЕЙ. Ознакомьтесь с Генеральной публичной
лицензией GNU для получения более подробной информации.
Вы должны были получить копию Генеральной публичной лицензии GNU вместе с этой
программой. Если Вы ее не получили, то перейдите по адресу: http://www.gnu.org/licenses
*/
package com.dokio.repository.Exceptions;

// при расчете средней себестоимости кол-во товара в какой то момент истории его изменения стало отрицательным
// это может случиться, если например отменить проведение приёмки, после которой идет продажа товара
// тогда история изменения товара будет такой
//  0
//  +5
//  +10  Эту приемку отменили
//  -10  В результате этой продажи в данном месте истории кол-во товара стало отрицательным (5 - 10 = -5) и кинется этот эксепшен
//  +3
public class CalculateNetcostNegativeSumException extends Exception {
    @Override
    public void printStackTrace() {
        System.err.println("Can't do operation of calculating netcost because of an negative sum");
    }
}
