package com.csp.app.mapper;

import com.csp.app.entity.OperateLog;

public interface OperateLogMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operate_log
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operate_log
     *
     * @mbg.generated
     */
    int insert(OperateLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operate_log
     *
     * @mbg.generated
     */
    int insertSelective(OperateLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operate_log
     *
     * @mbg.generated
     */
    OperateLog selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operate_log
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(OperateLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operate_log
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(OperateLog record);
}