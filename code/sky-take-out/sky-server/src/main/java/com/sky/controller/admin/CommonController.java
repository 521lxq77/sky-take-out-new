package com.sky.controller.admin;


import com.sky.result.Result;
import com.itheima.alioss.AliOSSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/admin/common")
@RestController
public class CommonController {

    @Autowired
    private AliOSSUtils aliOSSUtils;

    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws IOException {
        String path = aliOSSUtils.upload(file);
        return Result.success(path);

    }
}
