package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @ApiOperation(value = "员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @ApiOperation(value = "员工退出登录")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    @ApiOperation(value = "新增员工")
    @PostMapping
    public Result add(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.add(employeeDTO);
        return Result.success();
    }

    /**
     * 员工分页查询
     * @param dto
     * @return
     */
    @ApiOperation(value = "员工分页查询")
    @GetMapping("/page")
    public Result page(EmployeePageQueryDTO dto){
        PageResult pageResult = employeeService.pageQuery(dto);
        return Result.success(pageResult);
    }

    /**
     * 开启或者禁用员工状态
     * @param status
     * @param id
     * @return
     */
    @ApiOperation(value = "开启或者禁用员工状态")
    @PostMapping("/status/{status}")
    public Result updateStatus(@PathVariable Integer status, Long id){
        employeeService.updateStatus(status, id);
        return Result.success();
    }

    /**
     * 根据ID查询员工信息
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID查询员工信息")
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id){
        Employee e = employeeService.getById(id);
        return Result.success(e);
    }

    @ApiOperation(value = "编辑员工信息")
    @PutMapping
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        employeeService.update(employeeDTO);
        return Result.success();


    }


}
