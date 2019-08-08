package com.walmart.common.service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.google.common.base.CaseFormat;
import com.walmart.common.annotation.CustomTableName;
import com.walmart.common.dbsource.DBSource;
import com.walmart.common.enumeration.AppStatus;
import com.walmart.common.process.ProcessBack;

public class BaseService<Entity> {
	protected  static Logger logger;
	 /**
    * 数据库实体类型
    */
   protected final Class<Entity> entityClass;
   /**
    * 数据库表
    */
   protected final String entityTableName;
   
   @Autowired
   JdbcTemplate jdbcTemplate;
   
   public BaseService() {
       logger = LoggerFactory.getLogger(this.getClass());
       //获取到当前类的类型
       Type parentType = this.getClass().getGenericSuperclass();
       System.out.println("parentType:"+parentType);
       // 转成参数类型接口
       ParameterizedType paramterType = (ParameterizedType) parentType;
       System.out.println("paramterType:"+paramterType);
       // 得到泛型类型
       Type[] types = paramterType.getActualTypeArguments();
       System.out.println("types:"+types);
       // 得到传入泛型的类
       entityClass = (Class<Entity>) types[0];
       CustomTableName customTableName = entityClass.getAnnotation(CustomTableName.class);
       if(customTableName != null && StringUtils.isNotEmpty(customTableName.value())){
           entityTableName = customTableName.value();
       }else{
           entityTableName = entityClass.getSimpleName().toLowerCase();
       }
       
       System.out.println("entityTableName:"+entityTableName);
   }
   /**
    * 根据id查询到单个对象
    * @param id
    * @return
    */
   public Entity getEntityById(Long id){
       if(id == null || id <= 0){
           logger.error("根据ID查询数据异常,id:{}",id);
           return null;
       }
       String sql = "SELECT * FROM "+entityTableName+" WHERE id = ?";
       Entity entity = jdbcTemplate.queryForObject(sql, entityClass, new Object[]{id});
       return entity;
   }
   /**
    * 根据id删除对象
    * @param id
    * @return
    */
   public int deleteEntityById(Long id){
       try{
           if(id == null || id <= 0){
               logger.error("根据ID物理删除数据异常,id:{}",id);
           }
           String sql = "DELETE FROM "+entityTableName+" WHERE id = ?";
           int c = jdbcTemplate.update(sql, new Object[]{id});
           return c;
       }catch (Exception e){
           e.printStackTrace();
       }
       return -1;
   }
   
   public ProcessBack preInsert(Entity entity){
       return new ProcessBack(AppStatus.Success);
   }
   /**
    * 添加
    * @param entity
    * @return
    */
   public ProcessBack insert(Entity entity,String dataKey){
       try{
           ProcessBack back = preInsert(entity);
           if(back.getCode().equals(AppStatus.Fail.getCode())){
               logger.error("新增前验证未通过,back:{}",back);
               return back;
           }
           String sql = getSaveSql(entity);
           int c = jdbcTemplate.update(sql);
           if(c > 0){
        	   int id = getInsert_id(dataKey);
               return new ProcessBack(AppStatus.Success,id);
           }
       }catch (Exception e){
           e.printStackTrace();
       }
       return new ProcessBack(AppStatus.Fail);
   }
   /**
    * 获取刚插入数据的id
    * @param dataKey
    * @return
    */
   public Integer getInsert_id(String dataKey){
	   String sql  = "";
	   if(dataKey.equals(DBSource.getMysql())){
		   sql= "select @@identity";
	   }
	   if(dataKey.equals(DBSource.getSqlserver())){
		   sql = "select ident_current('"+entityTableName+"')";
	   }
	   return jdbcTemplate.queryForObject(sql, Integer.class);
   }
   /**
    * 获取到拼接的sql 新增语句
    * @param object
    * @return
    */
   public String getSaveSql(Entity object) {
	   //获取到插入的表名字 this.classToDbForMat(object.getClass().getSimpleName())
	   //System.out.println("插入："+this.classToDbForMat(object.getClass().getSimpleName()));
       String sql = "insert into " + this.classToDbForMat(object.getClass().getSimpleName());
       //获取表中的所有字段名字
       Field[] fields = object.getClass().getDeclaredFields();
       List<String> f = new ArrayList<String>();//用于装载属性名
       List<Object> v = new ArrayList<Object>();//用于装载传入的值
       SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       for (Field field : fields) {
    	   //开启私有属性
           field.setAccessible(true);
			System.out.println(Modifier.toString(field.getModifiers()) + "--" + field.getGenericType().getTypeName()
					+ "&&" + entityClass.getName() + "==" + field.getName());
			//private--java.lang.Long&&com.walmart.api.model.admin.Admin==id
           try {
               Object value = field.get(object);
               if (value != null) {
            	   f.add(this.fieldToDbForMat(field.getName()));
                   if (value instanceof Date) {
                       v.add("'" + sqlFormat.format(value) + "'");
                   } else if(value instanceof String){
                       v.add("'" + value.toString() + "'");
                   }else{
                	   v.add("" + value + "");
                   }
                }

           } catch (IllegalArgumentException | IllegalAccessException e) {
        	   logger.error("auto schedule plugin save domain error ...");
           }
       }
       //说明：String join = StringUtils.join(list,"-");//传入String类型的List集合，使用"-"号拼接
       sql += "(" + StringUtils.join(f, ",") + ") values(" + StringUtils.join(v, ",") + ")";
       logger.info("save sql is:{}",sql);
       return sql;
   }
   
   private String fieldToDbForMat(String fieldName) {
       return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
   }

   private String classToDbForMat(String className) {
       return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className);
   }
   
   public ProcessBack preUpdateById(Entity entity){
       return new ProcessBack(AppStatus.Success);
   }
   /**
    * 修改对象
    * @param entity
    * @return
    */
   public ProcessBack updateById(Entity entity){
       try{
           ProcessBack back =  preUpdateById(entity);
           if(back.getCode().equals(AppStatus.Fail.getCode())){
               logger.error("更新前验证未通过,back:{}", back);
               return back;
           }
           String sql = getUpdateSql(entity);
           Integer cc  = jdbcTemplate.update(sql);
           if(cc > 0){
               return new ProcessBack(AppStatus.Success);
           }
       }catch (Exception e){
           e.printStackTrace();
       }
       return new ProcessBack(AppStatus.Fail);
   }
   /**
    * 获取到拼接的sql 修改语句
    * @param object
    * @return
    */
   public String getUpdateSql(Entity object) {
       String sql = "update " + this.classToDbForMat(object.getClass().getSimpleName());
       String id = "";
       List<Object> list = new ArrayList<>();
       SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       //获取表中的所有字段名字
       Field[] fields = object.getClass().getDeclaredFields();
       for (Field field : fields) {
           field.setAccessible(true);
           try {
               Object value = field.get(object);
               if ("id".equals(field.getName())) {
                   id = value.toString();
               } else {
                   if (value instanceof Date) {
                       list.add(this.fieldToDbForMat(field.getName()) + " = " + (value == null ? "NULL" : "'" + sqlFormat.format(value) + "'"));
                   } else if(value instanceof String){
                       list.add(this.fieldToDbForMat(field.getName()) + " = " + (value == null ? "NULL" : "'" + value.toString() + "'"));
                   } else{
                	   list.add(this.fieldToDbForMat(field.getName()) + " = " + (value == null ? "NULL" : "" + value + ""));
                   }

               }
           } catch (IllegalArgumentException | IllegalAccessException e) {
           }
       }
       sql += " set " + StringUtils.join(list, ",") + " where id=" + id;
       logger.info(sql);
       return sql;
   }
}
