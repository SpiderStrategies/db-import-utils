# db-import-utils
Utility to import from flat files in to a designated db table.

See https://github.com/SpiderStrategies/encryption-utils for db password encryption 

Usage:
`java -cp .;db-import-utils-0.1.0.jar com.spider.dbimport.Main <AESKEY> <JDBCURL> <ENCRYPTED_PASS> <DB_TABLE> <FILE>`

Example of usage:
`java -cp .;db-import-utils-0.1.0.jar com.spider.dbimport.Main "Ziggy###Stardust" "jdbc:sqlserver://127.0.0.1:1433;databaseName=MYDB;user=myuser;password=" -22_-10_109_-104_-12_-24_-10_96_ "ds_dsaid" "C:/myspreadsheet.xlsx"`
