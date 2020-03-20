package com.laniakea.message.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class ProductsForm {
    private Long id;
    private String name;
    private String description;
    private String article;
    private Long company_id;
    private Long productgroup_id;
    private Set<Long> selectedProductCategories;
    private List<Long> imagesIdsInOrderOfList;// List id файлов-картинок для упорядочивания по месту в списке картинок товара (вобщем, для сохранения порядка картинок)
    private List<Long> cagentsIdsInOrderOfList;//List id контрагентов для упорядочивания по месту в списке поставщиков товара

    private Integer product_code;
    private Long ppr_id;
    private boolean by_weight;
    private Long edizm_id;
    private Long nds_id;
    private String weight;
    private String volume;
    private Long weight_edizm_id;
    private Long volume_edizm_id;
    private boolean  markable;
    private Long markable_group_id;
    private boolean excizable;
    private Long product_code_free;
    private boolean not_buy;

    public Long getId() {
        return id;
    }

    public Long getProduct_code_free() {
        return product_code_free;
    }

    public boolean isNot_buy() {
        return not_buy;
    }

    public void setNot_buy(boolean not_buy) {
        this.not_buy = not_buy;
    }

    public void setProduct_code_free(Long product_code_free) {
        this.product_code_free = product_code_free;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public Long getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Long company_id) {
        this.company_id = company_id;
    }

    public Long getProductgroup_id() {
        return productgroup_id;
    }

    public void setProductgroup_id(Long productgroup_id) {
        this.productgroup_id = productgroup_id;
    }

    public Set<Long> getSelectedProductCategories() {
        return selectedProductCategories;
    }

    public void setSelectedProductCategories(Set<Long> selectedProductCategories) {
        this.selectedProductCategories = selectedProductCategories;
    }

    public List<Long> getImagesIdsInOrderOfList() {
        return imagesIdsInOrderOfList;
    }

    public void setImagesIdsInOrderOfList(List<Long> imagesIdsInOrderOfList) {
        this.imagesIdsInOrderOfList = imagesIdsInOrderOfList;
    }

    public List<Long> getCagentsIdsInOrderOfList() {
        return cagentsIdsInOrderOfList;
    }

    public void setCagentsIdsInOrderOfList(List<Long> cagentsIdsInOrderOfList) {
        this.cagentsIdsInOrderOfList = cagentsIdsInOrderOfList;
    }

    public Integer getProduct_code() {
        return product_code;
    }

    public void setProduct_code(Integer product_code) {
        this.product_code = product_code;
    }

    public Long getPpr_id() {
        return ppr_id;
    }

    public void setPpr_id(Long ppr_id) {
        this.ppr_id = ppr_id;
    }

    public boolean isBy_weight() {
        return by_weight;
    }

    public void setBy_weight(boolean by_weight) {
        this.by_weight = by_weight;
    }

    public Long getEdizm_id() {
        return edizm_id;
    }

    public void setEdizm_id(Long edizm_id) {
        this.edizm_id = edizm_id;
    }

    public Long getNds_id() {
        return nds_id;
    }

    public void setNds_id(Long nds_id) {
        this.nds_id = nds_id;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public Long getWeight_edizm_id() {
        return weight_edizm_id;
    }

    public void setWeight_edizm_id(Long weight_edizm_id) {
        this.weight_edizm_id = weight_edizm_id;
    }

    public Long getVolume_edizm_id() {
        return volume_edizm_id;
    }

    public void setVolume_edizm_id(Long volume_edizm_id) {
        this.volume_edizm_id = volume_edizm_id;
    }

    public boolean isMarkable() {
        return markable;
    }

    public void setMarkable(boolean markable) {
        this.markable = markable;
    }

    public Long getMarkable_group_id() {
        return markable_group_id;
    }

    public void setMarkable_group_id(Long markable_group_id) {
        this.markable_group_id = markable_group_id;
    }

    public boolean isExcizable() {
        return excizable;
    }

    public void setExcizable(boolean excizable) {
        this.excizable = excizable;
    }
}
