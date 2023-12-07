package cn.chengygy.entity;

import java.util.ArrayList;
import java.util.List;

public class Rule {
    List<String> conditions;
    List<String> result;
    public Rule(){
        conditions=new ArrayList<>();
        result=new ArrayList<>();
    }

    public boolean equals(Rule rule){
        // 两个rule的条件和结论相同，即为相同的rule
        if(!conditions.equals(rule.getConditions())) return false;
        return result.equals(rule.getResult());
    }

    public Rule(List<String> conditions, List<String> result) {
        this.conditions = conditions;
        this.result = result;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }
}
