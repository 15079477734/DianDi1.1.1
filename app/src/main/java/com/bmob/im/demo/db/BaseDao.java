package com.bmob.im.demo.db;

import android.content.Context;
import android.util.Log;

import com.bmob.im.demo.bean.Plan;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ************************************************************
 * *********    User : SuLinger(462679107@qq.com) .
 * *********    Date : 2014-09-14  .
 * *********    Planime:  2014-09-14  .
 * *********    Project name :DianDi1.1.0 .
 * *********    Copyright @ 2014, SuLinger, All Rights Reserved
 * *************************************************************
 */


public abstract class BaseDao {
    private final static String TAG = "BaseDao";
    protected DatabaseHelper mDatabaseHelper;
    protected Context mContext;

    public BaseDao(Context context) {
        mContext = context;
        getHelper();
    }

    public DatabaseHelper getHelper() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = OpenHelperManager.getHelper(mContext, DatabaseHelper.class);
        }
        return mDatabaseHelper;
    }

    public abstract Dao<Plan, Integer> getDao() throws SQLException;

    public int save(Plan t) throws SQLException {
        return getDao().create(t);
    }

    public List<Plan> query(PreparedQuery<Plan> preparedQuery) throws SQLException {
        Dao<Plan, Integer> dao = getDao();
        return dao.query(preparedQuery);
    }

    public List<Plan> query(String attributeName, String attributeValue) throws SQLException {
        QueryBuilder<Plan, Integer> queryBuilder = getDao().queryBuilder();
        queryBuilder.where().eq(attributeName, attributeValue);
        PreparedQuery<Plan> preparedQuery = queryBuilder.prepare();
        return query(preparedQuery);
    }

    public List<Plan> query(String[] attributeNames, String[] attributeValues) throws SQLException {
        if (attributeNames.length != attributeValues.length) {
            Log.e(TAG, "params size is not equal");
        }
        QueryBuilder<Plan, Integer> queryBuilder = getDao().queryBuilder();
        Where<Plan, Integer> wheres = queryBuilder.where();
        for (int i = 0; i < attributeNames.length; i++) {
            wheres.eq(attributeNames[i], attributeValues[i]);
        }
        PreparedQuery<Plan> preparedQuery = queryBuilder.prepare();
        return query(preparedQuery);
    }

    public List<Plan> queryAll() throws SQLException {
        // QueryBuilder<Plan, Integer> queryBuilder = getDao().queryBuilder();
        // PreparedQuery<Plan> preparedQuery = queryBuilder.prepare();
        // return query(preparedQuery);
        Dao<Plan, Integer> dao = getDao();
        return dao.queryForAll();
    }

    public Plan queryById(String idName, String idValue) throws SQLException {
        List<Plan> lst = query(idName, idValue);
        if (null != lst && !lst.isEmpty()) {
            return lst.get(0);
        } else {
            return null;
        }
    }

    public int delete(PreparedDelete<Plan> preparedDelete) throws SQLException {
        Dao<Plan, Integer> dao = getDao();
        return dao.delete(preparedDelete);
    }

    public int delete(Plan t) throws SQLException {
        Dao<Plan, Integer> dao = getDao();
        return dao.delete(t);
    }

    public int delete(List<Plan> lst) throws SQLException {
        Dao<Plan, Integer> dao = getDao();
        return dao.delete(lst);
    }

    public int delete(String[] attributeNames, String[] attributeValues) throws SQLException {
        List<Plan> lst = query(attributeNames, attributeValues);
        if (null != lst && !lst.isEmpty()) {
            return delete(lst);
        }
        return 0;
    }

    public int deleteById(String idName, String idValue) throws SQLException {
        Plan t = queryById(idName, idValue);
        if (null != t) {
            return delete(t);
        }
        return 0;
    }

    public int update(Plan t) throws SQLException {
        Dao<Plan, Integer> dao = getDao();
        return dao.update(t);
    }

    public boolean isPlanableExsits() throws SQLException {
        return getDao().isTableExists();
    }

    public long countOf() throws SQLException {
        return getDao().countOf();
    }

    public List<Plan> query(Map<String, Object> map) throws SQLException {
        QueryBuilder<Plan, Integer> queryBuilder = getDao().queryBuilder();
        if (!map.isEmpty()) {
            Where<Plan, Integer> wheres = queryBuilder.where();
            Set<String> keys = map.keySet();
            ArrayList<String> keyss = new ArrayList<String>();
            keyss.addAll(keys);
            for (int i = 0; i < keyss.size(); i++) {
                if (i == 0) {
                    wheres.eq(keyss.get(i), map.get(keyss.get(i)));
                } else {
                    wheres.and().eq(keyss.get(i), map.get(keyss.get(i)));
                }
            }
        }
        PreparedQuery<Plan> preparedQuery = queryBuilder.prepare();
        return query(preparedQuery);
    }

    public List<Plan> query(Map<String, Object> map, Map<String, Object> lowMap,
                            Map<String, Object> highMap) throws SQLException {
        QueryBuilder<Plan, Integer> queryBuilder = getDao().queryBuilder();
        Where<Plan, Integer> wheres = queryBuilder.where();
        if (!map.isEmpty()) {
            Set<String> keys = map.keySet();
            ArrayList<String> keyss = new ArrayList<String>();
            keyss.addAll(keys);
            for (int i = 0; i < keyss.size(); i++) {
                if (i == 0) {
                    wheres.eq(keyss.get(i), map.get(keyss.get(i)));
                } else {
                    wheres.and().eq(keyss.get(i), map.get(keyss.get(i)));
                }
            }
        }
        if (!lowMap.isEmpty()) {
            Set<String> keys = lowMap.keySet();
            ArrayList<String> keyss = new ArrayList<String>();
            keyss.addAll(keys);
            for (int i = 0; i < keyss.size(); i++) {
                if (map.isEmpty()) {
                    wheres.gt(keyss.get(i), lowMap.get(keyss.get(i)));
                } else {
                    wheres.and().gt(keyss.get(i), lowMap.get(keyss.get(i)));
                }
            }
        }

        if (!highMap.isEmpty()) {
            Set<String> keys = highMap.keySet();
            ArrayList<String> keyss = new ArrayList<String>();
            keyss.addAll(keys);
            for (int i = 0; i < keyss.size(); i++) {
                wheres.and().lt(keyss.get(i), highMap.get(keyss.get(i)));
            }
        }
        PreparedQuery<Plan> preparedQuery = queryBuilder.prepare();
        return query(preparedQuery);
    }
}
