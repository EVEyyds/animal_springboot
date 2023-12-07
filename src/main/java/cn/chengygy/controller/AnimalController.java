package cn.chengygy.controller;

import cn.chengygy.dto.Request;
import cn.chengygy.response.Res;
import cn.chengygy.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
@RestController
@RequestMapping("/animal")
public class AnimalController {
    @Autowired
    private RuleService ruleService;
    @PostMapping( "/result")
    public Res<List<String>> rule(Request form){
        List<String> results=ruleService.rule(form);
        return new Res<>(200,results,"sucess");
    }
}