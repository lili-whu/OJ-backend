package com.lili.model;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T>{
    private int total;

    private List<T> listResult;
}
