package com.panaceasoft.restaurateur.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.panaceasoft.restaurateur.models.AttributeData;
import com.panaceasoft.restaurateur.models.BasketData;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Panacea-Soft on 28/6/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "moketsdb";

    // Contacts table name
    private static final String TABLE_BASKET = "BasketData";

    // Basket Table Columns names
    private static final String KEY_ID            = "id";
    private static final String KEY_ITEM_ID       = "item_id";
    private static final String KEY_SHOP_ID       = "shop_id";
    private static final String KEY_USER_ID       = "user_id";
    private static final String KEY_NAME          = "name";
    private static final String KEY_DESC          = "desc";
    private static final String KEY_UNTI_PRICE    = "unit_price";
    private static final String KEY_DISCOUNT_PERCENT    = "discount_percent";
    private static final String KEY_QTY           = "qty";
    private static final String KEY_IMAGE_PATH    = "image_path";
    private static final String KEY_CURRENCY_SYMBOL    = "currency_symbol";
    private static final String KEY_CURRENCY_SHORT_FORM   = "currency_short_form";
    private static final String KEY_SELECTED_ATTRIBUTE_NAMES   = "selected_attribute_names";
    private static final String KEY_SELECTED_ATTRIBUTE_IDS   = "selected_attribute_ids";

    //Temp Attribute Table Name
    private static final String TABLE_TEMP_ATTRIBUTE = "TempAttributeData";
    private static final String KEY_ATT_HEADER_ID = "header_id";
    private static final String KEY_ATT_DETAIL_ID = "detail_id";
    private static final String KEY_ATT_ITEM_ID = "item_id";
    private static final String KEY_ATT_SHOP_ID = "shop_id";
    private static final String KEY_ATT_DETAIL_NAME = "detail_name";
    private static final String KEY_ATT_PRICE = "attribute_price";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_BASKET_TABLE = "CREATE TABLE " + TABLE_BASKET + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_ITEM_ID + " INTEGER,"
                + KEY_SHOP_ID + " INTEGER,"
                + KEY_USER_ID + " INTEGER,"
                + KEY_NAME + " TEXT,"
                + KEY_DESC + " TEXT,"
                + KEY_UNTI_PRICE + " TEXT,"
                + KEY_DISCOUNT_PERCENT + " TEXT,"
                + KEY_QTY + " INTEGER,"
                + KEY_IMAGE_PATH + " TEXT,"
                + KEY_CURRENCY_SYMBOL + " TEXT,"
                + KEY_CURRENCY_SHORT_FORM + " TEXT,"
                + KEY_SELECTED_ATTRIBUTE_NAMES + " TEXT,"
                + KEY_SELECTED_ATTRIBUTE_IDS + " TEXT" + ")";

        db.execSQL(CREATE_BASKET_TABLE);

        String CREATE_TEMP_ATTRIBUTE_TABLE = "CREATE TABLE " + TABLE_TEMP_ATTRIBUTE + "("
                + KEY_ATT_HEADER_ID + " INTEGER,"
                + KEY_ATT_DETAIL_ID + " INTEGER,"
                + KEY_ATT_ITEM_ID + " INTEGER,"
                + KEY_ATT_SHOP_ID + " INTEGER,"
                + KEY_ATT_DETAIL_NAME + " TEXT,"
                + KEY_ATT_PRICE + " TEXT" + ")";

        db.execSQL(CREATE_TEMP_ATTRIBUTE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BASKET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMP_ATTRIBUTE);
        // Creating tables again
        onCreate(db);
    }

    // Adding new basket item
    public void addBasket(BasketData basketData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_ID, basketData.getItemId());
        values.put(KEY_SHOP_ID, basketData.getShopId());
        values.put(KEY_USER_ID, basketData.getUserId());
        values.put(KEY_NAME, basketData.getName());
        values.put(KEY_DESC, basketData.getDesc());
        values.put(KEY_UNTI_PRICE, basketData.getUnitPrice());
        values.put(KEY_DISCOUNT_PERCENT, basketData.getDiscountPercent());
        values.put(KEY_QTY, basketData.getQty());
        values.put(KEY_IMAGE_PATH, basketData.getImagePath());
        values.put(KEY_CURRENCY_SYMBOL, basketData.getCurrencySymbol());
        values.put(KEY_CURRENCY_SHORT_FORM, basketData.getCurrencyShortForm());
        values.put(KEY_SELECTED_ATTRIBUTE_NAMES, basketData.getSelectedAttributeNames());
        values.put(KEY_SELECTED_ATTRIBUTE_IDS, basketData.getSelectedAttributeIds());


        // Inserting Row
        db.insert(TABLE_BASKET, null, values);
        db.close(); // Closing database connection

    }

    //Add Attribute Table For header & detail id
    public void addAttribute(AttributeData attributeData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ATT_HEADER_ID, attributeData.getHeader_id());
        values.put(KEY_ATT_DETAIL_ID, attributeData.getDetail_id());
        values.put(KEY_ATT_ITEM_ID, attributeData.getItem_id());
        values.put(KEY_ATT_SHOP_ID, attributeData.getShop_id());
        values.put(KEY_ATT_DETAIL_NAME, attributeData.getDetail_name());
        values.put(KEY_ATT_PRICE, attributeData.getAttribute_price());

        // Inserting Row
        db.insert(TABLE_TEMP_ATTRIBUTE, null, values);
        db.close(); // Closing database connection
    }

    // Getting one basket
    public BasketData getBasketById(int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BASKET, new String[]{KEY_ID,
                        KEY_ITEM_ID, KEY_SHOP_ID, KEY_USER_ID, KEY_NAME, KEY_DESC, KEY_UNTI_PRICE,
                        KEY_DISCOUNT_PERCENT, KEY_QTY, KEY_IMAGE_PATH, KEY_CURRENCY_SYMBOL, KEY_CURRENCY_SHORT_FORM,
                        KEY_SELECTED_ATTRIBUTE_NAMES, KEY_SELECTED_ATTRIBUTE_IDS}, KEY_ID + "=?",
                new String[]{String.valueOf(itemId)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        BasketData basket = new BasketData(
                Integer.parseInt(cursor.getString(0)),
                Integer.parseInt(cursor.getString(1)),
                Integer.parseInt(cursor.getString(2)),
                Integer.parseInt(cursor.getString(3)),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                Integer.parseInt(cursor.getString(8)),
                cursor.getString(9),
                cursor.getString(10),
                cursor.getString(11),
                cursor.getString(12),
                cursor.getString(13));

        cursor.close();
        db.close();

        return basket;
    }

    // Getting All Basket
    public List<BasketData> getAllBasketData() {
        List<BasketData> basketList = new ArrayList<BasketData>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_BASKET;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                BasketData basketData = new BasketData();
                basketData.setId(Integer.parseInt(cursor.getString(0)));
                basketData.setItemId(Integer.parseInt(cursor.getString(1)));
                basketData.setShopId(Integer.parseInt(cursor.getString(2)));
                basketData.setUserId(Integer.parseInt(cursor.getString(3)));
                basketData.setName(cursor.getString(4));
                basketData.setDesc(cursor.getString(5));
                basketData.setUnitPrice(cursor.getString(6));
                basketData.setDiscountPercent(cursor.getString(7));
                basketData.setQty(Integer.parseInt(cursor.getString(8)));
                basketData.setImagePath(cursor.getString(9));
                basketData.setCurrencySymbol(cursor.getString(10));
                basketData.setCurrencyShortForm(cursor.getString(11));
                basketData.setSelectedAttributeNames(cursor.getString(12));
                basketData.setSelectedAttributeIds(cursor.getString(13));

                // Adding basket to list
                basketList.add(basketData);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return basket list
        return basketList;
    }

    // Getting All Basket By Shop Id
    public List<BasketData> getAllBasketDataByShopId(int shopId) {
        List<BasketData> basketList = new ArrayList<BasketData>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_BASKET + " Where " + KEY_SHOP_ID + " = " + shopId;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                BasketData basketData = new BasketData();
                basketData.setId(Integer.parseInt(cursor.getString(0)));
                basketData.setItemId(Integer.parseInt(cursor.getString(1)));
                basketData.setShopId(Integer.parseInt(cursor.getString(2)));
                basketData.setUserId(Integer.parseInt(cursor.getString(3)));
                basketData.setName(cursor.getString(4));
                basketData.setDesc(cursor.getString(5));
                basketData.setUnitPrice(cursor.getString(6));
                basketData.setDiscountPercent(cursor.getString(7));
                basketData.setQty(Integer.parseInt(cursor.getString(8)));
                basketData.setImagePath(cursor.getString(9));
                basketData.setCurrencySymbol(cursor.getString(10));
                basketData.setCurrencyShortForm(cursor.getString(11));
                basketData.setSelectedAttributeNames(cursor.getString(12));
                basketData.setSelectedAttributeIds(cursor.getString(13));

                // Adding basket to list
                basketList.add(basketData);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return basket list
        return basketList;
    }

    //Getting All Attribute By Header Id
    public List<AttributeData> getAllAttributeDataByHeaderId(int headerId) {
        List<AttributeData> attributeList = new ArrayList<AttributeData>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_TEMP_ATTRIBUTE + " Where " + KEY_ATT_HEADER_ID + " = " + headerId;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                AttributeData attributeData = new AttributeData();
                attributeData.setHeader_id(Integer.parseInt(cursor.getString(0)));
                attributeData.setDetail_id(Integer.parseInt(cursor.getString(1)));
                attributeData.setItem_id(Integer.parseInt(cursor.getString(2)));
                attributeData.setShop_id(Integer.parseInt(cursor.getString(3)));
                attributeData.setDetail_name(cursor.getString(4));
                attributeData.setAttribute_price(cursor.getString(5));
                attributeList.add(attributeData);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return attributeList;
    }

    //Getting All Attribute By Item Id
    public List<AttributeData> getAllAttributeDataByItemId(int itemId) {
        List<AttributeData> attributeList = new ArrayList<AttributeData>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_TEMP_ATTRIBUTE + " Where " + KEY_ATT_ITEM_ID + " = " + itemId;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                AttributeData attributeData = new AttributeData();
                attributeData.setHeader_id(Integer.parseInt(cursor.getString(0)));
                attributeData.setDetail_id(Integer.parseInt(cursor.getString(1)));
                attributeData.setItem_id(Integer.parseInt(cursor.getString(2)));
                attributeData.setShop_id(Integer.parseInt(cursor.getString(3)));
                attributeData.setDetail_name(cursor.getString(4));
                attributeData.setAttribute_price(cursor.getString(5));

                attributeList.add(attributeData);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return attributeList;
    }

    //Getting All Attribute By Ids
    public List<AttributeData> getAllAttributeDataByIds(int itemId, int headerId) {
        List<AttributeData> attributeList = new ArrayList<AttributeData>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_TEMP_ATTRIBUTE + " Where " + KEY_ATT_ITEM_ID + " = " + itemId + " AND " + KEY_ATT_HEADER_ID + " = " + headerId;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                AttributeData attributeData = new AttributeData();
                attributeData.setHeader_id(Integer.parseInt(cursor.getString(0)));
                attributeData.setDetail_id(Integer.parseInt(cursor.getString(1)));
                attributeData.setItem_id(Integer.parseInt(cursor.getString(2)));
                attributeData.setShop_id(Integer.parseInt(cursor.getString(3)));
                attributeData.setDetail_name(cursor.getString(4));
                attributeData.setAttribute_price(cursor.getString(5));

                attributeList.add(attributeData);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return attributeList;
    }


    //Getting QTY By Item ID and Shop ID
    public int getQTYByIds(int itemId, int shopId) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BASKET, new String[]{KEY_ID,
                        KEY_ITEM_ID, KEY_SHOP_ID, KEY_USER_ID, KEY_NAME, KEY_DESC, KEY_UNTI_PRICE,
                        KEY_DISCOUNT_PERCENT, KEY_QTY, KEY_IMAGE_PATH, KEY_CURRENCY_SYMBOL, KEY_CURRENCY_SHORT_FORM,
                        KEY_SELECTED_ATTRIBUTE_NAMES, KEY_SELECTED_ATTRIBUTE_IDS}, KEY_ITEM_ID + "=? AND " + KEY_SHOP_ID + " = ?",
                new String[]{String.valueOf(itemId), String.valueOf(shopId)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        int qty = Integer.parseInt(cursor.getString(8));
        cursor.close();
        db.close();

        return qty;

    }

    //Getting QTY By Item ID and Shop ID
    public int getQTYByKeyIds(int id, int shopId) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BASKET, new String[]{KEY_ID,
                        KEY_ITEM_ID, KEY_SHOP_ID, KEY_USER_ID, KEY_NAME, KEY_DESC, KEY_UNTI_PRICE,
                        KEY_DISCOUNT_PERCENT, KEY_QTY, KEY_IMAGE_PATH, KEY_CURRENCY_SYMBOL, KEY_CURRENCY_SHORT_FORM,
                        KEY_SELECTED_ATTRIBUTE_NAMES, KEY_SELECTED_ATTRIBUTE_IDS}, KEY_ID + "=? AND " + KEY_SHOP_ID + " = ?",
                new String[]{String.valueOf(id), String.valueOf(shopId)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        int qty = Integer.parseInt(cursor.getString(8));

        cursor.close();
        db.close();
        return qty;

    }

    // Getting basket Count
    public int getBasketCount() {
        String countQuery = "SELECT * FROM " + TABLE_BASKET;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

    // Getting basket Count By Item ID
    public int getBasketCountById(int id) {
        String countQuery = "SELECT * FROM " + TABLE_BASKET + " Where " + KEY_ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    // Getting basket Count By  ID And Attribute
    public int getBasketIdByIdAndAttr(int itemId, String paramAttrIds) {
        String countQuery = "SELECT * FROM " + TABLE_BASKET + " Where " + KEY_ITEM_ID + " = " + itemId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int keyId = 0;

        List<BasketData> basketList = new ArrayList<BasketData>();
        if (cursor.moveToFirst()) {
            do {
                BasketData basketData = new BasketData();
                basketData.setId(Integer.parseInt(cursor.getString(0)));
                basketData.setItemId(Integer.parseInt(cursor.getString(1)));
                basketData.setShopId(Integer.parseInt(cursor.getString(2)));
                basketData.setUserId(Integer.parseInt(cursor.getString(3)));
                basketData.setName(cursor.getString(4));
                basketData.setDesc(cursor.getString(5));
                basketData.setUnitPrice(cursor.getString(6));
                basketData.setDiscountPercent(cursor.getString(7));
                basketData.setQty(Integer.parseInt(cursor.getString(8)));
                basketData.setImagePath(cursor.getString(9));
                basketData.setCurrencySymbol(cursor.getString(10));
                basketData.setCurrencyShortForm(cursor.getString(11));
                basketData.setSelectedAttributeNames(cursor.getString(12));
                basketData.setSelectedAttributeIds(cursor.getString(13));

                // Adding basket to list
                basketList.add(basketData);
            } while (cursor.moveToNext());
        }

        if(basketList.size() != 0) {
            Utils.psLog("----------------------------------\nFinding Attr : Basket Size for this item -> " + basketList.size());

            for(int ii=0; ii<basketList.size(); ii++) {

                String attrIds = basketList.get(ii).selected_attribute_ids;
                Utils.psLog("Db Attr : " + attrIds + " Key ID " + basketList.get(ii).id);
                String[] attrIdArray = attrIds.split("#");

                if (paramAttrIds != null) {
                    Utils.psLog("Param Attr : " + paramAttrIds);
                    String[] paramAttrIdsArray = paramAttrIds.split("#");

                    boolean allSame = true;
                    for (String paramData : paramAttrIdsArray) {
                        boolean status = false;
                        for (int i = 0; i < attrIdArray.length; i++) {
                            if (paramData.equals(attrIdArray[i])) {
                                status = true;
                                break;
                            }

                /*if(i == attrIdArray.length-1){
                    innerLoopFinish = true;
                }else {
                    innerLoopFinish = false;
                }*/
                        }

                        //if(innerLoopFinish){
                        if (!status) {
                            allSame = false;
                            keyId = 0;
                            Utils.psLog("Attr Not Found Key for " + paramData);
                            break;
                        } else {
                            Utils.psLog("Attr Found Same Key for " + paramData);
                        }
                        //}
                    }

                    if (allSame) {
                        keyId = basketList.get(ii).id;
                    }
                }
                if (keyId != 0){
                    break;
                }
            }
        }

        Utils.psLog(">>> Attr Final Key Id : " + keyId);
        Utils.psLog("Attr-----------------------------------");
        cursor.close();
        db.close();

        return keyId;
    }

    // Getting basket Count By shop ID
    public int getBasketCountByShopId(int shopId) {
        String countQuery = "SELECT * FROM " + TABLE_BASKET + " Where " + KEY_SHOP_ID + " = " + shopId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();

        cursor.close();
        db.close();

        return count;
    }

    // Updating a basket
    public int updateBasketByIds(BasketData basketData, int id, int shopId) {
        int updateStatus;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_ID, basketData.getItemId());
        values.put(KEY_SHOP_ID, basketData.getShopId());
        values.put(KEY_USER_ID, basketData.getUserId());
        values.put(KEY_NAME, basketData.getName());
        values.put(KEY_DESC, basketData.getDesc());
        values.put(KEY_UNTI_PRICE, basketData.getUnitPrice());
        values.put(KEY_DISCOUNT_PERCENT, basketData.getDiscountPercent());
        values.put(KEY_QTY, basketData.getQty());
        values.put(KEY_IMAGE_PATH, basketData.getImagePath());
        values.put(KEY_CURRENCY_SYMBOL, basketData.getCurrencySymbol());
        values.put(KEY_CURRENCY_SHORT_FORM, basketData.getCurrencyShortForm());
        values.put(KEY_SELECTED_ATTRIBUTE_NAMES, basketData.getSelectedAttributeNames());
        values.put(KEY_SELECTED_ATTRIBUTE_IDS, basketData.getSelectedAttributeIds());

        // updating row
        updateStatus = db.update(TABLE_BASKET, values, KEY_ID + " = ? AND " + KEY_SHOP_ID + " = ?",
                new String[]{String.valueOf(id), String.valueOf(shopId)});
        db.close();
        return updateStatus;
    }

    //Update a Attribute By Header Id
    public int updateAttributeByIds(AttributeData attributeData, int headerId, int itemId) {
        int updateStatus;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ATT_HEADER_ID, attributeData.getHeader_id());
        values.put(KEY_ATT_DETAIL_ID, attributeData.getDetail_id());
        values.put(KEY_ATT_ITEM_ID, attributeData.getItem_id());
        values.put(KEY_ATT_SHOP_ID, attributeData.getShop_id());
        values.put(KEY_ATT_DETAIL_NAME, attributeData.getDetail_name());
        values.put(KEY_ATT_PRICE, attributeData.getAttribute_price());

        // updating row
        updateStatus = db.update(TABLE_TEMP_ATTRIBUTE, values, KEY_ATT_HEADER_ID + " = ? AND " + KEY_ATT_ITEM_ID + " = ?",
                new String[]{String.valueOf(headerId), String.valueOf(itemId)});
        db.close();
        return updateStatus;
    }

    // Deleting a basket
    public void deleteBasket(BasketData basketData) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BASKET, KEY_ID + " = ?",
                new String[] { String.valueOf(basketData.getId()) });
        db.close();
    }

    // Deleting a basket by IDs
    public void deleteBasketByIds(int itemId, int shopId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BASKET, KEY_ITEM_ID + " = ? AND " + KEY_SHOP_ID + " =? ",
                new String[] { String.valueOf(itemId), String.valueOf(shopId)  });
        db.close();
    }

    // Deleting a basket by IDs
    public void deleteBasketByKeyIds(int keyId, int shopId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BASKET, KEY_ID + " = ? AND " + KEY_SHOP_ID + " =? ",
                new String[] { String.valueOf(keyId), String.valueOf(shopId)  });
        db.close();
    }

    // Deleting a basket by Shop Id
    public void deleteBasketByShopId(int shopId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BASKET, KEY_SHOP_ID + " =? ",
                new String[] {String.valueOf(shopId)});
        db.close();
    }

    // Delete a Attribute By Header Id
    public void deleteAttributeByHeaderId(int headerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TEMP_ATTRIBUTE, KEY_ATT_HEADER_ID + " =? ",
                new String[] {String.valueOf(headerId)});
        db.close();
    }

    //Delete All Attribute
    public void deleteAllAttribute() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TEMP_ATTRIBUTE, null, null);
        db.close();
    }

    //Delete All Attribute By Ids
    public void deleteAllAttributeByIds(int headerId, int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TEMP_ATTRIBUTE, KEY_ATT_HEADER_ID + " = ? AND " + KEY_ATT_ITEM_ID + " =? ",
                new String[] { String.valueOf(headerId), String.valueOf(itemId)  });
        db.close();
    }

    // Delete a Attribute By Shop Id
    public void deleteAttributeByShopId(int shopId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TEMP_ATTRIBUTE, KEY_ATT_SHOP_ID + " =? ",
                new String[] {String.valueOf(shopId)});
        db.close();
    }

    //Delete Attributes By Item Id
    public void deleteAttributeByItemId(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TEMP_ATTRIBUTE, KEY_ATT_ITEM_ID + " =? ",
                new String[] {String.valueOf(itemId)});
        db.close();
    }
}