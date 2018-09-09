package com.panaceasoft.restaurateur;

import com.panaceasoft.restaurateur.models.PShopData;
import com.panaceasoft.restaurateur.models.PItemData;
import com.panaceasoft.restaurateur.models.PTransactionData;

import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 8/2/15.
 * Contact Email : teamps.is.cool@gmail.com
 */


public class GlobalData {
    public static PItemData itemData =  null;
    public static PShopData shopdata = null;
    public static ArrayList<PShopData> shopDatas = new ArrayList<PShopData>();
    public static ArrayList<PTransactionData> transactionDatas = new ArrayList<>();

}
