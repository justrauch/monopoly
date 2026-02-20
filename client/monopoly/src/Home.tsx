import { useNavigate } from "react-router-dom";
import { useState, useEffect, useRef } from "react";
import "./App.css";

import freeParking from "./assets/free-parking.jpg";
import jail from "./assets/jail.jpg";
import train from "./assets/train.png";
import water from "./assets/waterworks.png";
import questionmarkRed from "./assets/questionmark-red.jpg"
import ring from "./assets/ring.jpg"
import arrow from "./assets/arrow.png"
import communityChest from "./assets/community-chest.png"
import elektrizitätswerk from "./assets/elektrizitätswerk.png"
import gotoJail from "./assets/go-to-jail.jpg"

import penguin from "./assets/penguin.png"
import ship from "./assets/ship.png"
import car from "./assets/car.png"
import fingerhat from "./assets/fingerhat.png"

import house from "./assets/house.png"
import hotel from "./assets/hotel.png"

// TODO's
// Anzeigen ich bin dran und move in die mitte
// Gefängnis
// Gameoverregeln


class Field {
    name: string;
    color: string;
    price: number;
    isspecial: boolean;

    constructor(name: string, color: string, price: number);
    constructor(name: string, color: string, price: number, isspecial: boolean);

    constructor(name: string, color: string, price: number, isspecial = false) {
        this.name = name;
        this.color = color;
        this.price = price;
        this.isspecial = isspecial;
    }
}

class Position {
    x: number;
    y: number;
    figure: string;

    constructor(x: number, y: number, figure: string) {
        this.x = x;
        this.y = y;
        this.figure = figure;
    }
}

const decodeposition = (postion: number, figure: string) => {
    if (postion <= 10){
        return new Position(10, (10 - postion), figure);
    }

    if (postion <= 20){
        return new Position((10 - (postion - 10)), 0, figure);
    }

    if (postion <= 30){
        return new Position(0, postion - 20, figure);
    }

    if (postion <= 40){
        return new Position(postion - 30, 10, figure);
    }
}

const encodePosition = (pos: Position): number => {
    var x = pos.y;
    var y = pos.x;

    if (y === 10 && x >= 0 && x <= 10) {
        return 10 - x;
    }

    if (x === 0 && y >= 0 && y < 10) {
        return 20 - y;
    }

    if (y === 0 && x > 0 && x <= 10) {
        return 20 + x;
    }

    if (x === 10 && y > 0 && y <= 10) {
        return 30 + y;
    }

    throw new Error("Ungültige Position");
};

const housecost = (pos: Position): number => {
    var x = pos.y;
    var y = pos.x;

    if (y === 10 && x >= 0 && x <= 10) {
        return 50;
    }

    if (x === 0 && y >= 0 && y < 10) {
        return 100;
    }

    if (y === 0 && x > 0 && x <= 10) {
        return 150;
    }

    if (x === 10 && y > 0 && y <= 10) {
        return 200;
    }

    throw new Error("Ungültige Position");
};

class Street {
    index: Position;
    hotel: number;
    house: number;
    price: number;
    owner: number;

    constructor(index: number, hotel: number, house: number, price: number, owner: number) {
        this.index = decodeposition(index, "") || new Position(0, 0, "");
        this.hotel = hotel;
        this.house = house;
        this.price = price;
        this.owner = owner;
    }
}

