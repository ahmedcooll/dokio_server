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
        package com.dokio.message.response;

        import java.math.BigDecimal;

public class InvoiceinProductTableJSON {

    private Long id;                            // id строки в таблице - необходимо для идентификации ряда row_id в фронтэнде
    private String name;                        // наименование товара
    private Long product_id;                    // id товара
    private BigDecimal product_count;           // кол-во товара
    private BigDecimal estimated_balance;       // кол-во товара по БД (на момент формирования документа Инвентаризаиця)
    private BigDecimal actual_balance;          // кол-во товара фактическое (по ручному пересчёту товара в магазине)
    private String edizm;                       // наименование единицы измерения товара
    private Long nds_id;                        // id ндс
    private BigDecimal product_price;           // цена товара (может быть разная - закупочная, себестоимость, одна из типов цен)
    private String additional;                  // доп. инфо для поставщика
    private Boolean indivisible;                // неделимый товар (нельзя что-то сделать с, например, 0.5 единицами этого товара, только с кратно 1)
    private BigDecimal total;                   // всего на складе
    private BigDecimal reserved;                // в резервах
    private Boolean is_material;                // материален ли товар

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
    }

    public BigDecimal getProduct_count() {
        return product_count;
    }

    public void setProduct_count(BigDecimal product_count) {
        this.product_count = product_count;
    }

    public BigDecimal getEstimated_balance() {
        return estimated_balance;
    }

    public void setEstimated_balance(BigDecimal estimated_balance) {
        this.estimated_balance = estimated_balance;
    }

    public BigDecimal getActual_balance() {
        return actual_balance;
    }

    public void setActual_balance(BigDecimal actual_balance) {
        this.actual_balance = actual_balance;
    }

    public String getEdizm() {
        return edizm;
    }

    public void setEdizm(String edizm) {
        this.edizm = edizm;
    }

    public Long getNds_id() {
        return nds_id;
    }

    public void setNds_id(Long nds_id) {
        this.nds_id = nds_id;
    }

    public BigDecimal getProduct_price() {
        return product_price;
    }

    public void setProduct_price(BigDecimal product_price) {
        this.product_price = product_price;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }

    public Boolean getIndivisible() {
        return indivisible;
    }

    public void setIndivisible(Boolean indivisible) {
        this.indivisible = indivisible;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getReserved() {
        return reserved;
    }

    public void setReserved(BigDecimal reserved) {
        this.reserved = reserved;
    }

    public Boolean getIs_material() {
        return is_material;
    }

    public void setIs_material(Boolean is_material) {
        this.is_material = is_material;
    }
}