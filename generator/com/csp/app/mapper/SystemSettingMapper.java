package com.csp.app.mapper;

import com.csp.app.entity.SystemSetting;

public interface SystemSettingMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table system_setting
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table system_setting
     *
     * @mbg.generated
     */
    int insert(SystemSetting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table system_setting
     *
     * @mbg.generated
     */
    int insertSelective(SystemSetting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table system_setting
     *
     * @mbg.generated
     */
    SystemSetting selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table system_setting
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(SystemSetting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table system_setting
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(SystemSetting record);
}