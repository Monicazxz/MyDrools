package com.pomelo.drools.app;

import com.pomelo.drools.model.Users;
import com.pomelo.drools.util.MyDrools;

/**
 * @author zxz
 *
 */
public class Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Users users = new Users();
		users.name = "Monica";
		users.status = 0;
		
		System.out.println("before rule:" + users);
		long start = System.currentTimeMillis(); 
		MyDrools.invokeIns(2000001, users);
		long end = System.currentTimeMillis(); 
		System.out.println("after rule:" + users);
		
		long start2 = System.currentTimeMillis(); 
		MyDrools.invokeIns(2000001, users);
		long end2 = System.currentTimeMillis(); 
		
		System.out.println("time:" + (end - start));
		/**
		 * 第二次运行基本没有时间消耗
		 */
		System.out.println("time2:" + (end2 - start2));
	}

}
