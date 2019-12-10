create tablespace itheima
datafile 'c:\itheima.dbf'
size 100m
autoextend on
next 10m;

drop tablespace itheima;

create user itheima
identified by itheima
default tablespace itheima;
--授权
grant dba to itheima;

create table person(
       pid number(20),
       pname varchar2(10)
);

alter table person add gender number(1)

alter table person modify gender char(1)

alter table person rename column gender to sex

alter table person drop column sex

select * from person

insert into person(pid,pname) values(1,'小明');
commit;

update person set pname='小马' where pid=1;
commit;

alter user scott account unlock;
alter user scott identified by tiger;

delete from person;--删除全部记录
drop table person;
truncate table person;--删除表再创建表，数据量大的时候使用

--序列，默认1开始，依次递增
create sequence s_person;
select s_person.currval from dual;
select s_person.nextval from dual;

insert into person (pid,pname) values(s_person.nextval,'你大哥');

--单行
select upper('yes') from dual;
select lower('yEs') from dual;

select round(26.18,1) from dual;--四舍五入，后面的参数为保留的位数-1往前保留
select trunc(56.16,-2) from dual;--直接截取
select mod(10,3)from dual;

select sysdate-e.hiredate from emp e;--入职到现在几天
select months_between(sysdate,e.hiredate)from emp e;--距离几个月
select months_between(sysdate,e.hiredate)/12 from emp e;--距离几个月
select round((sysdate-e.hiredate)/7) from emp e;--入职到现在几星期
--转换
select to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') from dual;
select to_date('2019-03-26 22:24:34','yyyy-mm-dd hh24:mi:ss') from dual;

select sysdate+1 from dual;

--通用函数
select sal*12+nvl(e.comm,0) from emp e;

--条件表达式

select e.ename, 
       case e.ename
         when 'SMITH' then '布布'
           else '汶汶'
             end
from emp e;

--范围判断emp表中员工工资 高于3000高收入，大于1500小于3000中等收入，其余低等收入
select e.sal, 
       case 
         when e.sal>3000 then '高收入'
           when e.sal>1500 then '高收入'
           else '低收入'
             end
from emp e;
--orcal专用
select e.ename, 
       decode( e.ename,
         'SMITH' ,'布布',
           '汶汶')中文名
from emp e;

--多行函数 聚合函数
select count(1) from emp;
select sum(sal) from emp;
select max(sal) from emp;
select min(sal) from emp;
select round(avg(sal)) from emp;
--分组查询
select e.deptno,avg(e.sal)
from emp e
where e.sal>800
group by e.deptno;

select avg(e.sal)
from emp e
group by e.deptno;


select e.deptno,avg(e.sal)
from emp e
group by e.deptno
having avg(e.sal)>2000;
--where过滤分组前的数据，having是过滤分组后的数据，
--where在group by 之前，having在之后
select e.deptno,avg(e.sal)
from emp e
where e.sal>800
group by e.deptno
having avg(e.sal)>2000;
--多表查询
select * from emp e,dept d;
--内连接  也叫等值连接
select *from emp e,dept d
where e.deptno=d.deptno;
--外连接
select *
from emp e right join dept d
on e.deptno=d.deptno;
--orcl专用外连接
--省略
--查员工姓名，领导姓名   自连接,把一张表看成多张表
select e1.ename,e2.ename
from emp e1,emp e2
where e1.mgr=e2.empno;
--查员工姓名，员工部门，领导部门，领导姓名 
select e1.ename,d1.deptno,e2.ename,d2.deptno
from emp e1,emp e2,dept d1,dept d2
where e1.mgr=e2.empno
and e1.deptno=d1.deptno
and e2.deptno=d2.deptno;


--子查询，返回一个值，一个集合，一张表
--查询工资和scott一样的信息
select * from emp where sal in (
select sal from emp where ename='SCOTT')

--查询工资和10号部门任意员工一样
select * from emp where sal in(
select sal from emp where deptno=10)

--查询每个部门最低工资，和最低员工姓名，和该员工所在部门
select deptno,min(sal)msal
from emp
group by deptno;

select t.deptno,t.msal,e.ename,d.dname
from(select deptno,min(sal)msal
from emp
group by deptno) t,emp e,dept d
where t.deptno=e.deptno
and t.msal=e.sal
and e.deptno=d.deptno

--多行函数（聚合函数）
select count(1)from emp;
select sum(sal)from emp;
select min(sal)from emp;
select max(sal)from emp;
select avg(sal)from emp;
 
--分组查询
--查询寻每个部门的平均工资
select e.deptno,avg(sal)
from emp e
group by e.deptno
--平均工资高于两千的部门信息
select e.deptno,avg(sal)
from emp e
group by e.deptno
having avg(sal)>2000

--分页
select * from emp e  order by e.sal desc

select rownum,t.*from(
select rownum,e.* from emp e order by e.sal desc)t
--重要，不理解就背
select * from(
select rownum rn,e.* from(
select * from emp order by sal desc
)e where rownum<11
) where rn>5

