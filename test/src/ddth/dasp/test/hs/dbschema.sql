DROP TABLE IF EXISTS tbl_tesths;
CREATE TABLE tbl_tesths (
    id                      INT,
    c_string            	VARCHAR(32),
        INDEX(c_string),
    c_int                   INT,
        INDEX(c_int),
    c_real                  DOUBLE,
        INDEX(c_real),
    c_date                  DATE,
        INDEX(c_date),
    c_binary                BLOB,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
