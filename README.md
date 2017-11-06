# ImprovedThreadLocal
This is improved ThreadLocal class that does not leak class loaders when you redeploy your web application. It is completely non-blocking so you will not have thread contention while reading, setting or initializing threadLocal values.
