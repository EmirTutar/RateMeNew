# RateMe - Eine Bewertungs-App für Produkte

RateMe ist eine Android-App, die es Nutzern ermöglicht, Produkte zu scannen, zu bewerten und Kommentare zu hinterlassen. Die App integriert Firebase für Authentifizierung, Datenhaltung und Echtzeit-Datenaktualisierungen. Nutzer können ihre Lieblingsprodukte zu einer Favoritenliste hinzufügen und Produktinformationen über eine integrierte Barcode-Scanning-Funktion abrufen.

## Funktionen

- **Produkt Scannen:** Nutzer können Produkt-Barcodes scannen und erhalten Produktinformationen und Bewertungen.
- **Produktbewertungen:** Nutzer können Produkte bewerten und Kommentare hinterlassen.
- **Favoritenliste:** Nutzer können Produkte zu ihrer Favoritenliste hinzufügen.
- **Einstellungen:** Einstellungsmöglichkeiten für Profil, Berechtigungen und App-Informationen.
- **Benutzerkonto:** Registrierung und Login-Funktionalität über Firebase Authentication.

## Technologien

- **Firebase Authentication:** Für die Registrierung und Anmeldung von Nutzern.
- **Firebase Firestore:** Zur Speicherung von Produktinformationen, Bewertungen und Benutzerfavoriten.
- **ZXing ("Zebra Crossing") für Android:** Für das Barcode-Scanning.

## Voraussetzungen

- Android Studio
- Mindest-SDK-Version: 33

## Installation

1. **Klonen Sie das Repository** oder laden Sie den Quellcode herunter.
2. **Importieren Sie das Projekt in Android Studio.**
3. **Bauen und starten Sie die App auf einem Emulator oder einem echten Gerät.**

## Struktur des Projekts

- `LoginActivity` - Enthält die Anmelde- und Registrierungsfunktionen.
- `MainActivity` - Die Hauptaktivität, die nach der Anmeldung angezeigt wird.
- `ScanActivity` - Verantwortlich für das Scannen von Barcodes und das Anzeigen von Produktinformationen.
- `HistoryActivity` - Erlaubt Nutzern, ihre Scan History zu sehen un zu Verwalten.
- `Favouritesctivity` - Erlaubt Nutzern, ihre gespeicherten Favouriten zu sehen und zur löschen.
- `SettingsActivity` - Erlaubt Nutzern, ihre Einstellungen zu verwalten.

## Kontakt

Für Fragen und Unterstützung kontaktieren Sie bitte [Emircan Tutar] über [emircan.tutar@hs-weingarten.de].

## Copyright

Copyright © 2024 Emircan Tutar, Leon Muss. Alle Rechte vorbehalten.

