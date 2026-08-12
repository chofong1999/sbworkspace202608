CREATE TABLE products (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        varchar(80) NOT NULL,    
    price       float NOT NULL,
    stock       integer,
    category    varchar(100)
    
);