package com.csp.app.mapper;

import com.csp.app.entity.Clasz;

public interface ClaszMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table clasz
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table clasz
     *
     * @mbg.generated
     */
    int insert(Clasz record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table clasz
     *
     * @mbg.generated
     */
    int insertSelective(Clasz record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table clasz
     *
     * @mbg.generated
     */
    Clasz selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table clasz
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(Clasz record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table clasz
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Clasz record);
}