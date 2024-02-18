package com.lili.controller;

import com.lili.model.Result;
import com.lili.utils.AliOssUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class Upload{

    @Resource
    AliOssUtil aliOssUtil;
    @PostMapping("")
    public Result<String> fileUpload(MultipartFile file) throws IOException{
        String upload = aliOssUtil.upload(file.getBytes(), UUID.randomUUID().toString());
        return Result.success(upload);
    }
}
