-- Drop any existing tables if they exist
DROP TABLE IF EXISTS Booking;
DROP TABLE IF EXISTS Guest;
DROP TABLE IF EXISTS Room;
DROP TABLE IF EXISTS Hotel;

-- Create new tables
CREATE TABLE Hotel (
    hotelID SERIAL PRIMARY KEY,
    hotelName VARCHAR(30),
    city CHAR(9) CONSTRAINT city_constraint CHECK (
        city IN ('Guelph', 'Kitchener', 'Waterloo')
    )
);

CREATE TABLE Room (
    hotelID INTEGER,
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

CREATE TABLE Guest (
    guestID SERIAL PRIMARY KEY,
    guestName VARCHAR(30),
    guestAddress VARCHAR(50),
    guestAffiliation VARCHAR(30)
);

CREATE TABLE Booking (
    bookingID SERIAL PRIMARY KEY,
    hotelID INTEGER,
    roomNo CHAR(4),
    guestID INTEGER,
    startDate DATE,
    endDate DATE,
    FOREIGN KEY (hotelID, roomNo)
        REFERENCES Room (hotelID, roomno)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    FOREIGN KEY (guestID)
        REFERENCES Guest (guestID)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);
