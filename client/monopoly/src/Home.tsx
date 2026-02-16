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

export default function App() {

    const navigate = useNavigate();
    const [showField, setshowField] = useState<Field>(new Field("", "", 0));
    const [loading, setloading] = useState(true);
    const [is_in_Match, setis_in_Match] = useState(false);
    const [is_in_Search, setis_in_Search] = useState(false);
    const [chose_figure, setchose_figure] = useState(0);
    const [mynumber, setmynumber] = useState(0);
    const [mymoney, setmymoney] = useState(0);
    const [my_turn, setmy_turn] = useState(false);
    const [allpieces, setallPieces] = useState<string[]>([penguin, ship, fingerhat, car]);
    const [pieces, setPieces] = useState<string[]>([penguin, ship, fingerhat, car]);
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
            const response = await fetch(`http://localhost:8080/matches/searchMatch`, {
                method: "POST",
                credentials: "include"
            });

            if (!response.ok) { 
                await response.json(); 
                console.error(`Fehler beim Logout`);
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

                const message = await response.json();
                
                setmy_turn(mynumberref === message.isActive)

                if (message.creater && message.creater.figure > 0){
                    console.log(mynumberref);
                    if(mynumberref === 1) {setis_in_Match(true); setmymoney(message.creater.money);}
                    setPieces(prev => {
                        const copy = [...prev];
                        copy[message.creater.figure - 1] = "";
                        return copy;
                    });
                    positions[0] = decodeposition(message.creater.position, allpieces[message.creater.figure - 1]) || positions[0];
                }

                if (message.secondplayer && message.secondplayer.figure > 0){
                    if(mynumberref === 2) {setis_in_Match(true); setmymoney(message.secondplayer.money);}
                    setPieces(prev => {
                        const copy = [...prev];
                        copy[message.secondplayer.figure - 1] = "";
                        return copy;
                    });
                    positions[1] = decodeposition(message.secondplayer.position, allpieces[message.secondplayer.figure - 1]) || positions[1];
                }

                if (message.thirdplayer && message.thirdplayer.figure > 0){
                    if(mynumberref === 3) {setis_in_Match(true); setmymoney(message.thirdplayer.money);}
                    setPieces(prev => {
                        const copy = [...prev];
                        copy[message.thirdplayer.figure - 1] = "";
                        return copy;
                    });
                    positions[2] = decodeposition(message.thirdplayer.position, allpieces[message.thirdplayer.figure - 1]) || positions[2];
                }

                if (message.fourthplayer && message.fourthplayer.figure > 0){
                    if(mynumberref === 4) {setis_in_Match(true); setmymoney(message.fourthplayer.money);}
                    setPieces(prev => {
                        const copy = [...prev];
                        copy[message.fourthplayer.figure - 1] = "";
                        return copy;
                    });
                    positions[3] = decodeposition(message.fourthplayer.position, allpieces[message.fourthplayer.figure - 1]) || positions[3];
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
            const response = await fetch(`http://localhost:8080/users/setfigure?figure=${chose_figure + 1}`, {
                method: "POST",
                credentials: "include"
            });

            if (!response.ok) { 
                await response.json(); 
                console.error(`Fehler beim Logout`);
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
            const response = await fetch(`http://localhost:8080/users/users/mynumber`, {
                method: "GET",
                credentials: "include"
            });

            if (!response.ok) { 
                await response.json(); 
                console.error(`Fehler beim Logout`);
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
            const response = await fetch(`http://localhost:8080/matches/makeMove`, {
                method: "GET",
                credentials: "include"
            });

            if (!response.ok) { 
                await response.json(); 
                console.error(`Fehler beim Logout`);

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
            new Field("Free Parking", freeParking, 0, true),
            new Field("-Theater-\nstrasse", "red", 220),
            new Field("Ereignisfeld", questionmarkRed, 0, true),
            new Field("-Museum-\nstrasse", "red", 220),
            new Field("-Opern-\nstrasse", "red", 240),
            new Field("-Nord-\nbahnhof", train, 200),
            new Field("-Lessing-\nstrasse", "yellow", 260),
            new Field("-Schiller-\nstrasse", "yellow", 260),
            new Field("Wasserwerk", water, 150, true),
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
            <button onClick={logout}>Abmelden</button> {loading && <p>loading</p>}{!loading && <div>
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
            <p>{my_turn ? "ich bin dran :->" : "ich bin nicht dran T.T"}</p>
            <p>Mein verbleibendes Geld: {mymoney}</p>
            <table style={{ borderCollapse: "collapse" }}>
            <tbody>
                {fields.map((row, rowIndex) => (
                <tr key={rowIndex}>
                    {row.map((field, indexField) => {
                    
                    if(rowIndex == 4 && indexField == 5){
                        field = showField;
                    }
                    if(rowIndex == 4 && indexField == 4){
                        field = new Field(my_turn ? "würfeln" : "---", "roll", 0);
                    }
                    if(rowIndex == 4 && indexField == 6){
                        field = new Field("---", "not clickable", 0);
                    }
                    if(rowIndex == 5 && indexField == 4 && (showField.name).includes("strasse")){
                        field = new Field("Hotel bauen", "clickable", showField.price * 0.75);
                    }
                    if(rowIndex == 6 && indexField == 4 && (showField.name).includes("strasse")){
                        field = new Field("Haus bauen", "clickable", showField.price * 0.25);
                    }
                    if(rowIndex == 5 && indexField == 5){
                        field = new Field(showField.isspecial ? "interagieren" : "bezahlen", "clickable", -showField.price * 0.5);
                    }
                    if(rowIndex == 6 && indexField == 5 && !showField.isspecial){
                        field = new Field("tauschen", "clickable", 0);
                    }
                    if(rowIndex == 5 && indexField == 6 && !showField.isspecial){
                        field = new Field("kaufen", "clickable", showField.price);
                    }
                    if(rowIndex == 6 && indexField == 6 && !showField.isspecial){
                        field = new Field("verkaufen", "clickable", 0);
                    }
                    const isImage = field.color.startsWith("/src");
                    

                    return (
                        <td onClick={() => {
                            if(field.color && field.color !== "not clickable" && !(rowIndex == 4 && indexField == 5))
                            {
                                if(field.color === "roll" && my_turn){
                                    makeMove();
                                }
                                if(field.color === "clickable"){

                                }
                                else{
                                    setshowField(fields[rowIndex][indexField]);
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
                            backgroundColor: field.color
                                ? "#f5f5f5"
                                : (rowIndex > 3 && rowIndex < 7 && indexField > 3 && indexField < 7
                                    ? "grey"
                                    : "black")
                        }}
                        >

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
                        {!isImage && field.color && !field.color.includes("clickable") && !field.color.includes("roll") && (
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
                                <p style={{ color: "black", margin: 0, fontSize: "12px" }}>4x</p>
                                <img src={house} style={{ width: "25px"}} />
                                <p style={{ color: "black", margin: 0, fontSize: "12px" }}>5x</p>
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
                            {field.price}€
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