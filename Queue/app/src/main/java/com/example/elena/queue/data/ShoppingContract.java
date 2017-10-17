package com.example.elena.queue.data;

import android.provider.BaseColumns;

/**
 * Created by Elena on 9/13/2017.
 */

public class ShoppingContract {
    public static final class ShoppingEntry implements BaseColumns{
        public static final String TABLE_NAME = "products";
        public static final String COLUMN_PRODUCT_NAME = "product_name";
    }
}
