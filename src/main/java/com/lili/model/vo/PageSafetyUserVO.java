package com.lili.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class PageSafetyUserVO{

    private long total;

    private List<SafetyUser> userList;

}
