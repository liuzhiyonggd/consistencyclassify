package sysu.consistency.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class Log {
    
	@Before("execution(** tools.Test.test(..))")
	public void log(){
		System.out.println("aop test.");
	}
}
