package com.csp.app.entity;

import java.io.Serializable;
import java.util.Date;

public class Student implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column student.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column student.class_id
     *
     * @mbg.generated
     */
    private Integer classId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column student.create_time
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column student.student_id
     *
     * @mbg.generated
     */
    private Long studentId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column student.student_name
     *
     * @mbg.generated
     */
    private String studentName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column student.to_school_year
     *
     * @mbg.generated
     */
    private Integer toSchoolYear;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column student.type
     *
     * @mbg.generated
     */
    private Integer type;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table student
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column student.id
     *
     * @return the value of student.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column student.id
     *
     * @param id the value for student.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column student.class_id
     *
     * @return the value of student.class_id
     *
     * @mbg.generated
     */
    public Integer getClassId() {
        return classId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column student.class_id
     *
     * @param classId the value for student.class_id
     *
     * @mbg.generated
     */
    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column student.create_time
     *
     * @return the value of student.create_time
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column student.create_time
     *
     * @param createTime the value for student.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column student.student_id
     *
     * @return the value of student.student_id
     *
     * @mbg.generated
     */
    public Long getStudentId() {
        return studentId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column student.student_id
     *
     * @param studentId the value for student.student_id
     *
     * @mbg.generated
     */
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column student.student_name
     *
     * @return the value of student.student_name
     *
     * @mbg.generated
     */
    public String getStudentName() {
        return studentName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column student.student_name
     *
     * @param studentName the value for student.student_name
     *
     * @mbg.generated
     */
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column student.to_school_year
     *
     * @return the value of student.to_school_year
     *
     * @mbg.generated
     */
    public Integer getToSchoolYear() {
        return toSchoolYear;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column student.to_school_year
     *
     * @param toSchoolYear the value for student.to_school_year
     *
     * @mbg.generated
     */
    public void setToSchoolYear(Integer toSchoolYear) {
        this.toSchoolYear = toSchoolYear;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column student.type
     *
     * @return the value of student.type
     *
     * @mbg.generated
     */
    public Integer getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column student.type
     *
     * @param type the value for student.type
     *
     * @mbg.generated
     */
    public void setType(Integer type) {
        this.type = type;
    }
}