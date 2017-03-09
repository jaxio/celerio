/*
 * Copyright 2015 JAXIO http://www.jaxio.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jaxio.celerio.configuration.database.support;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

public class SqlUtil {
    /*
     * select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA != 'PUBLIC'
     */
    private static List<String> h2SchemaTables = newArrayList("HELP", "VIEWS", "CONSTRAINTS", "RIGHTS", "FUNCTION_COLUMNS", "SETTINGS", "TABLE_TYPES",
            "SCHEMATA", "INDEXES", "IN_DOUBT", "CROSS_REFERENCES", "USERS", "SESSIONS", "TABLE_PRIVILEGES", "CONSTANTS", "DOMAINS", "TABLES", "COLUMNS",
            "COLLATIONS", "ROLES", "SESSION_STATE", "SEQUENCES", "FUNCTION_ALIASES", "TYPE_INFO", "TRIGGERS", "LOCKS", "COLUMN_PRIVILEGES", "CATALOGS");

    /*
     * from http://www.h2database.com/html/grammar.html
     */
    private static List<String> h2Grammar = newArrayList("ADD", "ADMIN", "AGGREGATE", "ALIAS", "ALL", "ALLOW_LITERALS", "ALTER", "ANALYZE", "AUTOCOMMIT",
            "BACKUP", "CACHE_SIZE", "CALL", "CATALOGS", "CHECKPOINT", "CLUSTER", "COLLATION", "COLLATIONS", "COLUMN", "COLUMN_PRIVILEGES", "COLUMNS",
            "COMMENT", "COMMIT", "COMPRESS_LOB", "CONSTANT", "CONSTANTS", "CONSTRAINT", "CONSTRAINTS", "CREATE", "CROSS_REFERENCES", "DATABASE_EVENT_LISTENER",
            "DB_CLOSE_DELAY", "DEFAULT_LOCK_TIMEOUT", "DEFAULT_TABLE_TYPE", "DELETE", "DOMAIN", "DOMAINS", "DROP", "EXCLUSIVE", "EXPLAIN", "FUNCTION_ALIASES",
            "FUNCTION_COLUMNS", "GRANT", "HASH", "HELP", "IGNORECASE", "IN_DOUBT", "INDEX", "INDEXES", "INSERT", "LINKED", "LOCK_MODE", "LOCK_TIMEOUT",
            "LOCKS", "LOG", "MAX_LENGTH_INPLACE_LOB", "MAX_LOG_SIZE", "MAX_MEMORY_ROWS", "MAX_MEMORY_UNDO", "MAX_OPERATION_MEMORY", "MERGE", "MODE",
            "MULTI_THREADED", "OBJECTS", "OPTIMIZE_REUSE_RESULTS", "PASSWORD", "PREPARE", "QUERY_TIMEOUT", "REFERENTIAL_INTEGRITY", "RENAME", "REVOKE",
            "RIGHT", "RIGHTS", "ROLE", "ROLES", "ROLLBACK", "RUNSCRIPT", "SALT", "SAVEPOINT", "SCHEMA", "SCHEMA_SEARCH_PATH", "SCHEMATA", "SCRIPT", "SELECT",
            "SEQUENCE", "SEQUENCES", "SESSION_STATE", "SESSIONS", "SET", "SETTINGS", "SHOW", "SHUTDO", "SYNC", "TABLE", "TABLE_PRIVILEGES", "TABLE_TYPES",
            "TABLES", "THROTTLE", "TRACE_LEVEL", "TRACE_MAX_FILE_SIZE", "TRANSACTION", "TRIGGER", "TRIGGERS", "TRUNCATE", "TYPE_INFO", "UNDO_LOG", "UPDATE",
            "USER", "USERS", "VIEW", "VIEWS", "WRITE_DELAY");

    /*
     * http://www.h2database.com/html/functions.html
     */
    private static List<String> h2Functions = newArrayList("ABS", "ACOS", "ARRAY_GET", "ARRAY_LENGTH", "ASCII", "ASIN", "ATAN", "ATAN2", "AUTOCOMMIT", "AVG",
            "BIT_LENGTH", "BITAND", "BITOR", "BITXOR", "BOOL_AND", "BOOL_OR", "CANCEL_SESSION", "CASEWHEN", "CAST", "CEILING", "CHAR", "COALESCE", "COMPRESS",
            "CONCAT", "CONVERT", "COS", "COT", "COUNT", "CSVREAD", "CSVWRITE", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRVAL", "DATABASE",
            "DATABASE_PATH", "DATEADD", "DATEDIFF", "DAY_OF_MONTH", "DAY_OF_WEEK", "DAY_OF_YEAR", "DAYNAME", "DECRYPT", "DEGREES", "DIFFERENCE", "ENCRYPT",
            "EXP", "EXPAND", "EXTRACT", "FILE_READ", "FLOOR", "FORMATDATETIME", "GREATEST", "GROUP_CONCAT", "HASH", "HEXTORAW", "HOUR", "IDENTITY", "IFNULL",
            "INSTR", "LEAST", "LEFT", "LENGTH", "LINK_SCHEMA", "LOCATE", "LOCK_MODE", "LOCK_TIMEOUT", "LOG", "LOG10", "LOWER", "LPAD", "LTRIM", "MAX",
            "MEMORY_FREE", "MEMORY_USED", "MIN", "MINUTE", "MOD", "MONTH", "MONTHNAME", "NEXTVAL", "NULLIF", "OCTET_LENGTH", "PARSEDATETIME", "PI", "POSITION",
            "POWER", "QUARTER", "RADIANS", "RAND", "RANDOM_UUID", "RAWTOHEX", "READONLY", "REGEXP_REPLACE", "REPEAT", "REPLACE", "RIGHT", "ROUND",
            "ROUNDMAGIC", "ROWNUM", "RPAD", "RTRIM", "SCHEMA", "SCOPE_IDENTITY", "SECOND", "SECURE_RAND", "SELECTIVITY", "SESSION_ID", "SET", "SIGN", "SIN",
            "SOUNDEX", "SPACE", "SQRT", "STDDEV_POP", "STDDEV_SAMP", "STRINGDECODE", "STRINGENCODE", "STRINGTOUTF8", "SUBSTRING", "SUM", "TABLE", "TAN",
            "TRANSACTION_ID", "TRIM", "TRUNCATE", "UPPER", "USER", "UTF8TOSTRING", "VAR_POP", "VAR_SAMP", "WEEK", "XMLATTR", "XMLCDATA", "XMLCOMMENT",
            "XMLNODE", "XMLSTARTDOC", "XMLTEXT", "YEAR", "ZERO");

    /*
     * http://www.h2database.com/html/datatypes.html
     */
    private static List<String> h2Types = newArrayList("INT", "BOOLEAN", "TINYINT", "SMALLINT", "BIGINT", "IDENTITY", "DECIMAL", "DOUBLE", "REAL", "TIME",
            "DATE", "TIMESTAMP", "BINARY", "OTHER", "VARCHAR", "VARCHAR_IGNORECASE", "CHAR", "BLOB", "CLOB", "UUID", "ARRAY");

    private static Iterable<String> h2ReservedWords = concat(h2SchemaTables, h2Grammar, h2Functions, h2Types);

    /*
     * http://msdn.microsoft.com/en-us/library/aa238507(SQL.80).aspx
     */
    private static List<String> transactSqlReservedWords = newArrayList("ABSOLUTE", "FOUND", "PRESERVE", "ACTION", "FREE", "PRIOR", "ADMIN", "GENERAL",
            "PRIVILEGES", "AFTER", "GET", "READS", "AGGREGATE", "GLOBAL", "REAL", "ALIAS", "GO", "RECURSIVE", "ALLOCATE", "GROUPING", "REF", "ARE", "HOST",
            "REFERENCING", "ARRAY", "HOUR", "RELATIVE", "ASSERTION", "IGNORE", "RESULT", "AT", "IMMEDIATE", "RETURNS", "BEFORE", "INDICATOR", "ROLE", "BINARY",
            "INITIALIZE", "ROLLUP", "BIT", "INITIALLY", "ROUTINE", "BLOB", "INOUT", "ROW", "BOOLEAN", "INPUT", "ROWS", "BOTH", "INT", "SAVEPOINT", "BREADTH",
            "INTEGER", "SCROLL", "CALL", "INTERVAL", "SCOPE", "CASCADED", "ISOLATION", "SEARCH", "CAST", "ITERATE", "SECOND", "CATALOG", "LANGUAGE", "SECTION",
            "CHAR", "LARGE", "SEQUENCE", "CHARACTER", "LAST", "SESSION", "CLASS", "LATERAL", "SETS", "CLOB", "LEADING", "SIZE", "COLLATION", "LESS",
            "SMALLINT", "COMPLETION", "LEVEL", "SPACE", "CONNECT", "LIMIT", "SPECIFIC", "CONNECTION", "LOCAL", "SPECIFICTYPE", "CONSTRAINTS", "LOCALTIME",
            "SQL", "CONSTRUCTOR", "LOCALTIMESTAMP", "SQLEXCEPTION", "CORRESPONDING", "LOCATOR", "SQLSTATE", "CUBE", "MAP", "SQLWARNING", "CURRENT_PATH",
            "MATCH", "START", "CURRENT_ROLE", "MINUTE", "STATE", "CYCLE", "MODIFIES", "STATEMENT", "DATA", "MODIFY", "STATIC", "DATE", "MODULE", "STRUCTURE",
            "DAY", "MONTH", "TEMPORARY", "DEC", "NAMES", "TERMINATE", "DECIMAL", "NATURAL", "THAN", "DEFERRABLE", "NCHAR", "TIME", "DEFERRED", "NCLOB",
            "TIMESTAMP", "DEPTH", "NEW", "TIMEZONE_HOUR", "DEREF", "NEXT", "TIMEZONE_MINUTE", "DESCRIBE", "NO", "TRAILING", "DESCRIPTOR", "NONE",
            "TRANSLATION", "DESTROY", "NUMERIC", "TREAT", "DESTRUCTOR", "OBJECT", "TRUE", "DETERMINISTIC", "OLD", "UNDER", "DICTIONARY", "ONLY", "UNKNOWN",
            "DIAGNOSTICS", "OPERATION", "UNNEST", "DISCONNECT", "ORDINALITY", "USAGE", "DOMAIN", "OUT", "USING", "DYNAMIC", "OUTPUT", "VALUE", "EACH", "PAD",
            "VARCHAR", "END-EXEC", "PARAMETER", "VARIABLE", "EQUALS", "PARAMETERS", "WHENEVER", "EVERY", "PARTIAL", "WITHOUT", "EXCEPTION", "PATH", "WORK",
            "EXTERNAL", "POSTFIX", "WRITE", "FALSE", "PREFIX", "YEAR", "FIRST", "PREORDER", "ZONE", "FLOAT", "PREPARE");

    /*
     * http://download.oracle.com/docs/cd/A58617_01/server.804/a58225/ap_keywd.htm
     */
    private static List<String> oracleReservedWords = newArrayList("ACCESS", "ELSE", "MODIFY", "START", "ADD", "EXCLUSIVE", "NOAUDIT", "SELECT", "ALL",
            "EXISTS", "NOCOMPRESS", "SESSION", "ALTER", "FILE", "NOT", "SET", "AND", "FLOAT", "NOTFOUND", "SHARE", "ANY", "FOR", "NOWAIT", "SIZE", "ARRAYLEN",
            "FROM", "NULL", "SMALLINT", "AS", "GRANT", "NUMBER", "SQLBUF", "ASC", "GROUP", "OF", "SUCCESSFUL", "AUDIT", "HAVING", "OFFLINE", "SYNONYM",
            "BETWEEN", "IDENTIFIED", "ON", "SYSDATE", "BY", "IMMEDIATE", "ONLINE", "TABLE", "CHAR", "IN", "OPTION", "THEN", "CHECK", "INCREMENT", "OR", "TO",
            "CLUSTER", "INDEX", "ORDER", "TRIGGER", "COLUMN", "INITIAL", "PCTFREE", "UID", "COMMENT", "INSERT", "PRIOR", "UNION", "COMPRESS", "INTEGER",
            "PRIVILEGES", "UNIQUE", "CONNECT", "INTERSECT", "PUBLIC", "UPDATE", "CREATE", "INTO", "RAW", "USER", "CURRENT", "IS", "RENAME", "VALIDATE", "DATE",
            "LEVEL", "RESOURCE", "VALUES", "DECIMAL", "LIKE", "REVOKE", "VARCHAR", "DEFAULT", "LOCK", "ROW", "VARCHAR2", "DELETE", "LONG", "ROWID", "VIEW",
            "DESC", "MAXEXTENTS", "ROWLABEL", "WHENEVER", "DISTINCT", "MINUS", "ROWNUM", "WHERE", "DROP", "MODE", "ROWS", "WITH");
    private static List<String> oracleReservedKeywords = newArrayList("ADMIN", "CURSOR", "FOUND", "MOUNT", "AFTER", "CYCLE", "FUNCTION", "NEXT", "ALLOCATE",
            "DATABASE", "GO", "NEW", "ANALYZE", "DATAFILE", "GOTO", "NOARCHIVELOG", "ARCHIVE", "DBA", "GROUPS", "NOCACHE", "ARCHIVELOG", "DEC", "INCLUDING",
            "NOCYCLE", "AUTHORIZATION", "DECLARE", "INDICATOR", "NOMAXVALUE", "AVG", "DISABLE", "INITRANS", "NOMINVALUE", "BACKUP", "DISMOUNT", "INSTANCE",
            "NONE", "BEGIN", "DOUBLE", "INT", "NOORDER", "BECOME", "DUMP", "KEY", "NORESETLOGS", "BEFORE", "EACH", "LANGUAGE", "NORMAL", "BLOCK", "ENABLE",
            "LAYER", "NOSORT", "BODY", "END", "LINK", "NUMERIC", "CACHE", "ESCAPE", "LISTS", "OFF", "CANCEL", "EVENTS", "LOGFILE", "OLD", "CASCADE", "EXCEPT",
            "MANAGE", "ONLY", "CHANGE", "EXCEPTIONS", "MANUAL", "OPEN", "CHARACTER", "EXEC", "MAX", "OPTIMAL", "CHECKPOINT", "EXPLAIN", "MAXDATAFILES", "OWN",
            "CLOSE", "EXECUTE", "MAXINSTANCES", "PACKAGE", "COBOL", "EXTENT", "MAXLOGFILES", "PARALLEL", "COMMIT", "EXTERNALLY", "MAXLOGHISTORY",
            "PCTINCREASE", "COMPILE", "FETCH", "MAXLOGMEMBERS", "PCTUSED", "CONSTRAINT", "FLUSH", "MAXTRANS", "PLAN", "CONSTRAINTS", "FREELIST", "MAXVALUE",
            "PLI", "CONTENTS", "FREELISTS", "MIN", "PRECISION", "CONTINUE", "FORCE", "MINEXTENTS", "PRIMARY", "CONTROLFILE", "FOREIGN", "MINVALUE", "PRIVATE",
            "COUNT", "FORTRAN", "MODULE", "PROCEDURE", "PROFILE", "SAVEPOINT", "SQLSTATE", "TRACING", "QUOTA", "SCHEMA", "STATEMENT_ID", "TRANSACTION", "READ",
            "SCN", "STATISTICS", "TRIGGERS", "REAL", "SECTION", "STOP", "TRUNCATE", "RECOVER", "SEGMENT", "STORAGE", "UNDER", "REFERENCES", "SEQUENCE", "SUM",
            "UNLIMITED", "REFERENCING", "SHARED", "SWITCH", "UNTIL", "RESETLOGS", "SNAPSHOT", "SYSTEM", "USE", "RESTRICTED", "SOME", "TABLES", "USING",
            "REUSE", "SORT", "TABLESPACE", "WHEN", "ROLE", "SQL", "TEMPORARY", "WRITE", "ROLES", "SQLCODE", "THREAD", "WORK", "ROLLBACK", "SQLERROR", "TIME");
    private static List<String> oracleReservedPlSQL = newArrayList("ABORT", "BETWEEN", "CRASH", "DIGITS", "ACCEPT", "BINARY_INTEGER", "CREATE", "DISPOSE",
            "ACCESS", "BODY", "CURRENT", "DISTINCT", "ADD", "BOOLEAN", "CURRVAL", "DO", "ALL", "BY", "CURSOR", "DROP", "ALTER", "CASE", "DATABASE", "ELSE",
            "AND", "CHAR", "DATA_BASE", "ELSIF", "ANY", "CHAR_BASE", "DATE", "END", "ARRAY", "CHECK", "DBA", "ENTRY", "ARRAYLEN", "CLOSE", "DEBUGOFF",
            "EXCEPTION", "AS", "CLUSTER", "DEBUGON", "EXCEPTION_INIT", "ASC", "CLUSTERS", "DECLARE", "EXISTS", "ASSERT", "COLAUTH", "DECIMAL", "EXIT",
            "ASSIGN", "COLUMNS", "DEFAULT", "FALSE", "AT", "COMMIT", "DEFINITION", "FETCH", "AUTHORIZATION", "COMPRESS", "DELAY", "FLOAT", "AVG", "CONNECT",
            "DELETE", "FOR", "BASE_TABLE", "CONSTANT", "DELTA", "FORM", "BEGIN", "COUNT", "DESC", "FROM", "FUNCTION", "NEW", "RELEASE", "SUM", "GENERIC",
            "NEXTVAL", "REMR", "TABAUTH", "GOTO", "NOCOMPRESS", "RENAME", "TABLE", "GRANT", "NOT", "RESOURCE", "TABLES", "GROUP", "NULL", "RETURN", "TASK",
            "HAVING", "NUMBER", "REVERSE", "TERMINATE", "IDENTIFIED", "NUMBER_BASE", "REVOKE", "THEN", "IF", "OF", "ROLLBACK", "TO", "IN", "ON", "ROWID",
            "TRUE", "INDEX", "OPEN", "ROWLABEL", "TYPE", "INDEXES", "OPTION", "ROWNUM", "UNION", "INDICATOR", "OR", "ROWTYPE", "UNIQUE", "INSERT", "ORDER",
            "RUN", "UPDATE", "INTEGER", "OTHERS", "SAVEPOINT", "USE", "INTERSECT", "OUT", "SCHEMA", "VALUES", "INTO", "PACKAGE", "SELECT", "VARCHAR", "IS",
            "PARTITION", "SEPARATE", "VARCHAR2", "LEVEL", "PCTFREE", "SET", "VARIANCE", "LIKE", "POSITIVE", "SIZE", "VIEW", "LIMITED", "PRAGMA", "SMALLINT",
            "VIEWS", "LOOP", "PRIOR", "SPACE", "WHEN", "MAX", "PRIVATE", "SQL", "WHERE", "MIN", "PROCEDURE", "SQLCODE", "WHILE", "MINUS", "PUBLIC", "SQLERRM",
            "WITH", "MLSLABEL", "RAISE", "START", "WORK", "MOD", "RANGE", "STATEMENT", "XOR", "MODE", "REAL", "STDDEV", "", "NATURAL", "RECORD", "SUBTYPE");
    private static Iterable<String> oracleSqlReservedWords = concat(oracleReservedWords, oracleReservedKeywords, oracleReservedPlSQL);

    /*
     * http://dev.mysql.com/doc/refman/5.1/en/reserved-words.html
     */
    private static List<String> mysqlReservedWords = newArrayList("ACCESSIBLE", "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ASENSITIVE", "BEFORE",
            "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE", "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN",
            "CONDITION", "CONSTRAINT", "CONTINUE", "CONVERT", "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR",
            "DATABASE", "DATABASES", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE",
            "DESC", "DESCRIBE", "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE", "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "ENCLOSED", "ESCAPED",
            "EXISTS", "EXIT", "EXPLAIN", "FALSE", "FETCH", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "GRANT", "GROUP",
            "HAVING", "HIGH_PRIORITY", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IN", "INDEX", "INFILE", "INNER", "INOUT",
            "INSENSITIVE", "INSERT", "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERVAL", "INTO", "IS", "ITERATE", "JOIN", "KEY", "KEYS",
            "KILL", "LEADING", "LEAVE", "LEFT", "LIKE", "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB",
            "LONGTEXT", "LOOP", "LOW_PRIORITY", "MASTER_SSL_VERIFY_SERVER_CERT", "MATCH", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT",
            "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", "MODIFIES", "NATURAL", "NOT", "NO_WRITE_TO_BINLOG", "NULL", "NUMERIC", "ON", "OPTIMIZE", "OPTION",
            "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", "PRECISION", "PRIMARY", "PROCEDURE", "PURGE", "RANGE", "READ", "READS", "READ_WRITE",
            "REAL", "REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPEAT", "REPLACE", "REQUIRE", "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", "SCHEMA",
            "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", "SMALLINT", "SPATIAL", "SPECIFIC", "SQL", "SQLEXCEPTION",
            "SQLSTATE", "SQLWARNING", "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", "SSL", "STARTING", "STRAIGHT_JOIN", "TABLE", "TERMINATED",
            "THEN", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE", "USAGE",
            "USE", "USING", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", "WHEN", "WHERE", "WHILE",
            "WITH", "WRITE", "XOR", "YEAR_MONTH", "ZEROFILL");

    private static Iterable<String> sqlReservedWords = concat(h2ReservedWords, transactSqlReservedWords, oracleSqlReservedWords, mysqlReservedWords);

    private static Set<String> sqlReservedWordsSet = newHashSet(sqlReservedWords);

    private static boolean isSqlReserved(String s) {
        return sqlReservedWordsSet.contains(s.toUpperCase());
    }

    /*
     * If the given value collides with sql reserved words, add \"
     */
    public static String escapeSql(String s) {
        return isSqlReserved(s) ? ("\\\"" + s + "\\\"") : s;
    }
}
