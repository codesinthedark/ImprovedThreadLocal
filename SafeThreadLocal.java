package codes.inthedark.threadlocal;

import java.lang.ThreadLocal;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Srdjan Mitrovic
 *
 *         This is a ThreadLocal class that will not leak object's class loader
 *         and preserve non-synchronous read access to thread local values.
 */
public class SafeThreadLocal<T> extends ThreadLocal<T> {

	/**
	 * This synchronized map is not used to access ThreadLocal values. It is
	 * used to preserve strong references to the objects because Thread objects
	 * will have only weak references to ThreadLocal objects. WeakHashMap allows
	 * values to be garbage collected once no one else is holding onto the
	 * Thread
	 */
	public final Map<Thread, Object> strongReferencesToValues = Collections
			.synchronizedMap(new WeakHashMap<Thread, Object>());

	/**
	 * This is weak reference to the value belonging to the current thread.
	 * Using this reference we can access the thread local value in a
	 * non-synchronous way. WeakReference allows the value to be cleaned up as
	 * soon as this ThreadLocal object goes out of scope
	 */
	ThreadLocal<WeakReference<T>> threadLocalWeakReference = new ThreadLocal<WeakReference<T>>() {
		protected WeakReference<T> initialValue() {
			T value = SafeThreadLocal.this.initialValue();
			strongReferencesToValues.put(Thread.currentThread(), value);
			return new WeakReference<>(value);
		}
	};

	public T get() {
		return threadLocalWeakReference.get().get();
	}

	public void set(T value) {
		strongReferencesToValues.put(Thread.currentThread(), value);
		threadLocalWeakReference.set(new WeakReference<T>(value));
	}

	public void remove() {
		strongReferencesToValues.remove(Thread.currentThread());
	}
}
