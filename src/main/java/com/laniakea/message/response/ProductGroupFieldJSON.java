package com.laniakea.message.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class ProductGroupFieldJSON {

        @Id
        private Long id;
        private String company;
        private String company_id;
        private String master;
        private String master_id;
        private String creator;
        private String creator_id;
        private String changer;
        private String changer_id;
        @JsonSerialize(using = com.laniakea.util.JSONSerializer.class)
        @JsonDeserialize(using = com.laniakea.util.JSONDeserialize.class)
        private Timestamp date_time_created;
        @JsonSerialize(using = com.laniakea.util.JSONSerializer.class)
        @JsonDeserialize(using = com.laniakea.util.JSONDeserialize.class)
        private Timestamp date_time_changed;

        private String name;
        private String description;
        private String group_id;
        private String field_type;
        private String parent_set_id;
        private String output_order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getField_type() {
        return field_type;
    }

    public void setField_type(String field_type) {
        this.field_type = field_type;
    }

    public String getParent_set_id() {
        return parent_set_id;
    }

    public void setParent_set_id(String parent_set_id) {
        this.parent_set_id = parent_set_id;
    }

    public String getOutput_order() {
        return output_order;
    }

    public void setOutput_order(String output_order) {
        this.output_order = output_order;
    }
}
