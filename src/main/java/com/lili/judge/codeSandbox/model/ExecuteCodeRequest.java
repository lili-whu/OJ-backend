package com.lili.judge.codeSandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ExecuteCodeRequest{
    private List<String> inputList;
    private String code;
    private Integer language;
}