export default function App() {

    const navigate = useNavigate();
    const [showField, setshowField] = useState<Field>(new Field("", "", 0));
    const [showPosition, setPosition] = useState<Position>(new Position(10, 10, ""));
    const [loading, setloading] = useState(true);
    const [is_in_Match, setis_in_Match] = useState(false);
    const [is_in_Search, setis_in_Search] = useState(false);
    const [chose_figure, setchose_figure] = useState(0);
    const [mynumber, setmynumber] = useState(0);
    const [mymoney, setmymoney] = useState(0);
    const [com_money, setcom_money] = useState(0);
    const [prison, setprison] = useState(0);
    const [my_turn, setmy_turn] = useState(false);
    const [winner, setwinner] = useState(false);
    const [my_roll_dice, setmy_roll_dice] = useState(false);
    const [match_started, setmatch_started] = useState(false);
    const [my_end_turn, setmy_end_turn] = useState(false);
    const [my_position, setmy_position] = useState<Position>();
    const [move, setmove] = useState("");
    const [errormessage, seterrormessage] = useState("");
    const [error, seterror] = useState(false);
    const [allpieces, setallPieces] = useState<string[]>([penguin, ship, fingerhat, car]);
    const [pieces, setPieces] = useState<string[]>([penguin, ship, fingerhat, car]);
    const [boughtstreets, setboughtstreets] = useState<Street[]>();
    const [positions, setPositions] = useState<Position[]>([new Position(10, 10, ""), new Position(10, 10, ""), new Position(10, 10, ""), new Position(10, 10, "")]);

    // Logout-Request
    const logout = async () => {
        try {
            const response = await fetch(`http://localhost:8080/users/logout`, {
                method: "POST",
                credentials: "include"
            });

            if (!response.ok) { 
                await response.json(); 
                console.error(`Fehler beim Logout`);
                navigate("/");
                return; 
            }

            const message = await response.text();
            navigate("/");

        } catch (error) {
            console.error(`Fehler beim Logout:`, error);
        }
    };

    const searchmatch = async () => {
        try {
            seterror(false);
            const response = await fetch(`http://localhost:8080/matches/searchMatch`, {
                method: "POST",
                credentials: "include"
            });

            if (!response.ok) { 
                const bodyText = await response.text();
                seterrormessage(bodyText)
                seterror(true);
                return; 
            }

            const message = await response.text();

            if (message === "Match created" || message === "Match not full" || message === "Match full"){
                setis_in_Search(true);
            }

        } catch (error) {
            console.error(`Fehler beim Logout:`, error);
        }
    };

    const decodeposition = (postion: number, figure: string) => {
        if (postion <= 10){
            return new Position(10, (10 - postion), figure);
        }

        if (postion <= 20){
            return new Position((10 - (postion - 10)), 0, figure);
        }

        if (postion <= 30){
            return new Position(0, postion - 20, figure);
        }

        if (postion <= 40){
            return new Position(postion - 30, 10, figure);
        }
    }

    const gamestate = async (mynumberref: number) => {
        try {
            const response = await fetch(`http://localhost:8080/matches/getGamestate`, {
                method: "GET",
                credentials: "include"
            });
            
            if (!response.ok) { 
                await response.json(); 
                console.error(`Fehler beim Logout`);
                return; 
            }
            
            const contentType = response.headers.get("content-type");
            
            if (contentType && contentType.includes("application/json")) {

                var message = await response.json();

                setboughtstreets(
                    message.streets.map(
                        (m : any) => new Street(
                            m.index,
                            m.hotels,
                            m.houses,
                            m.price,
                            m.owner.turn_number
                        )
                    )
                );

                message = message.match;
                
                setmy_turn(mynumberref === Math.abs(message.isActive))
                setmy_roll_dice(mynumberref === message.isActive);
                setmy_end_turn((mynumberref * -1) === message.isActive);
                setcom_money(message.communityMoney);
                setmatch_started(message.isActive != 0);

                if (message.creater && message.creater.figure > 0){
                    if(mynumberref === 1) {
                        var pos = decodeposition(message.creater.position, "") || new Position(0,0,"");
                        setmy_position(new Position(pos.x, pos.y, "")); 
                        setis_in_Match(true); 
                        setmymoney(message.creater.money);
                        setprison(message.creater.prison_Sentence);
                        setwinner(message.winner && message.winner.turn_number == mynumberref);
                    }
                    setPieces(prev => {
                        const copy = [...prev];
                        copy[message.creater.figure - 1] = "";
                        return copy;
                    });
                    setPositions((prev: Position[]) => {
                        const copy = [...prev];
                        copy[0] = decodeposition(
                            message.creater.position,
                            allpieces[message.creater.figure - 1]
                        ) || copy[0];
                        return copy;
                    });
                }

                if (message.secondplayer && message.secondplayer.figure > 0){
                    if(mynumberref === 2) {
                        var pos = decodeposition(message.secondplayer.position, "") || new Position(0,0,"");
                        setmy_position(new Position(pos.x, pos.y, "")); 
                        setis_in_Match(true); 
                        setmymoney(message.secondplayer.money);
                        setprison(message.secondplayer.prison_Sentence);
                        setwinner(message.winner && message.winner.turn_number == mynumberref);
                    }
                    setPieces(prev => {
                        const copy = [...prev];
                        copy[message.secondplayer.figure - 1] = "";
                        return copy;
                    });
                    setPositions((prev: Position[]) => {
                        const copy = [...prev];
                        copy[1] = decodeposition(
                            message.secondplayer.position,
                            allpieces[message.secondplayer.figure - 1]
                        ) || copy[1];
                        return copy;
                    });
                }

                if (message.thirdplayer && message.thirdplayer.figure > 0){
                    if(mynumberref === 3) {
                        var pos = decodeposition(message.thirdplayer.position, "") || new Position(0,0,"");
                        setmy_position(new Position(pos.x, pos.y, "")); 
                        setis_in_Match(true); 
                        setmymoney(message.thirdplayer.money);
                        setprison(message.thirdplayer.prison_Sentence);
                        setwinner(message.winner && message.winner.turn_number == mynumberref);
                    }
                    
                    setPieces(prev => {
                        const copy = [...prev];
                        copy[message.thirdplayer.figure - 1] = "";
                        return copy;
                    });
                    setPositions((prev: Position[]) => {
                        const copy = [...prev];
                        copy[2] = decodeposition(
                            message.thirdplayer.position,
                            allpieces[message.thirdplayer.figure - 1]
                        ) || copy[2];
                        return copy;
                    });
                }

                if (message.fourthplayer && message.fourthplayer.figure > 0){
                    if(mynumberref === 4) {
                        var pos = decodeposition(message.fourthplayer.position, "") || new Position(0,0,"");
                        setmy_position(new Position(pos.x, pos.y, "")); 
                        setis_in_Match(true); 
                        setmymoney(message.fourthplayer.money);
                        setprison(message.fourthplayer.prison_Sentence);
                        setwinner(message.winner && message.winner.turn_number == mynumberref);
                    }
                    setPieces(prev => {
                        const copy = [...prev];
                        copy[message.fourthplayer.figure - 1] = "";
                        return copy;
                    });
                    setPositions((prev: Position[]) => {
                        const copy = [...prev];
                        copy[3] = decodeposition(
                            message.fourthplayer.position,
                            allpieces[message.fourthplayer.figure - 1]
                        ) || copy[3];
                        return copy;
                    });
                }

                setis_in_Search(mynumberref === 0 ? positions[mynumberref - 1].figure === "" : is_in_Search);

            }

            setloading(false);

        } catch (error) {
            console.error(`Fehler beim Logout:`, error);
        }
    };

    const setfigure = async () => {
        try {
            seterror(false);
            const response = await fetch(`http://localhost:8080/users/setfigure?figure=${chose_figure + 1}`, {
                method: "POST",
                credentials: "include"
            });

            if (!response.ok) { 
                const bodyText = await response.text();
                seterrormessage(bodyText)
                seterror(true);
                return; 
            }

            const message = await response.text();

            setis_in_Match(true);

        } catch (error) {
            console.error(`Fehler beim Logout:`, error);
        }
    };

    const myNumberRef = useRef(0);

    const getnumber = async () => {
        try {
            seterror(false);
            const response = await fetch(`http://localhost:8080/users/users/mynumber`, {
                method: "GET",
                credentials: "include"
            });

            if (!response.ok) { 
                const bodyText = await response.text();
                seterrormessage(bodyText)
                seterror(true);
                return; 
            }

            const message = await response.text();
            setmynumber(parseInt(message, 10));
            myNumberRef.current = parseInt(message, 10);


        } catch (error) {
            console.error(`Fehler beim Logout:`, error);
        }
    };

    const makeMove = async () => {
        try {
            seterror(false);
            const response = await fetch(`http://localhost:8080/matches/makeMove`, {
                method: "GET",
                credentials: "include"
            });

            if (!response.ok) { 
                const bodyText = await response.text();
                seterrormessage(bodyText)
                seterror(true);
                return; 
            }
            const message = await response.json();
            setmove("Würfel 1: " + message.dice1 + " \nWürfel 2: " + message.dice2 + (message.cardtext ? " \nKarte : " + message.cardtext : ""))

        } catch (error) {
            console.error(`Fehler beim Logout:`, error);
        }
    };

    const buystreet = async () => {
        try {
            seterror(false);
            const response = await fetch(`http://localhost:8080/streets/buystreet/${encodePosition(showPosition)}`, {
                method: "POST",
                credentials: "include"
            });

            if (!response.ok) { 
                const bodyText = await response.text();
                seterrormessage(bodyText)
                seterror(true);
                return; 
            }

            const message = await response.json();

        } catch (error) {
            console.error(`Fehler beim Logout:`, error);
        }
    };

    const sellstreet = async () => {
        try {
            seterror(false);
            const response = await fetch(`http://localhost:8080/streets/sellstreet/${encodePosition(showPosition)}`, {
                method: "POST",
                credentials: "include"
            });

            if (!response.ok) { 
                const bodyText = await response.text();
                seterrormessage(bodyText)
                seterror(true);
                return; 
            }

            const message = await response.json();

        } catch (error) {
            console.error(`Fehler beim Logout:`, error);
        }
    };

    const buybuilding = async (kind: string) => {
        try {
            seterror(false);
            const response = await fetch(`http://localhost:8080/streets/buybuilding/${encodePosition(showPosition)}/${kind}`, {
                method: "POST",
                credentials: "include"
            });

            if (!response.ok) { 
                const bodyText = await response.text();
                seterrormessage(bodyText)
                seterror(true);
                return; 
            }

            const message = await response.json();

        } catch (error) {
            console.error(`Fehler beim Logout:`, error);
        }
    };

    const sellbuilding = async (kind: string) => {
        try {
            seterror(false);
            const response = await fetch(`http://localhost:8080/streets/sellbuilding/${encodePosition(showPosition)}/${kind}`, {
                method: "POST",
                credentials: "include"
            });

            if (!response.ok) { 
                const bodyText = await response.text();
                seterrormessage(bodyText)
                seterror(true);
                return; 
            }

            const message = await response.json();

        } catch (error) {
            console.error(`Fehler beim Logout:`, error);
        }
    };

    const endturn = async () => {
        try {
            seterror(false);
            const response = await fetch(`http://localhost:8080/matches/endTurn`, {
                method: "GET",
                credentials: "include"
            });

            if (!response.ok) { 
                const bodyText = await response.text();
                seterrormessage(bodyText)
                seterror(true);
                return; 
            }

            const message = await response.json();

        } catch (error) {
            console.error(`Fehler beim Logout:`, error);
        }
    };

    const endMatch = async () => {
        try {
            seterror(false);
            const response = await fetch(`http://localhost:8080/matches/endMatch`, {
                method: "GET",
                credentials: "include"
            });

            if (!response.ok) { 
                const bodyText = await response.text();
                seterrormessage(bodyText)
                seterror(true);
                return; 
            }

            const message = await response.json();

        } catch (error) {
            console.error(`Fehler beim Logout:`, error);
        }
    };

    const surrender = async () => {
        try {
            seterror(false);
            const response = await fetch(`http://localhost:8080/matches/surrender`, {
                method: "GET",
                credentials: "include"
            });

            if (!response.ok) { 
                const bodyText = await response.text();
                seterrormessage(bodyText)
                seterror(true);
                return; 
            }

            const message = await response.json();

        } catch (error) {
            console.error(`Fehler beim Logout:`, error);
        }
    };

    const startmatch = async () => {
        try {
            seterror(false);
            const response = await fetch(`http://localhost:8080/matches/startMatch`, {
                method: "GET",
                credentials: "include"
            });

            if (!response.ok) { 
                const bodyText = await response.text();
                seterrormessage(bodyText)
                seterror(true);
                return; 
            }

            const message = await response.json();

        } catch (error) {
            console.error(`Fehler beim Logout:`, error);
        }
    };

    useEffect(() => {

        getnumber();
        gamestate(myNumberRef.current);

        const interval = setInterval(() => {
            gamestate(myNumberRef.current);
        }, 2500);

        return () => clearInterval(interval);
    }, []);

    // Beispiel-Daten
    const fields: Field[][] = [
        [
            new Field("Free Parking", freeParking, com_money, true),
            new Field("-Theater-\nstrasse", "red", 220),
            new Field("Ereignisfeld", questionmarkRed, 0, true),
            new Field("-Museum-\nstrasse", "red", 220),
            new Field("-Opern-\nstrasse", "red", 240),
            new Field("-Nord-\nbahnhof", train, 200),
            new Field("-Lessing-\nstrasse", "yellow", 260),
            new Field("-Schiller-\nstrasse", "yellow", 260),
            new Field("Wasserwerk", water, 150),
            new Field("-Goethe-\nstrasse", "yellow", 280),
            new Field("Ins\nGefängnis\ngehen", gotoJail, 0, true),
        ],
        [
            new Field("-Berliner-\nstrasse", "orange", 200),
            ...Array(9).fill(new Field("", "", 0, true)),
            new Field("-Rathaus-\nstrasse", "green", 300),
        ],
        [
            new Field("-Wiener-\nstrasse", "orange", 180),
            ...Array(9).fill(new Field("", "", 0, true)),
            new Field("-Haupt-\nstrasse", "green", 300),
        ],
        [
            new Field("Gemein-\nschaftsfeld", communityChest, 0, true),
            ...Array(9).fill(new Field("", "", 0, true)),
            new Field("Gemein-\nschaftsfeld", communityChest, 0, true),
        ],
        [
            new Field("-Muenchener-\nstrasse", "orange", 180),
            ...Array(9).fill(new Field("", "", 0, true)),
            new Field("-Bahnhof-\nstrasse", "green", 320),
        ],
        [
            new Field("-West-\nbahnhof", train, 200),
            ...Array(9).fill(new Field("", "", 0, true)),
            new Field("-Haupt-\nbahnhof", train, 200),
        ],
        [
            new Field("-Neue-\nstrasse", "pink", 160),
            ...Array(9).fill(new Field("", "", 0, true)),
            new Field("Ereignisfeld", questionmarkRed, 0, true),
        ],
        [
            new Field("-Hafen-\nstrasse", "pink", 140),
            ...Array(9).fill(new Field("", "", 0, true)),
            new Field("-Park-\nstrasse", "blue", 350),
        ],
        [
            new Field("Elektrizi-\ntätswerk", elektrizitätswerk, 150),
            ...Array(9).fill(new Field("", "", 0, true)),
            new Field("Zusatzsteuer", ring, -100, true),
        ],
        [
            new Field("-See-\nstrasse", "pink", 140),
            ...Array(9).fill(new Field("", "", 0, true)),
            new Field("-Schloss-\nstrasse", "blue", 400),
        ],
        [
            new Field("Nur\nzu\nbesuch", jail, 0, true),
            new Field("-Post-\nstrasse", "lightblue", 120),
            new Field("-Elisen-\nstrasse", "lightblue", 100),
            new Field("Ereignisfeld", questionmarkRed, 0, true),
            new Field("-Chaussee-\nstrasse", "lightblue", 100),
            new Field("-Sued-\nbahnhof", train, 200),
            new Field("Einkommen-\nsteuer", ring, -200, true),
            new Field("-Turm-\nstrasse", "brown", 60),
            new Field("Gehmein-\nschaftsfeld", communityChest, 0, true),
            new Field("-Bad-\nstrasse", "brown", 60),
            new Field("Los", arrow, 200, true),
        ],
    ];

    return (
        <div>
            {/* Logout */}
            <button onClick={logout}>Abmelden</button> 
            {match_started && <div> <button onClick={surrender}>Aufgeben</button>
            <button onClick={endMatch}>Spiel beenden</button> </div>}
            {!match_started && <button onClick={startmatch}>StartMatch</button>}
            {loading && <p>loading</p>}{!loading && <div>
            {!is_in_Match && <div>
                    {!is_in_Search && <button onClick={searchmatch}>Match suchen</button>}
                    {is_in_Search && <div className="form-column"><p>Wähle eine Figur</p>
                    <div className="form-row">
                        {pieces.map((p, index) => ( 
                            <div>
                                {p !== "" && <div onClick={() => setchose_figure(index)} style={{backgroundColor: chose_figure === index ?"white" : ""}}>
                                    <img 
                                        className="item-chose"
                                        src={p}
                                    />
                                </div>}
                            </div>
                            ))
                        }
                    </div>
                    <button onClick={setfigure}>Senden</button> </div>}
                </div>
            }
            {is_in_Match && <div className="table-container" > 
            <table style={{ borderCollapse: "collapse" }}>
            <tbody>
                {fields.map((row, rowIndex) => (
                <tr key={rowIndex}>
                    {row.map((field, indexField) => {

                    var boughtstreet = boughtstreets?.find((s) => s.index.y == indexField && s.index.x == rowIndex);
                    
                    var boughtshowstreet = boughtstreets?.find((s) => s.index.y == showPosition.y && s.index.x == showPosition.x);

                    if(rowIndex == 4 && indexField == 5){
                        field = showField;
                    }

                    if(rowIndex == 4 && indexField == 4){
                        field = new Field(my_turn && my_roll_dice ? "würfeln" : "---", "roll", 0);
                    }
                    if(rowIndex == 4 && indexField == 6){
                        field = new Field(my_turn && my_end_turn ? "Zug beenden" : "---", "endturn", 0);
                    }

                    if(rowIndex == 6 && indexField == 6){
                        field = new Field("Gefängnisstatus: " + (prison == -1 ? "Gefängnisfrei Karte" : (prison == 0 ? "Frei" : ("Runden bis Frei: " + prison))), "", 0);
                    }

                    if(rowIndex == 6 && indexField == 4){
                        field = new Field(move, "", 0);
                    }

                    if(rowIndex == 5 && indexField == 6){
                        field = new Field("Mein verbleibendes Geld :" + mymoney, "", 0);
                    }

                    if(rowIndex == 5 && indexField == 4){
                        field = new Field((winner ? "!!!Gewonnen!!!" : (my_turn ? "Ich bin dran!" : "Ich bin nicht dran!")), "", 0);
                    }

                    if(rowIndex == 5 && indexField == 5){
                        let enc_index = boughtshowstreet ? encodePosition(boughtshowstreet.index) : -1;
                        if (showField.isspecial){
                            field = new Field("---", "interact", 0);
                        }
                        else if (!boughtshowstreet){
                            field = new Field("kaufen", "buy", showField.price);
                        }
                        else if (boughtshowstreet.hotel <= 0 && boughtshowstreet.owner == mynumber && ((enc_index) != 5 && enc_index != 15 && enc_index != 25 && enc_index != 35 && enc_index != 12 && enc_index != 28)){
                            field = new Field("bauen", "build", housecost(new Position(showPosition.x, showPosition.y, "")));
                        }
                        else {
                            field = new Field("---", "trade", 0);
                        }
                    }

                    if(rowIndex == 6 && indexField == 5 && !showField.isspecial && boughtshowstreet){
                        let fieldpos = boughtshowstreet.index;
                        field = new Field("verkaufen", "sell", boughtshowstreet.hotel + boughtshowstreet.house > 0 ? housecost(new Position(showPosition.x, showPosition.y, "")) / 2 : fields[fieldpos.x][fieldpos.y].price / 2);
                    }

                    const isImage = field.color.startsWith("/src");
                    
                    return (
                        <td onClick={() => {
                            if(field.color && field.color !== "not clickable" && !(rowIndex == 4 && indexField == 5))
                            {
                                if(field.color === "roll" && my_turn && my_roll_dice){
                                    makeMove();
                                }
                                if(field.color === "endturn" && my_turn && my_end_turn){
                                    endturn();
                                }
                                if(field.color === "trade" && my_turn){

                                } 
                                if(field.color === "sell" && my_turn && boughtshowstreet){
                                    if (boughtshowstreet.hotel > 0){
                                        sellbuilding("hotel");
                                    }
                                    else if (boughtshowstreet.house > 0){
                                        sellbuilding("house");
                                    }
                                    else {
                                        sellstreet();
                                    }
                                }  
                                if(field.color === "interact" && my_turn){

                                }                               
                                if(field.color === "buy" && my_turn){
                                    buystreet();
                                }
                                if(field.color === "build" && my_turn && boughtshowstreet && boughtshowstreet.hotel <= 0){
                                    if (boughtshowstreet.house <= 3){
                                        buybuilding("house");
                                    }
                                    else if (boughtshowstreet.house == 4){
                                        buybuilding("hotel");
                                    }
                                }
                                else if (((!boughtstreet && my_position && my_position.x == rowIndex && my_position.y == indexField) || boughtstreet?.owner == mynumber)){
                                    setshowField(fields[rowIndex][indexField]);
                                    setPosition(new Position(rowIndex, indexField, ""));
                                }
                            }
                        }}
                        style={{
                            position: "relative",
                            width: "120px",
                            height: "100px",
                            border: "1px solid black",
                            textAlign: "center",
                            padding: "6px",
                            backgroundColor: 
                                my_position && my_position.y == indexField && my_position.x == rowIndex 
                                ? "green" :
                                boughtstreet && boughtstreet.owner == mynumber && boughtstreet.index.y == indexField && boughtstreet.index.x == rowIndex 
                                ? "orange" 
                                : ( field.color ? "#f5f5f5" 
                                : (rowIndex > 3 && rowIndex < 7 && indexField > 3 && indexField < 7
                                    ? "grey"
                                    : "black"))
                        }}
                        >
                        {error && rowIndex == 3 && indexField == 5 && <div style={{color: "red"}}>{errormessage}</div>}
                        
                        <div className="container">
                            {positions.map((p, i) => 
                                rowIndex === p.x && indexField === p.y ? (
                                    <img key={i} className="item" src={p.figure} />
                                ) : null
                            )}
                        </div>

                        {/* Bild */}
                        {isImage && (
                            <img 
                            src={field.color} 
                            style={{ width: "45px", marginBottom: "4px" }} 
                            />
                        )}

                        {/* Farbband */}
                        {!isImage && field.color && !field.color.includes("trade") && !field.color.includes("roll") && !field.color.includes("interact") && !field.color.includes("build") && !field.color.includes("sell") && !field.color.includes("endturn") && !field.color.includes("endturn") && !field.color.includes("buy") && (
                            <div 
                                style={{
                                    backgroundColor: field.color,
                                    height: "50px",
                                    marginBottom: "4px",
                                    display: "flex",
                                    alignItems: "center",
                                    justifyContent: "flex-start",
                                    gap: "5px",
                                    padding: "0 5px"
                                }}
                            >
                                <p style={{ color: "black", margin: 0, fontSize: "12px" }}>{
                                    boughtstreet && boughtstreet.index.y == indexField && boughtstreet.index.x == rowIndex ? boughtstreet.house : 0
                                }x</p>
                                <img src={house} style={{ width: "25px"}} />
                                <p style={{ color: "black", margin: 0, fontSize: "12px" }}>{
                                    boughtstreet && boughtstreet.index.y == indexField && boughtstreet.index.x == rowIndex ? boughtstreet.hotel : 0
                                }x</p>
                                <img src={hotel} style={{ width: "25px"}} />
                            </div>
                        )}

                        {/* Name */}
                        <div style={{ whiteSpace: "pre-line", fontSize: "12px", color: "black"}}>
                            {field.name}
                        </div>

                        {/* Preis */}
                        {field.price !== 0 && (
                            <div style={{ whiteSpace: "pre-line", fontSize: "12px", color: "black" }}>
                            {boughtstreet && boughtstreet.index.y == indexField && boughtstreet.index.x == rowIndex ? (boughtstreet.price === 0 ? "variable" : boughtstreet.price + "€") : (field.price + "€")}
                            </div>
                        )}
                        </td>
                    );
                    })}
                </tr>
                ))}
            </tbody>
            </table> </div>}
        </div> }</div>
    )
}