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
package com.dokio.message.response.additional;

public class ShiftsJSON {

    private Long id;
    private Long master_id;

    private Long creator_id;
    private Long closer_id;
    private Long company_id;
    private Long department_id;
    private Long kassa_id;  // id KKM
    private Long acquiring_bank_id; // банк эквайер
    private String date_time_created;
    private String date_time_closed;
    private String shift_status_id;

    private String master;
    private String creator;
    private String closer;
    private String company;
    private String department;
    private String kassa;
    private String acquiring_bank;
    private Integer shift_number;
    private String zn_kkt;
    private String shift_expired_at;
    private String fn_serial;
    private String uid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMaster_id() {
        return master_id;
    }

    public void setMaster_id(Long master_id) {
        this.master_id = master_id;
    }

    public Long getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(Long creator_id) {
        this.creator_id = creator_id;
    }

    public Long getCloser_id() {
        return closer_id;
    }

    public void setCloser_id(Long closer_id) {
        this.closer_id = closer_id;
    }

    public Long getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Long company_id) {
        this.company_id = company_id;
    }

    public Long getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(Long department_id) {
        this.department_id = department_id;
    }

    public Long getKassa_id() {
        return kassa_id;
    }

    public void setKassa_id(Long kassa_id) {
        this.kassa_id = kassa_id;
    }

    public String getKassa() {
        return kassa;
    }

    public void setKassa(String kassa) {
        this.kassa = kassa;
    }

    public Long getAcquiring_bank_id() {
        return acquiring_bank_id;
    }

    public void setAcquiring_bank_id(Long acquiring_bank_id) {
        this.acquiring_bank_id = acquiring_bank_id;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCloser() {
        return closer;
    }

    public void setCloser(String closer) {
        this.closer = closer;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAcquiring_bank() {
        return acquiring_bank;
    }

    public void setAcquiring_bank(String acquiring_bank) {
        this.acquiring_bank = acquiring_bank;
    }

    public String getDate_time_created() {
        return date_time_created;
    }

    public void setDate_time_created(String date_time_created) {
        this.date_time_created = date_time_created;
    }

    public String getDate_time_closed() {
        return date_time_closed;
    }

    public void setDate_time_closed(String date_time_closed) {
        this.date_time_closed = date_time_closed;
    }

    public Integer getShift_number() {
        return shift_number;
    }

    public void setShift_number(Integer shift_number) {
        this.shift_number = shift_number;
    }

    public String getZn_kkt() {
        return zn_kkt;
    }

    public void setZn_kkt(String zn_kkt) {
        this.zn_kkt = zn_kkt;
    }

    public String getShift_status_id() {
        return shift_status_id;
    }

    public void setShift_status_id(String shift_status_id) {
        this.shift_status_id = shift_status_id;
    }

    public String getShift_expired_at() {
        return shift_expired_at;
    }

    public void setShift_expired_at(String shift_expired_at) {
        this.shift_expired_at = shift_expired_at;
    }

    public String getFn_serial() {
        return fn_serial;
    }

    public void setFn_serial(String fn_serial) {
        this.fn_serial = fn_serial;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}