package com.panaceasoft.restaurateur.models;

/**
 * Created by Panacea-Soft on 20/7/17.
 * Contact Email : teamps.is.cool@gmail.com
 * Website : http://www.panacea-soft.com
 */

public class AttributeData {

    public int id;
    public int header_id;
    public int detail_id;
    public int item_id;
    public int shop_id;
    public String detail_name;
    public String attribute_price;

    public AttributeData(int id, int header_id, int detail_id, int item_id, int shop_id, String detail_name, String attribute_price) {
        this.id = id;
        this.header_id = header_id;
        this.detail_id = detail_id;
        this.item_id = item_id;
        this.shop_id = shop_id;
        this.detail_name = detail_name;
        this.attribute_price = attribute_price;
    }

    public AttributeData(int header_id, int detail_id, int item_id, int shop_id, String detail_name, String attribute_price) {
        this.header_id = header_id;
        this.detail_id = detail_id;
        this.item_id = item_id;
        this.shop_id = shop_id;
        this.detail_name = detail_name;
        this.attribute_price = attribute_price;
    }

    public AttributeData() {

    }

    public void setAttribute_price(String attribute_price) {
        this.attribute_price = attribute_price;
    }

    public String getAttribute_price() {

        return attribute_price;
    }

    public int getId() {
        return id;
    }

    public int getHeader_id() {
        return header_id;
    }

    public int getDetail_id() {
        return detail_id;
    }

    public String getDetail_name() {
        return detail_name;
    }

    public int getItem_id() {
        return item_id;
    }

    public int getShop_id() {
        return shop_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHeader_id(int header_id) {
        this.header_id = header_id;
    }

    public void setDetail_id(int detail_id) {
        this.detail_id = detail_id;
    }

    public void setDetail_name(String detail_name) {
        this.detail_name = detail_name;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

}
