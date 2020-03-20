package com.laniakea.message.response;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class UsersJSON {
    @Id
    private Long id;
    private String name;
    private String fio_family;
    private String fio_name;
    private String fio_otchestvo;
    private String username;
    private String email;
    private String company;
    private String company_id;
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> userDepartmentsNames;
    @ElementCollection(fetch = FetchType.LAZY)
    private List<Integer> userDepartmentsId;
    @ElementCollection(fetch = FetchType.LAZY)
    private List<Integer> userGroupsId;
    private String master;
    private String master_id;
    private String creator;
    private String creator_id;
    private String changer;
    private String changer_id;
    private String sex;
    private String status_account;
    private String date_birthday;
    private String additional;
    @JsonSerialize(using = com.laniakea.util.JSONSerializer.class)
    @JsonDeserialize(using = com.laniakea.util.JSONDeserialize.class)
    private Timestamp date_time_created;
    @JsonSerialize(using = com.laniakea.util.JSONSerializer.class)
    @JsonDeserialize(using = com.laniakea.util.JSONDeserialize.class)
    private Timestamp date_time_changed;
    private Long time_zone_id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Long getTime_zone_id() {
        return time_zone_id;
    }

    public void setTime_zone_id(Long time_zone_id) {
        this.time_zone_id = time_zone_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus_account() {
        return status_account;
    }

    public List<Integer> getUserGroupsId() {
        return userGroupsId;
    }

    public void setUserGroupsId(List<Integer> userGroupsId) {
        this.userGroupsId = userGroupsId;
    }

    public void setStatus_account(String status_account) {
        this.status_account = status_account;
    }

    public String getFio_family() {
        return fio_family;
    }

    public void setFio_family(String fio_family) {
        this.fio_family = fio_family;
    }

    public String getFio_name() {
        return fio_name;
    }

    public void setFio_name(String fio_name) {
        this.fio_name = fio_name;
    }

    public String getFio_otchestvo() {
        return fio_otchestvo;
    }

    public void setFio_otchestvo(String fio_otchestvo) {
        this.fio_otchestvo = fio_otchestvo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public List<String> getUserDepartmentsNames() {
        return userDepartmentsNames;
    }

    public void setUserDepartmentsNames(List<String> userDepartmentsNames) {
        this.userDepartmentsNames = userDepartmentsNames;
    }

    public List<Integer> getUserDepartmentsId() {
        return userDepartmentsId;
    }

    public void setUserDepartmentsId(List<Integer> userDepartmentsId) {
        this.userDepartmentsId = userDepartmentsId;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getMaster_id() {
        return master_id;
    }

    public void setMaster_id(String master_id) {
        this.master_id = master_id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public String getChanger() {
        return changer;
    }

    public void setChanger(String changer) {
        this.changer = changer;
    }

    public String getChanger_id() {
        return changer_id;
    }

    public void setChanger_id(String changer_id) {
        this.changer_id = changer_id;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDate_birthday() {
        return date_birthday;
    }

    public void setDate_birthday(String date_birthday) {
        this.date_birthday = date_birthday;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }

    public Timestamp getDate_time_created() {
        return date_time_created;
    }

    public void setDate_time_created(Timestamp date_time_created) {
        this.date_time_created = date_time_created;
    }

    public Timestamp getDate_time_changed() {
        return date_time_changed;
    }

    public void setDate_time_changed(Timestamp date_time_changed) {
        this.date_time_changed = date_time_changed;
    }
}
