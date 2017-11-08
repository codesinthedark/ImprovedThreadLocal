# ImprovedThreadLocal
This is a ThreadLocal compatible class that does not leak class loaders when you redeploy your web application. It is completely non-blocking so you will not have thread contention while reading, setting or initializing threadLocal values.

* compatible with ThreadLocal
* thread-pool friendly - it will not leak memory and class-loaders when application is stopped or thread removed from the pool
* absolutely no thread contention (no synchronization or locks for accessing, initializing or removing thread local values)
