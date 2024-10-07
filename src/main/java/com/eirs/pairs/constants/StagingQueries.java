package com.eirs.pairs.constants;

public interface StagingQueries {
    String PARAM_YYYYMMDD = "<yyyyMMdd>";
    String INSERT_FROM_EDR_TABLE = "insert into app.staging_temp_table_drop (edr_date_time,actual_imei,imsi,msisdn,operator_name,file_name,is_gsma_valid,is_custom_paid,tac,device_type) SELECT edr_date_time,actual_imei,imsi,msisdn,operator_name,file_name,is_gsma_valid,is_custom_paid,tac,device_type from app.edr_" + PARAM_YYYYMMDD + " where is_gsma_valid=0 and imsi like '456%'";
    String CREATE_INDEX_ON_TEMP_TABLE = "ALTER TABLE app.staging_temp_table_drop ADD INDEX((SUBSTRING(actual_imei,1,14)))";
    String DELETE_FROM_TEMP_TABLE_EXISTS_IN_TEMP_EXCEPTION = "delete from app.staging_temp_table_drop where SUBSTRING(actual_imei,1,14) in (select imei from app.temp_exception_list)";

    String INSERT_INTO_TEMP_EXCEPTION_LIST = "insert into app.temp_exception_list (actual_imei,imei,file_name,operator_name,edr_date_time,mode_type,request_type,imsi,msisdn) select actual_imei,SUBSTRING(actual_imei,1,14),file_name,operator_name,edr_date_time,'Single','EDR',imsi,msisdn from app.staging_temp_table_drop";
    String DROP_TABLE = "drop table app.staging_temp_table_drop";
    String TRUNCATE_TABLE = "truncate table app.staging_temp_table_drop";
    String CREATE_STAGING_DROP_TEMP_TABLE_MYSQL = "CREATE TABLE app.staging_temp_table_drop (" +
            "  id bigint NOT NULL AUTO_INCREMENT," +
            "  edr_date_time timestamp DEFAULT NULL," +
            "  imei_arrival_time timestamp DEFAULT NULL," +
            "  created_on timestamp DEFAULT CURRENT_TIMESTAMP," +
            "  actual_imei varchar(20) DEFAULT NULL," +
            "  imsi varchar(20) DEFAULT NULL," +
            "  msisdn varchar(15) DEFAULT NULL," +
            "  operator_name varchar(50) DEFAULT NULL," +
            "  file_name varchar(250) DEFAULT NULL," +
            "  is_gsma_valid int DEFAULT 0," +
            "  is_custom_paid int DEFAULT 0," +
            "  tac varchar(20) DEFAULT NULL," +
            "  device_type varchar(50) DEFAULT NULL," +
            "  source varchar(50) DEFAULT NULL," +
            "  protocol varchar(50) DEFAULT NULL," +
            "  UNIQUE (actual_imei,imsi)," +
            "  PRIMARY KEY (id)" +
            ") ENGINE=InnoDB;";

}
