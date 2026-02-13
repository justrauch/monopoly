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
    figures: string[];

    constructor(x: number, y: number, figures: string[]) {
        this.x = x;
        this.y = y;
        this.figures = figures;
    }
}



export default function App() {

    const navigate = useNavigate();
    const [showField, setshowField] = useState<Field>(new Field("", "", 0));
    const [is_in_Match, setis_in_Match] = useState(true);
    const [is_in_Search, setis_in_Search] = useState(true);
    const [is_in_Chose, setis_in_Chose] = useState(true);
    const [chose_figure, setchose_figure] = useState(0);

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

    const positions: Position[] = [
        new Position(10, 3, [penguin, ship]),
        new Position(1, 10, [fingerhat]),
        new Position(0, 10, [car]),
    ];

    const pieces: string[] = [penguin, ship, fingerhat, car];

    return (
        <div>
            {/* Logout */}
            <button onClick={logout}>Abmelden</button>
            {!is_in_Match && <div>
                    {!is_in_Search && <button>Match suchen</button>}
                    {is_in_Search && <div> {!is_in_Chose && <p>Suche im Gange ...</p>} {is_in_Chose && <div className="form-column"><p>Wähle eine Figur</p>
                    <div className="form-row">
                        {pieces.map((p, index) => (
                                <div onClick={() => setchose_figure(index)} style={{backgroundColor: chose_figure === index ?"white" : ""}}>
                                    <img 
                                        className="item-chose"
                                        src={p}
                                    />
                                </div>
                            ))
                        }
                    </div>
                    <button>Senden</button></div>} </div>}
                </div>
            }
            {is_in_Match && <div className="table-container" > 
            <table style={{ borderCollapse: "collapse" }}>
            <tbody>
                {fields.map((row, rowIndex) => (
                <tr key={rowIndex}>
                    {row.map((field, indexField) => {
                    console.log(field.color)
                    
                    if(rowIndex == 4 && indexField == 5){
                        field = showField;
                    }
                    if(rowIndex == 4 && indexField == 4){
                        field = new Field("---", "not clickable", 0);
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
                        
                        {positions.map((p) => (
                            <div>
                                {rowIndex == p.x && indexField == p.y && 
                                    <div className="container">
                                        {p.figures.map((f) => (
                                            <img
                                                className="item"
                                                src={f}
                                            />
                                        ))}
                                    </div>
                                }
                            </div>
                        ))}

                        {/* Bild */}
                        {isImage && (
                            <img 
                            src={field.color} 
                            style={{ width: "45px", marginBottom: "4px" }} 
                            />
                        )}

                        {/* Farbband */}
                        {!isImage && field.color && !field.color.includes("clickable") && (
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
        </div>
    )
}