# SafeThreadLocal
This is a ThreadLocal which does not leak class loaders in web containers or other applications that use thread pool. Primary use is for objects that you initialize once and use many times (eg. SimpleDateFormat).
