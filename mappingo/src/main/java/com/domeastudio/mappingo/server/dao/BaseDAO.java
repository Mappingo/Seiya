package com.domeastudio.mappingo.server.dao;

import org.hibernate.*;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Id;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 基于hibernate的basedao
 * spring3对hibernate4已经没有了HibernateDaoSupport和HibernateTemplate的支持，使用了原生态的API
 *
 * @param <T>
 * @author DomeA
 */
public class BaseDAO<T extends Serializable> {
    private static final Logger logger = LoggerFactory.getLogger(BaseDAO.class);

    @Autowired
    private SessionFactory sessionFactory;
    //当前泛型类
    @SuppressWarnings("rawtypes")
    private Class entityClass;
    //当前主键名称
    private String pkname;
    private final String HQL_LIST_ALL;
    private final String HQL_COUNT_ALL;

    @SuppressWarnings("rawtypes")
    public Class getEntityClass() {
        return entityClass;
    }

    @SuppressWarnings("rawtypes")
    public void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }

    @SuppressWarnings("rawtypes")
    public BaseDAO() {
        //获取当前泛型类
        Type type = this.getClass().getGenericSuperclass();
        System.out.println("===>this is BaseDao类=>当前泛型类:Type=" + type);
        logger.info("===>this is BaseDao类=> 当前泛型类:Type=" + type);
        if (type.toString().indexOf("BaseDAO") != -1) {
            ParameterizedType type1 = (ParameterizedType) type;
            Type[] types = type1.getActualTypeArguments();
            setEntityClass((Class) types[0]);
        } else {
            type = ((Class) type).getGenericSuperclass();
            ParameterizedType type1 = (ParameterizedType) type;
            Type[] types = type1.getActualTypeArguments();
            setEntityClass((Class) types[0]);
        }
        getPkname();
        HQL_LIST_ALL = "from " + this.entityClass.getSimpleName() + " order by " + pkname + " desc";
        HQL_COUNT_ALL = "select count(*) from " + this.entityClass.getSimpleName();
    }

    /**
     * 获取主键名称
     *
     * @return
     */
    public String getPkname() {
        Field[] fields = this.entityClass.getDeclaredFields();//反射类字段
        for (Field field : fields) {
            field.isAnnotationPresent(Id.class);
            this.pkname = field.getName();
            break;
        }
        return pkname;
    }

    /**
     * 保存实例
     *
     * @param t
     * @throws HibernateException
     */
    public void save(T t) throws HibernateException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(t);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new HibernateException(e);
        } finally {
            session.close();
        }
    }

    /**
     * 有返回值的保存实例
     *
     * @param t
     * @throws HibernateException
     */
    public Serializable saveExt(T t) throws HibernateException {
        Session session = null;
        Serializable rowid;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            rowid = session.save(t);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new HibernateException(e);
        } finally {
            session.close();
        }
        return rowid;
    }

    /**
     * 修改实例
     *
     * @param t
     * @throws HibernateException
     */
    public void update(T t) throws HibernateException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.update(t);//改动
            session.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new HibernateException(e);
        } finally {
            session.close();
        }
    }

    /**
     * 删除实例
     *
     * @param t
     * @throws HibernateException
     */
    public void delete(T t) throws HibernateException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.delete(t);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new HibernateException(e);
        } finally {
            session.close();
        }
    }

    /**
     * 获取实例
     *
     * @param id
     * @throws HibernateException
     */
    @SuppressWarnings("unchecked")
    public T get(Serializable id) throws Exception {
        Session session = null;
        T t = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            t = (T) session.get(getEntityClass(), id);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new HibernateException(e);
        } finally {
            session.close();
        }
        return t;
    }

    /**
     * 查询全部
     *
     * @throws HibernateException
     */
    @SuppressWarnings("unchecked")
    public List<T> findAll() throws Exception {
        List<T> list = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            Query query = session.createQuery(HQL_LIST_ALL);
            list = query.list();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new HibernateException(e);
        } finally {
            session.close();
        }
        return list;
    }

    /**
     * 查询总数
     *
     * @throws HibernateException
     */
    public Integer findAllCount() throws Exception {
        Session session = null;
        Integer count = 0;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            Query query = session.createQuery(HQL_COUNT_ALL);
            List<?> list = query.list();
            session.getTransaction().commit();
            if (list != null && !list.isEmpty()) {
                count = ((Integer) list.get(0)).intValue();
            }
        } catch (HibernateException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new HibernateException(e);
        } finally {
            session.close();
        }
        return count;
    }

    /**
     * QBC查询（标准查询）
     *
     * @param criteria
     * @throws HibernateException
     */
    @SuppressWarnings("unchecked")
    public List<T> findByCriteria(Criteria criteria) throws Exception {
        List<T> list = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            Criteria criteria1 = criteria;
            list = criteria1.list();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new HibernateException(e);
        } finally {
            session.close();
        }
        return list;
    }

    /**
     * QBE查询（模板查询）
     *
     * @param t
     * @throws HibernateException
     */
    @SuppressWarnings("unchecked")
    public List<T> findByExample(T t) throws Exception {
        List<T> list = null;
        Session session = null;
        Example example = Example.create(t);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            Criteria criteria = session.createCriteria(getEntityClass());
            criteria.add(example);
            list = criteria.list();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new HibernateException(e);
        } finally {
            session.close();
        }
        return list;
    }

    /**
     * HQL查询（hibernate语句查询）
     *
     * @param hql
     * @param objects
     * @throws HibernateException
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findByHql(String hql, final Object... objects) throws Exception {
        List<Object[]> list;
        Session session = null;
        list = getFindByCoreExt(hql, session, objects);
        return list;
    }

    private List<Object[]> getFindByCoreExt(String hql, Session session, Object... objects) {
        List<Object[]> list;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            Query query = session.createQuery(hql);
            for (int i = 0; i < objects.length; i++) {
                System.out.println("====>BaseDao类:不确定个数参数objects:" + objects[i]);
                query.setParameter(i, objects[i]);
            }
            list = query.list();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new HibernateException(e);
        } finally {
            session.close();
        }
        return list;
    }

    /**
     * HQL查询（hibernate语句查询）
     *
     * @param hql
     * @param objects
     * @throws HibernateException
     */
    @SuppressWarnings("unchecked")
    public List<T> findByHqlExt(String hql, final Object... objects) throws Exception {
        List<T> list;
        Session session = null;
        list = getFindByCore(hql, session, objects);
        return list;
    }

    private List<T> getFindByCore(String hql, Session session, Object... objects) {
        List<T> list;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            Query query = session.createQuery(hql);
            for (int i = 0; i < objects.length; i++) {
                System.out.println("====>BaseDao类:不确定个数参数objects:" + objects[i]);
                query.setParameter(i, objects[i]);
            }
            list = query.list();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new HibernateException(e);
        } finally {
            session.close();
        }
        return list;
    }

    /**
     * SQL查询（SQL语句查询）
     *
     * @param sql
     * @param objects
     * @throws HibernateException
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findBySql(String sql, final Object... objects) throws Exception {
        List<Object[]> list;
        Session session = null;
        list = getFindByCoreExt(sql, session, objects);
        return list;
    }

    /**
     * SQL查询（SQL语句查询）
     *
     * @param sql
     * @param objects
     * @throws HibernateException
     */
    @SuppressWarnings("unchecked")
    public List<T> findBySqlExt(String sql, final Object... objects) throws Exception {
        List<T> list;
        Session session = null;
        list = getFindByCore(sql, session, objects);
        return list;
    }

    /**
     * 统计查询
     *
     * @param hql
     * @param Objects
     * @return
     */
    public Integer findByHqlToCount(String hql, Object... Objects) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Query query = session.createQuery(hql);
            for (int i = 0; i < Objects.length; i++) {
                System.out.println("BaseDao>>findByHqlToCount不确定的参数Objects:" + Objects[i]);
                query.setParameter(i, Objects[i]);
            }
            return Integer.parseInt(query.uniqueResult().toString());
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(e.getMessage());
            return 0;
        } catch (HibernateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(e.getMessage());
            return 0;
        } finally {
            session.close();
        }
    }

    /**
     * 统计查询
     *
     * @param sql
     * @param Objects
     * @return
     */
    public Integer findBySqlToCount(String sql, Object... Objects) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Query query = session.createQuery(sql);
            for (int i = 0; i < Objects.length; i++) {
                System.out.println("BaseDao>>findBySqlToCount不确定的参数Objects:" + Objects[i]);
                query.setParameter(i, Objects[i]);
            }
            return Integer.parseInt(query.uniqueResult().toString());
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(e.getMessage());
            return 0;
        } catch (HibernateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(e.getMessage());
            return 0;
        } finally {
            session.close();
        }
    }
}