--视图 dba权限
create table emp as select * from scott.emp;
select * from emp;

create view v_emp as select ename,job from emp;

select * from v_emp;
update v_emp set job='CLERK' where ename='ALLEN';
commit;
create view v_emp1 as select ename,job from emp with read only;

--索引
create index idx_ename on emp(ename);
select * from emp where ename='SCOTT';

--复合
create index idx_enamejob on emp(ename,job);
select * from emp where ename='SCOTT' and job='xx';

--pl/sql变成语言

declare
 i number(2):=10;
 s varchar2(10):='小米';
 ena emp.ename%type;
 emprow emp%rowtype;
begin
  dbms_output.put_line(i);
  dbms_output.put_line(s);
  select ename into ena from emp where empno=7788;
  dbms_output.put_line(ena);
  select * into emprow from emp where empno=7788;
  dbms_output.put_line(emprow.ename||'的工作为'||emprow.job);
 end;
 
 --if判断
 declare
 i number(3):=&i;
 
 begin
   if i<18 then
     dbms_output.put_line('未成年');
   elsif  i<40 then
     dbms_output.put_line('中年');
     else
       dbms_output.put_line('老年');
       end if;
 end;
 --循环
 declare
 i number(2):=1;
 begin
   while i<11 loop
     dbms_output.put_line(i);
     i:=i+1;
     
   end loop;
 end;
 
 declare
 i number(2):=1;
 begin
  loop
    exit when i>10;
    dbms_output.put_line(i);
     i:=i+1;
     end loop;
 end;
 
 declare
  i number(2):=1;
 begin
 for i in 1..10 loop
    dbms_output.put_line(i);
   end loop;  
   end;
 
 --游标：存放多个对象，多行记录
 --输出所有员工
 declare
 cursor cl is select * from emp;
 emprow emp%rowtype;
 begin
   open cl;
   loop
     fetch cl into emprow;
     exit when cl%notfound;
     dbms_output.put_line(emprow.ename);
     end loop;
   close cl;
   end;
   
   declare
   cursor c2(eno emp.deptno%type) 
   is select empno from emp where deptno =eno;
   en emp.empno%type;
   begin
     open c2(10);
     loop
       fetch c2 into en;
       exit when c2%notfound;
       update emp set sal=sal+100 where empno=en;
       commit;
       end loop;
     close c2;
   end;
   --10号部门加工资
   select * from emp where deptno=10;
   
   
 --存储过程
 --给制定员工涨100
 create or replace procedure p1(eno emp.empno%type)
 is
 begin
   update emp set sal=sal+100 where empno=eno;
   commit;
   end;
   --
   select * from emp where empno=7788;
   --
   declare
   
   begin
     p1(7788);
   end;
 
 
--存储函数

--计算指定员工的年薪
create or replace function f_yearsal(eno emp.empno%type)return number
is
s number(10);
begin
   select sal*12+nvl(comm,0) into s from emp where empno=eno;
return s;
end; 
--测试,有返回值要接受
declare
s number(10);
begin
  s:=f_yearsal(7788);
dbms_output.put_line(s);
end;

--out参数使用
--存储过程算年薪
create or replace procedure p_yearsal(eno emp.empno%type,yearsal out number)
is 
s number(10);
c emp.comm%type;
begin
  select sal*12,nvl(comm,0)into s,c from emp where empno=eno;
  yearsal:=s+c;
  end;
  --测试
  declare
  yearsal number(10);
  begin
    p_yearsal(7788,yearsal);
    dbms_output.put_line(yearsal);
  end;

create table dept as select * from scott.dept;

select e.ename,d.dname
from emp e,dept d
where e.deptno=d.deptno;

--创建函数
create or replace function fdna(dno dept.deptno%type)return dept.dname%type
is
dna dept.dname%type;
begin
  select dname into dna from dept where deptno=dno;
  return dna;
end;

select
e.ename ,fdna(e.deptno)
from 
emp e;

--触发器,增删改触发
--语句触发器。
--行级触发器for each row,为了使用：old 或者：new

--插入，输出一个新员工入职
create or replace trigger t1
after
insert
on person
declare

begin
  dbms_output.put_line('新员工入职');
  end;
  
  insert into person values(2,'布布');
  commit;
  
  select * from person;
  
  
  --行级，不能给员工降薪
  -- -20001~-20999之间
  create or replace trigger t2
  before
  update
  on emp
  for each row
    declare
    begin
      if :old.sal>:new.sal then
        raise_application_error(-20001,'不能给员工降薪');
      end if;
    end;
    
    update emp set sal=sal-1 where empno=7788;
    
    --触发器实现主键自增

--在用户插入操作之前拿到即将插入的数据，给主键列赋值

create or replace trigger auid
before
insert 
on person
for each row
  declare
  
  begin
    select s_person.nextval into :new.pid from dual;
  end;
  
  select * from person;
  
  insert into person (pname) values('a');
  commit;





