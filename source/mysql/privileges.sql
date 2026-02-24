use mysql;
select host, user from user;
create user 'app_rw'@'%' identified by '[YOUR_DATABASE_PASSWORD]';
grant all on *.* to app_rw@'%' with grant option;
-- 这一条命令一定要有：
flush privileges;
