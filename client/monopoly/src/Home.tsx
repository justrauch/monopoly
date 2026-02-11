import { useNavigate } from "react-router-dom";
import { useState, useEffect, useRef } from "react";
import "./App.css";

export default function App() {

    const navigate = useNavigate();

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

    return (
        <div className="outer-div">
            {/* Logout */}
            <button onClick={logout}>Abmelden</button>
        </div>
    )
}