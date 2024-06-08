SELECT * FROM mysql_story.users order by id;

commit;
rollback;


delete from users where id in (7);

delete from users;

desc users;

commit;

-- allows to disable autom commit
set session autocommit = 0;
-- allows dml operation for all data without need to specify primary key
SET SQL_SAFE_UPDATES = 0;

