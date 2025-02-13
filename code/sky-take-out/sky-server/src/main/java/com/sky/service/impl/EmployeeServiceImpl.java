package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;



import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO
     */
    @Override
    public Employee add(EmployeeDTO employeeDTO) {
        //1. 手动创建一个Employee对象
        Employee employee = new Employee();


        //2. 把dto里面的数据搬运到employee对象身上
        // 太复杂
//        employee.setUsername(employeeDTO.getUsername());
//        employee.setName(employeeDTO.getName());
//        employee.setPhone(employeeDTO.getPhone());
//        employee.setSex(employeeDTO.getSex());
//        employee.setIdNumber(employeeDTO.getIdNumber());
        // 从源对象拷贝到目标属性（只会拷贝同名属性）
        BeanUtils.copyProperties(employeeDTO, employee);

        //3. 看看employee还缺少什么属性的值没有，缺少什么就补充什么
        employee.setStatus(1);
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //创建的用户和更新的用户。。
        //1 要想获取登录的ID值，必须获取到token令牌
        //2 要想获取到token的令牌，必须从请求头里面获取
        //3 要想从请求头里面获取数据，必须借助request对象

//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());

        employee.setPassword("123456");

        //4. 调用dao完成工作
        employeeMapper.add(employee);

        return null;

    }

    /**
     * 员工分页查询
     *
     * @param dto
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO dto) {
        //1 使用分页插件，设置查询第几页，每页多少条
        PageHelper.startPage(dto.getPage(), dto.getPageSize());

        //2 调用mapper
        Page<Employee> p = employeeMapper.pageQuery(dto);

        //3 封装结果返回

        return new PageResult(p.getTotal(), p.getResult());
    }

    /**
     * 员工状态禁用or开启
     *
     * @param status
     * @param id
     * @return
     */
    @Override
    public void updateStatus(Integer status, Long id) {
        Employee employee = new Employee();
        employee = Employee.builder()
                .id(id)
                .status(status)
                .build();
        employeeMapper.update(employee);
    }

    /**
     * 根据ID查询员工信息
     *
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        employee.setPassword("****");
        return employee;
    }

    /**
     * 更新员工信息
     *
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        //1 创建Employee对象employee
        Employee employee = new Employee();

        //2 将employeeDTO中的属性copy到employee中
        BeanUtils.copyProperties(employeeDTO,employee);

        //3 补充其他属性
//        employee.setUpdateUser(BaseContext.getCurrentId());
//        employee.setUpdateTime(LocalDateTime.now());

        employeeMapper.update(employee);
    }

}
