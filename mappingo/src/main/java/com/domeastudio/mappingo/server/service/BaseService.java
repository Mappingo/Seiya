package com.domeastudio.mappingo.server.service;

import com.domeastudio.mappingo.server.dao.BaseDAO;
import org.hibernate.Criteria;

import java.io.Serializable;
import java.util.List;


/**
 * @param <T>
 * @author DomeA
 */
public class BaseService<T extends Serializable> {
    private BaseDAO<T> baseDAO;

    public void save(T t) throws Exception {
        baseDAO.save(t);
    }

    public Serializable saveExt(T t) throws Exception {
        return baseDAO.saveExt(t);
    }

    public void update(T t) throws Exception {
        baseDAO.update(t);
    }

    public void delete(T t) throws Exception {
        baseDAO.delete(t);
    }

    public T get(Serializable id) throws Exception {
        return baseDAO.get(id);
    }

    public List<T> findAll() throws Exception {
        return baseDAO.findAll();
    }

    public List<T> findByExample(T t) throws Exception {
        return baseDAO.findByExample(t);
    }

    public int findAllCount() throws Exception {
        return baseDAO.findAllCount();
    }

    public List<T> findByCriteria(Criteria criteria) throws Exception {
        return baseDAO.findByCriteria(criteria);
    }

    public List<Object[]> findByHql(String hql, final Object... objects) throws Exception {
        return baseDAO.findByHql(hql, objects);
    }

    public List<T> findByHqlExt(String hql, final Object... objects) throws Exception {
        return baseDAO.findByHqlExt(hql, objects);
    }

    public List<Object[]> findBySql(String sql, final Object... objects) throws Exception {
        return baseDAO.findBySql(sql, objects);
    }

    public List<T> findBySqlExt(String sql, final Object... objects) throws Exception {
        return baseDAO.findBySqlExt(sql, objects);
    }

    public BaseDAO<T> getBaseDao() {
        return baseDAO;
    }

    public void setBaseDao(BaseDAO<T> baseDao) {
        this.baseDAO = baseDao;
    }

    public int findByHqlToCount(String hql, Object... Objects) {
        return this.baseDAO.findByHqlToCount(hql, Objects);
    }

    public int findBySqlToCount(String sql, Object... Objects) {
        return this.baseDAO.findBySqlToCount(sql, Objects);
    }
}
