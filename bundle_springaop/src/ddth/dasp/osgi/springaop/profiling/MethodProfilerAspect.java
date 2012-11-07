package ddth.dasp.osgi.springaop.profiling;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import ddth.dasp.common.logging.ProfileLogger;

@Aspect
public class MethodProfilerAspect {
	@Around("@annotation(ddth.dasp.osgi.springaop.profiling.MethodProfile)")
	public Object recordDuration(ProceedingJoinPoint joinPoint)
			throws Throwable {
		// String entryName = annotation.value();
		// if (StringUtils.isBlank(entryName)) {
		// Signature sign = joinPoint.getSignature();
		// if (annotation.clazz() != Object.class) {
		// Class<?> clazz = annotation.clazz();
		// entryName = sign.toString() + " [" + clazz.getSimpleName()
		// + "]";
		// } else {
		// entryName = sign.toString();
		// }
		// }
		String entryName = joinPoint.getSignature().toShortString();
		ProfileLogger.push(entryName);
		try {
			return joinPoint.proceed();
		} finally {
			ProfileLogger.pop();
		}
	}
}
