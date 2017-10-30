package com.pomelo.drools.model;

/**
 * @author zxz
 *
 */
public class RuleCfg {
	public static final int STATUS_INIT = 1;
	public static final int STATUS_LOADED = 2;
	private int ruleId;
	private String classify;
	private String ruleName;
	private String rule;
	private int status;
	
	public int getRuleId() {
		return ruleId;
	}
	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}
	public String getClassify() {
		return classify;
	}
	public void setClassify(String classify) {
		this.classify = classify;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
