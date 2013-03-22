DROP TABLE IF EXISTS Hotel;
CREATE TABLE Hotel (
    hotelID CHAR(4) PRIMARY KEY,
    hotelName VARCHAR(30),
    city CHAR(9) CONSTRAINT city_constraint CHECK (
        city IN ('Guelph', 'Kitchener', 'Waterloo')
    )
);

DROP TABLE IF EXISTS Room;
CREATE TABLE Room (
    hotelID CHAR(4),
    roomNo CHAR(4),
    price NUMERIC(5, 2),
    type CHAR(6),
    PRIMARY KEY (hotelID, roomNo),
    FOREIGN KEY (hotelID)
        REFERENCES Hotel (hotelID)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    CONSTRAINT price_constraint CHECK (price BETWEEN 50.00 AND 250.00),
    CONSTRAINT type_contraint CHECK (
        type IN ('Single', 'Double', 'Queen', 'King')
    )
);

DROP TABLE IF EXISTS Guest;
CREATE TABLE Guest (
    guestID CHAR(4) PRIMARY KEY,
    guestName VARCHAR(30),
    guestAddress VARCHAR(50),
    guestAffiliation VARCHAR(30)
);

DROP TABLE IF EXISTS Booking;
CREATE TABLE Booking (
    hotelID CHAR(4),
    roomNo CHAR(4),
    guestID CHAR(4),
    startDate DATE,
    endDate DATE,
    PRIMARY KEY (hotelID, roomNo, guestID, startDate),
    FOREIGN KEY (hotelID, roomNo)
        REFERENCES Room (hotelID, roomno)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    FOREIGN KEY (guestID)
        REFERENCES Guest (guestID)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);
