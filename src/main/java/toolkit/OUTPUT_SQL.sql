CREATE table item_pmp.TB_EMAIL_LOG(DATA_ID VARCHAR(20) primary key,USERNAME VARCHAR(20),DATA_DATE VARCHAR(8),Emile VARCHAR(20),DATA_Status CHAR(1));
CREATE table item_pmp.TB_CALENDAR(Data_id VARCHAR(20) primary key,Data_date VARCHAR(8),Date_value VARCHAR(8),Need_To_Do VARCHAR(2));
CREATE table item_pmp.UC_Matter(Data_id VARCHAR(20) primary key,Data_date VARCHAR(8),Matter_name VARCHAR(200),Matter_desc  CLOB ,Matter_flag VARCHAR(2),Rel_users  CLOB ,Matter_creator VARCHAR(200));
CREATE table item_pmp.PJ_PROJECT(Data_id VARCHAR(20) primary key,Data_date VARCHAR(8),Project_Name VARCHAR(8),Project_Flag VARCHAR(2),IP_Adress VARCHAR(20),Port_Number VARCHAR(20),DB_Name VARCHAR(200),DB_User VARCHAR(200),DB_password VARCHAR(200),Running_Time VARCHAR(8),Running_state VARCHAR(2),operator VARCHAR(200));
CREATE table item_pmp.SC_SYSINFO(Sys_id VARCHAR(20) primary key,Sys_date VARCHAR(8),Sys_ip VARCHAR(32),Sys_status VARCHAR(2),Sys_port VARCHAR(8));
CREATE table item_pmp.SC_USER(Data_id VARCHAR(20) primary key,UserName VARCHAR(20),PassWord VARCHAR(20),Emile VARCHAR(20),User_Role CHAR(1),Online_status CHAR(1),Error_Num VARCHAR(2));
CREATE table item_pmp.PJ_BUG(Bug_ID VARCHAR(20) primary key,User_ID VARCHAR(20),Submite_time VARCHAR(20),Bug_Ttle VARCHAR(20),Bug_Status CHAR(1),Bug_Description VARCHAR(200),Priority CHAR(1));
CREATE table item_pmp.SC_DATA_DIC(Dic_id VARCHAR(20) primary key,dic_code VARCHAR(20),Dic_status VARCHAR(20),Dic_name VARCHAR(40),Dic_text VARCHAR(40));
CREATE table item_pmp.UC_File(File_id VARCHAR(20) primary key,File_date VARCHAR(8),File_type VARCHAR(20),File_name VARCHAR(200),File_path VARCHAR(400),File_text VARCHAR(400));
