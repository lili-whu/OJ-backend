package com.lili.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResult<T>{
    private long total;

    private List<T> listResult;
}
