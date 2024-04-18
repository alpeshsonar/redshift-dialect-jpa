package com.workflow.callback.uniqueconstraint;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UniqueConstraintAspect {

	@Autowired
	private UniqueConstraintChecker uniqueConstraintChecker;

	@Pointcut("execution(* com..repository.*.save*(..)) && args(entity)")
	public void saveOperation(Object entity) {
	}

	@Before("saveOperation(entities)")
	public void checkEntityBeforeSave(JoinPoint joinPoint, Object entities) throws Throwable {
		if (entities instanceof Iterable) {
			for (Object entity : (Iterable<?>) entities) {
				uniqueConstraintChecker.checkUniqueness(entity);
			}
		} else {
			uniqueConstraintChecker.checkUniqueness(entities);
		}

	}
}
