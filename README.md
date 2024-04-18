Implemented limit and offset to use pageable on JPA. Default Postgres dialect does not support it. Also added unique constraint checker to avoid duplicate entries in the database using AOP.
