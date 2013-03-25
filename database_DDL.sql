-- Drop any existing tables if they exist
DROP TABLE IF EXISTS Booking;
DROP TABLE IF EXISTS Guest;
DROP TABLE IF EXISTS Room;
DROP TABLE IF EXISTS Hotel;
DROP TABLE IF EXISTS BillingLog;
DROP TRIGGER IF EXISTS check_booking_conflicts ON Booking; DROP FUNCTION IF EXISTS raise_booking_exception;

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

CREATE TABLE BillingLog (
    bookingID INTEGER,
    hotelName VARCHAR(30),
    city CHAR(9),
    roomNo CHAR(4),
    guestName VARCHAR(30),
    guestAddress VARCHAR(50),
    type CHAR(6),
    price NUMERIC(5, 2),
    startDate DATE,
    endDate DATE,
    numberOfDaysStayed INTEGER,
    total NUMERIC(10, 2)
);

-- Function to call on INSERT/UPDATE which raises an exception if a booking
-- conflict is found.
CREATE OR REPLACE FUNCTION raise_booking_exception() RETURNS TRIGGER AS $$
    BEGIN
        IF TG_OP = 'INSERT'
            AND (SELECT COUNT(*)
                FROM booking
                WHERE hotelid = NEW.hotelid
                    AND roomno = NEW.roomno
                    AND (
                        (NEW.startdate >= startdate AND NEW.startdate < enddate)
                        OR (NEW.enddate > startdate AND NEW.enddate <= enddate)
                        OR (NEW.startdate < startdate AND NEW.enddate > enddate)
                        OR (NEW.startdate = startdate AND NEW.enddate = enddate)
                    )
                ) = 0 THEN
            RETURN NEW;
        ELSIF TG_OP = 'UPDATE'
            AND (SELECT COUNT(*)
                FROM booking
                WHERE hotelid = NEW.hotelid
                    AND roomno = NEW.roomno
                    AND (
                        (NEW.startdate >= startdate AND NEW.startdate < enddate)
                        OR (NEW.enddate > startdate AND NEW.enddate <= enddate)
                        OR (NEW.startdate < startdate AND NEW.enddate > enddate)
                        OR (NEW.startdate = startdate AND NEW.enddate = enddate)
                    )
                    -- Filter out the updated row itself
                    AND NOT (
                        hotelid = OLD.hotelid
                        AND roomno = OLD.roomno
                        AND guestid = OLD.guestid
                        AND startdate = OLD.startdate
                        AND enddate = OLD.enddate
                    )
                ) = 0 THEN
            RETURN NEW;
        ELSE
            RAISE EXCEPTION USING
                errcode = 'BADBK',
                message = 'Conflict with existing booking';
        END IF;
    END;
$$ LANGUAGE plpgsql;

-- Insert/Update Trigger
CREATE TRIGGER check_booking_conflicts
    BEFORE INSERT OR UPDATE ON booking
    FOR EACH ROW
    EXECUTE PROCEDURE raise_booking_exception();
