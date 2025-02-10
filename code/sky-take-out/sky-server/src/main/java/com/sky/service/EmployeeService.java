package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     */
    Employee add(EmployeeDTO employeeDTO);

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 员工状态禁用or开启
     * @param status
     * @param id
     * @return
     */
    void updateStatus(Integer status, Long id);

    /**
     * 根据ID查询员工信息
     * @param id
     * @return
     */
    Employee getById(Long id);

    /** 更新员工信息
     * @param employeeDTO
     */
    void update(EmployeeDTO employeeDTO);



}
