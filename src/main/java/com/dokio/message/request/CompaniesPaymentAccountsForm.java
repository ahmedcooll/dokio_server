package com.dokio.message.request;

public class CompaniesPaymentAccountsForm {

    private Long id;
    private Long master_id;
    private Long company_id;
    private Integer output_order;
    private String bik;
    private String name;
    private String address;
    private String payment_account;
    private String corr_account;
    private String description;
    private String intermediatery;
    private String swift;
    private String iban;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIntermediatery() {
        return intermediatery;
    }

    public void setIntermediatery(String intermediatery) {
        this.intermediatery = intermediatery;
    }

    public String getSwift() {
        return swift;
    }

    public void setSwift(String swift) {
        this.swift = swift;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

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

    public Long getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Long company_id) {
        this.company_id = company_id;
    }

    public Integer getOutput_order() {
        return output_order;
    }

    public void setOutput_order(Integer output_order) {
        this.output_order = output_order;
    }

    public String getBik() {
        return bik;
    }

    public void setBik(String bik) {
        this.bik = bik;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPayment_account() {
        return payment_account;
    }

    public void setPayment_account(String payment_account) {
        this.payment_account = payment_account;
    }

    public String getCorr_account() {
        return corr_account;
    }

    public void setCorr_account(String corr_account) {
        this.corr_account = corr_account;
    }
}
