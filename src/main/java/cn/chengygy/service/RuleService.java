package cn.chengygy.service;

import cn.chengygy.dto.Request;
import cn.chengygy.engine.RuleEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class RuleService {
    List<String> SList=new ArrayList<>(Arrays.asList(
        "有毛发",
        "有奶",
        "有羽毛",
        "会飞",
        "会生蛋",
        "吃肉",
        "有锋利牙齿",
        "有爪",
        "眼向前方",
        "有蹄",
        "反刍",
        "有黄褐色皮毛",
        "有暗斑点",
        "有黑色条纹",
        "有长脖子",
        "有长腿",
        "不会飞",
        "有黑白二色",
        "会游泳",
        "善飞"));

    public List<String> rule(Request request){
        RuleEngine ruleEngine=new RuleEngine("rules.json");
        List<String> list=new ArrayList<>();
        if(request.getCondition1()!=null) list.add(SList.get(0));
        if(request.getCondition2()!=null) list.add(SList.get(1));
        if(request.getCondition3()!=null) list.add(SList.get(2));
        if(request.getCondition4()!=null) list.add(SList.get(3));
        if(request.getCondition5()!=null) list.add(SList.get(4));
        if(request.getCondition6()!=null) list.add(SList.get(5));
        if(request.getCondition7()!=null) list.add(SList.get(6));
        if(request.getCondition8()!=null) list.add(SList.get(7));
        if(request.getCondition9()!=null) list.add(SList.get(8));
        if(request.getCondition11()!=null) list.add(SList.get(9));
        if(request.getCondition12()!=null) list.add(SList.get(10));
        if(request.getCondition14()!=null) list.add(SList.get(11));
        if(request.getCondition15()!=null) list.add(SList.get(12));
        if(request.getCondition16()!=null) list.add(SList.get(13));
        if(request.getCondition18()!=null) list.add(SList.get(14));
        if(request.getCondition19()!=null) list.add(SList.get(15));
        if(request.getCondition21()!=null) list.add(SList.get(16));
        if(request.getCondition22()!=null) list.add(SList.get(17));
        if(request.getCondition23()!=null) list.add(SList.get(18));
        if(request.getCondition24()!=null) list.add(SList.get(19));
        ruleEngine.setFacts(list);
        if(ruleEngine.deduce()){
            return ruleEngine.getResults();
        }else {
            return new ArrayList<>(Arrays.asList("未识别出结果"));
        }
    }
}
