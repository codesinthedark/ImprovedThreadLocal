package codes.inthedark.threadutils;

import java.lang.ThreadLocal;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Srdjan Mitrovic
 *
 *         This is improved ThreadLocal class that is not as safe as
 *         SafeThreadLocal but it is completely non-blocking. It is still an
 *         improvement over regular ThreadLocal because this will not leak
 *         memory when you redeploy your web application.
 */
public class ImprovedThreadLocal<T> extends ThreadLocal<T> {

	/**
	 * We add an entry to this map only once for every thread. We do not add an
	 * entry for every thread local value so this will not affect the
	 * performance of accessing and creating thread local values. This
	 * synchronized map is not used to access ThreadLocal values. It is used to
	 * preserve strong references to the objects because Thread objects will
	 * have only weak references to ThreadLocal objects. WeakHashMap allows
	 * values to be garbage collected once no one else is holding onto the
	 * Thread
	 */
	public static final Map<Thread, Map<ThreadLocal<?>, Object>> strongReferencesToThreadLocalValues = Collections
			.synchronizedMap(new WeakHashMap<Thread, Map<ThreadLocal<?>, Object>>());

	/**
	 * This is weak reference to the set of objects belonging to the current
	 * thread. Using this reference we can access the list of strong references
	 * in a non-synchronous way.
	 */
	public static final ThreadLocal<WeakReference<Map<ThreadLocal<?>, Object>>> threadLocalWeakReferenceToMapOfThreadLocals = new ThreadLocal<WeakReference<Map<ThreadLocal<?>, Object>>>() {
		protected WeakReference<Map<ThreadLocal<?>, Object>> initialValue() {
			Map<ThreadLocal<?>, Object> value = new WeakHashMap<ThreadLocal<?>, Object>();
			strongReferencesToThreadLocalValues.put(Thread.currentThread(),
					value);
			return new WeakReference<>(value);
		}
	};

	/**
	 * We create WeakReference to ThreadLocal value and use this weak reference
	 * in ThreadLocalMap, so that it can be cleaned up as soon as this
	 * ThreadLocal object goes out of scope. We could use
	 * threadLocalWeakReferenceToMapOfThreadLocals to access the value but
	 * getting the value is faster through this weak reference.
	 */
	ThreadLocal<WeakReference<T>> threadLocalWeakReference = new ThreadLocal<WeakReference<T>>() {
		protected WeakReference<T> initialValue() {
			T value = ImprovedThreadLocal.this.initialValue();

			// Adding strong reference to the map corresponding to
			// the current thread. We access this map through weak reference in
			// a non-synchronized way to keep good performance.
			threadLocalWeakReferenceToMapOfThreadLocals.get().get()
					.put(this, value);

			return new WeakReference<>(value);
		}
	};

	public T get() {
		return threadLocalWeakReference.get().get();
		// return (T)
		// threadLocalWeakReferenceToMapOfThreadLocals.get().get().get(this);
	}

	public void set(T value) {
		// Adding strong reference of this value to the map corresponding to the
		// current thread. We access this map through weak reference in a
		// non-synchronized way to keep good performance.
		threadLocalWeakReferenceToMapOfThreadLocals.get().get()
				.put(this, value);
		threadLocalWeakReference.set(new WeakReference<T>(value));

	}

	public void remove() {
		threadLocalWeakReferenceToMapOfThreadLocals.get().get().remove(this);
	}

}
