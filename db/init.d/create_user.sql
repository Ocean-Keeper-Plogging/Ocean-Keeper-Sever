create user 'oceankeeper-admin'@'%' identified by 'oceankeeper';
create DATABASE oceankeeper default character set utf8mb4;

#GRANT ALL PRIVILEGES ON oceankeeper.* TO `oceankeeper-admin`@'%';
#GRANT ALL PRIVILEGES ON `oceankeeper-quartz`.* TO `oceankeeper-admin`@'%';
FLUSH privileges;