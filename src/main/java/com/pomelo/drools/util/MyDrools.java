package com.pomelo.drools.util;
import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

import com.pomelo.drools.model.RuleCfg;

/**
 * @author zxz
 *
 */
public class MyDrools {	
	/**
	 * 对GKS、GKR、GKFS这三个的概念模棱两可，大概一个应用构建一套就OK了。
	 */
	private static final KieServices GKS = KieServices.Factory.get();
	private static final KieRepository GKR = GKS.getRepository();
	private static final KieFileSystem GKFS = GKS.newKieFileSystem();
	/**
	 * 用于本地应用标记哪个规则文件已加载。
	 */
	private static List<String> gKfsFacade = new ArrayList<String>();
	/**
	 * 用于存放已加载规则的容器，每次加载规则需重新生成。
	 */
	private static KieContainer kc;
	/**
	 * 规则配置，一般存在数据库中，这里用静态语句块初始化。
	 */
	private static RuleCfg ruleCfg;
	
	static {
		StringBuffer sb = new StringBuffer("package com.pomelo.drools.drl;\n");
		sb.append("import com.pomelo.drools.model.Users\n");
		sb.append("rule \"userStatusChangeRule\"\n");
		sb.append("when\n");
		sb.append("users:Users(status == 0)\n");
		sb.append("then\n");
		sb.append("users.status = 1;\n");
		sb.append("System.out.println(\"done\");\n");
		sb.append("end;\n");

		ruleCfg = new RuleCfg();
		ruleCfg.setRuleId(2000001);
		ruleCfg.setClassify("user_status_change");
		ruleCfg.setRuleName("userStatusChangeRule");
		ruleCfg.setRule(sb.toString());
		ruleCfg.setStatus(0);
	}
	
	/**
	 * ruleId：规则编码，例如：2000001。
	 * params：规则判断所需要的参数。
	 */
	public static void invokeIns(final int ruleId, Object... params) {		
		/**
		 * loadCfg：根据规则编码查询规则对象，实际应用是查询数据库，这里简化不做说明。
		 */
		RuleCfg ruleCfg = loadCfg(ruleId);
		/**
		 * 生成规则文件存放路径，这里可以大致推测drool部分核心内容，是把规则写在drl文件，drools内部自己构建一个文件系统。
		 */
		String drlPath = genDrlPath(ruleCfg);
		
		if(ruleCfg.getStatus() == RuleCfg.STATUS_INIT)
			gKfsFacade.remove(drlPath);
		
		if(!gKfsFacade.contains(drlPath)) {
			synchronized(gKfsFacade) {
				if(!gKfsFacade.contains(drlPath)) {
					GKFS.write(drlPath, ruleCfg.getRule());
					KieBuilder kb = GKS.newKieBuilder(GKFS);
					/**
					 * 类似java工程，对文件系统进行编译。
					 */
					kb.buildAll();
					if(kb.getResults().hasMessages(Message.Level.ERROR)) {
						System.out.println(kb.getResults().toString());
						/**
						 * 若编译失败，则把当前规则从文件系统中删除掉，重新编译。
						 */
						GKFS.delete(ruleCfg.getClassify());
						kb = GKS.newKieBuilder(GKFS);
						kb.buildAll();
						kc = GKS.newKieContainer(GKR.getDefaultReleaseId());
						throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
					}
					kc = GKS.newKieContainer(GKR.getDefaultReleaseId());
					ruleCfg.setStatus(RuleCfg.STATUS_LOADED);
					gKfsFacade.add(drlPath);
				}
			}
		}
		fireTheRule(ruleCfg.getRuleName(), params);
	}
	
	private static RuleCfg loadCfg(final int ruleId) {
		return ruleCfg;
	}
	
	private static String genDrlPath(RuleCfg ruleCfg) {
		return "src/main/resources/drl/" + ruleCfg.getClassify() + "/" + ruleCfg.getRuleName() + ".drl";
	}
	
	private static void fireTheRule(String ruleName, Object... params) {
		KieSession ks = kc.newKieSession();
		try {
			for(int i = 0;i < params.length;i++) {
				ks.insert(params[i]);
			}
			ks.fireAllRules(ruleNameEqualsAgendaFilter(ruleName));
		} finally {
			ks.dispose();
		}
	}
	
	private static AgendaFilter ruleNameEqualsAgendaFilter(final String ruleName) {
		return new AgendaFilter() {
			public boolean accept(Match paramMatch) {
				return paramMatch.getRule().getName().equals(ruleName);
			}
		};
	}
}